package game.player.event;

/**
 * What the event must implement
 *
 * @author Stuart <RogueX>
 */
public abstract class CycleEvent<T> {

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	public abstract void execute(CycleEventContainer<T> container);

	/**
	 * Code which should be ran when the event stops
	 */
	public abstract void stop();

}
