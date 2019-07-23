package com.thealer.telehealer.views.home.vitals.iHealth.Controls;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.ihealth.communication.control.Hs2Control;
import com.ihealth.communication.control.Hs4sControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.WeightMeasureInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by rsekar on 12/4/18.
 */

public class WeightControl {

    private static final String TAG = "WeightControl";

    private WeightMeasureInterface weightMeasureInterface;
    private VitalBatteryFetcher vitalBatteryFetcher;
    private Context context;

    private Hs4sControl hs4Device;
    private Hs2Control hs2Control;

    public WeightControl(Context context, WeightMeasureInterface weightMeasureInterface, VitalBatteryFetcher vitalBatteryFetcher) {
        this.weightMeasureInterface = weightMeasureInterface;
        this.vitalBatteryFetcher = vitalBatteryFetcher;
        this.context = context;
    }

    public void onDeviceNotify(String mac, String deviceType, String action, String message) {
        Log.d("WeightControl - action",action);
        Log.d("WeightControl - message",message);
        Log.d("WeightControl - Type",deviceType);

        JSONTokener jsonTokener = new JSONTokener(message);
        switch (action) {
            case HsProfile.ACTION_BATTERY_HS:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    int battery = jsonObject.getInt(HsProfile.BATTERY_HS);
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType, mac, battery));
                } catch (JSONException e) {
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType, mac, -1));
                    e.printStackTrace();
                }
                break;
            case HsProfile.ACTION_HISTORICAL_DATA_HS:
                break;
            case HsProfile.ACTION_HISTORICAL_DATA_COMPLETE_HS:
                break;
            case HsProfile.ACTION_LIVEDATA_HS:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    float weight = ( (float) jsonObject.getDouble(HsProfile.LIVEDATA_HS) * 2.2f);
                    weight =  Float.parseFloat(String.format("%.1f", weight));
                    Log.d(TAG, "weight:" + weight);
                    weightMeasureInterface.updateWeightValue(deviceType,weight);
                } catch (JSONException e) {
                    e.printStackTrace();
                    weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            case HsProfile.ACTION_ONLINE_RESULT_HS:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    float weight = ((float) jsonObject.getDouble(HsProfile.WEIGHT_HS) * 2.2f);
                    weight =  Float.parseFloat(String.format("%.1f", weight));
                    String dataId = jsonObject.getString(HsProfile.DATAID);
                    Log.d(TAG, "dataId:" + dataId + "---weight:" + weight);
                    weightMeasureInterface.didFinishWeightMeasure(deviceType,weight,dataId);

                    //stop the current measure
                    stopMeasure(deviceType,mac);

                } catch (JSONException e) {
                    e.printStackTrace();
                    weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            case HsProfile.ACTION_NO_HISTORICALDATA:
                break;
            case HsProfile.ACTION_ERROR_HS:
                try {
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    int err = jsonObject.getInt(HsProfile.ERROR_NUM_HS);
                    String description = jsonObject.getString(HsProfile.ERROR_DESCRIPTION_HS);
                    Log.d(TAG, "weight:" + err);
                    Message error = new Message();
                    error.what = 1;
                    error.obj = "err:" + err;
                    if (err != 14) {
                        weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType, description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            default:
                break;
        }

    }

    public void startMeasure(String type,String deviceMac) {
        Log.d("WeightControl","startMeasure");
        switch (type) {
            case iHealthDevicesManager.TYPE_HS4S:
                startHS4Measure(type,deviceMac);
                break;
            case iHealthDevicesManager.TYPE_HS6:
                break;
            case iHealthDevicesManager.TYPE_HS2:
                startHs2Measure(type,deviceMac);
                break;
            default:
                break;
        }
    }

    public void stopMeasure(String type,String deviceMac) {
        switch (type) {
            case iHealthDevicesManager.TYPE_HS4S:
                if (hs4Device != null) {
                    hs4Device.getOfflineData();
                }
                break;
            case iHealthDevicesManager.TYPE_HS6:
                break;
            default:
                break;
        }
    }

    public Boolean hasValidController(String deviceType,String deviceMac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_HS4S:
                return getInstance(deviceType,deviceMac) != null;
            case iHealthDevicesManager.TYPE_HS6:
                return false;
            case iHealthDevicesManager.TYPE_HS2:
                return getInstance(deviceType,deviceMac) != null;
            default:
                return false;
        }
    }

    private Object getInstance(String deviceType,String deviceMac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_HS4S:
               return iHealthDevicesManager.getInstance().getHs4sControl(deviceMac);
            case iHealthDevicesManager.TYPE_HS6:
                return null;
            case iHealthDevicesManager.TYPE_HS2:
                return iHealthDevicesManager.getInstance().getHs2Control(deviceMac);
            default:
                return null;
        }
    }

    public void disconnect(String deviceType,String deviceMac) {
        if (getInstance(deviceType,deviceMac) == null) {
            return;
        }

        switch (deviceType) {
            case iHealthDevicesManager.TYPE_HS4S:
                iHealthDevicesManager.getInstance().getHs4sControl(deviceMac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_HS6:
                break;
            case iHealthDevicesManager.TYPE_HS2:
                iHealthDevicesManager.getInstance().getHs2Control(deviceMac).disconnect();
                break;
            default:
                break;
        }
    }

    public void fetchBatteryInformation(String type,String deviceMac) {
        switch (type) {
            case iHealthDevicesManager.TYPE_HS4S:
                vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(type,deviceMac,-1));
                break;
            case iHealthDevicesManager.TYPE_HS6:
                vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(type,deviceMac,-1));
                break;
            case iHealthDevicesManager.TYPE_HS2:
                vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(type,deviceMac,-1));
                break;
            default:
                break;
        }
    }


    private void startHS4Measure(String deviceType,String deviceMac) {
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            hs4Device = (Hs4sControl) object;
            weightMeasureInterface.didStartWeightMeasure(deviceType);
            hs4Device.measureOnline(2,123);

        } else {
            weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    private void startHs2Measure(String deviceType,String deviceMac) {
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            hs2Control = (Hs2Control) object;
            weightMeasureInterface.didStartWeightMeasure(deviceType);
            hs2Control.measureOnline(2,123);
        } else {
            weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

}
