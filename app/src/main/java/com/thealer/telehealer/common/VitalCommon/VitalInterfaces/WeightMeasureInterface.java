package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface WeightMeasureInterface {
    public void  updateWeightMessage(String message);
    public void  updateWeightValue(Float value);
    public void  didStartWeightMeasure();
    public void  didFinishWeightMeasure(Float weight,String id);
    public void  didFinishWeightMesureWithFailure(String error);
}
