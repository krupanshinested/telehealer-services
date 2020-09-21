package com.thealer.telehealer.common.VitalCommon;

/**
 * Created by rsekar on 12/4/18.
 */

public class BatteryResult {

    private String deviceType;
    private String deviceId;
    private int battery;

    public BatteryResult(String deviceType,String deviceId,int battery) {
        this.deviceId = deviceId;
        this.battery = battery;
        this.deviceType = deviceType;
    }

    public int getBattery() {
        return battery;
    }
}
