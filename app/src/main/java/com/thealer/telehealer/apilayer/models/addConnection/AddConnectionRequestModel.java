package com.thealer.telehealer.apilayer.models.addConnection;

import java.io.Serializable;

/**
 * Created by Aswin on 19,November,2018
 */
public class AddConnectionRequestModel implements Serializable {

    private Boolean resend_invite;
    private String requestee_id;
    private String type;
    private String message;
    private String detail;
    private String designation;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getRequestee_id() {
        return requestee_id;
    }

    public void setRequestee_id(String requestee_id) {
        this.requestee_id = requestee_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getResend_invite() {
        return resend_invite;
    }

    public void setResend_invite(Boolean resend_invite) {
        this.resend_invite = resend_invite;
    }


}
