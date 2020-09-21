package com.thealer.telehealer.apilayer.models.vitals.vitalCreation;

import com.google.gson.Gson;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.PreferenceKeys;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rsekar on 11/28/18.
 */

public class VitalDevice implements Serializable{

    String deviceId;
    String type;
    Boolean connected;
    String uuid;

    public VitalDevice(String deviceId, String type, Boolean connected, String uuid) {
        this.deviceId = deviceId;
        this.type = type;
        this.connected = connected;
        this.uuid = uuid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getType() {
        return type;
    }

    public Boolean getConnected() {
        return connected;
    }

    public String getUuid() {
        return uuid;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }
}
