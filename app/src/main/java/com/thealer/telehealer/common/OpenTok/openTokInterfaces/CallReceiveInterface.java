package com.thealer.telehealer.common.OpenTok.openTokInterfaces;

import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;

public interface CallReceiveInterface {
     void didFetchedAllRequiredData(Boolean isInWaitingRoom, String doctorName, CallRequest callRequest);
}
