package com.thealer.telehealer.apilayer.models.accessLog;

import android.net.Uri;

import com.thealer.telehealer.apilayer.models.PaginationApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 15,July,2019
 */
public class AccessLogApiResponseModel extends PaginationApiResponseModel {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {

        private int request_log_id;
        private String method;
        private String install_type;
        private int response_http_status;
        private String endpoint;
        private String timestamp;
        private UserBean user;
        private List<com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean> accessed_users;

        public int getRequest_log_id() {
            return request_log_id;
        }

        public void setRequest_log_id(int request_log_id) {
            this.request_log_id = request_log_id;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getInstall_type() {
            return install_type;
        }

        public void setInstall_type(String install_type) {
            this.install_type = install_type;
        }

        public int getResponse_http_status() {
            return response_http_status;
        }

        public void setResponse_http_status(int response_http_status) {
            this.response_http_status = response_http_status;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public String getType() {
            if (endpoint != null) {

                Uri uri = Uri.parse(getEndpoint());
                return uri.getPathSegments().get(0);
            }
            return null;
        }

        public List<com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean> getAccessed_users() {
            return accessed_users;
        }

        public void setAccessed_users(List<com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean> accessed_users) {
            this.accessed_users = accessed_users;
        }

        public static class UserBean implements Serializable {

            private int user_id;
            private String user_guid;
            private String email;

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

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }
        }
    }
}
