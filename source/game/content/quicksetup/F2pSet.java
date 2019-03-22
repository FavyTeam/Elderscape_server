package game.content.quicksetup;

import game.player.Player;

/**
 * F2p melee and ranged set.
 *
 * @author MGT Madness, created on 18-12-2016.
 */
public class F2pSet {
	/**
	 * Inventory items to spawn.
	 *
	 * @param player The associated player.
	 * @return The array of items.
	 */
	public static int[][] getInventory(Player player, boolean ranged) {

		int[][] inventory =
				{
						{113, 1},
						{1319, 1},
						{ranged ? 1333 : 2297, 1},
						{ranged ? 373 : 2297, 1},
						{ranged ? 2297 : 2297, 1},
						{ranged ? 2297 : 2297, 1},
						{ranged ? 2297 : 373, 1},
						{ranged ? 2297 : 373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
				};


		return inventory;
	}

	public static int[][] getEquipment(boolean ranged) {
		int[][] equipment =
				{
						{-1, 1},
						{QuickSetUp.getTeamCape(false), 1},
						{1725, 1},
						{ranged ? 853 : 1333, 1},
						{544, 1},
						{-1, 1},
						{-1, 1},
						{542, 1},
						{-1, 1},
						{ranged ? 1065 : 1059, 1},
						{1061, 1},
						{-1, 1},
						{-1, 1},
						{ranged ? 890 : -1, ranged ? 80 : 0}
				};

		return equipment;
	}
}
