package game.player.event.impl;

import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-01-23 at 1:38 PM
 */
public class TimedCameraShakeReset extends CycleEvent<Entity> {

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		container.stop();

		if (container.getOwner() instanceof Player) {
			Player player = (Player) container.getOwner();

			player.getPA().resetCameraShake();
		}
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {

	}
}
