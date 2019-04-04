package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface ThermoMeasureInterface {
    void updateThermoMessage(String deviceType, String message);

    void updateThermoValue(String deviceType, Double value);

    void didThermoStartMeasure(String deviceType);

    void didThermoFinishMesureWithFailure(String deviceType, String error);
}
