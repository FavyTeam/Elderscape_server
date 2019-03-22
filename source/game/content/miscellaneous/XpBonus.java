package game.content.miscellaneous;

import game.content.interfaces.InterfaceAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Xp bonus system where an interface shows on screen with countdown.
 * @author MGT Madness, created on 17-05-2018
 */
public class XpBonus {

	// Find a way to close the interface after xp bonus is complete.

	public final static double XP_BONUS_MULTIPLIER = 2.0;

	public static final int NEW_PLAYER_XP_BONUS_MINUTES = 60;

	public static int getNewExperienceIfOnXpBonus(Player player, int experience) {
		if (!XpBonus.xpBonusActive(player)) {
			return experience;
		}
		double experienceDouble = (double) experience;
		experienceDouble *= XP_BONUS_MULTIPLIER;
		return (int) Misc.getDoubleRoundedUp(experienceDouble);
	}

	public static void giveXpBonus(Player player, int minutes) {
		player.setXpBonusEndTime(System.currentTimeMillis() + Misc.getMinutesToMilliseconds(minutes));
		initiateXpBonusIfActive(player);
	}

	public static void initiateXpBonusIfActive(Player player) {
		if (!XpBonus.xpBonusActive(player)) {
			return;
		}
		enableXpBonusInterface(player);
		long secondsLeft = (player.getXpBonusEndTime() - System.currentTimeMillis()) / Misc.getSecondsToMilliseconds(1);
		InterfaceAssistant.setTextCountDownSecondsLeft(player, 21373, (int) secondsLeft);
	}

	private static boolean xpBonusActive(Player player) {
		if (System.currentTimeMillis() >= player.getXpBonusEndTime()) {
			return false;
		}
		return true;
	}

	public static void enableXpBonusInterface(Player player) {
		player.getPA().sendMessage(":packet:xpbonusinterfaceon");
	}

	public static void disableXpBonusInterface(Player player) {
		player.getPA().sendMessage(":packet:xpbonusinterfaceon");
	}

}
