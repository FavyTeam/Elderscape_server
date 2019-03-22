package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Max cape combining.
 *
 * @author MGT Madness, created on 25-05-2017
 */
public class MaxCape {

	/**
	 * Combine the item with the max cape set.
	 *
	 * @param player
	 * @param itemUsedId
	 * @param itemUsedWithId
	 * @param itemConsumed
	 * @param capeReward
	 * @param hoodReward
	 * @param itemCombined
	 * @return
	 */
	public static boolean combineMaxCape(Player player, int itemUsedId, int itemUsedWithId, int itemConsumed, int capeReward, int hoodReward) {
		if (ItemAssistant.hasTwoItems(player, itemUsedId, itemUsedWithId, 13280, itemConsumed) || ItemAssistant
				                                                                                          .hasTwoItems(player, itemUsedId, itemUsedWithId, 13281, itemConsumed)) {
			boolean hood = ItemAssistant.hasItemInInventory(player, 13281);
			boolean cape = ItemAssistant.hasItemInInventory(player, 13280);
			if (cape && hood) {
				if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 13280, itemConsumed, capeReward,
				                                  "You combine the " + ItemAssistant.getItemName(itemConsumed) + " with the Max cape.", true, 0, -30)) {
					ItemAssistant.deleteItemFromInventory(player, 13281, 1);
					ItemAssistant.addItem(player, hoodReward, 1);
					return true;
				} else if (ItemAssistant.combineTwoItems(player, itemUsedId, itemUsedWithId, 13281, itemConsumed, capeReward,
				                                         "You combine the " + ItemAssistant.getItemName(itemConsumed) + " with the Max cape.", true, 0, -30)) {
					ItemAssistant.deleteItemFromInventory(player, 13280, 1);
					ItemAssistant.addItem(player, hoodReward, 1);
					return true;
				}
			} else {
				if (!cape) {
					player.getPA().sendMessage("You also need the Max cape to do the transformation.");
				}
				if (!hood) {
					player.getPA().sendMessage("You also need the Max hood to do the transformation.");
				}
			}
		}
		return false;
	}
}
