package com.thealer.telehealer.apilayer.models.newDeviceSetup;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;

public class SubmitDeviceApiResponseModel extends BaseApiResponseModel {
    private Data data;

    public static class Data {

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
