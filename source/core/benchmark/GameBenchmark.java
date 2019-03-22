package core.benchmark;

import com.google.common.base.Stopwatch;
import utility.Misc;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jason MacKeigan on 2018-01-26 at 12:19 PM
 */
public class GameBenchmark {

	/**
	 * The identifying name of the benchmark, can be anything unique to separate it from others.
	 */
	private final String identifier;

	/**
	 * The task that is to be benchmarked
	 */
	private final Runnable task;

	/**
	 * The stop watch that determines the elapsed time.
	 */
	private final Stopwatch stopwatch = Stopwatch.createUnstarted();

	/**
	 * If this amount of time passes the warning print will be triggered.
	 */
	private final long warningThreshold;

	/**
	 * The unit of time to measure the warning threshold.
	 */
	private final TimeUnit warningThresholdTimeUnit;

    /**
     * Determines if the client should be warned or not.
     */
	private final boolean warn;

	/**
	 * A new benchmark that will execute the task and determine just how long it took.
	 *
	 * @param identifier the unique name of the benchmark
	 * @param task the task given to be bench marked
	 * @param warningThreshold the time that will pass before a warning is given.
	 * @param warningThresholdTimeUnit the unit of time for the warning threshold.
	 */
	public GameBenchmark(String identifier, Runnable task, long warningThreshold, TimeUnit warningThresholdTimeUnit) {
		this(identifier, task, warningThreshold, warningThresholdTimeUnit, true);
	}

    public GameBenchmark(String identifier, Runnable task, long warningThreshold, TimeUnit warningThresholdTimeUnit, boolean warn) {
        this.identifier = identifier;
        this.task = task;
        this.warningThreshold = warningThreshold;
        this.warningThresholdTimeUnit = warningThresholdTimeUnit;
        this.warn = warn;
    }

	public void execute() {
		if (!stopwatch.isRunning()) {
			stopwatch.start();
		} else {
			stopwatch.stop().reset().start();
		}
		task.run();
		if (warn && stopwatch.elapsed(warningThresholdTimeUnit) >= warningThreshold) {
			warn();
		}
	}

	public void warn() {
		if (!stopwatch.isRunning()) {
			throw new IllegalStateException("The stopwatch is not running.");
		}
		Misc.print(String.format("Benchmark[%s], threshold=%s %s, task time=%s", identifier, warningThreshold, warningThresholdTimeUnit.name().toLowerCase(),
		                         stopwatch.toString()));
	}

	public String getIdentifier() {
		return identifier;
	}

	public Runnable getTask() {
		return task;
	}

	public long getWarningThreshold() {
		return warningThreshold;
	}

	public TimeUnit getWarningThresholdTimeUnit() {
		return warningThresholdTimeUnit;
	}
}
