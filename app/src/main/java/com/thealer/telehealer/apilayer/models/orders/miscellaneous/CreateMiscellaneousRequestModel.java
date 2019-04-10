package com.thealer.telehealer.apilayer.models.orders.miscellaneous;

/**
 * Created by Aswin on 05,March,2019
 */
public class CreateMiscellaneousRequestModel {

    private String user_guid;
    private String name;
    private String order_id;
    private MiscellaneousDetailBean detail;

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

    public MiscellaneousDetailBean getDetail() {
        return detail;
    }

    public void setDetail(MiscellaneousDetailBean detail) {
        this.detail = detail;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
