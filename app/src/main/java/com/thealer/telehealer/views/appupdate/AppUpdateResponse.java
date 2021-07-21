package com.thealer.telehealer.views.appupdate;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class AppUpdateResponse extends BaseApiResponseModel {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {

        @SerializedName("name")
        private String versionCode;

        @SerializedName("is_hard_update")
        private boolean isHardUpdate;

        @SerializedName("description")
        private String updateDescription;

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public boolean isHardUpdate() {
            return isHardUpdate;
        }

        public void setHardUpdate(boolean hardUpdate) {
            isHardUpdate = hardUpdate;
        }

        public String getUpdateDescription() {
            return updateDescription;
        }

        public void setUpdateDescription(String updateDescription) {
            this.updateDescription = updateDescription;
        }
    }
}
