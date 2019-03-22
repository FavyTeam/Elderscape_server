package utility;

/**
 * A utility class that provides functions for measuring the elapsed time
 * between two different time periods.
 * <p>
 * <p>
 * This class is <b>not</b> intended for use across multiple threads.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Stopwatch {

	/**
	 * The internal cached time that acts as a time stamp.
	 */
	//private long cachedTime = Clock.TIME.get();


	/**
	 * The current toggle of this stopwatch.
	 */
	/*private State toggle = State.STOPPED;
	
	@Override
	public String toString() {
	boolean stopped = (toggle == State.STOPPED);
	return "STOPWATCH[elasped= " + (stopped ? 0 : elapsedTime()) + "]";
	}
	
	/**
	 * Gets the current time in {@link TimeUnit#MILLISECONDS}. This method is
	 * more accurate than {@link System#currentTimeMillis()} and does not rely
	 * on the underlying OS.
	 *
	 * @return the current time in milliseconds.
	 */
	/*public static long currentTime() {
	Clock.systemUTC();
	}
	
	/**
	 * Sets the internal cached time to {@link Utility#currentTime()},
	 * effectively making {@link Stopwatch#elapsedTime()} and
	 * {@link Stopwatch#elapsed(long, TimeUnit)} return {@code 0}. If this
	 * stopwatch is in a {@link State#STOPPED} toggle, invocation of this method
	 * will change it to a {@link State#RUNNING} toggle.
	 *
	 * @return an instance of this stopwatch.
	 */
	/*public Stopwatch reset() {
	//cachedTime = Stopwatch.currentTime();
	toggle = State.RUNNING;
	return this;
	}
	
	/**
	 * Sets the internal cached time to {@code 0} effectively putting this
	 * stopwatch in a {@link State#STOPPED} toggle.
	 *
	 * @return an instance of this stopwatch.
	 */
	/*public Stopwatch stop() {
	toggle = State.STOPPED;
	return this;
	}
	
	/**
	 * Retrieves the elapsed time in {@code unit}. If this stopwatch is stopped
	 * invocation of this method will throw an exception.
	 *
	 * @param unit
	 *            the time unit to retrieve the elapsed time in.
	 * @return the elapsed time.
	 * @throws IllegalStateException
	 *             if this stopwatch has been stopped.
	 */
	/*public long elapsedTime(TimeUnit unit) {
	if (toggle == State.STOPPED)
	    throw new IllegalStateException("The timer has been stopped!");
	return unit.convert((Stopwatch.currentTime() - cachedTime), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Retrieves the elapsed time in {@link TimeUnit#MILLISECONDS}. If this
	 * stopwatch is stopped invocation of this method will throw an exception.
	 *
	 * @return the elapsed time.
	 * @throws IllegalStateException
	 *             if this stopwatch has been stopped.
	 */
	/*public long elapsedTime() {
	return elapsedTime(TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Determines if the elapsed time is greater than {@code time} in
	 * {@code unit}. If this stopwatch is stopped invocation of this method will
	 * automatically return {@code true}.
	 *
	 * @param time
	 *            the time to check if greater than the elapsed time.
	 * @param unit
	 *            the time unit to has in.
	 * @return {@code true} if the elapsed time has passed or this stopwatch has
	 *         been stopped, {@code false} otherwise.
	 */
	/*public boolean elapsed(long time, TimeUnit unit) {
	if (toggle == State.STOPPED)
	    return true;
	return elapsedTime(unit) >= time;
	}
	
	/**
	 * Determines if the elapsed time is greater than {@code time} in
	 * {@link TimeUnit#MILLISECONDS}. If this stopwatch is stopped invocation of
	 * this method will automatically return {@code true}.
	 *
	 * @param time
	 *            the time to check if greater than the elapsed time.
	 * @return {@code true} if the elapsed time has passed or this stopwatch has
	 *         been stopped, {@code false} otherwise.
	 */
	/*public boolean elapsed(long time) {
	return elapsed(time, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Determines if this stopwatch is in a {@link State#STOPPED} toggle.
	 *
	 * @return {@code true} if this stopwatch is in a stopped toggle,
	 *         {@code false} otherwise.
	 */
	/*public boolean isStopped() {
	return toggle == State.STOPPED;
	}
	
	/**
	 * Executes {@code action} if the elapsed time is greater than {@code time}
	 * in {@code unit}. If this stopwatch is stopped invocation of this method
	 * will automatically execute {@code action}.
	 *
	 * @param time
	 *            the time to check if greater than the elapsed time.
	 * @param action
	 *            the action to execute if satisfied.
	 * @param unit
	 *            the time unit to check in.
	 */
	/*public void ifElapsed(long time, Consumer<? super Long> action, TimeUnit unit) {
	if (toggle == State.STOPPED) {
	    action.accept((long) 0);
	    return;
	}
	
	long elapsed = elapsedTime(unit);
	if (elapsed >= time) {
	    action.accept(elapsed);
	}
	}
	
	/**
	 * Executes {@code action} if the elapsed time is greater than {@code time}
	 * in {@link TimeUnit#MILLISECONDS}. If this stopwatch is stopped invocation
	 * of this method will automatically execute {@code action}.
	 *
	 * @param timePassed
	 *            the time to check if greater than the elapsed time.
	 * @param action
	 *            the action to execute if satisfied.
	 */
	/*public void ifElapsed(long timePassed, Consumer<? super Long> action) {
	ifElapsed(timePassed, action, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * The enumerated type representing all possible states of this stopwatch.
	 *
	 * @author lare96 <http://github.com/lare96>
	 */
	@SuppressWarnings("unused")
	private enum State {
		RUNNING,
		STOPPED
	}


	/**
	 * A utility class that provides functions for measuring the elapsed time
	 * between two different time periods. The only difference between this
	 * class and {@link Stopwatch} is that the fields in this class are wrapped
	 * in atomic based classes to ensure thread safety.
	 * <p>
	 * <p>
	 * This class is atomic and is intended for use across multiple threads.
	 *
	 * @author lare96 <http://github.com/lare96>
	 */
	public static final class AtomicStopwatch {

		/**
		 * The internal cached time that acts as a time stamp.
		 */
		/*private final AtomicLong cachedTime = new AtomicLong(Stopwatch.currentTime());
		
		/**
		 * The current toggle of this stopwatch.
		 */
		/*private final AtomicReference<State> toggle = new AtomicReference<>(State.STOPPED);
		
		@Override
		public String toString() {
		    boolean stopped = (toggle.get() == State.STOPPED);
		    return "STOPWATCH[elasped= " + (stopped ? 0 : elapsedTime()) + "]";
		}
		
		/**
		 * Gets the current time in {@link TimeUnit#MILLISECONDS}. This method
		 * is more accurate than {@link System#currentTimeMillis()} and does not
		 * rely on the underlying OS.
		 *
		 * @return the current time in milliseconds.
		 */
		/*public static long currentTime() {
		    return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
		}
		
		/**
		 * Sets the internal cached time to {@link Clock#systemUTC()} ()},
		 * effectively making {@link Stopwatch#elapsedTime()} and
		 * {@link Stopwatch#elapsed(long, TimeUnit)} return {@code 0}. If this
		 * stopwatch is in a {@link State#STOPPED} toggle, invocation of this
		 * method will change it to a {@link State#RUNNING} toggle.
		 *
		 * @return an instance of this stopwatch.
		 */
		/*public AtomicStopwatch reset() {
		    cachedTime.set(Stopwatch.currentTime());
		    toggle.set(State.RUNNING);
		    return this;
		}
		
		/**
		 * Sets the internal cached time to {@code 0} effectively putting this
		 * stopwatch in a {@link State#STOPPED} toggle.
		 *
		 * @return an instance of this stopwatch.
		 */
		/*public AtomicStopwatch stop() {
		    toggle.set(State.STOPPED);
		    return this;
		}
		
		/**
		 * Retrieves the elapsed time in {@code unit}. If this stopwatch is
		 * stopped invocation of this method will throw an exception.
		 *
		 * @param unit
		 *            the time unit to retrieve the elapsed time in.
		 * @return the elapsed time.
		 * @throws IllegalStateException
		 *             if this stopwatch has been stopped.
		 */
		/*public long elapsedTime(TimeUnit unit) {
		    if (toggle.get() == State.STOPPED)
			throw new IllegalStateException("The timer has been stopped!");
		    return unit.convert((Stopwatch.currentTime() - cachedTime.get()), TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Retrieves the elapsed time in {@link TimeUnit#MILLISECONDS}. If this
		 * stopwatch is stopped invocation of this method will throw an
		 * exception.
		 *
		 * @return the elapsed time.
		 * @throws IllegalStateException
		 *             if this stopwatch has been stopped.
		 */
		/*public long elapsedTime() {
		    return elapsedTime(TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Determines if the elapsed time is greater than {@code time} in
		 * {@code unit}. If this stopwatch is stopped invocation of this method
		 * will automatically return {@code true}.
		 *
		 * @param time
		 *            the time to check if greater than the elapsed time.
		 * @param unit
		 *            the time unit to has in.
		 * @return {@code true} if the elapsed time has passed or this stopwatch
		 *         has been stopped, {@code false} otherwise.
		 */
		/*public boolean elapsed(long time, TimeUnit unit) {
		    if (toggle.get() == State.STOPPED)
			return true;
		    return elapsedTime(unit) >= time;
		}
		
		/**
		 * Determines if the elapsed time is greater than {@code time} in
		 * {@link TimeUnit#MILLISECONDS}. If this stopwatch is stopped
		 * invocation of this method will automatically return {@code true}.
		 *
		 * @param time
		 *            the time to check if greater than the elapsed time.
		 * @return {@code true} if the elapsed time has passed or this stopwatch
		 *         has been stopped, {@code false} otherwise.
		 */
		/*public boolean elapsed(long time) {
		    return elapsed(time, TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Determines if this stopwatch is in a {@link State#STOPPED} toggle.
		 *
		 * @return {@code true} if this stopwatch is in a stopped toggle,
		 *         {@code false} otherwise.
		 */
		/*public boolean isStopped() {
		    return toggle.get() == State.STOPPED;
		}
		
		/**
		 * Executes {@code action} if the elapsed time is greater than
		 * {@code time} in {@code unit}. If this stopwatch is stopped invocation
		 * of this method will automatically execute {@code action}.
		 *
		 * @param time
		 *            the time to check if greater than the elapsed time.
		 * @param action
		 *            the action to execute if satisfied.
		 * @param unit
		 *            the time unit to check in.
		 */
		/*public void ifElapsed(long time, Consumer<? super Long> action, TimeUnit unit) {
		    if (toggle.get() == State.STOPPED) {
			action.accept((long) 0);
			return;
		    }
		
		    long elapsed = elapsedTime(unit);
		    if (elapsed >= time) {
			action.accept(elapsed);
		    }
		}
		
		/**
		 * Executes {@code action} if the elapsed time is greater than
		 * {@code time} in {@link TimeUnit#MILLISECONDS}. If this stopwatch is
		 * stopped invocation of this method will automatically execute
		 * {@code action}.
		 *
		 * @param timePassed
		 *            the time to check if greater than the elapsed time.
		 * @param action
		 *            the action to execute if satisfied.
		 */
		/*public void ifElapsed(long timePassed, Consumer<? super Long> action) {
		    ifElapsed(timePassed, action, TimeUnit.MILLISECONDS);
		}*/
	}

}
