package com.thealer.telehealer.common.pubNub;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;

/**
 * Created by rsekar on 12/25/18.
 */

public class TelehealerFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = TelehealerFirebaseMessagingService.class.getSimpleName();
    private static String currentToken = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> message = remoteMessage.getData();
        Log.v(TAG, "message " + message.toString());

        ObjectMapper mapper = new ObjectMapper();

        try {
            APNSPayload data = mapper.readValue(message.get("body"), new TypeReference<APNSPayload>(){});
            Log.v(TAG, "message " + data.getTo());
            extractMessage(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d("MessagingService","new token "+token);
        assignToken(token);
    }

    public static void assignToken(String currentToken) {
        TelehealerFirebaseMessagingService.currentToken = currentToken;

        if (UserDetailPreferenceManager.getUser_guid() != null && !UserDetailPreferenceManager.getUser_guid().isEmpty()) {
            PubnubUtil.shared.grantPubNub(currentToken,UserDetailPreferenceManager.getUser_guid());
        }
    }

    public static void refresh() {
        assignToken(currentToken);
    }


    private void extractMessage(APNSPayload data) {
        switch (data.getType()) {
            case APNSPayload.audio:
            case APNSPayload.video:
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        displayIncomingCall(data);
                    }
                });
                break;
            case APNSPayload.endCall:
                TokBox.shared.endCall(data.getCall_rejection());
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
            case APNSPayload.connection:
                break;
            case APNSPayload.missedCall:
                break;
            case APNSPayload.openApp:
                break;
            case APNSPayload.response:
                break;
            case APNSPayload.schedule:
                break;
        }
    }

    // Display the incoming call to the user
    private void dismissCall(APNSPayload data) {
        String currentUUID = TokBox.shared.getCurrentUUID();

        if (currentUUID != null && !currentUUID.equals(data.getUuid())) {
            return;
        }

        if (TokBox.shared.getConnectingDate() == null && TokBox.shared.getConnectedDate() == null) {
            //TODO : need to add local tray missed call notification
            //AppDelegate.showMissedCallNotifiation(userInfo: call.userinfo ?? [:],isVideo : TokBoxSession.shared.type == CallType.video)
        }

        if (data.getCall_rejection() != null) {
            TokBox.shared.endCall(data.getCall_rejection());
        } else {
            TokBox.shared.endCall(OpenTokConstants.other);
        }

    }

    private void displayIncomingCall(APNSPayload data) {
        if (!TokBox.shared.isActiveCallPreset()) {
             TokBox.shared.didRecieveIncoming(data);
        } else {
            PushPayLoad pushPayLoad = PubNubNotificationPayload.getPayloadForBusyInAnotherCall(UserDetailPreferenceManager.getUser_guid(),data.getFrom(),data.getUuid());
            PubnubUtil.shared.publishVoipMessage(pushPayLoad,null);

            //TODO local notification
            /*let notification = prepareUserInfoForMissedCall(userInfo: userinfo,isVideo: hasVideo)
            self.displayLoaclNotification(notification: notification)*/
        }
    }

}



