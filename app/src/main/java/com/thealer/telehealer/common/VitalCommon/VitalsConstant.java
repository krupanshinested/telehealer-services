package com.thealer.telehealer.common.VitalCommon;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Aswin on 27,November,2018
 */
public class VitalsConstant {

    public static final String INPUT_SYSTOLE = "SYSTOLE";
    public static final String INPUT_DIASTOLE = "DIASTOLE";
    public static final String INPUT_GLUCOSE = "BLOOD GLUCOSE";
    public static final String INPUT_PULSE = "PULSE";
    public static final String INPUT_SPO2 = "SPO2";
    public static final String INPUT_TEMPERATURE = "TEMPERATURE";
    public static final String INPUT_WEIGHT = "WEIGHT";

    public static final String VITAL_MODE_DOCTOR = "doctor";
    public static final String VITAL_MODE_PATIENT = "patient";
    public static final String VITAL_MODE_DEVICE = "automated";

    public static final String LABLE_DOCTOR = "Doctor";
    public static final String LABLE_PATIENT = "Patient";
    public static final String LABLE_DEVICE = "Device";
    public static final String LABLE_AUTOMATED = "Automatic";
    public static final String LABLE_MANUAL = "Manual";

    public static final String PRINT_1_WEEK = "Last 1 weeks";
    public static final String PRINT_2_WEEK = "Last 2 weeks";
    public static final String PRINT_1_MONTH = "Last 1 Month";
    public static final String PRINT_ALL = "All Vital History";
    public static final ArrayList<String> vitalPrintOptions = new ArrayList<>(Arrays.asList(PRINT_1_WEEK, PRINT_2_WEEK, PRINT_1_MONTH, PRINT_ALL));
    public static final int locationService = 2;

    public static final String TYPE_BP3M = "BP3M";
    public static final String TYPE_BP3L = "BP3L";
    public static final String TYPE_BP5 = "BP5";
    public static final String TYPE_BP5S = "BP5S";
    public static final String TYPE_BP7 = "BP7";
    public static final String TYPE_BP7S = "BP7S";
    public static final String TYPE_550BT = "KN-550BT";
    public static final String TYPE_KD926 = "KD-926";
    public static final String TYPE_KD723 = "KD-723";
    public static final String TYPE_ABPM = "ABP100";
    public static final String TYPE_BG1 = "BG1";
    public static final String TYPE_BG5 = "BG5";
    public static final String TYPE_HS3 = "HS3";
    public static final String TYPE_HS4 = "HS4";
    public static final String TYPE_HS4S = "HS4S";
    public static final String TYPE_HS5 = "HS5";
    public static final String TYPE_HS5S = "HS5S";
    public static final String TYPE_HS6 = "HS6";
    public static final String TYPE_AM3 = "AM3";
    public static final String TYPE_AM3S = "AM3S";
    public static final String TYPE_AM4 = "AM4";
    public static final String TYPE_PO3 = "PO3";
    public static final String TYPE_HS5_BT = "HS5BT";
    public static final String TYPE_FDIR_V3 = "FDIR-V3";
    public static final String TYPE_CBP = "CBP";
    public static final String TYPE_BG5S = "BG5S";
    public static final String TYPE_CBG = "CBG";
    public static final String TYPE_CPO = "CPO";
    public static final String TYPE_CHS = "CHS";
    public static final String TYPE_CBS = "CBS";
    public static final String TYPE_BPM1 = "BPM1";
    public static final String TYPE_HS2 = "HS2";
    public static final String TYPE_TS28B = "TS28B";
    public static final String TYPE_ECG3 = "ECG3";
    public static final String TYPE_ECG3_USB = "ECGUSB";

    public static int getMaxRange(String inputType) {
        switch (inputType) {
            case INPUT_SYSTOLE:
                return 300;
            case INPUT_DIASTOLE:
                return 250;
            case INPUT_GLUCOSE:
                return 1000;
            case INPUT_PULSE:
                return 150;
            case INPUT_SPO2:
                return 100;
            case INPUT_TEMPERATURE:
                return 212;
            case INPUT_WEIGHT:
                return 1500;
        }
        return 0;
    }

    public static int getMinRange(String inputType) {
        switch (inputType) {
            case INPUT_SYSTOLE:
            case INPUT_DIASTOLE:
            case INPUT_GLUCOSE:
            case INPUT_PULSE:
            case INPUT_SPO2:
            case INPUT_TEMPERATURE:
            case INPUT_WEIGHT:
            default:
                return 0;
        }
    }

    public static CharSequence getInputError(String inputType) {
        String value = " value should be in range of ";
        return inputType + value + getMinRange(inputType) + "-" + getMaxRange(inputType);
    }
}
