package com.thealer.telehealer.common.pubNub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.CallReceiveInterface;
import com.thealer.telehealer.common.ResultFetcher;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.Call.CallChannel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.call.CallActivity;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.home.chat.ChatActivity;
import com.thealer.telehealer.views.notification.NotificationActivity;
import com.thealer.telehealer.views.notification.NotificationDetailActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thealer.telehealer.common.PreferenceConstants;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;


import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 12/25/18.
 */

public class TelehealerFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = TelehealerFirebaseMessagingService.class.getSimpleName();
    private static String currentToken = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> message = remoteMessage.getData();
        Log.e(TAG, "message " + message.toString());

        ObjectMapper mapper = new ObjectMapper();

        try {

            Map<String, Object> mapFromString = new HashMap<>();
            mapFromString = mapper.readValue(message.get("body"), new TypeReference<Map<String, Object>>() {
            });
            mapFromString.remove("pn_push");

            APNSPayload data = mapper.readValue(mapper.writeValueAsString(mapFromString), new TypeReference<APNSPayload>() {
            });
            Log.e(TAG, "message " + data.getTo());
            extractMessage(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.e("MessagingService", "new token " + token);
        assignToken(token);
    }

    public static void assignToken(String currentToken) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                TelehealerFirebaseMessagingService.currentToken = currentToken;
                Log.d("MessagingService","assignToken");
                if (UserDetailPreferenceManager.getUser_guid() != null && !UserDetailPreferenceManager.getUser_guid().isEmpty() && appPreference.getBoolean(PreferenceConstants.IS_USER_LOGGED_IN)) {
                    PubnubUtil.shared.grantPubNub(currentToken, UserDetailPreferenceManager.getUser_guid());
                    Log.d("MessagingService","grantPubNub");
                } else {
                    Log.d("MessagingService","not grantPubNub "+UserDetailPreferenceManager.getUser_guid()+" " + appPreference.getBoolean(PreferenceConstants.IS_USER_LOGGED_IN));
                }
            }
        });
    }

    public static void refresh() {
        assignToken(currentToken);
    }


    private void extractMessage(APNSPayload data) {
        Intent intent;
        switch (data.getType()) {
            case APNSPayload.audio:
            case APNSPayload.video:
                EventRecorder.recordNotification("call_received");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        displayIncomingCall(data);
                    }
                });
                break;
           case APNSPayload.text:
                break;
            case APNSPayload.message:
                intent = new Intent(this, ChatActivity.class);
                Bundle chatBundle = new Bundle();
                chatBundle.putString(ArgumentKeys.USER_GUID, data.getFrom());
                intent.putExtras(chatBundle);
                Utils.createNotification(data, intent);

                if (UserType.isUserPatient()) {
                    Intent messageIntent = new Intent(Constants.message_broadcast);
                    messageIntent.putExtras(chatBundle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                }
                sendNewNotificationBroadCast();

                break;
            case APNSPayload.callHistory:
                break;
            case APNSPayload.callProposerBanner:
                break;
            case APNSPayload.missedCall:
                break;
            case APNSPayload.openApp:
                break;
            case APNSPayload.response:
                break;
            case APNSPayload.endCall:
                CallChannel.shared.didReceiveMessage(data);
                break;
            case APNSPayload.connection:
            case APNSPayload.schedule:
                intent = new Intent(this, NotificationActivity.class);
                Utils.createNotification(data, intent);
                sendNewNotificationBroadCast();
                break;
            case APNSPayload.vitals:
                intent = new Intent(this, NotificationDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putBoolean(ArgumentKeys.VIEW_ABNORMAL_VITAL, true);
                bundle.putString(ArgumentKeys.MEASUREMENT_TYPE, data.getVital_type());
                bundle.putString(ArgumentKeys.USER_GUID, data.getFrom());
                bundle.putString(ArgumentKeys.DOCTOR_GUID, data.getDoctor_guid());
                intent.putExtras(bundle);
                Utils.createNotification(data, intent);
                sendNewNotificationBroadCast();
                break;
            case APNSPayload.waitingInRoom:
                try {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            SchedulesApiViewModel schedulesApiViewModel = new SchedulesApiViewModel(getApplication());
                            schedulesApiViewModel.getScheduleDetail(Integer.parseInt(data.getSessionId()), null, new ResultFetcher() {
                                @Override
                                public void didFetched(BaseApiResponseModel baseApiResponseModel) {
                                    Intent contentIntent = new Intent(getApplicationContext(), NotificationDetailActivity.class);
                                    contentIntent.putExtra(ArgumentKeys.SCHEDULE_DETAIL, new Gson().toJson((SchedulesApiResponseModel.ResultBean) baseApiResponseModel));
                                    Utils.createNotification(data, contentIntent);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void sendNewNotificationBroadCast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.NOTIFICATION_COUNT_RECEIVER));
    }

    private Intent getCallIntent(Boolean isWaitingRoom,@Nullable Boolean accept) {
        Intent fullScreenIntent = new Intent(TelehealerFirebaseMessagingService.this, CallActivity.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_ANSWER,isWaitingRoom);

        if (accept != null) {
            if (accept) {
                fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_ANSWER,true);
            } else {
                fullScreenIntent.putExtra(ArgumentKeys.TRIGGER_END,true);
            }
        }

        return fullScreenIntent;
    }

    // Display the incoming call to the use
    private void displayIncomingCall(APNSPayload data) {
        if (!TokBox.shared.isActiveCallPreset()) {

            TokBox.shared.didRecieveIncoming(data, new CallReceiveInterface() {
                @Override
                public void didFetchedAllRequiredData(Boolean isWaitingRoom,String doctorName) {

                    CallActivity.firebaseMessagingService = TelehealerFirebaseMessagingService.this;

                    Intent fullScreenIntent = getCallIntent(isWaitingRoom,null);

                    if (TeleHealerApplication.isInForeGround) {
                        Log.d("MessagingService","start activity directly");
                        startActivity(fullScreenIntent);
                    } else {
                        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(TelehealerFirebaseMessagingService.this, 0,
                                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent acceptScreenIntent = getCallIntent(isWaitingRoom,true);
                        PendingIntent acceptScreenPendingIntent = PendingIntent.getActivity(TelehealerFirebaseMessagingService.this, 1,
                                acceptScreenIntent, PendingIntent.FLAG_ONE_SHOT);

                        Intent rejectScreenIntent = getCallIntent(isWaitingRoom,false);
                        PendingIntent rejectScreenPendingIntent = PendingIntent.getActivity(TelehealerFirebaseMessagingService.this, 2,
                                rejectScreenIntent, PendingIntent.FLAG_ONE_SHOT);

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(TelehealerFirebaseMessagingService.this, TeleHealerApplication.appConfig.getVoipChannel())
                                        .setSmallIcon(R.drawable.app_icon)
                                        .setContentTitle(getString(R.string.app_name)+" Incoming call")
                                        .setContentText(doctorName)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setCategory(NotificationCompat.CATEGORY_CALL)
                                        .setFullScreenIntent(fullScreenPendingIntent, true)
                                        .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon,"Reject",rejectScreenPendingIntent).build())
                                        .addAction(new NotificationCompat.Action.Builder(R.drawable.app_icon,"Accept",acceptScreenPendingIntent).build());

                        Notification incomingCallNotification = notificationBuilder.build();

                        Log.d("MessagingService","start pending indent");
                        TelehealerFirebaseMessagingService.this.startForeground(CallActivity.VOIP_NOTIFICATION_ID, incomingCallNotification);
                    }

                }
            });

        } else {
            CallChannel.shared.postEndCallToOtherPerson(data.getFrom(),data.getUuid(),UserDetailPreferenceManager.getUserDisplayName(),UserDetailPreferenceManager.getUser_avatar(),OpenTokConstants.busyInAnotherLine);
            EventRecorder.recordNotification("BUSY_CALL");

            if (data.getType().equals(OpenTokConstants.video)) {
                Utils.createNotification(data, getRecentIntent());
            } else {
                Utils.createNotification(data, getRecentIntent());
            }
        }
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    @Nullable
    private Intent getRecentIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(ArgumentKeys.SELECTED_MENU_ITEM, R.id.menu_recent);
        return intent;
    }
}



