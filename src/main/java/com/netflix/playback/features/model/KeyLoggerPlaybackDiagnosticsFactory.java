package com.netflix.playback.features.model;

import java.util.concurrent.TimeUnit;

public final class KeyLoggerPlaybackDiagnosticsFactory extends PlaybackDiagnosticsFactory {

  private final KeyLogger keyLogger;
  
  public KeyLoggerPlaybackDiagnosticsFactory(KeyLogger keyLogger) {
    super();
    this.keyLogger = keyLogger;
  }

  @Override
  public PlaybackDiagnostics create(TimeUnit timeUnit, long periodLength) {
    Timer timer = new SimpleTimer(timeUnit, periodLength);
    DiagnosticService diagnosticService = new KeyLoggerDiagnosticService(timer, this.keyLogger);
    return new KeyLoggerPlaybackDiagnostics(diagnosticService, timer, this.keyLogger);
  }
  
  
}
