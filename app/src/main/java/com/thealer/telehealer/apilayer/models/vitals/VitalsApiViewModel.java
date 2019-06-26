package com.thealer.telehealer.apilayer.models.vitals;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsApiViewModel extends BaseApiViewModel {

    public VitalsApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserVitals(String type, String user_guid, String doctorGuid, boolean isShowProgress, int page) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserVitals(type, user_guid, doctorGuid, true, page, Constants.PAGINATION_SIZE)
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

    public void getUserFilteredVitals(String type, String startDate, String endDate, String user_guid, String doctorGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserFilteredVitals(type, startDate, endDate, user_guid, doctorGuid, true, page, Constants.PAGINATION_SIZE)
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

    public void getVitals(String type, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getVitals(type, true, page, Constants.PAGINATION_SIZE)
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

    public void createVital(CreateVitalApiRequestModel createVitalApiRequestModel, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().createVital(createVitalApiRequestModel, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    EventRecorder.recordVitalsPushed();
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }


    public void getVitalDetail(String userGuid, String doctorGuid, List<Integer> idList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String ids = idList.toString().replace("[", "").replace("]", "");
                    getAuthApiService().getVitalDetail(userGuid, doctorGuid, ids)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<VitalsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<VitalsApiResponseModel> data) {

                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);

                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }
}
