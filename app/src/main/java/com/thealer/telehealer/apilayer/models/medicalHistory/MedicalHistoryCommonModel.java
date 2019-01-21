package com.thealer.telehealer.apilayer.models.medicalHistory;

import java.io.Serializable;

/**
 * Created by Aswin on 23,January,2019
 */
public class MedicalHistoryCommonModel implements Serializable {

    private String additionalInformation;
    private String title;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
