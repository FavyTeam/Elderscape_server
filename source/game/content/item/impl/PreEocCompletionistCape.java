package game.content.item.impl;

import core.ServerConstants;
import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.entity.Entity;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

/**
 * Handles the Completionist cape
 * 
 * @author 2012
 *
 */
@ItemInteractionComponent(
		identities = {@GameTypeIdentity(type = GameType.PRE_EOC, identity = {20_769, 20_771}),})
public class PreEocCompletionistCape implements ItemInteraction {

	/**
	 * Checking whether cape is equipped
	 * 
	 * @param player the player
	 * @return is equipped
	 */
	public static boolean capeEquipped(Player player) {
		return player.playerEquipment[ServerConstants.CAPE_SLOT] == 20771
				|| player.playerEquipment[ServerConstants.CAPE_SLOT] == 20769;
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		/*
		 * Max cape req
		 */
		if (!MaxCape.eligible(player)) {
			return false;
		}
		/*
		 * Veteran cape req
		 */
		if (!VeteranCape.eligible(player)) {
			return false;
		}
		return true;
	}

	@Override
	public void operate(Player player, int id) {
		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {

			@Override
			public void execute(CycleEventContainer<Entity> container) {

				switch (container.getExecutions()) {
					case 0:
						player.startAnimation(356);
						break;
					case 4:
						player.npcId2 =
								player.playerEquipment[ServerConstants.CAPE_SLOT] == 20771 ? 1831 : 1830;
						player.getPA().requestUpdates();
						player.startAnimation(1174);
						player.gfx0(1443);
						break;
					case 16:
						player.npcId2 = -1;
						player.getPA().requestUpdates();
						player.startAnimation(1175);
						container.stop();
						break;

				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		return false;
	}

	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		return false;
	}

	@Override
	public boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	@Override
	public boolean dropItem(Player player, int id) {
		return false;
	}
}
