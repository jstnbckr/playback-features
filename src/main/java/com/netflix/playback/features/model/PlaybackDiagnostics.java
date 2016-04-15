package com.netflix.playback.features.model;

public abstract class PlaybackDiagnostics {

    protected DiagnosticService diagnosticService;
    
    public PlaybackDiagnostics(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    public abstract void log(PlaybackRequest request);
    
    /**
     * @return rate of playback requests for the last two completed time periods
     */
    public abstract int requestRate();
    
    /**
     * @param viewableId
     * @return rate of playback requests for the given viewableId for the last two completed time periods
     */
    public abstract int requestRate(int viewableId);
    
    /**
     * @param viewableId
     * @return average rate of playback requests for the given viewableId across all completed periods
     */
    public abstract int avgRate(int viewableId);
}
