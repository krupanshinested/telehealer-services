package com.thealer.telehealer.apilayer.models.orders;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.forms.CreateFormRequestModel;
import com.thealer.telehealer.apilayer.models.orders.lab.CreateTestApiRequestModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.CreateMiscellaneousRequestModel;
import com.thealer.telehealer.apilayer.models.orders.pharmacy.SendFaxRequestModel;
import com.thealer.telehealer.apilayer.models.orders.prescription.CreatePrescriptionRequestModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.CreateRadiologyRequestModel;
import com.thealer.telehealer.apilayer.models.orders.specialist.AssignSpecialistRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.Map;

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

    public void createForm(String userGuid,CreateFormRequestModel createFormRequestModel, String doctorGuid, boolean isProgressVisibile) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.FORMS_CODE);
                    }
                    getAuthApiService().createForms(headers,createFormRequestModel, doctorGuid)
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

    public void assignSpecialist(String userGuid,boolean sync_create,AssignSpecialistRequestModel assignSpecialistRequestModel, String doctorGuid, boolean isProgressVisibile) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.REFERRALS_CODE);
                    }
                    getAuthApiService().assignSpecialist(headers,sync_create,assignSpecialistRequestModel, doctorGuid)
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


    public void uploadDocument(@Nullable String userGuid, String doctorGuid, String name, String image_path, String vistOrderId, boolean isProgressVisible) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    MultipartBody.Part file = getMultipartFile("file", image_path);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), name);

                    RequestBody orderId = null;
                    if (vistOrderId != null) {
                        orderId = RequestBody.create(MediaType.parse("multipart/form-data"), vistOrderId);
                    }
                    getAuthApiService()
                            .uploadDocument(requestBody, orderId, file, userGuid, doctorGuid)
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

    public void createPrescription(String user_guid,boolean sync_create,CreatePrescriptionRequestModel createPrescriptionRequestModel, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.PRESCRIPTION_CODE);
                    }
                    getAuthApiService().createPrescription(headers,sync_create,createPrescriptionRequestModel, doctorGuid)
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

    public void sendFax(SendFaxRequestModel sendFaxRequestModel, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().sendFax(sendFaxRequestModel, doctorGuid)
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

    public void createLabOrder(String userGuid,boolean sync_create,CreateTestApiRequestModel createTestApiRequestModel, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.LABS_CODE);
                    }
                    getAuthApiService().createLabOrder(headers,sync_create,createTestApiRequestModel, doctorGuid)
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

    public void createRadiologyOrder(String userGuid,boolean sync_create, CreateRadiologyRequestModel createRadiologyRequestModel, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.RADIOLOGY_CODE);
                    }

                    getAuthApiService().createRadiology(headers,sync_create,createRadiologyRequestModel, doctorGuid)
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

    public void createMiscellaneousOrder(String userGuid,CreateMiscellaneousRequestModel createMiscellaneousRequestModel, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.MEDICAL_DOCUMENTS_CODE);
                    }
                    getAuthApiService().createMiscellaneous(headers,createMiscellaneousRequestModel, doctorGuid)
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
