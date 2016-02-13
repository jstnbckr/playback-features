package com.netflix.playback.features.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeTimer extends Timer {
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