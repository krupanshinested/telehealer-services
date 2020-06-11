package com.thealer.telehealer.common.pubNub.pubinterface;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;

public interface PubNubDelagate {
    void didSubscribed();
    void didReceiveMessage(APNSPayload message);
    void changeInState(PNPresenceEventResult state);
}
