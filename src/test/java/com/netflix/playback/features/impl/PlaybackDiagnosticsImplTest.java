package com.netflix.playback.features.impl;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.PeriodCompletedCallback;
import com.netflix.playback.features.model.PlaybackRequest;

/**
 * Tests {@link PlaybackDiagnosticsImpl}.
 */
public class PlaybackDiagnosticsImplTest {

    private static Logger logger = LoggerFactory.getLogger(TimerImplTest.class);

    private int periodLength = 3;

    private TimerImpl timer;

    private DiagnosticServiceImpl diagnosticService;

    private PlaybackDiagnosticsImpl playbackDiagnostics;

    private ExecutorService executor = Executors.newFixedThreadPool(20);

    private CountDownLatch[] latch;

    @Before
    public void setUp() throws Exception {
        this.latch = new CountDownLatch[] { new CountDownLatch(1) };
        this.timer = new TimerImpl(TimeUnit.SECONDS, this.periodLength);
        this.timer.addCallback(new PeriodCompletedCallback() {
            @Override
            public void doAction() {
                PlaybackDiagnosticsImplTest.this.latch[0].countDown();
            }
        });
        this.diagnosticService = new DiagnosticServiceImpl(this.timer, 0);
        this.playbackDiagnostics = new PlaybackDiagnosticsImpl(this.diagnosticService);
    }

    @Test
    public void testRate() throws Exception {
        logger.info("==================================== Period 1 ====================================");
        this.sendRequest(this.playbackDiagnostics, "viewable1", "US", 5);
        this.sendRequest(this.playbackDiagnostics, "viewable2", "CN", 3);
        this.latch[0].await();
        assertEquals(this.diagnosticService.getNumCompletedPeriods(), 1);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable1"), 5 - 0);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable2"), 3 - 0);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable3"), 0 - 0);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable4"), 0 - 0);
        assertEquals(this.playbackDiagnostics.countryRate("US"), 5 - 0);
        assertEquals(this.playbackDiagnostics.countryRate("CN"), 3 - 0);

        logger.info("==================================== Period 2 ====================================");
        this.latch = new CountDownLatch[] { new CountDownLatch(1) };
        this.sendRequest(this.playbackDiagnostics, "viewable1", "US", 8);
        this.sendRequest(this.playbackDiagnostics, "viewable2", "CN", 2);
        this.sendRequest(this.playbackDiagnostics, "viewable3", "CN", 2);
        this.latch[0].await();
        assertEquals(this.diagnosticService.getNumCompletedPeriods(), 2);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable1"), 8 - 5);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable2"), 2 - 3);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable3"), 2 - 0);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable4"), 0 - 0);
        assertEquals(this.playbackDiagnostics.countryRate("US"), 8 - 5);
        assertEquals(this.playbackDiagnostics.countryRate("CN"), 2 + 2 - 3);

        logger.info("==================================== Period 3 ====================================");
        this.latch = new CountDownLatch[] { new CountDownLatch(1) };
        this.sendRequest(this.playbackDiagnostics, "viewable2", "CN", 10);
        this.latch[0].await();
        assertEquals(this.diagnosticService.getNumCompletedPeriods(), 3);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable1"), 0 - 8);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable2"), 10 - 2);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable3"), 0 - 2);
        assertEquals(this.playbackDiagnostics.viewableRate("viewable4"), 0 - 0);
        assertEquals(this.playbackDiagnostics.countryRate("US"), 0 - 8);
        assertEquals(this.playbackDiagnostics.countryRate("CN"), 10 - 2 - 2);
    }

    private void sendRequest(final PlaybackDiagnosticsImpl playbackDiagnostics, String viewableId,
            String country, int num) {
        final PlaybackRequest request = new PlaybackRequest("customerId", viewableId, country);
        for (int i = 0; i < num; i++) {
            this.executor.submit(new Runnable() {
                @Override
                public void run() {
                    playbackDiagnostics.log(request);
                }
            });
        }
    }
}
