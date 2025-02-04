package com.thealer.telehealer.apilayer.models.inviteUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByEmailPhoneRequestModel {

    private Boolean resend_invite;
    private String role;
    private List<InvitationsBean> invitations = new ArrayList<>();

    public List<InvitationsBean> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationsBean> invitations) {
        this.invitations = invitations;
    }

    public Boolean getResend_invite() {
        return resend_invite;
    }

    public void setResend_invite(Boolean resend_invite) {
        this.resend_invite = resend_invite;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static class InvitationsBean {

        private String email;
        private String phone;

        public InvitationsBean(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
