package com.thealer.telehealer.apilayer.models.vitals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.common.OnItemClickListener;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 28,June,2021
 **/
public class VitalThresholdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FragmentActivity activity;
    private OnListItemSelectInterface onListItemSelectInterface;
    List<VitalThresholdModel.VitalsThreshold> vitalThresholdList = new ArrayList<>();
    private boolean isEditable = false;

    public VitalThresholdAdapter(FragmentActivity activity, List<VitalThresholdModel.VitalsThreshold> vitalThresholdList, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.vitalThresholdList = vitalThresholdList;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_threshold_header, viewGroup, false);
        return new HeaderHolder(view);
    }

    public String getUpdatedValue(double submitedValue, String vitalType) {
        String finalValue;
        if (vitalType.equals(SupportedMeasurementType.temperature)) {
            finalValue = submitedValue + "";
        } else {
            finalValue = (int) submitedValue + "";
        }
        return finalValue;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        String rangetype = vitalThresholdList.get(position).getRanges().get(0).range_type != null ? vitalThresholdList.get(position).getRanges().get(0).range_type : "";
        String vitalName = getVitalName(vitalThresholdList.get(position).getVital_type(), rangetype);
        headerHolder.title.setText(vitalName.trim());

        List<VitalThresholdModel.Range> thresholdLimitList = vitalThresholdList.get(position).ranges;
        if (thresholdLimitList == null) {
            thresholdLimitList = new ArrayList<>();
        }
        ThresholdLimitAdapter thresholdLimitAdapter = new ThresholdLimitAdapter(activity, thresholdLimitList, position, isEditable, vitalThresholdList.get(position).getVital_type(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                if (bundle != null && bundle.getString("thresholdValue") == null) {
                    vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(position).setMessage(bundle.getString("thresholdMsg"));
                } else if (bundle != null && !vitalThresholdList.get(bundle.getInt("parentPos")).vital_type.equals(SupportedMeasurementType.bp)) {
                    double upperValue = Utils.get2Decimal(bundle.getString("thresholdValue"));
                    double prevUpperValue = Utils.get2Decimal(vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(position).getHigh_value());
                    double rangeDiff = upperValue - prevUpperValue;
                    vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(position).setHigh_value(getUpdatedValue(upperValue, bundle.getString("vitalType")));
                    for (int i = position + 1; i < vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().size(); i++) {
                        double prevLow = Utils.get2Decimal(vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).getLow_value());
                        double prevHigh = Utils.get2Decimal(vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).getHigh_value());
                        vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).setLow_value(getUpdatedValue((prevLow + rangeDiff), bundle.getString("vitalType")));
                        vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).setHigh_value(getUpdatedValue((prevHigh + rangeDiff), bundle.getString("vitalType")));
                    }
                } else if (bundle != null && vitalThresholdList.get(bundle.getInt("parentPos")).vital_type.equals(SupportedMeasurementType.bp)) {
                    String upperValue = bundle.getString("thresholdValue");
                    String[] upperValueList = upperValue.split("/");
                    double[] upperValueInt = new double[2];
                    upperValueInt[0] = Utils.get2Decimal(upperValueList[0]);
                    upperValueInt[1] = Utils.get2Decimal(upperValueList[1]);
                    String prevUpperValue = vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(position).getHigh_value();
                    String[] prevUpperValueList = prevUpperValue.split("/");
                    double[] prevUpperValueInt = new double[2];
                    prevUpperValueInt[0] = Utils.get2Decimal(prevUpperValueList[0]);
                    prevUpperValueInt[1] = Utils.get2Decimal(prevUpperValueList[1]);
                    double rangeLHS = upperValueInt[0] - prevUpperValueInt[0];
                    double rangeRHS = upperValueInt[1] - prevUpperValueInt[1];
                    vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(position).setHigh_value(upperValue + "");
                    for (int i = position + 1; i < vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().size(); i++) {
                        String[] prevLowStr = vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).getLow_value().split("/");
                        String[] prevHighStr = vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).getHigh_value().split("/");


                        vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).setLow_value(
                                ((getUpdatedValue((Utils.get2Decimal(prevLowStr[0]) + rangeLHS), bundle.getString("vitalType"))) + "/" +
                                        (getUpdatedValue((Utils.get2Decimal(prevLowStr[1]) + rangeRHS), bundle.getString("vitalType")))));

                        vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().get(i).setHigh_value(
                                ((getUpdatedValue((Utils.get2Decimal(prevHighStr[0]) + rangeLHS), bundle.getString("vitalType"))) + "/" +
                                        (getUpdatedValue((Utils.get2Decimal(prevHighStr[1]) + rangeRHS), bundle.getString("vitalType")))));
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(position);
                    }
                });
            }
        }, new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Bundle bundle) {
                if (bundle != null && bundle.getBoolean("isRemove")) {
                    vitalThresholdList.get(bundle.getInt("parentPos")).getRanges().remove(position);

                } else if (bundle != null && bundle.getBoolean("isAdd")) {
                    List<VitalThresholdModel.Range> ranges = vitalThresholdList.get(bundle.getInt("parentPos")).getRanges();
                    VitalThresholdModel.Range currentRangeInfo = new VitalThresholdModel.Range();
                    currentRangeInfo.message = "";
                    currentRangeInfo.abnormal = false;
                    if (vitalThresholdList.get(bundle.getInt("parentPos")).getVital_type().equals(SupportedMeasurementType.bp)) {
                        String[] lowerArray = bundle.getString("lowValue").split("/");
                        String[] upperArray = bundle.getString("highValue").split("/");
                        lowerArray[0] = getUpdatedValue((Utils.get2Decimal(upperArray[0]) + 1), bundle.getString("vitalType"));
                        lowerArray[1] = getUpdatedValue((Utils.get2Decimal(upperArray[1]) + 1), bundle.getString("vitalType"));
                        upperArray[0] = lowerArray[0];
                        upperArray[1] = lowerArray[1];
                        currentRangeInfo.low_value = lowerArray[0] + "/" + lowerArray[1];
                        currentRangeInfo.high_value = upperArray[0] + "/" + upperArray[1];
                    } else {
                        double lowerValue = Utils.get2Decimal(bundle.getString("highValue")) + 1;
                        currentRangeInfo.low_value = getUpdatedValue((lowerValue), bundle.getString("vitalType"));
                        currentRangeInfo.high_value = currentRangeInfo.low_value;
                    }
                    ranges.add(currentRangeInfo);
                    vitalThresholdList.get(bundle.getInt("parentPos")).setRanges(ranges);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
        headerHolder.rvThreshold.setAdapter(thresholdLimitAdapter);
        headerHolder.rvThreshold.setItemAnimator(null);

        if (vitalThresholdList.get(position).isRangeVisible()) {
            headerHolder.rvThreshold.setVisibility(View.VISIBLE);
            headerHolder.ivPlus.setBackgroundResource(R.drawable.ic_minus);
        } else {
            headerHolder.rvThreshold.setVisibility(View.GONE);
            headerHolder.ivPlus.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
        }
        headerHolder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemSelectInterface.onListItemSelected(position, null);

            }
        });
    }


    private String getVitalName(String vital_type, String rangetype) {
        String vital_name;
        switch (vital_type) {
            case SupportedMeasurementType.gulcose:
                vital_name = activity.getString(R.string.bloodGlucose);
                break;
            case SupportedMeasurementType.heartRate:
                vital_name = activity.getString(R.string.heartRate);
                break;
            case SupportedMeasurementType.pulseOximeter:
                vital_name = activity.getString(R.string.pulseOximeter);
                break;
            case SupportedMeasurementType.bp:
                vital_name = activity.getString(R.string.blood_pressure);
                ;
                break;
            case SupportedMeasurementType.temperature:
                vital_name = activity.getString(R.string.bodyTemperature);
                ;
                break;
            case SupportedMeasurementType.weight:
                vital_name = activity.getString(R.string.weight);
                break;
            default:
                vital_name = "";
        }
        if (rangetype.equals(activity.getString(R.string.relative))) {
            vital_name = vital_name + " (" + rangetype + ")";
        }
        return vital_name;
    }

    @Override
    public int getItemCount() {
        return vitalThresholdList.size();
    }

    public void UpdateItem(List<VitalThresholdModel.VitalsThreshold> vitalThresholdList, boolean isEditable) {
        this.vitalThresholdList = vitalThresholdList;
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView ivPlus;
        private RecyclerView rvThreshold;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            ivPlus = (ImageView) itemView.findViewById(R.id.iv_plus);
            rvThreshold = (RecyclerView) itemView.findViewById(R.id.rv_threshold);
            rvThreshold.setLayoutManager(new LinearLayoutManager(activity));
        }
    }


}
