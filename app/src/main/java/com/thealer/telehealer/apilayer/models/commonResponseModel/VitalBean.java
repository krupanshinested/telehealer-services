package com.thealer.telehealer.apilayer.models.commonResponseModel;

import java.io.Serializable;

/**
 * Created by Nimesh Patel
 * Created Date: 26,July,2021
 **/
public class VitalBean implements Serializable {
    String type;
    String count;
    String last_value;
    String last_date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLast_value() {
        return last_value;
    }

    public void setLast_value(String last_value) {
        this.last_value = last_value;
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public VitalBean(String type, String count, String last_value, String last_date) {
        this.type = type;
        this.count = count;
        this.last_value = last_value;
        this.last_date = last_date;
    }

}
