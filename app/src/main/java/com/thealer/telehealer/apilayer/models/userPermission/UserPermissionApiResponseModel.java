package com.thealer.telehealer.apilayer.models.userPermission;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 07,April,2021
 **/
public class UserPermissionApiResponseModel extends BaseApiResponseModel implements Serializable {

    @SerializedName("data")
    private ArrayList<Datum> data;

    public UserPermissionApiResponseModel() {
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public static class Datum {

        @SerializedName("permission_name")
        private String permissionName="";
        @SerializedName("permission_state")
        private Boolean permissionState=false;
        @SerializedName("sub_permission")
        private ArrayList<Datum> subPermission = new ArrayList<>();

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public Boolean getPermissionState() {
            return permissionState;
        }

        public void setPermissionState(Boolean permissionState) {
            this.permissionState = permissionState;
        }

        public ArrayList<Datum> getSubPermission() {
            return subPermission;
        }

        public void setSubPermission(ArrayList<Datum> subPermission) {
            this.subPermission = subPermission;
        }
    }
}
