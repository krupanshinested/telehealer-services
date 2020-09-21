package com.thealer.telehealer.views.home.vitals.measure;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
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
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

/**
 * Created by rsekar on 12/3/18.
 */

public class PulseMeasureFragment extends BaseFragment implements VitalPairInterface,
        View.OnClickListener,PulseMeasureInterface,VitalBatteryFetcher {

    private OnActionCompleteInterface onActionCompleteInterface;
    private VitalManagerInstance vitalManagerInstance;
    private ToolBarInterface toolBarInterface;
    private OnViewChangeInterface onViewChangeInterface;

    private VitalDevice vitalDevice;

    private ImageView otherOptionView;
    private LineChart graph;
    private TextView pulse_value,pulse,spo2_value,spo2,message_tv;
    private CustomButton save_bt,close_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay;

    private int currentState;
    private Boolean isNeedToTrigger = false;

    private String finalPulseValue = "",finalHeartRate = "";
    private VitalsApiViewModel vitalsApiViewModel;

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
        View view = inflater.inflate(R.layout.fragment_pulse_measurement, container, false);

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
        vitalManagerInstance.getInstance().setPulseListener(this);
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
        graph = baseView.findViewById(R.id.graph);
        pulse = baseView.findViewById(R.id.pulse);
        pulse_value = baseView.findViewById(R.id.pulse_value);
        spo2_value = baseView.findViewById(R.id.spo2_value);
        spo2 = baseView.findViewById(R.id.spo2);
        message_tv = baseView.findViewById(R.id.message_tv);
        result_lay = baseView.findViewById(R.id.result_lay);

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
                close_bt.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.SAVE_AND_CLOSE));
                close_bt.setText(getString(R.string.CLOSE));

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
                        vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
                        setCurrentState(MeasureState.notStarted);
                        break;
                    case MeasureState.ended:
                        vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.pulseOximeter,finalPulseValue, vitalsApiViewModel);
                        vitalManagerInstance.getInstance().saveVitals(SupportedMeasurementType.heartRate,finalHeartRate, vitalsApiViewModel);

                        getActivity().finish();
                        break;
                }
                break;
            case R.id.close_bt:
                getActivity().finish();
                break;
            case R.id.remeasure_bt:
                startMeasure();
                break;
        }
    }


    //PulseMeasureInterface methods
    @Override
    public void updatePulseMessage(String message) {
        message_tv.setText(message);
    }

    @Override
    public void updatePulseValue(int spo2, int bpm, int wave, float pi) {
        spo2_value.setText(spo2 + "%");
        pulse_value.setText(bpm + "");

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }

        addData(bpm);
    }

    @Override
    public void didFinishMeasure(int spo2, int bpm, int wave, float pi) {

        finalPulseValue = spo2 + "";
        finalHeartRate = bpm + "";
        setCurrentState(MeasureState.ended);
    }

    @Override
    public void didPulseStartMeasure() {

    }

    @Override
    public void didPulseFinishMesureWithFailure(String error) {
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
