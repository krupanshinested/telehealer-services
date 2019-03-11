package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

import java.util.ArrayList;

/**
 * Created by rsekar on 12/3/18.
 */

public interface BPMeasureInterface {
    public void updateBPMessage(String deviceType,String message);
    public void didStartBPMesure(String deviceType);
    public void didUpdateBPMesure(String deviceType,ArrayList<Double> value);
    public void didUpdateBPM(String deviceType,ArrayList<Double> value);
    public void didFinishBPMesure(String deviceType,Double systolicValue,Double diastolicValue,Double heartRate);
    public void didFailBPMesure(String deviceType,String error);

    public void didFinishBpMeasure(Object object);
}
