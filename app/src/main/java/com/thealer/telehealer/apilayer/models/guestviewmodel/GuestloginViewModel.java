package com.thealer.telehealer.apilayer.models.guestviewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;

import java.util.HashMap;

public class GuestloginViewModel extends BaseApiViewModel {

    public GuestloginViewModel(@NonNull Application application) {
        super(application);
    }

    public void guestLogin(String email,String phone,String userName,String code) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("phone", phone);
        params.put("name", userName);
        params.put("code", code);

        getAuthApiService().guestLogin(params)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });
    }

    public void registerUserEnterWatingRoom(String doctorUserGuuid) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("doctor_user_guid", doctorUserGuuid);

        getAuthApiService().registerUserEnterWaitingRoom(params)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });
    }
}
