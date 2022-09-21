package com.thealer.telehealer.apilayer.models.notification;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
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

    public void getNotifications(@Nullable String search, int page, boolean isShowProgress, String associationGuids, String selectedFilterTypes) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNotifications(search, true, page, Constants.PAGINATION_SIZE, associationGuids, selectedFilterTypes,
                            UserType.isUserDoctor()
                                    || UserType.isUserAssistant())
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

    public void updateNotification(Activity activity, String type, boolean isAccept, String toGuid, @NonNull int id, @NonNull String requestStatus, @Nullable String startDate, @Nullable String endDate,
                                   @Nullable String doctorGuid, boolean isShowProgress, boolean isRequestorMA, Button acceptreject, Boolean shownotification) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("status", requestStatus);

                    if (startDate != null) {
                        body.put("start", startDate);
                    }
                    if (endDate != null) {
                        body.put("end", endDate);
                    }

                    getAuthApiService().updateNotification(id, doctorGuid, body)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    if (acceptreject != null) {
                                        acceptreject.setEnabled(true);
                                    }
                                    switch (type) {
                                        case NewNotificationListAdapter.REQUEST_TYPE_APPOINTMENT:
                                            if (isAccept) {
                                                EventRecorder.recordNotification("APPOINTMENT_ACCEPTED");
                                            } else {
                                                EventRecorder.recordNotification("APPOINTMENT_REJECTED");
                                            }
                                            break;
                                        case NewNotificationListAdapter.REQUEST_TYPE_CONNECTION:
                                            if (isAccept) {
                                                EventRecorder.recordNotification("CONNECTION_ACCEPTED");
                                                EventRecorder.recordConnection("CONNECTION_ACCEPTED");

                                                if (UserType.isUserDoctor() && isRequestorMA) {
                                                    EventRecorder.recordConnection("MA_CONNECTION_ACCEPTED");
                                                }

                                            } else {
                                                EventRecorder.recordNotification("CONNECTION_REJECTED");
                                                EventRecorder.recordConnection("CONNECTION_REJECTED");
                                            }
                                            break;
                                    }

                                   if (UserDetailPreferenceManager.getWhoAmIResponse().getRole().equals(Constants.ROLE_ASSISTANT)) {
                                       try {
                                           if (shownotification) {
                                               if (baseApiResponseModel.getMessage() != null && !baseApiResponseModel.getMessage().equals("null")) {
                                                   Utils.showAlertDialog(activity, activity.getString(R.string.app_name), baseApiResponseModel.getMessage(),
                                                           null, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   dialog.dismiss();
                                                               }
                                                           }, new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   dialog.dismiss();
                                                               }
                                                           });
                                               }
                                           }
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    if (acceptreject != null) {
                                        acceptreject.setEnabled(true);
                                    }
                                }
                            });
                }
            }
        });
    }
}
