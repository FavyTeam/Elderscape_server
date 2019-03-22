package game.npc.impl.abyssal_sire;

import game.entity.Entity;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-02-26 at 1:26 PM
 */
public class AbyssalSirePhaseProgressionEvent extends CycleEvent<Entity> {

	@Override
	public void execute(CycleEventContainer<Entity> container) {
		container.stop();
	}

	@Override
	public void stop() {

	}
}
