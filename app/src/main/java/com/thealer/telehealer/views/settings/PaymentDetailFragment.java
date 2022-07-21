package com.thealer.telehealer.views.settings;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Payments.Transaction;
import com.thealer.telehealer.apilayer.models.Payments.TransactionApiViewModel;
import com.thealer.telehealer.apilayer.models.Payments.VitalVisit;
import com.thealer.telehealer.apilayer.models.Payments.VitalVisitResponse;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.Animation.ConstrainSetUtil;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.RoundCornerButton;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.settings.Adapters.PaymentDetailAdapter;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rsekar on 1/23/19.
 */

public class PaymentDetailFragment extends BaseFragment {

    private CustomRecyclerView recyclerContainer;
    private ConstraintLayout detail_view;
    private TextView call_charges_tv, amount_tv, bill_tv, bill_value_tv;
    private RoundCornerButton call_history, vital_history;
    private ConstraintLayout mainLay;

    private OnViewChangeInterface onViewChangeInterface;
    private Transaction transaction;

    private TransactionApiViewModel transactionApiViewModel;

    private ArrayList<VitalVisit> vitalVisits = new ArrayList<>();
    private ArrayList<RecentsApiResponseModel.ResultBean> calls = new ArrayList<>();

    private boolean isInCallsTab = true;
    private boolean isDetailViewHidden = false;

    private PaymentDetailAdapter paymentDetailAdapter;

    @Nullable
    private Transition detailTransition;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transaction = (Transaction) getArguments().getSerializable(ArgumentKeys.TRANSACTION);
        addObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_detail, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        onViewChangeInterface.updateTitle("");
        onViewChangeInterface.hideOrShowNext(false);
        onViewChangeInterface.hideOrShowClose(false);
        onViewChangeInterface.hideOrShowOtherOption(false);
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        recyclerContainer = view.findViewById(R.id.recyclerContainer);
        recyclerContainer.setScrollable(false);
        loadEmptyViewIfNeeded();
        recyclerContainer.hideEmptyState();

