package com.thealer.telehealer.views.home.vitals.measure;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.ResultFetcher;
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
import com.thealer.telehealer.views.home.vitals.VitalsSendBaseFragment;
import com.thealer.telehealer.views.home.vitals.measure.util.MeasureState;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalManagerInstance;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by rsekar on 12/3/18.
 */

public class BPMeasureFragment extends VitalMeasureBaseFragment implements
        View.OnClickListener,BPMeasureInterface {

    private LineChart graph;
    private TextView bp_value,bp,spo2_value,spo2,message_tv,title_tv;
    private CustomButton save_bt,close_bt;
    private Button remeasure_bt;
    private ConstraintLayout result_lay,main_container;

    private String finalBPValue = "",finalHeartRateValue = "";

    @Nullable
    public CallVitalPagerInterFace callVitalPagerInterFace;

    private ArrayList<Entry> entries = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bp_measure, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("BPMeasureFragment","onResume");
    }

    private void initView(View baseView) {
        graph = baseView.findViewById(R.id.graph);
        bp_value = baseView.findViewById(R.id.bp_value);
        bp = baseView.findViewById(R.id.bp);
        spo2_value = baseView.findViewById(R.id.spo2_value);
        spo2 = baseView.findViewById(R.id.spo2);
        message_tv = baseView.findViewById(R.id.message_tv);
        result_lay = baseView.findViewById(R.id.result_lay);
        main_container = baseView.findViewById(R.id.main_container);
        title_tv = baseView.findViewById(R.id.title_tv);

        String measurementType = VitalDeviceType.shared.getMeasurementType(vitalDevice.getType());
        bp.setText(SupportedMeasurementType.getVitalUnit(measurementType));
        spo2.setText(SupportedMeasurementType.getVitalUnit(SupportedMeasurementType.heartRate));

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

        if (isPresentedInsideCallActivity()) {
            graph.getLayoutParams().height = 150;
        } else {
            graph.getLayoutParams().height = 250;
        }

        Legend legend = graph.getLegend();
        legend.setEnabled(false);

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
                result_lay.setVisibility(View.VISIBLE);
                spo2_value.setText("-");
                bp_value.setText("-");
                message_tv.setText("");
                save_bt.setText(getString(R.string.START));
                break;
            case MeasureState.started:
                entries.clear();
                try {
                    if (graph.getData() != null) {
                        graph.getData().clearValues();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                graph.setVisibility(View.VISIBLE);
                message_tv.setText("");
                spo2_value.setText("-");
                bp_value.setText("-");
                result_lay.setVisibility(View.VISIBLE);
                save_bt.setText(getString(R.string.STOP));
                break;
            case MeasureState.ended:
                graph.setVisibility(View.GONE);
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

                bp_value.setText(finalBPValue);
                spo2_value.setText(finalHeartRateValue+"");

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

        if (callVitalPagerInterFace != null && getUserVisibleHint()) {
            if (currentState == MeasureState.failed || currentState == MeasureState.ended || currentState == MeasureState.notStarted) {
                callVitalPagerInterFace.updateState(Constants.idle);
            } else {
                callVitalPagerInterFace.updateState(Constants.measuring);
            }
        }
    }

    private void setData(Double value) {
        if (value < 1.0 || value.isNaN()) {
            return;
        }

        entries.add(new Entry(entries.size(), value.intValue(), getResources().getDrawable(R.drawable.app_icon)));

        LineDataSet set1;

        if (graph.getData() != null &&
                entries.size() > 1) {
            set1 = (LineDataSet) graph.getData().getDataSetByIndex(0);
            //set1.setValues(entries);
            set1.notifyDataSetChanged();
            Log.v("BPMeasureFragment","value - "+value);
            configureLineDataSet(set1);
            graph.getData().notifyDataChanged();
            graph.notifyDataSetChanged();
            int totalCount = graph.getData().getEntryCount();
            graph.setVisibleXRange(0,18);

            //graph.moveViewToAnimated(count,0, YAxis.AxisDependency.LEFT,1000);
            graph.moveViewTo(totalCount,0,YAxis.AxisDependency.LEFT);
        } else {
            set1 = new LineDataSet(entries, "");
            Log.v("BPMeasureFragment","1 value - "+value);

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
            graph.moveViewToX(data.getEntryCount());
            graph.setPinchZoom(false);
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
                        if (vitalManagerInstance != null) {
                            vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(), vitalDevice.getDeviceId());
                            setCurrentState(MeasureState.notStarted);
                        }
                        break;
                    case MeasureState.ended:
                        if (!isPresentedInsideCallActivity()) {
                            sendVitals(SupportedMeasurementType.bp,finalBPValue,SupportedMeasurementType.heartRate, finalHeartRateValue);
                        } else {
                            onClick(close_bt);
                        }
                        break;
                }
                break;
            case R.id.close_bt:
                if (isPresentedInsideCallActivity()) {

                    if (vitalManagerInstance != null) {
                        vitalManagerInstance.getInstance().stopMeasure(vitalDevice.getType(),vitalDevice.getDeviceId());
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

    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String deviceType,String message) {
        message_tv.setText(message);
        result_lay.setVisibility(View.GONE);
    }

    @Override
    public void didStartBPMesure(String deviceType) {
        entries = new ArrayList<>();

        setCurrentState(MeasureState.started);
    }

    @Override
    public void didUpdateBPMesure(String deviceType,ArrayList<Double> value) {

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }

        setData(getAverage(value));
    }

    double getAverage(ArrayList<Double> values)  {

        double total = 0.0;
        for (Double vote : values) {
            total += vote;
        }
        return total/values.size();
    }

    @Override
    public void didUpdateBPM(String deviceType,ArrayList<Double> value) {

        if (currentState != MeasureState.startedToReceieveValues) {
            setCurrentState(MeasureState.startedToReceieveValues);
        }
    }

    @Override
    public void didFinishBPMesure(String deviceType,Double systolicValue, Double diastolicValue, Double heartRate) {
        finalBPValue =  systolicValue.intValue() + "/" + diastolicValue.intValue();
        finalHeartRateValue = heartRate.intValue() + "";

        if (isPresentedInsideCallActivity()) {
            if (UserType.isUserPatient() && vitalManagerInstance != null) {
                sendVitals(SupportedMeasurementType.bp,finalBPValue,SupportedMeasurementType.heartRate, finalHeartRateValue);
            }
        }

        setCurrentState(MeasureState.ended);
    }

    @Override
    public void didFailBPMesure(String deviceType,String error) {
        message_tv.setText(error);
        setCurrentState(MeasureState.failed);
    }

    @Override
    public void didFinishBpMeasure(Object object) {

    }


    @Override
    public void startedToConnect(String type, String serailNumber) {
        super.startedToConnect(type,serailNumber);
        message_tv.setText(getString(R.string.connecting));
        result_lay.setVisibility(View.GONE);
    }

    //VitalPairInterface methods
    @Override
    public void didConnected(String type, String serailNumber) {
        Log.e("bp measure", "state changed "+serailNumber);
        super.didConnected(type,serailNumber);
    }

    //Call Events methods
    @Override
    public void didReceiveData(String data) {
        Log.d("BPMeasureFragment","received data");
        super.didReceiveData(data);
        if (bp_value == null) {
            action = new Action() {
                @Override
                public void doItNow() {
                    processSignalMessagesForBP(data);
                }
            };
        } else {
            processSignalMessagesForBP(data);
        }
    }
    @Override
    public void assignVitalListener() {
        if (vitalManagerInstance != null) {
            Log.d("BPMeasureFragment","assignVitalListener");
            vitalManagerInstance.getInstance().setListener(this);
            vitalManagerInstance.getInstance().setBPListener(this);
            needToAssignIHealthListener = false;
        } else {
            needToAssignIHealthListener = true;
        }
    }

    private void processSignalMessagesForBP(String data) {
        Log.d("BPMeasureFragment","processSignalMessagesForBP");
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
                    graph.setVisibility(View.VISIBLE);

                    ArrayList<Double> values = (ArrayList<Double>) map.get(VitalsConstant.VitalCallMapKeys.data);

                    if (values != null) {
                        didUpdateBPMesure(vitalDevice.getType(),values);
                        Log.d("BPMeasureFragment","processSignalMessagesForBP - not null measuring data");
                    } else {
                        Log.d("BPMeasureFragment","processSignalMessagesForBP - null measuring data");
                    }

                    break;
                case VitalsConstant.VitalCallMapKeys.errorInMeasure:

                    String errorMessage = (String) map.get(VitalsConstant.VitalCallMapKeys.message);
                    didFailBPMesure(vitalDevice.getType(),errorMessage);

                    break;
                case VitalsConstant.VitalCallMapKeys.finishedMeasure:

                    Double sys = (Double) map.get(VitalsConstant.VitalCallMapKeys.systolicValue);
                    Double dys = (Double) map.get(VitalsConstant.VitalCallMapKeys.diastolicValue);
                    Double heartRate = (Double) map.get(VitalsConstant.VitalCallMapKeys.heartRate);

                    didFinishBPMesure(vitalDevice.getType(),sys,dys,heartRate);

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
        didStartBPMesure(vitalDevice.getType());

        if (callVitalPagerInterFace != null)
            callVitalPagerInterFace.didInitiateMeasure(vitalDevice.getType());

    }
}
