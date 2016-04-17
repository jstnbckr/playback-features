import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.netflix.playback.features.model.impl.PeriodAggregator;

import junit.framework.Assert;

public class PeriodAggregatorTest {

	@Test
	public void testAggregateData() throws InterruptedException {
		PeriodAggregator aggregator = new PeriodAggregator();
		ConcurrentHashMap<Integer, AtomicInteger> testMap = new ConcurrentHashMap<>();
		testMap.put(1, new AtomicInteger(4));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 1);
		Thread.sleep(1000);
		Assert.assertEquals(7,aggregator.getAggregate());
		
		testMap.put(1, new AtomicInteger(10));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 2);
		Thread.sleep(1000);
		
		
	}
	
	@Test
	public void testAggregateDataForAKey() throws InterruptedException {
		PeriodAggregator aggregator = new PeriodAggregator();
		ConcurrentHashMap<Integer, AtomicInteger> testMap = new ConcurrentHashMap<>();
		testMap.put(1, new AtomicInteger(4));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 1);
		// 2nd period
		testMap = new ConcurrentHashMap<>();
		testMap.put(1, new AtomicInteger(10));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 2);
		Thread.sleep(1000);
		Assert.assertEquals(7,aggregator.getAggregateRates(1, 2));
		Assert.assertEquals(3,aggregator.getAggregateRates(2, 2));
		
	}
	
	@Test
	public void testLastTwoPeriodAggregateDataForAKey() throws InterruptedException {
		PeriodAggregator aggregator = new PeriodAggregator();
		ConcurrentHashMap<Integer, AtomicInteger> testMap = new ConcurrentHashMap<>();
		testMap.put(1, new AtomicInteger(4));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 1);
		// 2nd period
		testMap = new ConcurrentHashMap<>();
		testMap.put(1, new AtomicInteger(10));
		testMap.put(2, new AtomicInteger(3));
		aggregator.flush(testMap, 2);
		Thread.sleep(1000);
		Assert.assertEquals(7,aggregator.getPrevRatesAvg(1,2));
		Assert.assertEquals(3,aggregator.getPrevRatesAvg(2, 2));
		
	}
}


