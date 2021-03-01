package com.thealer.telehealer.apilayer.models.transaction;

import java.util.Calendar;

public class DateRangeReasonOption extends ReasonOption {

    private Calendar startDate;
    private Calendar endDate;


    public DateRangeReasonOption(int value, String title, double fee) {
        super(value, title, fee);
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }
}
