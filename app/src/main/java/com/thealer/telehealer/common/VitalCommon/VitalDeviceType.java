package com.thealer.telehealer.common.VitalCommon;

import com.thealer.telehealer.R;

import java.util.ArrayList;

/**
 * Created by rsekar on 11/27/18.
 */

public class VitalDeviceType {

    public static final VitalDeviceType shared = new VitalDeviceType();

    public static final ArrayList<String> types = new ArrayList<String>() {{
        add(VitalsConstant.TYPE_PO3);
        add(VitalsConstant.TYPE_550BT);
        add(VitalsConstant.TYPE_BP7);
        add(VitalsConstant.TYPE_HS4S);
        add(VitalsConstant.TYPE_BP3L);
        add(VitalsConstant.TYPE_BP5);
        add(VitalsConstant.TYPE_BG5);
        add(VitalsConstant.TYPE_TS28B);
        add(VitalsConstant.TYPE_FDIR_V3);
    }};

    public int getTitle(String type) {
        switch (type) {
            case VitalsConstant.TYPE_PO3: return R.string.air_title;
            case VitalsConstant.TYPE_BP3L: return R.string.ease_title;
            case VitalsConstant.TYPE_BP5: return R.string.feel_title;
            case VitalsConstant.TYPE_BP7: return R.string.sense_title;
            case VitalsConstant.TYPE_HS4S: return R.string.lite_title;
            case VitalsConstant.TYPE_BP7S: return R.string.view_title;
            case VitalsConstant.TYPE_BG5: return R.string.smart_title;
            case VitalsConstant.TYPE_BPM1: return R.string.clear_title;
            case VitalsConstant.TYPE_550BT: return R.string.track_title;
            case VitalsConstant.TYPE_HS6: return R.string.core_title;
            case VitalsConstant.TYPE_TS28B: return R.string.ts28_title;
            case VitalsConstant.TYPE_FDIR_V3: return R.string.fdir_title;
            default:
                return 0;
        }
    }

    public String getKeyValue(String type) {
        switch (type) {
            case VitalsConstant.TYPE_PO3: return VitalsConstant.air_key;
            case VitalsConstant.TYPE_BP3L: return VitalsConstant.ease_key;
            case VitalsConstant.TYPE_BP5: return VitalsConstant.feel_key;
            case VitalsConstant.TYPE_BP7: return VitalsConstant.sense_key;
            case VitalsConstant.TYPE_HS4S: return VitalsConstant.lite_key;
            case VitalsConstant.TYPE_BP7S: return VitalsConstant.view_key;
            case VitalsConstant.TYPE_BG5: return VitalsConstant.smart_key;
            case VitalsConstant.TYPE_BPM1: return VitalsConstant.clear_key;
            case VitalsConstant.TYPE_550BT: return VitalsConstant.track_key;
            case VitalsConstant.TYPE_HS6: return VitalsConstant.core_key;
            case VitalsConstant.TYPE_TS28B: return VitalsConstant.ts28_key;
            case VitalsConstant.TYPE_FDIR_V3: return VitalsConstant.thv3_key;
            default:
                return "";
        }
    }

    public String getVitalType(String key) {
        switch (key) {
            case VitalsConstant.air_key: return VitalsConstant.TYPE_PO3;
            case VitalsConstant.ease_key: return VitalsConstant.TYPE_BP3L;
            case VitalsConstant.feel_key: return VitalsConstant.TYPE_BP5;
            case VitalsConstant.sense_key: return VitalsConstant.TYPE_BP7;
            case VitalsConstant.lite_key: return VitalsConstant.TYPE_HS4S;
            case VitalsConstant.view_key: return VitalsConstant.TYPE_BP7S;
            case VitalsConstant.smart_key: return VitalsConstant.TYPE_BG5;
            case VitalsConstant.clear_key: return VitalsConstant.TYPE_BPM1;
            case VitalsConstant.track_key: return VitalsConstant.TYPE_550BT;
            case VitalsConstant.core_key: return VitalsConstant.TYPE_HS6;
            case VitalsConstant.ts28_key: return VitalsConstant.TYPE_TS28B;
            case VitalsConstant.thv3_key: return VitalsConstant.TYPE_FDIR_V3;
            default:
                return "";
        }
    }

    public int getDescription(String type) {
        switch (type) {
        case VitalsConstant.TYPE_PO3: return R.string.wireless_Pulse_Oximeter;
        case VitalsConstant.TYPE_BP3L: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_BP5: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_BP7: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_HS4S: return R.string.wireless_Weight_Scale;
        case VitalsConstant.TYPE_BP7S: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_BG5: return R.string.wireless_Glucometer;
        case VitalsConstant.TYPE_BPM1: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_550BT: return R.string.wireless_blood_Pressure;
        case VitalsConstant.TYPE_HS6: return R.string.wireless_Weight_Scale;
        case VitalsConstant.TYPE_TS28B: return R.string.wireless_Thermometer;
        case VitalsConstant.TYPE_FDIR_V3: return R.string.wireless_Thermometer;
        default:
            return 0;
        }
    }

