package com.thealer.telehealer.common.pubNub.models;

/**
 * Created by rsekar on 12/26/18.
 */

public class PushPayLoad {

    private APNSPayload pn_apns;
    private GCMPayload pn_gcm;

    public APNSPayload getPn_apns() {
        return pn_apns;
    }
    public GCMPayload getPn_gcm() {
        return pn_gcm;
    }

    public void setPn_apns(APNSPayload pn_apns) {
        this.pn_apns = pn_apns;
    }

    public void setPn_gcm(GCMPayload pn_gcm) {
        this.pn_gcm = pn_gcm;
    }

    public PushPayLoad(APNSPayload pn_apns) {
        this.pn_apns = pn_apns;
        this.pn_gcm = new GCMPayload(pn_apns);
    }

    public PushPayLoad() {
    }
}
