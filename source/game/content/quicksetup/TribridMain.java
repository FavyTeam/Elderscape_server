package game.content.quicksetup;

import game.player.Player;

/**
 * Store the main tribrid equipment and inventory data.
 */
public class TribridMain {

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
						{9185, 1},
						{legSlotItem, 1},
						{8850, 1},
						{hideChapsInventorySlot, 1},
						{10499, 1},
						{385, 1},
						{1215, 1},
						{6685, 1},
						{2440, 1},
						{385, 1},
						{385, 1},
						{6685, 1},
						{2436, 1},
						{385, 1},
						{385, 1},
						{3024, 1},
						{2444, 1},
						{385, 1},
						{385, 1},
						{3024, 1},
						{555, 600},
						{385, 1},
						{385, 1},
						{560, 400},
						{565, 200}
				};


		return inventory;
	}

	public static int[][] equipmentSet(Player player) {

		int headSlotItem = 10828;

		int[][] equipment =
				{
						{headSlotItem, 1},
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
						{9244, 15}
				};
		return equipment;
	}
}
