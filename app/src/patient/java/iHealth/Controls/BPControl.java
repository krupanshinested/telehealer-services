package iHealth.Controls;

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
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Log.d("BPControl - action", action);
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
                    bpMeasureInterface.didFailBPMesure(description);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_HISTORICAL_DATA_BP:
                String str = "";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
                }

                break;
            case BpProfile.ACTION_HISTORICAL_NUM_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_IS_ENABLE_OFFLINE:
                try {
                    JSONObject info = new JSONObject(message);
                    String isEnableoffline = info.getString(BpProfile.IS_ENABLE_OFFLINE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_ONLINE_PRESSURE_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case BpProfile.ACTION_ONLINE_PULSEWAVE_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
                }
                break;
            case BpProfile.ACTION_ONLINE_RESULT_BP:
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                } catch (JSONException e) {
                    e.printStackTrace();
                    bpMeasureInterface.didFailBPMesure(e.getLocalizedMessage());
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


    private void startBP3LMeasure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp3lControl = (Bp3lControl) object;
            bp3lControl.startMeasure();
        } else {
            bpMeasureInterface.didFailBPMesure(context.getString(R.string.unable_to_connect));
        }
    }

    private void startKN550BTMeasure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp550BTControl = (Bp550BTControl) object;
            //TODO
        } else {
            bpMeasureInterface.didFailBPMesure(context.getString(R.string.unable_to_connect));
        }
    }

    private void startBp5Measure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp5Control = (Bp5Control) object;
            bp5Control.startMeasure();
        } else {
            bpMeasureInterface.didFailBPMesure(context.getString(R.string.unable_to_connect));
        }
    }

    private void startBp7Measure(String deviceType, String deviceMac) {
        Object object = getInstance(deviceType, deviceMac);
        if (object != null) {
            bp7Control = (Bp7Control) object;
            bp7Control.startMeasure();
        } else {
            bpMeasureInterface.didFailBPMesure(context.getString(R.string.unable_to_connect));
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
