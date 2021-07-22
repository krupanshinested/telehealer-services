package com.thealer.telehealer.apilayer.models.schedules;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.ResultFetcher;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesApiViewModel extends BaseApiViewModel {
    public SchedulesApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getSchedule(@Nullable String search, int page, boolean isShowProgress, String doctorGuidList) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getSchedules(search,true, page, Constants.PAGINATION_SIZE, doctorGuidList)
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

    public void getUserSchedules(String user_guid, String doctorGuid, boolean isUpcoming, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserUpcomingSchedules(user_guid, isUpcoming, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<SchedulesApiResponseModel.ResultBean>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<SchedulesApiResponseModel.ResultBean> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void getUserSchedules(String user_guid, String doctorGuid, String day, String month, String year,
                                 boolean isUpcoming, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserUpcomingSchedules(user_guid, isUpcoming, doctorGuid, day, month, year)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<SchedulesApiResponseModel.ResultBean>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<SchedulesApiResponseModel.ResultBean> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void createSchedule(String userGuid,String doctorGuid, String toGuid, SchedulesCreateRequestModel createRequestModel, boolean isShowBoolean) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    Map<String, String> headers = new HashMap<>();
                    if(userGuid != null && !userGuid.isEmpty()) {
                        headers.put(ArgumentKeys.USER_GUID, userGuid);
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.SCHEDULING_CODE);
                    }
                    getAuthApiService().createSchedules(headers,doctorGuid, createRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowBoolean)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                    EventRecorder.recordNotification("NEW_APPOINTMENT");

                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void deleteSchedule(int scheduleId, String time, String userGuid, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(userGuid == null && !userGuid.isEmpty()){
                        headers.put(ArgumentKeys.USER_GUID, userGuid);
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.SCHEDULING_CODE);
                    }
                    getAuthApiService().deleteSchedule(headers,scheduleId, doctorGuid)
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

    public void getScheduleDetail(int scheduleId, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getScheduleDetail(scheduleId, doctorGuid)
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

    public void getScheduleDetail(int scheduleId, String doctorGuid, ResultFetcher resultFetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getScheduleDetail(scheduleId, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                    resultFetcher.didFetched(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
