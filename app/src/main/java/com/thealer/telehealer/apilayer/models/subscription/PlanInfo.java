package com.thealer.telehealer.apilayer.models.subscription;

import java.io.Serializable;

/**
 * Created by Nimesh Patel
 * Created Date: 10,April,2021
 **/
public class PlanInfo implements Serializable {
    Boolean isPlanActivated=true;
    String planName="";
    String planPricing="0.0";
    String existingFeatures="";
    String additionalFeatures="See Feature List";
    String FeatureListURL="";
    String freeDesc="";
    String rpmDesc="";
    String btnTitle="";

    public PlanInfo() {
    }

    public Boolean getPlanActivated() {
        return isPlanActivated;
    }

    public void setPlanActivated(Boolean planActivated) {
        isPlanActivated = planActivated;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanPricing() {
        return planPricing;
    }

    public void setPlanPricing(String planPricing) {
        this.planPricing = planPricing;
    }

    public String getExistingFeatures() {
        return existingFeatures;
    }

    public void setExistingFeatures(String existingFeatures) {
        this.existingFeatures = existingFeatures;
    }

    public String getAdditionalFeatures() {
        return additionalFeatures;
    }

    public void setAdditionalFeatures(String additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
    }

    public String getFeatureListURL() {
        return FeatureListURL;
    }

    public void setFeatureListURL(String featureListURL) {
        FeatureListURL = featureListURL;
    }

    public String getFreeDesc() {
        return freeDesc;
    }

    public void setFreeDesc(String freeDesc) {
        this.freeDesc = freeDesc;
    }

    public String getRpmDesc() {
        return rpmDesc;
    }

    public void setRpmDesc(String rpmDesc) {
        this.rpmDesc = rpmDesc;
    }

    public String getBtnTitle() {
        return btnTitle;
    }

    public void setBtnTitle(String btnTitle) {
        this.btnTitle = btnTitle;
    }
}
