package com.thealer.telehealer.apilayer.models.settings;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.UpdateQuestionaryBodyModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by rsekar on 11/22/18.
 */

public class AppointmentSlotUpdate extends BaseApiViewModel {

    public AppointmentSlotUpdate(@NonNull Application application) {
        super(application);
    }

    public void updateAppointmentSlot(String slot) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    HashMap<String, Object> value = new HashMap<>();
                    HashMap<String, String> key = new HashMap<>();
                    key.put("appt_length", slot);
                    value.put("user_data", key);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), value.toString());

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addPart(body)
                            .build();

                    getAuthApiService().updateUserDetail(requestBody)
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

                    Log.e("aswin", "onStatus: " + new Gson().toJson(historyBeanList));
                    Log.e("aswin", "onStatus: " + history.toString());

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
}
