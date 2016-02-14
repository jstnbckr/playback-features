package com.netflix.playback.features.model;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.netflix.common.EventSubscriber;
import com.netflix.common.SynchronizedEventBus;

/**
 * A simple {@link Timer} implementation which uses a fixed-rate
 * {@link java.util.Timer} internally to signal at regular
 * intervals as indicated by the provided constructor args.
 * @author tom
 *
 */
public final class SimpleTimer extends Timer {
  
  private class Event {}
  
  /**
   * {@link EventSubscriber} implementation which simply
   * forwards the received event notification to the provided {@link PeriodCompletedCallback}. 
   * @author tom
   *
   */
  private class Subscriber implements EventSubscriber<SimpleTimer.Event> {
    private final PeriodCompletedCallback callback;
    
    /**
     * Creates a new instance of the subscriber.
     * @param callback the {@link PeriodCompletedCallback} to be invoked in response to
     *        events being received by this subscriber.
     */
    public Subscriber(PeriodCompletedCallback callback) {
      this.callback = callback;
    }
    
    @Override
    public void onEvent(Event event) {
      callback.doAction();
    } 
  }
  
  private final SynchronizedEventBus<SimpleTimer.Event> eventBus = new SynchronizedEventBus<SimpleTimer.Event>(); 
  private final java.util.Timer timer = new java.util.Timer(true);
  private boolean isRunning = false;
  
  public SimpleTimer(TimeUnit timeUnit, long periodLength) {
    super(timeUnit, periodLength);
  }

  @Override
  public synchronized void start() {
    if (this.isRunning) {
      throw new IllegalStateException("Cannot start an already-running timer");
    }
    
    long periodInMilliseconds = this.timeUnit.toMillis(this.periodLength);
    timer.scheduleAtFixedRate(new TimerTask() {
      
      @Override
      public void run() {
        SimpleTimer.this.eventBus.postEvent(new SimpleTimer.Event());
      }
    }, periodInMilliseconds, periodInMilliseconds);
    this.isRunning = true;
  }

  @Override
  public void addCallback(PeriodCompletedCallback callback) {
    this.eventBus.addSubscriber(new SimpleTimer.Subscriber(callback));
  }

  @Override
  public synchronized void stop() {
    if (!this.isRunning) {
      throw new IllegalStateException("Cannot stop a non-running timer");
    }    

    this.timer.cancel();
    this.isRunning = false;
  }
  
  @Override
  public synchronized boolean isRunning() {
    return this.isRunning;
  }
}
