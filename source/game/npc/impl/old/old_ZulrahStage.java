package game.npc.impl.old;

import game.player.Player;
import game.player.event.CycleEvent;

/**
 * Created by Owain on 18/08/2017.
 */
public abstract class old_ZulrahStage extends CycleEvent {

	protected old_Zulrah oldZulrah;

	protected Player player;

	public old_ZulrahStage(old_Zulrah oldZulrah, Player player) {
		this.oldZulrah = oldZulrah;
		this.player = player;
	}

}
