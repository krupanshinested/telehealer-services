package com.thealer.telehealer.apilayer.models.vitals;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 03,April,2019
 */
public class VitalsCreateApiResponseModel extends BaseApiResponseModel {
    private boolean abnormal;

    private String description;

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public String getDescription() {
        return description;
    }
}
