package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;

/**
 * Created by Aswin on 24,January,2019
 */
public class PersonalHistoryModel extends MedicalHistoryCommonModel {

    private String selectedOption;

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getDetailString() {
        String detail = "";

        if (getSelectedOption() != null && !getSelectedOption().isEmpty()) {
            detail = detail.concat(getSelectedOption());
        }

        if (getAdditionalInformation() != null && !getAdditionalInformation().isEmpty()) {

            if (!detail.isEmpty())
                detail = detail.concat("\n" + getAdditionalInformation());
            else
                detail = getAdditionalInformation();

        }
        return detail;
    }
}
