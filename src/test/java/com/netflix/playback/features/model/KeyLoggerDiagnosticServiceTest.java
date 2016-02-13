package com.netflix.playback.features.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KeyLoggerDiagnosticServiceTest {
  
  private class FakeTimer extends Timer {
    List<PeriodCompletedCallback> callbacks = new ArrayList<PeriodCompletedCallback>();
    
    public FakeTimer() {
      super(TimeUnit.DAYS, 1); // dummy values to keep super happy
    }

    @Override
    public void start() {
      // does nothing
    }

    @Override
    public void addCallback(PeriodCompletedCallback callback) {
      callbacks.add(callback);
    }
    
    /**
     * Simulates a timer interval expiring
     */
    public void periodComplete() {
      for (PeriodCompletedCallback callback : callbacks) {
        callback.doAction();
      }
    }

    @Override
    public void stop() {
      // does nothing
    }

    @Override
    public boolean isRunning() {
      return false;
    }
  }
  
  private KeyLoggerDiagnosticService service;
  private FakeTimer timer;
  private KeyLogger logger;

  
  @Before
  public void setUp() throws Exception {
    this.logger = new KeyLogger();
    this.timer = new FakeTimer();
    this.service = new KeyLoggerDiagnosticService(this.timer, this.logger);
  }

  @Test
  public void testNoKeys() {
    assertEquals(0, this.service.count("a"));
    assertEquals(0, this.service.rate("a"));
    assertEquals(0, this.service.avg("a"));
  }

  @Test
  public void testWithinFirstPeriod() {
    this.logger.log("a", "b", "c");
    assertEquals(0, this.service.count("a"));
    assertEquals(0, this.service.rate("a"));
    assertEquals(0, this.service.avg("a"));
  }

  @Test
  public void testAfterFirstPeriod() {
    this.logger.log("a");
    this.logger.log("a", "b");
    this.logger.log("a", "b", "c");
    this.timer.periodComplete();  
    assertEquals(3, this.service.count("a"));
    assertEquals(2, this.service.count("b"));
    assertEquals(1, this.service.count("c"));
    assertEquals(0, this.service.rate("a"));
    assertEquals(0, this.service.rate("b"));
    assertEquals(0, this.service.rate("c"));
    assertEquals(3, this.service.avg("a"));
    assertEquals(2, this.service.avg("b"));
    assertEquals(1, this.service.avg("c"));
  }

  @Test
  public void testAfterSecondPeriod() {
    this.logger.log("a");
    this.logger.log("a", "b");
    this.logger.log("a", "b", "c");
    this.timer.periodComplete();  
    this.logger.log("a", "a", "a", "a", "a");
    this.logger.log("b", "b", "b", "b");
    this.logger.log("c", "c", "c");
    this.timer.periodComplete();  
    assertEquals(5, this.service.count("a"));
    assertEquals(4, this.service.count("b"));
    assertEquals(3, this.service.count("c"));
    assertEquals(2, this.service.rate("a"));
    assertEquals(2, this.service.rate("b"));
    assertEquals(2, this.service.rate("c"));
    assertEquals(4, this.service.avg("a"));
    assertEquals(3, this.service.avg("b"));
    assertEquals(2, this.service.avg("c"));
  }

  @Test
  public void testAfterThirdPeriod() {
    this.logger.log("a");
    this.timer.periodComplete();  
    this.logger.log("a", "a", "a");
    this.timer.periodComplete();  
    this.logger.log("a", "a", "a", "a", "a");
    this.timer.periodComplete();  
    assertEquals(5, this.service.count("a"));
    assertEquals(2, this.service.rate("a"));
    assertEquals(3, this.service.avg("a"));
  }
}
