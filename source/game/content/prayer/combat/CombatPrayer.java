package game.content.prayer.combat;

import game.npc.Npc;
import game.player.Player;

/**
 * Handles a prayer used in combat
 * 
 * @author 2012
 *
 */
public abstract class CombatPrayer {

	/**
	 * The prayer
	 */
	private int prayer;

	/**
	 * Represents the prayer
	 * 
	 * @param prayer
	 *            the prayer
	 */
	public CombatPrayer(int prayer) {
		this.prayer = prayer;
	}

	/**
	 * Gets the hit delay
	 * 
	 * @param player the player
	 * @param victim the victim
	 * @return the delay
	 */
	public int getHitDelay(Player player, Player victim) {
		int delay = 2;
		if (!player.playerAssistant.withinDistanceOfTargetPlayer(victim, 4)) {
			delay++;
		}
		if (!player.playerAssistant.withinDistanceOfTargetPlayer(victim, 6)) {
			delay++;
		}
		return delay;
	}

	/**
	 * Executing the prayer for player vs player
	 * 
	 * @param player the player
	 * @param victim the victim
	 * @param damage the damage
	 */
	public void execute(Player player, Player victim, int damage) {

	}

	/**
	 * Executing the prayer for player vs npc
	 * 
	 * @param player
	 *            the player
	 * @param victim
	 *            the victim
	 */
	public void execute(Player player, Npc victim) {

	}

	/**
	 * Handles deflecting for player vs player
	 * 
	 * @param player
	 *            the player
	 * @param victim
	 *            the victim
	 * @param hit
	 *            the hit
	 * @return the deflection
	 */
	public int getDeflect(Player player, Player victim, int hit) {
		return 0;
	}

	/**
	 * Handles deflecting for player vs npc
	 * 
	 * @param player
	 *            the player
	 * @param victim
	 *            the victim
	 * @param hit
	 *            the hit
	 * @return the deflection
	 */
	public int getDeflect(Player player, Npc victim, int hit) {
		return 0;
	}

	/**
	 * Gets the prayer
	 * 
	 * @return the prayer
	 */
	public int getPrayer() {
		return prayer;
	}
}
