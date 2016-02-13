package com.netflix.playback.features.model;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class KeyLoggerTest {
  private KeyLogger keyLogger = new KeyLogger();
  
  private class FakeHandler implements KeyLogger.Handler {
    private final List<String> allKeys = new ArrayList<String>();
    
    @Override
    public void handle(String... keys) {
      allKeys.addAll(Arrays.asList(keys));
    }
  }
  
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
    FakeHandler handlerA = new FakeHandler();
    FakeHandler handlerB = new FakeHandler();
    keyLogger.addHandler(handlerA);
    keyLogger.addHandler(handlerB);
    keyLogger.log("a", "b", "c");
    keyLogger.log("a", "b", "c", "d");
    String[] expected = new String[]{"a", "b", "c", "a", "b", "c", "d"};
    assertArrayEquals(expected, handlerA.allKeys.toArray());
    assertArrayEquals(expected, handlerB.allKeys.toArray());
  }
}
