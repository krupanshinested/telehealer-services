package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface PulseMeasureInterface {
    public void updatePulseMessage(String message);
    public void updatePulseValue(int spo2 ,int bpm,int wave,float pi);
    public void didFinishMeasure(int spo2,int bpm,int wave,float pi);
    public void didPulseStartMeasure();
    public void didPulseFinishMesureWithFailure(String error);
}
