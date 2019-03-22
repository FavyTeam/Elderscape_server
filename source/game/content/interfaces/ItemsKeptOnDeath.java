package game.content.interfaces;

import core.ServerConstants;
import game.content.starter.GameMode;
import game.content.wildernessbonus.WildernessRisk;
import game.item.ItemAssistant;
import game.player.Player;

import java.util.ArrayList;

/**
 * Items kept on death interface.
 *
 * @author MGT Madness, created on 30-01-2014.
 */
public class ItemsKeptOnDeath {

	public static void showDeathInterface(Player player) {
		if (!player.getDead()) {
			getItemsKeptOnDeath(player, false, false);
		}
		player.getPA().sendFrame126("Items Kept on Death", 17103);
		for (int k = 0; k < 8; k++) {
			player.getPA().sendFrame34(10494, -1, k, 1);
		}
		for (int k = 0; k < 39; k++) {
			player.getPA().sendFrame34(10600, -1, k, 1);
		}

		for (int index = 0; index < player.itemsKeptOnDeathList.size(); index++) {
			player.getPA().sendFrame34(10494, Integer.parseInt(player.itemsKeptOnDeathList.get(index)), index, 1);
		}
		ArrayList<String> itemList = new ArrayList<String>();
		itemList = player.itemsKeptOnDeathList;
		int slot = 0;
		int amountToRemove = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			amountToRemove = 0;
			if (player.playerItems[ITEM] - 1 <= 1) {
				continue;
			}
			boolean matched = false;
			for (int index = 0; index < itemList.size(); index++) {
				if (Integer.parseInt(itemList.get(index)) == player.playerItems[ITEM] - 1) {
					itemList.remove(index);
					if (player.playerItemsN[ITEM] > 1) {
						amountToRemove += 1;

						int remove = 0;
						int size = itemList.size();
						for (int a = 0; a < size; a++) {
							if (Integer.parseInt(itemList.get(a - remove)) == player.playerItems[ITEM] - 1) {
								if (amountToRemove - player.playerItemsN[ITEM] == 0) {
									continue;
								}
								itemList.remove(index);
								remove++;
								amountToRemove += 1;
							}
						}

					} else {
						matched = true;
					}
					break;
				}
			}
			if (!matched) {
				if (player.playerItemsN[ITEM] - amountToRemove == 0) {
					continue;
				}
				// Toxic Blowpipe.
				if (player.playerItems[ITEM] - 1 == 12926 && player.blowpipeDartItemId == 11230) {
					player.getPA().sendFrame34(10600, player.blowpipeDartItemId, slot, player.blowpipeDartItemAmount);
					slot++;
				}
				player.getPA().sendFrame34(10600, player.playerItems[ITEM] - 1, slot, player.playerItemsN[ITEM] - amountToRemove);
				slot++;
			}
		}
		amountToRemove = 0;
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] <= 1) {
				continue;
			}
			boolean matched = false;
			for (int index = 0; index < itemList.size(); index++) {
				if (Integer.parseInt(itemList.get(index)) == player.playerEquipment[EQUIP]) {
					itemList.remove(index);
					if (player.playerEquipmentN[EQUIP] > 1) {
						amountToRemove += 1;

						int remove = 0;
						int size = itemList.size();
						for (int a = 0; a < size; a++) {
							if (Integer.parseInt(itemList.get(a - remove)) == player.playerEquipment[EQUIP]) {
								if (amountToRemove - player.playerEquipmentN[EQUIP] == 0) {
									continue;
								}
								itemList.remove(index);
								remove++;
								amountToRemove += 1;
							}
						}

					} else {
						matched = true;
					}
					break;
				}
			}
			if (!matched) {
				if (player.playerEquipmentN[EQUIP] - amountToRemove == 0) {
					continue;
				}
				// Toxic Blowpipe.
				if (player.playerEquipment[EQUIP] == 12926 && player.blowpipeDartItemId == 11230) {
					player.getPA().sendFrame34(10600, player.blowpipeDartItemId, slot, player.blowpipeDartItemAmount);
					slot++;
				}
				player.getPA().sendFrame34(10600, player.playerEquipment[EQUIP], slot, player.playerEquipmentN[EQUIP] - amountToRemove);
				slot++;
			}
		}

		amountToRemove = 0;
		if (ItemAssistant.hasItemInInventory(player, 11941)) {
			for (int i = 0; i < player.lootingBagStorageItemId.length; i++) {
				if (player.lootingBagStorageItemId[i] <= 1) {
					continue;
				}

				player.getPA().sendFrame34(10600, player.lootingBagStorageItemId[i], slot, player.lootingBagStorageItemAmount[i]);
				slot++;

				// Toxic Blowpipe.
				if (player.lootingBagStorageItemId[i] == 12926 && player.blowpipeDartItemId == 11230) {
					player.getPA().sendFrame34(10600, player.blowpipeDartItemId, slot, player.blowpipeDartItemAmount);
					slot++;
				}
			}
		}
		WildernessRisk.carriedWealth(player, false);
		player.getPA().sendFrame126(ServerConstants.getFormattedNumberString(player.carriedWealth), 17132);
		player.getPA().sendFrame126(ServerConstants.getFormattedNumberString(player.riskedWealth), 17134);
		player.getPA().displayInterface(17100);
		player.isUsingDeathInterface = true;
		player.timeScannedForWildernessRisk = 0; // Must be reset.

	}

	/**
	 * Update the items kept on death interface, if something has changed while the interface is already opened.
	 *
	 * @param player The associated player.
	 */
	public static void updateInterface(Player player) {
		if (player.isUsingDeathInterface) {
			showDeathInterface(player);
			player.isUsingDeathInterface = true;
		}
	}

	public static void getItemsKeptOnDeath(Player player, boolean saveTemporary, boolean forceProtectItem) {
		if (saveTemporary) {
			player.wildernessRiskItemsKeptOnDeath.clear();
		} else {
			player.itemsKeptOnDeathList.clear();
		}
		// When it comes to stackable items, 8 of it, because 8 items is the maximum amount a Legendary Donator can keep on death.
		// Max of 242 items added
		int[] itemListNew = new int[242];
		int[] itemListValueNew = new int[242];

		int MAX_AMOUNT_OF_A_STACK = 8;

		for (int index = 0; index < player.playerEquipment.length; index++) {
			if (player.playerEquipment[index] <= 1) {
				continue;
			}
			int duplicate = player.playerEquipmentN[index];
			if (duplicate > MAX_AMOUNT_OF_A_STACK) {
				duplicate = MAX_AMOUNT_OF_A_STACK;
			}
			for (int i = 0; i < duplicate; i++) {
				int value = 0;
				int itemId = player.playerEquipment[index];
				value = WildernessRisk.getItemRiskValue(itemId);
				for (int b = 0; b < itemListNew.length; b++) {
					if (itemListNew[b] == 0) {
						itemListNew[b] = itemId;
						itemListValueNew[b] = value;
						break;
					}
				}
			}
		}
		for (int index = 0; index < player.playerItems.length; index++) {
			if (player.playerItems[index] - 1 <= 1) {
				continue;
			}
			int duplicate = player.playerItemsN[index];
			if (duplicate > MAX_AMOUNT_OF_A_STACK) {
				duplicate = MAX_AMOUNT_OF_A_STACK;
			}
			for (int i = 0; i < duplicate; i++) {
				int value = 0;
				int itemId = player.playerItems[index] - 1;

				// Looting bag, blood key and mini blood key
				if (itemId == 11941 || itemId == 13302 || itemId == 20526) {
					continue;
				}
				// Revenant cave emblem drops
				if (itemId == 21807 || itemId == 21808 || itemId == 21810 || itemId == 21811 || itemId == 21813 || itemId == 21814 || itemId == 22299 || itemId == 22300
				    || itemId == 22302 || itemId == 22303 || itemId == 22305 || itemId == 22306) {
					continue;
				}
				value = WildernessRisk.getItemRiskValue(itemId);
				for (int b = 0; b < itemListNew.length; b++) {
					if (itemListNew[b] == 0) {
						itemListNew[b] = itemId;
						itemListValueNew[b] = value;
						break;
					}
				}
			}
		}
		// Looting bag contents are always dropped on death on OSRS.

		// Sort from highest value to lowest.
		for (int counter = 0; counter < itemListNew.length - 1; counter++) {
			for (int index = 0; index < itemListNew.length - 1 - counter; index++) {

				if (itemListValueNew[index] > itemListValueNew[index + 1]) {
					int tempItemId = itemListNew[index];
					int tempValue = itemListValueNew[index];
					itemListNew[index] = itemListNew[index + 1];
					itemListValueNew[index] = itemListValueNew[index + 1];
					itemListNew[index + 1] = tempItemId;
					itemListValueNew[index + 1] = tempValue;
				}
			}
		}
		int amountToKeep = 0;
		if (!player.getWhiteSkull() && !player.getRedSkull()) {
			amountToKeep += 3;
		}
		if (player.prayerActive[ServerConstants.PROTECT_ITEM] || forceProtectItem && !player.getRedSkull()) {
			amountToKeep += 1;
		}
		if (GameMode.getGameMode(player, "ULTIMATE IRON MAN")) {
			amountToKeep = 0;
		}

		int number = itemListNew.length - amountToKeep;
		if (number < 0) {
			number = 0;
		}
		for (int index = itemListNew.length - 1; index >= number; index--) {
			if (saveTemporary) {
				player.wildernessRiskItemsKeptOnDeath.add(itemListNew[index] + "");
			} else {
				player.itemsKeptOnDeathList.add(itemListNew[index] + "");
			}
		}

		if (!saveTemporary) {
			updateItemsKeptOnDeathText(player, player.itemsKeptOnDeathList.size());
		}

	}

	public static void updateItemsKeptOnDeathText(Player player, int kept) {
		player.getPA().sendFrame126("Items you will keep on death:", 17104);
		player.getPA().sendFrame126("Items you will lose on death:", 17105);
		player.getPA().sendFrame126("Player Information", 17106);
		player.getPA().sendFrame126("Items kept on death:", 17107);
		player.getPA().sendFrame126("~ " + kept + " ~", 17108);
	}

	public static void deleteItemsKeptOnDeath(Player player) {
		for (int index = 0; index < player.itemsKeptOnDeathList.size(); index++) {
			int itemId = Integer.parseInt(player.itemsKeptOnDeathList.get(index));
			if (ItemAssistant.hasItemInInventory(player, itemId)) {
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				continue;
			}
			if (ItemAssistant.hasItemEquipped(player, itemId)) {
				ItemAssistant.deleteEquipment(player, itemId);
				continue;
			}
		}

	}

	public static void addItemsToInventoryThatAreKeptOnDeath(Player player) {
		for (int index = 0; index < player.itemsKeptOnDeathList.size(); index++) {
			int itemId = Integer.parseInt(player.itemsKeptOnDeathList.get(index));
			ItemAssistant.addItem(player, itemId, 1);
		}
		player.itemsKeptOnDeathList.clear();

	}

}
