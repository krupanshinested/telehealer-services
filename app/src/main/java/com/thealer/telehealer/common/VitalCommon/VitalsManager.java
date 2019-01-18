package com.thealer.telehealer.common.VitalCommon;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;

import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.GulcoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalBatteryFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalFirmwareFetcher;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.VitalPairInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.BPMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.PulseMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.ThermoMeasureInterface;
import com.thealer.telehealer.common.VitalCommon.VitalInterfaces.WeightMeasureInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rsekar on 11/27/18.
 */

public class VitalsManager extends BaseApiViewModel implements WeightMeasureInterface,
        BPMeasureInterface,
        ThermoMeasureInterface,
        PulseMeasureInterface,
        GulcoMeasureInterface,
        VitalBatteryFetcher {

    public static VitalsManager instance;

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

    public void startScan(String deviceType) {

    }

    public void connectDevice(String deviceType, String mac) {

    }

    public void startMeasure(String deviceType, String deviceMac) {

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

    public void saveVitals(String measurementType, String value, VitalsApiViewModel vitalsApiViewModel) {

    }

    //WeightMeasureInterface methods
    @Override
    public void updateWeightMessage(String message) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.updateWeightMessage(message);
    }

    @Override
    public void updateWeightValue(Float value) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.updateWeightValue(value);
    }

    @Override
    public void didStartWeightMeasure() {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didStartWeightMeasure();
    }

    @Override
    public void didFinishWeightMeasure(Float weight, String id) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didFinishWeightMeasure(weight, id);
    }

    @Override
    public void didFinishWeightMesureWithFailure(String error) {
        if (weightMeasureInterface != null)
            weightMeasureInterface.didFinishWeightMesureWithFailure(error);
    }

    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String message) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.updateBPMessage(message);
    }

    @Override
    public void didStartBPMesure() {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didStartBPMesure();
    }

    @Override
    public void didUpdateBPMesure(ArrayList<Double> value) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didUpdateBPMesure(value);
    }

    @Override
    public void didUpdateBPM(ArrayList<Double> value) {
        if (bpMeasureInterface != null)
            bpMeasureInterface.didUpdateBPMesure(value);
    }

    @Override
    public void didFinishBPMesure(Double systolicValue, Double diastolicValue, Double heartRate) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "success");
            detail.put("event", "didFinishBPMesure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (bpMeasureInterface != null)
            bpMeasureInterface.didFinishBPMesure(systolicValue, diastolicValue, heartRate);
    }

    @Override
    public void didFailBPMesure(String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didFailBPMesure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (bpMeasureInterface != null)
            bpMeasureInterface.didFailBPMesure(error);
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
    public void updateThermoMessage(String message) {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.updateThermoMessage(message);
    }

    @Override
    public void updateThermoValue(Double value) {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.updateThermoValue(value);
    }

    @Override
    public void didThermoStartMeasure() {
        if (thermoMeasureInterface != null)
            thermoMeasureInterface.didThermoStartMeasure();
    }

    @Override
    public void didThermoFinishMesureWithFailure(String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didThermoFinishMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (thermoMeasureInterface != null)
            thermoMeasureInterface.didThermoFinishMesureWithFailure(error);
    }

    //PulseMeasureInterface  methods
    @Override
    public void updatePulseMessage(String message) {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.updatePulseMessage(message);
    }

    @Override
    public void updatePulseValue(int spo2, int bpm, int wave, float pi) {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.updatePulseValue(spo2, bpm, wave, pi);
    }

    @Override
    public void didFinishMeasure(int spo2, int bpm, int wave, float pi) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "success");
            detail.put("event", "didFinishMeasure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didFinishMeasure(spo2, bpm, wave, pi);
    }

    @Override
    public void didPulseStartMeasure() {
        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didPulseStartMeasure();
    }

    @Override
    public void didPulseFinishMesureWithFailure(String error) {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didPulseFinishMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (pulseMeasureInterface != null)
            pulseMeasureInterface.didPulseFinishMesureWithFailure(error);
    }

    //GulcoMeasureInterface methods
    @Override
    public void updateGulcoMessage(String message) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.updateGulcoMessage(message);
    }

    @Override
    public void updateGulcoValue(int value) {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.updateGulcoValue(value);
    }

    @Override
    public void didGulcoStartMeasure() {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didGulcoStartMeasure();
    }

    @Override
    public void didFinishGulcoMesureWithFailure(String error) {

        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("status", "fail");
            detail.put("reason", error);
            detail.put("event", "didFinishGulcoMesureWithFailure");

            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
        }

        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didFinishGulcoMesureWithFailure(error);
    }

    @Override
    public void didStripInserted() {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didStripInserted();
    }

    @Override
    public void didStripEjected() {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didStripEjected();
    }

    @Override
    public void didBloodDropped() {
        if (gulcoMeasureInterface != null)
            gulcoMeasureInterface.didBloodDropped();
    }

}
