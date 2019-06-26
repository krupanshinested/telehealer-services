package com.thealer.telehealer.views.home.schedules;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.HistoryBean;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesCreateRequestModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 20,December,2018
 */
public class CreateScheduleViewModel extends ViewModel {

    private CommonUserApiResponseModel patientCommonModel;
    private CommonUserApiResponseModel doctorCommonModel;
    private MutableLiveData<List<String>> timeSlots = new MutableLiveData<>();
    private List<String> unAvaliableTimeSlots = new ArrayList<>();
    private String reasonForVisit;
    private SchedulesCreateRequestModel schedulesCreateRequestModel;
    private List<HistoryBean> patientHistory = new ArrayList<>();

    public List<HistoryBean> getPatientHistory() {
        return patientHistory;
    }

    public void setPatientHistory(List<HistoryBean> patientHistory) {
        this.patientHistory = patientHistory;
    }

    public CommonUserApiResponseModel getPatientCommonModel() {
        return patientCommonModel;
    }

    public void setPatientCommonModel(CommonUserApiResponseModel patientCommonModel) {
        this.patientCommonModel = patientCommonModel;
    }

    public CommonUserApiResponseModel getDoctorCommonModel() {
        return doctorCommonModel;
    }

    public void setDoctorCommonModel(CommonUserApiResponseModel doctorCommonModel) {
        this.doctorCommonModel = doctorCommonModel;
    }

    public MutableLiveData<List<String>> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(MutableLiveData<List<String>> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public SchedulesCreateRequestModel getSchedulesCreateRequestModel() {
        return schedulesCreateRequestModel;
    }

    public List<String> getUnAvaliableTimeSlots() {
        return unAvaliableTimeSlots;
    }

    public void setUnAvaliableTimeSlots(List<String> unAvaliableTimeSlots) {
        this.unAvaliableTimeSlots = unAvaliableTimeSlots;
    }

    public void setSchedulesCreateRequestModel(SchedulesCreateRequestModel schedulesCreateRequestModel) {
        this.schedulesCreateRequestModel = schedulesCreateRequestModel;
    }
}
