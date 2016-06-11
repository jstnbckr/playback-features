package com.netflix.playback.features.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.netflix.playback.features.model.PeriodCompletedCallback;
import com.netflix.playback.features.model.Timer;

/**
 *  An implementation of {@link Timer}.
 */
public class TimerImpl extends Timer {

    private static Logger logger = LoggerFactory.getLogger(TimerImpl.class);

    private final List<PeriodCompletedCallback> callbacks;

    private final ScheduledExecutorService service;

    private final TimeUnit timeUnit;

    private final long periodLength;

    private ScheduledFuture<?> scheduledFuture;

    public TimerImpl(TimeUnit timeUnit, long periodLength) {
        super(timeUnit, periodLength);
        this.timeUnit = timeUnit;
        this.periodLength = periodLength;
        this.callbacks = new CopyOnWriteArrayList<>();
        this.service = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() {
        logger.info("Timer started");
        this.scheduledFuture = this.service.scheduleAtFixedRate(
                new TimerPeriodicRunnable(this.callbacks), this.periodLength, this.periodLength,
                this.timeUnit);
    }

    @Override
    public void addCallback(PeriodCompletedCallback callback) {
        this.callbacks.add(callback);
    }

    @Override
    public void stop() {
        logger.info("Timer stopped");
        this.scheduledFuture.cancel(false);
    }

    private static class TimerPeriodicRunnable implements Runnable {

        private final List<PeriodCompletedCallback> callbacks;

        public TimerPeriodicRunnable(List<PeriodCompletedCallback> callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void run() {
            for (PeriodCompletedCallback callback : this.callbacks) {
                try {
                    callback.doAction();
                } catch (Exception ex) {
                    logger.error("Error on callback {}", callback, ex);
                }
            }
        }
    }
}
