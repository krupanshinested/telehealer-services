package com.thealer.telehealer.apilayer.models.inviteUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByEmailPhoneRequestModel {

    private List<InvitationsBean> invitations = new ArrayList<>();

    public List<InvitationsBean> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationsBean> invitations) {
        this.invitations = invitations;
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
