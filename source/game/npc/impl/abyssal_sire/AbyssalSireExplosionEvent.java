package game.npc.impl.abyssal_sire;

import core.ServerConstants;
import game.content.combat.Combat;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jason MacKeigan on 2018-03-10 at 10:12 AM
 */
public class AbyssalSireExplosionEvent extends CycleEvent<Entity> {

	private final AbyssalSire sire;

	private final Player player;

	public AbyssalSireExplosionEvent(AbyssalSire sire, Player player) {
		this.sire = sire;
		this.player = player;
	}

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		if (player.dead || player.distanceToPoint(sire.getX(), sire.getY()) > 32) {
			container.stop();
			return;
		}
		container.stop();

		if (player.distanceToPoint(sire.getX(), sire.getY()) <= 3) {
			Combat.createHitsplatOnPlayerNormal(player, ThreadLocalRandom.current().nextInt(0, 73),
			                                    ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
		}
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {

	}

}
