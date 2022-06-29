package com.thealer.telehealer.apilayer.models.Payments;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rsekar on 1/22/19.
 */

public class Transaction implements Serializable {

    @SerializedName("transaction_id")
    private String id;
    private String created_at;
    private String amount;
    private String status;
    private String invoice_number;
    private String total_price;
    private String date;

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedDate() {
        return Utils.getDateFromPossibleFormat(created_at);
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCreatedMonthYear() {
        return Utils.getStringFromDate(getCreatedDate(), "MMM yyyy");
    }

    public String getCreatedMonthYearWithDash() {
        return Utils.getStringFromDate(getCreatedDate(), "yyyy-MM");
    }
}
