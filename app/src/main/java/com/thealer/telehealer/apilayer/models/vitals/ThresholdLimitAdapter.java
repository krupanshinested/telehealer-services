package com.thealer.telehealer.apilayer.models.vitals;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import org.w3c.dom.Text;

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
    private OnListItemSelectInterface onListItemSelectInterface;
    int parentPos;

    public ThresholdLimitAdapter(FragmentActivity activity, List<VitalThresholdModel.Range> thresholdLimitList,int parentPos,boolean isEditable,String vitalType,OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.thresholdLimitList = thresholdLimitList;
        this.isEditable = isEditable;
        this.vitalType=vitalType;
        this.onListItemSelectInterface=onListItemSelectInterface;
        this.parentPos=parentPos;
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
        final String[] upperLeft = {""};
        final String[] upperRight = {""};
        VitalThresholdModel.Range rangeInfo = thresholdLimitList.get(position);
        if(isEditable){
            itemHolder.clEditable.setVisibility(View.VISIBLE);
            itemHolder.clFix.setVisibility(View.GONE);

            itemHolder.etMessage.setText(rangeInfo.message);
            itemHolder.switchAbnormal.setChecked(rangeInfo.abnormal);
            if(position==(thresholdLimitList.size()-1)){
                itemHolder.tvRemove.setVisibility(View.VISIBLE);
            }else{
                itemHolder.tvRemove.setVisibility(View.GONE);
            }
            if(vitalType.equals("bp")){
                String [] lowerArray=rangeInfo.low_value.split("/");
                String[] upperArray=rangeInfo.high_value.split("/");
                upperLeft[0] =upperArray[0];
                upperRight[0]=upperArray[1];
                itemHolder.etLowerLeft.setText(lowerArray[0]);
                itemHolder.etLowerRight.setText(lowerArray[1]);
                itemHolder.etUpperLeft.setText(upperLeft[0]);
                itemHolder.etUpperRight.setText(upperRight[0]);
                itemHolder.clDoubleValue.setVisibility(View.VISIBLE);
                itemHolder.clSingleValue.setVisibility(View.GONE);
            }else{
                itemHolder.etLower.setText(rangeInfo.low_value);
                itemHolder.etUpper.setText(rangeInfo.high_value);
                itemHolder.clSingleValue.setVisibility(View.VISIBLE);
                itemHolder.clDoubleValue.setVisibility(View.GONE);
            }
        }else {
            itemHolder.clEditable.setVisibility(View.GONE);
            itemHolder.clFix.setVisibility(View.VISIBLE);
            String title = rangeInfo.low_value + " - " + rangeInfo.high_value;
            itemHolder.title.setText(title);
            String unit = getUnit(vitalType);
            itemHolder.unit.setText(unit);
            if (rangeInfo.getAbnormal() != null && rangeInfo.getAbnormal()) {
                itemHolder.status.setVisibility(View.VISIBLE);
            } else {
                itemHolder.status.setVisibility(View.GONE);
            }
            itemHolder.msg.setText(rangeInfo.message);
        }
        itemHolder.switchAbnormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rangeInfo.setAbnormal(!rangeInfo.getAbnormal());
                notifyDataSetChanged();
            }
        });
        itemHolder.etUpperLeft.addTextChangedListener(new TextWatcher() {
            int startChanged,countChanged;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startChanged = start;
                countChanged = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                itemHolder.etUpperLeft.setSelection(startChanged+countChanged);
                upperLeft[0] =s.toString();
                String thresholdStr=(Integer.parseInt(upperLeft[0])+1)+"/"+(Integer.parseInt(upperRight[0]));
                Bundle bundle=new Bundle();
                bundle.putString("thresholdValue",thresholdStr);
                bundle.putInt("parentPos",parentPos);
                onListItemSelectInterface.onListItemSelected(position,bundle);
            }
        });
        itemHolder.etLowerRight.addTextChangedListener(new TextWatcher() {
            int startChanged,countChanged;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startChanged = start;
                countChanged = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                itemHolder.etUpperLeft.setSelection(startChanged+countChanged);
                upperRight[0] =s.toString();
                String thresholdStr=(Integer.parseInt(upperLeft[0]))+"/"+(Integer.parseInt(upperRight[0])+1);
                Bundle bundle=new Bundle();
                bundle.putString("thresholdValue",thresholdStr);
                bundle.putInt("parentPos",parentPos);
                onListItemSelectInterface.onListItemSelected(position,bundle);
            }
        });
        itemHolder.etUpper.addTextChangedListener(new TextWatcher() {
            int startChanged,countChanged;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startChanged = start;
                countChanged = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                itemHolder.etUpper.setSelection(startChanged+countChanged);
                Bundle bundle=new Bundle();
                bundle.putString("thresholdValue",s.toString());
                bundle.putInt("parentPos",parentPos);
                onListItemSelectInterface.onListItemSelected(position,bundle);

            }
        });
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
        private ConstraintLayout clFix,clEditable,clSingleValue,clDoubleValue;

        private EditText etLower,etUpper,etMessage,etLowerLeft,etLowerRight,etUpperLeft,etUpperRight;
        private TextView tvRemove,tvAdd;
        private Switch switchAbnormal;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            status = (TextView) itemView.findViewById(R.id.status);
            unit = (TextView) itemView.findViewById(R.id.unit);
            msg = (TextView) itemView.findViewById(R.id.msg);
            clFix = (ConstraintLayout) itemView.findViewById(R.id.cl_fix);
            clEditable = (ConstraintLayout) itemView.findViewById(R.id.cl_editable);

            etLower = (EditText) itemView.findViewById(R.id.et_lower);
            etUpper = (EditText) itemView.findViewById(R.id.et_upper);

            etLowerLeft = (EditText) itemView.findViewById(R.id.et_lower_left);
            etLowerRight = (EditText) itemView.findViewById(R.id.et_lower_right);
            etUpperLeft = (EditText) itemView.findViewById(R.id.et_upper_left);
            etUpperRight = (EditText) itemView.findViewById(R.id.et_upper_right);


            etMessage = (EditText) itemView.findViewById(R.id.et_message);
            tvRemove = (TextView) itemView.findViewById(R.id.tv_remove);
            tvAdd = (TextView) itemView.findViewById(R.id.tv_add);
            switchAbnormal = (Switch) itemView.findViewById(R.id.switch_abnormal);

            clSingleValue = (ConstraintLayout) itemView.findViewById(R.id.cl_single_value);
            clDoubleValue = (ConstraintLayout) itemView.findViewById(R.id.cl_double_value);
        }
    }

}
