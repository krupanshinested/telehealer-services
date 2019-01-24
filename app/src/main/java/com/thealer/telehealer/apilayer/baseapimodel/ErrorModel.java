package com.thealer.telehealer.apilayer.baseapimodel;

import androidx.annotation.Nullable;

/**
 * Created by Aswin on 08,October,2018
 */
public class ErrorModel {

    private int statusCode;
    private int code;
    private boolean success;
    private String message;
    private String data;
    private String response;

    //for Brain Tree
    private String name;

    public ErrorModel(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}