package com.netflix.playback.features.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHistory {

    private static Logger logger = LoggerFactory.getLogger(RequestHistory.class);

    private static final int REQUEST_HISTORY_SIZE = 3;

    private final Request[] requests;

    public RequestHistory() {
        this.requests = new Request[REQUEST_HISTORY_SIZE];
        for (int i = 0; i < REQUEST_HISTORY_SIZE; i++) {
            this.requests[i] = new Request();
        }
    }

    public RequestHistory(RequestHistory oldRequestHistory) {
        this.requests = new Request[REQUEST_HISTORY_SIZE];
        for (int i = 0; i < REQUEST_HISTORY_SIZE - 1; i++) {
            this.requests[i] = oldRequestHistory.getRequests()[i + 1];
        }
        this.requests[REQUEST_HISTORY_SIZE - 1] = new Request();
    }

    public int rate(String key) {
        int secondLast = this.getRequestsInSecondLastPeriod()
                .computeIfAbsent(key, k -> 0);
        int thirdFromLast = this.getRequestsInThirdFromLastPeriod()
                .computeIfAbsent(key, k -> 0);
        logger.info("{} secondLast {} thirdFromLast {}", key, secondLast, thirdFromLast);
        return secondLast - thirdFromLast;
    }

    public synchronized void log(String key) {
        int count = this.getRequestsInLastPeriod().computeIfAbsent(key, k -> 0) + 1;
        this.getRequestsInLastPeriod().put(key, count);
        logger.info("key {} count {}", key, count);
    }

    private Request[] getRequests() {
        return this.requests;
    }

    private Map<String, Integer> getRequestsInLastPeriod() {
        return this.requests[REQUEST_HISTORY_SIZE - 1].getStat();
    }

    private Map<String, Integer> getRequestsInSecondLastPeriod() {
        return this.requests[REQUEST_HISTORY_SIZE - 2].getStat();
    }

    private Map<String, Integer> getRequestsInThirdFromLastPeriod() {
        return this.requests[REQUEST_HISTORY_SIZE - 3].getStat();
    }

    private static class Request {
        private final Map<String, Integer> statistics;

        private Request() {
            this.statistics = new HashMap<>();
        }

        public Map<String, Integer> getStat() {
            return statistics;
        }
    }
}
