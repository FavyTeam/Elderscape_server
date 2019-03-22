import java.math.BigInteger;

/**
 * Created by Jason M
 * <p>
 * <p>
 * The purpose of this interface is to return a result that determines the average
 * time in nanoseconds or milliseconds that it takes to stress a piece of content.
 * </p>
 * <p>
 * It is worth noting that BigDecimal is used over long as each stress returns a long
 * and the sum of a group of longs may easily overflow the long.
 * </p>
 * <p>
 * There is some precision loss due to the rounding of BigInteger which is worth noting.
 * </p>
 * <p>
 * <p>
 * To implement this interface, first override the {@link #beforeStress()} function. The
 * purpose of this function is to execute any expensive operations as to not conflict
 * with stress times. After that, override the {@link #stress()} function. In this
 * function you should insert what you want to benchmark.
 * <p>
 * <pre>
 *     class SystemOutBenchmark implements Test {
 *
 *         public void beforeStress() {
 *             // we dont need to do anything
 *         };
 *
 *         public void stress() {
 *             System.out.println("Stress how long it takes to call System.out.println!");
 *         }
 *
 *         public static void main(String... args) {
 *             SystemOutBenchmark systemOutBenchmark = new SystemOutBenchmark();
 *
 *             int amountOfTests = 1; // the number of individual tests to find an average
 *
 *             int amountOfIterations = 1; // the number of times stress is called during a test.
 *
 *              BigInteger averageBenchmark = systemOutBenchmark.benchmark(amountOfTests, amountOfIterations);
 *
 *              System.out.printf("Average time it takes to print to the out stream in nano: %s", averageBenchmark);
 *          }
 *     }</pre>
 * </p>
 */
public interface BenchmarkTest {

	/**
	 * Prepares the test in some way before starting the benchmark, thus allowing
	 * the preparation to not affect the stress test or benchmark.
	 */
	void beforeStress();

	/**
	 * The function that will stress and perform our test. This function
	 * shouldn't represent a single test, not multiple. Use {@link #stress(int)}
	 * in cases where more than one iteration should be tested.
	 */
	void stress();

	/**
	 * The purpose is to stress a benchmark by referencing the {@link #stress()} function for every
	 * iteration.
	 *
	 * @param iterations the number of times we want to reference the stress function.
	 * @return the time in nanoseconds that it took to complete the stress test(s).
	 */
	default long stress(int iterations) {
		long start = System.nanoTime();

		while (iterations-- > 0) {
			stress();
		}

		return System.nanoTime() - start;
	}

	/**
	 * Creates a new benchmark where we determine the average computational time
	 * it takes to complete a tests X number of times in nanoseconds.
	 *
	 * @param tests the number of tests we must perform.
	 * @param iterationsPerTest the number of times we stress during a test.
	 * @return returns an average.
	 */
	default long benchmark(int tests, int iterationsPerTest) {
		BigInteger sum = BigInteger.valueOf(0L);

		int testsCompleted = 0;

		beforeStress();
		while (testsCompleted++ < tests) {
			sum = sum.add(new BigInteger(Long.toString(stress(iterationsPerTest))));
		}
		return sum.divide(new BigInteger(Integer.toString(tests))).longValue();
	}

}
