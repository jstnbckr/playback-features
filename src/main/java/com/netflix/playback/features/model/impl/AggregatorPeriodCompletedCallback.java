package com.netflix.playback.features.model.impl;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.PeriodCompletedCallback;

public class AggregatorPeriodCompletedCallback implements PeriodCompletedCallback {

	private DiagnosticService diagnosticService;
	
	public AggregatorPeriodCompletedCallback(DiagnosticService diagnosticService) {
		this.diagnosticService = diagnosticService;
	}

	@Override
	public void doAction() {
		diagnosticService.flush();
	}
}
