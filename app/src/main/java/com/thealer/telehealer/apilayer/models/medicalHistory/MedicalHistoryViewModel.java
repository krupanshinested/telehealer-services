package com.thealer.telehealer.apilayer.models.medicalHistory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.commonResponseModel.PersonalHistoryModel;

import java.util.List;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicalHistoryViewModel extends ViewModel {
    MutableLiveData<List<MedicationModel>> medicationListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<MedicalHistoryCommonModel>> pastMedicalHistoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<MedicalHistoryCommonModel>> surgeriesMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<FamilyHistoryModel>> familyHistoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<MedicalHistoryCommonModel>> recentImmunizationMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<SexualHistoryModel>> sexualHistoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<HealthHabitModel>> HealthHabitMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<PersonalHistoryModel>> personalHistoryMutableLiveData = new MutableLiveData<>();
    String OtherInformation;

    public MutableLiveData<List<MedicationModel>> getMedicationListMutableLiveData() {
        return medicationListMutableLiveData;
    }

    public void setMedicationListMutableLiveData(MutableLiveData<List<MedicationModel>> medicationListMutableLiveData) {
        this.medicationListMutableLiveData = medicationListMutableLiveData;
    }

    public MutableLiveData<List<MedicalHistoryCommonModel>> getPastMedicalHistoryMutableLiveData() {
        return pastMedicalHistoryMutableLiveData;
    }

    public void setPastMedicalHistoryMutableLiveData(MutableLiveData<List<MedicalHistoryCommonModel>> pastMedicalHistoryMutableLiveData) {
        this.pastMedicalHistoryMutableLiveData = pastMedicalHistoryMutableLiveData;
    }

    public MutableLiveData<List<MedicalHistoryCommonModel>> getSurgeriesMutableLiveData() {
        return surgeriesMutableLiveData;
    }

    public void setSurgeriesMutableLiveData(MutableLiveData<List<MedicalHistoryCommonModel>> surgeriesMutableLiveData) {
        this.surgeriesMutableLiveData = surgeriesMutableLiveData;
    }

    public MutableLiveData<List<FamilyHistoryModel>> getFamilyHistoryMutableLiveData() {
        return familyHistoryMutableLiveData;
    }

    public void setFamilyHistoryMutableLiveData(MutableLiveData<List<FamilyHistoryModel>> familyHistoryMutableLiveData) {
        this.familyHistoryMutableLiveData = familyHistoryMutableLiveData;
    }

    public String getOtherInformation() {
        return OtherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        OtherInformation = otherInformation;
    }

    public MutableLiveData<List<MedicalHistoryCommonModel>> getRecentImmunizationMutableLiveData() {
        return recentImmunizationMutableLiveData;
    }

    public void setRecentImmunizationMutableLiveData(MutableLiveData<List<MedicalHistoryCommonModel>> recentImmunizationMutableLiveData) {
        this.recentImmunizationMutableLiveData = recentImmunizationMutableLiveData;
    }

    public MutableLiveData<List<SexualHistoryModel>> getSexualHistoryMutableLiveData() {
        return sexualHistoryMutableLiveData;
    }

    public void setSexualHistoryMutableLiveData(MutableLiveData<List<SexualHistoryModel>> sexualHistoryMutableLiveData) {
        this.sexualHistoryMutableLiveData = sexualHistoryMutableLiveData;
    }

    public MutableLiveData<List<HealthHabitModel>> getHealthHabitMutableLiveData() {
        return HealthHabitMutableLiveData;
    }

    public void setHealthHabitMutableLiveData(MutableLiveData<List<HealthHabitModel>> healthHabitMutableLiveData) {
        HealthHabitMutableLiveData = healthHabitMutableLiveData;
    }

    public MutableLiveData<List<PersonalHistoryModel>> getPersonalHistoryMutableLiveData() {
        return personalHistoryMutableLiveData;
    }

    public void setPersonalHistoryMutableLiveData(MutableLiveData<List<PersonalHistoryModel>> personalHistoryMutableLiveData) {
        this.personalHistoryMutableLiveData = personalHistoryMutableLiveData;
    }

    //for common
    MutableLiveData<Boolean> onActionLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getOnActionLiveData() {
        return onActionLiveData;
    }

    public void setOnActionLiveData(MutableLiveData<Boolean> onActionLiveData) {
        this.onActionLiveData = onActionLiveData;
    }


    //for relation selection
    MutableLiveData<List<String>> selectedRelationsLiveData = new MutableLiveData<>();
    boolean isDeselected = false;

    public MutableLiveData<List<String>> getSelectedRelationsLiveData() {
        return selectedRelationsLiveData;
    }

    public void setSelectedRelationsLiveData(MutableLiveData<List<String>> selectedRelationsLiveData) {
        this.selectedRelationsLiveData = selectedRelationsLiveData;
    }

    public boolean isDeselected() {
        return isDeselected;
    }

    public void setDeselected(boolean deselected) {
        isDeselected = deselected;
    }

    //for options selection
    String selectedOption;

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
