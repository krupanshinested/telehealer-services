package com.thealer.telehealer.common.Signal.SignalModels;

import java.io.Serializable;

/**
 * Created by Aswin on 25,July,2019
 */
public class SignalKeyPostModel implements Serializable {
    private int device_id;
    private int user_id;
    private int registrationId;
    private PreKey preKey;
    private SignedPreKey signedPreKey;
    private IdentityKey identityKey;
    private String user_guid;
    private int encryption_key_id;

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public PreKey getPreKey() {
        return preKey;
    }

    public void setPreKey(PreKey preKey) {
        this.preKey = preKey;
    }

    public SignedPreKey getSignedPreKey() {
        return signedPreKey;
    }

    public void setSignedPreKey(SignedPreKey signedPreKey) {
        this.signedPreKey = signedPreKey;
    }

    public IdentityKey getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(IdentityKey identityKey) {
        this.identityKey = identityKey;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public int getEncryption_key_id() {
        return encryption_key_id;
    }

    public void setEncryption_key_id(int encryption_key_id) {
        this.encryption_key_id = encryption_key_id;
    }
}
