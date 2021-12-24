package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 15,July,2019
 */
public class NewDeviceApiViewModel extends BaseApiViewModel {
    public NewDeviceApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAccessLog(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getAccessLogs(page, Constants.PAGINATION_SIZE, "GET")
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
