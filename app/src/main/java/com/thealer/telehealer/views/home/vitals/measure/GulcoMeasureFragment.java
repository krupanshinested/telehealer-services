package com.thealer.telehealer.views.home.vitals.measure;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoQRCapture;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 12/3/18.
 */

public class GulcoMeasureFragment extends BaseFragment implements VitalPairInterface,View.OnClickListener,
        GulcoQRCapture,GulcoMeasureInterface,VitalBatteryFetcher {

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;

    private ImageView otherOptionView;

    private VitalDevice vitalDevice;

    private ImageView device_iv;
    private LinearLayout state_lay,bottom_lay;
    private Button scan_bt,power_bt,strip_bt,blood_bt;
    private ConstraintLayout result_lay;
    private TextView gulco_value,gulco;
    private CustomButton save_bt,close_bt;

    private int currentState;

    private String lastError;
    private String finalGulcoValue = "";
    private VitalsApiViewModel vitalsApiViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getInt(ArgumentKeys.CURRENT_VITAL_STATE, MeasureState.notStarted);
        } else {
            currentState = MeasureState.notStarted;
        }

        if (lastError == null) {
            lastError = getString(R.string.device_disconnected);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gulcose_measure, container, false);

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);

        initView(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstnace) {
        super.onSaveInstanceState(saveInstnace);

        saveInstnace.putInt(ArgumentKeys.CURRENT_VITAL_STATE,currentState);
    }

    @Override
    public void onResume() {
        super.onResume();

        vitalManagerInstance.getInstance().setListener(this);
        vitalManagerInstance.getInstance().setGulcoListener(this);
        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));

        onViewChangeInterface.hideOrShowClose(false);
        onViewChangeInterface.hideOrShowBackIv(true);

        otherOptionView = toolBarInterface.getExtraOption();
        if (otherOptionView != null) {
            otherOptionView.setVisibility(View.VISIBLE);
            otherOptionView.setOnClickListener(this);

            otherOptionView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            otherOptionView.setImageResource(R.drawable.info);
        }

        connectDeviceIfNeedeed();

        vitalManagerInstance.updateBatteryView(View.GONE,0);
        vitalManagerInstance.getInstance().setBatteryFetcherListener(this);
        fetchBattery();

        toolBarInterface.updateSubTitle("",View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        toolBarInterface = (ToolBarInterface) getActivity();
        vitalManagerInstance = (VitalManagerInstance) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        vitalManagerInstance.getInstance().reset(this);
    }

    private void initView(View baseView) {
        device_iv = baseView.findViewById(R.id.device_iv);
        state_lay = baseView.findViewById(R.id.state_lay);
        scan_bt = baseView.findViewById(R.id.scan_bt);
        power_bt = baseView.findViewById(R.id.power_bt);
        strip_bt = baseView.findViewById(R.id.strip_bt);
        blood_bt = baseView.findViewById(R.id.blood_bt);
        result_lay = baseView.findViewById(R.id.result_lay);
        gulco_value = baseView.findViewById(R.id.gulco_value);
        gulco = baseView.findViewById(R.id.gulco);
        bottom_lay = baseView.findViewById(R.id.bottom_lay);
        save_bt = baseView.findViewById(R.id.save_bt);
        close_bt = baseView.findViewById(R.id.close_bt);

        device_iv.setImageResource(VitalDeviceType.shared.getImage(vitalDevice.getType()));

        save_bt.setOnClickListener(this);
        close_bt.setOnClickListener(this);
        scan_bt.setOnClickListener(this);

        updateView(currentState);

        String measurementType = VitalDeviceType.shared.getMeasurementType(vitalDevice.getType());
        gulco.setText(SupportedMeasurementType.getVitalUnit(measurementType));

    }

    private void connectDeviceIfNeedeed() {
        if (!vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(),vitalDevice.getDeviceId())) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }
    }

    private void fetchBattery() {
        vitalManagerInstance.getInstance().fetchBattery(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    private void startMeasure() {
        setCurrentState(MeasureState.started);
        vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    private void updateView(int currentState) {
        switch (currentState) {
            case MeasureState.notStarted:
                result_lay.setVisibility(View.GONE);
                state_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.GONE);
                markAsCurrent(scan_bt,getString(R.string.scan_strip_bottle_qr));
                markAsNotComplete(power_bt,getString(R.string.power_on));
                markAsNotComplete(strip_bt,getString(R.string.insert_strip));
                markAsNotComplete(blood_bt,getString(R.string.drop_blood));

                break;
            case MeasureState.capturedCodeString:
                result_lay.setVisibility(View.GONE);
                state_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.GONE);
                markAsFinish(scan_bt,getString(R.string.qr_scanned));
                markAsCurrent(power_bt,getString(R.string.power_on));
                markAsNotComplete(strip_bt,getString(R.string.insert_strip));
                markAsNotComplete(blood_bt,getString(R.string.drop_blood));
                break;
            case MeasureState.started:
                result_lay.setVisibility(View.GONE);
                state_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.GONE);
                markAsFinish(scan_bt,getString(R.string.qr_scanned));
                markAsFinish(power_bt,getString(R.string.powered_on));
                markAsCurrent(strip_bt,getString(R.string.insert_strip));
                markAsNotComplete(blood_bt,getString(R.string.drop_blood));
                break;
            case MeasureState.stripInserted:
                state_lay.setVisibility(View.VISIBLE);
                result_lay.setVisibility(View.GONE);
                bottom_lay.setVisibility(View.GONE);
                markAsFinish(scan_bt,getString(R.string.qr_scanned));
                markAsFinish(power_bt,getString(R.string.powered_on));
                markAsFinish(strip_bt,getString(R.string.strip_inserted));
                markAsCurrent(blood_bt,getString(R.string.drop_blood));
                break;
            case MeasureState.bloodDropped:
                result_lay.setVisibility(View.GONE);
                state_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.GONE);
                markAsFinish(scan_bt,getString(R.string.qr_scanned));
                markAsFinish(power_bt,getString(R.string.powered_on));
                markAsFinish(strip_bt,getString(R.string.strip_inserted));
                markAsFinish(blood_bt,getString(R.string.blood_dropped));
                break;
            case MeasureState.calculating:
                state_lay.setVisibility(View.GONE);
                result_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.GONE);
                break;
            case MeasureState.ended:
                state_lay.setVisibility(View.GONE);
                result_lay.setVisibility(View.VISIBLE);
                bottom_lay.setVisibility(View.VISIBLE);
                close_bt.setVisibility(View.VISIBLE);
                save_bt.setVisibility(View.VISIBLE);
                save_bt.setText(getResources().getString(R.string.save_and_close));
                break;
            case MeasureState.failed:
                state_lay.setVisibility(View.GONE);
                bottom_lay.setVisibility(View.VISIBLE);
                close_bt.setVisibility(View.GONE);
                save_bt.setVisibility(View.VISIBLE);
                save_bt.setText(getResources().getString(R.string.retry));
                break;
        }
    }

    private void markAsFinish(Button button,String title) {
        button.setText(title);
        updateButtonTint(button,R.color.color_green_light);
    }

    private void markAsCurrent(Button button,String title) {
        button.setText(title);
        updateButtonTint(button,R.color.red);
    }

    private void markAsNotComplete(Button button,String title) {
        button.setText(title);
        updateButtonTint(button,R.color.colorGrey_light);
    }

    private void updateButtonTint(Button button,int color) {
        button.setTextColor(getResources().getColor(color));
        button.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(color)));
    }

    private void setCurrentState(int state) {
        Log.e("Gulco measure", "state changed "+state);
        currentState = state;
        updateView(currentState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.other_option:
                onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO,true,getArguments());
                break;
            case R.id.save_bt:
                switch (currentState) {
                    case MeasureState.failed:
                        startMeasure();
                        break;
                    case MeasureState.ended:
                        vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.gulcose,finalGulcoValue,vitalsApiViewModel);
                        getActivity().finish();
                        break;
                }
                break;
            case R.id.close_bt:
                getActivity().finish();
                break;
            case R.id.scan_bt:
                if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_CAMERA)) {
                    if (currentState == MeasureState.notStarted) {
                        onActionCompleteInterface.onCompletionResult(RequestID.OPEN_QR_READER, true, null);
                    }
                }
                break;

        }
    }

    //GulcoQRCapture methods
    @Override
    public void didCapture(String result) {
        setCurrentState(MeasureState.capturedCodeString);
        vitalManagerInstance.getInstance().updateStripBottleId(vitalDevice.getType(),vitalDevice.getDeviceId(),result);
        startMeasure();
    }

    //GulcoMeasureInterface methods
    @Override
    public void updateGulcoMessage(String deviceType,String message) {

    }

    @Override
    public void updateGulcoValue(String deviceType,int value) {
        gulco_value.setText(value + "");
        finalGulcoValue = value+"";
        setCurrentState(MeasureState.ended);
    }

    @Override
    public void didGulcoStartMeasure(String deviceType) {
        setCurrentState(MeasureState.started);
    }

    @Override
    public void didFinishGulcoMesureWithFailure(String deviceType,String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        setCurrentState(MeasureState.failed);
        lastError = error;
        showAlertDialog(getActivity(), getString(R.string.error), error, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        },null);
    }

    @Override
    public void didStripInserted(String deviceType) {
        setCurrentState(MeasureState.stripInserted);
    }

    @Override
    public void didStripEjected(String deviceType) {

    }

    @Override
    public void didBloodDropped(String deviceType) {
        setCurrentState(MeasureState.bloodDropped);
    }

    //VitalPairInterface methods
    @Override
    public void didScanFinish() {
        //nothing to do
    }

    @Override
    public void didScanFailed(String error) {
        //nothing to do
    }

    @Override
    public void didDiscoverDevice(String type, String serailNumber) {
        //nothing to do
    }

    @Override
    public void startedToConnect(String type, String serailNumber) {

    }

    @Override
    public void didConnected(String type, String serailNumber) {
        fetchBattery();
        setCurrentState(MeasureState.started);
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        if (type.equals(vitalDevice.getType()) && serailNumber.equals(vitalDevice.getDeviceId())) {
            if (currentState == MeasureState.failed) {
                showAlertDialog(getActivity(), getString(R.string.error), lastError, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }, null);


            } else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        }
    }

    @Override
    public void didFailConnectDevice(String type, String serailNumber, String errorMessage) {
        showAlertDialog(getActivity(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, null);
    }

    //VitalBatteryFetcher methods
    @Override
    public void updateBatteryDetails(BatteryResult batteryResult) {
        if (batteryResult.getBattery() != -1) {
            vitalManagerInstance.updateBatteryView(View.VISIBLE,batteryResult.getBattery());
        } else {
            vitalManagerInstance.updateBatteryView(View.GONE,0);
        }
    }

    @Override
    public void notConnected(String deviceType, String deviceMac) {
        connectDeviceIfNeedeed();
    }
}
