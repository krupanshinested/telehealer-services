package com.thealer.telehealer.common.pubNub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.ResultFetcher;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.notification.NotificationActivity;
import com.thealer.telehealer.views.notification.NotificationDetailActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            APNSPayload data = mapper.readValue(message.get("body"), new TypeReference<APNSPayload>() {
            });
            Log.e(TAG, "message " + data.getTo());
            extractMessage(data);
        } catch (IOException e) {
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

                if (UserDetailPreferenceManager.getUser_guid() != null && !UserDetailPreferenceManager.getUser_guid().isEmpty()) {
                    PubnubUtil.shared.grantPubNub(currentToken, UserDetailPreferenceManager.getUser_guid());
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
            case APNSPayload.endCall:
                dismissCall(data);
                break;
            case APNSPayload.busyInAnotherCall:
                TokBox.shared.endCall(OpenTokConstants.busyInAnotherLine);
                break;
            case APNSPayload.text:
                break;
            case APNSPayload.message:
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
            case APNSPayload.connection:
            case APNSPayload.schedule:
                intent = new Intent(this, NotificationActivity.class);
                Utils.createNotification(data, intent);
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

    // Display the incoming call to the user
    private void dismissCall(APNSPayload data) {

        if (TokBox.shared == null || TokBox.shared.getCurrentUUID() == null) {
            Log.d("MessagingService", "currentUUID null");
            return;
        }

        String currentUUID = TokBox.shared.getCurrentUUID();

        String endCallUUID = data.getUuid() != null ? data.getUuid() : data.getIdentifier();

        Log.d("MessagingService", "currentUUID " + currentUUID);
        Log.d("MessagingService", "endCallUUID " + endCallUUID);
        if (!currentUUID.equals(endCallUUID)) {
            return;
        }

        if (TokBox.shared.getConnectingDate() == null && TokBox.shared.getConnectedDate() == null && !TokBox.shared.getCalling()) {
            APNSPayload payload = new APNSPayload();
            HashMap<String, String> aps = new HashMap<>();
            if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
                aps.put(PubNubNotificationPayload.ALERT, getString(R.string.video_missed_call));
            } else {
                aps.put(PubNubNotificationPayload.ALERT, getString(R.string.audio_missed_call));
            }
            if (TokBox.shared.getOtherPersonDetail() != null) {
                aps.put(PubNubNotificationPayload.TITLE, TokBox.shared.getOtherPersonDetail().getDisplayName());
            } else {
                aps.put(PubNubNotificationPayload.TITLE, data.getFrom_name());
            }

            aps.put(PubNubNotificationPayload.MEDIA_URL, data.getMedia_url());
            payload.setAps(aps);
            Utils.createNotification(payload, getRecentIntent());
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (data.getCall_rejection() != null) {
                    TokBox.shared.endCall(data.getCall_rejection());
                } else {
                    TokBox.shared.endCall(OpenTokConstants.other);
                }
            }
        });
    }

    private void displayIncomingCall(APNSPayload data) {
        if (!TokBox.shared.isActiveCallPreset()) {
            TokBox.shared.didRecieveIncoming(data);
        } else {
            PushPayLoad pushPayLoad = PubNubNotificationPayload.getPayloadForBusyInAnotherCall(UserDetailPreferenceManager.getUser_guid(), data.getFrom(), data.getUuid());
            PubnubUtil.shared.publishVoipMessage(pushPayLoad, null);

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



