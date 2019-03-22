package game.object.areas;

import game.content.miscellaneous.GodCape;
import game.content.miscellaneous.Teleport;
import game.object.ObjectContent;
import game.player.Player;

/**
 * Mage's bank object features.
 *
 * @author MGT Madness, created on 31-01-2015.
 */
public class MageBank {
	/**
	 * @return true, if object is in Mage's bank.
	 */
	public static boolean isMageBankObject(final int objectType) {

		for (int i = 0; i < objects.length; i++) {
			if (objectType == objects[i]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Mage's bank object Identities.
	 */
	private static int[] objects =
			{5960, 2879, 2878, 2873, 2874, 2875,};

	/**
	 * Apply the actions of the requested object.
	 *
	 * @param player The associated player.
	 * @param objectID The object identity.
	 */
	public static void applyObjectAction(Player player, int objectID) {
		switch (objectID) {
			// Lever.
			case 5960:
				player.turnPlayerTo(player.getObjectX(), player.getObjectY() - 1);
				Teleport.startTeleport(player, 3090, 3956, 0, "LEVER");
				break;

			//Sparkling pool at magebank that leads to the surface
			case 2879:
				player.getPA().movePlayer(2542, 4718, 0);
				break;

			//Sparkling pool at Magebank.
			case 2878:
				ObjectContent.sparkingPool(player);
				break;

			case 2873:
				GodCape.giveGodCape(player, "saradomin cape");
				break;
			case 2875:
				GodCape.giveGodCape(player, "guthix cape");
				break;
			case 2874:
				GodCape.giveGodCape(player, "zamorak cape");
				break;
		}
	}

}
