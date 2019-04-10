package com.thealer.telehealer.apilayer.models.orders.lab;

import android.arch.lifecycle.ViewModel;

import java.io.Serializable;

/**
 * Created by Aswin on 01,December,2018
 */
public class CreateTestApiRequestModel extends ViewModel implements Serializable {

    private String user_guid;
    private String name;
    private LabsDetailBean detail = new LabsDetailBean();
    private String order_id;

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabsDetailBean getDetail() {
        return detail;
    }

    public void setDetail(LabsDetailBean detail) {
        this.detail = detail;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
