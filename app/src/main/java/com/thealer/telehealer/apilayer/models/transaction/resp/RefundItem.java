package com.thealer.telehealer.apilayer.models.transaction.resp;

import com.google.gson.annotations.SerializedName;

public class RefundItem {

    private double amount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("invoice_path")
    private String invoicePath;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }
}
