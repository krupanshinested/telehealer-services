package com.thealer.telehealer.apilayer.models.orders.lab;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 23,November,2018
 */
public class IcdCodeApiViewModel extends BaseApiViewModel {
    public IcdCodeApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getFilteredIcdCodes(String codes, boolean showProgress){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status){
                    getAuthApiService().getFilteredIcdCodes(codes)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getAllIcdCodes(int key, @Nullable String searchKey, @NonNull boolean showProgress){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status){
                    getAuthApiService().getAllIcdCodes(key, searchKey)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
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
