package com.thealer.telehealer.apilayer.models.medicalHistory;

import com.thealer.telehealer.common.Utils;

import java.io.Serializable;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicationModel implements Serializable {

    private String directionType2;
    private String direction;
    private String unit;
    private String strength;
    private String drugName;
    private String directionType1;

    public String getDirectionType2() {
        return directionType2;
    }

    public void setDirectionType2(String directionType2) {
        this.directionType2 = Utils.replaceAmpersand(directionType2);
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = Utils.replaceAmpersand(direction);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = Utils.replaceAmpersand(unit);
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = Utils.replaceAmpersand(strength);
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = Utils.replaceAmpersand(drugName);
    }

    public String getDirectionType1() {
        return directionType1;
    }

    public void setDirectionType1(String directionType1) {
        this.directionType1 = Utils.replaceAmpersand(directionType1);
    }
}
