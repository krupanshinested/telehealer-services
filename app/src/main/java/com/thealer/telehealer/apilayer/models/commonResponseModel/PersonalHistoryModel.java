package com.thealer.telehealer.apilayer.models.commonResponseModel;

import com.thealer.telehealer.apilayer.models.medicalHistory.MedicalHistoryCommonModel;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;

/**
 * Created by Aswin on 24,January,2019
 */
public class PersonalHistoryModel extends MedicalHistoryCommonModel implements Serializable {

    private String selectedOption;

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = Utils.replaceAmpersand(selectedOption);
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
