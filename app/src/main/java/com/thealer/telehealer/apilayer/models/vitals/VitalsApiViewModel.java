package com.thealer.telehealer.apilayer.models.vitals;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsApiViewModel extends BaseApiViewModel {

    public VitalsApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserVitals(String type, String user_guid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserVitals(type, user_guid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<VitalsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<VitalsApiResponseModel> o) {

                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(o);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }

    public void getUserFilteredVitals(String type, String user_guid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserFilteredVitals(type, user_guid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<VitalsApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(ArrayList<VitalsApiResponseModel> o) {

                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(o);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }

    public void getVitals(String type, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getVitals(type)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<VitalsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<VitalsApiResponseModel> o) {

                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(o);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);

                                }
                            });
                }
            }
        });
    }

    public void createVital(CreateVitalApiRequestModel createVitalApiRequestModel){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status){
                    getAuthApiService().createVital(createVitalApiRequestModel)
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
