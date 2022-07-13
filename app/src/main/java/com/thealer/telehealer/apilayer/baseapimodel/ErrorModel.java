package com.thealer.telehealer.apilayer.baseapimodel;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aswin on 08,October,2018
 */
public class ErrorModel {

    private int statusCode;
    private int code;
    private boolean success;
    private String message;
    private String errorCode;
    private String data;
    private String response;

    //for Brain Tree
    private String name;

    private boolean locked;
    private int lockTimeInMins;

    @SerializedName("is_cc_captured")
    private boolean isCCCaptured;

    @SerializedName("display_button")
    private boolean isDisplayButton;

    @SerializedName("is_default_card_valid")
    private boolean isDefaultCardValid;

    @SerializedName("saved_cards_count")
    private int savedCardsCount;

    public ErrorModel(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isDisplayButton() {
        return isDisplayButton;
    }

    public void setDisplayButton(boolean displayButton) {
        isDisplayButton = displayButton;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String geterrorCode() {
        return errorCode;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getLockTimeInMins() {
        return lockTimeInMins;
    }

    public void setLockTimeInMins(int lockTimeInMins) {
        this.lockTimeInMins = lockTimeInMins;
    }

    public boolean isCCCaptured() {
        return isCCCaptured;
    }

    public void setCCCaptured(boolean CCCaptured) {
        isCCCaptured = CCCaptured;
    }

    public int getSavedCardsCount() {
        return savedCardsCount;
    }

    public void setSavedCardsCount(int savedCardsCount) {
        this.savedCardsCount = savedCardsCount;
    }

    public boolean isDefaultCardValid() {
        return isDefaultCardValid;
    }

    public void setDefaultCardValid(boolean defaultCardValid) {
        isDefaultCardValid = defaultCardValid;
    }
}