package com.thealer.telehealer.views.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;


public class CustomPatientCountLayout extends ConstraintLayout {


    public ConstraintLayout patient_waiting_count_layout;
    public TextView tv_waiting_count;

    public CustomPatientCountLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CustomPatientCountLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomPatientCountLayout(Context context, AttributeSet attrs, int resId) {
        super(context, attrs, resId);
        init(context, attrs, resId);
    }

    private void init(final Context context, AttributeSet attrs, int resId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.patient_count_layout, this, true);
        tv_waiting_count=findViewById(R.id.tv_waiting_count);
        patient_waiting_count_layout=findViewById(R.id.patient_waiting_count_layout);
    }

    public void setCount(int size){
        if (size==0) {
            patient_waiting_count_layout.setVisibility(GONE);
        } else {
            if (size==1)
                tv_waiting_count.setText("" + size + " " + getContext().getString(R.string.patient_in_waiting_room));
            else
                tv_waiting_count.setText("" + size+" "+getContext().getString(R.string.patients_in_waiting_room));
            patient_waiting_count_layout.setVisibility(VISIBLE);
        }
    }

    private BroadcastReceiver patient_count_update = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int size= Integer.parseInt(bundle.getString(ArgumentKeys.SIZE));
            Log.d("BroadcastListen","CustomPatientCountLayout"+size);
            setCount(size);
        }
    };

    public void registerEvent(){
        Log.d("CustomPatientCountLay","registerEvent");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(patient_count_update, new IntentFilter(getContext().getString(R.string.patient_count_update)));
    }

    public void unregisterEvent(){
        Log.d("CustomPatientCountLay","unregisterEvent");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(patient_count_update);
    }

    @Override
    protected void onAttachedToWindow() {
        registerEvent();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterEvent();
        super.onDetachedFromWindow();
    }
}