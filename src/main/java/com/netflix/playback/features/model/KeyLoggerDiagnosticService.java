package com.netflix.playback.features.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A subclass of {@link DiagnosticService} which listens to keys
 * emitted from a provided {@link KeyLogger} and uses these keys
 * to build and serve it's queryable diagnostic data set. 
 * @author tom
 *
 */
public final class KeyLoggerDiagnosticService extends DiagnosticService {
  
  private final KeyCounter allPeriodsCounter = new KeyCounter();
  private final Deque<KeyCounter> previousPeriodCounters = new ArrayDeque<KeyCounter>();
  private KeyCounter currentPeriodCounter = new KeyCounter();
  private final KeyLogger keyLogger;
  
  /**
   * Instantiates the service, registering itself for timer callbacks
   * at the pre-configured intervals and to receive all keys logged to
   * the provided {@link KeyLogger}
   * @param timer time
   * @param keyLogger the key logger
   */
  public KeyLoggerDiagnosticService(Timer timer, KeyLogger keyLogger) {
    super(timer, 0);
    this.keyLogger = keyLogger;
    keyLogger.addHandler(new KeyLogger.Handler() {
      
      @Override
      public void handle(String... keys) {
        KeyLoggerDiagnosticService.this.log(keys);
      }
    });
    
    this.timer.addCallback(new PeriodCompletedCallback() {
      
      @Override
      public void doAction() {
        KeyLoggerDiagnosticService.this.rotateCounters();
      }
    });
  }
  
  /**
   * "Rotates" the counters maintained by this service instance.
   * 
   * This action is taken in response to a timer period completion 
   * callback indicating that we should stop counting keys for the 
   * current period and start a new period.
   * 
   * In practice this means that:
   * <ul>
   * <li>the current counter should be added into the "all time counter"</li>
   * <li>the current counter should be moved into the list of previous counters</li>
   * <li>the oldest previous counter should be removed to ensure we retain at most 2 previous counters</li>
   * </ul> 
   * 
   * Synchronized to ensure we don't attempt to log keys while this process is
   * in progress.
   */
  private synchronized void rotateCounters() {
    allPeriodsCounter.addOther(currentPeriodCounter);
    previousPeriodCounters.add(currentPeriodCounter);
    currentPeriodCounter = new KeyCounter();
    if (previousPeriodCounters.size() > 2) {
      previousPeriodCounters.pop();  
    }
    this.numCompletedPeriods++;
  }
  
  /**
   * Logs keys to the current counter. 
   * @param keys the keys.
   */
  private synchronized void log(String... keys) {
    for (String key : keys) {
      this.currentPeriodCounter.increment(key);
    }
  }

  @Override
  public synchronized int count(String key) {
    if (previousPeriodCounters.size() < 1) {
      return 0;
    }
    return this.previousPeriodCounters.getLast().getCount(key);
  }

  @Override
  public synchronized int rate(String key) {
    if (previousPeriodCounters.size() < 2) {
      return 0;
    }
    return (
        previousPeriodCounters.getLast().getCount(key) - 
        previousPeriodCounters.getFirst().getCount(key));
  }

  @Override
  public synchronized int avg(String key) {
    if (this.numCompletedPeriods < 1) {
      return 0;
    }
    return allPeriodsCounter.getCount(key) / this.numCompletedPeriods;
  }

  /**
   * @return the key logger in use by this object.
   */
  public KeyLogger getKeyLogger() {
    return this.keyLogger;
  }
  
  /**
   * @return the timer used by this object to signal period intervals.
   */
  public Timer getTimer() {
    return this.timer;
  }
}
