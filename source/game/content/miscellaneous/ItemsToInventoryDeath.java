package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Items sent to inventory after death.
 *
 * @author MGT Madness, created on 08-04-2014.
 */
public class ItemsToInventoryDeath {

	/**
	 * Add the items kept on death to the player's inventory.
	 */
	public static void addItemsAfterDeath(Player player) {
		for (int index = 0; index < player.itemsToInventory.size(); index++) {
			String[] args = player.itemsToInventory.get(index).split(" ");
			int itemId = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			boolean itemReturned = false;

			/*
			if (Config.ECO) {
				for (final Untradeables data : Untradeables.values()) {
					if (itemId != data.getFixedId()) {
						continue;
					}
					itemId = data.getBrokenId();
					ItemAssistant.addItem(player, itemId, amount);
					itemReturned = true;
					break;
				}
			}
			*/
			if (!itemReturned) {
				ItemAssistant.addItem(player, itemId, amount);
			}
		}
		player.itemsToInventory.clear();
	}


}
