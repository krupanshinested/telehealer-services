package com.thealer.telehealer.common.pubNub.waitingroom;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;

public interface PatientInviteRoomInterface {
    void didUpdateCurrentPosition(int position);
    void didReceiveMessage(APNSPayload message);
    void didPostStateVeryFirstTime();
    void kickOut();
    void changeInState(PNPresenceEventResult pnPresenceEventResult);
}
