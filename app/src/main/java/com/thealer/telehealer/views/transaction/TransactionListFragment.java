package com.thealer.telehealer.views.transaction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.transaction.TransactionListViewModel;
import com.thealer.telehealer.views.base.BaseFragment;

public class TransactionListFragment extends BaseFragment {

    private TransactionListViewModel transactionListViewModel;

    private RecyclerView rvTransactions;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        transactionListViewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        transactionListViewModel.loadTransactions();
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
        rvTransactions.setAdapter(new TransactionListAdapter(transactionListViewModel.getTransactions(), new TransactionListAdapter.OnOptionSelected() {
            @Override
            public void onReceiptClick(int pos) {

            }

            @Override
            public void onProcessPaymentClick(int pos) {

            }

            @Override
            public void onRefundClick(int pos) {

            }
        }));
    }
}
