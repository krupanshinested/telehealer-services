package com.thealer.telehealer.apilayer.models.master;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

public class MasterApiViewModel extends BaseApiViewModel {
    public MasterApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchMasters() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().fetchMasters()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<MasterResp>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(MasterResp baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

                }
            }
        });
    }
}
