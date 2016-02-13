package com.netflix.playback.features.model;

import com.netflix.common.EventSubscriber;
import com.netflix.common.SynchronizedEventBus;

/**
 * A KeyLogger is used to log occurrences of keys. 
 * 
 * Behaves much like a classic Logger, allowing multiple
 * handlers to be registered, to which all logged keys
 * are forwarded. 
 * 
 * The KeyLogger itself imposes no opinion on the semantics
 * of what keys represent to consumers, it in effect
 * simply acts as a dumb string router.
 * @author tom
 *
 */
// TODO: Split this into an ABC and synchronous subclass
public class KeyLogger {
  /**
   * Handler interface for KeyLogger handlers. Any class wishing
   * to register as a handler of KeyLogger keys must implement
   * this interface.
   * @author tom
   *
   */
  public interface Handler {
    /**
     * Handles an array of string keys.
     * @param key array of strings
     */
    public void handle(String... keys);
  }
  
  /**
   * Private Event class used to wrap a logged set of 
   * string keys in such away that they can be published
   * to subscribers via the {@link SynchronizedEventBus}. 
   * @author tom
   *
   */
  private class Event {
    private final String[] keys;
    
    private Event(String[] keys) {
      this.keys = keys;
    }
    
    private String[] getKeys() {
      return this.keys;
    }
  }
  
  /**
   * A subclass of {@link EventSubscriber} which 
   * simply translates {@link SynchronizedEventBus} callbacks in to 
   * handler calls.
   * @author tom
   *
   */
  private class Subscriber implements EventSubscriber<KeyLogger.Event> {
    private final Handler handler;
    
    /**
     * Instantiates the subscriber.
     * @param handler the handler to be wrapped.
     */
    private Subscriber(Handler handler) {
      this.handler = handler;
    }
    
    @Override
    public void onEvent(Event event) {
      this.handler.handle(event.getKeys());
    }
  }
  
  private final SynchronizedEventBus<KeyLogger.Event> eventBus = new SynchronizedEventBus<KeyLogger.Event>();
  
  /**
   * Registers the given handler to receive all subsequent logged keys.
   * @param handler the handler
   */
  public void addHandler(Handler handler) {
    this.eventBus.addSubscriber(new KeyLogger.Subscriber(handler));
  }
  
  /**
   * Logs one or more keys to the logger. All logged keys are forwarded to registered
   * handlers.
   * @param keys the keys
   */
  public void log(String... keys) {
    this.eventBus.postEvent(new Event(keys));
  }
}
