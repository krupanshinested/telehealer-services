package com.thealer.telehealer.apilayer.models.diet;

import android.app.Application;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.PDFUrlResponse;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Aswin on 20,February,2019
 */
public class DietApiViewModel extends BaseApiViewModel {
    public DietApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserDietDetails(String date, String userGuid, String doctorGuid, List<Integer> dietIdList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                String ids = null;
                if (dietIdList != null) {
                    ids = dietIdList.toString().replace("[", "").replace("]", "");
                }
                if (status) {
                    getAuthApiService().getDietDetails(date, userGuid, doctorGuid, ids)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<DietApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<DietApiResponseModel> data) {
                                    baseApiArrayListMutableLiveData.setValue(new ArrayList<>(data));
                                }
                            });
                }
            }
        });
    }

    public void getUserDietDetails(String filter, String startDate, String endDate, String userGuid, String doctorGuid, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getDietDetails(filter, startDate, endDate, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<DietApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(ArrayList<DietApiResponseModel> data) {
                                    baseApiArrayListMutableLiveData.setValue(new ArrayList<>(data));
                                }
                            });
                }
            }
        });
    }


    public void getDietPdf(String filter, String startDate, String endDate, String userGuid, String doctorGuid, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getDietPDF(filter, startDate, endDate, userGuid, doctorGuid,true)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<PDFUrlResponse>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(PDFUrlResponse data) {
                                    baseApiResponseModelMutableLiveData.setValue(data);
                                }
                            });
                }
            }
        });
    }


    public void getDietUserList(String filter, String startDate, String endDate, String userGuid, String doctorGuid, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getDietUserFilter(filter, startDate, endDate, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<DietUserListApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(DietUserListApiResponseModel dietUserListApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(dietUserListApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void addDiet(AddDietRequestModel addDietRequestModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    MultipartBody.Part file = null;
                    if (addDietRequestModel.getUploadUrl() != null) {
                        file = getMultipartFile("image", addDietRequestModel.getUploadUrl());
                    }

                    RequestBody mealType = FormBody.create(MediaType.parse("text/plain"), addDietRequestModel.getMeal_type().toLowerCase());
                    RequestBody serving = FormBody.create(MediaType.parse("text/plain"), addDietRequestModel.getServing());
                    RequestBody date = FormBody.create(MediaType.parse("text/plain"), addDietRequestModel.getDate());
                    RequestBody servingUnit = FormBody.create(MediaType.parse("text/plain"), addDietRequestModel.getServing_unit());
                    RequestBody foodBean = FormBody.create(MediaType.parse("text/plain"), new Gson().toJson(addDietRequestModel.getFoodBean()));

                    Map<String, RequestBody> requestBodyMap = new HashMap<>();

                    requestBodyMap.put("meal_type", mealType);
                    requestBodyMap.put("serving", serving);
                    requestBodyMap.put("date", date);
                    requestBodyMap.put("food", foodBean);
                    requestBodyMap.put("serving_unit", servingUnit);


                    getAuthApiService()
                            .addDiet(requestBodyMap, file)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                    EventRecorder.recordMeals(addDietRequestModel.getFoodBean().getName(), addDietRequestModel.getMeal_type());
                                }
                            });
                }
            }
        });
    }

}
