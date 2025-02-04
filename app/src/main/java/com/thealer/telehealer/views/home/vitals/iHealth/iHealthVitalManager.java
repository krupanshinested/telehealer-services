package com.thealer.telehealer.views.home.vitals.iHealth;

import android.app.Application;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.manager.iHealthDevicesUpgradeManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.BPTrack;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.Logs;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.common.VitalCommon.*;
import com.thealer.telehealer.views.home.vitals.iHealth.Controls.BPControl;
import com.thealer.telehealer.views.home.vitals.iHealth.Controls.GulcoControl;
import com.thealer.telehealer.views.home.vitals.iHealth.Controls.PulseControl;
import com.thealer.telehealer.views.home.vitals.iHealth.Controls.ThermoControl;
import com.thealer.telehealer.views.home.vitals.iHealth.Controls.WeightControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 12/18/18.
 */

public class iHealthVitalManager extends VitalsManager {

    private int callbackId;
    private final String userName = "twentydeg@gmail.com";

    private final BPControl bpControl;
    private final WeightControl weightControl;
    private final ThermoControl thermoControl;
    private final PulseControl pulseControl;
    private final GulcoControl gulcoControl;

    private static HashMap<String,Integer> deviceConnectionMap = new HashMap<String, Integer>();

    public iHealthVitalManager(@NonNull Application application) {
        super(application);

        try {
            init();
        }catch (Exception e){
            e.printStackTrace();
        }

        bpControl = new BPControl(getApplication(),this,this);
        weightControl = new WeightControl(getApplication(),this,this);
        thermoControl = new ThermoControl(getApplication(),this,this);
        pulseControl  = new PulseControl(getApplication(),this,this);
        gulcoControl = new GulcoControl(getApplication(),this,this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        deAllocate();
    }

    private void init() {
        iHealthDevicesManager.getInstance().init(getApplication(), Log.VERBOSE, Log.VERBOSE);
        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        String fileName = TeleHealerApplication.appConfig.getVitalPemFileName();
        if (fileName == null) {
            return;
        }
        try {
            InputStream is = getApplication().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = false;
            try {
                isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (isPass) {
                HashMap<String,String> detail = new HashMap<>();
                detail.put("status","success");
                detail.put("event","registerDevice");
                TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
            } else {
                HashMap<String,String> detail = new HashMap<>();
                detail.put("status","fail");
                detail.put("event","registerDevice");
                TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);
            }

        } catch (IOException e) {

            HashMap<String,String> detail = new HashMap<>();
            detail.put("status","fail");
            detail.put("reason",e.getLocalizedMessage());
            detail.put("event","registerDevice");
            TeleLogger.shared.log(TeleLogExternalAPI.ihealth, detail);

            Log.i("info", "health failed "+e.getMessage());
            Log.i("info", "health failed "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /*
     * When the Activity is destroyed , need to call destroy method of iHealthDeivcesManager to
     * release resources
    */
    @Override
    public void deAllocate() {
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
        iHealthDevicesManager.getInstance().destroy();
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi) {
            Log.v("vitalmanager","onScanDevice d"+mac);

            EventRecorder.recordVitals("DEVICE_DISCOVER_SUCCESS", deviceType);

            if (vitalPairInterface != null) {
                vitalPairInterface.didDiscoverDevice(deviceType, mac);
            }
        }

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map<String, Object> manufactorData) {
            Log.v("vitalmanager","onScanDevice with data d"+mac);
        }

        @Override
        public void onScanFinish() {
            Log.v("vitalmanager","onScanFinish ");
            if (vitalPairInterface != null) {
                vitalPairInterface.didScanFinish();
            }
        }

        @Override
        public void onScanError(String reason, long latency) {
            Log.v("vitalmanager","onScanError ");
            if (vitalPairInterface != null) {
                vitalPairInterface.didScanFailed(reason);
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            Log.v("vitalmanager","onDeviceConnectionStateChange "+deviceType+" status "+status+" errorID "+errorID);


            deviceConnectionMap.put(deviceType+"_"+mac,status);

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                EventRecorder.recordVitals("DEVICE_CONNECT_SUCCESS", deviceType);

                if (vitalPairInterface != null)
                    vitalPairInterface.didConnected(deviceType,mac);

                if (deviceType.equals(iHealthDevicesManager.TYPE_550BT)) {
                    bpControl.syncTimeForTrack(mac);
                }

            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {

                EventRecorder.recordVitals("DEVICE_CONNECT_FAIL", deviceType);

                if (vitalPairInterface != null)
                    vitalPairInterface.didDisConnected(deviceType,mac);
            }

            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(new Intent(getApplication().getString(R.string.did_device_state_changed)));
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.v("vitalmanager","onDeviceConnectionStateChange with data");

        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Log.v("vitalmanager","onUserStatus "+username+userStatus);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.v("vitalmanager","onDeviceNotify "+action+" fkkf "+message);
            switch (VitalDeviceType.shared.getMeasurementType(deviceType)) {
                case SupportedMeasurementType.bp:
                    bpControl.onDeviceNotify(mac,deviceType,action,message);
                    break;
                case SupportedMeasurementType.gulcose:
                    gulcoControl.onDeviceNotify(mac,deviceType,action,message);
                    break;
                case SupportedMeasurementType.heartRate:
                    break;
                case SupportedMeasurementType.pulseOximeter:
                    pulseControl.onDeviceNotify(mac,deviceType,action,message);
                    break;
                case SupportedMeasurementType.weight:
                    weightControl.onDeviceNotify(mac,deviceType,action,message);
                    break;
                case SupportedMeasurementType.temperature:
                    thermoControl.onDeviceNotify(mac,deviceType,action,message);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void startScan(String deviceType) {
        addFilter(deviceType);

        Log.v("vitalmanager","startScan "+deviceType);
        iHealthDevicesManager.getInstance().startDiscovery(getDiscovery(deviceType));
    }

    @Override
    public void disconnectAll() {
        Log.d("vitalmanager","disconnect all");
        VitalPairedDevices vitalPairedDevices = VitalPairedDevices.getAllPairedDevices(appPreference);
        for (VitalDevice device : vitalPairedDevices.getDevices()) {
            iHealthDevicesManager.getInstance().disconnectDevice(device.getDeviceId(),device.getType());
            if (VitalDeviceType.shared.getMeasurementType(device.getType()).equals(SupportedMeasurementType.bp)) {
                bpControl.disconnect(device.getType(), device.getDeviceId());
            }
        }
    }

    @Override
    public void disconnect(String deviceType,String mac) {
        addFilter(deviceType);

        Log.d("vitalmanager","disconnect all");
        iHealthDevicesManager.getInstance().disconnectDevice(mac,deviceType);
    }

    @Override
    public void getFirmWareInfo(String deviceType,String mac) {
        addFilter(deviceType);

        UpgradeControl.getInstance().queryDeviceFirmwareInfo(mac,deviceType);
        iHealthDevicesUpgradeManager.getInstance().getUpDeviceControl(mac,deviceType);
    }

    @Override
    public void downloadFirmware(String type,String productModel,String hardwareVersion,String firmwareVersion) {
        addFilter(type);

        UpgradeControl.getInstance().downloadFirmwareFile(type,productModel,hardwareVersion,firmwareVersion);
    }

    @Override
    public void updateFirmware(String mac,String type,String productModel,String hardwareVersion,String firmwareVersion,String fileCode) {
        addFilter(type);

        UpgradeControl.getInstance().startUpgrade(mac,type,productModel,hardwareVersion,firmwareVersion,fileCode);
    }

    @Override
    public void connectDevice(String deviceType,String mac) {
        addFilter(deviceType);

        if (isConnected(deviceType,mac))  {
            Logs.V("vitalmanager","already in connected state "+deviceType);
            if (vitalPairInterface != null)
                vitalPairInterface.didConnected(deviceType,mac);
        } else {
            Logs.V("vitalmanager","not in connected state ");

            boolean req = iHealthDevicesManager.getInstance().connectDevice(userName, mac,deviceType);

            if (!req) {
                if (vitalPairInterface != null)
                    vitalPairInterface.didFailConnectDevice(deviceType,mac,getApplication().getString(R.string.vital_connection_error));
            } else {
                if (vitalPairInterface != null)
                    vitalPairInterface.startedToConnect(deviceType,mac);
            }
        }
    }

    @Override
    public boolean startMeasure(String deviceType,String deviceMac) {
        Log.d("VitalManager","startMeasure "+deviceMac+" "+deviceType);
        addFilter(deviceType);

        int connectingStatus = getConnectionStatus(deviceType,deviceMac,true);
        if (connectingStatus != iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
            switch (connectingStatus) {
                case iHealthDevicesManager.DEVICE_STATE_CONNECTING:
                    Log.d("VitalManager","DEVICE_STATE_CONNECTING");
                    break;
                case iHealthDevicesManager.DEVICE_STATE_RECONNECTING:
                    Log.d("VitalManager","DEVICE_STATE_RECONNECTING");
                    break;
                case iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL:
                case iHealthDevicesManager.DEVICE_STATE_DISCONNECTED:
                    Log.d("VitalManager","not connected");
                    connectDevice(deviceType,deviceMac);
                    break;
            }
            return false;
        }

        switch (VitalDeviceType.shared.getMeasurementType(deviceType)) {
            case SupportedMeasurementType.bp:
                bpControl.startMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.gulcose:
                gulcoControl.startMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.heartRate:
                break;
            case SupportedMeasurementType.pulseOximeter:
                pulseControl.startMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.weight:
                weightControl.startMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.temperature:
                thermoControl.startMeasure(deviceType,deviceMac);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void updateStripBottleId(String deviceType,String mac,String result) {
        addFilter(deviceType);

        gulcoControl.updateStripBottleId(deviceType,mac,result);
    }

    @Override
    public void updateStripType(String deviceType, String mac, Boolean isGod) {
        gulcoControl.updateStripType(deviceType,mac,isGod);
    }

    @Override
    public void stopMeasure(String deviceType,String deviceMac) {
        addFilter(deviceType);

        switch (VitalDeviceType.shared.getMeasurementType(deviceType)) {
            case SupportedMeasurementType.bp:
                bpControl.stopMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.gulcose:
                gulcoControl.stopMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.heartRate:
                break;
            case SupportedMeasurementType.pulseOximeter:
                pulseControl.stopMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.weight:
                weightControl.stopMeasure(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.temperature:
                thermoControl.stopMeasure(deviceType,deviceMac);
                break;
            default:
                break;
        }

        if (UserType.isUserPatient()) {
            HashMap<String,Object> message = new HashMap<>();
            message.put(VitalsConstant.VitalCallMapKeys.status,VitalsConstant.VitalCallMapKeys.cancelled);
            message.put(VitalsConstant.VitalCallMapKeys.message,"");
            publishMessage(deviceType,message);
        }
    }

    @Override
    public void fetchBattery(String deviceType,String deviceMac) {
        addFilter(deviceType);

        switch (VitalDeviceType.shared.getMeasurementType(deviceType)) {
            case SupportedMeasurementType.bp:
                bpControl.fetchBatteryInformation(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.gulcose:
                gulcoControl.fetchBatteryInformation(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.heartRate:
                break;
            case SupportedMeasurementType.pulseOximeter:
                pulseControl.fetchBatteryInformation(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.weight:
                weightControl.fetchBatteryInformation(deviceType,deviceMac);
                break;
            case SupportedMeasurementType.temperature:
                thermoControl.fetchBatteryInformation(deviceType,deviceMac);
                break;
            default:
                break;
        }
    }

    @Override
    protected Boolean hasValidController(String deviceType,String mac) {
        addFilter(deviceType);

        switch (VitalDeviceType.shared.getMeasurementType(deviceType)) {
            case SupportedMeasurementType.bp:
                return bpControl.hasValidController(deviceType,mac);
            case SupportedMeasurementType.gulcose:
                return gulcoControl.hasValidController(deviceType,mac);
            case SupportedMeasurementType.heartRate:
                return false;
            case SupportedMeasurementType.pulseOximeter:
                return pulseControl.hasValidController(deviceType,mac);
            case SupportedMeasurementType.weight:
                return weightControl.hasValidController(deviceType,mac);
            case SupportedMeasurementType.temperature:
                return thermoControl.hasValidController(deviceType,mac);
            default:
                return false;
        }
    }

    @Override
    public Boolean isConnected(String deviceType,String mac) {
        addFilter(deviceType);

        int connectionType = getConnectionStatus(deviceType,mac,false);
        return connectionType == iHealthDevicesManager.DEVICE_STATE_CONNECTED;
    }

    private int getConnectionStatus(String deviceType,String mac,boolean needDisconnect) {
        addFilter(deviceType);

        int connectionType = iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL;
        try {
            Object connection = deviceConnectionMap.get(deviceType + "_" + mac);
            if (connection != null) {
                connectionType = (int) connection;
            }

            if (connectionType != iHealthDevicesManager.DEVICE_STATE_CONNECTED && needDisconnect) {
                disconnect(deviceType,mac);
                connectionType = iHealthDevicesManager.DEVICE_STATE_DISCONNECTED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logs.V("vitalmanager","isConnected "+e.getLocalizedMessage());
        }

        Logs.V("vitalmanager","connectionType "+connectionType);

        return connectionType;
    }

    private void addFilter(String deviceType) {
       /* Log.d("vitalmanager","addFilter "+deviceType);

        ArrayList<String> types = new ArrayList<String>(Arrays.asList(VitalDeviceType.shared.getFilterType(deviceType)));

        for (Object o : deviceConnectionMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if((Integer)pair.getValue() == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                String []split = ((String)pair.getKey()).split("_");
                types.add(split[0]);
            }
        }
        Set<String> unique = new HashSet<String>(types);
        String[] filterTypes = new String[unique.size()];
        filterTypes = unique.toArray(filterTypes);
        Log.d("vitalmanager","addFilter for "+ Arrays.toString(filterTypes));
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(callbackId, filterTypes);*/
    }

    private DiscoveryTypeEnum getDiscovery(String type) {
        switch (type) {
            case iHealthDevicesManager.TYPE_BP3L:
                return DiscoveryTypeEnum.BP3L;
            case iHealthDevicesManager.TYPE_550BT:
                return DiscoveryTypeEnum.BP550BT;
            case iHealthDevicesManager.TYPE_BP5:
                return DiscoveryTypeEnum.BP5;
            case iHealthDevicesManager.TYPE_PO3:
                return DiscoveryTypeEnum.PO3;
            case iHealthDevicesManager.TYPE_BP7:
                return DiscoveryTypeEnum.BP7;
            case iHealthDevicesManager.TYPE_HS4S:
                return DiscoveryTypeEnum.HS4S;
            case iHealthDevicesManager.TYPE_BG5:
                return DiscoveryTypeEnum.BG5;
            case iHealthDevicesManager.TYPE_FDIR_V3:
                return DiscoveryTypeEnum.FDIR_V3;
            case iHealthDevicesManager.TYPE_TS28B:
                return DiscoveryTypeEnum.TS28B;
            case iHealthDevicesManager.TYPE_BP7S:
                return DiscoveryTypeEnum.BP7S;
            case iHealthDevicesManager.TYPE_HS2:
                return DiscoveryTypeEnum.HS2;
            default:
                return DiscoveryTypeEnum.All;
        }
    }

    private void publishThatDeviceStarted(String deviceType) {
        EventRecorder.recordVitals("measurement_started", deviceType);
        EventRecorder.recordVitalsStartMeasurement(deviceType);

        HashMap<String, Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.startedToMeasure);
        message.put(VitalsConstant.VitalCallMapKeys.message, "");
        publishMessage(deviceType, message);
    }

    private void publishMessage(String type,HashMap<String,Object> message) {
        if (UserType.isUserPatient()) {
            if (CallManager.shared.isActiveCallPresent()) {

                EventRecorder.recordVitals("publish_vital_result",type);

                String deviceType = VitalDeviceType.shared.getKeyValue(type);
                CallManager.shared.getActiveCallToShow().sendMessage(deviceType.replaceAll(" ","_"), message);
                Log.d("VitalManager","publishMessage "+type+" - "+message.toString());
            }
        }
    }

    //WeightMeasureInterface methods
    @Override
    public void updateWeightMessage(String deviceType,String message) {
        super.updateWeightMessage(deviceType,message);

    }

    @Override
    public void updateWeightValue(String deviceType,Float value) {
        super.updateWeightValue(deviceType,value);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.measuring);
        message.put(VitalsConstant.VitalCallMapKeys.data, value);
        publishMessage(deviceType,message);
    }

    @Override
    public void didStartWeightMeasure(String deviceType) {
        super.didStartWeightMeasure(deviceType);

        publishThatDeviceStarted(deviceType);
    }

    @Override
    public void didFinishWeightMeasure(String deviceType,Float weight, String id) {
        super.didFinishWeightMeasure(deviceType,weight,id);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.finishedMeasure);
        message.put(VitalsConstant.VitalCallMapKeys.data, weight);
        publishMessage(deviceType,message);
    }

    @Override
    public void didFinishWeightMesureWithFailure(String deviceType,String error) {
        super.didFinishWeightMesureWithFailure(deviceType,error);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.errorInMeasure);
        if (TextUtils.isEmpty(error)) {
            message.put(VitalsConstant.VitalCallMapKeys.message, error);
        } else {
            message.put(VitalsConstant.VitalCallMapKeys.message, getApplication().getString(R.string.error_on_measure_vital));
        }
        publishMessage(deviceType,message);
    }

    //BPMeasureInterface methods
    @Override
    public void updateBPMessage(String deviceType,String message) {
       super.updateBPMessage(deviceType,message);
    }

    @Override
    public void didStartBPMesure(String deviceType) {
       super.didStartBPMesure(deviceType);

        publishThatDeviceStarted(deviceType);
    }

    @Override
    public void didUpdateBPMesure(String deviceType,ArrayList<Double> value) {
        super.didUpdateBPMesure(deviceType,value);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.measuring);
        message.put(VitalsConstant.VitalCallMapKeys.data, value);
        publishMessage(deviceType,message);
    }

    @Override
    public void didUpdateBPM(String deviceType,ArrayList<Double> value) {
        super.didUpdateBPM(deviceType,value);
    }

    @Override
    public void didFinishBPMesure(String deviceType,Double systolicValue, Double diastolicValue, Double heartRate) {
        super.didFinishBPMesure(deviceType,systolicValue,diastolicValue,heartRate);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.finishedMeasure);
        message.put(VitalsConstant.VitalCallMapKeys.systolicValue, systolicValue);
        message.put(VitalsConstant.VitalCallMapKeys.diastolicValue, diastolicValue);
        message.put(VitalsConstant.VitalCallMapKeys.heartRate, heartRate);
        publishMessage(deviceType,message);
    }

    @Override
    public void didFailBPMesure(String deviceType,String error) {
        super.didFailBPMesure(deviceType,error);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.errorInMeasure);
        if (TextUtils.isEmpty(error)) {
            message.put(VitalsConstant.VitalCallMapKeys.message, error);
        } else {
            message.put(VitalsConstant.VitalCallMapKeys.message, getApplication().getString(R.string.error_on_measure_vital));
        }

        publishMessage(deviceType,message);
    }

    @Override
    public void didFinishBpMeasure(Object object){
        super.didFinishBpMeasure(object);

        ArrayList<BPTrack> tracks = (ArrayList<BPTrack>) object;

        ArrayList<HashMap<String,String>> items = new ArrayList<>();

        for(BPTrack track : tracks) {
            items.add(track.getDictionary());
        }

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.finishedMeasure);
        message.put(VitalsConstant.VitalCallMapKeys.data, items);
        publishMessage(VitalsConstant.TYPE_550BT,message);

    }

    //ThermoMeasureInterface methods
    @Override
    public void updateThermoMessage(String deviceType,String message) {
        super.updateThermoMessage(deviceType,message);
    }

    @Override
    public void updateThermoValue(String deviceType,Double value) {
        super.updateThermoValue(deviceType,value);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.finishedMeasure);
        message.put(VitalsConstant.VitalCallMapKeys.data, value);
        publishMessage(deviceType,message);
    }

    @Override
    public void didThermoStartMeasure(String deviceType) {
        super.didThermoStartMeasure(deviceType);

        publishThatDeviceStarted(deviceType);
    }

    @Override
    public void didThermoFinishMesureWithFailure(String deviceType,String error) {
        super.didThermoFinishMesureWithFailure(deviceType,error);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.errorInMeasure);
        if (TextUtils.isEmpty(error)) {
            message.put(VitalsConstant.VitalCallMapKeys.message, error);
        } else {
            message.put(VitalsConstant.VitalCallMapKeys.message, getApplication().getString(R.string.error_on_measure_vital));
        }

        publishMessage(deviceType,message);
    }

    //PulseMeasureInterface  methods
    @Override
    public void updatePulseMessage(String deviceType,String message) {
        super.updatePulseMessage(deviceType,message);
    }

    @Override
    public void updatePulseValue(String deviceType,int spo2, int bpm, int wave, float pi) {
        super.updatePulseValue(deviceType,spo2,bpm,wave,pi);

        HashMap<String,Object> message = new HashMap<>();
        HashMap<String,Object> result = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.measuring);

        result.put(VitalsConstant.VitalCallMapKeys.spo2, spo2);
        result.put(VitalsConstant.VitalCallMapKeys.bpm, bpm);
        result.put(VitalsConstant.VitalCallMapKeys.pi, pi);

        message.put(VitalsConstant.VitalCallMapKeys.data, result);
        publishMessage(deviceType,message);
    }

    @Override
    public void didFinishMeasure(String deviceType,int spo2, int bpm, int wave, float pi) {
        super.didFinishMeasure(deviceType,spo2,bpm,wave,pi);

        HashMap<String,Object> message = new HashMap<>();
        HashMap<String,Object> result = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.finishedMeasure);

        result.put(VitalsConstant.VitalCallMapKeys.spo2, spo2);
        result.put(VitalsConstant.VitalCallMapKeys.bpm, bpm);
        result.put(VitalsConstant.VitalCallMapKeys.pi, pi);

        message.put(VitalsConstant.VitalCallMapKeys.data, result);
        publishMessage(deviceType,message);
    }

    @Override
    public void didPulseStartMeasure(String deviceType) {
        super.didPulseStartMeasure(deviceType);

        publishThatDeviceStarted(deviceType);
    }

    @Override
    public void didPulseFinishMesureWithFailure(String deviceType,String error) {
        super.didPulseFinishMesureWithFailure(deviceType,error);

        HashMap<String,Object> message = new HashMap<>();
        message.put(VitalsConstant.VitalCallMapKeys.status, VitalsConstant.VitalCallMapKeys.errorInMeasure);
        if (TextUtils.isEmpty(error)) {
            message.put(VitalsConstant.VitalCallMapKeys.message, error);
        } else {
            message.put(VitalsConstant.VitalCallMapKeys.message, getApplication().getString(R.string.error_on_measure_vital));
        }

        publishMessage(deviceType,message);
    }

    //GulcoMeasureInterface methods
    @Override
    public void updateGulcoMessage(String deviceType,String message) {
        super.updateGulcoMessage(deviceType,message);
    }

    @Override
    public void updateGulcoValue(String deviceType,int value) {
       super.updateGulcoValue(deviceType,value);
    }

    @Override
    public void didGulcoStartMeasure(String deviceType) {
        super.didGulcoStartMeasure(deviceType);

        publishThatDeviceStarted(deviceType);
    }

    @Override
    public void didFinishGulcoMesureWithFailure(String deviceType,String error) {
        super.didFinishGulcoMesureWithFailure(deviceType,error);
    }

    @Override
    public void didStripInserted(String deviceType) {
        super.didStripInserted(deviceType);
    }

    @Override
    public void didStripEjected(String deviceType) {
       super.didStripEjected(deviceType);
    }

    @Override
    public void didBloodDropped(String deviceType) {
        super.didBloodDropped(deviceType);
    }


}
