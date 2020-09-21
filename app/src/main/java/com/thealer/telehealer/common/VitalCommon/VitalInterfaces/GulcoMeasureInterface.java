package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface GulcoMeasureInterface {
    public void updateGulcoMessage(String message);
    public void updateGulcoValue(int value);
    public void didGulcoStartMeasure();
    public void didFinishGulcoMesureWithFailure(String error);
    public void didStripInserted();
    public void didStripEjected();
    public void didBloodDropped();
}
