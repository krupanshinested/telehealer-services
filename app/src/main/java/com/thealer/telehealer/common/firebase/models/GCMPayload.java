package com.thealer.telehealer.common.firebase.models;

import java.io.Serializable;
import java.util.HashMap;

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

        public APNSPayload getBody() {
            return body;
        }

        public GCMData(APNSPayload body) {
            this.body = body;
        }
    }
}
