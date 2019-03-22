import core.Server;
import game.npc.NpcHandler;
import game.object.clip.Region;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-02-15 at 11:49 AM
 */
public class SurroundingRegionBenchmark {

	public static void main(String[] args) {
		try {
			Server.main(args);

			SurroundingRegionsBenchmark surroundingRegionsBenchmark = new SurroundingRegionsBenchmark();

			LinearSearchBenchmark linearSearchBenchmark = new LinearSearchBenchmark();

			long resultOfSurrounding = surroundingRegionsBenchmark.benchmark(10, 10000);

			long resultOfLinear = linearSearchBenchmark.benchmark(10, 10000);

			System.out.println(String.format("Surrounding = %s ms\nLinear = %s ms", TimeUnit.NANOSECONDS.toMillis(resultOfSurrounding),
			                                 TimeUnit.NANOSECONDS.toMillis(resultOfLinear)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static final class SurroundingRegionsBenchmark implements BenchmarkTest {

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
			List<Region> regions = Region.getSurrounding(Region.getRegion(3200, 3200));

			for (Region region : regions) {
				region.forEachNpc(npc -> {
				});
			}
		}
	}


	private static final class LinearSearchBenchmark implements BenchmarkTest {

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
			for (int index = 0; index < NpcHandler.npcs.length; index++) {

			}
		}
	}

}
