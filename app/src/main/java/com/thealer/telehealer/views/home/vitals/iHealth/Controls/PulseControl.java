package com.thealer.telehealer.views.home.vitals.iHealth.Controls;

import android.content.Context;
import android.util.Log;

import com.ihealth.communication.control.Po3Control;
import com.ihealth.communication.control.PoProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by rsekar on 12/5/18.
 */

public class PulseControl {
    private static final String TAG = "PulseControl";

    private PulseMeasureInterface pulseMeasureInterface;
    private VitalBatteryFetcher vitalBatteryFetcher;
    private Context context;

    private Po3Control po3Control;

    public PulseControl(Context context, PulseMeasureInterface pulseMeasureInterface, VitalBatteryFetcher vitalBatteryFetcher) {
        this.pulseMeasureInterface = pulseMeasureInterface;
        this.vitalBatteryFetcher = vitalBatteryFetcher;
        this.context = context;
    }

    public void onDeviceNotify(String mac, String deviceType, String action, String message) {
        Log.d(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);

        JSONTokener jsonTokener = new JSONTokener(message);
        switch (action) {
            case PoProfile.ACTION_OFFLINEDATA_PO:
                break;
            case PoProfile.ACTION_LIVEDA_PO:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                    int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                    float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                    JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                    int[] wave = new int[3];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        wave[i] = jsonArray.getInt(i);
                    }
                    Log.i(TAG, "oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                            + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
                   pulseMeasureInterface.updatePulseValue(deviceType,oxygen,pulseRate,wave[0],PI);
                } catch (JSONException e) {
                    e.printStackTrace();
                    pulseMeasureInterface.didPulseFinishMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            case PoProfile.ACTION_RESULTDATA_PO:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    String dataId = jsonObject.getString(PoProfile.DATAID);
                    int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                    int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                    float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                    JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                    int[] wave = new int[3];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        wave[i] = jsonArray.getInt(i);
                    }
                    Log.i(TAG, "dataId:" + dataId + "--oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                            + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
                    pulseMeasureInterface.didFinishMeasure(deviceType,oxygen,pulseRate,wave[0],PI);
                } catch (JSONException e) {
                    e.printStackTrace();
                    pulseMeasureInterface.didPulseFinishMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            case PoProfile.ACTION_NO_OFFLINEDATA_PO:
                break;
            case PoProfile.ACTION_BATTERY_PO:
                JSONObject jsonobject;
                try {
                    jsonobject = (JSONObject) jsonTokener.nextValue();
                    int battery = jsonobject.getInt(PoProfile.BATTERY_PO);
                    Log.d(TAG, "battery:" + battery);
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,battery));
                } catch (JSONException e) {
                    e.printStackTrace();
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,-1));
                }

                break;
            default:
                break;
        }

    }

    public void startMeasure(String type,String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_PO3:
                startPo3Measure(type,deviceMac);
                break;
            default:
                break;
        }
    }

    public void stopMeasure(String type,String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_PO3:
                if (po3Control != null) {
                    po3Control.disconnect();
                }
                break;
            default:
                break;
        }
    }

    public Boolean hasValidController(String deviceType,String deviceMac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_PO3:
                return getInstance(deviceType,deviceMac) != null;
            default:
                return false;
        }
    }

    public void disconnect(String deviceType,String deviceMac) {
        if (getInstance(deviceType, deviceMac) == null) {
            return;
        }

        switch (deviceType) {
            case iHealthDevicesManager.TYPE_PO3:
                iHealthDevicesManager.getInstance().getPo3Control(deviceMac).disconnect();
                break;
            default:
                break;
        }
    }

    private Object getInstance(String deviceType,String deviceMac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_PO3:
                return iHealthDevicesManager.getInstance().getPo3Control(deviceMac);
            default:
                return null;
        }
    }

    public void fetchBatteryInformation(String type,String deviceMac) {
        Log.d("PulseControl", "fetchBatteryInformation");

        Object object = getInstance(type,deviceMac);

        if (object == null) {
            vitalBatteryFetcher.notConnected(type, deviceMac);
            return;
        }

        switch (type) {
            case iHealthDevicesManager.TYPE_PO3:
                po3Control = (Po3Control) object;
                po3Control.getBattery();
                break;
            default:
                break;
        }
    }

    private void startPo3Measure(String deviceType,String deviceMac) {
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            po3Control = (Po3Control) object;
            pulseMeasureInterface.didPulseStartMeasure(deviceType);
            po3Control.startMeasure();
        } else {
            pulseMeasureInterface.didPulseFinishMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }
}
