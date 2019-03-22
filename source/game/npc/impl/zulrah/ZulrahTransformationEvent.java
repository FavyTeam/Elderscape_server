package game.npc.impl.zulrah;

import game.entity.Entity;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Created by Jason MacKeigan on 2018-04-11 at 2:38 PM
 */
public class ZulrahTransformationEvent extends CycleEvent<Entity> {

	private final ZulrahLocation destination;

	private final ZulrahTransformation transformation;

	private final Player defender;

	public ZulrahTransformationEvent(ZulrahLocation destination, ZulrahTransformation transformation, Player defender) {
		this.destination = destination;
		this.transformation = transformation;
		this.defender = defender;
	}

	@Override
	public void execute(CycleEventContainer<Entity> container) {
		Entity owner = container.getOwner();

		if (owner == null) {
			container.stop();
			return;
		}
		Npc zulrah = (Zulrah) owner;

		if (zulrah.isDead()) {
			container.stop();
			return;
		}

		if (container.getExecutions() == 1) {
			zulrah.requestAnimation(5072);
		} else if (container.getExecutions() == 2) {
			zulrah.move(destination.getLocation().translateHeight(zulrah.getHeight()));
		} else if (container.getExecutions() == 3) {
			zulrah.setVisible(false);
		} else if (container.getExecutions() == 4) {
			zulrah.transform(transformation.getNpcId());
		} else if (container.getExecutions() == 5) {
			container.stop();
			zulrah.setVisible(true);
			zulrah.turnNpc(2268, 3070);
			zulrah.requestAnimation(5073);
			zulrah.setKillerId(defender.getPlayerId());
		}
	}

	@Override
	public void stop() {

	}
}