    public int getShortDescription(String type) {
        switch (type) {
            case VitalsConstant.TYPE_PO3: return R.string.air_description;
            case VitalsConstant.TYPE_BP3L: return R.string.ease_description;
            case VitalsConstant.TYPE_BP5: return R.string.feel_description;
            case VitalsConstant.TYPE_BP7: return R.string.sense_description;
            case VitalsConstant.TYPE_HS4S: return R.string.lite_description;
            case VitalsConstant.TYPE_BP7S: return R.string.view_description;
            case VitalsConstant.TYPE_BG5: return R.string.smart_description;
            case VitalsConstant.TYPE_BPM1: return R.string.clear_description;
            case VitalsConstant.TYPE_550BT: return R.string.track_description;
            case VitalsConstant.TYPE_HS6: return R.string.core_description;
            case VitalsConstant.TYPE_TS28B: return R.string.ts28_description;
            case VitalsConstant.TYPE_FDIR_V3: return R.string.fdir_description;
            default:
                return 0;
        }
    }

    public String getUrl(String type) {
        switch (type) {
        case VitalsConstant.TYPE_PO3: return "https://ihealthlabs.com/fitness-devices/wireless-pulse-oximeter/";
        case VitalsConstant.TYPE_BP3L: return "https://ihealthlabs.com/blood-pressure-monitors/ease-bp3l/";
        case VitalsConstant.TYPE_BP5: return "https://ihealthlabs.com/blood-pressure-monitors/feel/";
        case VitalsConstant.TYPE_BP7: return "https://ihealthlabs.com/blood-pressure-monitors/wireless-blood-pressure-wrist-monitor/";
        case VitalsConstant.TYPE_HS4S: return "https://ihealthlabs.com/wireless-scales/ihealth-lite/";
        case VitalsConstant.TYPE_BG5: return "https://ihealthlabs.com/glucometer/wireless-smart-gluco-monitoring-system/";
        case VitalsConstant.TYPE_BPM1: return "https://ihealthlabs.com/blood-pressure-monitors/clear";
        case VitalsConstant.TYPE_550BT: return "https://ihealthlabs.com/blood-pressure-monitors/track";
        case VitalsConstant.TYPE_HS6: return "https://ihealthlabs.com/wireless-scales/ihealth-core/";
        case VitalsConstant.TYPE_TS28B: return "https://ihealthlabs.com";
        case VitalsConstant.TYPE_FDIR_V3: return "https://ihealthlabs.com";
        case VitalsConstant.TYPE_BP7S:
            return "https://ihealthlabs.com/blood-pressure-monitors/wireless-blood-pressure-wrist-monitor-view-bp7s/";
            default: return "";
        }
    }

    public String getSupportUrl(String type) {
        switch (type) {
        case VitalsConstant.TYPE_PO3: return "https://ihealthlabs.com/support/fitness-devices/wireless-pulse-oximeter/";
        case VitalsConstant.TYPE_BP3L: return "https://ihealthlabs.com/support/blood-pressure-monitors/blood-pressure-dock-ease/";
        case VitalsConstant.TYPE_BP5: return "https://ihealthlabs.com/support/blood-pressure-monitors/wireless-blood-pressure-monitor/";
        case VitalsConstant.TYPE_BP7: return "https://ihealthlabs.com/support/blood-pressure-monitors/wireless-blood-pressure-wrist-monitor/";
        case VitalsConstant.TYPE_HS4S: return "https://ihealthlabs.com/support/wireless-scales/ihealth-lite/";
        case VitalsConstant.TYPE_BG5: return "https://ihealthlabs.com/support/glucometer/wireless-smart-gluco-monitoring-system/";
        case VitalsConstant.TYPE_BPM1: return "https://ihealthlabs.com/support/blood-pressure-monitors/clear/";
        case VitalsConstant.TYPE_550BT: return "https://ihealthlabs.com/support/blood-pressure-monitors/track-connected-blood-pressure-monitor/";
        case VitalsConstant.TYPE_HS6: return "https://ihealthlabs.com/support/wireless-scales/ihealth-core-wireless-body-composition-scale/";
        case VitalsConstant.TYPE_TS28B: return "https://ihealthlabs.com/support/thermometer/pt3";
        case VitalsConstant.TYPE_FDIR_V3: return "https://ihealthlabs.com/support/thermometer/pt3";
        case VitalsConstant.TYPE_BP7S:
            return "https://ihealthlabs.com/support/blood-pressure-monitors/view-wireless-blood-pressure-wrist-monitor/";
            default: return "";
        }
    }

