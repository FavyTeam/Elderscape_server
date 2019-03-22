package game.content.miscellaneous;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * Pvp blacklist system that only works at Edgeville and West dragons.
 *
 * @author MGT Madness, created on 21-01-2017.
 */
public class PvpBlacklist {

	/**
	 * Display the Pvp blacklist interface.
	 *
	 * @param player
	 */
	public static void displayPvpBlacklistInterface(Player player) {
		for (int index = 0; index < player.pvpBlacklist.size(); index++) {
			player.getPA().sendFrame126(player.pvpBlacklist.get(index), 22707 + index);
		}

		player.getPA().displayInterface(22700);
	}

	/**
	 * Add a player to Pvp blacklist. Tell client to show the interface.
	 *
	 * @param player
	 */
	public static void openPvpBlacklistInputBox(Player player) {

		player.getPA().sendMessage(":packet:addpvpblacklist");
	}

	/**
	 * Add a name to Pvp blacklist.
	 *
	 * @param player
	 * @param string
	 */
	public static void addPvpBlacklist(Player player, String string, boolean warn) {
		String name = string.substring(15).toLowerCase();
		for (int index = 0; index < player.pvpBlacklist.size(); index++) {
			if (player.pvpBlacklist.get(index).equals(name)) {
				if (warn) {
					player.getPA().sendMessage(name + " already exists in your blacklist.");
				}
				return;
			}
		}
		player.getPA().sendMessage("You have added a player to the blacklist: " + ServerConstants.RED_COL + name + ".");
		player.pvpBlacklist.add(name);
		displayPvpBlacklistInterface(player);
	}

	/**
	 * True if this button belongs to the Pvp blacklist interface.
	 *
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean isPvpBlacklistButton(Player player, int buttonId) {
		if (buttonId == 88175) {
			openPvpBlacklistInputBox(player);
			return true;
		} else if (buttonId >= 88179 && buttonId <= 88220) {
			removePvpBlacklistUsername(player, buttonId);
			return true;
		}
		return false;
	}

	/**
	 * Remove a username from the Pvp blacklist.
	 *
	 * @param player
	 * @param buttonId
	 */
	private static void removePvpBlacklistUsername(Player player, int buttonId) {
		int buttonIndex = buttonId - 88179;
		if (buttonIndex > player.pvpBlacklist.size() - 1) {
			return;
		}
		InterfaceAssistant.clearFrames(player, 22707, 22756);
		player.pvpBlacklist.remove(buttonIndex);
		displayPvpBlacklistInterface(player);

	}

}
