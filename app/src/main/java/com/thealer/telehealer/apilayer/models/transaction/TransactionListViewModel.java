package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;

import java.util.ArrayList;

public class TransactionListViewModel extends BaseApiViewModel {

    private ArrayList<TransactionItem> transactions = new ArrayList<>();

    public TransactionListViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTransactions() {
        transactions.add(new TransactionItem(Constants.ChargeStatus.CHARGE_ADDED));
        transactions.add(new TransactionItem(Constants.ChargeStatus.CHARGE_PROCESSED));
        transactions.add(new TransactionItem(Constants.ChargeStatus.CHARGE_PROCESS_FAILED));
    }

    public ArrayList<TransactionItem> getTransactions() {
        return transactions;
    }
}
