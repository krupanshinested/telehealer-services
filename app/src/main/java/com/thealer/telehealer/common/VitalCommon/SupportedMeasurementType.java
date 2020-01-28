package com.thealer.telehealer.common.VitalCommon;

import com.thealer.telehealer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

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
    public static final String height = "height";
    public static final String stethoscope = "stethoscope";
    public static final String bmi = "bmi";


    public static int getTitle(String type) {
        switch (type) {
            case bp:
                return R.string.blood_pressure_heart_rate;
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
            case height:
                return R.string.height;
            default:
                return R.string.blood_pressure;
        }
    }

    public static int getDrawable(String type) {
        switch (type) {
            case bp:
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
            case height:
                return R.drawable.ic_vitals_height;
            default:
                return 0;
        }
    }

    public static String getVitalUnit(String supportedMeasurementType) {
        switch (supportedMeasurementType) {
            case bp:
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
            case height:
                return "inches";
            case bmi:
                return "kg/m²";
            default:
                return "";
        }
    }


    public static List<String> getItems() {
        return new ArrayList<>(Arrays.asList(bp, weight,height,temperature, gulcose, pulseOximeter));
    }

}
