package game.content.packet.preeoc;

import core.GameType;
import game.player.Player;

/**
 * Handles clicking item for pre eoc
 * 
 * @author 2012
 *
 */
public class ClickItemPreEoc {
	/**
	 * Handles first click item for pre eoc
	 * 
	 * @param player the player
	 * @param itemId the item id
	 * @param slot the slot
	 * @return the item id
	 */
	public static boolean firstClickItemPreEoc(Player player, int itemId, int slot) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		switch (itemId) {
			case 18839:
				if (player.rigourUnlocked) {
					player.getPA().sendMessage("You cannot absorb anymore knowledge.");
					return true;
				}
				player.getDH().sendDialogues(633);
				return true;
			case 18344:
				if (player.auguryUnlocked) {
					player.getPA().sendMessage("You cannot absorb anymore knowledge.");
					return true;
				}
				player.getDH().sendDialogues(630);
				return true;
		}
		return false;
	}
}
