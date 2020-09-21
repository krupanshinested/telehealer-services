package com.thealer.telehealer.apilayer.models.orders;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersCommonResultResponseModel implements Serializable {

    private int referral_id;
    private int referred_by;
    private int referred_for;
    private String name;
    private String path;
    private String status;
    private String created_at;
    private String updated_at;
    private HashMap<String , CommonUserApiResponseModel> userDetailMap = new HashMap<>();
    private DoctorBean doctor;
    private PatientBean patient;
    private MedicalAssistantBean medical_assistant;

    public int getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(int referral_id) {
        this.referral_id = referral_id;
    }

    public int getReferred_by() {
        return referred_by;
    }

    public void setReferred_by(int referred_by) {
        this.referred_by = referred_by;
    }

    public int getReferred_for() {
        return referred_for;
    }

    public void setReferred_for(int referred_for) {
        this.referred_for = referred_for;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
        this.doctor = doctor;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public MedicalAssistantBean getMedical_assistant() {
        return medical_assistant;
    }

    public void setMedical_assistant(MedicalAssistantBean medical_assistant) {
        this.medical_assistant = medical_assistant;
    }
    
    public HashMap<String, CommonUserApiResponseModel> getUserDetailMap() {
        return userDetailMap;
    }

    public void setUserDetailMap(HashMap<String, CommonUserApiResponseModel> userDetailMap) {
        this.userDetailMap = userDetailMap;
    }

    public static class DoctorBean implements Serializable{

        private int user_id;
        private String user_guid;
        private String role;

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class PatientBean implements Serializable{

        private int user_id;
        private String user_guid;
        private String role;

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class MedicalAssistantBean implements Serializable{

        private int user_id;
        private String user_guid;
        private String role;

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
