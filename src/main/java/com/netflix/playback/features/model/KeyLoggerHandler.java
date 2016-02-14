package com.netflix.playback.features.model;

/**
 * Handler interface for KeyLogger handlers. Any class wishing
 * to register as a handler of KeyLogger keys must implement
 * this interface.
 * @author tom
 *
 */
public interface KeyLoggerHandler {
  /**
   * Handles an array of string keys.
   * @param key array of strings
   */
  public void handle(String... keys);
}