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
    public static final String CCM_30 = "99491";
    public static final String CCM_60 = "99487";
    public static final String CCM_90 = "99489";
    public static final String NEW_PATIENT_10 = "99201 GT";
    public static final String NEW_PATIENT_20 = "99202 GT";
    public static final String NEW_PATIENT_30 = "99203 GT";
    public static final String NEW_PATIENT_45 = "99204 GT";
    public static final String ESTABLISHED_PATIENT_5 = "99211 GT";
    public static final String ESTABLISHED_PATIENT_10 = "99212 GT";
    public static final String ESTABLISHED_PATIENT_15 = "99213 GT";
    public static final String ESTABLISHED_PATIENT_25 = "99214 GT";
    public static final String ESTABLISHED_PATIENT_40 = "99215 GT";


    public static final String CCM_G2065 = "G2065";
    public static final String CCM_99490 = "99490";
    public static final String BHI_99484 = "99484";
    public static final String RPM_99458 = "99458";
    public static final String RPM_99457 = "99457";
    public static final String TELEHEALTH_G2088 = "G2088";
    public static final String TELEHEALTH_G2087 = "G2087";
    public static final String TELEHEALTH_G2086 = "G2086";
    public static final String TELEHEALTH_G2063 = "G2063";
    public static final String TELEHEALTH_G2062 = "G2062";
    public static final String TELEHEALTH_G2061 = "G2061";
    public static final String TELEHEALTH_99423 = "99423";
    public static final String TELEHEALTH_99422 = "99422";
    public static final String TELEHEALTH_99421 = "99421";


    public static String getDescription(Context context, String type) {
        switch (type) {
            case TELEHEALTH:
                return context.getString(R.string.telehealth_description);
            case CCM_30:
                return context.getString(R.string.ccm_30_description);
            case CCM_60:
                return context.getString(R.string.ccm_60_description);
            case CCM_90:
                return context.getString(R.string.ccm_90_description);
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
            case CCM_G2065:
                return context.getString(R.string.ccm_g2065_description);
            case CCM_99490:
                return context.getString(R.string.ccm_99490_description);
            case BHI_99484:
                return context.getString(R.string.bhi_99484_description);
            case RPM_99458:
                return context.getString(R.string.rpm_99458_description);
            case RPM_99457:
                return context.getString(R.string.rpm_99457_description);
            case TELEHEALTH_G2088:
                return context.getString(R.string.g2088_description);
            case TELEHEALTH_G2087:
                return context.getString(R.string.g2087_description);
            case TELEHEALTH_G2086:
                return context.getString(R.string.g2086_description);
            case TELEHEALTH_G2063:
                return context.getString(R.string.g2063_description);
            case TELEHEALTH_G2062:
                return context.getString(R.string.g2062_description);
            case TELEHEALTH_G2061:
                return context.getString(R.string.g2061_description);
            case TELEHEALTH_99423:
                return context.getString(R.string.t_99432_description);
            case TELEHEALTH_99422:
                return context.getString(R.string.t_99422_description);
            case TELEHEALTH_99421:
                return context.getString(R.string.t_99421_description);
            default:
                return null;
        }

    }

    public static String getTitle(Context context, String type) {
        switch (type) {
            case TELEHEALTH:
                return context.getString(R.string.telehealth_title);
            case CCM_30:
                return context.getString(R.string.ccm_30_title);
            case CCM_60:
                return context.getString(R.string.ccm_60_title);
            case CCM_90:
                return context.getString(R.string.ccm_90_title);
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
            case CCM_G2065:
            return context.getString(R.string.ccm_complex);
            case CCM_99490:
            return  context.getString(R.string.ccm_non_complex);
            case BHI_99484:
            return context.getString(R.string.bhi_20);
            case RPM_99458:
            return context.getString(R.string.rpm_20);
            case RPM_99457:
            return context.getString(R.string.rpm_non_add_20);
            case TELEHEALTH_G2088:
            return context.getString(R.string.g2088_title);
            case TELEHEALTH_G2087:
            return context.getString(R.string.g2087_title);
            case TELEHEALTH_G2086:
            return context.getString(R.string.g2086_title);
            case TELEHEALTH_G2063:
            return context.getString(R.string.g2063_title);
            case TELEHEALTH_G2062:
            return context.getString(R.string.g2062_title);
            case TELEHEALTH_G2061:
            return context.getString(R.string.g2061_title);
            case TELEHEALTH_99423:
            return context.getString(R.string.t_99423_title);
            case TELEHEALTH_99422:
            return context.getString(R.string.t_99422_title);
            case TELEHEALTH_99421:
            return context.getString(R.string.t_99421_title);
            default:
                return null;
        }
    }

    public static List<String> getItems() {

        return new ArrayList<>(Arrays.asList(TELEHEALTH_G2088,TELEHEALTH_G2087,TELEHEALTH_G2086,TELEHEALTH_G2063,TELEHEALTH_G2062,TELEHEALTH_G2061,TELEHEALTH_99423,TELEHEALTH_99422,TELEHEALTH_99421,
                RPM_99457,RPM_99458,BHI_99484,CCM_99490,CCM_G2065));
    }


    public static String getCategory(String type) {
        if (type == null)
            return null;

        switch (type) {
            case CCM_99490:
            case CCM_30:
            case CCM_60:
            case CCM_90:
            case CCM_G2065:
                return "CCM";
            case BHI_99484:
                return "BHI";
            case RPM_99458:
            case RPM_99457:
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
