package com.thealer.telehealer.common.VitalCommon;

import android.content.Context;

import com.thealer.telehealer.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Aswin on 27,November,2018
 */
public class VitalsConstant {

    public static final String INPUT_SYSTOLE = "Systolic";
    public static final String INPUT_DIASTOLE = "Diastolic";
    public static final String INPUT_SYSTOLE_HINT = "Systolic (Top number)";
    public static final String INPUT_DIASTOLE_HINT = "Diastolic (Bottom number)";
    public static final String INPUT_GLUCOSE = "Blood Glucose";
    public static final String INPUT_PULSE = "Pulse";
    public static final String INPUT_SPO2 = "Spo2";
    public static final String INPUT_TEMPERATURE = "Temperature";
    public static final String INPUT_WEIGHT = "Weight";
    public static final String INPUT_FEET = "Feet";
    public static final String INPUT_INCHES = "Inches";

    public static final String SYSTOLE = "Systolic";
    public static final String DIASTOLE = "Diastolic";

    public static final String VITAL_MODE_DOCTOR = "doctor";
    public static final String VITAL_MODE_PATIENT = "patient";
    public static final String VITAL_MODE_DEVICE = "automated";

    public static final int LABLE_DOCTOR = R.string.doctor;
    public static final int LABLE_PATIENT = R.string.patient;
    public static final int LABLE_DEVICE = R.string.device;
    public static final int LABLE_AUTOMATED = R.string.automatic;
    public static final int LABLE_MANUAL = R.string.manual;

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


    public static final String clear_key = "iHealth Clear";
    public static final String air_key = "iHealth Air";
    public static final String track_key = "iHealth Track";
    public static final String core_key = "iHealth Core";
    public static final String sense_key = "iHealth Sense";
    public static final String lite_key = "iHealth Lite";
    public static final String ease_key = "iHealth Ease";
    public static final String feel_key = "iHealth Feel";
    public static final String smart_key = "iHealth Smart";
    public static final String view_key = "iHealth View";
    public static final String thv3_key = "iHealth Thermometer FDIR";
    public static final String ts28_key = "iHealth Thermometer TS28";
    public static final String lina_key = "iHealth Lina";

    public static final String heart = "heart";
    public static final String lung = "lung";

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
            case INPUT_FEET:
                return 10;
            case INPUT_INCHES:
                return 11;
        }
        return 0;
    }

    public static int getMinRange(String inputType) {
        switch (inputType) {
            case INPUT_INCHES:
            case INPUT_SYSTOLE:
            case INPUT_DIASTOLE:
            case INPUT_GLUCOSE:
            case INPUT_PULSE:
            case INPUT_SPO2:
            case INPUT_TEMPERATURE:
            case INPUT_WEIGHT:
            case INPUT_FEET:
            default:
                return 0;
        }
    }

    public static CharSequence getInputError(Context context, String inputType) {
        String value = context.getString(R.string.vital_input_error);
        return String.format(value, inputType, getMinRange(inputType), getMaxRange(inputType));
    }


    static public class VitalCallMapKeys {
        public static final String status = "status";
        //status
        public static final String measuring = "measuring";
        public static final String startedToMeasure = "startedToMeasure";
        public static final String finishedMeasure = "finishedMeasure";
        public static final String errorInMeasure = "errorInMeasure";
        public static final String stripInserted = "stripInserted";
        public static final String bloodDropped = "bloodDropped";
        public static final String cancelled = "cancelled";

        public static final String message = "message";
        public static final String data = "data";

        //vitals value
        public static final String spo2 = "spo2";
        public static final String wave = "wave";
        public static final String pi = "pi";
        public static final String bpm = "bpm";
        public static final String systolicValue = "systolicValue";
        public static final String diastolicValue = "diastolicValue";
        public static final String heartRate = "heartRate";

    }

    public static ArrayList<String> getVitalPrintOptions(Context context) {
        return new ArrayList<>(Arrays.asList(context.getString(R.string.PRINT_1_WEEK), context.getString(R.string.PRINT_2_WEEK), context.getString(R.string.PRINT_1_MONTH), context.getString(R.string.custom_range)));
    }
}
