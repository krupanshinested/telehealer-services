package com.thealer.telehealer.apilayer.models.transaction.req;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AddChargeReq {


    @SerializedName("charge_data")
    private List<ChargeDataItem> chargeData;

    @SerializedName("patient_id")
    private Integer patientId;

    @SerializedName("type_of_charge")
    private int typeOfCharge;

    @SerializedName("order_id")
    private String orderId;

    public List<ChargeDataItem> getChargeData() {
        return chargeData;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public int getTypeOfCharge() {
        return typeOfCharge;
    }

    public void setChargeData(List<ChargeDataItem> chargeData) {
        this.chargeData = chargeData;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public void setTypeOfCharge(int typeOfCharge) {
        this.typeOfCharge = typeOfCharge;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public static class ChargeDataItem {

        @SerializedName("reason")
        private int reason;

        @SerializedName("amount")
        private int amount;

        @SerializedName("description")
        private Description description;

        public int getReason() {
            return reason;
        }

        public int getAmount() {
            return amount;
        }

        public Description getDescription() {
            return description;
        }

        public void setReason(int reason) {
            this.reason = reason;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setDescription(Description description) {
            this.description = description;
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