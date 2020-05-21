package com.thealer.telehealer.views.common;

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.Util;
import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.VitalsDetailBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Util.Array.PeekingLinearLayoutManager;

import java.util.ArrayList;

public class VitalThresholdSuccessViewDialogFragment extends SuccessViewDialogFragment {
    protected RecyclerView rv_recycler_view;
    private TextView bottomTv;
    private PeekingLinearLayoutManager peekingLinearLayoutManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_view_threshold, container, false);
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        rv_recycler_view = (RecyclerView) view.findViewById(R.id.rv_recycler_view);
        bottomTv = (TextView) view.findViewById(R.id.bottom_tv);
        peekingLinearLayoutManager = new PeekingLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recycler_view.setLayoutManager(peekingLinearLayoutManager);

        super.initView(view);
    }

    @Override
    protected IntentFilter getIntentFilterKey(){
        return new IntentFilter(getString(R.string.success_vital_broadcast_receiver));
    }

    @Override
    protected void stopLoaderAnimation(boolean status){
        super.stopLoaderAnimation(status);
        if (message!=null){
            rv_recycler_view.setVisibility(View.INVISIBLE);
        }
        else {
            rv_recycler_view.setVisibility(View.VISIBLE);
        }
        rv_recycler_view.setVisibility(View.VISIBLE);
        if (BuildConfig.FLAVOR_TYPE.equals(Constants.BUILD_PATIENT)) {
            bottomTv.setVisibility(View.VISIBLE);
            String disclaimerHtml = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "<p>%s    <font color=\"black\">%s</font>   %s</p>\n" +
                    "</body>\n" +
                    "</html>\n";
            bottomTv.setText(Html.fromHtml(String.format(disclaimerHtml,getString(R.string.vitals_patient_bottom_hint_1),getString(R.string.not),getString(R.string.vitals_patient_bottom_hint_2))));
        }
    }

    @Override
    protected void onDataUpdated(Bundle bundle) {
        super.onDataUpdated(bundle);

        if (bundle.getSerializable(Constants.VITAL_DETAIL) != null) {
            ArrayList<VitalsDetailBean> detail = (ArrayList<VitalsDetailBean>) bundle.getSerializable(Constants.VITAL_DETAIL);
            Log.e("SuccessView", "VITAL_DETAIL count "+detail.size());
            rv_recycler_view.setAdapter(new VitalThresholdSuccessViewDialogAdapter(detail,getActivity()));
            rv_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING){

                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE){
                        int postition = peekingLinearLayoutManager.findFirstVisibleItemPosition();
                        if (detail.get(postition).isAbnormal()){
                            loaderIv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, null)));
                        } else {
                            loaderIv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.vital_good, null)));
                        }
                    }
                }
            });
        } else {
            Log.e("SuccessView", "VITAL_DETAIL is null");
        }
    }
}

