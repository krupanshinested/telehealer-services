package com.thealer.telehealer.views.home.recents.adapterModels;

import java.io.Serializable;

/**
 * Created by Aswin on 26,April,2019
 */
public class CallSummaryModel implements Serializable{
    private String callStartTime;
    private String callEndTime;
    private String callType;
    private String callCategory;
    private String video;
    private int durationInSec;
    private String screenShot;

    public CallSummaryModel(String callStartTime, String callEndTime, String callType, String callCategory, String video, int durationInSec, String screenShot) {
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
        this.callType = callType;
        this.callCategory = callCategory;
        this.video = video;
        this.durationInSec = durationInSec;
        this.screenShot = screenShot;
    }

    public String getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(String screenShot) {
        this.screenShot = screenShot;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(String callEndTime) {
        this.callEndTime = callEndTime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallCategory() {
        return callCategory;
    }

    public void setCallCategory(String callCategory) {
        this.callCategory = callCategory;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(int durationInSec) {
        this.durationInSec = durationInSec;
    }
}