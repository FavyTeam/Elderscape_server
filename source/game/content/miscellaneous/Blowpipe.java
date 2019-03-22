package game.content.miscellaneous;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.combat.vsplayer.range.RangedData;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Toxic blowpipe features.
 *
 * @author MGT Madness, created on 25-02-2017.
 */
public class Blowpipe {

	/**
	 * The maximum amount of charges (scales) a blowpipe can hold.
	 */

	public static int MAX_CHARGES = 16383;

	public static void unload(Player player) {
		if (player.blowpipeDartItemId == 0) {
			player.getPA().sendMessage("Your Toxic blowpipe is empty.");
			return;
		}
		if (!ItemAssistant.addItem(player, player.blowpipeDartItemId, player.blowpipeDartItemAmount)) {
			return;
		}
		player.getPA().sendMessage("You unload " + Misc.formatNumber(player.blowpipeDartItemAmount) + " " + ItemAssistant.getItemName(player.blowpipeDartItemId) + "s.");
		player.blowpipeDartItemId = 0;
		player.blowpipeDartItemAmount = 0;
	}

	/**
	 * Used when the player uses the dismantle option on a Toxic blowpipe.
	 */

	public static void dismantle(Player player) {
		if (!GameType.isOsrsEco()) {
			return;
		}
		player.getPA().sendMessage("You dismantle the blowpipe for 20,000 Zulrah's scales.");
		ItemAssistant.deleteItemFromInventory(player, 12924, 1);
		ItemAssistant.addItem(player, 12934, 20000);
		player.getPA().closeInterfaces(true);
	}

	public static void uncharge(Player player) {
		if (!GameType.isOsrsEco()) {
			return;
		}
		if (player.blowpipeDartItemAmount == 0) {
			player.getPA().sendMessage("You remove " + Misc.formatNumber(player.getBlowpipeCharges()) + " scales from your blowpipe.");
		} else {
			player.getPA().sendMessage(
					"You remove " + Misc.formatNumber(player.blowpipeDartItemAmount) + " " + ItemAssistant.getItemName(player.blowpipeDartItemId) + " and " + Misc.formatNumber(
							player.getBlowpipeCharges()) + " scales from your blowpipe.");
		}
		ItemAssistant.deleteItemFromInventory(player, 12926, 1);
		ItemAssistant.addItem(player, 12924, 1);
		ItemAssistant.addItemToInventoryOrDrop(player, 12934, player.getBlowpipeCharges());
		ItemAssistant.addItemToInventoryOrDrop(player, player.blowpipeDartItemId, player.blowpipeDartItemAmount);
		player.setBlowpipeCharges(0);
		player.blowpipeDartItemAmount = 0;
		player.blowpipeDartItemId = 0;
		player.getPA().closeInterfaces(true);
	}

	public static void check(Player player) {
		if (player.blowpipeDartItemId == 0) {
			player.getPA().sendMessage("Your Toxic blowpipe is empty.");
			return;
		}
		if (GameType.isOsrsEco()) {
			player.getPA().sendMessage(
					"Your Toxic blowpipe has " + Misc.formatNumber(player.blowpipeDartItemAmount) + " " + ItemAssistant.getItemName(player.blowpipeDartItemId) + "s and "
					+ Misc.formatNumber(player.getBlowpipeCharges()) + " scales stored inside.");
		} else {
			player.getPA().sendMessage("Your Toxic blowpipe has " + Misc.formatNumber(player.blowpipeDartItemAmount) + " " + ItemAssistant.getItemName(player.blowpipeDartItemId)
			                           + "s stored inside.");
		}
	}

	public static boolean useWithBlowpipe(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
		boolean hasBlowpipe = false;
		if (itemUsed == 12926 || usedWith == 12926) {
			hasBlowpipe = true;
		}
		if (!hasBlowpipe) {
			return false;
		}

		int storeItem = 0;
		int storeItemAmount = 0;
		int storeItemSlot = 0;
		if (itemUsed != 12926) {
			storeItem = itemUsed;
			storeItemSlot = itemUsedSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
		}
		if (usedWith != 12926) {
			storeItem = usedWith;
			storeItemSlot = usedWithSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
		}
		if (storeItem != 11230 && storeItem != 811) {
			return false;
		}
		if (player.blowpipeDartItemId != storeItem && player.blowpipeDartItemId != 0) {
			player.getPA().sendMessage("You need to unload the darts before adding different ammunition.");
			return true;
		}
		player.blowpipeDartItemId = storeItem;
		if (player.blowpipeDartItemId == storeItem) {
			int maximumAmount = Integer.MAX_VALUE - player.blowpipeDartItemAmount;
			if (storeItemAmount > maximumAmount) {
				player.playerAssistant.sendMessage(ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(storeItem) + ".");
				return true;
			}
			player.blowpipeDartItemAmount += storeItemAmount;
		} else {
			player.blowpipeDartItemAmount = storeItemAmount;
		}
		player.getPA().sendMessage("You load " + storeItemAmount + " " + ItemAssistant.getItemName(storeItem) + "s into your blowpipe.");
		ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
		return true;
	}

	/**
	 * Used when the player uses Zulrah scales on an uncharged blowpipe
	 */

