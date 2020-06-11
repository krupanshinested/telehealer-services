package com.thealer.telehealer.apilayer.models.guestviewmodel;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

/**
 * Created by Aswin on 01,November,2018
 */
public class GuestLoginApiResponseModel extends BaseApiResponseModel {

    private String apiKey;
    private String sessionId;
    private String token;
    private CommonUserApiResponseModel doctor_details;
    private Data data;

    public CommonUserApiResponseModel getDoctor_details() {
        return doctor_details;
    }

    public void setDoctor_details(CommonUserApiResponseModel doctor_details) {
        this.doctor_details = doctor_details;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public class Data {
        private String name;

        private String user_guid;

        private String token;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser_guid() {
            return user_guid;
        }

        public void setUser_guid(String user_guid) {
            this.user_guid = user_guid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "ClassPojo [name = " + name + ", user_guid = " + user_guid + ", token = " + token + "]";
        }
    }

}
