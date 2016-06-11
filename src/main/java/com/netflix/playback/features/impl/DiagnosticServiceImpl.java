package com.netflix.playback.features.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.RequestsInLastThreePeriods;
import com.netflix.playback.features.model.Timer;

/**
 * An implementation of {@link DiagnosticService}.
 */
public class DiagnosticServiceImpl extends DiagnosticService {

    private volatile RequestsInLastThreePeriods requestsInLastThreePeriods;

    public DiagnosticServiceImpl(Timer timer, int numCompletedPeriods) {
        super(timer, numCompletedPeriods);
        this.requestsInLastThreePeriods = new RequestsInLastThreePeriods();
        this.timer.addCallback(new DiagnosticServiceCallback(this));
        this.timer.start();
    }

    @Override
    public void log(String key) {
        this.requestsInLastThreePeriods.log(key);
    }

    @Override
    public int rate(String key) {
        return this.requestsInLastThreePeriods.rate(key);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.timer.stop();
    }

    public int incrementNumCompletedPeriods() {
        this.requestsInLastThreePeriods = new RequestsInLastThreePeriods(
                this.requestsInLastThreePeriods);
        return ++this.numCompletedPeriods;
    }

    protected int getNumCompletedPeriods() {
        return this.numCompletedPeriods;
    }
}
