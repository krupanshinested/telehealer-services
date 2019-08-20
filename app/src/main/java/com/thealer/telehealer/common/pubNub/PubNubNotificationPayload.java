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

    public static PushPayLoad getPayloadForBusyInAnotherCall(String from, String to, String uuid) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(to);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setPn_ttl(20);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        apnsPayload.setType(APNSPayload.busyInAnotherCall);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getPayloadForEndCall(String displayName, String from, String to, String uuid, String callRejectionReason) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(to);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setPn_ttl(20);
        apnsPayload.setFrom_name(displayName);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        apnsPayload.setType(APNSPayload.endCall);
        apnsPayload.setCall_rejection(callRejectionReason);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getCallInvitePayload(String displayName, String from, String to, String uuid,
                                                   String type,
                                                   String sessionId,
                                                   String doctor_guid) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");
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
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getCallDismissedPermissionLocalPayload(String otherPersonAvatar, String title, String description) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
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

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

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

}