	public static boolean chargeBlowpipe(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
		if (!GameType.isOsrsEco()) {
			return false;
		}
		boolean hasBlowpipe = false;
		if (itemUsed == 12924 || usedWith == 12924) {
			hasBlowpipe = true;
		}
		if (!hasBlowpipe) {
			return false;
		}

		int storeItem = 0;
		int storeItemAmount = 0;
		int storeItemSlot = 0;
		if (itemUsed != 12924) {
			storeItem = itemUsed;
			storeItemSlot = itemUsedSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
		}
		if (usedWith != 12924) {
			storeItem = usedWith;
			storeItemSlot = usedWithSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
		}
		if (storeItem != 12934) {
			return false;
		}
		if (player.getBlowpipeCharges() == MAX_CHARGES) {
			player.getPA().sendMessage("Your blowpipe already has the maximum amount of scales in it.");
		}
		if (storeItemAmount > MAX_CHARGES) {
			ItemAssistant.deleteItemFromInventory(player, 12934, storeItemSlot, MAX_CHARGES);
			ItemAssistant.deleteItemFromInventory(player, 12924, 1);
			ItemAssistant.addItem(player, 12926, 1);
			player.setBlowpipeCharges(player.getBlowpipeCharges() + MAX_CHARGES);
			player.getPA().sendMessage(
					"You add " + Misc.formatNumber(MAX_CHARGES) + " scales to your blowpipe. It has " + Misc.formatNumber(player.getBlowpipeCharges()) + " (<col=00a000>" + (
							(player.getBlowpipeCharges() / 16383.0f) * 100.0f) + "%<col=000000>) charges remaining.");
		} else {
			ItemAssistant.deleteItemFromInventory(player, 12934, storeItemSlot, storeItemAmount);
			ItemAssistant.deleteItemFromInventory(player, 12924, 1);
			ItemAssistant.addItem(player, 12926, 1);
			player.setBlowpipeCharges(player.getBlowpipeCharges() + storeItemAmount);
			player.getPA().sendMessage(
					"You add " + Misc.formatNumber(storeItemAmount) + " scales to your blowpipe. It has " + Misc.formatNumber(player.getBlowpipeCharges()) + " (<col=00a000>" + (
							(player.getBlowpipeCharges() / 16383.0f) * 100.0f) + "%<col=000000>) charges remaining.");
		}
		return true;
	}

	public static void diedWithBlowpipe(Player victim, Player killer, int itemId) {
		if (itemId != 12926) {
			return;
		}
		if (GameType.isOsrsEco()) {
			Server.itemHandler.createGroundItem(killer, victim.blowpipeDartItemId, victim.getX(), victim.getY(), victim.getHeight(), victim.blowpipeDartItemAmount, false, 0, true,
					victim.getPlayerName(), "", "", "", "blowpipeDartItemId, diedWithBlowpipe");
			Server.itemHandler
					.createGroundItem(killer, 12934, victim.getX(), victim.getY(), victim.getHeight(), victim.getBlowpipeCharges(), false, 0, true, victim.getPlayerName(), "", "",
							"", "diedWithBlowpipe dropping 12934");
			victim.blowpipeDartItemId = 0;
			victim.blowpipeDartItemAmount = 0;
			victim.setBlowpipeCharges(0);

		} else {
			Server.itemHandler.createGroundItem(killer, victim.blowpipeDartItemId, victim.getX(), victim.getY(), victim.getHeight(), victim.blowpipeDartItemAmount, false, 0, true,
					victim.getPlayerName(), "", "", "", "blowpipeDartItemId diedWithBlowpipe");
			victim.blowpipeDartItemId = 0;
			victim.blowpipeDartItemAmount = 0;
		}

	}

	/**
	 * Toxic blowpipe attack.
	 */
	public static void reduceBlowpipeScales(Player attacker) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		// Toxic blowpipe
		if (attacker.getWieldedWeapon() == 12926) {
			attacker.blowpipeShotsFired++;
			if (attacker.blowpipeShotsFired == 3) {
				attacker.blowpipeShotsFired = 0;
				attacker.setBlowpipeCharges(attacker.getBlowpipeCharges() - 1);
				if (attacker.getBlowpipeCharges() == 0) {
					// Toxic blowpipe (empty)
					ItemAssistant.replaceEquipmentSlot(attacker, ServerConstants.WEAPON_SLOT, 12924, 1, false);
					attacker.getPA().sendMessage("Your blowpipe has run out of scales.");
				}
			}
		}

	}

	public static void reduceBlowpipeDarts(Player player) {

		// Toxic blowpipe
		if (player.getWieldedWeapon() == 12926) {
			if (player.blowpipeDartItemAmount > 0) {
				if (RangedData.hasAvaRelatedItem(player) && Misc.hasPercentageChance(GameType.isOsrsEco() ? 85 : 25)) {
					return;
				}
				else {
					player.blowpipeDartItemAmount--;
				}
				if (player.blowpipeDartItemAmount == 200) {
					player.getPA().sendMessage(ServerConstants.PURPLE_COL + "You have 200 darts left.");
				} else if (player.blowpipeDartItemAmount == 75) {
					player.getPA().sendMessage(ServerConstants.PURPLE_COL + "You have 75 darts left.");
				}
			}
			if (player.blowpipeDartItemAmount == 0) {
				player.blowpipeDartItemId = 0;
			}
			return;
		}
	}
}
