package com.thealer.telehealer.common.pubNub;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.notification.NotificationActivity;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static com.thealer.telehealer.TeleHealerApplication.notificationChannelId;

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
                createNotification(data.getAps().get(PubNubNotificationPayload.TITLE), data.getAps().get(PubNubNotificationPayload.ALERT), intent);
                break;
            case APNSPayload.vitals:
                intent = new Intent(this, HomeActivity.class);
                createNotification(data.getAps().get(PubNubNotificationPayload.TITLE), data.getAps().get(PubNubNotificationPayload.ALERT), intent);
                break;
        }
    }

    // Display the incoming call to the user
    private void dismissCall(APNSPayload data) {
        String currentUUID = TokBox.shared.getCurrentUUID();
        String endCallUUID = data.getUuid() != null ? data.getUuid() : data.getIdentifier();

        if (currentUUID != null && !currentUUID.equals(endCallUUID)) {
            Log.d("MessagingService", "currentUUID "+currentUUID);
            Log.d("MessagingService", "endCallUUID "+endCallUUID);
            return;
        }

        if (TokBox.shared.getConnectingDate() == null && TokBox.shared.getConnectedDate() == null) {
            //TODO : need to add local tray missed call notification
            //AppDelegate.showMissedCallNotifiation(userInfo: call.userinfo ?? [:],isVideo : TokBoxSession.shared.type == CallType.video)
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

            //TODO local notification
            /*let notification = prepareUserInfoForMissedCall(userInfo: userinfo,isVideo: hasVideo)
            self.displayLoaclNotification(notification: notification)*/
        }
    }

    private void createNotification(String title, String message, Intent intent) {

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                .setSmallIcon(R.drawable.app_icon)
                .setBadgeIconType(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        if (intent != null) {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntentWithParentStack(intent);

            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendingIntent);
        }

        Random random = new Random();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(random.nextInt(1000), notification.build());

    }
    public static String getCurrentToken() {
        return currentToken;
    }
}



