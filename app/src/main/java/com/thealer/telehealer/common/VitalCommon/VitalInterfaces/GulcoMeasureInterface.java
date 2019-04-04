package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface GulcoMeasureInterface {
    void updateGulcoMessage(String deviceType, String message);

    void updateGulcoValue(String deviceType, int value);

    void didGulcoStartMeasure(String deviceType);

    void didFinishGulcoMesureWithFailure(String deviceType, String error);

    void didStripInserted(String deviceType);

    void didStripEjected(String deviceType);

    void didBloodDropped(String deviceType);
}
