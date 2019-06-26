package com.thealer.telehealer.apilayer.models.vitals.vitalCreation;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.common.AppPreference;
import com.thealer.telehealer.common.PreferenceKeys;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by rsekar on 11/28/18.
 */

public class VitalPairedDevices implements Serializable {

    ArrayList<VitalDevice> devices = new ArrayList<>();

    public ArrayList<VitalDevice> getDevices() {
        return devices;
    }


    public Boolean isAlreadyPaired(String type,String mac) {
        Boolean isPaired = false;
        for (int i = 0;i<devices.size();i++) {
            VitalDevice vitalDevice = devices.get(i);

            if (vitalDevice.getType().equals(type) &&
                    vitalDevice.getDeviceId().equals(mac)) {
                isPaired = true;
                break;
            }
        }

        return isPaired;
    }

    public Boolean isAlreadyPairedType(String type) {
        Boolean isPaired = false;
        for (int i = 0;i<devices.size();i++) {
            VitalDevice vitalDevice = devices.get(i);

            if (vitalDevice.getType().equals(type)) {
                isPaired = true;
                break;
            }
        }

        return isPaired;
    }

    @Nullable
    public VitalDevice getVitalDevice(String type,String mac) {
        VitalDevice vitalDevice = null;
        for (int i = 0;i<devices.size();i++) {
            VitalDevice device = devices.get(i);

            if (device.getType().equals(type) &&
                    device.getDeviceId().equals(mac)) {
                vitalDevice = device;
                break;
            }
        }
        return vitalDevice;
    }

    public static VitalPairedDevices getAllPairedDevices(AppPreference appPreference) {
        String deviceList = appPreference.getString(PreferenceKeys.VITAL_PAIRED_DEVICES_OBJECT);
        Log.v("VitalPairedDevices","data present in pref "+deviceList);
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList <VitalDevice >>(){}.getType();
        ArrayList <VitalDevice > vitalDevices = gson.fromJson(deviceList,type);

        if (vitalDevices != null) {
            Log.v("VitalPairedDevices","data present count"+vitalDevices.size());
            VitalPairedDevices vitalPairedDevices = new VitalPairedDevices();
            vitalPairedDevices.devices = vitalDevices;
            return vitalPairedDevices;
        } else {
            Log.v("VitalPairedDevices","no data");
            return new VitalPairedDevices();
        }
    }

    public static void addPairDevice(VitalDevice device,AppPreference appPreference) {
        VitalPairedDevices vitalPairedDevices = getAllPairedDevices(appPreference);

        if (!vitalPairedDevices.isAlreadyPaired(device.getType(),device.getDeviceId())) {
            vitalPairedDevices.devices.add(device);
            store(vitalPairedDevices, appPreference);
        }
    }

    public static void removePairDevice(VitalDevice device,AppPreference appPreference) {
        VitalPairedDevices pairedDevices = VitalPairedDevices.getAllPairedDevices(appPreference);

        for (int i = 0;i<pairedDevices.getDevices().size();i++) {
            VitalDevice vitalDevice = pairedDevices.getDevices().get(i);
            if (vitalDevice.getType().equals(device.getType()) &&
                    vitalDevice.getDeviceId().equals(device.getDeviceId())) {
                //no need to add
                pairedDevices.getDevices().remove(i);
            }
        }

        store(pairedDevices,appPreference);
    }

    private static void store(VitalPairedDevices vitalPairedDevices,AppPreference appPreference) {
        Gson gson = new Gson();
        String deviceList = gson.toJson(vitalPairedDevices.devices);
        appPreference.setString(PreferenceKeys.VITAL_PAIRED_DEVICES_OBJECT,deviceList);
    }

}
