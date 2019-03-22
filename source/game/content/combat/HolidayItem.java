package game.content.combat;

import core.GameType;
import game.player.Player;

/**
 * Handle holiday/fun items.
 *
 * @author MGT Madness, created on 27-03-2015.
 */
public class HolidayItem {

	/**
	 * Handle the holiday item attack.
	 *
	 * @param attacker The player attacking.
	 * @param victim The player under attack.
	 * @return True, if the weapon is a holiday item.
	 */
	public static boolean isHolidayItem(Player attacker, Player victim) {
		switch (attacker.getWieldedWeapon()) {
			case 14728: // Easter carrot.
				if (GameType.isPreEoc()) {
					applyHolidayItemAction(attacker, victim, 11547);
				}
				return true;

			case 4566: // Rubber chicken
				applyHolidayItemAction(attacker, victim, 1833);
				return true;

			case 20590: // Stale baguette
				if (GameType.isOsrs()) {
					applyHolidayItemAction(attacker, victim, 1833);
				}
		}
		return false;
	}

	/**
	 * Apply the holiday item action.
	 *
	 * @param player The player initiating the attack.
	 * @param victim The player under attack.
	 * @param animation The animation id for the player to perform
	 */
	private static void applyHolidayItemAction(Player player, Player victim, int animation) {
		player.startAnimation(animation);
		Combat.resetPlayerAttack(player);
		player.faceUpdate(victim.getPlayerId() + 32768);
		player.setFaceResetAtEndOfTick(true);
	}

}
