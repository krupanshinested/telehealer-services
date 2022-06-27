package com.thealer.telehealer.apilayer.models.commonResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 13,April,2021
 **/
public class PermissionBean implements Serializable {

    int id;
    int user_id;
    int buser_id;
    int permission_id;
    Boolean value = false;
    int parent_id;
    PermissionDetails permission;
    List<PermissionBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPermission_id() {
        return permission_id;
    }

    public void setPermission_id(int permission_id) {
        this.permission_id = permission_id;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public PermissionDetails getPermission() {
        return permission;
    }

    public void setPermission(PermissionDetails permission) {
        this.permission = permission;
    }

    public List<PermissionBean> getChildren() {
        return children;
    }

    public void setChildren(List<PermissionBean> children) {
        this.children = children;
    }
}
