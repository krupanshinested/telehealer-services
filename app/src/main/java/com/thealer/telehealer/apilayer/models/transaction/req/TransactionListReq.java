package com.thealer.telehealer.apilayer.models.transaction.req;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionListReq {

    @SerializedName("filter")
    private Filter filter;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }

    public static class Filter {

        @SerializedName("patient_id")
        private int patientId;

        @SerializedName("doctor_id")
        private int doctorId;

        @SerializedName("type_of_charge")
        private List<Integer> typeOfCharge;

        @SerializedName("reason")
        private List<Integer> reasons;


        //this fields are not required for api, its only to store state of filter to show in UI
        private String patientName;
        private String doctorName;

        @SerializedName("start_date")
        private String fromDate;

        @SerializedName("end_date")
        private String toDate;

        public void setPatientId(int patientId) {
            this.patientId = patientId;
        }

        public int getPatientId() {
            return patientId;
        }

        public void setTypeOfCharge(List<Integer> typeOfCharge) {
            this.typeOfCharge = typeOfCharge;
        }

        public List<Integer> getTypeOfCharge() {
            return typeOfCharge;
        }

        public int getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(int doctorId) {
            this.doctorId = doctorId;
        }

        public List<Integer> getReasons() {
            return reasons;
        }

        public void setReasons(List<Integer> reasons) {
            this.reasons = reasons;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }
    }
}