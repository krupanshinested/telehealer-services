package com.thealer.telehealer.apilayer.models.notification;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.notification.NotificationListAdapter;

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

    public void getNotifications(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNotifications(true, page, Constants.PAGINATION_SIZE)
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
                                   @Nullable String doctorGuid, boolean isShowProgress) {
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
                                        case NotificationListAdapter.REQUEST_TYPE_APPOINTMENT:
                                            if (isAccept){
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getScheduleAcceptPayload(toGuid, startDate), null);
                                            }else {
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getScheduleRejectPayload(toGuid, startDate), null);
                                            }
                                            break;
                                        case NotificationListAdapter.REQUEST_TYPE_CONNECTION:
                                            if (isAccept){
                                                PubnubUtil.shared.publishPushMessage(PubNubNotificationPayload.getConnectionAcceptPayload(toGuid), null);
                                            }else {
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
