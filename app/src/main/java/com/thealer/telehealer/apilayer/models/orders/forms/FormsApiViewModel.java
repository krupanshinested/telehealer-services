package com.thealer.telehealer.apilayer.models.orders.forms;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Aswin on 07,May,2019
 */
public class FormsApiViewModel extends BaseApiViewModel {
    public FormsApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateForm(int userFormId, DynamicFormDataBean dynamicFormDataBean, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new Gson().toJson(dynamicFormDataBean));

                    getAuthApiService().updateForm(userFormId, requestBody)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
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
