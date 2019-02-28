package com.thealer.telehealer.apilayer.models.commonResponseModel;

import java.io.Serializable;

/**
 * Created by Aswin on 28,February,2019
 */
public class ClinicBean implements Serializable {
    private String name;
    private String state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
