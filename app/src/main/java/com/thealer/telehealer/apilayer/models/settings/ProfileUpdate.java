package com.thealer.telehealer.apilayer.models.settings;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.medicalHistory.UpdateQuestionaryBodyModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by rsekar on 11/22/18.
 */

public class ProfileUpdate extends BaseApiViewModel {

    public ProfileUpdate(@NonNull Application application) {
        super(application);
    }

    public void updateAppointmentSlot(String slot) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    HashMap<String, String> key = new HashMap<>();
                    key.put("appt_length", slot);

                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(key));

                    getAuthApiService().updateUserDetail(body)
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

    public void updateUserDetail(WhoAmIApiResponseModel whoAmIApiResponseModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().updateUserDetail(whoAmIApiResponseModel)
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

    public void updateSecureMessage(boolean isSecureMessaging, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Boolean> secureMap = new HashMap<>();
                    secureMap.put("secure_message", isSecureMessaging);

                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(secureMap));

                    getAuthApiService().updateUserDetail(body)
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

    public void updatePatientCreditCard(boolean isPatientCreditCard, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Boolean> secureMap = new HashMap<>();
                    secureMap.put("patient_credit_card_required", isPatientCreditCard);

                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(secureMap));

                    getAuthApiService().updateUserDetail(body)
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

    public void updateUserQuestionary(UpdateQuestionaryBodyModel questionaryBodyModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    RequestBody requestBody = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(questionaryBodyModel.getQuestionnaire()));

                    getAuthApiService()
                            .updateUserQuestionnaire(requestBody)
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


    public void updateUserHistory(List<HistoryBean> historyBeanList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> history = new HashMap<>();
                    history.put("history", new Gson().toJson(historyBeanList));

                    RequestBody requestBody = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(historyBeanList));

                    getAuthApiService()
                            .updateUserHistory(requestBody)
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


    public void updateConnectionRequest(boolean request) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    HashMap<String, Boolean> key = new HashMap<>();
                    key.put("connection_requests", request);

                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(key));

                    getAuthApiService().updateUserDetail(body)
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

    public void updateAppointmentRequest(boolean request) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    HashMap<String, Boolean> key = new HashMap<>();
                    key.put("appt_requests", request);

                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(key));

                    getAuthApiService().updateUserDetail(body)
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

    public void updateAvailableTime(String appt_start_time, String appt_end_time) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, String> available_time = new HashMap<>();
                    available_time.put("appt_start_time", appt_start_time);
                    available_time.put("appt_end_time", appt_end_time);
                    RequestBody body = FormBody.create(MediaType.parse("application/form-data"), new Gson().toJson(available_time));

                    getAuthApiService().updateUserDetail(body)
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
