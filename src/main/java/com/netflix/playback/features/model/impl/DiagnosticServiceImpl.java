package com.netflix.playback.features.model.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.Timer;
/**
 *  The service maintains the instance of timer and start event of it
 *  The service stores the log events for the current time period.
 *  When timer is stopped the callback is invoked and the service flushes the data to aggregator 
 *  for various calculations.
 */
public class DiagnosticServiceImpl extends DiagnosticService {

	//store the events count for the current time period
	private final ConcurrentMap<Integer, AtomicInteger> currentPeriodCount = new ConcurrentHashMap<>();
	private final PeriodAggregator aggregator = new PeriodAggregator();
	private final Lock lock = new ReentrantLock();
	 
	public DiagnosticServiceImpl(Timer timer, int numCompletedPeriods) {
		super(timer, numCompletedPeriods);
	}

	@Override
	public int rate(int key) {
		Optional<Integer> lastRateOptional = aggregator.getRateForPeriod(key, numCompletedPeriods);
		Optional<Integer> lastMinusOneRateOptional = aggregator.getRateForPeriod(key, numCompletedPeriods -1);
		if (lastRateOptional.isPresent() && lastMinusOneRateOptional.isPresent()) {
			return (lastRateOptional.get() - lastMinusOneRateOptional.get());
		} else if (lastRateOptional.isPresent()) {
			return lastRateOptional.get();
		} else {
			return lastMinusOneRateOptional.get() * -1;
		}
	}

	@Override
	public int avg(int key) {
		return aggregator.getAggregateRates(key, numCompletedPeriods);
	}

	@Override
	public void logStat(int key) {
		start();
		incrementStat(key);
	}

	private void incrementStat(int key) {
		AtomicInteger count = currentPeriodCount.get(key);
		if (count == null) {
			count = currentPeriodCount.putIfAbsent(key, new AtomicInteger(1));
		}
		if (count != null) {
			count.incrementAndGet();
		}
	}
	
	@Override
	public int requestRate() {
		return aggregator.getAggregate();
	}
	
	@Override
	public int requestRate(int key) {
		return aggregator.getPrevRatesAvg(key, numCompletedPeriods);
	}

	/**
	 *  invoked by the callback, flush the data to aggregator
	 */
	public void flush() {
		lock.lock();
			numCompletedPeriods++;
			ConcurrentMap<Integer, AtomicInteger> countAggregator = 
					new ConcurrentHashMap<>(this.currentPeriodCount);
			this.currentPeriodCount.clear();
			aggregator.flush(countAggregator, numCompletedPeriods);
			super.flush();
		lock.unlock();
	}

}
