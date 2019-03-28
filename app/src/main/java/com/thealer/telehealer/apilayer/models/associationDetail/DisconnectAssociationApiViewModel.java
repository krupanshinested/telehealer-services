package com.thealer.telehealer.apilayer.models.associationDetail;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 20,November,2018
 */
public class DisconnectAssociationApiViewModel extends BaseApiViewModel {

    public DisconnectAssociationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void disconnectUser(String user_guid, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().disconnectUser(user_guid, doctorGuid)
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
