package game.item;

import game.player.Player;
import utility.FileUtility;
import utility.Misc;

import java.util.ArrayList;

/**
 * @author Sanity
 */

public class PotionCombining {

	public static boolean hasPotionsToCombine(Player player) {
		boolean hasTwoSameTypePotion = false;
		ArrayList<String> potionNames = new ArrayList<String>();
		ArrayList<String> potionNamesDetailed = new ArrayList<String>();
		for (int index = 0; index < 28; index++) {
			int item = player.playerItems[index] - 1;
			int slot = index;
			if (item <= 0) {
				continue;
			}
			String itemName = ItemDefinition.getDefinitions()[item].name.toLowerCase();

			if (!itemName.contains("(")) {
				continue;
			}
			if (itemName.contains("watering") || itemName.contains("glory")) {
				continue;
			}
			if (!itemName.contains("1") && !itemName.contains("2") && !itemName.contains("3")) {
				continue;
			}
			if (itemName.contains("4")) {
				continue;
			}

			String name = itemName.substring(0, itemName.indexOf("("));
			for (int index1 = 0; index1 < potionNames.size(); index1++) {
				if (potionNames.get(index1).equals(name)) {
					hasTwoSameTypePotion = true;
					player.potions.add(item + " " + slot);
					player.potions.add(potionNamesDetailed.get(index1));
					break;
				}
			}
			potionNames.add(name);
			potionNamesDetailed.add(item + " " + slot);
		}
		if (player.potions.isEmpty() || !hasTwoSameTypePotion) {
			return false;
		}
		return true;
	}

	public static void combineAllPotions(Player player) {
		if (!hasPotionsToCombine(player)) {
			return;
		}

		String potionName1 = "";
		ArrayList<String> potionList1 = new ArrayList<String>();

		for (int index = 0; index < player.potions.size(); index++) {
			String[] args = player.potions.get(index).split(" ");
			String fullName = ItemDefinition.getDefinitions()[Integer.parseInt(args[0])].name;
			String name = fullName.substring(0, fullName.indexOf("("));
			if (potionName1.isEmpty() || potionName1.equals(name) && potionList1.isEmpty()) {
				potionName1 = name;
				potionList1.add(player.potions.get(index));
				continue;
			}

			if (name.equals(potionName1)) {
				String[] args1 = potionList1.get(0).split(" ");
				PotionCombining.combinePotion(player, false, Integer.parseInt(args[0]), Integer.parseInt(args1[0]), Integer.parseInt(args[1]), Integer.parseInt(args1[1]), true);
				potionList1.clear();
				continue;
			}
		}

		player.potions.clear();
		player.potionCombineLoops++;
		if (player.potionCombineLoops > 20) {
			player.potionCombineLoops = 0;
			return;
		}
		combineAllPotions(player);
	}

