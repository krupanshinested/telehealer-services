package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/13/18.
 */

public interface VitalFirmwareFetcher {
    void didFetchedLocalInfo(String hardwareVersion, String firmwareVersion);

    void didFetchedServerInfo(String hardwareVersion, String firmwareVersion);
}
