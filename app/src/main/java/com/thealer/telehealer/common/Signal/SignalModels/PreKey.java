package com.thealer.telehealer.common.Signal.SignalModels;

import java.io.Serializable;

public class PreKey implements Serializable {
    private int keyId;
    private String pubKey;
    private String privKey;

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

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
        return "keyId : \t" +keyId+"\n pubKey : \t"+pubKey+"\n privKey : \t"+privKey;
    }
}
