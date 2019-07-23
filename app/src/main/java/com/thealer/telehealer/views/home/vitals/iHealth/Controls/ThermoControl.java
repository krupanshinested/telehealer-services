package com.thealer.telehealer.views.home.vitals.iHealth.Controls;

import android.content.Context;
import android.util.Log;

import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.BtmProfile;
import com.ihealth.communication.control.TS28BControl;
import com.ihealth.communication.control.TS28BProfile;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.VitalCommon.BatteryResult;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.ThermoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsekar on 12/4/18.
 */

public class ThermoControl {

    static final String TAG = "ThermoControl";

    private ThermoMeasureInterface thermoMeasureInterface;
    private VitalBatteryFetcher vitalBatteryFetcher;
    private Context context;

    private TS28BControl ts28BControl;
    private BtmControl fdirControl; //fdir Control

    public ThermoControl(Context context, ThermoMeasureInterface thermoMeasureInterface, VitalBatteryFetcher vitalBatteryFetcher) {
        this.thermoMeasureInterface = thermoMeasureInterface;
        this.vitalBatteryFetcher = vitalBatteryFetcher;
        this.context = context;
    }

    public void onDeviceNotify(String mac, String deviceType, String action, String message) {
        Log.d("ThermoControl - action", action);
        Log.d("ThermoControl - message", message);

        switch (deviceType) {
            case VitalsConstant.TYPE_TS28B:

                switch (action) {
                    case TS28BProfile.ACTION_BATTERY:
                        try {
                            JSONObject info = new JSONObject(message);
                            String battery = info.getString(TS28BProfile.BATTERY);
                            vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,Integer.parseInt(battery)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,-1));
                        }
                        break;
                    case TS28BProfile.ACTION_MEASUREMENT_RESULT:
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            Object result =  jsonObject.get("result");
                            String modified;
                            if (result instanceof Double) {
                                modified = String.format("%.2f", (Double)result);
                            } else {
                                modified = ((int)result + ".0");
                            }

                            thermoMeasureInterface.updateThermoValue(deviceType,Double.parseDouble(modified));

                        } catch (Exception e) {
                            thermoMeasureInterface.updateThermoValue(deviceType,0.0);
                        }
                        break;
                    case TS28BProfile.ACTION_COMMUNICATION_TIMEOUT:
                        thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,context.getString(R.string.communication_timeout));
                        break;
                }
                break;
            case VitalsConstant.TYPE_FDIR_V3:
                switch (action) {
                    case BtmProfile.ACTION_BTM_BATTERY:
                        try {
                            JSONObject info = new JSONObject(message);
                            String battery = info.getString(BtmProfile.BTM_BATTERY);
                            vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,Integer.parseInt(battery)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(deviceType,mac,-1));
                        }
                        break;
                    case BtmProfile.ACTION_BTM_MEMORY:
                        break;
                    case BtmProfile.ACTION_BTM_MEASURE:

                        try {
                            JSONObject info = new JSONObject(message);

                            Object result =  info.get(BtmProfile.BTM_TEMPERATURE);

                            String modified;
                            if (result instanceof Double) {
                                modified = String.format("%.2f", (Double)result);
                            } else {
                                modified = ((int)result + ".0");
                            }

                            thermoMeasureInterface.updateThermoValue(deviceType,Double.parseDouble(modified));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,context.getString(R.string.no_previous_value));
                        }


                        break;
                    case BtmProfile.ACTION_BTM_CALLBACK:
                        break;
                    case BtmProfile.ACTION_ERROR_BTM:
                        try {
                            JSONObject info = new JSONObject(message);
                            String descriptin = info.getString(BtmProfile.ERROR_DESCRIPTION_BTM);
                            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,descriptin);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,e.getLocalizedMessage());
                        }
                        break;

                }
                break;
        }

    }

    public void startMeasure(String type, String deviceMac) {

        switch (type) {
            case iHealthDevicesManager.TYPE_TS28B:
                startTS28bMeasure(type,deviceMac);
                break;
            case iHealthDevicesManager.TYPE_FDIR_V3:
                startFDIRMeasure(type,deviceMac);
                break;
            default:
                break;
        }
    }

    public void stopMeasure(String type,String deviceMac) {

    }

    public Boolean hasValidController(String deviceType,String mac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_TS28B:
               return getInstance(deviceType,mac) != null;
            case iHealthDevicesManager.TYPE_FDIR_V3:
                return getInstance(deviceType,mac) != null;
            default:
                return false;
        }
    }

    private Object getInstance(String deviceType,String mac) {
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_TS28B:
                return iHealthDevicesManager.getInstance().getTS28BControl(mac);
            case iHealthDevicesManager.TYPE_FDIR_V3:
                return iHealthDevicesManager.getInstance().getBtmControl(mac);
            default:
                return null;
        }
    }

    public void disconnect(String deviceType, String deviceMac) {
        if (getInstance(deviceType, deviceMac) == null) {
            return;
        }
        switch (deviceType) {
            case iHealthDevicesManager.TYPE_TS28B:
                iHealthDevicesManager.getInstance().getTS28BControl(deviceMac).disconnect();
                break;
            case iHealthDevicesManager.TYPE_FDIR_V3:
                iHealthDevicesManager.getInstance().getBtmControl(deviceMac).disconnect();
                break;
            default:
                break;
        }
    }

    public void fetchBatteryInformation(String type,String deviceMac) {
        switch (type) {
            case iHealthDevicesManager.TYPE_TS28B:
                vitalBatteryFetcher.updateBatteryDetails(new BatteryResult(type,deviceMac,-1));
                break;
            case iHealthDevicesManager.TYPE_FDIR_V3:
                Object object = getInstance(type,deviceMac);
                if (object != null) {
                    fdirControl = (BtmControl) object;
                    Log.d("ThermoControl","called getBattery");
                    fdirControl.getBattery();
                } else {
                    Log.d("ThermoControl","not called getBattery");
                    vitalBatteryFetcher.notConnected(type,deviceMac);
                }
                break;
            default:
                break;
        }
    }

    private void startTS28bMeasure(String deviceType,String deviceMac) {
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            ts28BControl = (TS28BControl) object;
            thermoMeasureInterface.didThermoStartMeasure(deviceType);
            ts28BControl.getMeasurement();
        } else {
            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }

    }

    private void startFDIRMeasure(String deviceType,String deviceMac) {
        Object object = getInstance(deviceType,deviceMac);
        if (object != null) {
            fdirControl = (BtmControl) object;
            thermoMeasureInterface.didThermoStartMeasure(deviceType);
            fdirControl.getMemoryData();
        } else {
            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType,context.getString(R.string.unable_to_connect));
        }

    }
}
