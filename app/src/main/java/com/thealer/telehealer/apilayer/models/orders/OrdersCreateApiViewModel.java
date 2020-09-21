package com.thealer.telehealer.apilayer.models.orders;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Aswin on 28,November,2018
 */
public class OrdersCreateApiViewModel extends BaseApiViewModel {

    public OrdersCreateApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void createForm(CreateFormRequestModel createFormRequestModel, boolean isProgressVisibile) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().createForms(createFormRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isProgressVisibile)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

                }
            }
        });
    }

    public void assignSpecialist(AssignSpecialistRequestModel assignSpecialistRequestModel, boolean isProgressVisibile) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().assignSpecialist(assignSpecialistRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isProgressVisibile)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }


    public void uploadDocument(String name, String image_path, boolean isProgressVisible) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    MultipartBody.Part file = getMultipartFile("file", image_path);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), name);
                    getAuthApiService()
                            .uploadDocument(requestBody, file)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isProgressVisible)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void createPrescription(CreatePrescriptionRequestModel createPrescriptionRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().createPrescription(createPrescriptionRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void sendFax(SendFaxRequestModel sendFaxRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().sendFax(sendFaxRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void createLabOrder(CreateTestApiRequestModel createTestApiRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().createLabOrder(createTestApiRequestModel).compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void createRadiologyOrder(CreateRadiologyRequestModel createRadiologyRequestModel) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().createRadiology(createRadiologyRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
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
