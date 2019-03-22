package game.player.event;

import game.log.GameTickLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Handles all of our cycle based events
 *
 * @author Stuart <RogueX>
 * @author Jason.
 * Ive generified the class so that implementations of this class can better operate on the underlying type of the CycleEventContainer.
 * For the sake of backwards compatibility, the default singleton will be of type {@link Object}.
 */
public class CycleEventHandler<T> {

	/**
	 * The instance of this class
	 */
	private static CycleEventHandler<Object> instance;

	/**
	 * Returns the instance of this class
	 *
	 * @return
	 */
	public static CycleEventHandler<Object> getSingleton() {
		if (instance == null) {
			instance = new CycleEventHandler<>();
		}
		return instance;
	}

	/**
	 * A predicate that determines if the owner is available. If null, it is assumed to be true.
	 */
	protected final Predicate<T> ownerAvailable;

	/**
	 * Holds all of our events currently being ran
	 */
	protected List<CycleEventContainer<T>> events;

	/**
	 * Creates a new instance of this class
	 */
	public CycleEventHandler() {
		this(t -> true);
	}

	/**
	 * A new cycle event handler with a predicate that determines if
	 * an owner of an event is available to the event.
	 *
	 * @param ownerAvailable a predicate that determines if the owner of an event is available.
	 */
	public CycleEventHandler(Predicate<T> ownerAvailable) {
		this.events = new ArrayList<>();
		this.ownerAvailable = Objects.requireNonNull(ownerAvailable);
	}

	public CycleEventContainer<T> singularExecution(T owner, Runnable runnable, int cycles) {
		return addEvent(owner, new CycleEventAdapter<T>() {
			@Override
			public void execute(CycleEventContainer<T> container) {
				container.stop();
				runnable.run();
			}
		}, cycles);
	}

	/**
	 * Add an event to the list for the given owner.
	 *
	 * @param owner the new event for the given owner.
	 * @param event the event being added.
	 * @param cycles the number of cycles that pass between each execution.
	 */
	public CycleEventContainer<T> addEvent(T owner, CycleEvent<T> event, int cycles) {
		CycleEventContainer<T> container = new CycleEventContainer<>(owner, event, cycles);

		this.events.add(container);

		return container;
	}

	/**
	 * Execute and remove events
	 */
	public void cycle() {
		GameTickLog.cycleEventTickDuration = System.currentTimeMillis();

		List<CycleEventContainer<T>> eventsCopy = new ArrayList<>(events);

		List<CycleEventContainer<T>> remove = new ArrayList<>();

		for (CycleEventContainer<T> c : eventsCopy) {
			if (c != null) {
				if (!ownerAvailable.test(c.getOwner())) {
					remove.add(c);
					continue;
				}
				if (c.needsExecution() && c.isRunning()) {
					c.execute();
					if (!c.isRunning()) {
						remove.add(c);
					}
				}
			}
		}
		for (CycleEventContainer c : remove) {
			events.remove(c);
		}
		GameTickLog.cycleEventTickDuration = System.currentTimeMillis() - GameTickLog.cycleEventTickDuration;
	}

	/**
	 * Retrieves the first container from the list of containers where the underlying
	 * {@link CycleEvent} <b>equals</b> the given parameter.
	 *
	 * @param event the event the container must match to return.
	 * @return the event for the given container.
	 */
	public CycleEventContainer<T> getContainer(CycleEvent<T> event) {
		return events.stream().filter(e -> e.getEvent().equals(event)).findAny().orElse(null);
	}

	/**
	 * Returns the amount of events currently running
	 *
	 * @return amount
	 */
	public int getEventsCount() {
		return events.size();
	}

	/**
	 * Stops all events in the handler and clears the list.
	 */
	public void stopAll() {
		events.forEach(CycleEventContainer::stop);

		events.clear();
	}

	/**
	 * Stop all events for the specific owner.
	 *
	 * @param owner the owner of the event(s).
	 */
	public void stopEvents(Object owner) {
		List<CycleEventContainer> eventsCopy = new ArrayList<>(events);
		for (CycleEventContainer c : eventsCopy) {
			if (c.getOwner() == owner) {
				c.stop();
				events.remove(c);
			}
		}
	}

	public void stopIfEventEquals(CycleEvent<?> event) {
		List<CycleEventContainer> eventsCopy = new ArrayList<>(events);

		for (CycleEventContainer c : eventsCopy) {
			if (c.isRunning() && c.getEvent().equals(event)) {
				c.stop();
				events.remove(c);
			}
		}
	}

}
