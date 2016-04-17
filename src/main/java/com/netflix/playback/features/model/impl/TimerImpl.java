package com.netflix.playback.features.model.impl;

import java.util.concurrent.TimeUnit;

import com.netflix.playback.features.model.PeriodCompletedCallback;
import com.netflix.playback.features.model.Timer;

public class TimerImpl extends Timer {

	PeriodCompletedCallback periodCompletedCallBack;

	public TimerImpl(TimeUnit timeUnit, long periodLength) {
		super(timeUnit, periodLength);
	}

	@Override
	public void start() {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				stop();
			}
		}, timeUnit.toMillis(periodLength));
	}

	@Override
	public void addCallback(PeriodCompletedCallback callback) {
		this.periodCompletedCallBack = callback;
	}

	@Override
	public void stop() {
		periodCompletedCallBack.doAction();

	}

}
