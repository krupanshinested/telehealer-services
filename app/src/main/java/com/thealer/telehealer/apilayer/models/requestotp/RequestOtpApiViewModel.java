package com.thealer.telehealer.apilayer.models.requestotp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

/**
 * Created by Aswin on 18,October,2018
 */
public class RequestOtpApiViewModel extends BaseApiViewModel {
    private String guid;
    private HashMap<String, Object> params;

    public RequestOtpApiViewModel(@NonNull Application application) {
        super(application);
        guid = TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_GUID);
    }

    public void requestOtpUsingGuid() {
        EventRecorder.recordRegistration("SEND_OTP", guid);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    EventRecorder.recordRegistration("OTP_SENT", guid);

                    params = new HashMap<>();
                    params.put(PreferenceConstants.USER_GUID, guid);

                    getPublicApiService().requestOtp(params).compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void requestOtpUsingEmail(String email) {
        EventRecorder.recordRegistration("SEND_OTP", UserDetailPreferenceManager.getUser_guid());

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    params = new HashMap<>();
                    params.put("email", email);

                    EventRecorder.recordRegistration("OTP_SENT", UserDetailPreferenceManager.getUser_guid());

                    getPublicApiService().requestOtp(params).compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void validateOtpUsingGuid(String otp) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    params = new HashMap<>();
                    params.put(PreferenceConstants.USER_GUID, guid);
                    params.put("otp", otp);

                    getPublicApiService().validateOtp(params)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

}
