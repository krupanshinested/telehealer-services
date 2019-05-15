package com.thealer.telehealer.apilayer.models.chat;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.models.PaginationApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 15,May,2019
 */
public class ChatApiResponseModel extends PaginationApiResponseModel implements Serializable {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {

        @SerializedName("message")
        private String chatMessage;
        private String created_at;
        private UserBean user;
        private String file_type;

        public ResultBean() {
        }

        public ResultBean(String chatMessage, UserBean user) {
            this.chatMessage = chatMessage;
            this.created_at = Utils.getCurrentUtcDate();
            this.user = user;
        }

        public ResultBean(String chatMessage, String created_at, UserBean user) {
            this.chatMessage = chatMessage;
            this.created_at = created_at;
            this.user = user;
        }

        public String getChatMessage() {
            return chatMessage;
        }

        public void setChatMessage(String chatMessage) {
            this.chatMessage = chatMessage;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public static class UserBean implements Serializable {
            public UserBean() {
            }

            public UserBean(String user_guid) {
                this.user_guid = user_guid;
            }

            private String user_guid;

            public String getUser_guid() {
                return user_guid;
            }

            public void setUser_guid(String user_guid) {
                this.user_guid = user_guid;
            }
        }
    }
}
