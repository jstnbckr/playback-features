package com.netflix.playback.features.model;

public class PlaybackRequest {
    
    private String customerId;
    private int viewableId;
    private String country;
    
    public PlaybackRequest(String customerId, int viewableId, String country) {
        this.customerId = customerId;
        this.viewableId = viewableId;
        this.country = country;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getViewableId() {
        return viewableId;
    }

    public String getCountry() {
        return country;
    }

    public String toString() {
        return customerId + " : " + viewableId + " : " + country;
    }
}
