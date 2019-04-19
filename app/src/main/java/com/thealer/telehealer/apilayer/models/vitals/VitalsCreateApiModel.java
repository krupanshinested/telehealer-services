package com.thealer.telehealer.apilayer.models.vitals;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

public class VitalsCreateApiModel extends BaseApiViewModel {

    public VitalsCreateApiModel(@NonNull Application application) {
        super(application);
    }

    public void createVital(CreateVitalApiRequestModel createVitalApiRequestModel, String doctorGuid){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                Log.v("VitalsCreateApiModel","status "+status);
                if (status){
                    getAuthApiService().createVital(createVitalApiRequestModel, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    Log.v("VitalsCreateApiModel","onSuccess");
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
