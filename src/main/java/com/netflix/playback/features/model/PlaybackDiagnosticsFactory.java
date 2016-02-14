package com.netflix.playback.features.model;

import java.util.concurrent.TimeUnit;

public abstract class PlaybackDiagnosticsFactory {

  public abstract PlaybackDiagnostics create(TimeUnit timeUnit, long periodLength);
  
}
