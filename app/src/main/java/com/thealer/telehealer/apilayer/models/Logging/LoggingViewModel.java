package com.thealer.telehealer.apilayer.models.Logging;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

/**
 * Created by rsekar on 1/21/19.
 */

public class LoggingViewModel extends BaseApiViewModel {

    public LoggingViewModel(@NonNull Application application) {
        super(application);
    }

    public void postCapability(HashMap<String,Object> payload) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postCapabilityLog(payload)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel tokenFetchModel) {


                                }
                            });
                }
            }
        });
    }

    public void postExternalApi(HashMap<String,Object> payload) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postExternalApiLog(payload)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel tokenFetchModel) {

                                }
                            });
                }
            }
        });
    }
}