package game.content.skilling.hunter.technique.impl.bird_snaring;

import core.Server;
import game.content.skilling.Skilling;
import game.content.skilling.hunter.HunterCreature;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.HunterTrapObjectManager;
import game.content.skilling.hunter.technique.HunterTechnique;
import game.content.skilling.hunter.trap.HunterTrap;
import game.content.skilling.hunter.trap.HunterTrapTechnique;
import game.content.skilling.hunter.trap.impl.BoxTrap;
import game.entity.Entity;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import java.util.stream.Stream;

/**
 * Created by Jason MacKeigan on 2018-05-01 at 5:03 PM
 */
public class BoxTrappingTechnique implements HunterTechnique {

	@Override
	public HunterTrap createTrap(Player hunter) {
		return new BoxTrap(hunter.getX(), hunter.getY(), hunter.getHeight(),
		                   0, 10, -1, 180, hunter.getPlayerName(), HunterStyle.BOX_TRAPPING);
	}

	@Override
	public boolean capture(Player hunter, HunterCreature creature, HunterTrapTechnique trap) {
		return trap.caught(hunter, creature);
	}

	@Override
	public boolean check(Player hunter, HunterTrap trap) {
		if (!trap.isTriggered()) {
			return false;
		}
		if (!hunter.getPlayerName().equalsIgnoreCase(trap.getOwner())) {
			return false;
		}
		HunterCreature trapped = trap.getTrapped();

		if (trapped == null) {
			return false;
		}
		hunter.doingActionEvent(3);
		hunter.getEventHandler().addEvent(hunter, new CycleEvent<Entity>() {
			int i = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				i++;
				switch (i) {
					case 1:
						hunter.startAnimation(5212);
						break;
					case 2:
						trap.setItemReturned(true);
						Stream.of(trapped.captureReward()).forEach(item -> ItemAssistant.addItemToInventoryOrDrop(hunter, item.getId(), item.getAmount()));
						Stream.of(trapped.captureReward()).forEach(item -> Skilling.addHarvestedResource(hunter, item.getId(), 1));
						ItemAssistant.addItemToInventoryOrDrop(hunter, getStyle().getEquipment().getItemId(), 1);
						HunterTrapObjectManager.getSingleton().remove(trap);
						Server.objectManager.removeObject(trap);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
		return true;
	}

	@Override
	public HunterStyle getStyle() {
		return HunterStyle.BOX_TRAPPING;
	}
}
