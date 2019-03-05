package com.thealer.telehealer.apilayer.models.orders.miscellaneous;

import java.io.Serializable;

/**
 * Created by Aswin on 05,March,2019
 */
public class MiscellaneousDetailBean implements Serializable {

    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
