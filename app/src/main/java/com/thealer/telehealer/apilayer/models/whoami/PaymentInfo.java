package com.thealer.telehealer.apilayer.models.whoami;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.common.Constants;

import java.io.Serializable;
import java.util.List;

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

    @SerializedName("currently_due")
    private List<String> currentlyDue;

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

    public List<String> getCurrentlyDue() {
        return currentlyDue;
    }

    public void setCurrentlyDue(List<String> currentlyDue) {
        this.currentlyDue = currentlyDue;
    }
}