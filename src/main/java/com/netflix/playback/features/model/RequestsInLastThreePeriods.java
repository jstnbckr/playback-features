package com.netflix.playback.features.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestsInLastThreePeriods {

    private static Logger logger = LoggerFactory.getLogger(RequestsInLastThreePeriods.class);

    private Map<String, AtomicInteger> requestsInLastPeriod;

    private Map<String, AtomicInteger> requestsInSecondLastPeriod;

    private Map<String, AtomicInteger> requestsInThirdFromLastPeriod;

    public RequestsInLastThreePeriods() {
        this.requestsInLastPeriod = new HashMap<>();
        this.requestsInSecondLastPeriod = new HashMap<>();
        this.requestsInThirdFromLastPeriod = new HashMap<>();
    }

    public RequestsInLastThreePeriods(RequestsInLastThreePeriods oldRequests) {
        this.requestsInThirdFromLastPeriod = oldRequests.requestsInSecondLastPeriod;
        this.requestsInSecondLastPeriod = oldRequests.requestsInLastPeriod;
        this.requestsInLastPeriod = new HashMap<>();
    }

    public int rate(String key) {
        int secondLast = this.requestsInSecondLastPeriod
                .computeIfAbsent(key, k -> new AtomicInteger(0)).get();
        int thirdFromLast = this.requestsInThirdFromLastPeriod
                .computeIfAbsent(key, k -> new AtomicInteger(0))
                .get();
        logger.info("{} secondLast {} thirdFromLast {}", key, secondLast, thirdFromLast);
        return secondLast - thirdFromLast;
    }

    public synchronized void log(String key) {
        logger.info("key {} {}", key,
                this.requestsInLastPeriod.computeIfAbsent(key, k -> new AtomicInteger(0))
                        .incrementAndGet());
    }
}
