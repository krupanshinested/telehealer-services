package com.thealer.telehealer.views.call.Interfaces;

import com.thealer.telehealer.apilayer.models.vitals.vitalCreation.VitalDevice;

/**
 * Created by rsekar on 1/24/19.
 */

public interface CallVitalEvents {
    void didReceiveData(String data);

    void assignVitalListener();

    void assignVitalDevice(VitalDevice vitalDevice);

    String getVitalDeviceType();
}
