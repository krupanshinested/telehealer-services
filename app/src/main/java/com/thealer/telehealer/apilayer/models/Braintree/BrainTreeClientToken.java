package com.thealer.telehealer.apilayer.models.Braintree;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

/**
 * Created by rsekar on 1/22/19.
 */

public class BrainTreeClientToken extends BaseApiResponseModel implements Serializable {

    private String client_token;

    public String getClient_token() {
        return client_token;
    }
}
