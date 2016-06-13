package com.netflix.playback.features.impl;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.RequestHistory;
import com.netflix.playback.features.model.Timer;

/**
 * An implementation of {@link DiagnosticService}.
 */
public class DiagnosticServiceImpl extends DiagnosticService {

    private volatile RequestHistory requestHistory;

    public DiagnosticServiceImpl(Timer timer, int numCompletedPeriods) {
        super(timer, numCompletedPeriods);
        this.requestHistory = new RequestHistory();
        this.timer.addCallback(new DiagnosticServiceCallback(this));
        this.timer.start();
    }

    @Override
    public void log(String key) {
        this.requestHistory.log(key);
    }

    @Override
    public int rate(String key) {
        return this.requestHistory.rate(key);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.timer.stop();
    }

    public int incrementNumCompletedPeriods() {
        this.requestHistory = new RequestHistory(
                this.requestHistory);
        return ++this.numCompletedPeriods;
    }

    protected int getNumCompletedPeriods() {
        return this.numCompletedPeriods;
    }
}
