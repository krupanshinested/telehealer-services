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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
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
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalEvents;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.ThermoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by rsekar on 12/3/18.
 */

public class ThermoMeasureFragment extends BaseFragment implements VitalPairInterface,
        View.OnClickListener,ThermoMeasureInterface,VitalBatteryFetcher,CallVitalEvents {

    private TextView value_tv,unit_tv,message_tv,title_tv;
    private CustomButton close_bt,save_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay,main_container;
    private ImageView otherOptionView;

    @Nullable
    private OnActionCompleteInterface onActionCompleteInterface;
    @Nullable
    private OnViewChangeInterface onViewChangeInterface;
    @Nullable
    private VitalManagerInstance vitalManagerInstance;
    @Nullable
    private ToolBarInterface toolBarInterface;

    @Nullable
    private Action action;

    private VitalDevice vitalDevice;
    private int currentState;
    private String finalThermoValue = "0";
    private VitalsApiViewModel vitalsApiViewModel;
    private Boolean isNeedToTrigger = false;

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);

        if (getArguments() != null) {
            vitalDevice = (VitalDevice) getArguments().getSerializable(ArgumentKeys.VITAL_DEVICE);
            isNeedToTrigger = getArguments().getBoolean(ArgumentKeys.NEED_TO_TRIGGER_VITAL_AUTOMATICALLY);
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getInt(ArgumentKeys.CURRENT_VITAL_STATE, MeasureState.notStarted);
        } else {
            currentState = MeasureState.notStarted;
        }

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(vitalsApiViewModel);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thermo_measurement, container, false);

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

        assignVitalListener();

        if (toolBarInterface != null)
            toolBarInterface.updateTitle(getString(VitalDeviceType.shared.getTitle(vitalDevice.getType())));

        if (currentState == MeasureState.notStarted && vitalManagerInstance != null) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }

        if (onViewChangeInterface != null) {
            onViewChangeInterface.hideOrShowClose(false);
            onViewChangeInterface.hideOrShowBackIv(true);
        }

        if (toolBarInterface != null)
            otherOptionView = toolBarInterface.getExtraOption();
        if (otherOptionView != null) {
            otherOptionView.setVisibility(View.VISIBLE);
            otherOptionView.setOnClickListener(this);

            otherOptionView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            otherOptionView.setImageResource(R.drawable.info);
        }

        connectDeviceIfNeedeed();

        if (vitalManagerInstance != null) {
            vitalManagerInstance.updateBatteryView(View.GONE, 0);
            vitalManagerInstance.getInstance().setBatteryFetcherListener(this);
        }
        fetchBattery();

        if (toolBarInterface != null)
            toolBarInterface.updateSubTitle("",View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof OnViewChangeInterface) {
            onViewChangeInterface = (OnViewChangeInterface) getActivity();
        }

        if (getActivity() instanceof OnActionCompleteInterface) {
            onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        }

        if (getActivity() instanceof ToolBarInterface) {
            toolBarInterface = (ToolBarInterface) getActivity();
        }

        if (getActivity() instanceof VitalManagerInstance) {
            vitalManagerInstance = (VitalManagerInstance) getActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().reset(this);
    }

    private void initView(View baseView) {
        value_tv = baseView.findViewById(R.id.value_tv);
        unit_tv = baseView.findViewById(R.id.unit_tv);
        close_bt = baseView.findViewById(R.id.close_bt);
        save_bt = baseView.findViewById(R.id.save_bt);
        result_lay = baseView.findViewById(R.id.result_lay);
        message_tv = baseView.findViewById(R.id.message_tv);
        remeasure_bt = baseView.findViewById(R.id.remeasure_bt);
        main_container = baseView.findViewById(R.id.main_container);
        title_tv = baseView.findViewById(R.id.title_tv);

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

        if (isPresentedInsideCallActivity()) {
            title_tv.setVisibility(View.VISIBLE);
            title_tv.setText(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
            main_container.setBackgroundColor(getResources().getColor(R.color.colorWhiteWithLessAlpha));
        }

        if (action != null) {
            action.doItNow();
            action = null;
        }
    }

    private void connectDeviceIfNeedeed() {
        if (vitalManagerInstance != null && !vitalManagerInstance.getInstance().isConnected(vitalDevice.getType(),vitalDevice.getDeviceId())) {
            vitalManagerInstance.getInstance().connectDevice(vitalDevice.getType(),vitalDevice.getDeviceId());
        }
    }

    private Boolean isPresentedInsideCallActivity() {
        return getActivity() instanceof CallActivity;
    }

    private void fetchBattery() {
        if (vitalManagerInstance != null)
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
                save_bt.setVisibility(View.VISIBLE);
                break;
            case MeasureState.started:
                message_tv.setText(getString(R.string.start_your_measurement_from_device));
                value_tv.setText("0");
                result_lay.setVisibility(View.GONE);
                save_bt.setVisibility(View.GONE);
                break;
            case MeasureState.ended:
                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                remeasure_bt.setVisibility(View.VISIBLE);

                if (isPresentedInsideCallActivity()) {
                    save_bt.setText(getString(R.string.done));
                } else {
                    save_bt.setText(getString(R.string.SAVE_AND_CLOSE));
                    close_bt.setText(getString(R.string.CLOSE));
                    close_bt.setVisibility(View.VISIBLE);

                }
                save_bt.setVisibility(View.VISIBLE);
                value_tv.setText(finalThermoValue);
                break;
            case MeasureState.startedToReceieveValues:
                value_tv.setText("0");
                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setVisibility(View.GONE);

                break;
            case MeasureState.failed:
                result_lay.setVisibility(View.GONE);
                save_bt.setText(getString(R.string.RESTART));
                save_bt.setVisibility(View.VISIBLE);
                break;
        }

        if (!UserType.isUserPatient()) {
            save_bt.setVisibility(View.GONE);
            close_bt.setVisibility(View.GONE);
            remeasure_bt.setVisibility(View.GONE);
        }

        if (callVitalPagerInterFace != null && getUserVisibleHint()) {
            if (currentState == MeasureState.failed || currentState == MeasureState.ended || currentState == MeasureState.notStarted) {
                callVitalPagerInterFace.updateState(Constants.idle);
            } else {
                callVitalPagerInterFace.updateState(Constants.measuring);
            }
        }
    }

    private void setCurrentState(int state) {
        Log.d("Thermo measure", "state changed "+state);
        currentState = state;
        updateView(currentState);
    }

    private void startMeasure() {
        setCurrentState(MeasureState.started);
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_bt:
                if (isPresentedInsideCallActivity()) {
                    if (vitalManagerInstance != null) {
                        vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
                        setCurrentState(MeasureState.notStarted);

                        if (callVitalPagerInterFace != null) {
                            callVitalPagerInterFace.closeVitalController();
                        }
                    }
                } else {
                    getActivity().finish();
                }
                break;
            case R.id.save_bt:
                switch (currentState) {
                    case MeasureState.notStarted:
                    case MeasureState.failed:
                        startMeasure();
                        break;
                    case MeasureState.started:
                    case MeasureState.startedToReceieveValues:
                        if (vitalManagerInstance != null)
                            vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
                        setCurrentState(MeasureState.notStarted);
                        break;
                    case MeasureState.ended:
                        if (!isPresentedInsideCallActivity()) {
                            if (vitalManagerInstance != null)
                                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.temperature, finalThermoValue, vitalsApiViewModel);
                        }
                        onClick(close_bt);
                }
                break;
            case R.id.remeasure_bt:
                startMeasure();
                break;
            case R.id.other_option:
                if (onActionCompleteInterface != null)
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
    public void startedToConnect(String type, String serailNumber) {
        message_tv.setText(getString(R.string.connecting));
        result_lay.setVisibility(View.GONE);
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

            if (!isPresentedInsideCallActivity()) {
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
            } else {
                setCurrentState(MeasureState.notStarted);
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
    public void updateThermoMessage(String deviceType,String message) {
        message_tv.setText(message);
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void updateThermoValue(String deviceType,Double value) {
        message_tv.setText("");

        finalThermoValue = value+"";
        setCurrentState(MeasureState.ended);

        if (isPresentedInsideCallActivity()) {
            if (UserType.isUserPatient() && vitalManagerInstance != null) {
                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.temperature, finalThermoValue, vitalsApiViewModel);
            }
        }
    }

    @Override
    public void didThermoStartMeasure(String deviceType) {

    }

    @Override
    public void didThermoFinishMesureWithFailure(String deviceType,String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        message_tv.setText(error);
        result_lay.setVisibility(View.GONE);

        setCurrentState(MeasureState.failed);
    }

    //VitalBatteryFetcher methods
    @Override
    public void updateBatteryDetails(BatteryResult batteryResult) {
        if (vitalManagerInstance != null) {
            if (batteryResult.getBattery() != -1) {
                vitalManagerInstance.updateBatteryView(View.VISIBLE, batteryResult.getBattery());
            } else {
                vitalManagerInstance.updateBatteryView(View.GONE, 0);
            }
        }
    }

    @Override
    public void notConnected(String deviceType, String deviceMac) {
        connectDeviceIfNeedeed();
    }


    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("ThermoMeasureFragmetn","received data");
        if (value_tv == null) {
            action = new Action() {
                @Override
                public void doItNow() {
                    processSignalMessagesForThermo(data);
                }
            };
        } else {
            processSignalMessagesForThermo(data);
        }
    }

    @Override
    public void assignVitalListener() {
        if (vitalManagerInstance != null) {
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setThermoListener(this);
        }
    }

    @Override
    public String getVitalDeviceType() {
        return vitalDevice.getType();
    }

    private void processSignalMessagesForThermo(String data) {
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:
                case VitalsConstant.VitalCallMapKeys.measuring:
                    Double value =  Double.parseDouble((String) map.get(VitalsConstant.VitalCallMapKeys.data));
                    updateThermoValue(vitalDevice.getType(),value);
                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didThermoFinishMesureWithFailure(vitalDevice.getType(),errorMessage);

                    break;
            }

            if (currentState == MeasureState.notStarted) {
                openInDetail();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInDetail() {
        didThermoStartMeasure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }

    @Override
    public void assignVitalDevice(VitalDevice vitalDevice) {
        this.vitalDevice = vitalDevice;
    }
}
