package com.netflix.playback.features.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.PeriodCompletedCallback;

/**
 * Tests {@link TimerImpl}.
 */
public class TimerImplTest {

    private static Logger logger = LoggerFactory.getLogger(TimerImplTest.class);

    private TimerImpl timer;

    private int count;

    @Before
    public void setUp() throws Exception {
        this.timer = new TimerImpl(TimeUnit.MILLISECONDS, 30);
        this.count = 0;
        this.timer.addCallback(new PeriodCompletedCallback() {
            @Override
            public void doAction() {
                logger.info("count {}", ++TimerImplTest.this.count);
            }
        });
    }

    @Test
    public void testStartAndStop() throws Exception {
        this.timer.start();
        Thread.sleep(70);
        this.timer.stop();
        assertEquals(this.count, 2);
    }

}
