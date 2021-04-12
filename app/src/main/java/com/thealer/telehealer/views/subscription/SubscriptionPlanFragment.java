package com.thealer.telehealer.views.subscription;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfo;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import java.util.ArrayList;

public class SubscriptionPlanFragment extends BaseFragment implements View.OnClickListener {

    private TextView tvFeatureList;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OnCloseActionInterface onCloseActionInterface;
    private RecyclerView subscriptionPlanRv;
    private CustomRecyclerView subscriptionPlanListCrv;
    private SubscriptionPlanAdapter subscriptionPlanAdapter;
    private ArrayList<PlanInfo> subscriptionPlanList=new ArrayList<>();
    public SubscriptionPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_subscription_plan,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        subscriptionPlanListCrv = (CustomRecyclerView) view.findViewById(R.id.subscription_plan_crv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.lbl_subscriptions_plan));

        subscriptionPlanRv=subscriptionPlanListCrv.getRecyclerView();

        subscriptionPlanAdapter=new SubscriptionPlanAdapter(getActivity());
        subscriptionPlanRv.setAdapter(subscriptionPlanAdapter);

        backIv.setOnClickListener(this);

        prepareData();
    }

    private void prepareData() {
        PlanInfo plan1=new PlanInfo();
        plan1.setPlanName("Limited Practice");
        plan1.setPlanPricing("40");
        plan1.setPlanActivated(false);
        plan1.setExistingFeatures("All Standard Features");
        plan1.setAdditionalFeatures("See Feature List");
        plan1.setFreeDesc("Get this plan Free when");
        plan1.setRpmDesc("15 RPMs performed Monthly");
        plan1.setBtnTitle("Started With Limited");

        PlanInfo plan2=new PlanInfo();
        plan2.setPlanName("Basic Practice");
        plan2.setPlanPricing("75");
        plan2.setPlanActivated(false);
        plan2.setExistingFeatures("All Limited Practice Features");
        plan2.setAdditionalFeatures("Order Capability");
        plan2.setFreeDesc("Get this plan Free when");
        plan2.setRpmDesc("30 RPMs performed Monthly");
        plan2.setBtnTitle("Started With Basic");

        PlanInfo plan3=new PlanInfo();
        plan3.setPlanName("Better Practice");
        plan3.setPlanPricing("125");
        plan3.setPlanActivated(false);
        plan3.setExistingFeatures("All Basic Practice Features");
        plan3.setAdditionalFeatures("Record Visit");
        plan3.setFreeDesc("Get this plan Free when");
        plan3.setRpmDesc("45 RPMs performed Monthly");
        plan3.setBtnTitle("Started With Better");

        PlanInfo plan4=new PlanInfo();
        plan4.setPlanName("Ideal Practice");
        plan4.setPlanPricing("175");
        plan4.setPlanActivated(false);
        plan4.setExistingFeatures("All Better Practice Features");
        plan4.setAdditionalFeatures("Auto Transcript");
        plan4.setFreeDesc(getString(R.string.str_free_data));
        plan4.setRpmDesc("60 RPMs performed Monthly");
        plan4.setBtnTitle("Started With Ideal");
        subscriptionPlanList.add(plan1);
        subscriptionPlanList.add(plan2);
        subscriptionPlanList.add(plan3);
        subscriptionPlanList.add(plan4);

        subscriptionPlanAdapter.setAdapterData(subscriptionPlanList);
        subscriptionPlanAdapter.notifyDataSetChanged();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }
}