package iHealth;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.manager.iHealthDevicesUpgradeManager;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;
import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalPairedDevices;
import com.thealer.telehealer.common.Logs;
import com.thealer.telehealer.common.VitalCommon.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import iHealth.Controls.BPControl;
import iHealth.Controls.GulcoControl;
import iHealth.Controls.PulseControl;
import iHealth.Controls.ThermoControl;
import iHealth.Controls.WeightControl;

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

    private HashMap<String,Integer> deviceConnectionMap = new HashMap<String, Integer>();
    private CreateVitalApiRequestModel vitalApiRequestModel;

    public iHealthVitalManager(@NonNull Application application) {
        super(application);

        init();

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
        iHealthDevicesManager.getInstance().init(getApplication(), Log.VERBOSE, Log.ASSERT);
        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        try {
            InputStream is = getApplication().getAssets().open("com_thealer_android.pem");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Log.i("info", "isPass:    " + isPass);
        } catch (IOException e) {

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
            Log.v("vitalmanager","onDeviceConnectionStateChange "+deviceType);

            deviceConnectionMap.put(deviceType+"_"+mac,status);

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                if (vitalPairInterface != null)
                    vitalPairInterface.didConnected(deviceType,mac);
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
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
            Logs.V("vitalmanager","nog in connected state ");

            boolean req = iHealthDevicesManager.getInstance().connectDevice(userName, mac,deviceType);

            if (!req) {
                if (vitalPairInterface != null)
                    vitalPairInterface.didFailConnectDevice(deviceType,mac,getApplication().getString(R.string.vital_connection_error));
            }
        }
    }

    @Override
    public void startMeasure(String deviceType,String deviceMac) {
        addFilter(deviceType);

        if (!isConnected(deviceType,deviceMac)) {
            connectDevice(deviceType,deviceMac);
            return;
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
    }

    @Override
    public void updateStripBottleId(String deviceType,String mac,String result) {
        addFilter(deviceType);

        gulcoControl.updateStripBottleId(deviceType,mac,result);
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

        int connectionType = -1;
        try {
            Object connection = deviceConnectionMap.get(deviceType+"_"+mac);
            if (connection != null) {
                connectionType = (int) connection;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logs.V("vitalmanager","isConnected "+e.getLocalizedMessage());
        }

        Logs.V("vitalmanager","connectionType "+connectionType);

        return connectionType != -1 && connectionType == iHealthDevicesManager.DEVICE_STATE_CONNECTED;
    }

    @Override
    public void saveVitals(String measurementType,String value,VitalsApiViewModel vitalsApiViewModel) {
        CreateVitalApiRequestModel vitalApiRequestModel = new CreateVitalApiRequestModel();

        vitalApiRequestModel.setType(measurementType);
        vitalApiRequestModel.setMode(VitalsConstant.VITAL_MODE_DEVICE);
        vitalApiRequestModel.setValue(value);

        vitalsApiViewModel.createVital(vitalApiRequestModel);
        this.vitalApiRequestModel = vitalApiRequestModel;
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

    private Long getDiscovery(String type) {
        switch (type) {
            case iHealthDevicesManager.TYPE_BP3L:
                return iHealthDevicesManager.DISCOVERY_BP3L;
            case iHealthDevicesManager.TYPE_550BT:
                return iHealthDevicesManager.DISCOVERY_BP550BT;
            case iHealthDevicesManager.TYPE_BP5:
                return iHealthDevicesManager.DISCOVERY_BP5;
            case iHealthDevicesManager.TYPE_BPM1:
                return 12L;//TODO need to work
            case iHealthDevicesManager.TYPE_PO3:
                return iHealthDevicesManager.DISCOVERY_PO3;
            case iHealthDevicesManager.TYPE_HS6:
                return 12L;//TODO need to work
            case iHealthDevicesManager.TYPE_BP7:
                return iHealthDevicesManager.DISCOVERY_BP7;
            case iHealthDevicesManager.TYPE_HS4S:
                return iHealthDevicesManager.DISCOVERY_HS4S;
            case iHealthDevicesManager.TYPE_BG5:
                return iHealthDevicesManager.DISCOVERY_BG5;
            case VitalsConstant.TYPE_FDIR_V3:
                return iHealthDevicesManager.DISCOVERY_FDIR_V3;
            case iHealthDevicesManager.TYPE_TS28B:
                return iHealthDevicesManager.DISCOVERY_TS28B;
            case iHealthDevicesManager.TYPE_BP7S:
                return iHealthDevicesManager.DISCOVERY_BP7S;
            default:
                return 124L;
        }
    }

}
