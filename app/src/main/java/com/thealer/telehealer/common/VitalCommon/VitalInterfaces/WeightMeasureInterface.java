package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface WeightMeasureInterface {
    public void  updateWeightMessage(String deviceType,String message);
    public void  updateWeightValue(String deviceType,Float value);
    public void  didStartWeightMeasure(String deviceType);
    public void  didFinishWeightMeasure(String deviceType,Float weight,String id);
    public void  didFinishWeightMesureWithFailure(String deviceType,String error);
}
