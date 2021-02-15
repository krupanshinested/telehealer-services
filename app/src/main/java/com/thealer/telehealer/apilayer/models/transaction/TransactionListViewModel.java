package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionListResp;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

public class TransactionListViewModel extends BaseApiViewModel {

    private ArrayList<TransactionItem> transactions = new ArrayList<>();

    private int page = 1;
    private int totalCount;
    private boolean isApiRequested;

    public TransactionListViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTransactions(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            fetchToken(new BaseViewInterface() {
                @Override
                public void onStatus(boolean status) {
                    if (status) {
                        getAuthApiService().transactionPaginate(true, page, 5)
                                .compose(applySchedulers())
                                .subscribe(new RAObserver<TransactionListResp>(getProgress(isShowProgress)) {
                                    @Override
                                    public void onSuccess(TransactionListResp response) {
                                        isApiRequested = false;
                                        if (page == 1) {
                                            transactions.clear();
                                        }
                                        transactions.addAll(response.getResult());
                                        totalCount = response.getCount();
                                        baseApiResponseModelMutableLiveData.postValue(response);
                                    }
                                });

                    }
                }
            });
        }
    }

    public ArrayList<TransactionItem> getTransactions() {
        return transactions;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isApiRequested() {
        return isApiRequested;
    }

    public void setApiRequested(boolean apiRequested) {
        isApiRequested = apiRequested;
    }
}
