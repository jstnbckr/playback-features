package com.netflix.playback.features.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class KeyLoggerPlaybackDiagnosticsTest {

  private class FakeDiagnosticService extends DiagnosticService {
    private Map<String, Integer> counts = new HashMap<String, Integer>();
    private Map<String, Integer> rates = new HashMap<String, Integer>();
    private Map<String, Integer> avgs = new HashMap<String, Integer>();

    public FakeDiagnosticService(Timer timer, int numCompletedPeriods) {
      super(timer, numCompletedPeriods);
    }
    
    @Override
    public int avg(String key) {
      return avgs.get(key);
    }
    
    @Override
    public int count(String key) {
      return counts.get(key);
    }
    
    @Override
    public int rate(String key) {
      return rates.get(key);
    }
  }
  
  private KeyLoggerPlaybackDiagnostics diagnostics;
  private KeyLogger keyLogger;
  private DiagnosticService diagnosticService;
  private FakeTimer timer;
  
  @Before
  public void setUp() throws Exception {
    this.timer = new FakeTimer();
    this.keyLogger = new KeyLogger();
    this.diagnosticService = new KeyLoggerDiagnosticService(this.timer, this.keyLogger);
    this.diagnostics = new KeyLoggerPlaybackDiagnostics(diagnosticService, timer, keyLogger);
  }

  @Test
  public void testLogging() {
    FakeKeyLoggerHandler handler = new FakeKeyLoggerHandler();
    this.keyLogger.addHandler(handler);
    
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "UK"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    
    String[] expected = new String[]{
        "country:UK", "country:UK", "country:USA", "customerId:bob", "customerId:bob", 
        "customerId:jim", "request", "request", "request", "uniqueCustomers", 
        "uniqueCustomers", "uniqueViewables", "uniqueViewables", "viewableId:32", 
        "viewableId:32", "viewableId:33"};
    handler.allKeys.sort(null);
    assertArrayEquals(expected, handler.allKeys.toArray());
  }

  @Test
  public void testRequestCount(){
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "UK"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    assertEquals(0, this.diagnostics.requestCount());
    assertEquals(0, this.diagnostics.requestCount(32));
    assertEquals(0, this.diagnostics.requestCount(30));
    this.timer.periodComplete();
    assertEquals(3, this.diagnostics.requestCount());
    assertEquals(2, this.diagnostics.requestCount(32));
    assertEquals(0, this.diagnostics.requestCount(30));
  }
  
  @Test
  public void testAvgRateRequestsPerCountry() {
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "UK"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    assertEquals(0, this.diagnostics.avgRateRequestsPerCountry("UK"));
    assertEquals(0, this.diagnostics.avgRateRequestsPerCountry("USA"));
    assertEquals(0, this.diagnostics.avgRateRequestsPerCountry("FR"));
    this.timer.periodComplete();
    assertEquals(2, this.diagnostics.avgRateRequestsPerCountry("UK"));
    assertEquals(1, this.diagnostics.avgRateRequestsPerCountry("USA"));
    assertEquals(0, this.diagnostics.avgRateRequestsPerCountry("FR"));
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "USA"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "USA"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    this.timer.periodComplete();
    assertEquals(2, this.diagnostics.avgRateRequestsPerCountry("UK"));
    assertEquals(2, this.diagnostics.avgRateRequestsPerCountry("USA"));
    assertEquals(0, this.diagnostics.avgRateRequestsPerCountry("FR"));    
  }
  
  @Test 
  public void testUniqueCustomers() {
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "UK"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    assertEquals(0, this.diagnostics.uniqueCustomerCount());
    this.timer.periodComplete();
    assertEquals(2, this.diagnostics.uniqueCustomerCount());
  }

  @Test 
  public void testUniqueViewables() {
    this.diagnostics.log(new PlaybackRequest("bob", 33, "UK"));
    this.diagnostics.log(new PlaybackRequest("bob", 32, "UK"));
    this.diagnostics.log(new PlaybackRequest("jim", 32, "USA"));
    assertEquals(0, this.diagnostics.uniqueViewableCount());
    this.timer.periodComplete();
    assertEquals(2, this.diagnostics.uniqueViewableCount());
  }
}
