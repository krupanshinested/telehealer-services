package com.thealer.telehealer.apilayer.models.createuser;

import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Aswin on 25,October,2018
 */
public class LicensesBean implements Serializable {

    private String state;
    private String number;
    private String end_date;

    public LicensesBean() {
    }

    public LicensesBean(String state, String number, String end_date) {
        this.state = state;
        this.number = number;
        this.end_date = end_date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public boolean isEqual(LicensesBean licensesBean) {
        return licensesBean.getState().equals(getState()) && licensesBean.getNumber().equals(getNumber()) && Utils.getDateFromPossibleFormat(getEnd_date()).equals(Utils.getDateFromPossibleFormat(licensesBean.getEnd_date()));
    }
}
