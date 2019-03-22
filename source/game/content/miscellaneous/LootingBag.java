package game.content.miscellaneous;

import core.ServerConstants;
import game.content.combat.Death;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Looting bag feature.
 *
 * @author MGT Madness, created on 20-04-2016.
 */
public class LootingBag {

	public static void displayLootingBagInterface(Player player) {
		for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
			int item = player.lootingBagStorageItemId[index] == 0 ? -1 : player.lootingBagStorageItemId[index];
			player.getPA().sendFrame34(22251, item, index, player.lootingBagStorageItemAmount[index]);
		}
		updateLootingBagValueText(player);
		player.getPA().setSidebarInterface(3, 22245);
	}

	/**
	 * Update the value text of the looting bag contents.
	 */
	private static void updateLootingBagValueText(Player player) {
		int value = 0;

		for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
			int item = player.lootingBagStorageItemId[index];
			int amount = player.lootingBagStorageItemAmount[index];
			if (item <= 0) {
				continue;
			}
			value += ServerConstants.getItemValue(item) * amount;
		}

		player.getPA().sendFrame126("Value: " + Misc.formatNumber(value) + " " + ServerConstants.getMainCurrencyName(), 22252);

	}

	public static void closeLootingBagInterface(Player player) {
		player.getPA().setSidebarInterface(3, 3213);
	}

	public static void lootingBagDeath(Player victim, Player attacker, boolean killerExists) {
		for (int index = 0; index < victim.lootingBagStorageItemId.length; index++) {
			int item = victim.lootingBagStorageItemId[index];
			int amount = victim.lootingBagStorageItemAmount[index];
			if (item <= 0) {
				continue;
			}

			victim.lootingBagStorageItemId[index] = 0;
			victim.lootingBagStorageItemAmount[index] = 0;
			Death.victimLoot(victim, attacker, item, amount, false, killerExists);
		}
	}

	public static void withdrawLootingBag(Player player) {
		if (Area.inDangerousPvpAreaOrClanWars(player)) {
			player.getPA().sendMessage("You may only withdraw items outside Pvp areas.");
			return;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1) {
			return;
		}
		for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
			int item = player.lootingBagStorageItemId[index];
			int amount = player.lootingBagStorageItemAmount[index];
			boolean give = false;
			if (ItemDefinition.getDefinitions()[item].stackable && ItemAssistant.hasItemInInventory(player, item)) {
				give = true;
			} else if (ItemAssistant.getFreeInventorySlots(player) > 0) {
				give = true;
			}
			if (give) {
				if (ItemAssistant.addItem(player, item, amount)) {
					player.lootingBagStorageItemId[index] = 0;
					player.lootingBagStorageItemAmount[index] = 0;
				}
			}
		}
	}

	public static boolean useWithLootingBag(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
		boolean hasBag = false;
		if (itemUsed == 11941 || usedWith == 11941) {
			hasBag = true;
		}
		if (!hasBag) {
			return false;
		}

		if (!Area.inDangerousPvpArea(player)) {
			player.getPA().sendMessage("You can't put items in the bag unless you're in the Wilderness");
			return true;
		}

		int storeItem = 0;
		int storeItemAmount = 0;
		int storeItemSlot = 0;
		if (itemUsed != 11941) {
			storeItem = itemUsed;
			storeItemSlot = itemUsedSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
		}
		if (usedWith != 11941) {
			storeItem = usedWith;
			storeItemSlot = usedWithSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
		}
		if (storeItem == 11941) {
			player.getPA().sendMessage("Bag'ception is not permitted.");
			return true;
		}
		boolean itemStoredCompleted = false;
		if (ItemDefinition.getDefinitions()[storeItem].stackable) {
			for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
				if (player.lootingBagStorageItemId[index] == storeItem) {
					int maximumAmount = Integer.MAX_VALUE - player.lootingBagStorageItemAmount[index];
					if (storeItemAmount > maximumAmount) {
						player.playerAssistant.sendMessage(
								ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.lootingBagStorageItemId[index]) + ".");
						return false;
					}
					player.lootingBagStorageItemAmount[index] += storeItemAmount;
					ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
					itemStoredCompleted = true;
					break;
				}
			}
		}
		if (!itemStoredCompleted) {

			for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
				if (player.lootingBagStorageItemId[index] == 0) {
					player.lootingBagStorageItemId[index] = storeItem;
					player.lootingBagStorageItemAmount[index] = storeItemAmount;
					ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
					break;
				}
			}
		}

		return true;
	}
}
