package com.netflix.playback.features.model;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DiagnosticService {

    protected Timer timer;
    protected int numCompletedPeriods;
    private AtomicBoolean isTimerRunning = new AtomicBoolean(false);
    
    public DiagnosticService(Timer timer, int numCompletedPeriods) {
        this.timer = timer;
        this.numCompletedPeriods = numCompletedPeriods;
    }
    
    /**
     * @param key
     * @return difference between last two completed periods
     */
    public abstract int rate(int key);
    
    /**
     * @param key
     * @return average rate across all completed periods 
     */
    public abstract int avg(int key);
    
    /**
     * @return rate of playback requests for the given viewableId for the last two completed time periods
     */
    public abstract int requestRate(int key);

    /**
     * write stat for the current time period
     * @param key
     */
    public abstract void logStat(int key) ;
    
    /**
     * @return rate of playback requests for the last two completed time periods
     */
    public abstract int requestRate();

    
    public void start() {
    	if (isTimerRunning.compareAndSet(false, true)) {
    		timer.start();
    	}
    }
    
    public void flush() {
    	isTimerRunning.set(false);
    }
    
}
