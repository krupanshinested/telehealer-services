package com.thealer.telehealer.common.Signal.SignalModels;

import java.io.Serializable;

public class SignalKey implements Serializable {
    private int device_id;
    private int user_id;
    private int registration_id;
    private PreKey pre_key;
    private SignedPreKey signed_pre_key;
    private IdentityKey identity_key;
    private String user_guid;
    private int encryption_key_id;
    private String created_at;
    private String updated_at;

    public int getEncryption_key_id() {
        return encryption_key_id;
    }

    public void setEncryption_key_id(int encryption_key_id) {
        this.encryption_key_id = encryption_key_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

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

    public int getRegistration_id() {
        return registration_id;
    }

    public void setRegistration_id(int registration_id) {
        this.registration_id = registration_id;
    }

    public PreKey getPre_key() {
        return pre_key;
    }

    public void setPre_key(PreKey pre_key) {
        this.pre_key = pre_key;
    }

    public SignedPreKey getSigned_pre_key() {
        return signed_pre_key;
    }

    public void setSigned_pre_key(SignedPreKey signed_pre_key) {
        this.signed_pre_key = signed_pre_key;
    }

    public IdentityKey getIdentity_key() {
        return identity_key;
    }

    public void setIdentity_key(IdentityKey identity_key) {
        this.identity_key = identity_key;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    @Override
    public String toString() {
        return "device_id : \t" + device_id + "\n user_id : \t" + user_id + "\n registration_id : \t" + registration_id + "\n preKey : \n" + pre_key.toString() + "\n signedPreKey : \n" + signed_pre_key.toString() + "\n identityKey : \n" + identity_key.toString() + "\n userGuid : \n" + user_guid;
    }
}
