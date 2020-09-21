package com.thealer.telehealer.views.home.vitals.measure.util;

/**
 * Created by rsekar on 12/4/18.
 */

public class MeasureState {
    public static final int notStarted = 0;
    public static final int started = 1;
    public static final int ended = 2;
    public static final int startedToReceieveValues = 3;
    public static final int failed = 4;

    //Gulco state
    public static final int capturedCodeString = 5;
    public static final int stripInserted = 6;
    public static final int bloodDropped = 7;
    public static final int calculating = 8;
}
