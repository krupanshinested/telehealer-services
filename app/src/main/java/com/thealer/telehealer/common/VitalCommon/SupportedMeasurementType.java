package com.thealer.telehealer.common.VitalCommon;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 11/27/18.
 */

public class SupportedMeasurementType {
    public static final String bpHeart = "bp,heartRate";
    public static final String bp = "bp";
    public static final String weight = "weight";
    public static final String temperature = "temperature";
    public static final String gulcose = "gulcose";
    public static final String pulseOximeter = "pulseOximeter";
    public static final String heartRate = "heartRate"; // ony used to save the heart rate which is calculated from bp or pulse machine
    public static final String stethoscope = "stethoscope";


    public static int getTitle(String type) {
        switch (type) {
            case bpHeart:
                return R.string.blood_pressure_heart_rate;
            case bp:
                return R.string.blood_pressure;
            case weight:
                return R.string.weight;
            case temperature:
                return R.string.bodyTemperature;
            case gulcose:
                return R.string.bloodGlucose;
            case pulseOximeter:
                return R.string.pulseOximeter;
            case heartRate:
                return R.string.heartRate;
            case stethoscope:
                return R.string.stethoscope;
            default:
                return R.string.blood_pressure;
        }
    }

    public static int getDrawable(String type) {
        switch (type) {
            case bp:
            case bpHeart:
                return R.drawable.ic_vitals_bp;
            case weight:
                return R.drawable.ic_vitals_weight;
            case temperature:
                return R.drawable.ic_vitals_temperature;
            case gulcose:
                return R.drawable.ic_vitals_glucose;
            case pulseOximeter:
                return R.drawable.ic_vitals_pulse;
            case heartRate:
                return R.drawable.ic_vitals_heart;
            case stethoscope:
                return R.drawable.ic_vitals_stethio;
            default:
                return 0;
        }
    }

    public static String getVitalUnit(String supportedMeasurementType) {
        switch (supportedMeasurementType) {
            case bp:
            case bpHeart:
                return "mmHg";
            case gulcose:
                return "mg/dL";
            case heartRate:
                return "count/min";
            case pulseOximeter:
                return "%";
            case temperature:
                return "°F";
            case weight:
                return "lbs";
            default:
                return "";
        }
    }

    public static final String[] items = new String[]{bpHeart, weight, temperature, gulcose, pulseOximeter};
    public static final String[] vitalItems = new String[]{bp, weight, temperature, gulcose, pulseOximeter, heartRate};

}
