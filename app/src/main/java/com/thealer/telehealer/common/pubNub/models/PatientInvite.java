package com.thealer.telehealer.common.pubNub.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

import java.io.Serializable;

public class PatientInvite implements Serializable {

   public Patientinfo patientinfo;
   public CommonUserApiResponseModel doctorDetails;
   private String apiKey;
   private String token;

    public String getApiKey() {
        return apiKey;
    }

    public String getToken() {
        return token;
    }

    public PatientInvite() {
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Patientinfo getPatientinfo() {
        return patientinfo;
    }

    public void setPatientinfo(Patientinfo patientinfo) {
        this.patientinfo = patientinfo;
    }

    public CommonUserApiResponseModel getDoctorDetails() {
        return doctorDetails;
    }

    public void setDoctorDetails(CommonUserApiResponseModel doctorDetails) {
        this.doctorDetails = doctorDetails;
    }

    public void getCallRequest(String doctorGuid , CommonUserApiResponseModel doctorDetails){
        boolean canStartGoogleTranscript;
        boolean canStartRecording;
    }

    public String getwaitingRoomChannel(){
        return doctorDetails.getUser_guid()+":waitingRoom";
    }

}

