package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface ThermoMeasureInterface {
    public void  updateThermoMessage(String message);
    public void  updateThermoValue(Double value);
    public void didThermoStartMeasure();
    public void didThermoFinishMesureWithFailure(String error);
}
