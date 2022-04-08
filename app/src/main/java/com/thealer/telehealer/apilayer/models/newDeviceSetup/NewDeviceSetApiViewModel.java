package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.setDevice.SetDeviceResponseModel;

import java.util.ArrayList;
import java.util.HashMap;

public class NewDeviceSetApiViewModel extends BaseApiViewModel {
    public NewDeviceSetApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDevice(HashMap<String, Object> param) {
        fetchToken(status -> {
            getAuthApiService().setDeviceStore(param)
                    .compose(applySchedulers())
                    .subscribe(new RAObserver<SetDeviceResponseModel>(getProgress(true)) {
                        @Override
                        public void onSuccess(SetDeviceResponseModel setDeviceResponseModel) {
                            baseApiSetDeviceResponseModelMutableLiveData.setValue(setDeviceResponseModel);
                        }
                    });
        });
    }
}
