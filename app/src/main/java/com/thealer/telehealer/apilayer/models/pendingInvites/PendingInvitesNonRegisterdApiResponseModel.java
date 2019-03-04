package com.thealer.telehealer.apilayer.models.pendingInvites;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesNonRegisterdApiResponseModel extends BaseApiResponseModel {

    private int count;
    private Object prev;
    private Object next;
    private List<ResultBean> result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {

        private int invitation_id;
        private int doctor_id;
        private String email;
        private String phone;
        private boolean accepted;
        private String created_at;
        private String updated_at;

        public int getInvitation_id() {
            return invitation_id;
        }

        public void setInvitation_id(int invitation_id) {
            this.invitation_id = invitation_id;
        }

        public int getDoctor_id() {
            return doctor_id;
        }

        public void setDoctor_id(int doctor_id) {
            this.doctor_id = doctor_id;
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

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
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
    }
}
