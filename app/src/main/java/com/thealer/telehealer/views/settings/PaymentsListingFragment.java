package com.thealer.telehealer.views.settings;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.Payments.Transaction;
import com.thealer.telehealer.apilayer.models.Payments.TransactionApiViewModel;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.ClickListener;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.settings.Adapters.PaymentAdapter;

import java.util.ArrayList;


/**
 * Created by rsekar on 1/22/19.
 */

public class PaymentsListingFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private CustomRecyclerView recyclerContainer;

    private OnActionCompleteInterface onActionCompleteInterface;

    private TransactionApiViewModel transactionApiViewModel;

    private PaymentAdapter paymentAdapter;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private TextView nextTv;
    private ImageView closeIv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_listing, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        closeIv = (ImageView) view.findViewById(R.id.close_iv);
        recyclerContainer = view.findViewById(R.id.recyclerContainer);
        recyclerContainer.setScrollable(false);
        recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        recyclerContainer.hideEmptyState();

        toolbarTitle.setText(getString(R.string.call_charges));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });
        nextTv.setVisibility(View.GONE);
        closeIv.setVisibility(View.VISIBLE);
        closeIv.setImageDrawable(getActivity().getDrawable(R.drawable.ic_info_24dp));
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCurrentTransaction();
            }
        });

        paymentAdapter = new PaymentAdapter(getActivity(), new ArrayList<>());
        recyclerContainer.getRecyclerView().setAdapter(paymentAdapter);

        paymentAdapter.clickListener = new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Transaction transaction = paymentAdapter.getTransactions().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.TRANSACTION, transaction);
                onActionCompleteInterface.onCompletionResult(RequestID.TRANSACTION_DETAIL, true, bundle);
            }
        };

        recyclerContainer.getSwipeLayout().setEnabled(false);
        transactionApiViewModel.getTransactions();
    }

    private void addObserver() {
        transactionApiViewModel = ViewModelProviders.of(this).get(TransactionApiViewModel.class);

        transactionApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof TransactionResponse) {
                        TransactionResponse transactionResponse = (TransactionResponse) baseApiResponseModel;
                        ArrayList<Transaction> transactions = transactionResponse.mergeTransactions();

                        if (transactions.size() == 0) {
                            loadEmptyView();
                        }

                        paymentAdapter.update(transactions);
                    } else {
                        loadEmptyView();
                    }
                } else {
                    loadEmptyView();
                }

            }
        });

        transactionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null)
                    loadEmptyView();
            }
        });

        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).attachObserver(transactionApiViewModel);
        }
    }

    private void loadEmptyView() {
        recyclerContainer.setEmptyState(EmptyViewConstants.EMPTY_PAYMENTS);
        recyclerContainer.showEmptyState();
    }

    @Override
    public void doCurrentTransaction() {
        onActionCompleteInterface.onCompletionResult(RequestID.CARD_INFORMATION_VIEW, true, null);
    }
}
