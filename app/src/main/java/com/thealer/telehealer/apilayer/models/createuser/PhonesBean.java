package com.thealer.telehealer.apilayer.models.createuser;

import java.io.Serializable;

/**
 * Created by Aswin on 25,October,2018
 */
public class PhonesBean implements Serializable {

    private String number;
    private String type;

    public PhonesBean() {
    }

    public PhonesBean(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
