package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.VisitAddressBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aswin on 14,November,2018
 */
public class CommonUserApiResponseModel extends UserBean implements Serializable {

    private String status;
    private String phone;
    private String gender;
    private int appt_length;
    private String name;
    private String connection_status;
    private QuestionnaireBean questionnaire;
    private List<HistoryBean> history;
    private UserDetailBean user_detail;
    private boolean email_verified;
    private String website;

    public CommonUserApiResponseModel() {
    }

    public CommonUserApiResponseModel(String first_name, String last_name, String status, String email, String user_guid, int user_id,
                                      String user_avatar, String role, String phone, String gender, String dob, int appt_length, String name,
                                      String connection_status, QuestionnaireBean questionnaire, List<HistoryBean> history,
                                      UserDetailBean user_detail) {
        super(user_id, user_guid, first_name, last_name, email, user_avatar, role, dob);
        this.status = status;
        this.phone = phone;
        this.gender = gender;
        this.appt_length = appt_length;
        this.name = name;
        this.connection_status = connection_status;
        this.questionnaire = questionnaire;
        this.history = history;
        this.user_detail = user_detail;
    }

    public String getDoctorDisplayName() {
        String title = null;
        if (getUser_detail() != null && getUser_detail().getData() != null
                && getUser_detail().getData().getTitle() != null && !getUser_detail().getData().getTitle().isEmpty()) {
            title = ", " + getUser_detail().getData().getTitle();
        }
        return Utils.getDoctorDisplayName(getFirst_name(), getLast_name(), title);
    }


    public String getDoctorSpecialist() {
        if (getUser_detail() != null
                && getUser_detail().getData() != null
                && getUser_detail().getData().getSpecialties() != null
                && getUser_detail().getData().getSpecialties().size() > 0) {
            return getUser_detail().getData().getSpecialties().get(0).getName();
        }
        return "";
    }

    public String getUserDisplay_name() {
        if (getRole().equals(Constants.ROLE_DOCTOR)) {
            String title = null;
            if (getUser_detail() != null && getUser_detail().getData() != null
                    && getUser_detail().getData().getTitle() != null && !getUser_detail().getData().getTitle().isEmpty()) {
                title = ", " + getUser_detail().getData().getTitle();
            }
            return Utils.getDoctorDisplayName(getFirst_name(), getLast_name(), title);
        } else {
            if (getFirst_name() != null && !getFirst_name().isEmpty()) {

                return getFirst_name().substring(0, 1).toUpperCase() + getFirst_name().substring(1) + " " + getLast_name();
            }
        }
        return "";
    }

    public String getDoctorAddress() {

        if (getUser_detail() != null &&
                getUser_detail().getData() != null &&
                getUser_detail().getData().getPractices().size() > 0 &&
                getUser_detail().getData().getPractices().get(0).getVisit_address() != null) {
            VisitAddressBean visitAddressBean = getUser_detail().getData().getPractices().get(0).getVisit_address();

            return visitAddressBean.getStreet() + "," + visitAddressBean.getCity() + "," + visitAddressBean.getState() + "," + visitAddressBean.getZip();

        } else {
            return "";
        }
    }

    public String getDoctorPhone() {
        if (getUser_detail() != null &&
                getUser_detail().getData() != null &&
                getUser_detail().getData().getPractices() != null &&
                getUser_detail().getData().getPractices().size() > 0 &&
                getUser_detail().getData().getPractices().get(0).getPhones() != null &&
                getUser_detail().getData().getPractices().get(0).getPhones().size() > 0) {

            return getUser_detail().getData().getPractices().get(0).getPhones().get(0).getNumber();

        } else {
            return "";
        }
    }

    public String getDoctorNpi() {

        if (getUser_detail() != null &&
                getUser_detail().getData() != null) {
            return getUser_detail().getData().getNpi();
        } else {
            return "";
        }
    }

    public String getDisplayName() {
        switch (getRole()) {
            case Constants.ROLE_DOCTOR:
                return getDoctorDisplayName();
            default:
                return getUserDisplay_name();
        }
    }

    public String getDisplayInfo() {
        switch (getRole()) {
            case Constants.ROLE_DOCTOR:
                return getDoctorSpecialist();
            case Constants.ROLE_PATIENT:
                return getDob();
            case Constants.ROLE_ASSISTANT:
                if (getUser_detail() != null && getUser_detail().getData() != null && getUser_detail().getData().getTitle() != null)
                    return getUser_detail().getData().getTitle().toUpperCase();
                else
                    return "";
            default:
                return "";
        }
    }

    public QuestionnaireBean getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireBean questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<HistoryBean> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryBean> history) {
        this.history = history;
    }

    public String getStatus() {
        return status;
    }

    public Boolean isAvailable() {
        return status.equals(Constants.AVAILABLE);
    }

    public int getStatusColorCode() {
        switch (status) {
            case Constants.AVAILABLE:
                return R.drawable.ic_status_12dp;
            case Constants.BUSY:
                return R.drawable.ic_busy_status;
            case Constants.OFFLINE:
                return R.drawable.ic_offline_status;
            default:
                return R.drawable.ic_offline_status;
        }
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

    public int getAppt_length() {
        return appt_length;
    }

    public void setAppt_length(int appt_length) {
        this.appt_length = appt_length;
    }

    public UserDetailBean getUser_detail() {
        return user_detail;
    }

    public void setUser_detail(UserDetailBean user_detail) {
        this.user_detail = user_detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }

    public String getWebsite() {
        return website;
    }

    public String getAssistantTitle() {
        if (getUser_detail() != null &&
                getUser_detail().getData() != null &&
                getUser_detail().getData().getTitle() != null) {
            return getUser_detail().getData().getTitle();
        }
        return "";
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public int getSexDrawable() {
        if (getGender().toLowerCase().equals(Constants.male))
            return R.drawable.gender_male;
        else if (getGender().toLowerCase().equals(Constants.female))
            return R.drawable.gender_female;
        else
            return R.drawable.gender_others;
    }
}
