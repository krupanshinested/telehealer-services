package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

import com.thealer.telehealer.common.VitalCommon.BatteryResult;

/**
 * Created by rsekar on 12/4/18.
 */

public interface VitalBatteryFetcher {
    public void updateBatteryDetails(BatteryResult batteryResult);
    public void notConnected(String deviceType,String deviceMac);
}
