package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.VisitAddressBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 14,November,2018
 */
public class CommonUserApiResponseModel extends UserBean implements Serializable {

    private int appt_length;
    private String connection_status;
    private QuestionnaireBean questionnaire;
    private List<HistoryBean> history;
    private UserDetailBean user_detail;
    private boolean email_verified;
    private String website;
    private boolean has_abnormal_vitals;
    private Boolean favorite;
    private Boolean connection_requests;

    public CommonUserApiResponseModel() {
    }

    public CommonUserApiResponseModel(String first_name, String last_name, String status, String email, String user_guid, int user_id,
                                      String user_avatar, String role, String phone, String gender, String dob, int appt_length, String name,
                                      String connection_status, QuestionnaireBean questionnaire, List<HistoryBean> history,
                                      UserDetailBean user_detail) {
        super(user_id, user_guid, first_name, last_name, email, user_avatar, role, dob, status, phone, gender, name);
        this.appt_length = appt_length;
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

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
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

    public String getConnection_status() {
        return connection_status;
    }

    public void setConnection_status(String connection_status) {
        this.connection_status = connection_status;
    }

    public boolean isHas_abnormal_vitals() {
        return has_abnormal_vitals;
    }

    public void setHas_abnormal_vitals(boolean has_abnormal_vitals) {
        this.has_abnormal_vitals = has_abnormal_vitals;
    }

    public String getWebsite() {
        return website;
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

    public String getAssistantTitle() {
        if (getUser_detail() != null &&
                getUser_detail().getData() != null &&
                getUser_detail().getData().getTitle() != null) {
            return getUser_detail().getData().getTitle();
        }
        return "";
    }

    public int getSexDrawable() {
        if (getGender().toLowerCase().equals(Constants.male))
            return R.drawable.gender_male;
        else if (getGender().toLowerCase().equals(Constants.female))
            return R.drawable.gender_female;
        else
            return R.drawable.gender_others;
    }

    public Boolean getConnection_requests() {
        return connection_requests;
    }

    public void setConnection_requests(Boolean connection_requests) {
        this.connection_requests = connection_requests;
    }
}
