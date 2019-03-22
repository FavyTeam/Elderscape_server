package game.content.miscellaneous;

import core.Server;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;

/**
 * Rune pouch item feature.
 *
 * @author MGT Madness, created on 25-02-2017.
 */
public class RunePouch {

	public static boolean runePouchInterfaceButton(Player player, int buttonId) {
		switch (buttonId) {
			case 89134:
				withdrawAllRunePouch(player, 0, true);
				break;
			case 89135:
				withdrawAllRunePouch(player, 1, true);
				break;
			case 89136:
				withdrawAllRunePouch(player, 2, true);
				break;
		}
		return false;
	}

	private static void withdrawAllRunePouch(Player player, int index, boolean updateInterface) {
		if (player.getActionIdUsed() != 8) {
			return;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1) {
			return;
		}
		if (ItemAssistant.addItem(player, player.runePouchItemId[index], player.runePouchItemAmount[index])) {
			player.runePouchItemId[index] = 0;
			player.runePouchItemAmount[index] = 0;
			if (updateInterface) {
				openRunePouch(player, false);
			}
		}

	}

	public static boolean runePouchItemClick(Player player, String clickType) {

		switch (clickType) {
			case "OPEN":
				openRunePouch(player, true);
				player.setActionIdUsed(8);
				return true;
			case "EMPTY":
				emptyRunePouch(player);
				return true;
		}
		return false;
	}

	/**
	 * Sent on log-in and when using presets, so the client knows what runes the player is holding.
	 *
	 * @param player
	 */
	public static void updateRunePouchMainStorage(Player player, boolean logIn) {
		if (logIn) {
			if (!ItemAssistant.hasItemInInventory(player, 12791)) {
				return;
			}
		}
		player.getPA().sendFrame34(22917, player.runePouchItemId[0], 0, player.runePouchItemAmount[0]);
		player.getPA().sendFrame34(22917, player.runePouchItemId[1], 1, player.runePouchItemAmount[1]);
		player.getPA().sendFrame34(22917, player.runePouchItemId[2], 2, player.runePouchItemAmount[2]);
	}

	private static void openRunePouch(Player player, boolean openInterface) {
		updateRunePouchMainStorage(player, false);
		for (int index = 0; index < 28; index++) {
			player.getPA().sendFrame34(22921, player.playerItems[index] - 1, index, player.playerItemsN[index]);
		}
		if (openInterface) {
			player.getPA().displayInterface(22910);
		}
	}

	private static void emptyRunePouch(Player player) {
		withdrawAllRunePouch(player, 0, false);
		withdrawAllRunePouch(player, 1, false);
		withdrawAllRunePouch(player, 2, true);
	}

	public static boolean useWithRunePouch(Player player, int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
		boolean hasRunePouch = false;
		if (itemUsed == 12791 || usedWith == 12791) {
			hasRunePouch = true;
		}
		if (!hasRunePouch) {
			return false;
		}

		int storeItem = 0;
		int storeItemAmount = 0;
		int storeItemSlot = 0;
		if (itemUsed != 12791) {
			storeItem = itemUsed;
			storeItemSlot = itemUsedSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, itemUsedSlot);
		}
		if (usedWith != 12791) {
			storeItem = usedWith;
			storeItemSlot = usedWithSlot;
			storeItemAmount = ItemAssistant.getItemAmount(player, storeItem, usedWithSlot);
		}
		boolean isRuneItemId = storeItem >= 4694 && storeItem <= 4699 || storeItem == 9075 || storeItem >= 554 && storeItem <= 566;
		if (!isRuneItemId) {
			return false;
		}
		player.setActionIdUsed(8);
		boolean itemStoredCompleted = false;
		if (ItemDefinition.getDefinitions()[storeItem].stackable) {
			for (int index = 0; index < player.runePouchItemId.length; index++) {
				if (player.runePouchItemId[index] == storeItem) {
					int maximumAmount = Integer.MAX_VALUE - player.runePouchItemAmount[index];
					if (storeItemAmount > maximumAmount) {
						player.playerAssistant
								.sendMessage(ServerConstants.RED_COL + "Maximum item stack reached, cannot add: " + ItemAssistant.getItemName(player.runePouchItemId[index]) + ".");
						return false;
					}
					player.runePouchItemAmount[index] += storeItemAmount;
					ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
					itemStoredCompleted = true;
					break;
				}
			}
		}
		if (!itemStoredCompleted) {
			for (int index = 0; index < player.runePouchItemId.length; index++) {
				if (player.runePouchItemId[index] == 0) {
					player.runePouchItemId[index] = storeItem;
					player.runePouchItemAmount[index] = storeItemAmount;
					ItemAssistant.deleteItemFromInventory(player, storeItem, storeItemSlot, storeItemAmount);
					break;
				}
			}
		}
		openRunePouch(player, false);
		return true;
	}

	public static boolean specificRuneInsideRunePouch(Player player, String action, int runeIdToSearchFor, int amountRequired) {
		for (int index = 0; index < player.runePouchItemId.length; index++) {
			if (player.runePouchItemId[index] == runeIdToSearchFor && player.runePouchItemAmount[index] >= amountRequired) {
				if (action.equals("DELETE")) {
					player.runePouchItemAmount[index] -= amountRequired;
					if (player.runePouchItemAmount[index] == 0) {
						player.runePouchItemId[index] = 0;
					}
					updateRunePouchMainStorage(player, false);
					return true;
				} else if (action.equals("CHECK")) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getAmountLeftToDeleteFromRunePouch(Player player, int itemId, int itemAmount) {
		for (int index = 0; index < player.runePouchItemId.length; index++) {
			if (player.runePouchItemId[index] == itemId && player.runePouchItemAmount[index] >= 1) {
				if (itemAmount > player.runePouchItemAmount[index]) {
					player.runePouchItemAmount[index] = 0;
					itemAmount -= player.runePouchItemAmount[index];
				} else {
					player.runePouchItemAmount[index] -= itemAmount;
					itemAmount = 0;
				}
				if (player.runePouchItemAmount[index] == 0) {
					player.runePouchItemId[index] = 0;
				}
				updateRunePouchMainStorage(player, false);
				return itemAmount;
			}
		}
		return itemAmount;
	}

	public static void dropRunePouchLoot(Player victim, Player killer) {
		for (int index = 0; index < victim.runePouchItemId.length; index++) {
			Server.itemHandler
					.createGroundItem(killer, victim.runePouchItemId[index], victim.getX(), victim.getY(), victim.getHeight(), victim.runePouchItemAmount[index], false, 0, true,
							"", "", "", "", "dropRunePouchLoot");
			victim.runePouchItemAmount[index] = 0;
			victim.runePouchItemId[index] = 0;
		}
	}

}
