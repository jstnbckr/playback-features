package com.netflix.playback.features.model.impl;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.EvictingQueue;

/**
 * Based on various use cases, the aggregator return the data. It stores the events count for each period.
 * 
 */
public class PeriodAggregator {

	private final ConcurrentMap<Integer, ConcurrentMap<Integer, Integer>> periodToEventCount 
				= new ConcurrentHashMap<>();
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	// keep track of stats
	private EvictingQueue<Integer> totalStatsQueue = EvictingQueue.create(2);
	
	// do a callback to aggregate data asynchronously
	public void flush(ConcurrentMap<Integer, AtomicInteger> countAggregator, int currPeriod) {
		FutureTask task = new FutureTask<>(new StatsCallable(countAggregator, currPeriod));
		executor.execute(task);
	}

	/**
	 * return avg of last two periods
	 * @param key
	 * @return
	 */
	public int getPrevRatesAvg(int key, int totalPeriod) {
		int sum = 0;
		if (periodToEventCount.get(totalPeriod).containsKey(key)) {
			sum += periodToEventCount.get(totalPeriod).get(key);
		}
		if (periodToEventCount.get(totalPeriod - 1).containsKey(key)) {
			sum += periodToEventCount.get(totalPeriod - 1).get(key);
		}
		return sum / 2;
	}

	public Optional<Integer> getRateForPeriod(int key, int period) {
		return Optional.ofNullable(periodToEventCount.get(period).get(key));
	}

	/**
	 * return avg of all periods for a key
	 */
	public int getAggregateRates(int key, int totalPeriods) {
		int sum = 0;
		for (Entry<Integer, ConcurrentMap<Integer, Integer>> entry : periodToEventCount.entrySet()) {
			if (entry.getValue().containsKey(key)) {
				sum += entry.getValue().get(key);
			}
		}
		return sum / totalPeriods;
	}

	/**
	 * return avg of last two periods
	 * 
	 * @return
	 */
	public int getAggregate() {
		if (totalStatsQueue.isEmpty()) {
			return 0;
		}
		synchronized (totalStatsQueue) {
			return (int) totalStatsQueue.stream().mapToInt(i -> i).average().getAsDouble();
		}
	}

	class StatsCallable implements Callable {

		private ConcurrentMap<Integer, AtomicInteger> countAggregator;
		private int currPeriod;

		public StatsCallable(ConcurrentMap<Integer, AtomicInteger> countAggregator, int currPeriod) {
			this.countAggregator = countAggregator;
			this.currPeriod = currPeriod;
		}

		/**
		 * merged the last period count to the all time count
		 * 
		 */
		@Override
		public Object call() throws Exception {
			int totalCount = 0;
			periodToEventCount.put(currPeriod, new ConcurrentHashMap<>());
			for (Entry<Integer, AtomicInteger> entry : countAggregator.entrySet()) {
				totalCount += entry.getValue().get();
				periodToEventCount.get(currPeriod).put(entry.getKey(), entry.getValue().get());
			}
			synchronized (totalStatsQueue) {
				totalStatsQueue.add(totalCount);
			}
			return null;
		}

	}

}
