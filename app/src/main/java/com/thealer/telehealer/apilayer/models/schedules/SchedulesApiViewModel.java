package com.thealer.telehealer.apilayer.models.schedules;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

/**
 * Created by Aswin on 18,December,2018
 */
public class SchedulesApiViewModel extends BaseApiViewModel {
    public SchedulesApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getSchedule(int page, boolean isShowProgress, String doctorGuidList) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getSchedules(true, page, Constants.PAGINATION_SIZE, doctorGuidList)
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

    public void createSchedule(String doctorGuid, String toGuid, SchedulesCreateRequestModel createRequestModel, boolean isShowBoolean) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().createSchedules(doctorGuid, createRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowBoolean)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getNewSchedulePayload(toGuid), null);
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void deleteSchedule(int scheduleId, String time, String user_guid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().deleteSchedule(scheduleId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getScheduleCancelPayload(user_guid, time), null);
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
