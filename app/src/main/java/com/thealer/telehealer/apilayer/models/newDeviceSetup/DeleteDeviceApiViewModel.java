package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

/**
 * Created by Aswin on 15,July,2019
 */
public class DeleteDeviceApiViewModel extends BaseApiViewModel {
    public DeleteDeviceApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void deleteDevice(String id) {
        fetchToken(status -> getAuthApiService().deleteDevice(id)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                }));
    }
}
