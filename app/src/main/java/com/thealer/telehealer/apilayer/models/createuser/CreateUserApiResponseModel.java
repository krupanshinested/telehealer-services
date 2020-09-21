package com.thealer.telehealer.apilayer.models.createuser;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 17,October,2018
 */
public class CreateUserApiResponseModel extends BaseApiResponseModel {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String user_guid;
        private String token;
        private String refresh_token;
        private String name;

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

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
