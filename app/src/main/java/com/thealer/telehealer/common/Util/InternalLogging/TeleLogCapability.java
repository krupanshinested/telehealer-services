package com.thealer.telehealer.common.Util.InternalLogging;

import android.support.annotation.Nullable;

import com.thealer.telehealer.common.PreferenceConstants;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 1/21/19.
 */

public class TeleLogCapability {
    public static final TeleLogCapability shared = new TeleLogCapability();

    public static final String camera = "camera";
    public static final String mic = "mic";
    public static final String location = "location";
    public static final String photo = "photo";
    public static final String contacts = "contacts";

    private String getKey(String type) {
        String key;
        switch (type) {
            case camera:
                key = PreferenceConstants.CAMERA_CAPABILITY;
            case mic:
                key = PreferenceConstants.MIC_CAPABILITY;
            case location:
                key = PreferenceConstants.LOCATION_CAPABILITY;
            case photo:
                key = PreferenceConstants.PHOTO_CAPABILITY;
            default:
                key = "";
        }

        return key;
    }

    @Nullable
    public Boolean getValue(String type) {
        String key = getKey(type);
        int storedValue = appPreference.getInt(key);
        if (storedValue == -1) {
            return null;
        } else {
            return storedValue != 0;
        }
    }

    public void assignValue(String type,@Nullable Boolean enabled){
        String key = getKey(type);
        if (enabled != null) {
            appPreference.setInt(key,enabled ? 1 : 0);
        } else {
            appPreference.setInt(key,-1);
        }
    }

    public void reset() {
        assignValue(camera,null);
        assignValue(mic,null);
        assignValue(photo,null);
        assignValue(location,null);
    }

}
