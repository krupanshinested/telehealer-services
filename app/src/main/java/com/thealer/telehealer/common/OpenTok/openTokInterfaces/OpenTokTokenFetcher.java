package com.thealer.telehealer.common.OpenTok.openTokInterfaces;

import com.thealer.telehealer.common.OpenTok.CallSettings;

/**
 * Created by rsekar on 12/27/18.
 */

public interface OpenTokTokenFetcher {
    void didFetched(CallSettings callSettings);
}
