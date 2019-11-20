package com.thealer.telehealer.common.Util.Call;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.Pubnub.PubNubMessage;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.home.HomeActivity;

import static com.thealer.telehealer.TeleHealerApplication.application;

import java.util.HashMap;

public class CallChannel {
    public static CallChannel shared = new CallChannel();

    private final String channelName = "_CALL";

    //fields
    private final String action = "action";
    private final String reason = "reason";
    private final String uuid = "uuid";
    private final String fromName = "from";
    private final String mediaUrl = "url";

    //values
    private final String end = "end";

    public void startToListen() {
        PubnubUtil.shared.subscribe(UserDetailPreferenceManager.getUser_guid()+channelName);
    }

    public void stopToListen() {
        PubnubUtil.shared.unSubscribe(UserDetailPreferenceManager.getUser_guid()+channelName);
    }

    public void didReceiveMessage(PubNubMessage message) {
        String type = (String) message.data.get(action);
        if (type != null && type.equals(end)) {

            String endReason = (String) message.data.get(reason);
            if (TextUtils.isEmpty(endReason)) {
                endReason = OpenTokConstants.other;
            }

            String callId = (String) message.data.get(uuid);
            if (TextUtils.isEmpty(callId)) {
                callId = "";
            }

            String from = (String) message.data.get(fromName);
            if (TextUtils.isEmpty(from)) {
                from = "";
            }

            String url = (String) message.data.get(mediaUrl);
            if (TextUtils.isEmpty(url)) {
                url = "";
            }
            dismissCall(callId,endReason,from,url);
        }
    }

    public void postEndCallToOtherPerson(String userGuid,
                                         String callId,
                                         String from,
                                         String url,
                                         String callRejectionReason) {
        HashMap<String,Object> data = new HashMap<>();
        data.put(action,end);
        data.put(reason,callRejectionReason);
        data.put(uuid,callId);
        data.put(fromName,from);
        data.put(mediaUrl,url);
        PubNubMessage message = new PubNubMessage(PubNubMessage.call,data);
        PubnubUtil.shared.sendMessage(userGuid+channelName,message);
    }

    private void dismissCall(String uuid,
                             String reason,
                             String from,String url) {
        if (TokBox.shared == null || TokBox.shared.getCurrentUUID() == null) {
            Log.d("CallChannel", "currentUUID null");
            return;
        }

        String currentUUID = TokBox.shared.getCurrentUUID();
        String endCallUUID = uuid;

        Log.d("CallChannel", "currentUUID " + currentUUID);
        Log.d("CallChannel", "endCallUUID " + endCallUUID);
        if (!currentUUID.toLowerCase().equals(endCallUUID.toLowerCase())) {
            Log.d("CallChannel", "not equal");
            return;
        }

        if (!reason.equals(OpenTokConstants.busyInAnotherLine) && (TokBox.shared.getConnectingDate() == null && TokBox.shared.getConnectedDate() == null && !TokBox.shared.getCalling())) {
            APNSPayload payload = new APNSPayload();
            HashMap<String, String> aps = new HashMap<>();
            if (TokBox.shared.getCallType().equals(OpenTokConstants.video)) {
                aps.put(PubNubNotificationPayload.ALERT, application.getString(R.string.video_missed_call));
            } else {
                aps.put(PubNubNotificationPayload.ALERT, application.getString(R.string.audio_missed_call));
            }
            if (TokBox.shared.getOtherPersonDetail() != null) {
                aps.put(PubNubNotificationPayload.TITLE, TokBox.shared.getOtherPersonDetail().getDisplayName());
            } else {
                aps.put(PubNubNotificationPayload.TITLE, from);
            }

            aps.put(PubNubNotificationPayload.MEDIA_URL, url);
            payload.setAps(aps);

            Intent intent = new Intent(application, HomeActivity.class);
            intent.putExtra(ArgumentKeys.SELECTED_MENU_ITEM, R.id.menu_recent);
            Utils.createNotification(payload, intent);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("CallChannel", "endCall");
                TokBox.shared.endCall(reason);
            }
        });

        LocalBroadcastManager.getInstance(application).sendBroadcast(new Intent(Constants.NOTIFICATION_COUNT_RECEIVER));
    }
}
