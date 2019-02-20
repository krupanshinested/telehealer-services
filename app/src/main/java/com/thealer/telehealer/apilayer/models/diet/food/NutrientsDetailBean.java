package com.thealer.telehealer.apilayer.models.diet.food;

import java.io.Serializable;

public class NutrientsDetailBean implements Serializable {

    private String label;
    private double quantity;
    private String unit;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
