package com.thealer.telehealer.apilayer.models.inviteUser;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteByDemographicRequestModel {

    private String role;
    private String first_name;
    private String last_name;
    private String dob;
    private String gender;

    public InviteByDemographicRequestModel(String role,String first_name, String last_name, String dob, String gender) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.dob = dob;
        this.gender = gender;
        this.role=role;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
