package com.thealer.telehealer.apilayer.models.transaction;

import android.text.Editable;
import android.text.TextWatcher;

public class ReasonOption {

    private String title;

    private int value;

    private int fee;

    private boolean isSelected;

    public ReasonOption(int value, String title, int fee) {
        this.value = value;
        this.title = title;
        this.fee = fee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
