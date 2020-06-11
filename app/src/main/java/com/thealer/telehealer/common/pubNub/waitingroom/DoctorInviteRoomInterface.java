package com.thealer.telehealer.common.pubNub.waitingroom;

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;

import java.util.List;

public interface DoctorInviteRoomInterface {
    void didUpdateCurrentPosition(int position);
    void didReceiveMessage(String message);
    void didSubscribersChanged(List<Patientinfo> guestinfo);
    void changeInState(PNPresenceEventResult state);
}
