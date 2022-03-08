package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailRequestModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 15,July,2019
 */
public class NewDeviceApiViewModel extends BaseApiViewModel {
    public NewDeviceApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getDevicelist() {
        fetchToken(status -> getPublicApiService().getDeviceList()
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                }));
    }

    public void getMyDevicelist() {
        fetchToken(status -> getAuthApiService().getMyDeviceList()
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                }));
    }

    public void deleteDevice(String device_id) {
        HashMap<String, String> param = new HashMap<>();
        param.put("id", device_id);
        fetchToken(status -> getAuthApiService().deleteDevice(param)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseDeleteApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                }));
    }
}
