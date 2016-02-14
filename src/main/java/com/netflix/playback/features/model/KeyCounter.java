package com.netflix.playback.features.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A counter which maintains separate per-key counts.
 * 
 * Reads and writes are synchronized
 * 
 * @author tom
 *
 */
public final class KeyCounter {

  private final Map<String, AtomicInteger> countMap = Collections.synchronizedMap(
      new HashMap<String, AtomicInteger>());
  
  /**
   * Creates a counter for the given key if it does not already exists and 
   * returns it. Returns the existing counter if it already exists.
   * @param key the key identifying the counter
   * @return the counter associated with the key
   */
  private AtomicInteger ensureCounter(String key) {
    synchronized (this.countMap) {      
      AtomicInteger counter = countMap.get(key);
      if (counter == null) {
        counter = new AtomicInteger();
        countMap.put(key, counter);
      }
      return counter;
    }
  }
  
  /**
   * Increments the count for the given key by 1.
   * @param key the key
   */
  public void increment(String key) {
    this.ensureCounter(key).incrementAndGet();
  }
  
  /**
   * Increments the count for the given key by the given value.
   * @param key the key
   * @param value the value to increment by
   */
  public void increment(String key, int value) {
    this.ensureCounter(key).addAndGet(value);
  }
  
  /**
   * Returns the current count for the given key.
   * @param key the key
   * @return the count
   */
  public int getCount(String key) {
    AtomicInteger counter = this.countMap.get(key);
    if (counter != null) {
      return counter.get();
    }
    return 0;
  }
  
  /**
   * Adds the key counts from the given other KeyCounter
   * into this KeyCounter. 
   * @param other
   */
  public void addOther(KeyCounter other) {
    synchronized (other.countMap) {
      for (Entry<String, AtomicInteger> entry : other.countMap.entrySet()) {
        this.increment(entry.getKey(), entry.getValue().intValue());
      }      
    }
  }
}
