package com.netflix.playback.features.model;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

public class KeyLoggerTest {
  private KeyLogger keyLogger = new KeyLogger();
  
  @Before
  public void setUp() throws Exception {
    keyLogger = new KeyLogger();
  }

  @Test
  public void testNoHandlers() {
    keyLogger.log("a", "b", "c");
  }

  @Test
  public void testMultpleHandlers() {
    FakeKeyLoggerHandler handlerA = new FakeKeyLoggerHandler();
    FakeKeyLoggerHandler handlerB = new FakeKeyLoggerHandler();
    keyLogger.addHandler(handlerA);
    keyLogger.addHandler(handlerB);
    keyLogger.log("a", "b", "c");
    keyLogger.log("a", "b", "c", "d");
    String[] expected = new String[]{"a", "b", "c", "a", "b", "c", "d"};
    assertArrayEquals(expected, handlerA.allKeys.toArray());
    assertArrayEquals(expected, handlerB.allKeys.toArray());
  }
}
