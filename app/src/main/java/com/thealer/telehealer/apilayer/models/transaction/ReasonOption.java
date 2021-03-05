package com.thealer.telehealer.apilayer.models.transaction;

import android.text.Editable;
import android.text.TextWatcher;

public class ReasonOption {

    private String title;

    private int value;

    private double fee;

    private boolean isSelected;

    private String chargeTypeCode;
    private String chargeTypeName;

    private boolean disableSelection;

    public ReasonOption(int value, String title, double fee) {
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

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getChargeTypeName() {
        return chargeTypeName;
    }

    public void setChargeTypeName(String chargeTypeName) {
        this.chargeTypeName = chargeTypeName;
    }

    public String getChargeTypeCode() {
        return chargeTypeCode;
    }

    public void setChargeTypeCode(String chargeTypeCode) {
        this.chargeTypeCode = chargeTypeCode;
    }

    public boolean isDisableSelection() {
        return disableSelection;
    }

    public void setDisableSelection(boolean disableSelection) {
        this.disableSelection = disableSelection;
    }
}
