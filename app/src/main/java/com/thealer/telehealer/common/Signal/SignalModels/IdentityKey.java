package com.thealer.telehealer.common.Signal.SignalModels;

import java.io.Serializable;

public class IdentityKey implements Serializable {
    private String pubKey;
    private String privKey;

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    @Override
    public String toString() {
        return "pubKey : \t" +pubKey+"\n privKey : \t"+privKey;
    }
}