	public static void combinePotion(Player player, boolean message, int id, int id2, int slot1, int slot2, boolean automatic) {
		if (player.getHeight() == 20 && ItemAssistant.getItemName(id).contains("brew")) {
			player.getPA().sendMessage("You cannot combine brews around here.");
			return;
		}
		if (automatic) {
			if (!ItemAssistant.playerHasItem(player, id, 1, slot1)) {
				return;
			}
			if (!ItemAssistant.playerHasItem(player, id2, 1, slot2)) {
				return;
			}
		}
		if (ItemDefinition.getDefinitions()[id].note) {
			return;
		}
		if (ItemDefinition.getDefinitions()[id2].note) {
			return;
		}
		String id11 = ItemAssistant.getItemName(id);
		String id22 = ItemAssistant.getItemName(id2);
		if (!id11.contains("(1)") && !id11.contains("(2)") && !id11.contains("(3)") && !id11.contains("(4)")) {
			return;
		}
		if (!id22.contains("(1)") && !id22.contains("(2)") && !id22.contains("(3)") && !id22.contains("(4)")) {
			return;
		}
		if (id11.substring(0, id11.indexOf("(")).equalsIgnoreCase(id22.substring(0, id22.indexOf("(")))) {
			try {
				int amount1 = Integer.parseInt(id11.substring(id11.indexOf("(") + 1, id11.indexOf("(") + 2));
				int amount2 = Integer.parseInt(id22.substring(id22.indexOf("(") + 1, id22.indexOf("(") + 2));
				int totalAmount = amount1 + amount2;
				if (totalAmount > 4) {
					amount1 = 4;
					amount2 = totalAmount - 4;
					String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
					String item2 = id11.substring(0, id11.indexOf("(") + 1) + amount2 + ")";
					player.potionDecanted = true;
					ItemAssistant.deleteItemFromInventory(player, id, slot1, 1);
					ItemAssistant.deleteItemFromInventory(player, id2, slot2, 1);
					ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item1, false), 1, slot1, false);
					ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item2, false), 1, slot2, true);
					if (message) {
						player.playerAssistant.sendFilterableMessage("You combine the potion.");
					}
				} else {
					amount1 = totalAmount;
					String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
					player.potionDecanted = true;
					ItemAssistant.deleteItemFromInventory(player, id, slot1, 1);
					ItemAssistant.deleteItemFromInventory(player, id2, slot2, 1);
					ItemAssistant.addItemToInventory(player, ItemAssistant.getItemId(item1, false), 1, slot1, false);
					ItemAssistant.addItemToInventory(player, 229, 1, slot2, true);
					if (message) {
						player.playerAssistant.sendFilterableMessage("You combine the potion.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void decantAllPotions(Player player) {
		ArrayList<String> inventoryDebug = new ArrayList<String>();
		for (int index = 0; index < player.playerItems.length; index++) {
			inventoryDebug.add(player.playerItems[index] + " " + player.playerItemsN[index]);
		}
		try {
			ArrayList<String> potionName = new ArrayList<String>();
			ArrayList<Integer> doseAmount = new ArrayList<Integer>();
			for (int index = 0; index < 28; index++) {
				int item = player.playerItems[index] - 1;
				if (item <= 0) {
					continue;
				}
				String itemName = ItemDefinition.getDefinitions()[item].name;

				if (!itemName.contains("(")) {
					continue;
				}
				if (itemName.toLowerCase().contains("watering") || itemName.toLowerCase().contains("glory")) {
					continue;
				}
				if (!itemName.contains("1") && !itemName.contains("2") && !itemName.contains("3")) {
					continue;
				}
				if (itemName.contains("4")) {
					continue;
				}

				String potionNameWithoutDose = itemName.substring(0, itemName.indexOf("("));
				String doseString = itemName.substring(itemName.indexOf("(") + 1, itemName.indexOf(")"));
				if (!Misc.isNumeric(doseString)) {
					continue;
				}
				int doses = Integer.parseInt(doseString);
				if (ItemAssistant.isNoted(item)) {
					doses *= player.playerItemsN[index];
				}

				boolean found = false;
				for (int i = 0; i < potionName.size(); i++) {
					if (potionNameWithoutDose.equals(potionName.get(i))) {
						found = true;
						doseAmount.set(i, doseAmount.get(i) + doses);
						break;
					}
				}
				if (!found) {
					potionName.add(potionNameWithoutDose);
					doseAmount.add(doses);
				}
				player.playerItems[index] = 0;
				player.playerItemsN[index] = 0;
				/*
				 * 
				Get allpotions in inventory, noted or not noted
				get its name before the (3), like 'Super strength#DOSES HERE'
				then delete all potions in inventory and give back to player in noted potions
				 */
			}

			if (!potionName.isEmpty()) {
				for (int index = 0; index < potionName.size(); index++) {
					//get itemId by name, also noted only searches
					String name = potionName.get(index);
					int amount = doseAmount.get(index);

					// This is used to get the item name, so it has to be 4 maximum
					int doseLimited = amount;
					if (doseLimited > 4) {
						doseLimited = 4;
					}

					int itemId = ItemAssistant.getItemId(name + "(" + doseLimited + ")", true);
					if (itemId > 0) {
						if (doseLimited < 4) {
							ItemAssistant.addItem(player, itemId, 1);
						} else {
							int fullPotionsAmount = amount / 4;
							ItemAssistant.addItem(player, itemId, fullPotionsAmount);
							if ((double) amount / (double) 4 > (double) fullPotionsAmount) {
								int remainingDose = amount - (fullPotionsAmount * 4);
								ItemAssistant.addItem(player, ItemAssistant.getItemId(name + "(" + remainingDose + ")", true), 1);
							}
						}
					}
				}
			} else {
				player.getPA().sendMessage("You do not have any potions to decant.");
			}
		} catch (Exception e) {
			FileUtility.saveArrayContentsSilent("decant_debug.txt", inventoryDebug);
			e.printStackTrace();
		}

	}

}
