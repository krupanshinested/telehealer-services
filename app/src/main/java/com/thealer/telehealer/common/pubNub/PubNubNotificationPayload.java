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

    /**
     * new connection request push
     *
     * @param to_guid
     * @return
     */
    public static PushPayLoad getConnectionPayload(String to_guid) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps;
        aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_CONNECTION_REQUEST));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_CONNECTION_REQUEST, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.connection);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    /**
     * connection accept push
     *
     * @param to_guid
     * @return
     */
    public static PushPayLoad getConnectionAcceptPayload(String to_guid) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_ACCEPT_CONNECTION));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_ACCEPT_CONNECTION, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.connection);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    /**
     * connection reject push
     *
     * @param to_guid
     * @return
     */
    public static PushPayLoad getConnectionRejectPayload(String to_guid) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_REJECT_CONNECTION));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_REJECT_CONNECTION, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.connection);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }


    /**
     * Create new schedule push notification
     *
     * @param to_guid
     * @return
     */
    public static PushPayLoad getNewSchedulePayload(String to_guid) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_SCHEDULE_REQUEST));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_SCHEDULE_REQUEST, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        apnsPayload.setType(APNSPayload.schedule);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    /**
     * Schedule accept push notification
     *
     * @param to_guid
     * @param time
     * @return
     */
    public static PushPayLoad getScheduleAcceptPayload(String to_guid, String time) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_ACCEPT_SCHEDULE));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_ACCEPT_SCHEDULE, time));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.schedule);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }


    /**
     * Schedule reject push notification
     *
     * @param to_guid
     * @param time
     * @return
     */
    public static PushPayLoad getScheduleRejectPayload(String to_guid, String time) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_REJECT_SCHEDULE));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_REJECT_SCHEDULE, time));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.schedule);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }


    /**
     * Schedule cancel push notification
     *
     * @param to_guid
     * @param time
     * @return
     */
    public static PushPayLoad getScheduleCancelPayload(String to_guid, String time) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");

        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_CANCEL_SCHEDULE));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_CANCEL_SCHEDULE, time));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.schedule);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    /**
     * create chat push payload
     *
     * @param to_guid
     * @return
     */
    public static PushPayLoad getChatPayload(String to_guid) {
        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String, String> aps = new HashMap<>();
        aps.put(CONTENT_AVAILABLE, "1");


        aps.put(TITLE, PushNotificationConstants.getTitle(PushNotificationConstants.PUSH_CHAT));
        aps.put(ALERT, PushNotificationConstants.getMessage(PushNotificationConstants.PUSH_CHAT, null));
        aps.put(MUTABLE_CONTENT, "1");
        aps.put(SOUND, DEFAULT);

        apnsPayload.setAps(aps);
        apnsPayload.setUuid(UUID.randomUUID().toString());
        apnsPayload.setType(APNSPayload.message);
        apnsPayload.setFrom(UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
        apnsPayload.setTo(to_guid);
        apnsPayload.setMedia_url(UserDetailPreferenceManager.getUser_avatar());
        apnsPayload.setPn_bundle_ids(new String[]{"com.thealer", "com.thealer.pro"});

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

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

    /**
     * Create new schedule push notification
     *
     * @param to_guid
     * @return
     */
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
