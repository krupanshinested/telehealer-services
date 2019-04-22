package com.thealer.telehealer.apilayer.models.createuser;

import android.arch.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.commonResponseModel.UserDetailBean;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 17,October,2018
 */
public class CreateUserRequestModel extends ViewModel implements Serializable {

    private UserDataBean user_data = new UserDataBean();
    private UserDetailBean user_detail = new UserDetailBean();
    private String user_avatar_path;
    private String insurance_front_path;
    private String insurance_back_path;
    private String secondary_insurance_front_path;
    private String secondary_insurance_back_path;
    private String certification_path;
    private String doctor_driving_license_path;
    private String doctor_certificate_path;
    private String doctor_signature_path;
    private boolean hasValidLicenses = true;
    private List<Boolean> hasValidLicensesList = new ArrayList<>();

    public String getDoctor_signature_path() {
        return doctor_signature_path;
    }

    public void setDoctor_signature_path(String doctor_signature_path) {
        this.doctor_signature_path = doctor_signature_path;
    }


    public void clearData() {
        this.user_data = new UserDataBean();
        this.user_detail = new UserDetailBean();
        this.user_avatar_path = null;
        this.insurance_front_path = null;
        this.insurance_back_path = null;
        this.secondary_insurance_front_path = null;
        this.secondary_insurance_back_path = null;
        this.certification_path = null;
        this.doctor_driving_license_path = null;
        this.doctor_certificate_path = null;
        this.hasValidLicenses = true;
        this.hasValidLicensesList.clear();
    }

    public List<Boolean> getHasValidLicensesList() {
        return hasValidLicensesList;
    }

    public void setHasValidLicensesList(List<Boolean> hasValidLicensesList) {
        this.hasValidLicensesList = hasValidLicensesList;
    }

    public boolean isHasValidLicenses() {
        return hasValidLicenses;
    }

    public void setHasValidLicenses(boolean hasValidLicenses) {
        this.hasValidLicenses = hasValidLicenses;
    }

    public String getDoctor_driving_license_path() {
        return doctor_driving_license_path;
    }

    public void setDoctor_driving_license_path(String doctor_driving_license_path) {
        this.doctor_driving_license_path = doctor_driving_license_path;
    }

    public String getDoctor_certificate_path() {
        return doctor_certificate_path;
    }

    public void setDoctor_certificate_path(String doctor_certificate_path) {
        this.doctor_certificate_path = doctor_certificate_path;
    }

    public UserDataBean getUser_data() {
        return user_data;
    }

    public void setUser_data(UserDataBean user_data) {
        this.user_data = user_data;
    }

    public UserDetailBean getUser_detail() {
        return user_detail;
    }

    public void setUser_detail(UserDetailBean user_detail) {
        this.user_detail = user_detail;
    }

    public String getUser_avatar_path() {
        return user_avatar_path;
    }

    public void setUser_avatar_path(String user_avatar_path) {
        this.user_avatar_path = user_avatar_path;
    }

    public String getInsurance_front_path() {
        return insurance_front_path;
    }

    public void setInsurance_front_path(String insurance_front_path) {
        this.insurance_front_path = insurance_front_path;
    }

    public String getInsurance_back_path() {
        return insurance_back_path;
    }

    public void setInsurance_back_path(String insurance_back_path) {
        this.insurance_back_path = insurance_back_path;
    }

    public String getSecondary_insurance_front_path() {
        return secondary_insurance_front_path;
    }

    public void setSecondary_insurance_front_path(String secondary_insurance_front_path) {
        this.secondary_insurance_front_path = secondary_insurance_front_path;
    }

    public String getSecondary_insurance_back_path() {
        return secondary_insurance_back_path;
    }

    public void setSecondary_insurance_back_path(String secondary_insurance_back_path) {
        this.secondary_insurance_back_path = secondary_insurance_back_path;
    }

    public String getCertification_path() {
        return certification_path;
    }

    public void setCertification_path(String certification_path) {
        this.certification_path = certification_path;
    }

    public boolean isInsurancePresent() {
        return getInsurance_front_path() != null && getInsurance_back_path() != null;
    }

    public boolean isSecondaryInsurancePresent() {
        return getInsurance_front_path() != null && getInsurance_back_path() != null;
    }

    public static class UserDataBean extends ViewModel implements Serializable {

        private String phone;
        private String first_name;
        private String last_name;
        private String password;
        private String user_name;
        private String email;
        private String role;
        private String gender;
        private String dob;
        private String user_avatar;
        private Object appt_length;

        private String status;

        public UserDataBean() {
        }

        public UserDataBean(String phone, String first_name, String last_name,
                            String password, String user_name, String email,
                            String role, String gender, String dob, int appt_length) {
            this.phone = phone;
            this.first_name = first_name;
            this.last_name = last_name;
            this.password = password;
            this.user_name = user_name;
            this.email = email;
            this.role = role;
            this.gender = gender;
            this.dob = dob;
            this.appt_length = appt_length;
        }

        public UserDataBean(WhoAmIApiResponseModel whoAmIApiResponseModel) {
            this.phone = whoAmIApiResponseModel.getPhone();
            this.first_name = whoAmIApiResponseModel.getFirst_name();
            this.last_name = whoAmIApiResponseModel.getLast_name();
            this.password = "";
            this.user_name = whoAmIApiResponseModel.getEmail();
            this.email = whoAmIApiResponseModel.getEmail();
            this.role = whoAmIApiResponseModel.getRole();
            this.gender = whoAmIApiResponseModel.getGender();
            this.dob = whoAmIApiResponseModel.getUnformattedDob();
            this.appt_length = whoAmIApiResponseModel.getAppt_length();
            this.user_avatar = whoAmIApiResponseModel.getUser_avatar();
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = getCasedName(first_name);
        }

        private String getCasedName(String name) {
            if (name.length() > 0) {
                char[] chars = name.toLowerCase().toCharArray();
                chars[0] = Character.toUpperCase(name.charAt(0));
                name = new String(chars);
            }
            return name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = getCasedName(last_name);
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email.trim();
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getGender() {
            return gender;
        }

        public String getGenderKey() {
            if (gender != null && !gender.isEmpty()) {
                return gender.replace(String.valueOf(gender.charAt(0)), String.valueOf(gender.charAt(0)).toUpperCase());
            } else {
                return gender;
            }
        }

        public void setGender(String gender) {
            this.gender = (gender != null) ? gender.toLowerCase() : null;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public int getAppt_length() {
            return Integer.parseInt(appt_length.toString());
        }

        public void setAppt_length(Object appt_length) {
            this.appt_length = appt_length;
        }

        public String getUser_avatar() {
            return user_avatar;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setUser_avatar(String user_avatar) {
            this.user_avatar = user_avatar;
        }
    }
}
