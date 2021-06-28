package com.thealer.telehealer.apilayer.models.vitals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;

import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 28,June,2021
 **/
public class ThresholdLimitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FragmentActivity activity;
    private List<VitalThresholdModel.Range> thresholdLimitList;
    private boolean isEditable =false;
    private  String vitalType="";

    public ThresholdLimitAdapter(FragmentActivity activity, List<VitalThresholdModel.Range> thresholdLimitList,boolean isEditable,String vitalType) {
        this.activity = activity;
        this.thresholdLimitList = thresholdLimitList;
        this.isEditable = false;
        this.vitalType=vitalType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_threshold_raw_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        VitalThresholdModel.Range rangeInfo = thresholdLimitList.get(position);
        String title = rangeInfo.low_value+" - "+rangeInfo.high_value;

        itemHolder.title.setText(title);
        String unit=getUnit(vitalType);
        itemHolder.unit.setText(unit);
        if(rangeInfo.getAbnormal()!=null && rangeInfo.getAbnormal()){
            itemHolder.status.setVisibility(View.VISIBLE);
        }else{
            itemHolder.status.setVisibility(View.GONE);
        }
        itemHolder.msg.setText(rangeInfo.message);
    }

    private String getUnit(String vitalType) {
        String unit;
        switch (vitalType){
            case "glucose":
                unit=activity.getString(R.string.mgdl);
                break;
            case "heartRate":
                unit=activity.getString(R.string.bpm);
                break;
            case "pulseOximeter":
                unit=activity.getString(R.string.percentage);
                break;
            case "bp":
                unit=activity.getString(R.string.mmhg);;
                break;
            case "temperature":
                unit=activity.getString(R.string.fahrenheit);;
                break;
            case "weight":
                unit=activity.getString(R.string.lbs);;
                break;
            default:
                unit="";
        }
        return unit;


    }

    @Override
    public int getItemCount() {
        return thresholdLimitList.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView title,unit,status,msg;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            status = (TextView) itemView.findViewById(R.id.status);
            unit = (TextView) itemView.findViewById(R.id.unit);
            msg = (TextView) itemView.findViewById(R.id.msg);
        }
    }

}
