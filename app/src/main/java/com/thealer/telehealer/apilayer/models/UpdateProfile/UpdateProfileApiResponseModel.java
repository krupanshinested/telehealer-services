package com.thealer.telehealer.apilayer.models.UpdateProfile;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

public class UpdateProfileApiResponseModel extends CommonUserApiResponseModel {

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
        private String user_activated;

        public String getUser_guid() {
            return user_guid;
        }

        public void setUser_guid(String user_guid) {
            this.user_guid = user_guid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getUser_activated() {
            return user_activated;
        }

        public void setUser_activated(String user_activated) {
            this.user_activated = user_activated;
        }
    }
}
