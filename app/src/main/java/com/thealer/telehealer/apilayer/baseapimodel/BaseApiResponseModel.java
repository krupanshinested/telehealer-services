package com.thealer.telehealer.apilayer.baseapimodel;

import java.io.Serializable;

/**
 * Created by Aswin on 08,October,2018
 */
public class BaseApiResponseModel implements Serializable {

    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
