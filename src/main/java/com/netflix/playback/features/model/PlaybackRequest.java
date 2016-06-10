package com.netflix.playback.features.model;

public class PlaybackRequest {
    
    private String customerId;
    private String viewableId;
    private String country;
    
    public PlaybackRequest(String customerId, String viewableId, String country) {
        this.customerId = customerId;
        this.viewableId = viewableId;
        this.country = country;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getViewableId() {
        return viewableId;
    }

    public String getCountry() {
        return country;
    }
}
