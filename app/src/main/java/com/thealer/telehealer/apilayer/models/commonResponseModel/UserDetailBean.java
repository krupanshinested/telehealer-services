package com.thealer.telehealer.apilayer.models.commonResponseModel;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;

/**
 * Created by Aswin on 26,November,2018
 */
public class UserDetailBean extends ViewModel implements Serializable,Cloneable {

    private DataBean data;
    private String signature;

    public UserDetailBean() {
    }

    public UserDetailBean(DataBean data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public DataBean getData() {
        if (data == null) {
            data = new DataBean();
        }

        return data;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

}

