package com.thealer.telehealer.common.pubNub.pubinterface;

import com.pubnub.api.models.consumer.presence.PNSetStateResult;

/**
 * Created by rsekar on 1/7/19.
 */

public interface PubNubResultFetcher {
    void didresultFetched(PNSetStateResult isresultFetched);
}
