package com.thealer.telehealer.apilayer.models.transaction.req;

import com.google.gson.annotations.SerializedName;

public class RefundReq {

    @SerializedName("refund_amount")
    private int refundAmount;

    public int getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(int refundAmount) {
        this.refundAmount = refundAmount;
    }
}
