package com.thealer.telehealer.views.inviteUser;

/**
 * Created by Aswin on 19,February,2019
 */
public class SelectedContactModel {
    private String id;
    private String email;
    private String phone;

    public SelectedContactModel() {
    }

    public SelectedContactModel(String id, String email, String phone) {
        this.id = id;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
