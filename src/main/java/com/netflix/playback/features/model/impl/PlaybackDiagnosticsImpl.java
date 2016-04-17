package com.netflix.playback.features.model.impl;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.PlaybackDiagnostics;
import com.netflix.playback.features.model.PlaybackRequest;

public class PlaybackDiagnosticsImpl extends PlaybackDiagnostics {
	
	public PlaybackDiagnosticsImpl(DiagnosticService diagnosticService) {
		super(diagnosticService);
	}

	@Override
	public void log(PlaybackRequest request) {
		diagnosticService.logStat(request.getViewableId());
	}

	@Override
	public int requestRate() {
		return diagnosticService.requestRate();
	}

	@Override
	public int requestRate(int viewableId) {
		return diagnosticService.requestRate(viewableId);
	}

	@Override
	public int avgRate(int viewableId) {
		return diagnosticService.avg(viewableId);
	}

}
