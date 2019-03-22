package game.player.event;

/**
 * Created by Jason MacKeigan on 2018-06-14 at 1:09 PM
 */
public abstract class CycleEventAdapter<T> extends CycleEvent<T> {

	@Override
	public abstract void execute(CycleEventContainer<T> container);

	@Override
	public void stop() {

	}
}
