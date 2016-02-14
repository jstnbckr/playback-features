package com.netflix.playback.features.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FakeKeyLoggerHandler implements KeyLoggerHandler {
  public final List<String> allKeys = new ArrayList<String>();
  
  @Override
  public void handle(String... keys) {
    allKeys.addAll(Arrays.asList(keys));
  }
}