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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;
import com.thealer.telehealer.views.call.Interfaces.Action;
import com.thealer.telehealer.views.call.Interfaces.CallVitalPagerInterFace;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rsekar on 12/3/18.
 */

public class PulseMeasureFragment extends VitalMeasureBaseFragment implements
        View.OnClickListener,PulseMeasureInterface {

    private LineChart graph;
    private TextView pulse_value, pulse, spo2_value, spo2, message_tv, title_tv;
    private CustomButton save_bt, close_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay, main_container;

    private String finalPulseValue = "",finalHeartRate = "";
    private ArrayList<Entry> entries = new ArrayList<>();

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pulse_measurement, container, false);

        initView(view);
        return view;
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

        graph.setTouchEnabled(true);
        graph.getDescription().setEnabled(false);
        graph.setDrawGridBackground(false);
        graph.getXAxis().setEnabled(false);
        graph.getAxisLeft().setEnabled(false);
        graph.getAxisRight().setEnabled(false);
        graph.getXAxis().setDrawGridLines(false);
        graph.getAxisLeft().setDrawGridLines(false);
        graph.getAxisRight().setDrawGridLines(false);
        Legend legend = graph.getLegend();
        legend.setEnabled(false);

        Easing.EasingFunction easingFunction = new Easing.EasingFunction() {
            @Override
            public float getInterpolation(float input) {
                return 100;
            }
        };
        graph.animateX(1000, easingFunction);

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
    protected void updateView(int currentState) {
        remeasure_bt.setVisibility(View.GONE);
        close_bt.setVisibility(View.GONE);

        switch (currentState) {
            case MeasureState.notStarted:
                graph.setVisibility(View.GONE);
                result_lay.setVisibility(View.GONE);
                spo2_value.setText("-");
                pulse_value.setText("-");
                message_tv.setText("");
                save_bt.setText(getString(R.string.START));
                break;
            case MeasureState.started:
                graph.setVisibility(View.VISIBLE);
                message_tv.setText("");
                spo2_value.setText("-");
                pulse_value.setText("-");
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

        if (!UserType.isUserPatient() && isPresentedInsideCallActivity()) {
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

    private void addData(int bpm) {
        if (bpm < 1) {
            return;
        }

        entries.add(new Entry(entries.size(), bpm, getResources().getDrawable(R.drawable.app_icon)));
        LineDataSet set1;
        if (graph.getData() != null &&
                entries.size() > 1) {
            set1 = (LineDataSet) graph.getData().getDataSetByIndex(0);
            set1.setValues(entries);
            set1.notifyDataSetChanged();

            configureLineDataSet(set1);
            graph.getData().notifyDataChanged();
            graph.notifyDataSetChanged();
            int totalCount = graph.getData().getEntryCount();
            graph.setVisibleXRange(0, 12);
            graph.moveViewTo(totalCount, 0, YAxis.AxisDependency.LEFT);
        } else {
            set1 = new LineDataSet(entries, "");

            configureLineDataSet(set1);

            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return graph.getAxisLeft().getAxisMinimum();
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            LineData data = new LineData(dataSets);
            graph.setData(data);
            graph.setPinchZoom(false);
            graph.moveViewToX(data.getEntryCount());
        }
    }

    private void configureLineDataSet(LineDataSet lineDataSet) {
        lineDataSet.setCircleColor(getResources().getColor(R.color.app_gradient_start));
        lineDataSet.setColor(getResources().getColor(R.color.app_gradient_start));
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawIcons(false);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setValueTextSize(9f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                            sendVitals(SupportedMeasurementType.pulseOximeter, finalPulseValue,SupportedMeasurementType.heartRate, finalHeartRate,null);
                        } else {
                            onClick(close_bt);
                        }
                        break;
                }
                break;
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
            case R.id.remeasure_bt:
                startMeasure();
                break;
        }
    }


    //PulseMeasureInterface methods
    @Override
    public void updatePulseMessage(String deviceType, String message) {
        message_tv.setText(message);
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void updatePulseValue(String deviceType, int spo2, int bpm, int wave, float pi) {
        spo2_value.setText(spo2 + "%");
        pulse_value.setText(bpm + "");

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }

        addData(bpm);
    }

    @Override
    public void didFinishMeasure(String deviceType, int spo2, int bpm, int wave, float pi) {

        finalPulseValue = spo2 + "";
        finalHeartRate = bpm + "";
        setCurrentState(MeasureState.ended);

        if (isPresentedInsideCallActivity()) {
            if (UserType.isUserPatient() && vitalManagerInstance != null) {
                sendVitals(SupportedMeasurementType.pulseOximeter, finalPulseValue,SupportedMeasurementType.heartRate, finalHeartRate,null);
            }
        }
    }

    @Override
    public void didPulseStartMeasure(String deviceType) {
        entries = new ArrayList<>();

        setCurrentState(MeasureState.started);
    }

    @Override
    public void didPulseFinishMesureWithFailure(String deviceType, String error) {
        EventRecorder.recordVitals("FAIL_MEASURE", vitalDevice.getType());
        didReceivedError(error);
    }

    private void didReceivedError(String error) {
        message_tv.setText(error);
        setCurrentState(MeasureState.failed);
    }

    @Override
    public void startedToConnect(String type, String serailNumber) {
        super.startedToConnect(type,serailNumber);
        message_tv.setText(getString(R.string.connecting));
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void didConnected(String type, String serailNumber) {
        Log.e("pulse measure", "state changed "+serailNumber);
        super.didConnected(type,serailNumber);
    }

    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("PulseMeasureFragment", "received data");
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
            needToAssignIHealthListener = false;
        } else {
            needToAssignIHealthListener = true;
        }
    }

    private void processSignalMessagesForPulse(String data) {
        Log.d("PulseMeasureFragment", "processSignalMessagesForPulse");

        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();

        try {
            HashMap<String, Object> map = Utils.deserialize(data, type);

            switch ((String) map.get(VitalsConstant.VitalCallMapKeys.status)) {
                case VitalsConstant.VitalCallMapKeys.startedToMeasure:
                    if (currentState != MeasureState.started) {
                        setCurrentState(MeasureState.started);
                    }
                    break;
                case VitalsConstant.VitalCallMapKeys.measuring:

                    HashMap<String, Double> value = (HashMap<String, Double>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    HashMap<String, Integer> intValue = new HashMap<>();
                    if (value == null) {
                        intValue = (HashMap<String, Integer>) map.get(VitalsConstant.VitalCallMapKeys.data);
                    } else {
                        for (String key : value.keySet()) {
                            intValue.put(key, value.get(key).intValue());
                        }
                    }

                    Integer spo2 = intValue.get(VitalsConstant.VitalCallMapKeys.spo2);
                    Integer bpm = intValue.get(VitalsConstant.VitalCallMapKeys.bpm);
                    Integer wave = intValue.get(VitalsConstant.VitalCallMapKeys.wave);
                    Integer pi = intValue.get(VitalsConstant.VitalCallMapKeys.pi);

                    if (spo2 != null && bpm != null && wave != null && pi != null) {
                        updatePulseValue(vitalDevice.getType(), spo2, bpm, wave, pi);
                    }


                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didReceivedError(errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    HashMap<String, Double> resultValue = (HashMap<String, Double>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    HashMap<String, Integer> resultIntValue = new HashMap<>();
                    if (resultValue == null) {
                        resultIntValue = (HashMap<String, Integer>) map.get(VitalsConstant.VitalCallMapKeys.data);
                    } else {
                        for (String key : resultValue.keySet()) {
                            resultIntValue.put(key, resultValue.get(key).intValue());
                        }
                    }

                    Integer result_spo2 = resultIntValue.get(VitalsConstant.VitalCallMapKeys.spo2);
                    Integer result_bpm = resultIntValue.get(VitalsConstant.VitalCallMapKeys.bpm);
                    Integer result_wave = resultIntValue.get(VitalsConstant.VitalCallMapKeys.wave);
                    Integer result_pi = resultIntValue.get(VitalsConstant.VitalCallMapKeys.pi);

                    if (result_spo2 != null && result_bpm != null && result_wave != null && result_pi != null) {
                        updatePulseValue(vitalDevice.getType(), result_spo2, result_bpm, result_wave, result_pi);
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
