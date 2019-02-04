package com.thealer.telehealer.common.VitalCommon;

import com.thealer.telehealer.R;

/**
 * Created by rsekar on 11/27/18.
 */

public class SupportedMeasurementType {
    public static final String bp = "bp";
    public static final String weight = "weight";
    public static final String temperature = "temperature";
    public static final String gulcose = "gulcose";
    public static final String pulseOximeter = "pulseOximeter";
    public static final String heartRate = "heartRate"; // ony used to save the heart rate which is calculated from bp or pulse machine


    public static int getTitle(String type) {
        switch (type) {
            case SupportedMeasurementType.bp:
                return R.string.blood_pressure;
            case SupportedMeasurementType.weight:
                return R.string.weight;
            case SupportedMeasurementType.temperature:
                return R.string.bodyTemperature;
            case SupportedMeasurementType.gulcose:
                return R.string.bloodGlucose;
            case SupportedMeasurementType.pulseOximeter:
                return R.string.pulseOximeter;
            case SupportedMeasurementType.heartRate:
                return R.string.heartRate;
            default:
                return R.string.blood_pressure;
        }
    }

    public static int getDrawable(String type) {
        switch (type) {
            case SupportedMeasurementType.bp:
                return R.drawable.ic_vitals_bp;
            case SupportedMeasurementType.weight:
                return R.drawable.ic_vitals_weight;
            case SupportedMeasurementType.temperature:
                return R.drawable.ic_vitals_temperature;
            case SupportedMeasurementType.gulcose:
                return R.drawable.ic_vitals_glucose;
            case SupportedMeasurementType.pulseOximeter:
                return R.drawable.ic_vitals_pulse;
            case SupportedMeasurementType.heartRate:
                return R.drawable.ic_vitals_heart;
            default:
                return 0;
        }
    }

    public static String getVitalUnit(String supportedMeasurementType) {
        switch (supportedMeasurementType) {
            case SupportedMeasurementType.bp:
                return "mmHg";
            case SupportedMeasurementType.gulcose:
                return "mg/dL";
            case SupportedMeasurementType.heartRate:
                return "count/min";
            case SupportedMeasurementType.pulseOximeter:
                return "%";
            case SupportedMeasurementType.temperature:
                return "°F";
            case SupportedMeasurementType.weight:
                return "lbs";
        }
        return null;
    }

    public static final String[] items = new String[]{bp,weight,temperature,gulcose,pulseOximeter,heartRate};

}
