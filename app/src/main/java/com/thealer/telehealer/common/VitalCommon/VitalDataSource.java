package com.thealer.telehealer.common.VitalCommon;

import com.thealer.telehealer.common.BaseAdapterObjectModel;

public class VitalDataSource extends BaseAdapterObjectModel {
    private String deviceType;
    private String measurementType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public VitalDataSource(String deviceType, String measurementType) {
        this.deviceType = deviceType;
        this.measurementType = measurementType;
    }

    @Override
    public String getAdapterTitle() {
        return measurementType;
    }

    @Override
    public Object getComparableObject() {
        return measurementType;
    }

}
