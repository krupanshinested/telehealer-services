package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Aswin on 19,March,2019
 */
public class UserBean extends BaseApiResponseModel implements Serializable {

    private int user_id;
    private String user_guid;
    private String first_name;
    private String last_name;
    private String email;
    private String user_avatar;
    private String role;
    private String dob;

    public UserBean() {
    }

    public UserBean(int user_id, String user_guid, String first_name, String last_name, String email, String user_avatar, String role, String dob) {
        this.user_id = user_id;
        this.user_guid = user_guid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.user_avatar = user_avatar;
        this.role = role;
        this.dob = dob;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDob() {
        if (dob != null && !dob.isEmpty())
            return "DoB : " + dob;
        else
            return dob;
    }

    public String getUnformattedDob() {
        return dob;
    }

    public String getAge() {
        String age = null;
        if (dob != null) {
            DateFormat inputFormat = new SimpleDateFormat("dd MMM, yyyy");
            DateFormat outputFormat = new SimpleDateFormat("yyyy");
            try {
                int year = Integer.parseInt(outputFormat.format(inputFormat.parse(dob)));
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                age = String.valueOf(currentYear - year).concat(" Yrs");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return age;
    }

    public String getUserName() {

        if (UserDetailPreferenceManager.getWhoAmIResponse().getUser_id() == getUser_id()) {
            return "Myself";
        }

        StringBuilder name = new StringBuilder();
        if (getRole().equals(Constants.ROLE_DOCTOR)) {
            name = name.append("Dr. ");
        }

        name = name.append(getFirst_name()).append(" ").append(getLast_name());

        return name.toString();
    }


}
