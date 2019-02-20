package com.thealer.telehealer.apilayer.models.diet;

import java.io.Serializable;

/**
 * Created by Aswin on 14,March,2019
 */
public class NutrientsDetailModel implements Serializable {

    private double daily;
    private String label;
    private String tag;
    private double total;
    private boolean hasRDI;
    private String unit;
    private boolean isSubData;

    public NutrientsDetailModel() {
    }

    public NutrientsDetailModel(double daily, String label, String tag, double total, boolean hasRDI, String unit) {
        this.daily = daily;
        this.label = label;
        this.tag = tag;
        this.total = total;
        this.hasRDI = hasRDI;
        this.unit = unit;
    }

    public double getDaily() {
        return daily;
    }

    public void setDaily(double daily) {
        this.daily = daily;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isHasRDI() {
        return hasRDI;
    }

    public void setHasRDI(boolean hasRDI) {
        this.hasRDI = hasRDI;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isSubData() {
        return isSubData;
    }

    public void setSubData(boolean subData) {
        isSubData = subData;
    }
}

