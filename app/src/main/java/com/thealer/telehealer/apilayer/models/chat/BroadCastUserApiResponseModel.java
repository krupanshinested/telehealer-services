package com.thealer.telehealer.apilayer.models.chat;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 15,April,2021
 **/
public class BroadCastUserApiResponseModel extends BaseApiResponseModel implements Serializable {

    List<UserBean> patients;

    public static class UserBean {
        int user_id;
        int buser_id;
        UserInfo user;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getBuser_id() {
            return buser_id;
        }

        public void setBuser_id(int buser_id) {
            this.buser_id = buser_id;
        }

        public class UserInfo {
            int user_id;
            String user_guid;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public String getUser_guid() {
                return user_guid;
            }

            public void setUser_guid(String user_guid) {
                this.user_guid = user_guid;
            }
        }
    }

    public List<UserBean> getPatients() {
        return patients;
    }

    public void setPatients(List<UserBean> patients) {
        this.patients = patients;
    }
}
