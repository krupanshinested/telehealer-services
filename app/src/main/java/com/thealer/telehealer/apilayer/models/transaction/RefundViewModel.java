package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.transaction.req.RefundReq;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;

import java.util.HashMap;
import java.util.Map;

public class RefundViewModel extends BaseApiViewModel {
    public RefundViewModel(@NonNull Application application) {
        super(application);
    }

    public void processRefund(String doctorGuid,int id, RefundReq req) {
        fetchToken(status -> {
            if (status) {
                getAuthApiService().processRefund(id, req)
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
}
