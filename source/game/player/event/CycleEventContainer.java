package game.player.event;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The wrapper for our event
 *
 * @author Stuart <RogueX>
 */

public class CycleEventContainer<T> {

	/**
	 * Event owner
	 */
	private T owner;

	/**
	 * Is the event running or not
	 */
	private boolean isRunning;

	/**
	 * The amount of cycles per event execution
	 */
	private int tick;

	/**
	 * The actual event
	 */
	private CycleEvent event;

	/**
	 * The current amount of cycles passed
	 */
	private int cyclesPassed;

	/**
	 * The number of executions made.
	 */
	private int executions;

	/**
	 * A collection of listeners that are called when the cycle event is to be stopped.
	 */
	private final Collection<CycleEventStopListener> stopListeners;

	/**
	 * Sets the event containers details
	 *
	 * @param owner , the owner of the event
	 * @param event , the actual event to run
	 * @param tick , the cycles between execution of the event
	 */
	public CycleEventContainer(T owner, CycleEvent event, int tick) {
		this.owner = owner;
		this.event = event;
		this.isRunning = true;
		this.cyclesPassed = 0;
		this.tick = tick;
		this.stopListeners = new ArrayList<>();
	}

	/**
	 * Execute the contents of the event
	 */
	public void execute() {
		try {
			event.execute(this);

			executions++;
		} catch (Exception e) {
			// Wrap in try statement to avoid server crash when dealing with content created errors.
			e.printStackTrace();
		}
	}

	/**
	 * Adds a listener to the underlying collection of listeners that will be referenced
	 * if or once this event stops.
	 *
	 * @param listener the listener to be added and referenced on stop.
	 */
	public void addStopListener(CycleEventStopListener listener) {
		stopListeners.add(listener);
	}

	/**
	 * Stop the event from running
	 */
	public void stop() {
		isRunning = false;
		event.stop();
		stopListeners.forEach(CycleEventStopListener::onStop);
	}

	/**
	 * Does the event need to be ran?
	 *
	 * @return true yes false no
	 */
	public boolean needsExecution() {
		if (++this.cyclesPassed >= tick) {
			this.cyclesPassed = 0;
			return true;
		}
		return false;
	}

	/**
	 * Returns the owner of the event
	 *
	 * @return
	 */
	public T getOwner() {
		return owner;
	}

	/**
	 * Is the event running?
	 *
	 * @return true yes false no
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Set the amount of cycles between the execution
	 *
	 * @param tick
	 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	/**
	 * The number of game ticks that have passed since the creation of the event.
	 */
	public int getTotalTicks() {
		return tick;
	}

	/**
	 * The total number of times that the {@link #execute()} has been called.
	 *
	 * @return the total number of executions.
	 */
	public int getExecutions() {
		return executions;
	}

	/**
	 * The event for the container.
	 *
	 * @return the event.
	 */
	public CycleEvent getEvent() {
		return event;
	}
}
