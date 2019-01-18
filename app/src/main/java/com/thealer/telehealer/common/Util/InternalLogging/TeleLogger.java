package com.thealer.telehealer.common.Util.InternalLogging;

import android.text.TextUtils;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.apilayer.models.Logging.LoggingViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;

import java.util.HashMap;
import java.util.UUID;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 1/21/19.
 */

public class TeleLogger {

    public static final TeleLogger shared = new TeleLogger();

    private LoggingViewModel loggingViewModel;

    private TeleLogger() {
        loggingViewModel = new LoggingViewModel(application);
    }

    public String getDeviceId() {
        String deviceId = appPreference.getString(PreferenceConstants.DEVICE_ID);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            appPreference.setString(PreferenceConstants.DEVICE_ID,deviceId);
        }

        return deviceId;
    }

    public String getBuildType() {
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            return  Constants.BUILD_PATIENT;
        } else {
            return  Constants.BUILD_MEDICAL;
        }
    }

    public void log(String capability, Boolean enabled) {
        Boolean storedValue = TeleLogCapability.shared.getValue(capability);
        if (storedValue != null) {
            if (storedValue != enabled) {
                TeleLogCapability.shared.assignValue(capability, enabled);
                updateToLogServer();
            }
        } else {
            TeleLogCapability.shared.assignValue(capability,enabled);
            updateToLogServer();
        }
    }

    public void log(String externalApi, HashMap<String,String> detail) {
        if (TextUtils.isEmpty(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN))) {
            return;
        }

        HashMap<String,Object> payload = new HashMap<>();
        payload.put("type",externalApi);
        payload.put("detail",detail);

        loggingViewModel.postExternalApi(payload);
    }

    public void initialLog() {
        Boolean cameraPermission = PermissionChecker.with(application).isGranted(PermissionConstants.PERMISSION_CAMERA);
        log(TeleLogCapability.camera, cameraPermission);

        Boolean photoPermission = PermissionChecker.with(application).isGranted(PermissionConstants.PERMISSION_GALLERY);
        log(TeleLogCapability.photo, photoPermission);

        Boolean micPermission = PermissionChecker.with(application).isGranted(PermissionConstants.PERMISSION_MICROPHONE);
        log(TeleLogCapability.mic, micPermission);

        Boolean locPermission = PermissionChecker.with(application).isGranted(PermissionConstants.PERMISSION_MICROPHONE);
        log(TeleLogCapability.location, locPermission);
    }

    private void updateToLogServer() {
        if (TextUtils.isEmpty(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN))) {
            return;
        }

        HashMap<String,Object> payload = prepareCapabilityJson();
        loggingViewModel.postCapability(payload);
    }

    private HashMap<String,Object> prepareCapabilityJson() {
        HashMap<String,Object> detail = new HashMap<>();
        detail.put("user_type",getBuildType());
        detail.put("device_id",getDeviceId());

        HashMap<String,Boolean> capability = new HashMap<>();

        capability.put(TeleLogCapability.camera,TeleLogCapability.shared.getValue(TeleLogCapability.camera));
        capability.put(TeleLogCapability.mic,TeleLogCapability.shared.getValue(TeleLogCapability.mic));
        capability.put(TeleLogCapability.location,TeleLogCapability.shared.getValue(TeleLogCapability.location));
        capability.put(TeleLogCapability.photo,TeleLogCapability.shared.getValue(TeleLogCapability.photo));

        detail.put("capability",capability);

        return detail;
    }


}
