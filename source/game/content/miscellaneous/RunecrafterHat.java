package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Runecrafter hat goggle switching.
 *
 * @author MGT Madness, created on 29-09-2015.
 */
public class RunecrafterHat {

	private static int[][] RUNECRAFTER_HAT_SET =
			{
					{13626, 13625
							// Blue
					},
					{13616, 13615
							// Yellow
					},
					{13613, 13620
							// Green
					},
			};

	/**
	 * True, if the itemId given, matches any of the Runecrafter hats.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity to find a match for.
	 * @return True, if the itemId is a Runecrafter hat.
	 */
	public static boolean isRunecrafterHat(Player player, int itemId) {
		for (int i = 0; i < RUNECRAFTER_HAT_SET.length; i++) {
			if (itemId == RUNECRAFTER_HAT_SET[i][0]) {
				int slot = ItemAssistant.getItemSlot(player, itemId);
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				ItemAssistant.addItemToInventory(player, RUNECRAFTER_HAT_SET[i][1], 1, slot, true);
				return true;
			}
		}
		for (int i = 0; i < RUNECRAFTER_HAT_SET.length; i++) {
			if (itemId == RUNECRAFTER_HAT_SET[i][1]) {
				int slot = ItemAssistant.getItemSlot(player, itemId);
				ItemAssistant.deleteItemFromInventory(player, itemId, 1);
				ItemAssistant.addItemToInventory(player, RUNECRAFTER_HAT_SET[i][0], 1, slot, true);
				return true;
			}
		}
		return false;
	}

}
