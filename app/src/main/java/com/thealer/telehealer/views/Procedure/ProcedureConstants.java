package com.thealer.telehealer.views.Procedure;

import android.content.Context;

import com.thealer.telehealer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 22,July,2019
 */
public class ProcedureConstants {

    public static final String TELEHEALTH = "99444";
    public static final String CCM_20 = "99490";
    public static final String CCM_30 = "99491";
    public static final String CCM_60 = "99487";
    public static final String CCM_90 = "99489";
    public static final String RPM_20 = "99457";
    public static final String BHI_20 = "99484";
    public static final String NEW_PATIENT_10 = "99201 GT";
    public static final String NEW_PATIENT_20 = "99202 GT";
    public static final String NEW_PATIENT_30 = "99203 GT";
    public static final String NEW_PATIENT_45 = "99204 GT";
    public static final String ESTABLISHED_PATIENT_5 = "99211 GT";
    public static final String ESTABLISHED_PATIENT_10 = "99212 GT";
    public static final String ESTABLISHED_PATIENT_15 = "99213 GT";
    public static final String ESTABLISHED_PATIENT_25 = "99214 GT";
    public static final String ESTABLISHED_PATIENT_40 = "99215 GT";


    public static String getDescription(Context context, String type) {
        switch (type) {
            case TELEHEALTH:
                return context.getString(R.string.telehealth_description);
            case CCM_20:
                return context.getString(R.string.ccm_20_description);
            case CCM_30:
                return context.getString(R.string.ccm_30_description);
            case CCM_60:
                return context.getString(R.string.ccm_60_description);
            case CCM_90:
                return context.getString(R.string.ccm_90_description);
            case BHI_20:
                return context.getString(R.string.bhi_20_description);
            case RPM_20:
                return context.getString(R.string.rpm_20_description);
            case NEW_PATIENT_10:
                return context.getString(R.string.new_patient_10_description);
            case NEW_PATIENT_20:
                return context.getString(R.string.new_patient_20_description);
            case NEW_PATIENT_30:
                return context.getString(R.string.new_patient_30_description);
            case NEW_PATIENT_45:
                return context.getString(R.string.new_patient_45_description);
            case ESTABLISHED_PATIENT_5:
                return context.getString(R.string.established_patient_5_description);
            case ESTABLISHED_PATIENT_10:
                return context.getString(R.string.established_patient_10_description);
            case ESTABLISHED_PATIENT_15:
                return context.getString(R.string.established_patient_15_description);
            case ESTABLISHED_PATIENT_25:
                return context.getString(R.string.established_patient_25_description);
            case ESTABLISHED_PATIENT_40:
                return context.getString(R.string.established_patient_40_description);
            default:
                return null;
        }

    }

    public static String getTitle(Context context, String type) {
        switch (type) {
            case TELEHEALTH:
                return context.getString(R.string.telehealth_title);
            case CCM_20:
                return context.getString(R.string.ccm_20_title);
            case CCM_30:
                return context.getString(R.string.ccm_30_title);
            case CCM_60:
                return context.getString(R.string.ccm_60_title);
            case CCM_90:
                return context.getString(R.string.ccm_90_title);
            case BHI_20:
                return context.getString(R.string.bhi_20_title);
            case RPM_20:
                return context.getString(R.string.rpm_20_title);
            case NEW_PATIENT_10:
                return context.getString(R.string.new_patient_10_title);
            case NEW_PATIENT_20:
                return context.getString(R.string.new_patient_20_title);
            case NEW_PATIENT_30:
                return context.getString(R.string.new_patient_30_title);
            case NEW_PATIENT_45:
                return context.getString(R.string.new_patient_45_title);
            case ESTABLISHED_PATIENT_5:
                return context.getString(R.string.established_patient_5_title);
            case ESTABLISHED_PATIENT_10:
                return context.getString(R.string.established_patient_10_title);
            case ESTABLISHED_PATIENT_15:
                return context.getString(R.string.established_patient_15_title);
            case ESTABLISHED_PATIENT_25:
                return context.getString(R.string.established_patient_25_title);
            case ESTABLISHED_PATIENT_40:
                return context.getString(R.string.established_patient_40_title);
            default:
                return null;
        }
    }

    public static List<String> getItems() {

        return new ArrayList<>(Arrays.asList(TELEHEALTH, CCM_20, CCM_30, CCM_60, CCM_90, BHI_20, RPM_20, NEW_PATIENT_10, NEW_PATIENT_20, NEW_PATIENT_30, NEW_PATIENT_45,
                ESTABLISHED_PATIENT_5, ESTABLISHED_PATIENT_10, ESTABLISHED_PATIENT_15, ESTABLISHED_PATIENT_25, ESTABLISHED_PATIENT_40));
    }


    public static String getCategory(String type) {
        switch (type) {
            case CCM_20:
            case CCM_30:
            case CCM_60:
            case CCM_90:
                return "CCM";
            case BHI_20:
                return "BHI";
            case RPM_20:
                return "RPM";
            case TELEHEALTH:
            case NEW_PATIENT_10:
            case NEW_PATIENT_20:
            case NEW_PATIENT_30:
            case NEW_PATIENT_45:
            case ESTABLISHED_PATIENT_5:
            case ESTABLISHED_PATIENT_10:
            case ESTABLISHED_PATIENT_15:
            case ESTABLISHED_PATIENT_25:
            case ESTABLISHED_PATIENT_40:
            default:
                return null;
        }
    }
}
