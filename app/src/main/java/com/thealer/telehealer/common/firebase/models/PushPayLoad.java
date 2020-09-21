package com.thealer.telehealer.common.firebase.models;

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
}
