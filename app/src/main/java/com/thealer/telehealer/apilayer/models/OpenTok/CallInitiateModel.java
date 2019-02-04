package com.thealer.telehealer.apilayer.models.OpenTok;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;

import java.io.Serializable;

/**
 * Created by rsekar on 1/4/19.
 */

public class CallInitiateModel implements Serializable {

    private String toUserGuid;
    private CommonUserApiResponseModel userData;
    private String doctorGuid;
    private String doctorName;
    private String scheduleId;
    private String token;
    private String sessionId;
    private String tokBoxApiKey;
    private String callType;

    public CallInitiateModel(String toUserGuid, CommonUserApiResponseModel userData,
                             String doctorGuid, String doctorName, String scheduleId,String callType) {
        this.toUserGuid = toUserGuid;
        this.userData = userData;
        this.doctorGuid = doctorGuid;
        this.doctorName = doctorName;
        this.scheduleId = scheduleId;
        this.callType = callType;
    }

    public void update(TokenFetchModel tokenFetchModel) {
        sessionId = tokenFetchModel.getSessionId();
        tokBoxApiKey = tokenFetchModel.getApiKey();
        token = tokenFetchModel.getToken();
    }

    public String getToUserGuid() {
        return toUserGuid;
    }

    public CommonUserApiResponseModel getUserData() {
        return userData;
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

    public String getToken() {
        return token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTokBoxApiKey() {
        return tokBoxApiKey;
    }

    public void setToUserGuid(String toUserGuid) {
        this.toUserGuid = toUserGuid;
    }

    public void setUserData(CommonUserApiResponseModel userData) {
        this.userData = userData;
    }

    public void setDoctorGuid(String doctorGuid) {
        this.doctorGuid = doctorGuid;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTokBoxApiKey(String tokBoxApiKey) {
        this.tokBoxApiKey = tokBoxApiKey;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
