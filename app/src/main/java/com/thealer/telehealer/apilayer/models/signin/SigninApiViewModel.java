package com.thealer.telehealer.apilayer.models.signin;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

/**
 * Created by Aswin on 01,November,2018
 */
public class SigninApiViewModel extends BaseApiViewModel {
    public SigninApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void loginUser(String email, String password) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        getPublicApiService().signinUser(params)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        EventRecorder.recordUserSession("login_username");

                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });
    }

    public void refreshToken() {
        String refreshToken = TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN);
        getAuthApiService().refreshToken(refreshToken)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });
    }
}
