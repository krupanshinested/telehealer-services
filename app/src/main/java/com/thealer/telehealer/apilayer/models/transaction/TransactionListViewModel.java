package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

import java.util.ArrayList;

public class TransactionListViewModel extends BaseApiViewModel {

    private ArrayList<TransactionItem> transactions = new ArrayList<>();

    public TransactionListViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTransactions() {
        for (int i = 0; i < 10; i++) {
            transactions.add(new TransactionItem());
        }
    }

    public ArrayList<TransactionItem> getTransactions() {
        return transactions;
    }
}
