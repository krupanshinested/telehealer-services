package com.thealer.telehealer.apilayer.models.Pubnub;

import java.io.Serializable;

/**
 * Created by Aswin on 17,May,2019
 */
public class PubnubChatModel implements Serializable {

    private String pn_content;
    private String sender_uuid;
    private String content;
    private String pn_date;
    private String pn_sender_uuid;
    private String date;

    public PubnubChatModel() {
    }

    public PubnubChatModel(String sender_uuid, String content, String date) {
        this.sender_uuid = sender_uuid;
        this.content = content;
        this.date = date;
        this.pn_sender_uuid = sender_uuid;
        this.pn_content = content;
        this.pn_date = date;
    }

    public String getPn_content() {
        return pn_content;
    }

    public void setPn_content(String pn_content) {
        this.pn_content = pn_content;
    }

    public String getSender_uuid() {
        return sender_uuid;
    }

    public void setSender_uuid(String sender_uuid) {
        this.sender_uuid = sender_uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPn_date() {
        return pn_date;
    }

    public void setPn_date(String pn_date) {
        this.pn_date = pn_date;
    }

    public String getPn_sender_uuid() {
        return pn_sender_uuid;
    }

    public void setPn_sender_uuid(String pn_sender_uuid) {
        this.pn_sender_uuid = pn_sender_uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
