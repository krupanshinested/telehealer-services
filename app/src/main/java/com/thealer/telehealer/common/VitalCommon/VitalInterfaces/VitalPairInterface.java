package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 11/27/18.
 */

public interface VitalPairInterface {
    void didScanFinish();
    void didScanFailed(String error);
    void didDiscoverDevice(String type,String serailNumber);
    void didConnected(String type,String serailNumber);
    void didDisConnected(String type,String serailNumber);
    void didFailConnectDevice(String type, String serailNumber,String errorMessage);

    int noState = -1;
    int connected = 0;
    int connecting = 1;
    int failed = 2;
    int discovering = 3;
    int notConnected = 4;
    int scanningFailed = 5;
    int scanned = 6;
}
