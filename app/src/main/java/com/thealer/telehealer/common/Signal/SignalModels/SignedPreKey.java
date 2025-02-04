package com.thealer.telehealer.common.Signal.SignalModels;

import com.thealer.telehealer.common.Utils;

import java.io.Serializable;

public class SignedPreKey implements Serializable {
    private int keyId;
    private String pubKey;
    private String privKey;
    private String signature;

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

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

    public String getSignature() {
        return Utils.trimAndRemoveNewLineCharacters(signature);
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "keyId : \t" +keyId+"\n pubKey : \t"+pubKey+"\n privKey : \t"+privKey+"\n signature : \t"+signature;
    }
}