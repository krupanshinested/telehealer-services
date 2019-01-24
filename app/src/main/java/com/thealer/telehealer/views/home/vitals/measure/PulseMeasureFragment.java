package com.thealer.telehealer.views.home.vitals.measure;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Message;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rsekar on 12/3/18.
 */

public class PulseMeasureFragment extends BaseFragment implements VitalPairInterface,
        View.OnClickListener,PulseMeasureInterface,VitalBatteryFetcher,CallVitalEvents {

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

    private ImageView otherOptionView;
    private LineChart graph;
    private TextView pulse_value,pulse,spo2_value,spo2,message_tv,title_tv;
    private CustomButton save_bt,close_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay,main_container;

    private int currentState;
    private Boolean isNeedToTrigger = false;

    private String finalPulseValue = "",finalHeartRate = "";
    private VitalsApiViewModel vitalsApiViewModel;

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
        View view = inflater.inflate(R.layout.fragment_pulse_measurement, container, false);

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
        graph = baseView.findViewById(R.id.graph);
        pulse = baseView.findViewById(R.id.pulse);
        pulse_value = baseView.findViewById(R.id.pulse_value);
        spo2_value = baseView.findViewById(R.id.spo2_value);
        spo2 = baseView.findViewById(R.id.spo2);
        message_tv = baseView.findViewById(R.id.message_tv);
        result_lay = baseView.findViewById(R.id.result_lay);
        main_container = baseView.findViewById(R.id.main_container);
        title_tv = baseView.findViewById(R.id.title_tv);

        save_bt = baseView.findViewById(R.id.save_bt);
        close_bt = baseView.findViewById(R.id.close_bt);
        remeasure_bt = baseView.findViewById(R.id.remeasure_bt);

        close_bt.setOnClickListener(this);
        save_bt.setOnClickListener(this);
        remeasure_bt.setOnClickListener(this);

        graph.getXAxis().setEnabled(false);
        graph.getAxisLeft().setEnabled(false);
        graph.getAxisRight().setEnabled(false);

        Easing.EasingFunction easingFunction = new Easing.EasingFunction() {
            @Override
            public float getInterpolation(float input) {
                return 100;
            }
        };
        graph.animateX(1000,easingFunction);

        if (isNeedToTrigger && currentState == MeasureState.notStarted) {
            startMeasure();
        } else {
            updateView(currentState);
        }

        String measurementType = VitalDeviceType.shared.getMeasurementType(vitalDevice.getType());
        spo2.setText(SupportedMeasurementType.getVitalUnit(measurementType));
        pulse.setText(SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.heartRate));

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

    private void startMeasure() {
        setCurrentState(MeasureState.started);
        if (vitalManagerInstance != null)
            vitalManagerInstance.getInstance().startMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
    }

    private void updateView(int currentState) {
        remeasure_bt.setVisibility(View.GONE);
        close_bt.setVisibility(View.GONE);

        switch (currentState) {
            case MeasureState.notStarted:
                graph.setVisibility(View.GONE);
                result_lay.setVisibility(View.GONE);
                spo2_value.setText("0");
                pulse_value.setText("0");
                message_tv.setText("");
                save_bt.setText(getString(R.string.START));
                break;
            case MeasureState.started:
                graph.setVisibility(View.VISIBLE);
                message_tv.setText("");
                spo2_value.setText("0");
                pulse_value.setText("0");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.STOP));
                break;
            case MeasureState.ended:
                graph.setVisibility(View.GONE);
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
                spo2_value.setText(finalPulseValue + "%");
                pulse_value.setText(finalHeartRate);
                break;
            case MeasureState.startedToReceieveValues:
                graph.setVisibility(View.VISIBLE);
                message_tv.setText("");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.STOP));
                break;
            case MeasureState.failed:
                result_lay.setVisibility(View.GONE);
                save_bt.setText(getString(R.string.RESTART));
                break;
        }

        if (!UserType.isUserPatient()) {
            save_bt.setVisibility(View.GONE);
            close_bt.setVisibility(View.GONE);
            remeasure_bt.setVisibility(View.GONE);
        }
    }

    private void setCurrentState(int state) {
        Log.e("pulse measure", "state changed "+state);
        currentState = state;
        updateView(currentState);
    }

    private void addData(int bpm) {

        if (graph.getData() != null &&
                graph.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet) graph.getData().getDataSetByIndex(0);

            ArrayList<Entry> values = new ArrayList<>();
            values.add(new Entry(set1.getXMax()+1,bpm));

            set1.setValues(values);
            set1.notifyDataSetChanged();
            graph.getData().notifyDataChanged();
            graph.notifyDataSetChanged();

            graph.moveViewToAnimated(graph.getData().getEntryCount(),0, YAxis.AxisDependency.LEFT,1000);


        } else {
            ArrayList<Entry> values = new ArrayList<>();
            values.add(new Entry(0,bpm));

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return graph.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            // drawables only supported on api level 18 and above
            //Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.flag_albania);
            // set1.setFillDrawable(drawable);
            set1.setFillColor(Color.TRANSPARENT);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            graph.setData(data);

            graph.moveViewToX(data.getEntryCount());

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.other_option:
                if (onActionCompleteInterface != null)
                    onActionCompleteInterface.onCompletionResult(RequestID.OPEN_VITAL_INFO,true,getArguments());
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
                            if (vitalManagerInstance != null) {
                                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.pulseOximeter, finalPulseValue, vitalsApiViewModel);
                                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.heartRate, finalHeartRate, vitalsApiViewModel);
                            }
                        }
                        onClick(close_bt);
                        break;
                }
                break;
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
            case R.id.remeasure_bt:
                startMeasure();
                break;
        }
    }


    //PulseMeasureInterface methods
    @Override
    public void updatePulseMessage(String deviceType,String message) {
        message_tv.setText(message);
    }

    @Override
    public void updatePulseValue(String deviceType,int spo2, int bpm, int wave, float pi) {
        spo2_value.setText(spo2 + "%");
        pulse_value.setText(bpm + "");

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }

        addData(bpm);
    }

    @Override
    public void didFinishMeasure(String deviceType,int spo2, int bpm, int wave, float pi) {

        finalPulseValue = spo2 + "";
        finalHeartRate = bpm + "";
        setCurrentState(MeasureState.ended);

        if (isPresentedInsideCallActivity()) {
            if (UserType.isUserPatient() && vitalManagerInstance != null) {
                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.pulseOximeter, finalPulseValue, vitalsApiViewModel);
                vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.heartRate, finalHeartRate, vitalsApiViewModel);
            }
        }
    }

    @Override
    public void didPulseStartMeasure(String deviceType) {

    }

    @Override
    public void didPulseFinishMesureWithFailure(String deviceType,String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        }

        message_tv.setText(error);
        setCurrentState(MeasureState.failed);
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
        message_tv.setText(getString(R.string.connecting));
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        Log.e("bp measure", "state changed "+serailNumber);
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
        Log.d("PulseMeasureFragment","received data");
        if (pulse_value == null) {
            action = new Action() {
                @Override
                public void doItNow() {
                    processSignalMessagesForPulse(data);
                }
            };
        } else {
            processSignalMessagesForPulse(data);
        }
    }
    @Override
    public void assignVitalListener() {
        if (vitalManagerInstance != null) {
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setPulseListener(this);
        }
    }

    @Override
    public void assignVitalDevice(VitalDevice vitalDevice) {
        this.vitalDevice = vitalDevice;
    }

    @Override
    public String getVitalDeviceType() {
        return vitalDevice.getType();
    }

    private void processSignalMessagesForPulse(String data) {
        Log.d("PulseMeasureFragment","processSignalMessagesForPulse");

        Type type = new TypeToken<HashMap<String, String>>() {}.getType();

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    break;
                case VitalsConstant.VitalCallMapKeys.measuring:

                    HashMap<String,Double> value = (HashMap<String,Double>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    HashMap<String,Integer> intValue = new HashMap<>();
                    if (value == null) {
                        intValue = (HashMap<String,Integer>) map.get(VitalsConstant.VitalCallMapKeys.data);
                    } else {
                        for (String key : value.keySet()) {
                            intValue.put(key,value.get(key).intValue());
                        }
                    }

                    Integer spo2 = intValue.get(VitalsConstant.VitalCallMapKeys.spo2);
                    Integer bpm = intValue.get(VitalsConstant.VitalCallMapKeys.bpm);
                    Integer wave = intValue.get(VitalsConstant.VitalCallMapKeys.wave);
                    Integer pi = intValue.get(VitalsConstant.VitalCallMapKeys.pi);

                    if (spo2 != null && bpm != null && wave != null && pi != null) {
                        updatePulseValue(vitalDevice.getType(),spo2,bpm,wave,pi);
                    }


                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didPulseFinishMesureWithFailure(vitalDevice.getType(),errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    HashMap<String,Double> resultValue = (HashMap<String,Double>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    HashMap<String,Integer> resultIntValue = new HashMap<>();
                    if (resultValue == null) {
                        resultIntValue = (HashMap<String,Integer>) map.get(VitalsConstant.VitalCallMapKeys.data);
                    } else {
                        for (String key : resultValue.keySet()) {
                            resultIntValue.put(key,resultValue.get(key).intValue());
                        }
                    }

                    Integer result_spo2 = resultIntValue.get(VitalsConstant.VitalCallMapKeys.spo2);
                    Integer result_bpm = resultIntValue.get(VitalsConstant.VitalCallMapKeys.bpm);
                    Integer result_wave = resultIntValue.get(VitalsConstant.VitalCallMapKeys.wave);
                    Integer result_pi = resultIntValue.get(VitalsConstant.VitalCallMapKeys.pi);

                    if (result_spo2 != null && result_bpm != null && result_wave != null && result_pi != null) {
                        updatePulseValue(vitalDevice.getType(),result_spo2,result_bpm,result_wave,result_pi);
                    }

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
        didPulseStartMeasure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }
}
