package com.thealer.telehealer.common.pubNub.models;

import java.io.Serializable;

/**
 * Created by rsekar on 12/26/18.
 */

public class FCMPayload implements Serializable {
    private FCMData data;

    public FCMData getData() {
        return data;
    }

    public FCMPayload(APNSPayload body) {
        this.data = new FCMData(body);
    }

    public class FCMData implements Serializable {
        private APNSPayload body;

        public FCMData(APNSPayload apnsPayload) {
            this.body = apnsPayload;
        }

        public APNSPayload getBody() {
            return body;
        }
    }
}
