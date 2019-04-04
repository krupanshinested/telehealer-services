package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

import java.util.ArrayList;

/**
 * Created by rsekar on 12/3/18.
 */

public interface BPMeasureInterface {
    void updateBPMessage(String deviceType, String message);

    void didStartBPMesure(String deviceType);

    void didUpdateBPMesure(String deviceType, ArrayList<Double> value);

    void didUpdateBPM(String deviceType, ArrayList<Double> value);

    void didFinishBPMesure(String deviceType, Double systolicValue, Double diastolicValue, Double heartRate);

    void didFailBPMesure(String deviceType, String error);

    void didFinishBpMeasure(Object object);
}
