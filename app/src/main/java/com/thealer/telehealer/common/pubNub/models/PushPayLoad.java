package com.thealer.telehealer.common.pubNub.models;

/**
 * Created by rsekar on 12/26/18.
 */

public class PushPayLoad {

    private APNSPayload pn_apns;
    private FCMPayload pn_fcm;

    public APNSPayload getPn_apns() {
        return pn_apns;
    }

    public FCMPayload getPn_fcm() {
        return pn_fcm;
    }

    public void setPn_apns(APNSPayload pn_apns) {
        this.pn_apns = pn_apns;
    }

    public void setPn_fcm(FCMPayload pn_fcm) {
        this.pn_fcm = pn_fcm;
    }

    public PushPayLoad(APNSPayload pn_apns) {
        this.pn_apns = pn_apns;
        this.pn_fcm = new FCMPayload(pn_apns);
    }

    public PushPayLoad() {
    }
}
