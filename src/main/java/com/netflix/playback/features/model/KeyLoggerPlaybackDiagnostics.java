package com.netflix.playback.features.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete {@link PlaybackDiagnostics} subclass which exposes
 * various counts, rates and averages. 
 *  
 * @author tom
 *
 */
public class KeyLoggerPlaybackDiagnostics extends PlaybackDiagnostics {
  
  private static final String REQUEST_KEY = "request";
  private static final String UNIQUE_CUSTOMER_KEY = "uniqueCustomers";
  private static final String UNIQUE_VIEWABLES_KEY = "uniqueViewables";
  private static final String COUNTRY_KEY_FORMAT = "country:%s";
  private static final String VIEWABLE_KEY_FORMAT = "viewableId:%d";
  private static final String CUSTOMER_KEY_FORMAT = "customerId:%s";
  
  private final KeyLogger keyLogger;
  private final Timer timer;
  private final Set<String> uniqueCustomers = new HashSet<String>();
  private final Set<Integer> uniqueViewables = new HashSet<Integer>();
  
  /**
   * @param diagnosticService a diagnostic service instance
   * @param timer the timer used to signal when each period ends
   * @param keyLogger key logger to which new request data should be logged.
   */
  public KeyLoggerPlaybackDiagnostics(DiagnosticService diagnosticService, Timer timer, KeyLogger keyLogger) {
    super(diagnosticService);
    this.keyLogger = keyLogger;
    this.timer = timer;
    this.timer.addCallback(new PeriodCompletedCallback() {
      
      @Override
      public void doAction() {
        KeyLoggerPlaybackDiagnostics.this.resetSets();
      }
    });
  }
  
  /**
   * Clears the sets tracking the unique customer and viewables for the 
   * current period. 
   */
  private synchronized void resetSets() {
    this.uniqueCustomers.clear();
    this.uniqueViewables.clear();
  }
  
  /**
   * Encodes the string key used to represent a request 
   * associated with the given country.
   * @param country the country
   * @return the encoded key
   */
  private String encodeCountryKey(String country) {
    return String.format(COUNTRY_KEY_FORMAT, country);
  }
  
  /**
   * Encodes the string key used to represent a request 
   * associated with the given viewable.
   * @param viewableId the id of the viewable
   * @return the encoded key
   */
  private String encodeViewableKey(int viewableId) {
    return String.format(VIEWABLE_KEY_FORMAT, viewableId);
  }
  
  /**
   * Encodes the string key used to represent a request 
   * associated with the given customer.
   * @param customerId the customer id
   * @return the encoded key
   */
  private String encodeCustomerKey(String customerId) {
    return String.format(CUSTOMER_KEY_FORMAT, customerId);
  }
  
  @Override
  public synchronized void log(PlaybackRequest request) {
    // Break the request down into several attribute-based keys 
    // which we'll log separately. This then allows us to 
    // perform queries against those keys later.
    this.keyLogger.log(
        REQUEST_KEY,
        encodeCountryKey(request.getCountry()),
        encodeCustomerKey(request.getCustomerId()),
        encodeViewableKey(request.getViewableId()));
    
    // If we determine that the customer or viewable have not
    // been seen before in the current period, then log a
    // key which indicates we've seen a new distinct 
    // customer/viewable.
    if (this.uniqueCustomers.add(request.getCustomerId())) {
      this.keyLogger.log(UNIQUE_CUSTOMER_KEY);
    }
    if (this.uniqueViewables.add(request.getViewableId())) {
      this.keyLogger.log(UNIQUE_VIEWABLES_KEY);
    }      
  }

  @Override
  public int requestCount() {
    return this.diagnosticService.count(REQUEST_KEY);
  }

  @Override
  public int avgRateRequestsPerCountry(String country) {
    return this.diagnosticService.avg(encodeCountryKey(country));
  }

  @Override
  public int requestCount(int viewableId) {
    return this.diagnosticService.count(encodeViewableKey(viewableId));
  }

  @Override
  public int uniqueCustomerCount() {
    return this.diagnosticService.count(UNIQUE_CUSTOMER_KEY);
  }

  @Override
  public int uniqueViewableCount() {
    return this.diagnosticService.count(UNIQUE_VIEWABLES_KEY);
  }
}
