package com.thealer.telehealer.common.VitalCommon;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.ThermoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalFirmwareFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.WeightMeasureInterface;

import java.util.ArrayList;
import java.util.HashMap;

import iHealth.iHealthVitalManager;

/**
 * Created by rsekar on 11/27/18.
 */

public class VitalsManager extends BaseApiViewModel implements WeightMeasureInterface,
        BPMeasureInterface,
        ThermoMeasureInterface,
        PulseMeasureInterface,
        GulcoMeasureInterface,
        VitalBatteryFetcher {

    @Nullable
    private static VitalsManager instance;

    protected @Nullable
    VitalPairInterface vitalPairInterface;
    private @Nullable
    BPMeasureInterface bpMeasureInterface;
    private @Nullable
    ThermoMeasureInterface thermoMeasureInterface;
    private @Nullable
    WeightMeasureInterface weightMeasureInterface;
    private @Nullable
    PulseMeasureInterface pulseMeasureInterface;
    private @Nullable
    GulcoMeasureInterface gulcoMeasureInterface;
    private @Nullable
    VitalBatteryFetcher vitalBatteryFetcher;
    private @Nullable
    VitalFirmwareFetcher vitalFirmwareFetcher;

    public VitalsManager(@NonNull Application application) {
        super(application);

        init();
    }

    public static VitalsManager getInstance() {
        if (VitalsManager.instance != null) {
            return VitalsManager.instance;
        } else {
            if (BuildConfig.FLAVOR.equals(Constants.BUILD_DOCTOR)) {
                VitalsManager.instance = new VitalsManager(TeleHealerApplication.application);
            } else {
                VitalsManager.instance = new iHealthVitalManager(TeleHealerApplication.application);
            }
            return VitalsManager.instance;
        }
    }

    private void init() {

    }

    public void deAllocate() {

    }

    public void disconnectAll() {

    }

    public void disconnect(String deviceType, String mac) {

    }

    public void setListener(VitalPairInterface vitalPairInterface) {
        this.vitalPairInterface = vitalPairInterface;
    }

    public void setBPListener(BPMeasureInterface bpMeasureInterface) {
        this.bpMeasureInterface = bpMeasureInterface;
    }

    public void setThermoListener(ThermoMeasureInterface thermoMeasureInterface) {
        this.thermoMeasureInterface = thermoMeasureInterface;
    }

    public void setGulcoListener(GulcoMeasureInterface gulcoListener) {
        this.gulcoMeasureInterface = gulcoListener;
    }

    public void setPulseListener(PulseMeasureInterface pulseListener) {
        this.pulseMeasureInterface = pulseListener;
    }

    public void setWeightListener(WeightMeasureInterface weightMeasureInterface) {
        this.weightMeasureInterface = weightMeasureInterface;
    }

    public void setBatteryFetcherListener(VitalBatteryFetcher vitalBatteryFetcher) {
        this.vitalBatteryFetcher = vitalBatteryFetcher;
    }

    public void setFirmwareFetcherListener(VitalFirmwareFetcher vitalFirmwareFetcher) {
        this.vitalFirmwareFetcher = vitalFirmwareFetcher;
    }

    public void reset(Object object) {
        if (vitalPairInterface == object) {
            vitalPairInterface = null;
        }

        if (bpMeasureInterface == object) {
            bpMeasureInterface = null;
        }

        if (weightMeasureInterface == object) {
            weightMeasureInterface = null;
        }

        if (pulseMeasureInterface == object) {
            pulseMeasureInterface = null;
        }

        if (thermoMeasureInterface == object) {
            thermoMeasureInterface = null;
        }

        if (gulcoMeasureInterface == object) {
            gulcoMeasureInterface = null;
        }

        if (vitalBatteryFetcher == object) {
            vitalBatteryFetcher = null;
        }

        if (vitalFirmwareFetcher == object) {
            vitalFirmwareFetcher = null;
        }
    }

    public void resetAll() {
        vitalPairInterface = null;
        bpMeasureInterface = null;
        weightMeasureInterface = null;
        pulseMeasureInterface = null;
        thermoMeasureInterface = null;
        gulcoMeasureInterface = null;
        vitalBatteryFetcher = null;
        vitalFirmwareFetcher = null;
    }

    public void startScan(String deviceType) {

    }

    public void connectDevice(String deviceType, String mac) {

    }

    public boolean startMeasure(String deviceType, String deviceMac) {
        return false;
    }

    public void updateStripBottleId(String deviceType, String mac, String result) {
    }

    public void stopMeasure(String deviceType, String deviceMac) {
    }

    public void fetchBattery(String deviceType, String deviceMac) {
    }

    public void getFirmWareInfo(String deviceType, String mac) {

    }

    public void downloadFirmware(String type, String productModel, String hardwareVersion, String firmwareVersion) {
    }

    public void updateFirmware(String mac, String type, String productModel, String hardwareVersion, String firmwareVersion, String fileCode) {
    }

    protected Boolean hasValidController(String deviceType, String mac) {
        return false;
    }

    public Boolean isConnected(String deviceType, String mac) {
        return hasValidController(deviceType, mac);
    }

    //WeightMeasureInterface methods
    @Override
    public void updateWeightMessage(String deviceType, String message) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.updateWeightMessage(deviceType, message);
    }

    @Override
    public void updateWeightValue(String deviceType, Float value) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.updateWeightValue(deviceType, value);
    }

    @Override
    public void didStartWeightMeasure(String deviceType) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didStartWeightMeasure(deviceType);

    }

    @Override
    public void didFinishWeightMeasure(String deviceType, Float weight, String id) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didFinishWeightMeasure(deviceType, weight, id);
    }

    @Override
    public void didFinishWeightMesureWithFailure(String deviceType, String error) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didFinishWeightMesureWithFailure(deviceType, error);
    }

    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String deviceType, String message) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.updateBPMessage(deviceType, message);
    }

    @Override
    public void didStartBPMesure(String deviceType) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didStartBPMesure(deviceType);

    }

    @Override
    public void didUpdateBPMesure(String deviceType, ArrayList<Double> value) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didUpdateBPMesure(deviceType, value);

    }

    @Override
    public void didUpdateBPM(String deviceType, ArrayList<Double> value) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didUpdateBPMesure(deviceType, value);
    }

    @Override
    public void didFinishBPMesure(String deviceType, Double systolicValue, Double diastolicValue, Double heartRate) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "success");
            detail.put("event", "didFinishBPMesure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (bpMeasureInterface != null)
            bpMeasureInterface.didFinishBPMesure(deviceType, systolicValue, diastolicValue, heartRate);
    }

    @Override
    public void didFailBPMesure(String deviceType, String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didFailBPMesure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (bpMeasureInterface != null)
            bpMeasureInterface.didFailBPMesure(deviceType, error);
    }

    @Override
    public void didFinishBpMeasure(Object object) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "success");
            detail.put("event", "didFinishBPMesure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }


        if (bpMeasureInterface != null)
            bpMeasureInterface.didFinishBpMeasure(object);
    }

    //VitalBatteryFetcher methods
    @Override
    public void updateBatteryDetails(BatteryResult batteryResult) {
        if (vitalBatteryFetcher != null) {
            vitalBatteryFetcher.updateBatteryDetails(batteryResult);
        }
    }

    @Override
    public void notConnected(String deviceType, String deviceMac) {
        if (vitalBatteryFetcher != null) {
            vitalBatteryFetcher.notConnected(deviceType, deviceMac);
        }
    }

    //ThermoMeasureInterface methods
    @Override
    public void updateThermoMessage(String deviceType, String message) {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.updateThermoMessage(deviceType, message);
    }

    @Override
    public void updateThermoValue(String deviceType, Double value) {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.updateThermoValue(deviceType, value);
    }

    @Override
    public void didThermoStartMeasure(String deviceType) {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.didThermoStartMeasure(deviceType);
    }

    @Override
    public void didThermoFinishMesureWithFailure(String deviceType, String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didThermoFinishMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (thermoMeasureInterface != null)
            thermoMeasureInterface.didThermoFinishMesureWithFailure(deviceType, error);
    }

    //PulseMeasureInterface  methods
    @Override
    public void updatePulseMessage(String deviceType, String message) {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.updatePulseMessage(deviceType, message);
    }

    @Override
    public void updatePulseValue(String deviceType, int spo2, int bpm, int wave, float pi) {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.updatePulseValue(deviceType, spo2, bpm, wave, pi);
    }

    @Override
    public void didFinishMeasure(String deviceType, int spo2, int bpm, int wave, float pi) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "success");
            detail.put("event", "didFinishMeasure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didFinishMeasure(deviceType, spo2, bpm, wave, pi);
    }

    @Override
    public void didPulseStartMeasure(String deviceType) {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didPulseStartMeasure(deviceType);

    }

    @Override
    public void didPulseFinishMesureWithFailure(String deviceType, String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didPulseFinishMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didPulseFinishMesureWithFailure(deviceType, error);
    }

    //GulcoMeasureInterface methods
    @Override
    public void updateGulcoMessage(String deviceType, String message) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.updateGulcoMessage(deviceType, message);
    }

    @Override
    public void updateGulcoValue(String deviceType, int value) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.updateGulcoValue(deviceType, value);
    }

    @Override
    public void didGulcoStartMeasure(String deviceType) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didGulcoStartMeasure(deviceType);
    }

    @Override
    public void didFinishGulcoMesureWithFailure(String deviceType, String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didFinishGulcoMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didFinishGulcoMesureWithFailure(deviceType, error);
    }

    @Override
    public void didStripInserted(String deviceType) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didStripInserted(deviceType);
    }

    @Override
    public void didStripEjected(String deviceType) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didStripEjected(deviceType);
    }

    @Override
    public void didBloodDropped(String deviceType) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didBloodDropped(deviceType);
    }

}
