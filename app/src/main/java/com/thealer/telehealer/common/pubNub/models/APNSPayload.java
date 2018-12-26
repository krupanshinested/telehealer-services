package com.thealer.telehealer.common.pubNub.models;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by rsekar on 12/26/18.
 */

public class APNSPayload implements Serializable {

    //Notification types
    public static final String video = "video";
    public static final String audio = "audio";
    public static final String text = "text";
    public static final String response = "response";
    public static final String schedule = "schedule";
    public static final String endCall = "end_call";
    public static final String busyInAnotherCall = "busyInAnotherCall";
    public static final String message = "message";
    public static final String connection = "connection";
    public static final String callProposerBanner = "callProposerBanner";
    public static final String callHistory = "callHistory";
    public static final String openApp = "openApp";    // use this to just notify to other user of some activity, no specific action is done on open of app
    public static final String missedCall = "missedCall";


    private HashMap<String,String> aps;
    private String identifier;
    private String type;
    private String from;
    private String to;
    private int pn_ttl;

    @Nullable
    private String call_rejection;
    @Nullable
    private String at;
    @Nullable
    private String[] pn_bundle_ids;

    @Nullable
    private String sessionId;
    @Nullable
    private Double timestamp;
    @Nullable
    private String from_name;
    @Nullable
    private Boolean is_conference;
    @Nullable
    private String doctor_guid;
    @Nullable
    private String uuid;


    public HashMap<String, String> getAps() {
        return aps;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getPn_ttl() {
        return pn_ttl;
    }

    @Nullable
    public String getCall_rejection() {
        return call_rejection;
    }

    @Nullable
    public String getAt() {
        return at;
    }

    @Nullable
    public String[] getPn_bundle_ids() {
        return pn_bundle_ids;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    @Nullable
    public Double getTimestamp() {
        return timestamp;
    }

    @Nullable
    public String getFrom_name() {
        return from_name;
    }

    @Nullable
    public Boolean getIs_conference() {
        return is_conference;
    }

    @Nullable
    public String getDoctor_guid() {
        return doctor_guid;
    }

    @Nullable
    public String getUuid() {
        return uuid;
    }


    public void setAps(HashMap<String, String> aps) {
        this.aps = aps;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setPn_ttl(int pn_ttl) {
        this.pn_ttl = pn_ttl;
    }

    public void setCall_rejection(@Nullable String call_rejection) {
        this.call_rejection = call_rejection;
    }

    public void setAt(@Nullable String at) {
        this.at = at;
    }

    public void setPn_bundle_ids(@Nullable String[] pn_bundle_ids) {
        this.pn_bundle_ids = pn_bundle_ids;
    }

    public void setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimestamp(@Nullable Double timestamp) {
        this.timestamp = timestamp;
    }

    public void setFrom_name(@Nullable String from_name) {
        this.from_name = from_name;
    }

    public void setIs_conference(@Nullable Boolean is_conference) {
        this.is_conference = is_conference;
    }

    public void setDoctor_guid(@Nullable String doctor_guid) {
        this.doctor_guid = doctor_guid;
    }

    public void setUuid(@Nullable String uuid) {
        this.uuid = uuid;
    }
}
