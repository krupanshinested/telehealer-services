package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.apilayer.models.createuser.VisitAddressBean;
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
public class CommonUserApiResponseModel extends BaseApiResponseModel implements Serializable {

    private String first_name;
    private String last_name;
    private String status;
    private String email;
    private String user_guid;
    private int user_id;
    private String user_avatar;
    private String website;
    private String role;
    private String phone;
    private String gender;
    private String dob;
    private int appt_length;
    private String name;
    private String connection_status;
    private QuestionnaireBean questionnaire;
    private List<HistoryBean> history;
    private UserDetailBean user_detail;

    public CommonUserApiResponseModel() {
    }

    public CommonUserApiResponseModel(String first_name, String last_name, String status, String email, String user_guid, int user_id,
                                      String user_avatar, String role, String phone, String gender, String dob, int appt_length, String name,
                                      String connection_status, QuestionnaireBean questionnaire, List<HistoryBean> history,
                                      UserDetailBean user_detail) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.status = status;
        this.email = email;
        this.user_guid = user_guid;
        this.user_id = user_id;
        this.user_avatar = user_avatar;
        this.role = role;
        this.phone = phone;
        this.gender = gender;
        this.dob = dob;
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
        if (getRole().equals(Constants.ROLE_PATIENT)) {
            if (getFirst_name() != null && !getFirst_name().isEmpty()) {

                char c = getFirst_name().charAt(0);
                return getFirst_name().replace(c, String.valueOf(c).toUpperCase().charAt(0)) + " " + getLast_name();
            }
        } else if (getRole().equals(Constants.ROLE_DOCTOR)) {
            String title = null;
            if (getUser_detail() != null && getUser_detail().getData() != null
                    && getUser_detail().getData().getTitle() != null && !getUser_detail().getData().getTitle().isEmpty()) {
                title = ", " + getUser_detail().getData().getTitle();
            }
            return Utils.getDoctorDisplayName(getFirst_name(), getLast_name(), title);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(String user_guid) {
        this.user_guid = user_guid;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        if (gender != null && !gender.isEmpty()){
            return gender.replace(String.valueOf(gender.charAt(0)), String.valueOf(gender.charAt(0)).toUpperCase());
        }else {
            return gender;
        }
    }

    public String getGenderKey() {
        if (gender != null && !gender.isEmpty()){
            return gender.replace(String.valueOf(gender.charAt(0)), String.valueOf(gender.charAt(0)).toUpperCase());
        }else {
            return gender;
        }
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public void setDob(String dob) {
        this.dob = dob;
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

}
