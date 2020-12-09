package com.thealer.telehealer.apilayer.models.OpenTok;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by rsekar on 1/4/19.
 */

public class CallRequest implements Serializable {

    private String otherUserGuid;
    private String doctorGuid;
    private String doctorName;
    private String scheduleId;
    private String sessionId;
    private String callType;
    private boolean isCalling;

    private String educationTitle;
    private String educationDescription;

    @Nullable
    private CommonUserApiResponseModel otherPersonDetail;

    @Nullable
    private CallSettings callSettings;

    private String callUUID;
    @Nullable
    private String additionalId;

    private boolean isCallForDirectWaitingRoom = false;
    private boolean isUserAdmitted = true;
    private boolean isForGuestUser = false;

    public CallRequest(String callUUID,
                       String otherUserGuid, @Nullable CommonUserApiResponseModel otherPersonDetail,
                       String doctorGuid, String doctorName, String scheduleId, String callType, boolean isCalling, @Nullable String additionalId) {
        this.otherUserGuid = otherUserGuid;
        this.otherPersonDetail = otherPersonDetail;
        this.doctorGuid = doctorGuid;
        this.doctorName = doctorName;
        this.scheduleId = scheduleId;
        this.callType = callType;
        this.isCalling = isCalling;
        this.additionalId = additionalId;
        this.callUUID = callUUID;
    }

    public void update(CallSettings callSettings) {
        sessionId = callSettings.sessionId;
        this.callSettings = callSettings;
    }

    @Nullable
    public CallSettings getCallSettings() {
        return callSettings;
    }

    public boolean isCalling() {
        return isCalling;
    }

    public String getEducationTitle() {
        return educationTitle;
    }

    public String getCallUUID() {
        return callUUID;
    }

    @Nullable
    public String getAdditionalId() {
        return additionalId;
    }

    public String getEducationDescription() {
        return educationDescription;
    }

    public String getOtherUserGuid() {
        return otherUserGuid;
    }

    public String getDoctorGuid() {
        return doctorGuid;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setDoctorGuid(String doctorGuid) {
        this.doctorGuid = doctorGuid;
    }

    public String getCallType() {
        return callType;
    }

    public void setEducationTitle(String educationTitle) {
        this.educationTitle = educationTitle;
    }

    public void setEducationDescription(String educationDescription) {
        this.educationDescription = educationDescription;
    }

    @Nullable
    public CommonUserApiResponseModel getOtherPersonDetail() {
        return otherPersonDetail;
    }

    public void setOtherPersonDetail(@Nullable CommonUserApiResponseModel otherPersonDetail) {
        this.otherPersonDetail = otherPersonDetail;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public boolean isCallForDirectWaitingRoom() {
        return isCallForDirectWaitingRoom;
    }

    public boolean isUserAdmitted() {
        return isUserAdmitted;
    }

    public void setCallForDirectWaitingRoom(boolean callForDirectWaitingRoom) {
        isCallForDirectWaitingRoom = callForDirectWaitingRoom;
    }

    public void setUserAdmitted(boolean userAdmitted) {
        isUserAdmitted = userAdmitted;
    }

    public boolean isForGuestUser() {
        return isForGuestUser;
    }

    public void setForGuestUser(boolean forGuestUser) {
        isForGuestUser = forGuestUser;
    }
}
