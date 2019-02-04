package com.thealer.telehealer.views.call.Interfaces;

/**
 * Created by rsekar on 1/24/19.
 */

public interface CallVitalPagerInterFace {
    void didInitiateMeasure(String type);
    void closeVitalController();
    void updateState(int state);
}
