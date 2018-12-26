package com.thealer.telehealer.common.Util;

/**
 * Created by rsekar on 1/4/19.
 */

public class TimerRunnable implements Runnable {

    private Boolean isStopped = false;
    private TimerInterface timerInterface;

    public TimerRunnable(TimerInterface timerInterface) {
        this.timerInterface = timerInterface;
    }

    @Override
    public void run() {
        if (!isStopped && timerInterface != null) {
            timerInterface.run();
        }
    }

    public void setStopped(Boolean stopped) {
        isStopped = stopped;
    }
}
