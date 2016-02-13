package com.netflix.playback.features.model;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleTimerTest {

  private class FakeCallback implements PeriodCompletedCallback {
    private int callCount;

    @Override
    public void doAction() {
      callCount++;
    }
  }
  
  private SimpleTimer timer;
  
  @Before
  public void setUp() throws Exception {
    this.timer = new SimpleTimer(TimeUnit.MILLISECONDS, 10);
  }
  
  @After
  public void tearDown() throws Exception {
    if (this.timer.isRunning()) {
      this.timer.stop();
    }
  }

  @Test(expected=IllegalStateException.class)
  public void testStartWhenAlreadyStarted() {
    this.timer.start();
    this.timer.start();
  }

  @Test(expected=IllegalStateException.class)
  public void testStopWhenAlreadyStopped() {
    this.timer.stop();
  }
  
  @Test
  public void testCallback() throws InterruptedException {
    FakeCallback callbackA = new FakeCallback();
    FakeCallback callbackB = new FakeCallback();
    this.timer.addCallback(callbackA);
    this.timer.addCallback(callbackB);
    
    this.timer.start();
    Thread.sleep(15L); 
    assertEquals(1, callbackA.callCount);
    assertEquals(1, callbackB.callCount);
    Thread.sleep(10L); 
    assertEquals(2, callbackA.callCount);
    assertEquals(2, callbackB.callCount);
    this.timer.stop();
    Thread.sleep(10L); 
    assertEquals(2, callbackA.callCount);
    assertEquals(2, callbackB.callCount);
  }
}
