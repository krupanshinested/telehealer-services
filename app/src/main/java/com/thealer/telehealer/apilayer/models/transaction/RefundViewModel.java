package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.transaction.req.RefundReq;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;

import java.util.HashMap;
import java.util.Map;

public class RefundViewModel extends BaseApiViewModel {
    public RefundViewModel(@NonNull Application application) {
        super(application);
    }

    public void processRefund(String userGuid,int id, RefundReq req) {
        fetchToken(status -> {
            if (status) {
                Map<String, String> headers = new HashMap<>();
                if(userGuid != null && !userGuid.isEmpty()) {
                    headers.put(ArgumentKeys.USER_GUID, userGuid);
                    headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.MANAGE_REFUND_CODE);
                }
                getAuthApiService().processRefund(headers,id, req)
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
