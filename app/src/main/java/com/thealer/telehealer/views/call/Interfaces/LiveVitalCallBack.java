package com.thealer.telehealer.views.call.Interfaces;

/**
 * Created by rsekar on 1/24/19.
 */

public interface LiveVitalCallBack {
    void closeVitalController();
    void didInitiateMeasure(String type);
    void didChangedNumberOfScreens(int count);
}
