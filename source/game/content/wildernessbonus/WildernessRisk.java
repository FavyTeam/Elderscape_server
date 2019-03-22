package game.content.wildernessbonus;


import core.ServerConstants;
import game.content.interfaces.ItemsKeptOnDeath;
import game.item.ItemAssistant;
import game.player.Player;
import java.util.ArrayList;
import utility.Misc;

/**
 * Risk required to start a Wilderness activity.
 *
 * @author MGT Madness, created on 03-01-2015.
 */
public class WildernessRisk {
	/**
	 * @param amountOfRisk Amount of risk to scan for in thousands.
	 * @return True, if player has more than amountOfRisk * 1000 of risk.
	 */
	public static boolean hasWildernessActivityRisk(Player player, int amountOfRisk) {
		if (System.currentTimeMillis() - player.timeScannedForWildernessRisk <= 20000) {
			if (!player.hasWildernessRisk && amountOfRisk > 0) {
				player.getPA().sendMessage("You do not have enough risk.");
			}
			return player.hasWildernessRisk;
		}
		player.timeScannedForWildernessRisk = System.currentTimeMillis();

		carriedWealth(player, true);

		if (player.riskedWealth < amountOfRisk && amountOfRisk > 0) {
			player.playerAssistant.sendMessage("You need " + Misc.formatNumber((amountOfRisk - player.riskedWealth)) + " more risk to access this Wilderness activity.");
		}
		player.hasWildernessRisk = player.riskedWealth >= amountOfRisk;
		player.wildernessRiskAmount = player.riskedWealth;
		return player.riskedWealth >= amountOfRisk;
	}

