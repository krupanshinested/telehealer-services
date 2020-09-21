package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

import com.thealer.telehealer.common.VitalCommon.VitalsManager;

/**
 * Created by rsekar on 11/29/18.
 */

public interface VitalManagerInstance {
    public VitalsManager getInstance();
    public void updateBatteryView(int visibility,int battery);
}
