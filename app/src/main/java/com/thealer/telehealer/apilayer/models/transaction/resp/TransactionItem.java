package com.thealer.telehealer.apilayer.models.transaction.resp;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

public class TransactionItem extends BaseApiResponseModel {

    @SerializedName("doctor_id")
    private CommonUserApiResponseModel doctorId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("charge_status")
    private int chargeStatus;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("charge_data")
    private List<AddChargeReq.ChargeDataItem> chargeData;

    @SerializedName("patient_id")
    private CommonUserApiResponseModel patientId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private int id;

    @SerializedName("order_id")
    private String orderId;

    private List<RefundItem> refunds;

    @SerializedName("retry_count")
    private int maxRetries;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("payment_mode")
    private int paymentMode;

    private double totalRefund;

    @SerializedName("transactions")
    private List<TransactionRecord> transactionRecord;


    public void setDoctorId(CommonUserApiResponseModel doctorId) {
        this.doctorId = doctorId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setChargeStatus(int chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setChargeData(List<AddChargeReq.ChargeDataItem> chargeData) {
        this.chargeData = chargeData;
    }

    public void setPatientId(CommonUserApiResponseModel patientId) {
        this.patientId = patientId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public CommonUserApiResponseModel getDoctorId() {
        return doctorId;
    }

    public double getAmount() {
        return amount;
    }

    public int getChargeStatus() {
        return chargeStatus;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<AddChargeReq.ChargeDataItem> getChargeData() {
        return chargeData;
    }

    public CommonUserApiResponseModel getPatientId() {
        return patientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getId() {
        return id;
    }


    public String getOrderId() {
        return orderId;
    }

    public String getStatusString() {
        switch (chargeStatus) {
            case Constants
                    .ChargeStatus.CHARGE_ADDED:
                return "Pending Payment";
            case Constants.ChargeStatus.CHARGE_PENDING:
                return "Pending Add charge";
            case Constants.ChargeStatus.CHARGE_PROCESS_INITIATED:
            case Constants.ChargeStatus.CHARGE_PROCESS_IN_STRIPE:
                return "Payment initiated";
            case Constants.ChargeStatus.CHARGE_PROCESS_FAILED:
                return "Payment Failed";
            case Constants.ChargeStatus.CHARGE_PROCESSED:
                return "Payment Success";
            default:
                return "Pending";
        }
    }

    public String getAmountString() {
        return Utils.getFormattedCurrency(getAmount());
    }

    public String getCommaSeparatedReason(Context context) {
        ArrayList<String> reasons = new ArrayList<>();
        if (chargeData != null && chargeData.size() > 0) {
            for (AddChargeReq.ChargeDataItem item : chargeData) {
                reasons.add(item.getReasonString(context));
            }
        }
        return TextUtils.join(",", reasons);
    }


    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public List<RefundItem> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<RefundItem> refunds) {
        this.refunds = refunds;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(double totalRefund) {
        this.totalRefund = totalRefund;
    }

    public List<TransactionRecord> getTransactionRecord() {
        return transactionRecord;
    }

    public void setTransactionRecord(List<TransactionRecord> transactionRecord) {
        this.transactionRecord = transactionRecord;
    }

    public String getTransactionReceipt() {
        if (getTransactionRecord() == null)
            return null;
        if (getTransactionRecord().size() == 0)
            return null;
        for (TransactionRecord record : getTransactionRecord()) {
            if ("succeeded".equals(record.getStatus()))
                return record.getInvoicePath();
        }
        return null;
    }
}