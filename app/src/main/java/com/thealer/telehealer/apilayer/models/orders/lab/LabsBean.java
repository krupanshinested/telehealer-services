package com.thealer.telehealer.apilayer.models.orders.lab;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class LabsBean implements Serializable {

    private String test_description;
    private boolean stat;
    private List<String> ICD10_codes;

    public String getTest_description() {
        return test_description;
    }

    public void setTest_description(String test_description) {
        this.test_description = test_description;
    }

    public boolean isStat() {
        return stat;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }

    public List<String> getICD10_codes() {
        return ICD10_codes;
    }

    public void setICD10_codes(List<String> ICD10_codes) {
        this.ICD10_codes = ICD10_codes;
    }
}
