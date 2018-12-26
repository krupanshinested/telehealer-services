package com.thealer.telehealer.common.OpenTok.openTokInterfaces;

import com.thealer.telehealer.apilayer.models.OpenTok.TokenFetchModel;

/**
 * Created by rsekar on 12/27/18.
 */

public interface OpenTokTokenFetcher {
    void didFetched(TokenFetchModel tokenFetchModel);
}
