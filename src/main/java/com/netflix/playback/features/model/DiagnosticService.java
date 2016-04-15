package com.netflix.playback.features.model;

public abstract class DiagnosticService {

    protected Timer timer;
    protected int numCompletedPeriods;
    
    public DiagnosticService(Timer timer, int numCompletedPeriods) {
        this.timer = timer;
    }
    
    /**
     * @param key
     * @return difference between last two completed periods
     */
    public abstract int rate(String key);
    
    /**
     * @param key
     * @return average rate across all completed periods 
     */
    public abstract int avg(String key);
}