    public int getImage(String type) {
        switch (type) {
        case VitalsConstant.TYPE_PO3: return R.drawable.ihealth_air;
        case VitalsConstant.TYPE_BP3L: return R.drawable.ihealth_bloodpressure_ease;
        case VitalsConstant.TYPE_BP5: return R.drawable.ihealth_bloodpressure_feel;
        case VitalsConstant.TYPE_BP7: return R.drawable.ihealth_bloodpressure_sense;
        case VitalsConstant.TYPE_HS4S: return R.drawable.ihealth_scale_lite;
        case VitalsConstant.TYPE_BG5: return R.drawable.ihealth_glucometer_smart;
        case VitalsConstant.TYPE_FDIR_V3: return R.drawable.ihealth_fdir;
        case VitalsConstant.TYPE_BPM1: return R.drawable.ihealth_bloodpressure_clear;
        case VitalsConstant.TYPE_550BT: return R.drawable.ihealth_bloodpressure_track;
        case VitalsConstant.TYPE_HS6: return R.drawable.ihealth_scale_core;
        case VitalsConstant.TYPE_TS28B: return R.drawable.ihealth_ts28b;
        case VitalsConstant.TYPE_BP7S: return R.drawable.ihealth_bloodpressure_view;
        default:
            return 0;
        }
    }

    public String getVideoId(String type) {
        switch (type) {
        case VitalsConstant.TYPE_PO3: return "Yiy8-agx84M";
        case VitalsConstant.TYPE_BP3L: return "fHHFT_5uC6I";
        case VitalsConstant.TYPE_BP5: return "w84VXgFZ7As";
        case VitalsConstant.TYPE_BP7: return "cS2G_io96QY";
        case VitalsConstant.TYPE_HS4S: return "gqleMSP6HWM";
        case VitalsConstant.TYPE_BG5: return "5Patm9FEdv4";
        case VitalsConstant.TYPE_FDIR_V3: return "xifp2ZMWDBc";
        case VitalsConstant.TYPE_BPM1: return "19ccBjC6BM4";
        case VitalsConstant.TYPE_550BT: return "PovTKbQTGFk";
        case VitalsConstant.TYPE_HS6: return "SGUMQ7u2GS8";
        case VitalsConstant.TYPE_TS28B: return "xifp2ZMWDBc";
        case VitalsConstant.TYPE_BP7S: return "TGmWV2yKWQs";
            default:
                return "";
        }
    }

    public String getMeasurementType(String type) {
        switch (type) {
            case VitalsConstant.TYPE_BP3L:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_550BT:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_BP5:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_BPM1:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_BP7:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_BP7S:
                return SupportedMeasurementType.bp;
            case VitalsConstant.TYPE_PO3:
                return SupportedMeasurementType.pulseOximeter;
            case VitalsConstant.TYPE_HS6:
                return SupportedMeasurementType.weight;
            case VitalsConstant.TYPE_HS4S:
                return SupportedMeasurementType.weight;
            case VitalsConstant.TYPE_BG5:
                return SupportedMeasurementType.gulcose;
            case VitalsConstant.TYPE_FDIR_V3:
                return SupportedMeasurementType.temperature;
            case VitalsConstant.TYPE_TS28B:
                return SupportedMeasurementType.temperature;
            default:
                return "";
        }
    }

    public String[] getFilterType(String type) {
        switch (type) {
            case VitalsConstant.TYPE_BP3L:
                return new String[]{VitalsConstant.TYPE_BP3L};
            case VitalsConstant.TYPE_550BT:
                return new String[]{VitalsConstant.TYPE_550BT};
            case VitalsConstant.TYPE_BP5:
                return new String[]{VitalsConstant.TYPE_BP5};
            case VitalsConstant.TYPE_BPM1:
                return new String[]{VitalsConstant.TYPE_BPM1};
            case VitalsConstant.TYPE_BP7:
                return new String[]{VitalsConstant.TYPE_BP7};
            case VitalsConstant.TYPE_BP7S:
                return new String[]{VitalsConstant.TYPE_BP7S};
            case VitalsConstant.TYPE_PO3:
                return new String[]{VitalsConstant.TYPE_PO3};
            case VitalsConstant.TYPE_HS6:
                return new String[]{VitalsConstant.TYPE_HS6};
            case VitalsConstant.TYPE_HS4S:
                return new String[]{VitalsConstant.TYPE_HS4S};
            case VitalsConstant.TYPE_BG5:
                return new String[]{VitalsConstant.TYPE_BG5};
            case VitalsConstant.TYPE_FDIR_V3:
                return new String[]{VitalsConstant.TYPE_FDIR_V3};
            case VitalsConstant.TYPE_TS28B:
                return new String[]{VitalsConstant.TYPE_TS28B,"DTS28B"};
            default:
                return new String[]{};
        }

    }

}
