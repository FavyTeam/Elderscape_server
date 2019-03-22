package game.content.quicksetup;

import core.GameType;
import utility.Misc;

/**
 * Store the pure tribrid equipment and inventory data.
 *
 * @author MGT Madness, created on 17-03-2015.
 */
public class PureTribrid {

	public static int[][] inventory =
			{
					{9185, 1},
					{10499, 1},
					{555, 600},
					{2440, 1},
					{2497, 1},
					{1215, 1},
					{560, 400},
					{2436, 1},
					{4587, 1},
					{3024, 1},
					{565, 200},
					{2444, 1},
					{6685, 1},
					{3024, 1},
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
					{385, 1},
					{385, 1},
					{385, 1}
			};

	public static int[][] getEquipment() {


		int randomOutFitSet = Misc.random(GameType.isOsrs() ? Pure.robeAndHatSet.length - 1: Pure.robeAndHatSetPreEOC.length - 1);
		int[][] equipment =
				{
						{GameType.isOsrs() ? Pure.robeAndHatSet[randomOutFitSet][0] : Pure.robeAndHatSetPreEOC[randomOutFitSet][0], 1},
						{QuickSetUp.getRandomGodCape(), 1},
						{1712, 1},
						{4675, 1},
						{6107, 1},
						{3842, 1},
						{-1, 1},
						{6108, 1},
						{-1, 1},
						{7458, 1},
						{3105, 1},
						{-1, 1},
						{-1, 1},
						{9244, 15}
				};
		return equipment;
	}
}
