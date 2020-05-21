package com.thealer.telehealer.apilayer.models.vitals;

import java.io.Serializable;

public class VitalsDetailBean implements Serializable{

    private String doctor_guid;
    private boolean abnormal;
    private String message;

    public VitalsDetailBean() {
    }

    public VitalsDetailBean(String doctor_guid, boolean abnormal, String message) {
        this.doctor_guid = doctor_guid;
        this.abnormal = abnormal;
        this.message = message;
    }

    public String getDoctor_guid() {
        return doctor_guid;
    }

    public void setDoctor_guid(String doctor_guid) {
        this.doctor_guid = doctor_guid;
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
