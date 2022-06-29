package com.thealer.telehealer.apilayer.models.Payments;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

/**
 * Created by rsekar on 1/22/19.
 */

public class TransactionApiViewModel extends BaseApiViewModel {

    public TransactionApiViewModel(@NonNull Application application) {
        super(application);
    }


    public void getTransactions() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getTransactions(true, 1, Constants.PAGINATION_SIZE)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<TransactionResponse>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(TransactionResponse transactionResponse) {
                                    baseApiResponseModelMutableLiveData.setValue(transactionResponse);
                                }
                            });
                }
            }
        });
    }

    public void getInvoice(String start, String end,int page) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getInvoice(true, page, 8,start,end)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<TransactionResponse>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(TransactionResponse transactionResponse) {
                                    baseApiResponseModelMutableLiveData.setValue(transactionResponse);
                                }
                            });
                }
            }
        });
    }


    public void getVitalVisit(String forMonth) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getVitalVisit(forMonth)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<VitalVisitResponse>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(VitalVisitResponse vitalVisitResponse) {
                                    baseApiResponseModelMutableLiveData.setValue(vitalVisitResponse);
                                }
                            });
                }
            }
        });
    }

    public void getCallLogs(String forMonth) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getCallLogs(true)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<RecentsApiResponseModel.ResultBean>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(ArrayList<RecentsApiResponseModel.ResultBean> data) {
                                    baseApiArrayListMutableLiveData.setValue(new ArrayList<>(data));
                                }
                            });

                }

            }
        });
    }

}
