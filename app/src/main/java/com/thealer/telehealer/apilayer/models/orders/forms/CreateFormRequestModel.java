package com.thealer.telehealer.apilayer.models.orders.forms;

/**
 * Created by Aswin on 28,November,2018
 */
public class CreateFormRequestModel {

    private String form_id;
    private String user_guid;

    public CreateFormRequestModel(String form_id, String user_guid) {
        this.form_id = form_id;
        this.user_guid = user_guid;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }
}
