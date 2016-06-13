package com.netflix.playback.features.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHistory {

    private static Logger logger = LoggerFactory.getLogger(RequestHistory.class);

    private static final int REQUEST_HISTORY_SIZE = 3;

    private final Request[] requests;

    private int index;

    public RequestHistory() {
        this.index = 0;
        this.requests = new Request[REQUEST_HISTORY_SIZE];
        for (int i = 0; i < REQUEST_HISTORY_SIZE; i++) {
            this.requests[i] = new Request();
        }
    }

    public RequestHistory(RequestHistory oldRequestHistory) {
        this.requests = new Request[REQUEST_HISTORY_SIZE];
        for (int i = 0; i < REQUEST_HISTORY_SIZE; i++) {
            this.requests[i] = oldRequestHistory.getRequests()[i];
        }
        this.index = (oldRequestHistory.getIndex() + 1) % REQUEST_HISTORY_SIZE;
        this.newRequestsInLastPeriod();
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

    private int getIndex() {
        return this.index;
    }

    private Request[] getRequests() {
        return this.requests;
    }

    private void newRequestsInLastPeriod() {
        this.requests[(this.index + 2) % REQUEST_HISTORY_SIZE] = new Request();
    }

    private Map<String, Integer> getRequestsInLastPeriod() {
        return this.requests[(this.index + 2) % REQUEST_HISTORY_SIZE].getStat();
    }

    private Map<String, Integer> getRequestsInSecondLastPeriod() {
        return this.requests[(this.index + 1) % REQUEST_HISTORY_SIZE].getStat();
    }

    private Map<String, Integer> getRequestsInThirdFromLastPeriod() {
        return this.requests[this.index % REQUEST_HISTORY_SIZE].getStat();
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
