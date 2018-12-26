package com.thealer.telehealer.apilayer.models.OpenTok;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

import java.io.Serializable;

/**
 * Created by rsekar on 12/27/18.
 */

public class TokenFetchModel extends BaseApiResponseModel implements Serializable {

    private String token;
    private String apiKey;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public String getToken() {
        return token;
    }

    public String getApiKey() {
        return apiKey;
    }
}
