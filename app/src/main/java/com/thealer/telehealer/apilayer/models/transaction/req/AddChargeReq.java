package com.thealer.telehealer.apilayer.models.transaction.req;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;

public class AddChargeReq {


    @SerializedName("charge_data")
    private List<ChargeDataItem> chargeData;

    @SerializedName("patient_id")
    private Integer patientId;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("doctor_id")
    private Integer doctorId;

    public List<ChargeDataItem> getChargeData() {
        return chargeData;
    }

    public Integer getPatientId() {
        return patientId;
    }


    public void setChargeData(List<ChargeDataItem> chargeData) {
        this.chargeData = chargeData;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }


    public static class ChargeDataItem {

        @SerializedName("reason")
        private int reason;

        @SerializedName("amount")
        private double amount;

        @SerializedName("description")
        private Description description;

        @SerializedName("type_of_charge_name")
        private String typeOfChargeName;

        @SerializedName("type_of_charge_code")
        private String typeOfChargeCode;


        public int getReason() {
            return reason;
        }

        public double getAmount() {
            return amount;
        }

        public Description getDescription() {
            return description;
        }

        public void setReason(int reason) {
            this.reason = reason;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getReasonString(Context context) {
            switch (reason) {
                case Constants.ChargeReason.VISIT:
                    return context.getString(R.string.visit);
                case Constants.ChargeReason.RPM:
                    return context.getString(R.string.rpm);
                case Constants.ChargeReason.CCM:
                    return context.getString(R.string.ccm);
                case Constants.ChargeReason.BHI:
                    return context.getString(R.string.bhi);
                case Constants.ChargeReason.CONCIERGE:
                    return context.getString(R.string.lbl_concierge);
                case Constants.ChargeReason.SUPPLIES:
                    return context.getString(R.string.lbl_supplies);
                case Constants.ChargeReason.MEDICINE:
                    return context.getString(R.string.lbl_medicine);
            }
            return null;
        }

        public String getTypeOfChargeCode() {
            return typeOfChargeCode;
        }

        public void setTypeOfChargeCode(String typeOfChargeCode) {
            this.typeOfChargeCode = typeOfChargeCode;
        }

        public String getTypeOfChargeName() {
            return typeOfChargeName;
        }

        public void setTypeOfChargeName(String typeOfChargeName) {
            this.typeOfChargeName = typeOfChargeName;
        }
    }

    public static class Description {

        @SerializedName("end_date")
        private String endDate;

        @SerializedName("start_date")
        private String startDate;

        @SerializedName("suppliers")
        private List<String> suppliers;

        @SerializedName("medicines")
        private List<String> medicines;

        @SerializedName("date_of_service")
        private String dateOfService;

        public String getEndDate() {
            return endDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public List<String> getSuppliers() {
            return suppliers;
        }

        public List<String> getMedicines() {
            return medicines;
        }

        public String getDateOfService() {
            return dateOfService;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public void setSuppliers(List<String> suppliers) {
            this.suppliers = suppliers;
        }

        public void setMedicines(List<String> medicines) {
            this.medicines = medicines;
        }

        public void setDateOfService(String dateOfService) {
            this.dateOfService = dateOfService;
        }
    }
}