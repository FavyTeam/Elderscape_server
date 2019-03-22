package game.content.degrading;

import java.util.HashMap;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsplayer.magic.AutoCast;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;

/**
 * Handles degrading items
 * 
 * @author 2012
 *
 */
public class DegradingManager {

	/**
	 * The armour slots
	 */
	private static final int[] BODY_SLOTS = {ServerConstants.WEAPON_SLOT, ServerConstants.HEAD_SLOT,
			ServerConstants.LEG_SLOT, ServerConstants.BODY_SLOT, ServerConstants.RING_SLOT};

	/**
	 * The degrading items
	 */
	private HashMap<Integer, DegradingItem> degradingItems = new HashMap<Integer, DegradingItem>();

	/**
	 * Degrading weapon from attack
	 * 
	 * @param player the player
	 * @param attack on attack
	 */
	public static void degrade(Player player, boolean attack) {
		/*
		 * Loop through the slots
		 */
		for (int slot = 0; slot < BODY_SLOTS.length; slot++) {
			/*
			 * Only attacking or blocking
			 */
			if ((attack && BODY_SLOTS[slot] != ServerConstants.WEAPON_SLOT)
					|| (!attack && BODY_SLOTS[slot] == ServerConstants.WEAPON_SLOT)) {
				continue;
			}
			/*
			 * The weapon
			 */
			int id = player.playerEquipment[BODY_SLOTS[slot]];
			/*
			 * No weapon
			 */
			if (id < 1) {
				continue;
			}
			/*
			 * The name
			 */
			String name = ItemDefinition.DEFINITIONS[player.playerEquipment[BODY_SLOTS[slot]]].name;
			/*
			 * The definition
			 */
			DegradingItem item = DegradingItemJSONLoader.getItem(id);
			/*
			 * The item
			 */
			if (item == null) {
				continue;
			}
			/*
			 * Check existence
			 */
			if (player.playerEquipment[BODY_SLOTS[slot]] != item.getId()
					&& player.playerEquipment[BODY_SLOTS[slot]] != item.getNextItem()) {
				continue;
			}
			/*
			 * Degrades on instant combat
			 */
			if (item.isDegradeOnCombat()
					&& player.playerEquipment[BODY_SLOTS[slot]] != item.getNextItem()) {
				ItemAssistant.setEquipment(player, item.getNextItem(), 1, BODY_SLOTS[slot]);
				ItemAssistant.updateSlot(player, BODY_SLOTS[slot]);
				player.getPA().sendMessage("Your " + name + " has degraded!");
			}
			/*
			 * Already contains
			 */
			if (player.getDegrading().getDegradable().containsKey(id)) {
				/*
				 * The degrading item
				 */
				DegradingItem degrading = player.getDegrading().getDegradable().get(id);
				/*
				 * Degrading hits
				 */
				degrading.decreaseHits();
				/*
				 * Notify
				 */
				if (degrading.getHitsRemaining() == 100) {
					player.getPA()
							.sendMessage(degrading.getDefinition().name + ": has about 100 charges left!");
				} else if (degrading.getHitsRemaining() < 50) {
					if (degrading.getHitsRemaining() % 10 == 0) {
						player.getPA().sendMessage(degrading.getDefinition().name + ": has about "
								+ degrading.getHitsRemaining() + " charges left!");
					}
				}
				/*
				 * Finished
				 */
				if (degrading.getHitsRemaining() == 0) {
					/*
					 * Fully degraded
					 */
					if (item.getNextItem() == -1) {
						/*
						 * Reset weapon
						 */
						if (BODY_SLOTS[slot] == ServerConstants.WEAPON_SLOT) {
							/*
							 * Reset auto cast
							 */
							if (player.getAutoCasting() && player.usingOldAutocast) {
								AutoCast.resetAutocast(player, true);
							}
							/*
							 * Reset animation
							 */
							Combat.updatePlayerStance(player);
							Combat.resetPlayerAttack(player);
							/*
							 * Reset special attack
							 */
							player.setUsingSpecialAttack(false);
						}
						player.getPA().sendMessage("Your " + name + " has fully degraded.");
					} else {
						player.getPA().sendMessage("Your " + name + " has degraded.");
					}
					/*
					 * Degrades item
					 */
					ItemAssistant.setEquipment(player, item.getNextItem(),
							item.getNextItem() == -1 ? 0 : 1, BODY_SLOTS[slot]);
					ItemAssistant.updateSlot(player, BODY_SLOTS[slot]);
				}
				continue;
			}
			/*
			 * Adds to list
			 */
			player.getDegrading().getDegradable()
					.put(item.isDegradeOnCombat() ? item.getNextItem() : id, item);
		}
	}

	/**
	 * Checks the charges on the weapon
	 * 
	 * @param player the player
	 * @param id the id
	 */
	public static boolean checkCharge(Player player, int id) {
		/*
		 * The definition
		 */
		DegradingItem item = DegradingItemJSONLoader.getItem(id);
		/*
		 * The item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Already contains
		 */
		if (player.getDegrading().getDegradable().containsKey(id)) {
			/*
			 * The degrading item
			 */
			DegradingItem degrading = player.getDegrading().getDegradable().get(id);
			/*
			 * The charges left
			 */
			player.getPA().sendMessage(degrading.getDefinition().name + " has "
					+ degrading.getHitsRemaining() + " charges left.");
		} else {
			player.getPA().sendMessage(ItemDefinition.DEFINITIONS[id].name
					+ " has not been affected and provides " + item.getHitsRemaining() + " charges.");
		}
		return true;
	}

	/**
	 * Gets the degradable
	 *
	 * @return the degradable
	 */
	public HashMap<Integer, DegradingItem> getDegradable() {
		return degradingItems;
	}
}
