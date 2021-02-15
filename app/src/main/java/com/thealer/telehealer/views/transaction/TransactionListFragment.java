package com.thealer.telehealer.views.transaction;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.transaction.TransactionListViewModel;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionListResp;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

public class TransactionListFragment extends BaseFragment {

    private TransactionListViewModel transactionListViewModel;
    private OnViewChangeInterface onViewChangeInterface;

    private RecyclerView rvTransactions;
    private ImageView progressBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        transactionListViewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        onViewChangeInterface.attachObserver(transactionListViewModel);
        transactionListViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModels) {
                if (baseApiResponseModels instanceof TransactionListResp) {
                    rvTransactions.getAdapter().notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                } else {
                    transactionListViewModel.setPage(1);
                    transactionListViewModel.loadTransactions(true);
                }
            }
        });
        transactionListViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                transactionListViewModel.setApiRequested(false);
                progressBar.setVisibility(View.GONE);
                String errorMessage = errorModel.getMessage() != null ? errorModel.getMessage() : getString(R.string.failed_to_connect);
                Utils.showAlertDialog(getContext(), getString(R.string.error), errorMessage, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
            }
        });
        transactionListViewModel.loadTransactions(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        progressBar = view.findViewById(R.id.progressbar);
        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(progressBar);
        rvTransactions.setAdapter(new TransactionListAdapter(transactionListViewModel.getTransactions(), new TransactionListAdapter.OnOptionSelected() {
            @Override
            public void onReceiptClick(int pos) {

            }

            @Override
            public void onProcessPaymentClick(int pos) {
                transactionListViewModel.processPayment(transactionListViewModel.getTransactions().get(pos).getId());
            }

            @Override
            public void onRefundClick(int pos) {

            }
        }));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTransactions.setLayoutManager(linearLayoutManager);

        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < transactionListViewModel.getTotalCount()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        transactionListViewModel.setPage(transactionListViewModel.getPage() + 1);
                        transactionListViewModel.loadTransactions(false);
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
