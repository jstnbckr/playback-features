package com.netflix.playback.features.model.test;

import com.netflix.playback.features.model.MyDiagnosticService;
import com.netflix.playback.features.model.MyTimer;
import com.netflix.playback.features.model.Timer;
import com.netflix.playback.features.model.Util;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class TestMyDiagnosticService {
    public static final String KEY_1 = "key1";
    public static final String KEY_2 = "key2";
    public static final int NUM_OBJ_10 = 10;
    public static final int NUM_OBJ_15 = 15;
    public static final int PERIOD_5_SECOND = 5;
    public static final int ONE_PERIOD = 1;
    public static final int TWO_PERIOD = 2;

    @Test
    public void testOnePeriodWithOneKey() {

        Timer timer = new MyTimer(TimeUnit.SECONDS, PERIOD_5_SECOND);
        MyDiagnosticService ds = new MyDiagnosticService(timer, ONE_PERIOD);
        timer.addCallback(ds);

        ds.startTimer();

        Util.printWithTime("Log " + NUM_OBJ_10 + " " + KEY_1 + " objects");
        for (int i = 0; i < NUM_OBJ_10; i++) {
            ds.log(KEY_1, new Object());
        }

        // Check before timer is up
        Assert.assertEquals(0, ds.count(KEY_1));
        Assert.assertEquals(0, ds.rate(KEY_1));
        Assert.assertEquals(0, ds.avg(KEY_1));

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(5 + 1);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Checking rate, avg and count");
        Util.printWithTime(KEY_1 + " Expected rate " + NUM_OBJ_10 + ", actual count " + ds.count(KEY_1));
        Util.printWithTime(KEY_1 + " Expected rate 0, actual rate " + ds.rate(KEY_1));
        Util.printWithTime(KEY_1 + " Expected avg 0, actual avg " + ds.avg(KEY_1));
        Assert.assertEquals(NUM_OBJ_10, ds.count(KEY_1));
        Assert.assertEquals(0, ds.rate(KEY_1));
        Assert.assertEquals(0, ds.avg(KEY_1));
        Util.printWithTime("Checking passed");
    }

    @Test
    public void testTwoPeriodWithTwoKeys() {
        Timer timer = new MyTimer(TimeUnit.SECONDS, PERIOD_5_SECOND);
        MyDiagnosticService ds = new MyDiagnosticService(timer, TWO_PERIOD);
        timer.addCallback(ds);

        ds.startTimer();

        Util.printWithTime("Log " + NUM_OBJ_10 + " " + KEY_1 + " objects");
        for (int i = 0; i < NUM_OBJ_10; i++) {
            ds.log(KEY_1, new Object());
        }
        Util.printWithTime("Log " + NUM_OBJ_10 + " " + KEY_2 + " objects");
        for (int i = 0; i < NUM_OBJ_10; i++) {
            ds.log(KEY_2, new Object());
        }

        // Check before timer is up
        Util.printWithTime(KEY_1 + " Expected count 0, actual count " + ds.count(KEY_1));
        Util.printWithTime(KEY_1 + " Expected rate 0, actual rate " + ds.rate(KEY_1));
        Util.printWithTime(KEY_1 + " Expected avg 0, actual avg " + ds.avg(KEY_1));
        Assert.assertEquals(0, ds.count(KEY_1));
        Assert.assertEquals(0, ds.rate(KEY_1));
        Assert.assertEquals(0, ds.avg(KEY_1));

        Util.printWithTime(KEY_2 + " Expected count 0, actual count " + ds.count(KEY_2));
        Util.printWithTime(KEY_2 + " Expected rate 0, actual rate " + ds.rate(KEY_2));
        Util.printWithTime(KEY_2 + " Expected avg 0, actual avg " + ds.avg(KEY_2));
        Assert.assertEquals(0, ds.count(KEY_2));
        Assert.assertEquals(0, ds.rate(KEY_2));
        Assert.assertEquals(0, ds.avg(KEY_2));

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(5 + 1);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Checking rate, avg and count");
        Util.printWithTime(KEY_1 + " Expected count " + NUM_OBJ_10 + ", actual count " + ds.count(KEY_1));
        Util.printWithTime(KEY_1 + " Expected rate 0, actual rate " + ds.rate(KEY_1));
        Util.printWithTime(KEY_1 + " Expected avg 0, actual avg " + ds.avg(KEY_1));
        Assert.assertEquals(NUM_OBJ_10, ds.count(KEY_1));
        Assert.assertEquals(0, ds.rate(KEY_1));
        Assert.assertEquals(0, ds.avg(KEY_1));

        Util.printWithTime(KEY_2 + " Expected count " + NUM_OBJ_10 + ", actual count " + ds.count(KEY_2));
        Util.printWithTime(KEY_2 + " Expected rate 0, actual rate " + ds.rate(KEY_2));
        Util.printWithTime(KEY_2 + " Expected avg 0, actual avg " + ds.avg(KEY_2));
        Assert.assertEquals(NUM_OBJ_10, ds.count(KEY_2));
        Assert.assertEquals(0, ds.rate(KEY_2));
        Assert.assertEquals(0, ds.avg(KEY_2));
        Util.printWithTime("Checking passed");

        Util.printWithTime("Log " + NUM_OBJ_15 + " " + KEY_1 + " objects");
        for (int i = 0; i < NUM_OBJ_15; i++) {
            ds.log(KEY_1, new Object());
        }

        Util.printWithTime("Sleep 6s .....");
        Util.sleep(4 + 2);
        Util.printWithTime("Wake up after sleeping 6s .....");

        Util.printWithTime("Checking rate, avg and count");
        int expectedRateKey1 = NUM_OBJ_15 - NUM_OBJ_10;
        int expectedAvgRateKey1 = expectedRateKey1 / (TWO_PERIOD - 1);

        Util.printWithTime(KEY_1 + " Expected count " + NUM_OBJ_15 + ", actual count " + ds.count(KEY_1));
        Util.printWithTime(KEY_1 + " Expected rate " + expectedRateKey1 + ", actual rate " + ds.rate(KEY_1));
        Util.printWithTime(KEY_1 + " Expected avg " + expectedAvgRateKey1 + ", actual avg " + ds.avg(KEY_1));
        Assert.assertEquals(NUM_OBJ_15, ds.count(KEY_1));
        Assert.assertEquals(expectedRateKey1, ds.rate(KEY_1));
        Assert.assertEquals(expectedAvgRateKey1, ds.avg(KEY_1));

        int expectedRateKey2 = 0 - NUM_OBJ_10;
        int expectedAvgRateKey2 = expectedRateKey2 / (TWO_PERIOD - 1);
        Util.printWithTime(KEY_2 + " Expected count " + NUM_OBJ_10 + ", actual count " + ds.count(KEY_2));
        Util.printWithTime(KEY_2 + " Expected rate " + expectedRateKey2 + ", actual rate " + ds.rate(KEY_2));
        Util.printWithTime(KEY_2 + " Expected avg " + expectedAvgRateKey2 + ", actual avg " + ds.avg(KEY_2));
        Assert.assertEquals(0, ds.count(KEY_2));
        Assert.assertEquals(expectedRateKey2, ds.rate(KEY_2));
        Assert.assertEquals(expectedAvgRateKey2, ds.avg(KEY_2));

        Util.printWithTime("Checking passed");
    }
}
