package com.thealer.telehealer.common.firebase;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.firebase.models.APNSPayload;
import com.thealer.telehealer.common.firebase.models.GCMPayload;

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
                break;
            case APNSPayload.video:
                break;
            case APNSPayload.endCall:
                break;
            case APNSPayload.busyInAnotherCall:
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
}



