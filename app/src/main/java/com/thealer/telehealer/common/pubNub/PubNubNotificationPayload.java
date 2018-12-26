package com.thealer.telehealer.common.pubNub;

import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.common.pubNub.models.GCMPayload;
import com.thealer.telehealer.common.pubNub.models.PushPayLoad;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by rsekar on 12/27/18.
 */

public class PubNubNotificationPayload {

    public static PushPayLoad getPayloadForBusyInAnotherCall(String from,String to,String uuid) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String,String > aps = new HashMap<>();
        aps.put("content-available","1");

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(to);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setPn_ttl(20);
        apnsPayload.setType(APNSPayload.busyInAnotherCall);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getPayloadForEndCall(String from,String to,String uuid,String callRejectionReason) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String,String > aps = new HashMap<>();
        aps.put("content-available","1");

        apnsPayload.setAps(aps);
        apnsPayload.setFrom(from);
        apnsPayload.setTo(to);
        apnsPayload.setUuid(uuid);
        apnsPayload.setIdentifier(uuid);
        apnsPayload.setPn_ttl(20);
        apnsPayload.setType(APNSPayload.endCall);
        apnsPayload.setCall_rejection(callRejectionReason);

        pushPayLoad.setPn_apns(apnsPayload);
        pushPayLoad.setPn_gcm(new GCMPayload(apnsPayload));

        return pushPayLoad;
    }

    public static PushPayLoad getCallInvitePayload(String displayName,String from,String to,String uuid,
                                                   String type,
                                                   String sessionId,
                                                   String doctor_guid) {

        PushPayLoad pushPayLoad = new PushPayLoad();
        APNSPayload apnsPayload = new APNSPayload();

        HashMap<String,String > aps = new HashMap<>();
        aps.put("content-available","1");
        aps.put("alert","Call from "+displayName);

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

}