        recyclerContainer.getSwipeLayout().setEnabled(false);
        mainLay = view.findViewById(R.id.main_container);
        detail_view = view.findViewById(R.id.detail_view);
        call_charges_tv = view.findViewById(R.id.call_charges_tv);
        amount_tv = view.findViewById(R.id.amount_tv);
        bill_tv = view.findViewById(R.id.bill_tv);
        bill_value_tv = view.findViewById(R.id.bill_value_tv);
        call_history = view.findViewById(R.id.call_history);
        vital_history = view.findViewById(R.id.vital_history);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });
        nextTv.setVisibility(View.GONE);
        call_charges_tv.setText(getString(R.string.call_charges_for) + " " + transaction.getCreatedMonthYear());
        amount_tv.setText("$" + transaction.getAmount());
        bill_value_tv.setText(transaction.getId());

        call_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInCallsTab) {
                    call_history.assignBackgroundColor(R.color.app_gradient_start);
                    call_history.setTextColor(getResources().getColor(R.color.colorWhite));

                    vital_history.assignBackgroundColor(R.color.colorWhite);
                    vital_history.setTextColor(getResources().getColor(R.color.app_gradient_start));
                    vital_history.assignStrokeColor(R.color.app_gradient_start);

                    isInCallsTab = true;
                    updateRecyclerView();
                }
            }
        });

        vital_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInCallsTab) {
                    vital_history.assignBackgroundColor(R.color.app_gradient_start);
                    vital_history.setTextColor(getResources().getColor(R.color.colorWhite));

                    call_history.assignBackgroundColor(R.color.colorWhite);
                    call_history.setTextColor(getResources().getColor(R.color.app_gradient_start));
                    call_history.assignStrokeColor(R.color.app_gradient_start);

                    isInCallsTab = false;
                    updateRecyclerView();
                }
            }
        });

        recyclerContainer.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("onScrolled", "dy:" + recyclerView.computeVerticalScrollOffset());

                if (recyclerView.computeVerticalScrollOffset() > 0) {
                    if (isDetailViewHidden && detailTransition == null) {
                        updateDetailView(false);
                    }
                } else if (recyclerView.computeVerticalScrollOffset() == 0) {
                    if (!isDetailViewHidden && detailTransition == null) {
                        updateDetailView(true);
                    }
                }

            }
        });

        transactionApiViewModel.getCallLogs(transaction.getCreatedMonthYearWithDash());
        transactionApiViewModel.getVitalVisit(transaction.getCreatedMonthYearWithDash());

        paymentDetailAdapter = new PaymentDetailAdapter(getActivity());
        recyclerContainer.getRecyclerView().setAdapter(paymentDetailAdapter);

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        if (isInCallsTab) {
            paymentDetailAdapter.showCalls(calls);
        } else {
            paymentDetailAdapter.showVitals(vitalVisits);
        }

        loadEmptyViewIfNeeded();
    }

    private void addObserver() {
        transactionApiViewModel = new ViewModelProvider(this).get(TransactionApiViewModel.class);

        transactionApiViewModel.getArrayListMutableLiveData().observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {

                ArrayList<RecentsApiResponseModel.ResultBean> callObjects = (ArrayList<RecentsApiResponseModel.ResultBean>) (Object) baseApiResponseModels;

                calls = new ArrayList<>();

                if (callObjects != null) {
                    for (RecentsApiResponseModel.ResultBean call : callObjects) {
                        if (call.getDurationInSecs() > 0) {
                            if (call.getStartMonthYear().equals(transaction.getCreatedMonthYear()))
                                calls.add(call);
                        }
                    }
                }

                if (isInCallsTab) {
                    updateRecyclerView();
                }

                loadEmptyViewIfNeeded();
            }
        });

        transactionApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel instanceof VitalVisitResponse) {

                    vitalVisits = ((VitalVisitResponse) baseApiResponseModel).getResult();

                    if (!isInCallsTab) {
                        updateRecyclerView();
                    }

                    Set<String> userGuids = new HashSet<>();
                    for (VitalVisit vitalVisit : vitalVisits) {
                        userGuids.add(vitalVisit.getUser_guid());
                    }

                    getUserDetails(userGuids);

                    loadEmptyViewIfNeeded();
                }
            }
        });

        transactionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        loadEmptyViewIfNeeded();
                    }
                }
            }
        });

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(transactionApiViewModel);
        }
    }

    private void loadEmptyViewIfNeeded() {
        if (isInCallsTab) {
            if (calls.size() == 0) {
                recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_CALL_LOGS);
                recyclerContainer.showEmptyState();
            }
        } else {
            if (vitalVisits.size() == 0) {
                recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_VITAL_LOGS);
                recyclerContainer.showEmptyState();
            }
        }
    }

    private void updateDetailView(Boolean show) {
        Transition transition = new ChangeBounds();
        transition.setDuration(800);
        AnticipateOvershootInterpolator anticipateOvershootInterpolator = new AnticipateOvershootInterpolator(1.0f);
        transition.setInterpolator(anticipateOvershootInterpolator);


        this.detailTransition = transition;

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                detailTransition = null;
                isDetailViewHidden = show;
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                detailTransition = null;
            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });


        TransitionManager.beginDelayedTransition(mainLay, transition);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLay);

        ConstrainSetUtil.clearAllConstraint(constraintSet, detail_view.getId());

        ConstrainSetUtil.assignLeftAndRightToMain(constraintSet, mainLay.getId(), detail_view.getId(), 0);

        if (show) {
            onViewChangeInterface.updateTitle("");
            constraintSet.connect(detail_view.getId(), ConstraintSet.TOP, mainLay.getId(), ConstraintSet.TOP);
        } else {
            onViewChangeInterface.updateTitle(call_charges_tv.getText().toString());
            constraintSet.connect(detail_view.getId(), ConstraintSet.BOTTOM, mainLay.getId(), ConstraintSet.TOP);
        }

        constraintSet.applyTo(mainLay);
    }

    private void getUserDetails(Set<String> userGuid) {
        GetUserDetails
                .getInstance(getActivity())
                .getDetails(userGuid)
                .getHashMapMutableLiveData()
                .observe(getActivity(),
                        new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                            @Override
                            public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> data) {
                                if (data != null) {
                                    if (paymentDetailAdapter != null) {
                                        paymentDetailAdapter.setUserDetailHashMap(data);
                                    }
                                }
                            }
                        });
    }

}
