package com.netflix.playback.features.model;

import java.util.concurrent.TimeUnit;

public abstract class Timer {

    protected TimeUnit timeUnit;
    protected long periodLength;
    
    public Timer(TimeUnit timeUnit, long periodLength) {
        this.timeUnit = timeUnit;
        this.periodLength = periodLength;
    }
    
    /**
     * Start the timer.
     */
    public abstract void start();
    
    /**
     * @param callback to be executed after each period completed.
     */
    public abstract void addCallback(PeriodCompletedCallback callback);
    
    /**
     * Stop the timer.
     */
    public abstract void stop();
    
    /**
     * @return true if the timer is currently running
     */
    public abstract boolean isRunning();
}
