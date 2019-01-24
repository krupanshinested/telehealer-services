package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface PulseMeasureInterface {
    public void updatePulseMessage(String deviceType,String message);
    public void updatePulseValue(String deviceType,int spo2 ,int bpm,int wave,float pi);
    public void didFinishMeasure(String deviceType,int spo2,int bpm,int wave,float pi);
    public void didPulseStartMeasure(String deviceType);
    public void didPulseFinishMesureWithFailure(String deviceType,String error);
}
