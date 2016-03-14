package com.netflix.playback.features.model;

import java.util.List;
import java.util.Map;

/**
 * A generic diagnostic service that can diagnose different types of requests that
 * are identified by the key. This class implements callback interface for timer
 * to call back.
 */
public abstract class DiagnosticService implements PeriodCompletedCallback {

    protected Timer timer;
    protected int numCompletedPeriods;
    
    public DiagnosticService(Timer timer, int numCompletedPeriods) {
        this.timer = timer;
        this.numCompletedPeriods = numCompletedPeriods;
    }
    
    /**
     * @param key
     * @return total count for the last completed period 
     */
    public abstract int count(String key);
    
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

    /**
     * Log a request by a key
     * @param key the request key
     * @param request the request
     */
    public abstract void log(String key, Object request);

    /**
     * @param key
     * @return list of objects in last completed periods for the given key
     */
    public abstract List<Object> get(String key);

    /**
     * @param key
     * @return all requests across all completed periods for the given key
     */
    public abstract Map<Integer, List<Object>> getAll(String key);

    /**
     * Start the timer.
     */
    public void startTimer() {
        timer.start();
    }

    /**
     * Stop the timer.
     */
    public void stopTimer() {
        timer.stop();
    }

}
