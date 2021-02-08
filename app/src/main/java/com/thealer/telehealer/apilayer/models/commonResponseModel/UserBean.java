package com.thealer.telehealer.apilayer.models.commonResponseModel;

import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.PaymentInfo;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.thealer.telehealer.common.Constants.ACTIVATION_PENDING;
import static com.thealer.telehealer.common.Constants.AVAILABLE;
import static com.thealer.telehealer.common.Constants.BUSY;
import static com.thealer.telehealer.common.Constants.NO_DATA;
import static com.thealer.telehealer.common.Constants.OFFLINE;

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
    private String status;
    private String phone;
    private String gender;
    private String name;
    private String last_active;
    private boolean secure_message;
    private boolean patient_credit_card_required;
    private PaymentInfo payment_account_info;

    public UserBean() {
    }

    public UserBean(int user_id, String user_guid, String first_name, String last_name, String email, String user_avatar, String role, String dob, String status, String phone, String gender, String name) {
        this.user_id = user_id;
        this.user_guid = user_guid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.user_avatar = user_avatar;
        this.role = role;
        this.dob = dob;
        this.status = status;
        this.phone = phone;
        this.gender = gender;
        this.name = name;
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
            return dob == null ? "" : dob;
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

    public String getUserName(FragmentActivity activity) {

        if (UserDetailPreferenceManager.getWhoAmIResponse().getUser_id() == getUser_id()) {
            return activity.getString(R.string.myself);
        }

        StringBuilder name = new StringBuilder();

        name = name.append(getFirst_name()).append(" ").append(getLast_name());

        return name.toString();
    }


    public String getDisplayName() {
        return getDefaultDisplayName();
    }

    public String getDefaultDisplayName() {
        String displayName = "";
        if (!TextUtils.isEmpty(getFirst_name())) {
            displayName += getFirst_name();
        }

        if (!TextUtils.isEmpty(getLast_name())) {
            if (!TextUtils.isEmpty(displayName)) {
                displayName += " ";
            }
            displayName += getLast_name();
        }

        if (!TextUtils.isEmpty(displayName)) {
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
        }

        return displayName;
    }


    public String getStatus() {
        return status;
    }

    public Boolean isAvailable() {
        return status.equals(Constants.AVAILABLE);
    }

    public int getStatusColorCode() {
        int color = R.color.status_offline;

        switch (getStatus()) {
            case AVAILABLE:
                color = R.color.status_available;
                if (Utils.isOneHourBefore(getLast_active()))
                    color = R.color.status_away;
                break;
            case OFFLINE:
                color = R.color.status_offline;
                break;
            case BUSY:
                color = R.color.status_busy;
                break;
            case ACTIVATION_PENDING:
            case NO_DATA:
                color = R.color.colorBlack;
                break;
        }

        return color;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        if (gender != null && !gender.isEmpty()) {
            return gender.replace(String.valueOf(gender.charAt(0)), String.valueOf(gender.charAt(0)).toUpperCase());
        } else {
            return gender;
        }
    }

    public String getGenderKey() {
        if (gender != null && !gender.isEmpty()) {
            return gender.replace(String.valueOf(gender.charAt(0)), String.valueOf(gender.charAt(0)).toUpperCase());
        } else {
            return gender;
        }
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_active() {
        return last_active;
    }

    public void setLast_active(String last_active) {
        this.last_active = last_active;
    }

    public boolean isSecure_message() {
        return secure_message;
    }

    public void setSecure_message(boolean secure_message) {
        this.secure_message = secure_message;
    }

    public boolean isPatient_credit_card_required() {
        return patient_credit_card_required;
    }

    public void setPatient_credit_card_required(boolean patient_credit_card_required) {
        this.patient_credit_card_required = patient_credit_card_required;
    }

    public PaymentInfo getPayment_account_info() {
        return payment_account_info;
    }

    public void setPayment_account_info(PaymentInfo payment_account_info) {
        this.payment_account_info = payment_account_info;
    }
}
