import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.netflix.playback.features.model.DiagnosticService;
import com.netflix.playback.features.model.PeriodCompletedCallback;
import com.netflix.playback.features.model.PlaybackDiagnostics;
import com.netflix.playback.features.model.PlaybackRequest;
import com.netflix.playback.features.model.Timer;
import com.netflix.playback.features.model.impl.AggregatorPeriodCompletedCallback;
import com.netflix.playback.features.model.impl.DiagnosticServiceImpl;
import com.netflix.playback.features.model.impl.PlaybackDiagnosticsImpl;
import com.netflix.playback.features.model.impl.TimerImpl;

import junit.framework.Assert;

public class PlayBackFeaturesTest {

	/**
	 * this is more like an integration test 
	 */
	@Test
	public void testPlayBackFeatures() throws InterruptedException {
		Timer timer = new TimerImpl(TimeUnit.SECONDS, 2);
		DiagnosticService service= new DiagnosticServiceImpl(timer, 0);
		PeriodCompletedCallback callback = new AggregatorPeriodCompletedCallback(service);
		timer.addCallback(callback);
		PlaybackDiagnostics pd = new PlaybackDiagnosticsImpl(service);
		// 1 = 2 , 2 = 5
		pd.log(new PlaybackRequest("1", 1, "US"));
		pd.log(new PlaybackRequest("1", 1, "US"));
		for (int i=0; i < 5; i++) {
			pd.log(new PlaybackRequest("1", 2, "US"));
		}
		Thread.sleep(5000);
		Assert.assertEquals(7, pd.requestRate());
		// 1 = 4, 2 = 3, 3 = 3
		for (int i=0; i < 3; i++) {
			pd.log(new PlaybackRequest("1", 2, "US"));
		}
		for (int i=0; i < 4; i++) {
			pd.log(new PlaybackRequest("1", 1, "US"));
		}
		for (int i=0; i < 3; i++) {
			pd.log(new PlaybackRequest("1", 3, "US"));
		}
		Thread.sleep(4000);
		// 1 = 3 , 2 = 2
		for (int i=0; i < 2; i++) {
			pd.log(new PlaybackRequest("1", 2, "US"));
		}
		for (int i=0; i < 3; i++) {
			pd.log(new PlaybackRequest("1", 1, "US"));
		}
		Thread.sleep(3000);
		
		Assert.assertEquals(3,pd.avgRate(1));
		Assert.assertEquals(3,pd.avgRate(2));
		Assert.assertEquals(3,pd.requestRate(1));
		Assert.assertEquals(2, pd.requestRate(2));
		Assert.assertEquals(1, pd.requestRate(3));
		Assert.assertEquals(7, pd.requestRate());
	}
}
