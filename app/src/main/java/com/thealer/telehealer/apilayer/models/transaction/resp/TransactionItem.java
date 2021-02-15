package com.thealer.telehealer.apilayer.models.transaction.resp;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.common.Constants;

public class TransactionItem extends BaseApiResponseModel {

    @SerializedName("doctor_id")
    private CommonUserApiResponseModel doctorId;

    @SerializedName("amount")
    private int amount;

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

    @SerializedName("type_of_charge")
    private int typeOfCharge;

    @SerializedName("order_id")
    private Object orderId;

    public CommonUserApiResponseModel getDoctorId() {
        return doctorId;
    }

    public int getAmount() {
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

    public int getTypeOfCharge() {
        return typeOfCharge;
    }

    public Object getOrderId() {
        return orderId;
    }

    public String getStatusString() {
        switch (chargeStatus) {
            case Constants
                    .ChargeStatus.CHARGE_ADDED:
                return "Pending Payment";
            case Constants.ChargeStatus.CHARGE_PENDING:
                return "Pending Add charge";
            case Constants.ChargeStatus.CHARGE_PROCESS_FAILED:
                return "Payment Failed";
            case Constants.ChargeStatus.CHARGE_PROCESSED:
                return "Payment Success";
            default:
                return "Pending";
        }
    }
}