package com.thealer.telehealer.apilayer.models.inviteUser;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.List;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByEmailPhoneApiResponseModel extends BaseApiResponseModel{

    private int successCount;
    private int failureCount;
    private List<ResultDataBean> resultData;

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<ResultDataBean> getResultData() {
        return resultData;
    }

    public void setResultData(List<ResultDataBean> resultData) {
        this.resultData = resultData;
    }

    public static class ResultDataBean {

        private String email;
        private String phone;
        private boolean success;
        private String message;
        private String user_guid;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUser_guid() {
            return user_guid;
        }

        public void setUser_guid(String user_guid) {
            this.user_guid = user_guid;
        }
    }
}
