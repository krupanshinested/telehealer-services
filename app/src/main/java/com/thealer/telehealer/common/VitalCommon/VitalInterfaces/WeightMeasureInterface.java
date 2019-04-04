package com.thealer.telehealer.common.VitalCommon.VitalInterfaces;

/**
 * Created by rsekar on 12/3/18.
 */

public interface WeightMeasureInterface {
    void updateWeightMessage(String deviceType, String message);

    void updateWeightValue(String deviceType, Float value);

    void didStartWeightMeasure(String deviceType);

    void didFinishWeightMeasure(String deviceType, Float weight, String id);

    void didFinishWeightMesureWithFailure(String deviceType, String error);
}
