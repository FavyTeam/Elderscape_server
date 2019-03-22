package game.content.miscellaneous;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.interfaces.InterfaceAssistant;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

public class PriceChecker {

	public static boolean isPriceCheckerButton(Player player, int buttonId) {

		switch (buttonId) {
			case 80025:
				for (int index = 0; index < player.playerItems.length; index++) {
					storeItem(player, player.playerItems[index] - 1, index, player.playerItemsN[index], true);
				}
				ItemAssistant.resetItems(player, 5064);
				updateChecker(player);
				return true;
		}
		return false;
	}

	public static void open(Player player) {
		if (!Bank.hasBankingRequirements(player, true)) {
			return;
		}
		updateChecker(player);
		ItemAssistant.resetItems(player, 5064);
		player.getPA().sendFrame248(24980, 5063);
	}

	private static void setFrame(Player player, int frameId, int itemId, int amount, boolean store) {
		long totalAmount = (long) ServerConstants.getItemValue(itemId) * (long) amount;
		String total = Misc.formatRunescapeStyle(totalAmount);
		if (ItemAssistant.isStackable(itemId)) {
			player.getPA().sendFrame126(amount + " x " + Misc.formatRunescapeStyle(ServerConstants.getItemValue(itemId)), frameId);
			player.getPA().sendFrame126("= " + total + "", frameId + 1);
		} else {
			player.getPA().sendFrame126(total, frameId);
			player.getPA().sendFrame126("", frameId + 1);
		}
	}

	private static void updateTotalPrice(Player player) {
		long total = 0;
		for (int index = 0; index < player.priceCheckerStoredItems.size(); index++) {
			GameItem instance = player.priceCheckerStoredItems.get(index);
			total += (double) ServerConstants.getItemValue(instance.getId()) * (double) instance.getAmount();
		}
		String string = Misc.formatRunescapeStyle(total);
		if (total == 0) {
			string = "";
		}
		player.getPA().sendFrame126(string, 20513);
	}

	public static void updateChecker(Player player) {
		double rows = (double) player.priceCheckerStoredItems.size() / 5.0;
		rows = Misc.getDoubleRoundedUp(rows);
		InterfaceAssistant.setFixedScrollMax(player, 20542, rows, 66, 230);
		for (int index = 0; index < 28; index++) {
			if (index > player.priceCheckerStoredItems.size() - 1) {
				player.getPA().sendFrame34(20543, -1, index, 0);
				player.getPA().sendFrame126("", 20550 + (index * 2));
				player.getPA().sendFrame126("", 20550 + (index * 2) + 1);
				continue;
			}
			GameItem instance = player.priceCheckerStoredItems.get(index);
			player.getPA().sendFrame34(20543, instance.getId(), index, instance.getAmount());
			setFrame(player, 20550 + (index * 2), instance.getId(), instance.getAmount(), true);
		}
		updateTotalPrice(player);
	}

	public static boolean storeItem(Player player, int itemId, int itemSlot, int amount, boolean isPriceCheckAllInventoryButton) {
		if (player.getInterfaceIdOpened() != 24980) {
			return false;
		}
		if (!Bank.hasBankingRequirements(player, true)) {
			return true;
		}
		if (ItemAssistant.isNulledSlot(itemSlot)) {
			return true;
		}
		if (ItemAssistant.nulledItem(itemId)) {
			return true;
		}
		if (!isPriceCheckAllInventoryButton) {
			int itemStockInInventory = ItemAssistant.getItemAmount(player, itemId);
			if (amount > itemStockInInventory) {
				amount = itemStockInInventory;
			}
			if (!ItemAssistant.hasItemInInventory(player, itemId)) {
				return true;
			}
		}

		boolean added = false;
		for (int index = 0; index < player.priceCheckerStoredItems.size(); index++) {
			GameItem instance = player.priceCheckerStoredItems.get(index);

			if (instance.getId() == itemId && ItemAssistant.isStackable(itemId)) {
				player.priceCheckerStoredItems.set(index, new GameItem(instance.getId(), instance.getAmount() + amount));
				added = true;
				break;
			}
		}
		if (!added) {
			if (amount > 1 && !ItemAssistant.isStackable(itemId)) {
				for (int i = 0; i < amount; i++) {
					player.priceCheckerStoredItems.add(new GameItem(itemId, 1));
				}
			} else {
				player.priceCheckerStoredItems.add(new GameItem(itemId, amount));
			}
		}

		ItemAssistant.deleteItemFromInventory(player, itemId, amount);
		if (!isPriceCheckAllInventoryButton) {
			ItemAssistant.resetItems(player, 5064);
			updateChecker(player);
		}
		return true;
	}

	public static void refundStoredItems(Player player) {
		if (player.getInterfaceIdOpened() != 24980) {
			return;
		}
		for (int index = 0; index < player.priceCheckerStoredItems.size(); index++) {
			GameItem instance = player.priceCheckerStoredItems.get(index);
			ItemAssistant.addItemToInventoryOrDrop(player, instance.getId(), instance.getAmount());
		}
		player.priceCheckerStoredItems.clear();
	}

	public static void removeItem(Player player, int removeId, int removeSlot, int amount) {
		if (player.getInterfaceIdOpened() != 24980) {
			return;
		}
		if (!Bank.hasBankingRequirements(player, true)) {
			return;
		}
		if (removeSlot > player.priceCheckerStoredItems.size() - 1) {
			return;
		}
		if (ItemAssistant.nulledItem(removeId)) {
			return;
		}
		GameItem instance = player.priceCheckerStoredItems.get(removeSlot);
		if (removeId != instance.getId()) {
			return;
		}
		int itemStockInInterface = 0;
		for (int index = 0; index < player.priceCheckerStoredItems.size(); index++) {
			GameItem loop = player.priceCheckerStoredItems.get(index);
			if (loop.getId() == removeId) {
				itemStockInInterface += loop.getAmount();
			}
		}
		if (amount > itemStockInInterface) {
			amount = itemStockInInterface;
		}
		if (ItemAssistant.isStackable(removeId)) {
			instance = new GameItem(instance.getId(), instance.getAmount() - amount);

			if (instance.getAmount() == 0) {
				player.priceCheckerStoredItems.remove(removeSlot);
			} else {
				player.priceCheckerStoredItems.set(removeSlot, instance);
			}
			ItemAssistant.addItemToInventoryOrDrop(player, removeId, amount);
		} else {
			for (int index = 0; index < player.priceCheckerStoredItems.size(); index++) {
				GameItem loop = player.priceCheckerStoredItems.get(index);
				if (loop.getId() == removeId) {
					amount--;
					ItemAssistant.addItemToInventoryOrDrop(player, removeId, 1);
					player.priceCheckerStoredItems.remove(index);
					index--;
				}
				if (amount == 0) {
					break;
				}
			}
		}
		ItemAssistant.resetItems(player, 5064);
		updateChecker(player);
	}

}
