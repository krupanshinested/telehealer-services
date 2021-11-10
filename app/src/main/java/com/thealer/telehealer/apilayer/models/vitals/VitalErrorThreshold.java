package com.thealer.telehealer.apilayer.models.vitals;

/**
 * Created by Nimesh Patel
 * Created Date: 23,July,2021
 **/
public class VitalErrorThreshold {
    int parentPos=-1;
    int itemPos=-1;
    boolean errorEtUpperRight=false;
    boolean errorEtUpper=false;
    boolean errorEtMessage=false;
    boolean errorEtUpperLeft=false;



    public boolean isErrorEtUpperRight() {
        return errorEtUpperRight;
    }

    public int getParentPos() {
        return parentPos;
    }

    public void setParentPos(int parentPos) {
        this.parentPos = parentPos;
    }

    public int getItemPos() {
        return itemPos;
    }

    public void setItemPos(int itemPos) {
        this.itemPos = itemPos;
    }

    public void setErrorEtUpperRight(boolean errorEtUpperRight) {
        this.errorEtUpperRight = errorEtUpperRight;
    }

    public boolean isErrorEtUpper() {
        return errorEtUpper;
    }

    public void setErrorEtUpper(boolean errorEtUpper) {
        this.errorEtUpper = errorEtUpper;
    }

    public boolean isErrorEtMessage() {
        return errorEtMessage;
    }

    public void setErrorEtMessage(boolean errorEtMessage) {
        this.errorEtMessage = errorEtMessage;
    }

    public boolean isErrorEtUpperLeft() {
        return errorEtUpperLeft;
    }

    public void setErrorEtUpperLeft(boolean errorEtUpperLeft) {
        this.errorEtUpperLeft = errorEtUpperLeft;
    }
}