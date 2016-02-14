package com.netflix.common;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A synchronized (thread-safe) event bus.
 * 
 * Allows subscribers to be registered and then notified whenever a new event is
 * posted to the bus. 
 * 
 * Subscriber dispatch is not ordered.
 * 
 * @author tom
 *
 * @param <T> the type of the events this bus will accept and propagate to subscribers
 */
public final class SynchronizedEventBus<T> {
  
  // Use a CopyOnWriteArraySet to store the subscribers. This ensures we don't need to
  // synchronize access (optimizing for postEvent speed). Mutation is more expensive
  // but that's fine because subscriber modifications are likely to be relatively rare.
  private final Set<EventSubscriber<T>> subscribers = new CopyOnWriteArraySet<EventSubscriber<T>>();
  
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
   * Deregisters the given subscriber instance which will subsequently
   * no longer receive notifications. 
   * @param subscriber the subscriber
   */
  public void removeSubscriber(EventSubscriber<T> subscriber) {
    this.subscribers.remove(subscriber);
  }
  
  /**
   * Posts a new event to the event bus which in turn will propagate
   * it to all registered subscribers.
   * @param event the event
   */
  public void postEvent(T event) {
    for (EventSubscriber<T> subscriber : subscribers) {
      subscriber.onEvent(event);
    }      
  }
}
