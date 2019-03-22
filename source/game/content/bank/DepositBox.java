package game.content.bank;

import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;

/**
 * Deposit Box.
 *
 * @author MGT Madness, created on 04-11-2016.
 */
public class DepositBox {

	public static void depositItemAmount(Player player, int slot, int amount) {
		if (player.getActionIdUsed() != (15000 + 6948)) {
			return;
		}
		int inventoryItemId = player.playerItems[slot] - 1;
		if (inventoryItemId <= 0) {
			return;
		}
		int stockAmount = ItemDefinition.getDefinitions()[inventoryItemId].stackable ?
				                  ItemAssistant.getItemAmount(player, inventoryItemId, slot) :
				                  ItemAssistant.getItemAmount(player, inventoryItemId);

		// if amount == 0, then it means bank all.
		if (amount == 0) {
			if (ItemDefinition.getDefinitions()[inventoryItemId].stackable) {
				amount = player.playerItemsN[slot];
			} else {
				amount = stockAmount;
			}
		}

		if (amount > stockAmount) {
			amount = stockAmount;
		}
		if (amount <= 0) {
			return;
		}
		if (Bank.addItemToBank(player, inventoryItemId, amount, false)) {
			if (ItemDefinition.getDefinitions()[inventoryItemId].stackable) {
				ItemAssistant.deleteItemFromInventory(player, inventoryItemId, slot, amount);
			} else {
				ItemAssistant.deleteItemFromInventory(player, inventoryItemId, amount);
			}
		}
		ItemAssistant.resetItems(player, 7423);
	}
}
