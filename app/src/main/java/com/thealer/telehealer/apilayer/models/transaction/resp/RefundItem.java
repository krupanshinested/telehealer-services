package com.thealer.telehealer.apilayer.models.transaction.resp;

import com.google.gson.annotations.SerializedName;

public class RefundItem {

    private int amount;

    @SerializedName("created_at")
    private String createdAt;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
