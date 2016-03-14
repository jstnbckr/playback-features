package com.netflix.playback.features.model.test;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.MyDiagnosticService;
import com.netflix.playback.features.model.MyPlaybackDiagnostics;
import com.netflix.playback.features.model.MyTimer;
import com.netflix.playback.features.model.PlaybackDiagnostics;
import com.netflix.playback.features.model.PlaybackRequest;
import com.netflix.playback.features.model.Timer;
import com.netflix.playback.features.model.Util;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class TestMyPlaybackDiagnostics {
    public static final String NAME_PD1 = "PD1";
    public static final int PERIOD_5_SECOND = 5;
    public static final int ONE_PERIOD = 1;
    public static final int THREE_PERIOD = 3;
    public static final int NUM_REQUESTS_10 = 10;
    public static final int NUM_REQUESTS_5 = 5;
    public static final int NUM_REQUESTS_20 = 20;
    public static final String US = "US";
    public static final String CUSTOMER = "CUSTOMER_";

    @Test
    public void testOnePeriod() {
        Timer t = new MyTimer(TimeUnit.SECONDS, PERIOD_5_SECOND);
        DiagnosticService ds = new MyDiagnosticService(t, ONE_PERIOD);
        t.addCallback(ds);

        PlaybackDiagnostics pd1 = new MyPlaybackDiagnostics(NAME_PD1, ds);

        ds.startTimer();

        Util.printWithTime("Log " + NUM_REQUESTS_10 + " " + NAME_PD1 + " requests");
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            pd1.log(new PlaybackRequest(CUSTOMER+i, i, US));
        }

        Util.printWithTime("Validating before timer period 1");
        Assert.assertEquals(0, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            Assert.assertEquals(0, pd1.requestCount(i));
        }
        Assert.assertEquals(0, pd1.uniqueCustomerCount());
        Assert.assertEquals(0, pd1.uniqueViewableCount());
        Assert.assertEquals(0, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(0, pd1.rate());
        Assert.assertEquals(0, pd1.avgRate());

        Util.sleep(7);

        Util.printWithTime("Validating after timer period 1");
        Assert.assertEquals(NUM_REQUESTS_10, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            Assert.assertEquals(1, pd1.requestCount(i));
        }
        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueCustomerCount());
        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueViewableCount());
        Assert.assertEquals(0, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(0, pd1.rate());
        Assert.assertEquals(0, pd1.avgRate());
    }

    @Test
    public void testThreePeriod() {
        Timer t = new MyTimer(TimeUnit.SECONDS, PERIOD_5_SECOND);
        DiagnosticService ds = new MyDiagnosticService(t, THREE_PERIOD);
        t.addCallback(ds);

        PlaybackDiagnostics pd1 = new MyPlaybackDiagnostics(NAME_PD1, ds);

        ds.startTimer();

        Util.printWithTime("Log " + NUM_REQUESTS_10 + " " + NAME_PD1 + " requests");
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            pd1.log(new PlaybackRequest(CUSTOMER + i, i, US));
        }

        Util.printWithTime("Validating before timer period 1");
        Assert.assertEquals(0, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            Assert.assertEquals(0, pd1.requestCount(i));
        }
        Assert.assertEquals(0, pd1.uniqueCustomerCount());
        Assert.assertEquals(0, pd1.uniqueViewableCount());
        Assert.assertEquals(0, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(0, pd1.rate());
        Assert.assertEquals(0, pd1.avgRate());

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(5 + 1);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Validating after timer period 1");
        Assert.assertEquals(NUM_REQUESTS_10, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            Assert.assertEquals(1, pd1.requestCount(i));
        }
        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueCustomerCount());
        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueViewableCount());
        Assert.assertEquals(0, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(0, pd1.rate());
        Assert.assertEquals(0, pd1.avgRate());

        Util.printWithTime("Log " + (NUM_REQUESTS_10 + NUM_REQUESTS_5) + " " + NAME_PD1 + " requests");
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            pd1.log(new PlaybackRequest(CUSTOMER + i, i, US));
        }
        for (int i = 0; i < NUM_REQUESTS_5; i++) {
            pd1.log(new PlaybackRequest(CUSTOMER + i, i, US));
        }

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(4 + 2);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Validating after timer period 2");
        Assert.assertEquals(NUM_REQUESTS_10 + NUM_REQUESTS_5, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_10; i++) {
            if (i < 5) {
                Assert.assertEquals(2, pd1.requestCount(i));
            } else {
                Assert.assertEquals(1, pd1.requestCount(i));
            }
        }

        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueCustomerCount());
        Assert.assertEquals(NUM_REQUESTS_10, pd1.uniqueViewableCount());
        Assert.assertEquals(NUM_REQUESTS_5, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(NUM_REQUESTS_5, pd1.rate());
        Assert.assertEquals(NUM_REQUESTS_5, pd1.avgRate());

        Util.printWithTime("Log " + NUM_REQUESTS_20 + " " + NAME_PD1 + " requests");
        for (int i = 0; i < NUM_REQUESTS_20; i++) {
            pd1.log(new PlaybackRequest(CUSTOMER + i, i, US));
        }

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(3 + 3);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Validating after timer period 3");
        Assert.assertEquals(NUM_REQUESTS_20, pd1.requestCount());
        for (int i = 0; i < NUM_REQUESTS_20; i++) {
            Assert.assertEquals(1, pd1.requestCount(i));
        }

        Assert.assertEquals(NUM_REQUESTS_20, pd1.uniqueCustomerCount());
        Assert.assertEquals(NUM_REQUESTS_20, pd1.uniqueViewableCount());
        Assert.assertEquals(NUM_REQUESTS_5, pd1.avgRateRequestsPerCountry(US));
        Assert.assertEquals(NUM_REQUESTS_5, pd1.rate());
        Assert.assertEquals(NUM_REQUESTS_5, pd1.avgRate());
    }

}
