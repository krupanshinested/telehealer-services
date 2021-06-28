package com.thealer.telehealer.apilayer.models.vitals;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HeaderHolder headerHolder = (HeaderHolder) holder;
        String rangetype=vitalThresholdList.get(position).getRanges().get(0).range_type !=null?vitalThresholdList.get(position).getRanges().get(0).range_type:"";
            String vitalName=getVitalName(vitalThresholdList.get(position).getVital_type(),rangetype);
        headerHolder.title.setText(vitalName.trim());

        List<VitalThresholdModel.Range> thresholdLimitList = vitalThresholdList.get(position).ranges;
        if(thresholdLimitList == null){
            thresholdLimitList=new ArrayList<>();
        }
        ThresholdLimitAdapter thresholdLimitAdapter = new ThresholdLimitAdapter(activity, thresholdLimitList,false,vitalThresholdList.get(position).getVital_type());
            headerHolder.rvThreshold.setAdapter(thresholdLimitAdapter);

        if(vitalThresholdList.get(position).isRangeVisible()){
            headerHolder.rvThreshold.setVisibility(View.VISIBLE);
            headerHolder.ivPlus.setBackgroundResource(R.drawable.ic_minus);
        }else{
            headerHolder.rvThreshold.setVisibility(View.GONE);
            headerHolder.ivPlus.setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
        }
        headerHolder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemSelectInterface.onListItemSelected(position,null);

            }
        });
    }

    private String getVitalName(String vital_type,String rangetype) {
        String vital_name;
        switch (vital_type){
            case "glucose":
                vital_name=activity.getString(R.string.bloodGlucose);
                break;
            case "heartRate":
                vital_name=activity.getString(R.string.heartRate);
                break;
            case "pulseOximeter":
                vital_name=activity.getString(R.string.pulseOximeter);
                break;
            case "bp":
                vital_name=activity.getString(R.string.blood_pressure_heart_rate);;
                break;
            case "temperature":
                vital_name=activity.getString(R.string.bodyTemperature);;
                break;
            case "weight":
                vital_name=activity.getString(R.string.weight);;
                break;
            default:
                vital_name="";
        }
        if(rangetype.equals(activity.getString(R.string.relative))){
            vital_name=vital_name+" ("+rangetype+")";
        }
        return vital_name;
    }

    @Override
    public int getItemCount() {
        return vitalThresholdList.size();
    }

    public void UpdateItem(List<VitalThresholdModel.VitalsThreshold> vitalThresholdList, boolean isEditable) {
        this.vitalThresholdList=vitalThresholdList;
        isEditable = false;
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
