package com.thealer.telehealer.apilayer.models.setDevice;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

public class SetDeviceResponseModel extends BaseApiResponseModel implements Serializable {

    private String code;

    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String is_active;

        private String device_id;

        private String sms_enabled;

        private String created_at;

        private String deleted_by;

        private String deleted_at;

        private String created_by;

        private String is_deleted;

        private String updated_at;

        private String user_id;

        private String healthcare_device_id;

        private String updated_by;

        private String id;
    }
}
