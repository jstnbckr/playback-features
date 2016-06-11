package com.netflix.playback.features.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.PlaybackDiagnostics;
import com.netflix.playback.features.model.PlaybackRequest;

public class PlaybackDiagnosticsImpl extends PlaybackDiagnostics {

    private static Logger logger = LoggerFactory.getLogger(PlaybackDiagnosticsImpl.class);

    public PlaybackDiagnosticsImpl(DiagnosticService diagnosticService) {
        super(diagnosticService);
    }

    @Override
    public void log(PlaybackRequest request) {
        logger.info("request {}", request);
        this.diagnosticService.log(request.getCountry());
        this.diagnosticService.log(request.getViewableId());
    }

    @Override
    public int viewableRate(String viewableId) {
        ValidationUtils.validateViewableId(viewableId);
        return this.diagnosticService.rate(viewableId);
    }

    @Override
    public int countryRate(String country) {
        ValidationUtils.validateCountryKey(country);
        return this.diagnosticService.rate(country);
    }
}
