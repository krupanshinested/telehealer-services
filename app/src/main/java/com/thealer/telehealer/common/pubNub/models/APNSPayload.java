package com.thealer.telehealer.common.pubNub.models;

import androidx.annotation.Nullable;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.TeleHealerApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by rsekar on 12/26/18.
 */

public class APNSPayload implements Serializable {

    //Notification types
    public static final String video = "video";
    public static final String audio = "audio";
    public static final String text = "text";
    public static final String vitals = "vitals";
    public static final String response = "response";
    public static final String schedule = "schedule";
    public static final String waitingInRoom = "waitingInRoom";
    public static final String message = "message";
    public static final String connection = "connection";
    public static final String subscription = "subscription";
    public static final String callProposerBanner = "callProposerBanner";
    public static final String callHistory = "callHistory";
    public static final String openApp = "openApp";    // use this to just notify to other user of some activity, no specific action is done on open of app
    public static final String missedCall = "missedCall";
    public static final String endCall = "endCall";
    public static final String liveMessage = "liveMessage";
    public static final String waitingRoomMessage = "waitingRoomMessage";
    public static final String kickOutwaitingRoom = "kickOutwaitingRoom";
    public static final String kickOut = "kickOut";
    public static final String newUserEnteredWaitingRoom = "newUserEnteredWaitingRoom";
    public static final String creditCardExpired = "creditcard";
    public static final String creditCardRequested = "creditCardRequested";
    public static final String charge = "charge";
    public static final String forms = "forms";


    private HashMap<String, Object> aps;
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
    private int form_id;
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
    @Nullable
    private String media_url;

    private String vital_type;

    @Nullable
    private String content;
    @Nullable
    private String createdAt;

    private ArrayList<HashMap<String, Object>> pn_push;


    public HashMap<String, Object> getAps() {
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

    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
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


    public void setAps(HashMap<String, Object> aps) {
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

    @Nullable
    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(@Nullable String media_url) {
        this.media_url = media_url;
    }

    public String getVital_type() {
        return vital_type;
    }

    public void setVital_type(String vital_type) {
        this.vital_type = vital_type;
    }

    public void setPn_push(HashMap<String, Object> pn_push) {
        ArrayList<HashMap<String, Object>> item = new ArrayList<>();
        item.add(pn_push);
        this.pn_push = item;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public void setContent(@Nullable String content) {
        this.content = content;
    }

    @Nullable
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable String createdAt) {
        this.createdAt = createdAt;
    }

    public static HashMap<String, Object> getPnPushObject() {
        HashMap<String, Object> item = new HashMap<>();
        item.put("version", "v2");
        item.put("push_type","voip");
        item.put("auth_method","token");
        ArrayList<HashMap<String, String>> targets = new ArrayList<>();
        for (String bundleId : TeleHealerApplication.appConfig.getOtherParentBundleIds()) {
            HashMap<String, String> target = new HashMap<>();
            target.put("environment", BuildConfig.DEBUG ? "development" : "production");
            target.put("topic", bundleId);
            targets.add(target);
        }

        item.put("targets", targets);
        return item;
    }
}
