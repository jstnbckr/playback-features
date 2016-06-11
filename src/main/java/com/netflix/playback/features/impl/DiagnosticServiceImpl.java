package com.netflix.playback.features.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.PlaybackRequest;
import com.netflix.playback.features.model.Timer;

/**
 * An implementation of {@link DiagnosticService}.
 */
public class DiagnosticServiceImpl extends DiagnosticService {

    private static Logger logger = LoggerFactory.getLogger(DiagnosticServiceImpl.class);

    private Map<String, AtomicInteger> requestsInLastPeriod;

    private Map<String, AtomicInteger> requestsInSecondLastPeriod;

    private Map<String, AtomicInteger> requestsInThirdFromLastPeriod;

    public DiagnosticServiceImpl(Timer timer, int numCompletedPeriods) {
        super(timer, numCompletedPeriods);
        this.timer.addCallback(new DiagnosticServiceCallback(this));
        this.requestsInLastPeriod = new ConcurrentHashMap<>();
        this.requestsInSecondLastPeriod = new HashMap<>();
        this.requestsInThirdFromLastPeriod = new HashMap<>();
        this.timer.start();
    }

    @Override
    public void log(PlaybackRequest request) {
        logger.info("viewableId {} {}", request.getViewableId(),
                this.requestsInLastPeriod
                        .computeIfAbsent(request.getViewableId(), k -> new AtomicInteger(0))
                        .incrementAndGet());
        logger.info("Country {} {}", request.getCountry(),
                this.requestsInLastPeriod
                        .computeIfAbsent(request.getCountry(), k -> new AtomicInteger(0))
                        .incrementAndGet());
    }

    @Override
    public synchronized int rate(String key) {
        int secondLast = this.requestsInSecondLastPeriod
                .computeIfAbsent(key, k -> new AtomicInteger(0)).get();
        int thirdFromLast = this.requestsInThirdFromLastPeriod
                .computeIfAbsent(key, k -> new AtomicInteger(0))
                .get();
        logger.info("{} secondLast {} thirdFromLast {}", key, secondLast, thirdFromLast);
        return secondLast - thirdFromLast;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.timer.stop();
    }

    public int incrementNumCompletedPeriods() {
        synchronized (this) {
            this.requestsInThirdFromLastPeriod = requestsInSecondLastPeriod;
            this.requestsInSecondLastPeriod = requestsInLastPeriod;
        }
        this.requestsInLastPeriod = new ConcurrentHashMap<>();
        return ++this.numCompletedPeriods;
    }

    protected int getNumCompletedPeriods() {
        return this.numCompletedPeriods;
    }
}
