package com.thealer.telehealer.apilayer.models.transaction;

public class DetailAmountModel {

    private String title;
    private String details;
    private double amount;
    private boolean showReceipt;
    private String receiptURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getAmount() {
        return amount;
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isShowReceipt() {
        return showReceipt;
    }

    public void setShowReceipt(boolean showReceipt) {
        this.showReceipt = showReceipt;
    }

    public String getReceiptURL() {
        return receiptURL;
    }

    public void setReceiptURL(String receiptURL) {
        this.receiptURL = receiptURL;
    }
}
