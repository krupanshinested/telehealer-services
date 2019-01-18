package com.thealer.telehealer.views.home.vitals.measure;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.ThermoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by rsekar on 12/3/18.
 */

public class ThermoMeasureFragment extends BaseFragment implements VitalPairInterface,
        View.OnClickListener,ThermoMeasureInterface,VitalBatteryFetcher {

    private TextView value_tv,unit_tv,message_tv;
    private CustomButton close_bt,save_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay;
    private ImageView otherOptionView;

    private OnActionCompleteInterface onActionCompleteInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;
    private OnViewChangeInterface onViewChangeInterface;

    private VitalDevice vitalDevice;
    private int currentState;
    private String finalThermoValue = "";
    private VitalsApiViewModel vitalsApiViewModel;
    private Boolean isNeedToTrigger = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);
            isNeedToTrigger = getArguments().getBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY);
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getInt(ArgumentKeys.CURRENT_VITAL_STATE, MeasureState.notStarted);
        } else {
            currentState = MeasureState.notStarted;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thermo_measurement, container, false);

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
        vitalManagerInstance.getInstance().setThermoListener(this);
        toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));

        if (currentState == MeasureState.notStarted) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }

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
        value_tv = baseView.findViewById(R.id.value_tv);
        unit_tv = baseView.findViewById(R.id.title_tv);
        close_bt = baseView.findViewById(R.id.close_bt);
        save_bt = baseView.findViewById(R.id.save_bt);
        result_lay = baseView.findViewById(R.id.result_lay);
        message_tv = baseView.findViewById(R.id.message_tv);
        remeasure_bt = baseView.findViewById(R.id.remeasure_bt);

        save_bt.setOnClickListener(this);
        close_bt.setOnClickListener(this);
        remeasure_bt.setOnClickListener(this);

        if (isNeedToTrigger && currentState == MeasureState.notStarted) {
            startMeasure();
        } else {
            updateView(currentState);
        }

        String measurementType = VitalDeviceType.shared.getMeasurementType(vitalDevice.getType());
        unit_tv.setText(SupportedMeasurementType.getVitalUnit(measurementType));
    }

    private void connectDeviceIfNeedeed() {
        if (!vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(),vitalDevice.getDeviceId())) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }
    }

    private void fetchBattery() {
        vitalManagerInstance.getInstance().fetchBattery(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    private void updateView(int state){
        remeasure_bt.setVisibility(View.GONE);
        close_bt.setVisibility(View.GONE);

        switch (state) {
            case MeasureState.notStarted:
                result_lay.setVisibility(View.GONE);
                value_tv.setText("0");
                message_tv.setText("");
                save_bt.setText(getString(R.string.START));
                break;
            case MeasureState.started:
                message_tv.setText("");
                value_tv.setText("0");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setVisibility(View.GONE);
                break;
            case MeasureState.ended:
                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                remeasure_bt.setVisibility(View.VISIBLE);

                close_bt.setVisibility(View.VISIBLE);
                save_bt.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.SAVE_AND_CLOSE));
                close_bt.setText(getString(R.string.CLOSE));
                value_tv.setText(finalThermoValue);
                break;
            case MeasureState.startedToReceieveValues:

                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setVisibility(View.GONE);

                break;
            case MeasureState.failed:
                result_lay.setVisibility(View.GONE);
                save_bt.setText(getString(R.string.RESTART));
                break;
        }
    }

    private void startMeasure() {
        currentState = MeasureState.started;
        updateView(currentState);
        vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_bt:
                getActivity().finish();
                break;
            case R.id.save_bt:
                switch (currentState) {
                    case MeasureState.notStarted:
                    case MeasureState.failed:
                        startMeasure();
                        break;
                    case MeasureState.started:
                    case MeasureState.startedToReceieveValues:
                        vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
                        currentState = MeasureState.notStarted;
                        updateView(currentState);
                        break;
                    case MeasureState.ended:
                        vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.temperature,finalThermoValue, vitalsApiViewModel);

                        getActivity().finish();
                }
                break;
            case R.id.remeasure_bt:
                startMeasure();
                break;
            case R.id.other_option:
                onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO,true,getArguments());
                break;
        }
    }

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
            //nothing to do here
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        fetchBattery();
        startMeasure();
    }

    @Override
    public void didDisConnected(String type, String serailNumber) {
        if (type.equals(vitalDevice.getType()) && serailNumber.equals(vitalDevice.getDeviceId())) {
            if (currentState == MeasureState.failed) {
                showAlertDialog(getActivity(), getString(R.string.error), message_tv.getText().toString(), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
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

    @Override
    public void updateThermoMessage(String message) {
        message_tv.setText(message);
    }

    @Override
    public void updateThermoValue(Double value) {
        message_tv.setText("");

        finalThermoValue = value+"";
        currentState = MeasureState.ended;
        updateView(currentState);
    }

    @Override
    public void didThermoStartMeasure() {

    }

    @Override
    public void didThermoFinishMesureWithFailure(String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        message_tv.setText(error);

        currentState = MeasureState.failed;
        updateView(currentState);
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
