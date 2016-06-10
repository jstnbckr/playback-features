package com.netflix.playback.features.model;

public abstract class PlaybackDiagnostics {

    protected DiagnosticService diagnosticService;
    
    public PlaybackDiagnostics(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    public abstract void log(PlaybackRequest request);
    
    /**
     * @param viewableId
     * @return most recent rate of playback requests for the given viewableId
     */
    public abstract int viewableRate(String viewableId);
    
    /**
     * @param viewableId
     * @return most recent rate of playback requests for the given country
     */
    public abstract int countryRate(String country);
    
}
