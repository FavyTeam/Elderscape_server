package game.object;

import game.player.Player;

/**
 * Methods of objects in other areas.
 *
 * @author MGT Madness, created on 19-01-2015.
 */
public class ObjectContent {

	/**
	 * Sparking pool at Mage bank on the top of the surface.
	 *
	 * @param player The associated player.
	 */
	public static void sparkingPool(final Player player) {
		player.getPA().movePlayer(2509, 4689, 0);
	}

}
