package com.thealer.telehealer.views.home.vitals.iHealth.Controls;

import android.content.Context;
import android.util.Log;

import com.ihealth.communication.control.Bp3lControl;
import com.ihealth.communication.control.Bp550BTControl;
import com.ihealth.communication.control.Bp5Control;
import com.ihealth.communication.control.Bp7Control;
import com.ihealth.communication.control.Bp7sControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.control.Bpm1Control;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalDeviceType;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;

/**
 * Created by rsekar on 11/28/18.
 */

public class BPControl {

    private static final String TAG = "BPControl";

    private BPMeasureInterface bpMeasureInterface;
    private VitalBatteryFetcher vitalBatteryFetcher;

    private Bp5Control bp5Control;
    private Bp3lControl bp3lControl;
    private Bp7Control bp7Control;
    private Bpm1Control bpm1Control;
    private Bp7sControl bp7sControl;
    private Bp550BTControl bp550BTControl;
    private Context context;

    public BPControl(Context context, BPMeasureInterface bpMeasureInterface, VitalBatteryFetcher vitalBatteryFetcher) {
        this.context = context;
        this.bpMeasureInterface = bpMeasureInterface;
        this.vitalBatteryFetcher = vitalBatteryFetcher;
    }

    public void onDeviceNotify(String mac, String deviceType, String action, String message) {

        if (action != null)
            Log.d("BPControl - action", action);

        if (message != null)
            Log.d("BPControl - message", message);

        switch (action) {
            case BpProfile.ACTION_BATTERY_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BpProfile.BATTERY_BP);
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType, mac, Integer.parseInt(battery)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType, mac, -1));
                }
                break;
            case BpProfile.ACTION_DISENABLE_OFFLINE_BP:
                Log.i(TAG, "disable operation is success");
                break;
            case BpProfile.ACTION_ENABLE_OFFLINE_BP:
                Log.i(TAG, "enable operation is success");
                break;
            case BpProfile.ACTION_ERROR_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    String description = info.getString(BpProfile.ERROR_DESCRIPTION_BP);
                    bpMeasureInterface.didFailBPMesure(deviceType,description);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(deviceType,e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_HISTORICAL_DATA_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    Log.e("BPControl","data "+info.toString());
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);

                        ArrayList<BPTrack> tracks = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);


                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            Date createdDate = Utils.getDateFromString(date,"yyyy-MM-dd HH:mm:ss");

                            if (createdDate == null) {
                                createdDate = new Date();
                            }

                            double sys = obj.getDouble(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            double dia = obj.getDouble(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            double pulseWave = obj.getDouble(BpProfile.PULSE_BP);
                            String id = obj.getString(BpProfile.DATAID);

                            tracks.add(new BPTrack(sys,dia,pulseWave,createdDate,id));
                        }

                        bpMeasureInterface.didFinishBpMeasure(tracks);

                    } else {
                        Log.e("BPControl","error info does not data");
                        bpMeasureInterface.didFinishBpMeasure(new ArrayList<BPTrack>());
                    }
                } catch (JSONException e) {
                    Log.e("BPControl","error "+e.getLocalizedMessage());
                    bpMeasureInterface.didFinishBpMeasure(new ArrayList<BPTrack>());
                }

                break;
            case BpProfile.ACTION_HISTORICAL_NUM_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(deviceType,e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_IS_ENABLE_OFFLINE:
                try {
                    JSONObject info = new JSONObject(message);
                    String isEnableoffline = info.getString(BpProfile.IS_ENABLE_OFFLINE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(deviceType,e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_ONLINE_PRESSURE_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);

                    if (pressure != null && pressure.length() > 3) {

                        pressure = pressure.replaceAll("\\[","");
                        pressure = pressure.replaceAll("]","");

                        String items[] = pressure.split(",");
                        ArrayList<Double> waveItems = new ArrayList<Double>();

                        for (String vl : items) {
                            waveItems.add(Double.parseDouble(vl));
                        }

                        bpMeasureInterface.didUpdateBPM(deviceType,waveItems);

                    } else {
                        bpMeasureInterface.didUpdateBPM(deviceType,new ArrayList<>());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case BpProfile.ACTION_ONLINE_PULSEWAVE_BP:
                try {
                    //{"pressure":129,"wave":"[39,39,37,34,30]","heartbeat":false}
                    JSONObject info = new JSONObject(message);

                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);

                    if (wave != null && wave.length() > 3) {

                        Log.e("BPControl","parsing wave");
                        wave = wave.replaceAll("\\[","");
                        wave = wave.replaceAll("]","");

                        String items[] = wave.split(",");
                        ArrayList<Double> waveItems = new ArrayList<Double>();

                        for (String vl : items) {
                            waveItems.add(Double.parseDouble(vl));
                        }

                        Log.e("BPControl","parsing wave "+waveItems.toString());
                        bpMeasureInterface.didUpdateBPMesure(deviceType,waveItems);

                    } else {
                        bpMeasureInterface.didUpdateBPMesure(deviceType,new ArrayList<>());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(deviceType,e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_ONLINE_RESULT_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP); //sys
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP); //dia
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);

                    bpMeasureInterface.didFinishBPMesure(deviceType,Double.parseDouble(highPressure),Double.parseDouble(lowPressure),Double.parseDouble(pulse));

                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(deviceType,e.getLocalizedMessage());
                }

                break;
            case BpProfile.ACTION_ZOREING_BP:
                break;
            case BpProfile.ACTION_ZOREOVER_BP:
                break;
        }
    }

    public void startMeasure(String type, String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_BP3L:
                startBP3LMeasure(type, deviceMac);
                break;
            case iHealthDevicesManager.TYPE_550BT:
                startKN550BTMeasure(type, deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP5:
                startBp5Measure(type, deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP7:
                startBp7Measure(type, deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BPM1:
                startBPM1AEMeasure(type, deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP7S:
                startBp7SMeasure(type, deviceMac);
                break;
            default:
                break;
        }
    }

    public void stopMeasure(String type, String deviceMac) {

        /*
        switch (type) {
            case iHealthDevicesManager.TYPE_BP3L:
                startBP3LMeasure(deviceMac);
                break;
            case iHealthDevicesManager.TYPE_550BT:
                startKN550BTMeasure(deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP5:
                startBp5Measure(deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP7:
                startBp7Measure(deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BPM1:
                startBPM1AEMeasure(deviceMac);
                break;
            case iHealthDevicesManager.TYPE_BP7S:
                startBp7SMeasure(deviceMac);
                break;
            default:
                break;
        }*/
    }

    public void fetchBatteryInformation(String type, String deviceMac) {
        Log.d("BPControl", "fetchBatteryInformation");

        Object object = getInstance(type, deviceMac);

        if (object == null) {
            vitalBatteryFetcher.notConnected(type, deviceMac);
            return;
        }

        switch (type) {
            case iHealthDevicesManager.TYPE_BP3L:
                bp3lControl = (Bp3lControl) object;
                bp3lControl.getBattery();
                break;
            case iHealthDevicesManager.TYPE_550BT:
                bp550BTControl = (Bp550BTControl) object;
                bp550BTControl.getBattery();
                break;
            case iHealthDevicesManager.TYPE_BP5:
                bp5Control = (Bp5Control) object;
                bp5Control.getBattery();
                break;
            case iHealthDevicesManager.TYPE_BP7:
                bp7Control = (Bp7Control) object;
                bp7Control.getBattery();
                break;
            case iHealthDevicesManager.TYPE_BPM1:
                vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(type, deviceMac, -1));
                break;
            case iHealthDevicesManager.TYPE_BP7S:
                bp7sControl = (Bp7sControl) object;
                bp7sControl.getBattery();
                break;
            default:
                break;
        }

    }

    public Boolean hasValidController(String deviceType, String mac) {
        return getInstance(deviceType, mac) != null;
    }

    @Nullable
    private Object getInstance(String deviceType, String mac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_BP3L:
                return iHealthDevicesManager.getInstance().getBp3lControl(mac);
            case iHealthDevicesManager.TYPE_550BT:
                return iHealthDevicesManager.getInstance().getBp550BTControl(mac);
            case iHealthDevicesManager.TYPE_BP5:
                return iHealthDevicesManager.getInstance().getBPControl(mac);
            case iHealthDevicesManager.TYPE_BP7:
                return iHealthDevicesManager.getInstance().getBp7Control(mac);
            case iHealthDevicesManager.TYPE_BPM1:
                return null;
            case iHealthDevicesManager.TYPE_BP7S:
                return iHealthDevicesManager.getInstance().getBp7sControl(mac);
            default:
                return null;
        }
    }

    public void disconnect(String deviceType, String mac) {
        if (getInstance(deviceType,mac) == null) {
            return;
        }

        switch (deviceType) {
            case iHealthDevicesManager.TYPE_BP3L:
                iHealthDevicesManager.getInstance().getBp3lControl(mac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_550BT:
                iHealthDevicesManager.getInstance().getBp550BTControl(mac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_BP5:
                iHealthDevicesManager.getInstance().getBPControl(mac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_BP7:
                iHealthDevicesManager.getInstance().getBp7Control(mac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_BPM1:
                break;
            case iHealthDevicesManager.TYPE_BP7S:
                iHealthDevicesManager.getInstance().getBp7sControl(mac).disconnect();
                break;
            default:
                break;
        }
    }

    private void startBP3LMeasure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp3lControl = (Bp3lControl) object;
            bpMeasureInterface.didStartBPMesure(deviceType);
            bp3lControl.startMeasure();
        } else {
            bpMeasureInterface.didFailBPMesure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    private void startKN550BTMeasure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp550BTControl = (Bp550BTControl) object;
            bp550BTControl.getFunctionInfo();
            bp550BTControl.getOfflineData();
            bpMeasureInterface.didStartBPMesure(deviceType);
        } else {
            bpMeasureInterface.didFailBPMesure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    public void syncTimeForTrack(String deviceMac) {
        Object object = getInstance(iHealthDevicesManager.TYPE_550BT, deviceMac);
        if (object != null) {
            Bp550BTControl bp550BTControl = (Bp550BTControl) object;
            bp550BTControl.getFunctionInfo();
        }
    }

    private void startBp5Measure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp5Control = (Bp5Control) object;
            bp5Control.startMeasure();
            bpMeasureInterface.didStartBPMesure(deviceType);
        } else {
            bpMeasureInterface.didFailBPMesure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    private void startBp7Measure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp7Control = (Bp7Control) object;
            bp7Control.startMeasure();
            bpMeasureInterface.didStartBPMesure(deviceType);
        } else {
            bpMeasureInterface.didFailBPMesure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    private void startBPM1AEMeasure(String deviceType, String deviceMac) {
        //bpm1Control = iHealthDevicesManager.getInstance().getBP(deviceMac);
        //bp5Control.startMeasure();
    }

    private void startBp7SMeasure(String deviceType, String deviceMac) {
        //bp7sControl = iHealthDevicesManager.getInstance().getBp7sControl(deviceMac);
        //bp7sControl.startMeasure();
    }

}
