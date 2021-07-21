package com.thealer.telehealer.apilayer.models.transaction;

import java.util.Calendar;

public class SingleDateReasonOption extends ReasonOption {

    private Calendar date;

    public SingleDateReasonOption(int value, String title, double fee) {
        super(value, title, fee);
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
