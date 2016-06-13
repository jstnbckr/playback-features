package com.netflix.playback.features.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.playback.features.model.PeriodCompletedCallback;

public class DiagnosticServiceCallback implements PeriodCompletedCallback {

    private static Logger logger = LoggerFactory.getLogger(DiagnosticServiceCallback.class);
    
    private DiagnosticServiceImpl diagnosticService;

    public DiagnosticServiceCallback(DiagnosticServiceImpl diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @Override
    public void doAction() {
        int numCompletedPeriods = this.diagnosticService.incrementNumCompletedPeriods();
        logger.info("numCompletedPeriods {}", numCompletedPeriods);
    }

}
