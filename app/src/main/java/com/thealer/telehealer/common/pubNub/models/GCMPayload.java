package com.thealer.telehealer.common.pubNub.models;

import java.io.Serializable;

/**
 * Created by rsekar on 12/26/18.
 */

public class GCMPayload implements Serializable {
    private GCMData data;

    public GCMData getData() {
        return data;
    }

    public GCMPayload(APNSPayload body) {
        this.data = new GCMData(body);
    }

    public class GCMData implements Serializable {
        private APNSPayload body;

        public GCMData(APNSPayload apnsPayload) {
            this.body = apnsPayload;
        }

        public APNSPayload getBody() {
            return body;
        }
    }
}
