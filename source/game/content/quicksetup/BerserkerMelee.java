package game.content.quicksetup;

import game.player.Player;

/**
 * Store the berserker melee equipment and inventory data.
 *
 * @author MGT Madness, created on 17-03-2015.
 */
public class BerserkerMelee {

	public static int[][] inventory =
			{
					{2440, 1},
					{2436, 1},
					{3024, 1},
					{557, 200},
					{6685, 1},
					{3024, 1},
					{3024, 1},
					{9075, 80},
					{385, 1},
					{385, 1},
					{385, 1},
					{560, 40},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{1215, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1},
					{385, 1}
			};

	public static int[][] equipmentSet(Player player) {
		int[][] equipment =
				{
						{3751, 1},
						{QuickSetUp.getTeamCape(false), 1},
						{1712, 1},
						{4587, 1},
						{1127, 1},
						{8850, 1},
						{-1, 1},
						{1079, 1},
						{-1, 1},
						{7461, 1},
						{3105, 1},
						{-1, 1},
						{-1, 1},
						{-1, 1}
				};
		return equipment;
	}

	public static int[] berserkerMeleeEquipmentSetModifications(Player player, int headSlotItem, int bodySlotItem, int legSlotItem) {
		int[] result = new int[3];
		result[0] = headSlotItem;
		result[1] = bodySlotItem;
		result[2] = legSlotItem;
		return result;
	}
}
