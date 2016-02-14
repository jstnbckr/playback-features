package com.netflix.common;

/**
 * Interface which must be implemented by all event subscribers
 * which are to received event notifications. 
 * @author tom
 *
 * @param <T> the type of the event the wrapping {@link SynchronizedEventBus}
 *            will pass to the subscriber.
 */
public interface EventSubscriber<T> {
  /**
   * Notifies the underlying implementation that the given
   * event occurred.
   * @param event the event
   */
  public void onEvent(T event);
}