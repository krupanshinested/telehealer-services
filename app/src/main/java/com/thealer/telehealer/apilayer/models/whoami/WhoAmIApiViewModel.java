package com.thealer.telehealer.apilayer.models.whoami;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 17,December,2018
 */
public class WhoAmIApiViewModel extends BaseApiViewModel {

    public WhoAmIApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkWhoAmI() {
        checkWhoAmI(null);
    }

    public void checkWhoAmI(@Nullable String doctorGuId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().whoAmI(doctorGuId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

                }
            }
        });
    }


    public void assignWhoAmI() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().whoAmI(null)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);

                                    if (baseApiResponseModel instanceof WhoAmIApiResponseModel) {
                                        UserDetailPreferenceManager.insertUserDetail((WhoAmIApiResponseModel) baseApiResponseModel);
                                    }

                                }
                            });
                }
            }
        });
    }

}
