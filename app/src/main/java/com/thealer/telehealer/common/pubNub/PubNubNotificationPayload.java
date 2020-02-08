package com.thealer.telehealer.common.pubNub;

import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.GCMPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;
import com.thealer.telehealer.views.notification.PushNotificationConstants;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by rsekar on 12/27/18.
 */

public class PubNubNotificationPayload {
    public static final String TITLE = "title";
    public static final String ALERT = "alert";
    public static final String MEDIA_URL = "media_url";
    public static final String MUTABLE_CONTENT = "mutable-content";
    public static final String SOUND = "sound";
    public static final String DEFAULT = "default";
    public static final String CONTENT_AVAILABLE = "content-available";

    public static PushPayLoad getCallInvitePayload(String displayName, String from, String to, String uuid,
                                                   String type,
                                                   String sessionId,
                                                   String doctor_guid) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, Object> aps = new HashMap<>();
        //aps.put(CONTENT_AVAILABLE, 1);
        aps.put("alert", "Call from " + displayName);

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(to);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setPn_ttl(20);
        apnsPayload.setIs_conference(false);
        apnsPayload.setType(type);
        apnsPayload.setSessionId(sessionId);
        apnsPayload.setFrom_name(displayName);
        apnsPayload.setDoctor_guid(doctor_guid);
        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getCallDismissedPermissionLocalPayload(String otherPersonAvatar, String title, String description) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, Object> aps = new HashMap<>();
        aps.put(TITLE, title);
        aps.put(ALERT, description);
        aps.put(MEDIA_URL, otherPersonAvatar);

        apnsPayload.setAps(aps);
        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    
    public static PushPayLoad getWaitingRoomPayload(String to_guid,String scheduleId) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, Object> aps = new HashMap<>();
        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_WAITING_ROOM));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_WAITING_ROOM, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        apnsPayload.setType(APNSPayload.waitingInRoom);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setSessionId(scheduleId);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getNewMessagePayload(String userGuid,
                                         String message,
                                         String from,
                                         String date)  {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        String uuid = UUID.randomUUID().toString();
        HashMap<String, Object> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, 1);

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(userGuid);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setType(APNSPayload.liveMessage);
        apnsPayload.setPn_push(APNSPayload.getPnPushObject());
        apnsPayload.setContent(message);
        apnsPayload.setCreatedAt(date);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

}
