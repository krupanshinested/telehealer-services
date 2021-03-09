package com.thealer.telehealer.apilayer.models.transaction.resp;

import com.google.gson.annotations.SerializedName;

public class TransactionRecord {


    @SerializedName("invoice_path")
    private String invoicePath;

    @SerializedName("status")
    private String status;

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
