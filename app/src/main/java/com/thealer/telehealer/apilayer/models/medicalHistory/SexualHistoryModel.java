package com.thealer.telehealer.apilayer.models.medicalHistory;

import androidx.fragment.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.settings.medicalHistory.MedicalHistoryConstants;

import java.io.Serializable;

/**
 * Created by Aswin on 24,January,2019
 */
public class SexualHistoryModel extends MedicalHistoryCommonModel implements Serializable {

    private DetailBean detail;

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean implements Serializable {

        private String actively_With;
        private String periodsRegular;
        private String periodsStoppedAt;
        private String miscarriages_Count;
        private String children_Count;
        private String abortions_Count;
        private String pregnant_Times;

        public DetailBean() {
        }

        public DetailBean(String actively_With) {
            this.actively_With = actively_With;
        }

        public DetailBean(String periodsRegular, String periodsStoppedAt) {
            setPeriodsRegular(periodsRegular);
            setPeriodsStoppedAt(periodsStoppedAt);
        }

        public DetailBean(String miscarriages_Count, String children_Count, String abortions_Count, String pregnant_Times) {
            setMiscarriages_Count(miscarriages_Count);
            setChildren_Count(children_Count);
            setAbortions_Count(abortions_Count);
            setPregnant_Times(pregnant_Times);
        }

        public String getActively_With() {
            return actively_With;
        }

        public void setActively_With(String actively_With) {
            this.actively_With = Utils.replaceAmpersand(actively_With);
        }

        public String getPeriodsRegular() {
            return periodsRegular;
        }

        public void setPeriodsRegular(String periodsRegular) {
            this.periodsRegular = Utils.replaceAmpersand(periodsRegular);
        }

        public String getPeriodsStoppedAt() {
            return periodsStoppedAt;
        }

        public void setPeriodsStoppedAt(String periodsStoppedAt) {
            this.periodsStoppedAt = Utils.replaceAmpersand(periodsStoppedAt);
        }

        public String getMiscarriages_Count() {
            return miscarriages_Count;
        }

        public void setMiscarriages_Count(String miscarriages_Count) {
            this.miscarriages_Count = Utils.replaceAmpersand(miscarriages_Count);
        }

        public String getChildren_Count() {
            return children_Count;
        }

        public void setChildren_Count(String children_Count) {
            this.children_Count = Utils.replaceAmpersand(children_Count);
        }

        public String getAbortions_Count() {
            return abortions_Count;
        }

        public void setAbortions_Count(String abortions_Count) {
            this.abortions_Count = Utils.replaceAmpersand(abortions_Count);
        }

        public String getPregnant_Times() {
            return pregnant_Times;
        }

        public void setPregnant_Times(String pregnant_Times) {
            this.pregnant_Times = Utils.replaceAmpersand(pregnant_Times);
        }
    }

    public String getDetailString(FragmentActivity activity) {
        String detail;

        if (getAdditionalInformation().equals(activity.getString(R.string.no))) {
            detail = activity.getString(R.string.no);
        } else {
            detail = activity.getString(R.string.yes);

            if (getDetail() != null) {
                if (getTitle().equals(MedicalHistoryConstants.SEXUALLY_ACTIVE)) {
                    if (getDetail().getActively_With() != null &&
                            !getDetail().getActively_With().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.actively_with) + " : " + getDetail().getActively_With();
                    }
                } else if (getTitle().equals(MedicalHistoryConstants.MENSURAL_PERIODS)) {
                    if (getDetail().getPeriodsStoppedAt() != null &&
                            !getDetail().getPeriodsStoppedAt().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.periods_stopped_at) + " : " + getDetail().getPeriodsStoppedAt();
                    }

                    if (getDetail().getPeriodsRegular() != null &&
                            !getDetail().getPeriodsRegular().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.is_periods_regular) + " : " + getDetail().getPeriodsRegular();
                    }

                } else if (getTitle().equals(MedicalHistoryConstants.EVER_BEEN_PREGNANT)) {
                    if (getDetail().getAbortions_Count() != null &&
                            !getDetail().getAbortions_Count().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.no_of_abortions_times) + " : " + getDetail().getAbortions_Count();
                    }
                    if (getDetail().getPregnant_Times() != null &&
                            !getDetail().getPregnant_Times().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.no_of_pregnant_times) + " : " + getDetail().getPregnant_Times();
                    }
                    if (getDetail().getChildren_Count() != null &&
                            !getDetail().getChildren_Count().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.no_of_childrens_living) + " : " + getDetail().getChildren_Count();
                    }
                    if (getDetail().getMiscarriages_Count() != null &&
                            !getDetail().getMiscarriages_Count().trim().isEmpty()) {
                        detail = detail + "\n" + activity.getString(R.string.no_of_miscarriage_times) + " : " + getDetail().getMiscarriages_Count();
                    }

                }
            }
        }
        return detail;
    }
}
