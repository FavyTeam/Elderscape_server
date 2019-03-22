package game.npc.impl.cerberus;

import core.ServerConstants;
import game.position.Position;
import game.content.combat.Combat;
import game.entity.Entity;
import game.npc.Npc;
import game.object.clip.Region;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-02-13 at 11:21 AM
 */
public class CerberusLavaCycleEvent extends CycleEvent<Entity> {

	private final Npc cerberus;

	private final List<Position> lavaPositions;

	public CerberusLavaCycleEvent(Npc cerberus, List<Position> lavaPositions) {
		this.cerberus = cerberus;
		this.lavaPositions = lavaPositions;
	}

	/**
	 * Code which should be ran when the event is executed
	 *
	 * @param container
	 */
	@Override
	public void execute(CycleEventContainer<Entity> container) {
		if (cerberus == null || cerberus.isDead() || cerberus.getCurrentHitPoints() <= 0) {
			container.stop();
			return;
		}

		Region region = cerberus.getRegionOrNull();

		if (region == null) {
			if (container.getExecutions() >= 20) {
				container.stop();
				return;
			}
			return;
		}

		if (container.getExecutions() >= 20) {
			container.stop();
			region.forEachPlayer(player -> lavaPositions.forEach(position ->
					                                                     player.getPA().stillGfx(1247, position.getX(), position.getY(), position.getZ(), 10)));
			return;
		}

		if (container.getExecutions() % 2 == 0) {
			return;
		}

		region.forEachPlayer(player -> {
			for (Position object : lavaPositions) {
				int distanceTo = object.distanceTo(player.getX(), player.getY());

				if (distanceTo <= 1) {
					Combat.createHitsplatOnPlayerNormal(player, distanceTo == 0 ? 15 : 7, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				}
			}
		});
	}

	/**
	 * Code which should be ran when the event stops
	 */
	@Override
	public void stop() {
		// do not remove, a part of this contract is that this will affect the top level reference in CerberusCombatStrategy - jason
		lavaPositions.clear();
	}
}
