package game.npc.impl.abyssal_sire;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-03-10 at 8:32 AM
 */
public class AbyssalSirePoisonEvent extends CycleEvent<Entity> {

	private final Position poison;

	private final Player player;

	public AbyssalSirePoisonEvent(Position poison, Player player) {
		this.poison = poison;
		this.player = player;
	}

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		if (player.dead || player.isDisconnected()) {
			container.stop();
			return;
		}

		if (container.getExecutions() == 0) {
			return;
		}

		if (poison.matches(player.getX(), player.getY(), player.getHeight())) {
			Combat.createHitsplatOnPlayerNormal(player, 5, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
		}

		if (container.getExecutions() >= 4) {
			container.stop();
		}
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {

	}
}
