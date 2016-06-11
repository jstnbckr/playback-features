package com.netflix.playback.features.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestsInLastThreePeriods {

    private static Logger logger = LoggerFactory.getLogger(RequestsInLastThreePeriods.class);

    private Map<String, Integer> requestsInLastPeriod;

    private Map<String, Integer> requestsInSecondLastPeriod;

    private Map<String, Integer> requestsInThirdFromLastPeriod;

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
                .computeIfAbsent(key, k -> 0);
        int thirdFromLast = this.requestsInThirdFromLastPeriod
                .computeIfAbsent(key, k -> 0);
        logger.info("{} secondLast {} thirdFromLast {}", key, secondLast, thirdFromLast);
        return secondLast - thirdFromLast;
    }

    public synchronized void log(String key) {
        int count = this.requestsInLastPeriod.computeIfAbsent(key, k -> 0) + 1;
        this.requestsInLastPeriod.put(key, count);
        logger.info("key {} count {}", key, count);
    }
}
