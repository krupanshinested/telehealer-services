package com.thealer.telehealer.common.pubNub;

/**
 * Created by rsekar on 1/7/19.
 */

public interface PubNubResult {
    void didSend(Boolean isSuccess);
}
