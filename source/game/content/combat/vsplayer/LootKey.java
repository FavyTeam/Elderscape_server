package game.content.combat.vsplayer;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import utility.Misc;

public class LootKey {
	public static void displayKeyLoot(Player player) {
		/*for (int index = 0; index < player.lootKey1ItemId.length; index++)
		{
			int item = player.lootKey1ItemId[index] == 0 ? -1 : player.lootKey1ItemId[index];
			player.getPA().sendFrame34(29106, item, index, player.lootKey1ItemAmount[index]);
		}*/
		for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
			int item = player.lootingBagStorageItemId[index] == 0 ? -1 : player.lootingBagStorageItemId[index];
			player.getPA().sendFrame34(29106, item, index, player.lootingBagStorageItemAmount[index]);
		}
		updateKeyValueText(player);
		player.getPA().displayInterface(29100);
	}

	private static void updateKeyValueText(Player player) {
		int value = 0;

		/*for (int index = 0; index < player.lootKey1ItemId.length; index++)
		{
			int item = player.lootKey1ItemId[index];
			int amount = player.lootKey1ItemAmount[index];
			if (item <= 0)
			{
				continue;
			}
			value += ServerConstants.getItemValue(item) * amount;
		}*/
		for (int index = 0; index < player.lootingBagStorageItemId.length; index++) {
			int item = player.lootingBagStorageItemId[index];
			int amount = player.lootingBagStorageItemAmount[index];
			if (item <= 0) {
				continue;
			}
			value += ServerConstants.getItemValue(item) * amount;
		}
		player.getPA().sendFrame126("Estimated loot value: " + Misc.formatNumber(value) + " " + ServerConstants.getMainCurrencyName(), 29107);

	}

	public static void bankLoot(Player player) {
		ItemAssistant.deleteItemFromInventory(player, 16140, 1);
		player.getPA().closeInterfaces(true);
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
		player.getPA().sendMessage("You have banked the loot. The key is destroyed in the process.");
	}
}
