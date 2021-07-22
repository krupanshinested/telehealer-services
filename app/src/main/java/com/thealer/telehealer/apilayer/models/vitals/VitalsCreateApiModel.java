package com.thealer.telehealer.apilayer.models.vitals;

import android.app.Application;
import androidx.annotation.NonNull;
import android.util.Log;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.Map;

public class VitalsCreateApiModel extends BaseApiViewModel {

    public VitalsCreateApiModel(@NonNull Application application) {
        super(application);
    }

    public void createVital(String userGuid,CreateVitalApiRequestModel createVitalApiRequestModel, String doctorGuid){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                Log.v("VitalsCreateApiModel","status "+status);
                if (status){
                    Map<String, String> headers = new HashMap<>();
                    headers.put(ArgumentKeys.USER_GUID,userGuid);
                    headers.put(ArgumentKeys.MODULE_CODE,ArgumentKeys.ADD_VITALS_CODE);
                    getAuthApiService().createVital(headers,createVitalApiRequestModel, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    Log.v("VitalsCreateApiModel","onSuccess");
                                    EventRecorder.recordVitalsPushed();
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
