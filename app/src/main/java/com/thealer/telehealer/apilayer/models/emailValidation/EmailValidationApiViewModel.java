package com.thealer.telehealer.apilayer.models.emailValidation;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 18,January,2019
 */
public class EmailValidationApiViewModel extends BaseApiViewModel {
    public EmailValidationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void requestVerificationMain(boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().requestVerificationMain()
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
