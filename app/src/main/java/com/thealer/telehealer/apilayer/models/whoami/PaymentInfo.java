package com.thealer.telehealer.apilayer.models.whoami;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentInfo implements Serializable {

    @SerializedName("oauth_status")
    private String oauthStatus;

    @SerializedName("cc_status")
    private String ccStatus;

    @SerializedName("is_cc_captured")
    private boolean isCCCaptured;

    @SerializedName("is_default_card_valid")
    private boolean isDefaultCardValid;

    @SerializedName("saved_cards_count")
    private int savedCardsCount;

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

    public boolean isDefaultCardValid() {
        return isDefaultCardValid;
    }

    public void setDefaultCardValid(boolean defaultCardValid) {
        isDefaultCardValid = defaultCardValid;
    }

    public int getSavedCardsCount() {
        return savedCardsCount;
    }

    public void setSavedCardsCount(int savedCardsCount) {
        this.savedCardsCount = savedCardsCount;
    }
}