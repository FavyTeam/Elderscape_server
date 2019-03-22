package utility;

public class SimpleTimer {

	private long cachedTime;

	public SimpleTimer() {
		reset();
	}

	public void reset() {
		cachedTime = System.currentTimeMillis();
	}

	public long elapsed() {
		return System.currentTimeMillis() - cachedTime;
	}
}
