package com.thealer.telehealer.apilayer.models.CheckUserEmailMobileModel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 08,October,2018
 */
public class CheckUserEmailMobileApiViewModel extends BaseApiViewModel {

    public CheckUserEmailMobileApiViewModel(@NonNull Application application) {
        super(application);

    }

    /**
     * Method to make check user api call
     *
     * @param email is the api param
     */
    public void checkUserEmail(String email) {
        String app_type;

        if (TeleHealerApplication.appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_PATIENT) {
            app_type = Constants.BUILD_PATIENT;
        } else {
            app_type = Constants.BUILD_MEDICAL;
        }

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean isTokenAvailable) {
                if (isTokenAvailable) {
                    getPublicApiService().checkUserEmail(email, app_type)
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

    /**
     * Method to make check user api call
     *
     * @param mobile is the api param
     */
    public void checkUserMobile(String mobile) {

        String app_type;

        if (TeleHealerApplication.appPreference.getInt(Constants.USER_TYPE) == Constants.TYPE_PATIENT) {
            app_type = Constants.BUILD_PATIENT;
        } else {
            app_type = Constants.BUILD_MEDICAL;
        }

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean isTokenAvailable) {
                if (isTokenAvailable) {
                    getPublicApiService().checkUserMobile(mobile, app_type)
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
}
