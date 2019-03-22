package game.content.packet.preeoc;

import core.GameType;
import game.container.impl.MoneyPouch;
import game.content.prayer.PrayerManager;
import game.content.skilling.summoning.Summoning;
import game.player.Player;

/**
 * Handles button clicking for pre eoc
 * 
 * @author 2012
 *
 */
public class ClickButtonPreEoc {
	
	/**
	 * Handles clicking button for pre eoc
	 * 
	 * @param player the player
	 * @param buttonId the button
	 * @return clicking buttons
	 */
	public static boolean handleButtonPreEoc(Player player, int buttonId) {
		if(!GameType.isPreEoc()) {
			return false;
		}
		if (Summoning.handleButton(player, buttonId)) {
			return true;
		} else if (PrayerManager.handleButton(player, buttonId)) {
			return true;
		}
		switch (buttonId) {
			case 149_189:
				MoneyPouch.sendWithdraw(player);
				return true;
		}
		return false;
	}
}
