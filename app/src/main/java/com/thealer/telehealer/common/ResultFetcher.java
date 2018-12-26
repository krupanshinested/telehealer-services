package com.thealer.telehealer.common;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by rsekar on 1/7/19.
 */

public interface ResultFetcher {
    void didFetched(BaseApiResponseModel baseApiResponseModel);
}
