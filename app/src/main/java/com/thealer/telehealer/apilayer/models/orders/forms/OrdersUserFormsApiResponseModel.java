package com.thealer.telehealer.apilayer.models.orders.forms;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 28,November,2018
 */
public class OrdersUserFormsApiResponseModel extends BaseApiResponseModel {

    private AssignedByUserBean assigned_by_user;
    private AssignedToUserBean assigned_to_user;
    private int form_id;
    private String name;
    private String url;
    private String created_at;
    private String updated_at;

    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public AssignedByUserBean getAssigned_by_user() {
        return assigned_by_user;
    }

    public void setAssigned_by_user(AssignedByUserBean assigned_by_user) {
        this.assigned_by_user = assigned_by_user;
    }

    public AssignedToUserBean getAssigned_to_user() {
        return assigned_to_user;
    }

    public void setAssigned_to_user(AssignedToUserBean assigned_to_user) {
        this.assigned_to_user = assigned_to_user;
    }

    public static class AssignedByUserBean {

        private String user_guid;

        public String getUser_guid() {
            return user_guid;
        }

        public void setUser_guid(String user_guid) {
            this.user_guid = user_guid;
        }
    }

    public static class AssignedToUserBean {

        private String user_guid;

        public String getUser_guid() {
            return user_guid;
        }

        public void setUser_guid(String user_guid) {
            this.user_guid = user_guid;
        }
    }
}
