package com.thealer.telehealer.apilayer.models.transaction.req;

import com.google.gson.annotations.SerializedName;

public class RefundReq {

    @SerializedName("refund_amount")
    private double refundAmount;

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
}
