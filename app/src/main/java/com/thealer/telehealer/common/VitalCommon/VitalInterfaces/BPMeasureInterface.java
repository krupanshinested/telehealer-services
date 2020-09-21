package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

import java.util.ArrayList;

/**
 * Created by rsekar on 12/3/18.
 */

public interface BPMeasureInterface {
    public void updateBPMessage(String message);
    public void didStartBPMesure();
    public void didUpdateBPMesure(ArrayList<Double> value);
    public void didUpdateBPM(ArrayList<Double> value);
    public void didFinishBPMesure(Double systolicValue,Double diastolicValue,Double heartRate);
    public void didFailBPMesure(String error);
}
