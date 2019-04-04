package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/5/18.
 */

public interface PulseMeasureInterface {
    void updatePulseMessage(String deviceType, String message);

    void updatePulseValue(String deviceType, int spo2, int bpm, int wave, float pi);

    void didFinishMeasure(String deviceType, int spo2, int bpm, int wave, float pi);

    void didPulseStartMeasure(String deviceType);

    void didPulseFinishMesureWithFailure(String deviceType, String error);
}
