package com.thealer.telehealer.apilayer.models.Braintree;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class OAuthURLResp extends BaseApiResponseModel {

    @SerializedName("oauth_url")
    private String oAuthURL;

    public String getoAuthURL() {
        return oAuthURL;
    }

    public void setoAuthURL(String oAuthURL) {
        this.oAuthURL = oAuthURL;
    }
}
