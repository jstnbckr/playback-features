package com.netflix.playback.features.model;

public abstract class PlaybackDiagnostics {

    protected DiagnosticService diagnosticService;
    
    public PlaybackDiagnostics(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    public abstract void log(PlaybackRequest request);
    
    /**
     * @return number of playback requests for the last completed time period
     */
    public abstract int requestCount();
        
    /**
     * @param country
     * @return average rate of playback requests for the given country
     */
    public abstract int avgRateRequestsPerCountry(String country);
    
    /**
     * @param viewableId
     * @return number of playback requests for the given viewableId for the last completed time period
     */
    public abstract int requestCount(int viewableId);
    
    /**
     * @return number of unique playback requests by customer for the last completed time period
     */
    public abstract int uniqueCustomerCount();
    
    /**
     * @return number of unique playback requests by viewable for the last completed time period
     */
    public abstract int uniqueViewableCount();
}
