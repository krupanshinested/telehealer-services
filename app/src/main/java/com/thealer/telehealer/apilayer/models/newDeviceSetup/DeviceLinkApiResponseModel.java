package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;

public class DeviceLinkApiResponseModel extends BaseApiResponseModel {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String external_id;

        public String getExternal_id() {
            return external_id;
        }

        public void setExternal_id(String external_id) {
            this.external_id = external_id;
        }
    }
}
