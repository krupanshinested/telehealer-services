package com.thealer.telehealer.views.subscription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.OnAdapterListener;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.subscription.PlanInfoBean;
import com.thealer.telehealer.apilayer.models.subscription.SubscriptionViewModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionHistoryActivity extends BaseActivity implements OnAdapterListener {

    private SubscriptionViewModel subscriptionViewModel;
    private SubscriptionHistoryAdapter subscriptionHistoryAdapter;
    private List<PlanInfoBean.Result> planList = new ArrayList<>();
    private int page = 1;
    private RecyclerView subscripthistory;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_history);

        initObserver();
        initview();

    }

    private void initObserver() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        attachObserver(subscriptionViewModel);
        subscriptionViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof PlanInfoBean) {
                        PlanInfoBean planInfoBean = (PlanInfoBean) baseApiResponseModel;
                        if (planInfoBean != null && planInfoBean.getResults().size() > 0) {
                            if (planList == null || planList.isEmpty())
                                planList = planInfoBean.getResults();
                            subscriptionHistoryAdapter.setAdapterData(planList);
                            subscriptionHistoryAdapter.notifyDataSetChanged();
                        }
                    } else {
//                        Utils.showAlertDialog(getActivity(), getString(R.string.success), getString(R.string.str_plan_is_subscribe_now), getString(R.string.ok), null, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                onCloseActionInterface.onClose(false);
//                            }
//                        }, null);
                    }
                }
            }
        });
    }

    private void initview() {
        subscripthistory = (RecyclerView) findViewById(R.id.rv_subscripthistory);

        subscriptionHistoryAdapter = new SubscriptionHistoryAdapter(SubscriptionHistoryActivity.this, this);
        subscripthistory.setAdapter(subscriptionHistoryAdapter);
        makeApiCall();

        subscripthistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.findFirstVisibleItemPosition() < planList.size()) {
                            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > 0) {
                                linearLayoutManager.setSmoothScrollbarEnabled(true);
                                linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.findFirstCompletelyVisibleItemPosition(), 0);
                            }
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < planList.size()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        page = page + 1;
                        makeApiCall();
                    } else {

                    }
                } else {

                }
            }
        });

    }

    private void makeApiCall() {

//        subscriptionViewModel.fetchSubscriptionHistoryList(page);

    }

    @Override
    public void onEventTrigger(Bundle bundle) {

    }
}