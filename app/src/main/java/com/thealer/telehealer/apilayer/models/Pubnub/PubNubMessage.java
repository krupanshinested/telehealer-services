package com.thealer.telehealer.apilayer.models.Pubnub;

import java.io.Serializable;
import java.util.HashMap;

public class PubNubMessage implements Serializable {
    public static final String call = "call";

    public String type = "";
    public HashMap<String,Object> data = new HashMap<>();

    public PubNubMessage(String type, HashMap<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    public PubNubMessage() {
    }
}
