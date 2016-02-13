package com.netflix.playback.features.model;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.css.Counter;

import com.netflix.playback.features.model.KeyCounter;

public class KeyCounterTest {
  
  private KeyCounter counter;
  
  @Before
  public void setUp() throws Exception {
    this.counter = new KeyCounter();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testIncrementSingleKey() {
    this.counter.increment("a");
    this.counter.increment("a");
    assertEquals(2, this.counter.getCount("a"));
  }

  @Test
  public void testIncrementMultipleKeys() {
    this.counter.increment("a");
    this.counter.increment("b");
    this.counter.increment("c");
    this.counter.increment("b");
    this.counter.increment("c");
    this.counter.increment("c");
    assertEquals(1, this.counter.getCount("a"));
    assertEquals(2, this.counter.getCount("b"));
    assertEquals(3, this.counter.getCount("c"));    
  }

  @Test
  public void testIncrementSingleKeyByValue() {
    this.counter.increment("a", 4);
    this.counter.increment("a", 2);
    assertEquals(6, this.counter.getCount("a"));
  }

  @Test
  public void testIncrementMultipleKeysByValue() {
    this.counter.increment("a", 2);
    this.counter.increment("b", 3);
    this.counter.increment("c", 4);
    this.counter.increment("b", 5);
    this.counter.increment("c", 6);
    this.counter.increment("c", 7);
    assertEquals(2, this.counter.getCount("a"));
    assertEquals(8, this.counter.getCount("b"));
    assertEquals(17, this.counter.getCount("c"));    
  }

  @Test
  public void testAddOther() {
    KeyCounter other = new KeyCounter();
    other.increment("a", 2);
    other.increment("b", 3);
    this.counter.increment("b");
    this.counter.increment("c", 5);
    this.counter.addOther(other);
    assertEquals(2, this.counter.getCount("a"));
    assertEquals(4, this.counter.getCount("b"));
    assertEquals(5, this.counter.getCount("c"));
  }

}
