package com.netflix.playback.features.model;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyTimer extends Timer {
    PeriodCompletedCallback callback;
    java.util.Timer timer = new java.util.Timer();

    public MyTimer(TimeUnit timeUnit, long periodLength) {
        super(timeUnit, periodLength);
    }
    
    /**
     * Start the timer.
     */
    @Override
    public void start() {
        Util.printWithTime("Start timer (" + timeUnit.toSeconds(periodLength) + "s)");
        timer.schedule(new MyTimerTask(callback), timeUnit.toMillis(periodLength), timeUnit.toMillis(periodLength));
    }

    /**
     * @param callback to be executed after each period completed.
     */
    @Override
    public void addCallback(PeriodCompletedCallback callback) {
        this.callback = callback;
    }

    /**
     * Stop the timer.
     */
    @Override
    public void stop() {
        Util.printWithTime("Stop the timer");
        timer.cancel();
        callback.doAction();
    }

    class MyTimerTask extends TimerTask {
        PeriodCompletedCallback callback;
        public MyTimerTask(PeriodCompletedCallback callback) {
            this.callback = callback;
        }

        public void run() {
            callback.doAction();
        }
    }
}
