package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

public class NewDeviceSetApiViewModel extends BaseApiViewModel {
    public NewDeviceSetApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDevice(String healthCareId, String deviceId) {
        fetchToken(status -> getAuthApiService().setDeviceStore(healthCareId, deviceId)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                }));
    }
}
