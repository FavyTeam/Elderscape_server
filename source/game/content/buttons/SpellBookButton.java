package game.content.buttons;

import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.TeleportInterface;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Spellbook buttons.
 *
 * @author MGT Madness, created on 29-11-2013.
 */

public class SpellBookButton {

	/**
	 * Perform actions of the buttons in Note tab.
	 */
	public static boolean isSpellBookButton(Player player, int actionButtonId) {

		switch (actionButtonId) {

			// Home teleport
			case 117048:
			case 4171:
			case 50056:

				if (player.isInZombiesMinigame()) {
					return true;
				}
				// In Barrows chest area.
				if (Area.isInBarrowsChestArea(player)) {
					Teleport.spellTeleport(player, 3565, 3289, 0, false);
				} else {
					Teleport.spellTeleport(player, 3084 + Misc.random(5), 3490 + Misc.random(7), 0, false);
				}
				return true;

			// Wilderness Teleports.
			case 50235:
			case 4140:
			case 117112:
			case 118203:
			case 117131:
			case 118266:
			case 117210:
			case 118242:
			case 117154:
			case 117218:
			case 50245:
			case 50253:
			case 51031:
			case 51023:
			case 51013:
			case 51005:
			case 51039:
			case 4146:
			case 4143:
			case 4150:
			case 6004:
			case 6005:
			case 29031:
			case 117123:
			case 117162:
			case 117186:

				if (player.isInZombiesMinigame()) {
					return true;
				}
				player.canUseTeleportInterface = true;
				TeleportInterface.displayInterface(player);
				return true;
		}
		return false;
	}
}
