import core.Server;
import game.object.clip.Region;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-02-16 at 5:08 PM
 */
public class RegionReferenceBenchmark implements BenchmarkTest {

	public static void main(String[] args) {
		try {
			Server.main(new String[] {});

			while (!Region.hasLoadingCompleted()) {
				try {
					Thread.sleep(1_000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long elapsed = new RegionReferenceBenchmark().benchmark(10000, 1000000);

		System.out.println(String.format("The benchmark took %s milliseconds.", TimeUnit.NANOSECONDS.toMillis(elapsed)));
	}

	/**
	 * Prepares the test in some way before starting the benchmark, thus allowing
	 * the preparation to not affect the stress test or benchmark.
	 */
	@Override
	public void beforeStress() {

	}

	/**
	 * The function that will stress and perform our test. This function
	 * shouldn't represent a single test, not multiple. Use {@link #stress(int)}
	 * in cases where more than one iteration should be tested.
	 */
	@Override
	public void stress() {
		Region region = Region.getRegion(3200, 3200);
	}
}
