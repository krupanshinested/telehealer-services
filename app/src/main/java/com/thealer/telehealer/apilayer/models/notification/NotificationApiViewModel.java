package com.thealer.telehealer.apilayer.models.notification;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.notification.NewNotificationListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationApiViewModel extends BaseApiViewModel {
    public NotificationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateNotificationStatus() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> body = new HashMap<>();
                    body.put("set_read_status", "true");
                    getAuthApiService().setNotificationsRead(body)
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

    public void getNotifications(int page, boolean isShowProgress, String associationGuids) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNotifications(true, page, Constants.PAGINATION_SIZE, associationGuids)
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

    public void updateNotification(String type, boolean isAccept, String toGuid, @NonNull int id, @NonNull String requestStatus, @Nullable String startDate, @Nullable String endDate,
                                   @Nullable String doctorGuid, boolean isShowProgress,boolean isRequestorMA) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("status", requestStatus);

                    if (startDate != null){
                        body.put("start", startDate);
                    }
                    if (endDate != null){
                        body.put("end", endDate);
                    }

                    getAuthApiService().updateNotification(id, doctorGuid, body)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    switch (type){
                                        case NewNotificationListAdapter.REQUEST_TYPE_APPOINTMENT:
                                            if (isAccept){
                                                EventRecorder.recordNotification("APPOINTMENT_ACCEPTED");
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getScheduleAcceptPayload(toGuid, startDate), null);
                                            }else {
                                                EventRecorder.recordNotification("APPOINTMENT_REJECTED");
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getScheduleRejectPayload(toGuid, startDate), null);
                                            }
                                            break;
                                        case NewNotificationListAdapter.REQUEST_TYPE_CONNECTION:
                                            if (isAccept){
                                                EventRecorder.recordNotification("CONNECTION_ACCEPTED");
                                                EventRecorder.recordConnection("CONNECTION_ACCEPTED");

                                                if (UserType.isUserDoctor() && isRequestorMA) {
                                                    EventRecorder.recordConnection("MA_CONNECTION_ACCEPTED");
                                                }

                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getConnectionAcceptPayload(toGuid), null);
                                            }else {
                                                EventRecorder.recordNotification("CONNECTION_REJECTED");
                                                EventRecorder.recordConnection("CONNECTION_REJECTED");
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getConnectionRejectPayload(toGuid), null);
                                            }
                                            break;
                                    }
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
