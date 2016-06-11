package com.netflix.playback.features.model;

public abstract class DiagnosticService {

    protected Timer timer;
    protected int numCompletedPeriods;

    public DiagnosticService(Timer timer, int numCompletedPeriods) {
        this.timer = timer;
        this.numCompletedPeriods = numCompletedPeriods;
    }

    /**
     * @param key
     * @return difference between last two completed periods
     */
    public abstract int rate(String key);

    public abstract void log(PlaybackRequest request);
}
