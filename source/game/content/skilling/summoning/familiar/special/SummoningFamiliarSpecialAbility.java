package game.content.skilling.summoning.familiar.special;

import game.player.Player;

/**
 * Handles familiar special ability
 *
 * @author 2012
 */
public interface SummoningFamiliarSpecialAbility {

	/**
	 * Performing the special ability
	 *
	 * @param player the player
	 */
	public boolean sendScroll(Player player);

	/**
	 * The special ability cycle
	 *
	 * @param player the player
	 * @return whether there is a cycle at all
	 */
	public void cycle(Player player);

	/**
	 * Gets the skill bonus
	 *
	 * @param player the player
	 * @param skill the skill
	 * @return the bonsu
	 */
	public double getSkillBonus(Player player, int skill);

	/**
	 * Gets the special amount required
	 *
	 * @return the special amount
	 */
	public int getPercentageRequired();
}
