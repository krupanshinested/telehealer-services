package com.thealer.telehealer.common.Signal.SignalModels;

import com.thealer.telehealer.common.Utils;

import java.io.Serializable;

public class IdentityKey implements Serializable {
    private String pubKey;
    private String privKey;

    public String getPubKey() {
        return Utils.trimAndRemoveNewLineCharacters(pubKey);
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivKey() {
        return Utils.trimAndRemoveNewLineCharacters(privKey);
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    @Override
    public String toString() {
        return "pubKey : \t" +pubKey+"\n privKey : \t"+privKey;
    }
}
