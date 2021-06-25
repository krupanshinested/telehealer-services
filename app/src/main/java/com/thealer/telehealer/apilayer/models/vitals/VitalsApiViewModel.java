package com.thealer.telehealer.apilayer.models.vitals;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.PDFUrlResponse;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    public void getUserFilteredVitals(String filter, String startDate, String endDate, String user_guid, String doctorGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserFilteredVitals(filter, startDate, endDate, user_guid, doctorGuid, true, page, Constants.PAGINATION_SIZE)
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

    public void getVitalPdf(String type,String filter, String startDate, String endDate, String user_guid, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getVitalPDF(type,filter, startDate, endDate, user_guid, doctorGuid, true)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<PDFUrlResponse>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(PDFUrlResponse baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }


    public void getUserFilteredVitals(String type,String filter, String startDate, String endDate, String user_guid, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserFilteredVitals(type, filter, startDate, endDate, user_guid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<VitalsApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<VitalsApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> baseApiResponseModelArrayList = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(baseApiResponseModelArrayList);
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

    public void sentBulkVitals(CreateVitalApiRequestModel createVitalApiRequestModel,
                               String doctorGuid, SuccessViewInterface successViewInterface) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    Gson gson = new Gson();
                    String vitals = gson.toJson(createVitalApiRequestModel);

                    MultipartBody.Part file = MultipartBody.Part.createFormData("request_input", "request_input.txt",
                            RequestBody.create(MediaType.parse("text/plain"),vitals.getBytes()));

                    getAuthApiService().createBulkVital(file, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    successViewInterface.onSuccessViewCompletion(true);
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

    public void getVitalThreshold(boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getVitalsThreshold()
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
