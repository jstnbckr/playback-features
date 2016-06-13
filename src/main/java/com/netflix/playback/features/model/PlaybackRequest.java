package com.netflix.playback.features.model;

import com.google.common.base.MoreObjects.ToStringHelper;

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

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("customerId", this.getCustomerId())
                .add("viewableId", this.getViewableId())
                .add("country", this.getCountry());
    }
}
