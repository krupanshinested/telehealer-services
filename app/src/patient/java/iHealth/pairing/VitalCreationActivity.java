package iHealth.pairing;

import android.arch.lifecycle.Lifecycle;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoQRCapture;

import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.common.VitalCommon.VitalsManager;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.QRCodeReaderActivity;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.vitals.BluetoothEnableActivity;
import com.thealer.telehealer.views.home.vitals.VitalCreateNewFragment;
import com.thealer.telehealer.views.home.vitals.measure.BPMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.BPTrackMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.GulcoMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.PulseMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.ThermoMeasureFragment;
import com.thealer.telehealer.views.home.vitals.measure.WeightMeasureFragment;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;

import iHealth.iHealthVitalManager;
import iHealth.pairing.CustomViews.BatteryView;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 11/28/18.
 */

public class VitalCreationActivity extends BaseActivity implements
        OnActionCompleteInterface,
        OnViewChangeInterface,
        SuccessViewInterface,
        View.OnClickListener,
        VitalManagerInstance,
        ToolBarInterface,
        OnCloseActionInterface,
        AttachObserverInterface {

    private FrameLayout mainContainer;
    private Toolbar toolbar;
    private ImageView closeButton,backIv,otherOption;
    private BatteryView batteryView;
    private TextView toolbarTitle,toolbarSubTitle;

    @Nullable
    private String measurementType;

    private VitalsManager vitalsManager;
    private String detailTitle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_creation);

        measurementType = getIntent().getStringExtra(ArgumentKeys.MEASUREMENT_TYPE);

        initView();

        if (savedInstanceState != null) {
            updateDetailTitle(savedInstanceState.getString(ArgumentKeys.CURRENT_TITLE));
            hideOrShowBackIv(savedInstanceState.getBoolean(ArgumentKeys.SHOW_BACK));
        } else {
            hideOrShowBackIv(false);
            updateDetailTitle(getString(R.string.new_vital));

            VitalDeviceListFragment vitalDeviceListFragment = new VitalDeviceListFragment();
            Bundle bundle = new Bundle();

            if (measurementType != null && !TextUtils.isEmpty(measurementType)) {
                bundle.putString(ArgumentKeys.MEASUREMENT_TYPE,measurementType);
            }

            vitalDeviceListFragment.setArguments(bundle);

            setFragment(vitalDeviceListFragment,false);
        }


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }

       // registerReceiver(new BTStateChangedBroadcastReceiver(),new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.bluetooth_disabled)));

        if (vitalsManager != null) {
            checkVitalPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("vitalCreationActivity","onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        int maxCount = isSplitModeNeeded() ? 1 : 0;
        int currentBackStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (currentBackStackCount > maxCount) {
            getSupportFragmentManager().popBackStack();

            if ((currentBackStackCount - 1) <= maxCount) {
                updateDetailTitle(getString(R.string.new_vital));
                hideOrShowNext(false);
                hideOrShowBackIv(false);
            }

        } else {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ArgumentKeys.CURRENT_TITLE, this.detailTitle);

        outState.putBoolean(ArgumentKeys.SHOW_BACK,backIv.getVisibility() == View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.close_iv:
                finish();
                break;
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success)
            finish();
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        switch (string) {
            case RequestID.TRIGGER_MANUAL_ENTRY:
                VitalCreateNewFragment vitalCreateNewFragment = new VitalCreateNewFragment();
                vitalCreateNewFragment.setArguments(bundle);
                setFragment(vitalCreateNewFragment,true);
                break;
            case RequestID.SET_UP_DEVICE:
                updateDetailTitle(getString(R.string.setup_new_devices));
                NewVitalDeviceSetUpFragment deviceSetUpFragment = new NewVitalDeviceSetUpFragment();
                deviceSetUpFragment.setArguments(bundle);
                setFragment(deviceSetUpFragment,true);
                break;
            case RequestID.OPEN_CONNECTED_DEVICE:
                EventRecorder.recordLastUpdate("last_vitals_measured_date");

                String type = bundle.getString(ArgumentKeys.DEVICE_TYPE);
                String measurementType = VitalDeviceType.shared.getMeasurementType(type);

                switch (measurementType) {
                    case SupportedMeasurementType.bp:
                        if (type.equals(VitalsConstant.TYPE_550BT)) {
                            BPTrackMeasureFragment bpMeasureFragment = new BPTrackMeasureFragment();
                            bpMeasureFragment.setArguments(bundle);
                            setFragment(bpMeasureFragment,true);
                        } else {
                            BPMeasureFragment bpMeasureFragment = new BPMeasureFragment();
                            bpMeasureFragment.setArguments(bundle);
                            setFragment(bpMeasureFragment,true);
                        }
                        break;
                    case SupportedMeasurementType.weight:
                        WeightMeasureFragment weightMeasureFragment = new WeightMeasureFragment();
                        weightMeasureFragment.setArguments(bundle);
                        setFragment(weightMeasureFragment,true);
                        break;
                    case SupportedMeasurementType.heartRate:
                        break;
                    case SupportedMeasurementType.temperature:
                        ThermoMeasureFragment thermoMeasureFragment = new ThermoMeasureFragment();
                        thermoMeasureFragment.setArguments(bundle);
                        setFragment(thermoMeasureFragment,true);
                        break;
                    case SupportedMeasurementType.gulcose:
                        GulcoMeasureFragment gulcoMeasureFragment = new GulcoMeasureFragment();
                        gulcoMeasureFragment.setArguments(bundle);
                        setFragment(gulcoMeasureFragment,true);
                        break;
                    case SupportedMeasurementType.pulseOximeter:
                        PulseMeasureFragment pulseMeasureFragment = new PulseMeasureFragment();
                        pulseMeasureFragment.setArguments(bundle);
                        setFragment(pulseMeasureFragment,true);
                        break;
                }
                break;
            case RequestID.OPEN_NOT_CONNECTED_DEVICE:
                VitalConnectingFragment notConnectedfragment = new VitalConnectingFragment();
                notConnectedfragment.setArguments(bundle);
                setFragment(notConnectedfragment,true);
                break;
            case RequestID.OPEN_VITAL_SETUP:
                VitalDemoVideoFragment demoVideoFragment = new VitalDemoVideoFragment();
                demoVideoFragment.setArguments(bundle);
                setFragment(demoVideoFragment,true);
                break;
            case RequestID.TRIGGER_DEVICE_CONNECTION:
                VitalDiscoveringFragment vitalConnectingFragment = new VitalDiscoveringFragment();
                vitalConnectingFragment.setArguments(bundle);
                setFragment(vitalConnectingFragment,true);
                break;
            case RequestID.OPEN_VITAL_INFO:
                VitalInfoFragment vitalInfoFragment = new VitalInfoFragment();
                vitalInfoFragment.setArguments(bundle);
                setFragment(vitalInfoFragment,true);
                break;
            case RequestID.OPEN_QR_READER:
                Intent intent = new Intent(VitalCreationActivity.this, QRCodeReaderActivity.class);
                startActivityForResult(intent,QRCodeReaderActivity.RequestID);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode) {
            case QRCodeReaderActivity.RequestID:
                if (data != null) {
                    String qr = data.getStringExtra(ArgumentKeys.RESULT);
                    if (qr != null && !TextUtils.isEmpty(qr)) {
                        ((GulcoQRCapture) getCurrentFragment()).didCapture(qr);
                    }
                }
                break;
            case BluetoothEnableActivity.REQUEST_ID:
                if (data != null) {
                    Boolean isEnable = data.getBooleanExtra(ArgumentKeys.RESULT, false);
                    if (!isEnable) {
                        finish();
                    }
                }
                break;
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }

                break;
            case Constants.LOCATION_SETTINGS_REQUEST:
               Log.d("VitalCreationActivity",resultCode+" for loca");
                if (resultCode == RESULT_CANCELED) {
                    onBackPressed();
                }
                break;
        }
    }

    /*
   *
   * Initializing the views
   * */
    private  void initView() {
        mainContainer = findViewById(R.id.main_container);
        toolbar = findViewById(R.id.appbar);
        closeButton = toolbar.findViewById(R.id.close_iv);
        backIv = toolbar.findViewById(R.id.back_iv);
        toolbar.findViewById(R.id.next_tv).setVisibility(View.GONE);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        otherOption = toolbar.findViewById(R.id.other_option);
        toolbarSubTitle = toolbar.findViewById(R.id.toolbar_subtitle);
        batteryView = toolbar.findViewById(R.id.batter_view);

        toolbar.setBackground(getDrawable(R.drawable.app_background_gradient));

        toolbarTitle.setTextColor(getColor(R.color.colorWhite));

        closeButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(this);

        backIv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        backIv.setOnClickListener(this);
    }

    private void updateDetailTitle(String detailTitle){
        this.detailTitle = detailTitle;
        toolbarTitle.setText(detailTitle);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager()
                .findFragmentById(R.id.main_container);
    }

    private void setFragment(Fragment fragment,Boolean needToAddBackTrace) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        findViewById(mainContainer.getId()).bringToFront();

        if (needToAddBackTrace) {
            fragmentManager.beginTransaction().addToBackStack(fragment.getClass().getSimpleName()).replace(mainContainer.getId(), fragment).commit();
        } else {
            fragmentManager.beginTransaction().replace(mainContainer.getId(), fragment).commit();
        }

    }

    private void openBluetoothEnable() {
        if (getLifecycle().getCurrentState() != Lifecycle.State.RESUMED || getLifecycle().getCurrentState() != Lifecycle.State.STARTED) {
            Intent intent = new Intent(VitalCreationActivity.this, BluetoothEnableActivity.class);
            startActivityForResult(intent, BluetoothEnableActivity.REQUEST_ID);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openBluetoothEnable();
        }
    };


    @Override
    public void enableNext(boolean enabled) {

    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {
        if (hideOrShow) {

        }
    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {
        if (hideOrShow) {
            closeButton.setVisibility(View.VISIBLE);
            otherOption.setVisibility(View.GONE);
        } else {
            closeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {
        if (hideOrShow) {
            backIv.setVisibility(View.VISIBLE);
        } else {
            backIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateNextTitle(String nextTitle) {

    }

    @Override
    public VitalsManager getInstance() {
        if (VitalsManager.instance == null) {
            if (BuildConfig.FLAVOR.equals(Constants.BUILD_DOCTOR)) {
                VitalsManager.instance = new VitalsManager(getApplication());
            } else{
                VitalsManager.instance = new iHealthVitalManager(getApplication());
            }
            checkVitalPermission();
        }

        vitalsManager = VitalsManager.instance;

        return VitalsManager.instance;
    }

    @Override
    public void updateBatteryView(int visibility, int battery) {
        batteryView.setVisibility(visibility);
        batteryView.setmLevel(battery);
    }

    @Override
    public void updateTitle(String title) {
        updateDetailTitle(title);
    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public ImageView getExtraOption() { return otherOption; }

    @Override
    public void updateSubTitle(String subTitle, int visibility) {
        toolbarSubTitle.setVisibility(visibility);
        toolbarSubTitle.setText(subTitle);
    }

    private void checkVitalPermission() {
       PermissionChecker permissionChecker = PermissionChecker.with(this);
       if (permissionChecker.isGranted(PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS)) {
           //nothing to do
       } else if (getLifecycle().getCurrentState() != Lifecycle.State.RESUMED || getLifecycle().getCurrentState() != Lifecycle.State.STARTED) {
           permissionChecker.checkPermission(PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS);
       }
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

}
