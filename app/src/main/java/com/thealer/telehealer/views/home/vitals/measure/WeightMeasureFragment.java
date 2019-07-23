package com.thealer.telehealer.views.home.vitals.measure;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.WeightMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by rsekar on 12/3/18.
 */

public class WeightMeasureFragment extends VitalMeasureBaseFragment implements
        View.OnClickListener, WeightMeasureInterface {

    private TextView value_tv, unit_tv, message_tv, title_tv;
    private CustomButton close_bt, save_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay,main_container;
    private String finalWeightValue = "";

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_measure, container, false);

        initView(view);
        return view;
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
            String title = getString(VitalDeviceType.shared.getTitle(vitalDevice.getType()));
            if (!TextUtils.isEmpty(vitalDevice.getDeviceId())) {
                title += " ("+vitalDevice.getDeviceId()+")";
            }
            title_tv.setText(title);
            main_container.setBackgroundColor(getResources().getColor(R.color.colorWhiteWithLessAlpha));
        }

        if (action != null) {
            action.doItNow();
            action = null;
        }
    }

    @Override
    protected void updateView(int state) {
        remeasure_bt.setVisibility(View.GONE);
        close_bt.setVisibility(View.GONE);

        switch (state) {
            case MeasureState.notStarted:
                result_lay.setVisibility(View.GONE);
                value_tv.setText("-");
                message_tv.setText("");
                save_bt.setText(getString(R.string.START));
                break;
            case MeasureState.started:
                message_tv.setText("");
                value_tv.setText("-");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.STOP));
                break;
            case MeasureState.ended:
                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                remeasure_bt.setVisibility(View.VISIBLE);

                save_bt.setVisibility(View.VISIBLE);
                if (isPresentedInsideCallActivity()) {
                    save_bt.setText(getString(R.string.done));
                } else {
                    save_bt.setText(getString(R.string.SAVE_AND_CLOSE));
                    close_bt.setText(getString(R.string.CLOSE));
                    close_bt.setVisibility(View.VISIBLE);

                }
                value_tv.setText(finalWeightValue);
                break;
            case MeasureState.startedToReceieveValues:

                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.STOP));
                break;
            case MeasureState.failed:
                result_lay.setVisibility(View.GONE);
                save_bt.setText(getString(R.string.RESTART));
                break;
        }

        if (!UserType.isUserPatient() && isPresentedInsideCallActivity()) {
            save_bt.setVisibility(View.GONE);
            close_bt.setVisibility(View.GONE);
            remeasure_bt.setVisibility(View.GONE);
        }

        if (callVitalPagerInterFace != null) {
            if (currentState == MeasureState.failed || currentState == MeasureState.ended || currentState == MeasureState.notStarted) {
                callVitalPagerInterFace.updateState(Constants.idle);
            } else {
                callVitalPagerInterFace.updateState(Constants.measuring);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_bt:
                if (isPresentedInsideCallActivity()) {
                    if (vitalManagerInstance != null) {
                        vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(), vitalDevice.getDeviceId());
                        setCurrentState(MeasureState.notStarted);
                    }
                    if (callVitalPagerInterFace != null) {
                        callVitalPagerInterFace.closeVitalController();
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
                            vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(), vitalDevice.getDeviceId());
                        setCurrentState(MeasureState.notStarted);
                        break;
                    case MeasureState.ended:
                        if (!isPresentedInsideCallActivity()) {
                            sendVitals(SupportedMeasurementType.weight, finalWeightValue,null,null);
                        } else {
                            onClick(close_bt);
                        }
                        break;
                }
                break;
            case R.id.remeasure_bt:
                startMeasure();
                break;
        }
    }

    //VitalPairInterface methods
    @Override
    public void startedToConnect(String type, String serailNumber) {
        super.startedToConnect(type,serailNumber);

        message_tv.setText(getString(R.string.connecting));
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        super.didConnected(type,serailNumber);
        Log.d("Weight measure", "state changed " + serailNumber);
    }

    //WeightMeasureInterface methods
    @Override
    public void updateWeightMessage(String deviceType, String message) {
        message_tv.setText(message);
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void updateWeightValue(String deviceType,Float value) {
        if (value <= 0) {
            return;
        }

        message_tv.setText("");

        value_tv.setText(value + "");

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }
    }

    @Override
    public void didStartWeightMeasure(String deviceType) {

    }

    @Override
    public void didFinishWeightMeasure(String deviceType, Float weight, String id) {
        message_tv.setText("");

        finalWeightValue = weight + "";
        setCurrentState(MeasureState.ended);

        if (isPresentedInsideCallActivity()) {
            if (UserType.isUserPatient() && vitalManagerInstance != null) {
                sendVitals(SupportedMeasurementType.weight, finalWeightValue,null,null);
            }
        }

    }

    @Override
    public void didFinishWeightMesureWithFailure(String deviceType, String error) {
        message_tv.setText(error);

        setCurrentState(MeasureState.failed);
    }

    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("WeightMeasureFragment", "received data");
        if (value_tv == null) {
            action = new Action() {
                @Override
                public void doItNow() {
                    processSignalMessagesForWeight(data);
                }
            };
        } else {
            processSignalMessagesForWeight(data);
        }
    }

    @Override
    public void assignVitalListener() {
        if (vitalManagerInstance != null) {
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setWeightListener(this);
            needToAssignIHealthListener = false;
        } else {
            needToAssignIHealthListener = true;
        }
    }

    private void processSignalMessagesForWeight(String data) {

        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        Log.d("WeightMeasureFragment","processSignalMessagesForWeight");

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);

            Log.d("WeightMeasureFragment","processSignalMessagesForWeight "+map.toString());

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    if (currentState != MeasureState.started) {
                        setCurrentState(MeasureState.started);
                    }
                    break;
                case VitalsConstant.VitalCallMapKeys.measuring:

                    float result = 0f;

                    try {
                        Double value = (Double) map.get(VitalsConstant.VitalCallMapKeys.data);
                        result = value.floatValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = (Float) map.get(VitalsConstant.VitalCallMapKeys.data);
                    }

                    updateWeightValue(vitalDevice.getType(), result);

                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didFinishWeightMesureWithFailure(vitalDevice.getType(), errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    float finalResult = 0f;

                    try {
                        Double value = (Double) map.get(VitalsConstant.VitalCallMapKeys.data);
                        finalResult = value.floatValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finalResult = (Float) map.get(VitalsConstant.VitalCallMapKeys.data);
                    }

                    didFinishWeightMeasure(vitalDevice.getType(), finalResult, "");

                    break;
            }

            if (currentState == MeasureState.notStarted) {
                openInDetail();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("WeightMeasureFragment","processSignalMessagesForWeight "+e.getLocalizedMessage());
        }
    }

    private void openInDetail() {
        didStartWeightMeasure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }
}
