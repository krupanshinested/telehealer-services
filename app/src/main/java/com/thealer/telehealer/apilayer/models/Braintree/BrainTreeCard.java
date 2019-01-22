package com.thealer.telehealer.apilayer.models.Braintree;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rsekar on 1/22/19.
 */

public class BrainTreeCard implements Serializable {
    private String cardType;
    private String imageUrl;
    private String maskedNumber;
    private String expirationDate;

    @SerializedName("default")
    private Boolean isDefault;

    public String getCardType() {
        return cardType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public String getFormattedCardNumber() {
        if (maskedNumber.length() == 16) {
            StringBuilder cardnumber = new StringBuilder();

            for (int i = 0;i< maskedNumber.length();i++) {
                cardnumber.append(maskedNumber.charAt(i));

                if ((i + 1) % 4 == 0) {
                    cardnumber.append("  ");
                }
            }

            return cardnumber.toString();

        } else {
            return maskedNumber;
        }
    }
}
