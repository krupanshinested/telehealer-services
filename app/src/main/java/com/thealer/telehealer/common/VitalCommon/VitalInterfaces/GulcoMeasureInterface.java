package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface GulcoMeasureInterface {
    public void updateGulcoMessage(String deviceType,String message);
    public void updateGulcoValue(String deviceType,int value);
    public void didGulcoStartMeasure(String deviceType);
    public void didFinishGulcoMesureWithFailure(String deviceType,String error);
    public void didStripInserted(String deviceType);
    public void didStripEjected(String deviceType);
    public void didBloodDropped(String deviceType);
}
