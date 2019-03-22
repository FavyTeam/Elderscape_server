package game.player.event;

/**
 * Created by Jason MacKeigan on 2018-02-14 at 9:27 AM
 * <p>
 * All {@link CycleEvent}s that implement this interface will have the {@link #onStop()} method referenced
 * when the event is stopped.
 */
public interface CycleEventStopListener {

	/**
	 * Referenced when the parent {@link CycleEvent} stop executing.
	 */
	void onStop();
}
