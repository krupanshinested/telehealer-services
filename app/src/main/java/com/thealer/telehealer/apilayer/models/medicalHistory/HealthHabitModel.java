package com.thealer.telehealer.apilayer.models.medicalHistory;

import androidx.fragment.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants;

import java.io.Serializable;

/**
 * Created by Aswin on 24,January,2019
 */
public class HealthHabitModel extends MedicalHistoryCommonModel implements Serializable {

    private DetailBean detail;

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean implements Serializable {

        private String alcohol_Often;
        private String alcohol_Quantity;
        private String alcohol_Felt_Cut_Down_Drinking;
        private String cigarettes_Per_Day;
        private String otherFormsOfTobacco;
        private String cigarettes_For_How_Many_Years;
        private String Still_Using_Drug;

        public String getAlcohol_Often() {
            return alcohol_Often;
        }

        public void setAlcohol_Often(String alcohol_Often) {
            this.alcohol_Often = Utils.replaceAmpersand(alcohol_Often);
        }

        public String getAlcohol_Quantity() {
            return alcohol_Quantity;
        }

        public void setAlcohol_Quantity(String alcohol_Quantity) {
            this.alcohol_Quantity = Utils.replaceAmpersand(alcohol_Quantity);
        }

        public String getAlcohol_Felt_Cut_Down_Drinking() {
            return alcohol_Felt_Cut_Down_Drinking;
        }

        public void setAlcohol_Felt_Cut_Down_Drinking(String alcohol_Felt_Cut_Down_Drinking) {
            this.alcohol_Felt_Cut_Down_Drinking = Utils.replaceAmpersand(alcohol_Felt_Cut_Down_Drinking);
        }

        public String getCigarettes_Per_Day() {
            return cigarettes_Per_Day;
        }

        public void setCigarettes_Per_Day(String cigarettes_Per_Day) {
            this.cigarettes_Per_Day = Utils.replaceAmpersand(cigarettes_Per_Day);
        }

        public String getOtherFormsOfTobacco() {
            return otherFormsOfTobacco;
        }

        public void setOtherFormsOfTobacco(String otherFormsOfTobacco) {
            this.otherFormsOfTobacco = Utils.replaceAmpersand(otherFormsOfTobacco);
        }

        public String getCigarettes_For_How_Many_Years() {
            return cigarettes_For_How_Many_Years;
        }

        public void setCigarettes_For_How_Many_Years(String cigarettes_For_How_Many_Years) {
            this.cigarettes_For_How_Many_Years = Utils.replaceAmpersand(cigarettes_For_How_Many_Years);
        }

        public String getStill_Using_Drug() {
            return Still_Using_Drug;
        }

        public void setStill_Using_Drug(String Still_Using_Drug) {
            this.Still_Using_Drug = Utils.replaceAmpersand(Still_Using_Drug);
        }
    }

    public String getDetailString(FragmentActivity activity) {
        String detail;
        if (getAdditionalInformation().equals(activity.getString(R.string.yes)) || getAdditionalInformation().equals(activity.getString(R.string.quit))) {
            detail = getAdditionalInformation();

            if (getDetail() != null) {
                if (getTitle().equals(MedicalHistoryConstants.DO_YOU_DRINK)) {

                    if (getDetail().getAlcohol_Often() != null &&
                            !getDetail().getAlcohol_Often().isEmpty()) {
                        detail = detail + "\n" + getDetail().getAlcohol_Often().trim();
                    }

                    if (getDetail().getAlcohol_Quantity() != null &&
                            !getDetail().getAlcohol_Quantity().isEmpty()) {
                        detail = detail + "\n" + getDetail().getAlcohol_Quantity();
                    }

                    if (getDetail().getAlcohol_Felt_Cut_Down_Drinking() != null &&
                            !getDetail().getAlcohol_Felt_Cut_Down_Drinking().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.ever_felt_to_cut_down_drinking) + " : " + getDetail().getAlcohol_Felt_Cut_Down_Drinking();
                    }

                } else if (getTitle().equals(MedicalHistoryConstants.DO_YOU_SMOKE)) {

                    if (getDetail().getCigarettes_Per_Day() != null &&
                            !getDetail().getCigarettes_Per_Day().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.cigarettes_per_day) + " : " + getDetail().getCigarettes_Per_Day();
                    }

                    if (getDetail().getCigarettes_For_How_Many_Years() != null &&
                            !getDetail().getCigarettes_For_How_Many_Years().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.For) + " : " + getDetail().getCigarettes_For_How_Many_Years() + " " + activity.getString(R.string.years);
                    }

                    if (getDetail().getOtherFormsOfTobacco() != null &&
                            !getDetail().getOtherFormsOfTobacco().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.other_tobacco_forms) + " : " + getDetail().getOtherFormsOfTobacco();
                    }

                } else if (getTitle().equals(MedicalHistoryConstants.USED_OTHER_DRUGS)) {

                    if (getDetail().getStill_Using_Drug() != null &&
                            !getDetail().getStill_Using_Drug().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.still_using) + " : " + getDetail().getStill_Using_Drug();
                    }

                }
            }
        } else {
            detail = activity.getString(R.string.no);
        }

        return detail;
    }
}
