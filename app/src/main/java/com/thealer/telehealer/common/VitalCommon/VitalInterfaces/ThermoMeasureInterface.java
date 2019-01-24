package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface ThermoMeasureInterface {
    public void  updateThermoMessage(String deviceType,String message);
    public void  updateThermoValue(String deviceType,Double value);
    public void didThermoStartMeasure(String deviceType);
    public void didThermoFinishMesureWithFailure(String deviceType,String error);
}
