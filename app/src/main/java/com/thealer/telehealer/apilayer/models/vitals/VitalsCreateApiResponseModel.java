package com.thealer.telehealer.apilayer.models.vitals;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;

/**
 * Created by Aswin on 03,April,2019
 */
public class VitalsCreateApiResponseModel extends BaseApiResponseModel {
    private boolean abnormal;
    private ArrayList<VitalsDetailBean> detail;
    private String description;

    public boolean isAbnormal() {
        if (getDetail()!=null) {
            if (getDetail().size() != 0) {
                for (int j = 0; j < getDetail().size(); j++) {
                    if (detail.get(j).isAbnormal()) {
                     return true;
                    }
                }
            }
        }
        return false;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<VitalsDetailBean> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<VitalsDetailBean> detail) {
        this.detail = detail;
    }
}
