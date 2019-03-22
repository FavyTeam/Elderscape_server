package game.content.godbook;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * God pages combining.
 *
 * @author MGT Madness, created on 20-02-2016.
 */
public class PageCombining {

	/**
	 * Page 1, page 2, page 3, page 4, book.
	 */
	private final static int[][] PAGES =
			{
					// Zamorak pages.
					{3831, 3832, 3833, 3834, 3842},

					// Saradomin pages.
					{3827, 3828, 3829, 3830, 3840},

					// Guthix pages.
					{3835, 3836, 3837, 3838, 3844}
			};

	/**
	 * @return True, if the item is a God page.
	 */
	public static boolean isPage(Player player, int itemUsed, int usedWith) {
		boolean hasPage = false;
		int pageIndex = 0;
		for (int index = 0; index < PAGES.length; index++) {
			for (int i = 0; i < 4; i++) {
				if (itemUsed == PAGES[index][i] || usedWith == PAGES[index][i]) {
					hasPage = true;
					pageIndex = index;
					break;
				}
			}
		}
		if (!hasPage) {
			return false;
		}

		for (int i = 0; i < 4; i++) {
			if (!ItemAssistant.hasItemInInventory(player, PAGES[pageIndex][i])) {
				player.playerAssistant.sendMessage("You are missing a page.");
				return true;
			}
		}

		for (int i = 0; i < 4; i++) {
			ItemAssistant.deleteItemFromInventory(player, PAGES[pageIndex][i], 1);
		}
		ItemAssistant.addItem(player, PAGES[pageIndex][4], 1);
		player.playerAssistant.sendMessage("You combine the pages and create the " + ItemAssistant.getItemName(PAGES[pageIndex][4]) + ".");



		return false;
	}

}
