package game.content.miscellaneous;

import core.Server;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.player.Player;
import utility.Misc;

/**
 * Handles the Bracelet of ethereum
 *
 * @author Owain, created on 27-11-2017.
 */
public class BraceletOfEthereum {
	/**
	 * The maximum amount of charges (Revenant ether) a bracelet can hold.
	 */

	public static int MAX_CHARGES = 16000;

	/**
	 * Used when the player uses the uncharge option on a charged Bracelet of ethereum.
	 */

	public static void uncharge(Player player) {
		player.getPA().sendMessage("You remove " + Misc.formatNumber(player.braceletCharges) + " Revenant ether from the bracelet.");
		ItemAssistant.addItemToInventoryOrDrop(player, 21820, player.braceletCharges);
		ItemAssistant.deleteItemFromInventory(player, 21816, 1);
		ItemAssistant.addItem(player, 21817, 1);
		player.braceletCharges = 0;
	}

	/**
	 * Used when the player's bracelet runs out of charges.
	 */

	public static void replaceBracelet(Player player) {
		player.getPA().sendMessage("<col=ff0000>Your bracelet has run out of charges. Revenants will now be agressive towards you.");
		ItemAssistant.replaceEquipmentSlot(player, ServerConstants.HAND_SLOT, 21817, 1, false);
	}

	/**
	 * Used when the player uses the dismantle option on an uncharged bracelet.
	 */

	public static void dismantle(Player player) {
		player.getPA().sendMessage("You dismantle the bracelet for 250 Revenant ether.");
		ItemAssistant.deleteItemFromInventory(player, 21817, 1);
		ItemAssistant.addItem(player, 21820, 250);
	}

	/**
	 * Handles the check option on a charged bracelet, used to check charges left.
	 */

	public static void check(Player player) {
		if (player.braceletCharges == 0) {
			player.getPA().sendMessage("<col=ba05a8>Your bracelet has run out of charges.");
			return;
		}
		player.getPA().sendMessage(
				"The bracelet has " + Misc.formatNumber(player.braceletCharges) + " (<col=00a000>" + Misc.roundDoubleToNearestTwoDecimalPlaces(((player.braceletCharges / 16000.0f) * 100.0f))
				+ "%<col=000000>) charges remaining.");
	}

	/**
	 * Used when the player uses revenant ether on an uncharged bracelet.
	 */

	public static boolean useWithBracelet(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
		boolean hasBracelet = false;
		if (itemUsed == 21817 || usedWith == 21817) {
			hasBracelet = true;
		}
		if (!hasBracelet) {
			return false;
		}

		int storeItem = 0;
		int storeItemAmount = 0;
		int storeItemSlot = 0;
		if (itemUsed != 21817) {
			storeItem = itemUsed;
			storeItemSlot = itemUsedSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
		}
		if (usedWith != 21817) {
			storeItem = usedWith;
			storeItemSlot = usedWithSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
		}
		if (storeItem != 21820) {
			return false;
		}
		if (player.braceletCharges == 16000) {
			player.getPA().sendMessage("Your bracelet already has the maximum amount of charges.");
		}
		if (storeItemAmount > MAX_CHARGES) {
			storeItemAmount = MAX_CHARGES;
		}
		ItemAssistant.deleteItemFromInventory(player, 21820, storeItemSlot, storeItemAmount);
		ItemAssistant.deleteItemFromInventory(player, 21817, 1);
		ItemAssistant.addItem(player, 21816, 1);
		player.braceletCharges += storeItemAmount;
		player.getPA().sendMessage(
				"You add " + Misc.formatNumber(storeItemAmount) + " ether to your bracelet. It has " + Misc.formatNumber(player.braceletCharges) + " (<col=00a000>"
				+ Misc.roundDoubleToNearestTwoDecimalPlaces(((player.braceletCharges / 16000.0f) * 100.0f)) + "%<col=000000>) charges remaining.");
		return true;
	}

	/**
	 * If a player dies with a charged bracelet, the killer gets the revenant ether that they had as charges.
	 */

	public static void diedWithBracelet(Player victim, Player killer, int itemId) {
		Server.itemHandler
				.createGroundItem(killer, 21820, victim.getX(), victim.getY(), victim.getHeight(), victim.braceletCharges, false, 0, true, victim.getPlayerName(), "", "", "", "diedWithBracelet 21820");
		ItemAssistant.deleteItemFromInventory(victim, 21816, 1);
		Server.itemHandler.createGroundItem(killer, 21817, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "diedWithBracelet 21817");
		victim.braceletCharges = 0;

	}

	/**
	 * If a player dies with a charged bracelet, the killer gets the revenant ether that they had as charges.
	 */

	public static void diedWithBraceletToNPC(Player victim, int itemId) {
		Server.itemHandler
				.createGroundItem(victim, 21820, victim.getX(), victim.getY(), victim.getHeight(), victim.braceletCharges, false, 0, true, victim.getPlayerName(), "", "", "", "diedWithBraceletToNPC 21820");
		ItemAssistant.deleteItemFromInventory(victim, 21816, 1);
		Server.itemHandler.createGroundItem(victim, 21817, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "diedWithBraceletToNPC 21817");
		victim.braceletCharges = 0;

	}

	/**
	 * Checks if a player has a charged bracelet equipped
	 */

	public static boolean hasChargedBracelet(Player player) {
		if (player.playerEquipment[ServerConstants.HAND_SLOT] == 21816) {
			return true;
		}
		return false;
	}

	/**
	 * When a player attacks a Revenant with an ethereum bracelet equipped, this handles the draining of the tokens.
	 */
	public static void reduceBracletCharges(Player player, Npc npc) {
		if (player.braceletCharges == 0) {
			return;
		}
		if (hasChargedBracelet(player)) {
			player.braceletCharges -= 1;
			if (player.braceletCharges == 0) {
				replaceBracelet(player);
			}
		}
	}
}
