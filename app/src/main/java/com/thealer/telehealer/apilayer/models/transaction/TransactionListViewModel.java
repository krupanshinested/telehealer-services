package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.Payments.TransactionResponse;
import com.thealer.telehealer.apilayer.models.transaction.req.TransactionListReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.RefundItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionListResp;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TransactionListViewModel extends BaseApiViewModel {

    private ArrayList<TransactionItem> transactions = new ArrayList<>();
    private TransactionListReq filterReq;

    private int page = 1;
    private int totalCount;
    private boolean isApiRequested;

    public TransactionListViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadTransactions(boolean isShowProgress) {
        if (filterReq == null) {
            filterReq.setFilter(new TransactionListReq.Filter());
            filterReq.getFilter().setToDate(Utils.getUTCDateFromCalendar(Calendar.getInstance()));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            filterReq.getFilter().setFromDate(Utils.getUTCDateFromCalendar(calendar));
        }
        if (!isApiRequested) {
            isApiRequested = true;
            fetchToken(status -> {
                if (status) {
                    getAuthApiService().transactionPaginate(true, page, 5, filterReq)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<TransactionListResp>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(TransactionListResp response) {
                                    isApiRequested = false;
                                    if (page == 1) {
                                        transactions.clear();
                                    }
                                    for (TransactionItem item : response.getResult()) {
                                        if (item.getRefunds() != null && item.getRefunds().size() > 0) {
                                            double totalRefund = 0;
                                            for (RefundItem refund :
                                                    item.getRefunds()) {
                                                totalRefund += refund.getAmount();
                                            }
                                            item.setTotalRefund(totalRefund);
                                        }
                                    }
                                    transactions.addAll(response.getResult());
                                    totalCount = response.getCount();
                                    baseApiResponseModelMutableLiveData.postValue(response);
                                }
                            });

                }
            });
        }
    }

    public void processPayment(int id, int paymentMode) {

        HashMap<String, Object> req = new HashMap<>();
        req.put("payment_mode", paymentMode);

        fetchToken(status -> {
            if (status) {
                getAuthApiService().processPayment(id, req)
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                            @Override
                            public void onSuccess(BaseApiResponseModel response) {
                                baseApiResponseModelMutableLiveData.postValue(response);
                            }
                        });

            }
        });
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

    public void setFilterReq(TransactionListReq filterReq) {
        this.filterReq = filterReq;
    }

    public TransactionListReq getFilterReq() {
        return filterReq;
    }
}
