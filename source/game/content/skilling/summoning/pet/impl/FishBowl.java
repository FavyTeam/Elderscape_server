package game.content.skilling.summoning.pet.impl;

import core.GameType;
import game.content.item.ItemInteraction;
import game.content.item.ItemInteractionComponent;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import game.type.GameTypeIdentity;
import utility.Misc;

@ItemInteractionComponent(identities = {@GameTypeIdentity(type = GameType.PRE_EOC,
		identity = {401, 6667, 6668, 6669, 6670, 6671, 6672}),})
/**
 * Handles the fish bowl pet
 * 
 * @author 2012
 *
 */
public class FishBowl implements ItemInteraction {

	/**
	 * The chance of getting the pet
	 */
	private static final int PET_CHANCE = 200;

	/**
	 * Rewards the pet
	 * 
	 * @param player the player
	 */
	public static void reward(Player player) {
		/*
		 * No bowl
		 */
		if (!ItemAssistant.hasItemAmountInInventory(player, 6669, 1)) {
			return;
		}
		/*
		 * Missed chance
		 */
		if (Misc.random(PET_CHANCE) != 1) {
			return;
		}
		/*
		 * Reward
		 */
		ItemAssistant.deleteItemFromInventory(player, 6669, 1);
		ItemAssistant.addItem(player, 6670 + Misc.random(2), 1);
		player.getPA().sendMessage("It feels as if.. Something jumped into your fishbowl.");
	}

	@Override
	public boolean canEquip(Player player, int id, int slot) {
		if (id >= 6670 && id <= 6672) {
			player.startAnimation(Misc.random(2) == 1 ? 2780 : 2783);
			return true;
		}
		return false;
	}

	@Override
	public void operate(Player player, int id) {

	}

	@Override
	public boolean sendItemAction(Player player, int id, int type) {
		if (type == 1) {
			switch (id) {
				case 6670:
				case 6671:
				case 6672:
					player.startAnimation(Misc.random(2) == 1 ? 2782 : 2788);
					return true;
			}
		} else if (type == 2) {
			switch (id) {
				case 6669:
					ItemAssistant.deleteItemFromInventory(player, 6669, 1);
					ItemAssistant.addItem(player, 6668, 1);
					ItemAssistant.addItem(player, 401, 1);
					return true;

				case 6670:
				case 6671:
				case 6672:
					player.startAnimation(Misc.random(2) == 1 ? 2781 : 2784);
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	@Override
	public boolean useItemOnObject(Player player, int id, int object) {
		if (id == 6667) {
			if (object == 874) {
				ItemAssistant.deleteItemFromInventory(player, 6667, 1);
				ItemAssistant.addItem(player, 6668, 1);
				player.getPA()
						.sendMessage("You feel the fishbowl with water. It is now ready for seaweed.");
			}
			return true;
		}
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
