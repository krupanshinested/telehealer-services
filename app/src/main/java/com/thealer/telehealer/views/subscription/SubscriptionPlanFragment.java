package com.thealer.telehealer.views.subscription;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfoBean;
import com.thealer.telehealer.apilayer.models.subscription.SubscriptionViewModel;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.common.Constants.activatedPlan;
import static com.thealer.telehealer.common.Constants.isFromSubscriptionPlan;

public class SubscriptionPlanFragment extends BaseFragment implements View.OnClickListener, OnAdapterListener {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OnCloseActionInterface onCloseActionInterface;
    private RecyclerView subscriptionPlanRv;
    private CustomRecyclerView subscriptionPlanListCrv;
    private SubscriptionPlanAdapter subscriptionPlanAdapter;
    private SubscriptionViewModel subscriptionViewModel;
    private AttachObserverInterface attachObserverInterface;
    private boolean isChangePlan = false;
    public static boolean isContinuePlan = false;
    public static boolean isCurrentPlan = true;
    private boolean isResubscriptPlan = false;
    private boolean isHideBack = false;
    private List<PlanInfoBean.Result> planList = new ArrayList<>();

    public SubscriptionPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            isChangePlan = getArguments().getBoolean(ArgumentKeys.IS_CHANGE_PLAN, false);
            isContinuePlan = getArguments().getBoolean(ArgumentKeys.IS_CONTINUE_PLAN, false);
            isResubscriptPlan = getArguments().getBoolean(ArgumentKeys.IS_RESUBSCRIBE_PLAN, false);
            isHideBack = getArguments().getBoolean(ArgumentKeys.IS_HIDE_BACK, false);
        }

        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        attachObserverInterface.attachObserver(subscriptionViewModel);
        subscriptionViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                String title = getString(R.string.failure);
                if (!errorModel.isCCCaptured() || !errorModel.isDefaultCardValid()) {
                    sendSuccessViewBroadCast(getActivity(), false, title, errorModel.getMessage());
                    PaymentInfo paymentInfo = new PaymentInfo();
                    paymentInfo.setCCCaptured(errorModel.isCCCaptured());
                    paymentInfo.setSavedCardsCount(errorModel.getSavedCardsCount());
                    paymentInfo.setDefaultCardValid(errorModel.isDefaultCardValid());
                    AppPaymentCardUtils.handleCardCasesFromPaymentInfo(getActivity(), paymentInfo, "");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));

                    if (errorModel != null && !TextUtils.isEmpty(errorModel.getMessage())) {
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                    } else {
                        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.something_went_wrong_try_again));
                    }

                    LocalBroadcastManager
                            .getInstance(getActivity())
                            .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                    .putExtras(bundle));
                }
            }
        });
        subscriptionViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof PlanInfoBean) {
                        PlanInfoBean planInfoBean = (PlanInfoBean) baseApiResponseModel;
                        if (planInfoBean != null && planInfoBean.getResults().size() > 0) {
                            if (planList == null || planList.isEmpty())
                                planList = planInfoBean.getResults();
                            subscriptionPlanAdapter.setAdapterData(planList);
                            subscriptionPlanAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Utils.showAlertDialog(getActivity(), getString(R.string.success), getString(R.string.str_plan_is_subscribe_now), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onCloseActionInterface.onClose(false);
                            }
                        }, null);
                    }
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_plan, container, false);
        isFromSubscriptionPlan = true;
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
        subscriptionPlanRv = subscriptionPlanListCrv.getRecyclerView();

        if (isHideBack)
            backIv.setVisibility(View.GONE);

        subscriptionPlanAdapter = new SubscriptionPlanAdapter(getActivity(), this);
        subscriptionPlanRv.setAdapter(subscriptionPlanAdapter);
        subscriptionViewModel.fetchSubscriptionPlanList();
        backIv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    @Override
    public void onEventTrigger(Bundle bundle) {
        if (bundle != null) {
            int pos = bundle.getInt(ArgumentKeys.ITEM_CLICK_PARENT_POS);
            activatedPlan = pos;

            if (isChangePlan) {
                subscriptionViewModel.changeSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID));
            } else if (isContinuePlan) {
                subscriptionViewModel.purchaseSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID), bundle.getString(ArgumentKeys.BillingCycle));
            } else if (isResubscriptPlan) {
                subscriptionViewModel.purchaseSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID), bundle.getString(ArgumentKeys.BillingCycle));
            } else {
                subscriptionViewModel.purchaseSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID), bundle.getString(ArgumentKeys.BillingCycle));
            }

//            if(isChangePlan) {
//                subscriptionViewModel.changeSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID));
//            }else {
//                subscriptionViewModel.purchaseSubscriptionPlan(bundle.getString(ArgumentKeys.PlanID), bundle.getString(ArgumentKeys.BillingCycle));
        }
    }
}