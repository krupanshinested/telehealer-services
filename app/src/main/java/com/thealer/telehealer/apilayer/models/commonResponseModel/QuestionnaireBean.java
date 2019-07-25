package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.models.medicalHistory.FamilyHistoryModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.HealthHabitModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.MedicationModel;
import com.thealer.telehealer.apilayer.models.medicalHistory.SexualHistoryModel;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.List;

// FIXME generate failure  field _$FamilyHistory212

/**
 * Created by Aswin on 26,November,2018
 */
public class QuestionnaireBean implements Serializable {

    //Variable

    private MedicationBean Medication;

    @SerializedName("Past Medical History")
    private PastMedicalHistoryBean pastMedicalHistoryBean;

    private SurgeriesBean Surgeries;

    @SerializedName("Family History")
    private FamilyHistoryBean familyHistoryBean;

    @SerializedName("Recent Immunization")
    private CommonItemBean recentImmunizationBean;

    @SerializedName("Sexual History")
    private SexualHistoryBean sexualHistoryBean;

    @SerializedName("Health Habits")
    private HealthHabitBean healthHabitBean;

    @SerializedName("Personal History")
    private PersonalHistoryBean personalHistoryBean;

    public boolean isQuestionariesEmpty() {
        return getMedication() != null ||
                getPastMedicalHistoryBean() != null ||
                getSurgeries() != null ||
                getFamilyHistoryBean() != null ||
                getRecentImmunizationBean() != null ||
                getSexualHistoryBean() != null ||
                getHealthHabitBean() != null ||
                getPersonalHistoryBean() != null;
    }

    public boolean isHistoryEmpty() {
        return getMedication() == null &&
                getPastMedicalHistoryBean() == null &&
                getSurgeries() == null &&
                getFamilyHistoryBean() == null &&
                getRecentImmunizationBean() == null &&
                getSexualHistoryBean() == null &&
                getHealthHabitBean() == null &&
                getPersonalHistoryBean() == null;
    }

    //getter setters
    public MedicationBean getMedication() {
        return Medication;
    }

    public void setMedication(MedicationBean Medication) {
        this.Medication = Medication;
    }

    public PastMedicalHistoryBean getPastMedicalHistoryBean() {
        return pastMedicalHistoryBean;
    }

    public void setPastMedicalHistoryBean(PastMedicalHistoryBean pastMedicalHistoryBean) {
        this.pastMedicalHistoryBean = pastMedicalHistoryBean;
    }

    public SurgeriesBean getSurgeries() {
        return Surgeries;
    }

    public void setSurgeries(SurgeriesBean surgeries) {
        Surgeries = surgeries;
    }

    public FamilyHistoryBean getFamilyHistoryBean() {
        return familyHistoryBean;
    }

    public void setFamilyHistoryBean(FamilyHistoryBean familyHistoryBean) {
        this.familyHistoryBean = familyHistoryBean;
    }

    public CommonItemBean getRecentImmunizationBean() {
        return recentImmunizationBean;
    }

    public void setRecentImmunizationBean(CommonItemBean recentImmunizationBean) {
        this.recentImmunizationBean = recentImmunizationBean;
    }

    public SexualHistoryBean getSexualHistoryBean() {
        return sexualHistoryBean;
    }

    public void setSexualHistoryBean(SexualHistoryBean sexualHistoryBean) {
        this.sexualHistoryBean = sexualHistoryBean;
    }

    public HealthHabitBean getHealthHabitBean() {
        return healthHabitBean;
    }

    public void setHealthHabitBean(HealthHabitBean healthHabitBean) {
        this.healthHabitBean = healthHabitBean;
    }

    public PersonalHistoryBean getPersonalHistoryBean() {
        return personalHistoryBean;
    }

    public void setPersonalHistoryBean(PersonalHistoryBean personalHistoryBean) {
        this.personalHistoryBean = personalHistoryBean;
    }

    // classes

    public static class MedicationBean implements Serializable {
        private List<MedicationModel> items;

        public MedicationBean(List<MedicationModel> items) {
            this.items = items;
        }

        public List<MedicationModel> getItems() {
            return items;
        }

        public void setItems(List<MedicationModel> items) {
            this.items = items;
        }
    }

    public static class PastMedicalHistoryBean extends CommonItemBean implements Serializable {
        private String otherInformation;

        public PastMedicalHistoryBean(String otherInformation, List<MedicalHistoryCommonModel> items) {
            this.otherInformation = Utils.replaceAmpersand(otherInformation);
            this.items = items;
        }

        public String getOtherInformation() {
            return otherInformation;
        }

        public void setOtherInformation(String otherInformation) {
            this.otherInformation = otherInformation;
        }

    }

    public static class SurgeriesBean extends CommonItemBean implements Serializable {
        private String otherInformation;

        public SurgeriesBean(String otherInformation, List<MedicalHistoryCommonModel> items) {
            this.otherInformation = otherInformation;
            this.items = items;
        }

        public String getOtherInformation() {
            return otherInformation;
        }

        public void setOtherInformation(String otherInformation) {
            this.otherInformation = otherInformation;
        }

    }

    public static class FamilyHistoryBean implements Serializable {
        public List<FamilyHistoryModel> items;

        public FamilyHistoryBean(List<FamilyHistoryModel> items) {
            this.items = items;
        }

        public List<FamilyHistoryModel> getItems() {
            return items;
        }

        public void setItems(List<FamilyHistoryModel> items) {
            this.items = items;
        }
    }

    public static class SexualHistoryBean implements Serializable {

        public List<SexualHistoryModel> items;

        public SexualHistoryBean(List<SexualHistoryModel> items) {
            this.items = items;
        }

        public List<SexualHistoryModel> getItems() {
            return items;
        }

        public void setItems(List<SexualHistoryModel> items) {
            this.items = items;
        }
    }

    public static class HealthHabitBean implements Serializable {
        public List<HealthHabitModel> items;

        public HealthHabitBean(List<HealthHabitModel> items) {
            this.items = items;
        }

        public List<HealthHabitModel> getItems() {
            return items;
        }

        public void setItems(List<HealthHabitModel> items) {
            this.items = items;
        }
    }

    public static class PersonalHistoryBean implements Serializable {
        public List<PersonalHistoryModel> items;

        public PersonalHistoryBean(List<PersonalHistoryModel> items) {
            this.items = items;
        }

        public List<PersonalHistoryModel> getItems() {
            return items;
        }

        public void setItems(List<PersonalHistoryModel> items) {
            this.items = items;
        }
    }

    public static class CommonItemBean implements Serializable {

        public List<MedicalHistoryCommonModel> items;

        public CommonItemBean() {
        }

        public CommonItemBean(List<MedicalHistoryCommonModel> items) {
            this.items = items;
        }

        public List<MedicalHistoryCommonModel> getItems() {
            return items;
        }

        public void setItems(List<MedicalHistoryCommonModel> items) {
            this.items = items;
        }

    }
}