	public static void carriedWealth(Player player, boolean forceProtectItem) {
		player.carriedWealth = 0;
		player.riskedWealth = 0;
		long value = 0;
		long valueOfRiskedItems = 0;

		ArrayList<String> keptOnDeathList = new ArrayList<String>();
		ItemsKeptOnDeath.getItemsKeptOnDeath(player, true, forceProtectItem);
		keptOnDeathList = player.wildernessRiskItemsKeptOnDeath;
		long amountOfItemStackRemovedBecauseIsProtected = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			amountOfItemStackRemovedBecauseIsProtected = 0;
			if (player.playerItems[ITEM] - 1 <= 1) {
				continue;
			}
			boolean matched = false;
			for (int index = 0; index < keptOnDeathList.size(); index++) {
				if (Integer.parseInt(keptOnDeathList.get(index)) == player.playerItems[ITEM] - 1) {
					keptOnDeathList.remove(index);
					if (player.playerItemsN[ITEM] > 1) {
						amountOfItemStackRemovedBecauseIsProtected += 1;
						int remove = 0;
						int size = keptOnDeathList.size();
						for (int a = 0; a < size; a++) {
							if (Integer.parseInt(keptOnDeathList.get(a - remove)) == player.playerItems[ITEM] - 1) {
								if (amountOfItemStackRemovedBecauseIsProtected - player.playerItemsN[ITEM] == 0) {
									continue;
								}
								keptOnDeathList.remove(index);
								remove++;
								amountOfItemStackRemovedBecauseIsProtected += 1;
							}
						}

					} else {
						matched = true;
					}
					break;
				}
			}

			int itemId = player.playerItems[ITEM] - 1;
			int itemAmount = player.playerItemsN[ITEM];
			if (!matched) {
				if (itemAmount - amountOfItemStackRemovedBecauseIsProtected == 0) {
					continue;
				}
				// Toxic Blowpipe.
				if (itemId == 12926 && player.blowpipeDartItemId == 11230) {
					valueOfRiskedItems += WildernessRisk.getItemRiskValue(11230) * player.blowpipeDartItemAmount;
					value += ServerConstants.getItemValue(11230) * player.blowpipeDartItemAmount;
				}

				valueOfRiskedItems += WildernessRisk.getItemRiskValue(itemId) * (itemAmount - amountOfItemStackRemovedBecauseIsProtected);
				value += ServerConstants.getItemValue(itemId) * itemAmount;
			} else {
				value += ServerConstants.getItemValue(itemId);
			}
		}
		amountOfItemStackRemovedBecauseIsProtected = 0;
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (player.playerEquipment[EQUIP] <= 1) {
				continue;
			}
			boolean matched = false;
			for (int index = 0; index < keptOnDeathList.size(); index++) {
				if (Integer.parseInt(keptOnDeathList.get(index)) == player.playerEquipment[EQUIP]) {
					keptOnDeathList.remove(index);
					if (player.playerEquipmentN[EQUIP] > 1) {
						amountOfItemStackRemovedBecauseIsProtected += 1;
						int remove = 0;
						int size = keptOnDeathList.size();
						for (int a = 0; a < size; a++) {
							if (Integer.parseInt(keptOnDeathList.get(a - remove)) == player.playerEquipment[EQUIP]) {
								if (amountOfItemStackRemovedBecauseIsProtected - player.playerEquipmentN[EQUIP] == 0) {
									continue;
								}
								keptOnDeathList.remove(index);
								remove++;
								amountOfItemStackRemovedBecauseIsProtected += 1;
							}
						}

					} else {
						matched = true;
					}
					break;
				}
			}
			int itemId = player.playerEquipment[EQUIP];
			int itemAmount = player.playerEquipmentN[EQUIP];
			if (!matched) {
				if (itemAmount - amountOfItemStackRemovedBecauseIsProtected == 0) {
					continue;
				}

				// Toxic Blowpipe.
				if (itemId == 12926 && player.blowpipeDartItemId == 11230) {
					valueOfRiskedItems += WildernessRisk.getItemRiskValue(11230) * player.blowpipeDartItemAmount;
					value += ServerConstants.getItemValue(11230) * player.blowpipeDartItemAmount;
				}
				valueOfRiskedItems += WildernessRisk.getItemRiskValue(itemId) * (itemAmount - amountOfItemStackRemovedBecauseIsProtected);
				value += ServerConstants.getItemValue(itemId) * itemAmount;
			} else {
				value += ServerConstants.getItemValue(itemId);
			}
		}
		if (ItemAssistant.hasItemInInventory(player, 11941)) {
			for (int i = 0; i < player.lootingBagStorageItemId.length; i++) {
				if (player.lootingBagStorageItemId[i] <= 1) {
					continue;
				}
				int itemId = player.lootingBagStorageItemId[i];
				int itemAmount = player.lootingBagStorageItemAmount[i];
				valueOfRiskedItems += WildernessRisk.getItemRiskValue(itemId) * itemAmount;
				value += ServerConstants.getItemValue(itemId) * itemAmount;// Toxic Blowpipe.

				if (itemId == 12926 && player.blowpipeDartItemId == 11230) {
					valueOfRiskedItems += WildernessRisk.getItemRiskValue(11230) * player.blowpipeDartItemAmount;
					value += ServerConstants.getItemValue(11230) * player.blowpipeDartItemAmount;
				}
			}
		}

		player.carriedWealth = value;
		player.riskedWealth = valueOfRiskedItems;
	}

	/**
	 * Get the item risk value, items that go to inventory on death is 0, items that go to shop is price / 10. Normal items are normal.
	 *
	 * @param itemId
	 * @return
	 */
	public static int getItemRiskValue(int itemId) {
		/*
		for (final PvpItems data : PvpItems.values()) {
			if (GameType.isPreEoc() && itemId == data.getPreEocId() || GameType.isOsrs() && itemId == data.getOsrsId()) {
				return (ServerConstants.getItemValue(itemId) / 4 * 3);
			}
		}
		*/
		if (ItemAssistant.isItemToUntradeableShopOnDeath(itemId)) {
			return (ServerConstants.getItemValue(itemId) / 10);
		} else if (ItemAssistant.isItemToInventoryOnDeath(itemId)) {
			return 0;
		} else {
			return ServerConstants.getItemValue(itemId);
		}
	}

}
