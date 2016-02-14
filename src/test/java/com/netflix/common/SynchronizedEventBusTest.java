package com.netflix.common;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynchronizedEventBusTest {

  private class FakeEvent {}
  
  private class FakeSubscriber implements EventSubscriber<FakeEvent> {
    private int callCount = 0;
    
    @Override
    public void onEvent(FakeEvent event) {
      this.callCount++;
    }
  }
  
  private SynchronizedEventBus<FakeEvent> bus;
  
  @Before
  public void setUp() throws Exception {
    this.bus = new SynchronizedEventBus<SynchronizedEventBusTest.FakeEvent>();
  }

  @Test
  public void testNoSubscribers() {
    // Really just testing that this doesn't blow up
    bus.postEvent(new FakeEvent());
  }

  @Test
  public void testMultipleSubscribers() {
    FakeSubscriber subA = new FakeSubscriber();
    bus.addSubscriber(subA);
    bus.postEvent(new FakeEvent());
    FakeSubscriber subB = new FakeSubscriber();
    bus.addSubscriber(subB);
    bus.postEvent(new FakeEvent());
    assertEquals(2, subA.callCount);
    assertEquals(1, subB.callCount);
  }
  
  @Test
  public void testAddSubscriberMultipleTimes() {
    FakeSubscriber subA = new FakeSubscriber();
    bus.addSubscriber(subA);
    bus.postEvent(new FakeEvent());    
    bus.addSubscriber(subA);
    bus.addSubscriber(subA);
    bus.postEvent(new FakeEvent());    
    assertEquals(2, subA.callCount);
  }
}
