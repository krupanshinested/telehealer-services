package com.thealer.telehealer.apilayer.models.whoami;

import com.google.gson.annotations.SerializedName;

public class PaymentInfo {

    @SerializedName("oauth_status")
    private String oauthStatus;

    @SerializedName("cc_status")
    private String ccStatus;

    @SerializedName("is_cc_captured")
    private boolean isCCCaptured;

    public String getOauthStatus() {
        return oauthStatus;
    }

    public String getCcStatus() {
        return ccStatus;
    }


    public boolean isCCCaptured() {
        return isCCCaptured;
    }

    public void setCCCaptured(boolean CCCaptured) {
        isCCCaptured = CCCaptured;
    }
}