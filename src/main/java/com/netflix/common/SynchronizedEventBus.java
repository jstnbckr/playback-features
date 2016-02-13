package com.netflix.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A synchronized (thread-safe) event bus.
 * 
 * Allows subscribers to be registered and then notified whenever a new event is
 * posted to the bus. 
 * @author tom
 *
 * @param <T> the type of the events this bus will accept and propagate to subscribers
 */
public class SynchronizedEventBus<T> {
  
  private final Set<EventSubscriber<T>> subscribers = Collections.synchronizedSet(new HashSet<EventSubscriber<T>>());
  
  /**
   * Registers an {@link EventSubscriber} instance to receive notifications from
   * this {@link SynchronizedEventBus}. 
   * 
   * Note that registering the same subscriber instance more than once has no
   * effect.
   * 
   * @param subscriber the subscriber instance
   */
  public void addSubscriber(EventSubscriber<T> subscriber) {
    this.subscribers.add(subscriber);
  }
  
  /**
   * Posts a new event to the event bus which in turn will propagate
   * it to all registered subscribers.
   * @param event the event
   */
  public void postEvent(T event) {
    // Ensure we synchronize on the Set as we are iterating.
    synchronized (subscribers) {
      for (EventSubscriber<T> subscriber : subscribers) {
        subscriber.onEvent(event);
      }      
    }
  }
}
