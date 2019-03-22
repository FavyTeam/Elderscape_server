package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Combine Godsword pieces to create a Godsword.
 *
 * @author MGT Madness, created on 13-02-2015.
 */
public class CombineGodsword {

	/**
	 * Combine Godsword shard pieces to create a Godsword blade.
	 *
	 * @param player The associated player.
	 */
	public static boolean createGodSwordBlade(Player player, int itemUsed, int itemUsedOn) {
		if (itemUsed != 11818 && itemUsed != 11820 && itemUsed != 11822) {
			return false;
		}
		if (!ItemAssistant.hasItemInInventory(player, 11818) || !ItemAssistant.hasItemInInventory(player, 11820) || !ItemAssistant.hasItemInInventory(player, 11822)) {
			return false;
		}
		ItemAssistant.deleteItemFromInventory(player, 11818, 1);
		ItemAssistant.deleteItemFromInventory(player, 11820, 1);
		ItemAssistant.deleteItemFromInventory(player, 11822, 1);
		ItemAssistant.addItem(player, 11798, 1);
		player.getDH().sendItemChat("", "You combine the shards and create a Godsword blade.", 11798, 200, 15, 0);
		return true;
	}

	/**
	 * Create the Godsword by using the appropriate hilt.
	 *
	 * @param player The associated player.
	 */
	public static boolean createGodSword(Player player, int itemUsed, int itemUsedOn) {
		if (itemUsed != 11798 && itemUsed != 11810 && itemUsed != 11812 && itemUsed != 11814 && itemUsed != 11816) {
			return false;
		}
		if (ItemAssistant.hasItemInInventory(player, 11798) && ItemAssistant.hasItemInInventory(player, 11810)) {
			ItemAssistant.deleteItemFromInventory(player, 11798, 1);
			ItemAssistant.deleteItemFromInventory(player, 11810, 1);
			ItemAssistant.addItem(player, 11802, 1);
			successfulGodSwordMessage(player, 11802);
			return true;
		}
		if (ItemAssistant.hasItemInInventory(player, 11798) && ItemAssistant.hasItemInInventory(player, 11812)) {
			ItemAssistant.deleteItemFromInventory(player, 11798, 1);
			ItemAssistant.deleteItemFromInventory(player, 11812, 1);
			ItemAssistant.addItem(player, 11804, 1);
			successfulGodSwordMessage(player, 11804);
			return true;
		}
		if (ItemAssistant.hasItemInInventory(player, 11798) && ItemAssistant.hasItemInInventory(player, 11814)) {
			ItemAssistant.deleteItemFromInventory(player, 11798, 1);
			ItemAssistant.deleteItemFromInventory(player, 11814, 1);
			ItemAssistant.addItem(player, 11806, 1);
			successfulGodSwordMessage(player, 11806);
			return true;
		}
		if (ItemAssistant.hasItemInInventory(player, 11798) && ItemAssistant.hasItemInInventory(player, 11816)) {
			ItemAssistant.deleteItemFromInventory(player, 11798, 1);
			ItemAssistant.deleteItemFromInventory(player, 11816, 1);
			ItemAssistant.addItem(player, 11808, 1);
			successfulGodSwordMessage(player, 11808);
			return true;
		}
		return false;
	}

	public static void successfulGodSwordMessage(Player player, int itemId) {
		player.getDH().sendItemChat("", "You combine the Godsword blade with the hilt.", itemId, 200, 0, -20);
	}

}
