package com.thealer.telehealer.apilayer.models.orders.forms;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;
import com.thealer.telehealer.views.home.orders.OrderStatus;

/**
 * Created by Aswin on 28,November,2018
 */
public class OrdersUserFormsApiResponseModel extends BaseApiResponseModel {

    private int user_form_id;
    private int form_id;
    private String name;
    private String url;
    private String filled_form_url;
    private String status;
    private String order_id;
    private int assigned_to;
    private int assigned_by;
    private Object doctor_id;
    private String created_at;
    private String updated_at;
    private OrdersCommonResultResponseModel.PatientBean patient;
    private OrdersCommonResultResponseModel.DoctorBean doctor;
    private OrdersCommonResultResponseModel.MedicalAssistantBean medical_assistant;
    private DynamicFormDataBean data;

    public String getFilled_form_url() {
        return filled_form_url;
    }

    public void setFilled_form_url(String filled_form_url) {
        this.filled_form_url = filled_form_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getUser_form_id() {
        return user_form_id;
    }

    public void setUser_form_id(int user_form_id) {
        this.user_form_id = user_form_id;
    }

    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(int assigned_to) {
        this.assigned_to = assigned_to;
    }

    public int getAssigned_by() {
        return assigned_by;
    }

    public void setAssigned_by(int assigned_by) {
        this.assigned_by = assigned_by;
    }

    public Object getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Object doctor_id) {
        this.doctor_id = doctor_id;
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

    public OrdersCommonResultResponseModel.PatientBean getPatient() {
        return patient;
    }

    public void setPatient(OrdersCommonResultResponseModel.PatientBean patient) {
        this.patient = patient;
    }

    public OrdersCommonResultResponseModel.DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(OrdersCommonResultResponseModel.DoctorBean doctor) {
        this.doctor = doctor;
    }

    public OrdersCommonResultResponseModel.MedicalAssistantBean getMedical_assistant() {
        return medical_assistant;
    }

    public void setMedical_assistant(OrdersCommonResultResponseModel.MedicalAssistantBean medical_assistant) {
        this.medical_assistant = medical_assistant;
    }

    public DynamicFormDataBean getData() {
        return data;
    }

    public void setData(DynamicFormDataBean data) {
        this.data = data;
    }

    public boolean isCompleted() {
        if (getStatus() == null)
            return false;

        return getStatus().equals(OrderStatus.STATUS_COMPLETED);
    }
}
