package com.thealer.telehealer.views.home.vitals.iHealth.Controls;

import android.content.Context;
import android.util.Log;

import com.ihealth.communication.control.Bg5Control;
import com.ihealth.communication.control.Bg5Profile;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rsekar on 12/5/18.
 */

public class GulcoControl {
    private static final String TAG = "GulcoControl";

    private GulcoMeasureInterface gulcoMeasureInterface;
    private VitalBatteryFetcher vitalBatteryFetcher;
    private Context context;

    private Bg5Control bg5Control;

    private boolean isGodStrip = true;

    public GulcoControl(Context context, GulcoMeasureInterface gulcoMeasureInterface, VitalBatteryFetcher vitalBatteryFetcher) {
        this.gulcoMeasureInterface = gulcoMeasureInterface;
        this.vitalBatteryFetcher = vitalBatteryFetcher;
        this.context = context;
    }

    public void onDeviceNotify(String mac, String deviceType, String action, String message)  {
        Log.d(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);

        switch (action) {
            case Bg5Profile.ACTION_BATTERY_BG:
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(Bg5Profile.BATTERY_BG);
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,Integer.parseInt(battery)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,-1));
                }
                break;
            case Bg5Profile.ACTION_SET_TIME:

                break;
            case Bg5Profile.ACTION_SET_UNIT:

                break;
            case Bg5Profile.ACTION_ERROR_BG:
                try {
                    JSONObject info = new JSONObject(message);
                    String descriptin = info.getString(Bg5Profile.ERROR_DESCRIPTION_BG);
                    gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType,descriptin);
                } catch (JSONException e) {
                    e.printStackTrace();
                    gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType,e.getLocalizedMessage());
                }

                break;
            case Bg5Profile.ACTION_GET_CODEINFO:

                break;
            case Bg5Profile.ACTION_HISTORICAL_DATA_BG:

                break;
            case Bg5Profile.ACTION_DELETE_HISTORICAL_DATA:

                break;
            case Bg5Profile.ACTION_SET_BOTTLE_MESSAGE_SUCCESS:

                break;
            case Bg5Profile.ACTION_START_MEASURE:

                break;
            case Bg5Profile.ACTION_ONLINE_RESULT_BG:
                // {"result":96,"dataID":"E16733B7847059AAD69B87EC16A52464","measured_at":"2018-12-05 20:00:46"}
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    int result = (int) jsonObject.get("result");
                    gulcoMeasureInterface.updateGulcoValue(deviceType,result);
                } catch (Exception e) {
                    gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType,e.getLocalizedMessage());
                }
                break;
            case Bg5Profile.ACTION_KEEP_LINK:
                break;
            case Bg5Profile.ACTION_STRIP_IN:
                gulcoMeasureInterface.didStripInserted(deviceType);
                break;
            case Bg5Profile.ACTION_GET_BLOOD:
                gulcoMeasureInterface.didBloodDropped(deviceType);
                break;
            case Bg5Profile.ACTION_STRIP_OUT:
                gulcoMeasureInterface.didStripEjected(deviceType);
                break;
            case Bg5Profile.ACTION_GET_BOTTLEID:
                break;
            case Bg5Profile.ACTION_SET_BOTTLE_ID_SUCCESS:
                break;

        }
    }

    public void startMeasure(String type,String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_BG5:
                startBG5Measure(type,deviceMac);
                break;
            default:
                break;
        }
    }

    public void stopMeasure(String type,String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_BG5:
                if (bg5Control != null) {
                    bg5Control.disconnect();
                }
                break;
            default:
                break;
        }
    }

    public Boolean hasValidController(String deviceType,String deviceMac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_BG5:
                return getInstance(deviceType,deviceMac) != null;
            default:
                return false;
        }
    }

    public void disconnect(String deviceType, String deviceMac) {
        if (getInstance(deviceType, deviceMac) == null) {
            return;
        }
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_BG5:
                iHealthDevicesManager.getInstance().getBg5Control(deviceMac).disconnect();
                break;
            default:
                break;
        }
    }

    public void updateStripBottleId(String deviceType,String mac,String result) {
        Log.d("GulcoControl","updateStripBottleId "+deviceType+" mac "+mac);
        Object object = getInstance(deviceType,mac);
        if (object != null) {
            bg5Control = (Bg5Control) object;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String today = formatter.format(new Date());
            bg5Control.setBottleMessageWithInfo(isGodStrip ? Bg5Profile.STRIP_GOD : Bg5Profile.STRIP_GDH,Bg5Profile.MEASURE_BLOOD,result,20,today);
        } else {
            Log.d("GulcoControl","didFinishGulcoMesureWithFailure "+deviceType+" mac "+mac);
            gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }

    public void updateStripType(String deviceType,String mac,boolean isGod) {
        isGodStrip = isGod;

        if (!isGod) {
            updateStripBottleId(deviceType,mac,"");
        }
    }

    private Object getInstance(String deviceType,String deviceMac) {
        Log.d("GulcoControl","getInstance type "+deviceType+" mac "+deviceMac);
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_BG5:
                Log.d("GulcoControl","inside bg5");
                return iHealthDevicesManager.getInstance().getBg5Control(deviceMac);
            default:
                Log.d("GulcoControl","inside default");
                return null;
        }
    }

    public void fetchBatteryInformation(String type,String deviceMac) {
        Log.d("GulcoControl","fetchBatteryInformation");

        Object object = getInstance(type,deviceMac);

        switch (type) {
            case iHealthDevicesManager.TYPE_BG5:
                if (object != null) {
                    bg5Control = (Bg5Control) object;
                    Log.d("GulcoControl","called getBattery");
                    bg5Control.getBattery();
                } else {
                    Log.d("GulcoControl","not called getBattery");
                    vitalBatteryFetcher.notConnected(type,deviceMac);
                }
                break;
            default:
                break;
        }
    }

    private void startBG5Measure(String deviceType,String deviceMac) {
        Log.d("GulcoControl","startBG5Measure "+deviceType+" mac "+deviceMac);
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            bg5Control = (Bg5Control) object;
            gulcoMeasureInterface.didGulcoStartMeasure(deviceType);
            bg5Control.startMeasure(Bg5Profile.MEASURE_BLOOD);
            bg5Control.holdLink();
        } else {
            Log.d("GulcoControl","didFinishGulcoMesureWithFailure "+deviceType+" mac "+deviceMac);
            gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }
    }
}
