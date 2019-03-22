package game.content.quicksetup;

import game.player.Player;

/**
 * Store the berserker hybrid equipment and inventory data.
 *
 * @author MGT Madness, created on 17-03-2015.
 */
public class BerserkerHybrid {

	/**
	 * Inventory items to spawn.
	 *
	 * @param player The associated player.
	 * @return The array of items.
	 */
	public static int[][] inventorySet(Player player) {

		int hideBodyInventorySlot = 2503;
		int hideChapsInventorySlot = 2497;
		int bodySlotItem = 1127;
		int legSlotItem = 1079;


		int[][] inventory =
				{
						{bodySlotItem, 1},
						{4587, 1},
						{hideBodyInventorySlot, 1},
						{565, 200},
						{legSlotItem, 1},
						{8850, 1},
						{hideChapsInventorySlot, 1},
						{560, 400},
						{2440, 1},
						{1215, 1},
						{2436, 1},
						{555, 600},
						{6685, 1},
						{3024, 1},
						{3024, 1},
						{3040, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1},
						{385, 1}
				};


		return inventory;
	}

	public static int[][] getEquipment() {
		int[][] equipment =
				{
						{3751, 1},
						{QuickSetUp.getRandomGodCape(), 1},
						{1712, 1},
						{4675, 1},
						{QuickSetUp.getRandomMysticTop(), 1},
						{3842, 1},
						{-1, 1},
						{QuickSetUp.getRandomMysticBottom(), 1},
						{-1, 1},
						{7461, 1},
						{3105, 1},
						{-1, 1},
						{-1, 1},
						{-1, 1}
				};

		return equipment;
	}

}
