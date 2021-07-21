package com.thealer.telehealer.common.pubNub.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import org.threeten.bp.DateTimeUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Patientinfo implements Serializable {

    private String phone;
    private String email;
    private String inviteCode;
    private String displayName;
    private Long joinedDate;
    private String userGuid;
    private String sessionId;
    private String id;
    private boolean isAvailable = true;
    private boolean isGuestUser;
    private boolean hasValidCard;

    public Patientinfo() {
    }

    public Patientinfo(String phone, String email, String inviteCode, String displayName, String userGuid, String apiKey, String sessionId, String token, boolean isGuestUser) {
        this.id = UUID.randomUUID().toString();
        this.phone = phone;
        this.email = email;
        this.inviteCode = inviteCode;
        this.displayName = displayName;
        this.joinedDate = findSecondsSince1970();
        this.userGuid = userGuid;
        this.sessionId = sessionId;
        this.isGuestUser = isGuestUser;
    }

    private long findSecondsSince1970() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                .getTimeInMillis() / 1000;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    @Override
    public String toString() {
        return "Guestinfo{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", displayName='" + displayName + '\'' +
                ", joinedDate=" + joinedDate +
                ", userGuid='" + userGuid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", id='" + id + '\'' +
                ", isAvailable=" + isAvailable +
                ", hasValidCard=" + hasValidCard +
                '}';
    }

    public boolean isGuestUser() {
        return isGuestUser;
    }

    public boolean isHasValidCard() {
        return hasValidCard;
    }

    public void setHasValidCard(boolean hasValidCard) {
        this.hasValidCard = hasValidCard;
    }
}
