package com.thealer.telehealer.views.subscription;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfoBean;
import com.thealer.telehealer.apilayer.models.subscription.SubscriptionViewModel;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

import static com.thealer.telehealer.common.Constants.activatedPlan;
import static com.thealer.telehealer.common.Constants.isFromSubscriptionPlan;


public class ActivePlanFragment extends BaseFragment implements View.OnClickListener {
    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvPlanName, tvTotalRpm;
    private ImageView backIv;
    private SubscriptionViewModel subscriptionViewModel;
    private Button btnUnsubscribe, btnChange;
    private List<PlanInfoBean.Result> planList = new ArrayList<>();

    public ActivePlanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

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

                            prePareData();

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_plan, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        tvPlanName = (TextView) view.findViewById(R.id.tv_plan_name);
        tvTotalRpm = (TextView) view.findViewById(R.id.tv_total_rpm);
        btnChange = (Button) view.findViewById(R.id.btn_change);
        btnUnsubscribe = (Button) view.findViewById(R.id.btn_unsubscribe);
        toolbarTitle.setText(getString(R.string.lbl_my_subscriptions));

        backIv.setOnClickListener(this);
        btnUnsubscribe.setOnClickListener(this);
        btnChange.setOnClickListener(this);

        if (activatedPlan == -1 && isFromSubscriptionPlan) {
            isFromSubscriptionPlan = false;
            onCloseActionInterface.onClose(false);
        } else {
            subscriptionViewModel.fetchSubscriptionPlanList();
        }
    }

    private void prePareData() {
        if (planList != null && planList.size() > 0) {
            activatedPlan=-1;
            for (int i = 0; i < planList.size(); i++) {
                PlanInfoBean.Result currentPlan = planList.get(i);
                if (currentPlan.isPurchased()) {
                    activatedPlan = i;
                    i = planList.size() + 1;
                }
            }

            if (activatedPlan >= 0) {
                PlanInfoBean.Result currentPlanInfo = planList.get(activatedPlan);
                if (currentPlanInfo.isCanReshedule()) {
                    btnUnsubscribe.setText(getString(R.string.str_subscribe));
                }else {
                    btnUnsubscribe.setText(getString(R.string.str_unsubscribe));
                }
                if (currentPlanInfo.isCancelled() && currentPlanInfo.isPurchased()) {
                    btnUnsubscribe.setVisibility(View.GONE);
                }else{
                    btnUnsubscribe.setVisibility(View.VISIBLE);
                }
                tvPlanName.setText(currentPlanInfo.getName());
                tvTotalRpm.setText(currentPlanInfo.getRpm_count());
            } else {
                visitSubscriptionPlan();
            }
        } else {
            visitSubscriptionPlan();
        }

    }

    private void visitSubscriptionPlan() {
        SubscriptionPlanFragment subscriptionPlanFragment = new SubscriptionPlanFragment();
        showSubFragmentInterface.onShowFragment(subscriptionPlanFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.btn_unsubscribe:
                manageSubscription(v);
                break;
            case R.id.btn_change:
                SubscriptionPlanFragment subscriptionPlanFragment = new SubscriptionPlanFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.IS_CHANGE_PLAN, true);
                subscriptionPlanFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(subscriptionPlanFragment);
                break;
        }
    }

    private void manageSubscription(View v) {
        if (activatedPlan >= 0 && planList.size() > 0) {
            PlanInfoBean.Result currentPlan = planList.get(activatedPlan);
            if (currentPlan.isCanReshedule())
                subscriptionViewModel.purchaseSubscriptionPlan(currentPlan.getPlan_id(), currentPlan.getBilling_cycle());
            else if(currentPlan.isCancelled() && currentPlan.isPurchased()){
                showToast(getString(R.string.str_plan_is_continue_till));
            }
            else
                selectReason(v);
        }
    }


    //Allow physician to view list of support staff. Also physician can request to add them.
    private void selectReason(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View layoutInflateView = layoutInflater.inflate
                (R.layout.cancel_reason_alert, (ViewGroup) v.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle = layoutInflateView.findViewById(R.id.header_title);

        Button btnConfirm = layoutInflateView.findViewById(R.id.btn_confirm);
        Button btnCancel = layoutInflateView.findViewById(R.id.btn_cancel);
        Spinner spinner = (Spinner) layoutInflateView.findViewById(R.id.spinner);
        EditText commentsEt = (EditText) layoutInflateView.findViewById(R.id.comments_et);

        headerTitle.setText(getString(R.string.str_plan_is_continue_till));
        List<String> tempList = new ArrayList<>();
        tempList = new ArrayList<>();

        tempList.add("Select your reason");
        tempList.add("Reason 1");
        tempList.add("Reason 2");
        tempList.add("Reason 3");
        tempList.add("Reason 4");
        tempList.add("Other");
        commentsEt.setVisibility(View.GONE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tempList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        List<String> finalTempList = tempList;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == (finalTempList.size() - 1)) {
                    commentsEt.setVisibility(View.VISIBLE);
                } else {
                    commentsEt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptionViewModel.unSubscriptionPlan();
                dialog.dismiss();
                isFromSubscriptionPlan = false;
                onCloseActionInterface.onClose(false);
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}