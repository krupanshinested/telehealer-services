package com.thealer.telehealer.apilayer.models.commonResponseModel;

/**
 * Created by Nimesh Patel
 * Created Date: 15,April,2021
 **/
public class PermissionRequestModel {
    Boolean value=false;
    String guid;
    int id;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
