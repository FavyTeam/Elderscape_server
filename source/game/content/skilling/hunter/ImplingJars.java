package game.content.skilling.hunter;

import core.GameType;
import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Created by Owain on 06/08/2017.
 *
 * //TODO(Jason) Need to rewrite for quality assurance
 */
public class ImplingJars {

	/**
	 * The chance of a very rare item
	 */
	private static final int VERY_RARE = 10;

	/**
	 * The chance of a rare item
	 */
	private static final int RARE = 30;

	/**
	 * The chance of an uncommon item
	 */
	private static final int UNCOMMON = 40;

	/**
	 * The chance of a common item
	 */
	private static final int COMMON = 75;

	/**
	 * The baby impling jar items
	 */
	private final static int[][] BABY =
			{
					{1755, COMMON, 1},
					{1734, COMMON, 10},
					{1732, COMMON, 1},
					{946, COMMON, 1},
					{1985, COMMON, 1},
					{2347, COMMON, 1},
					{1760, COMMON, 5},
					{1927, COMMON, 1},
					{319, COMMON, 1},
					{2007, COMMON, 1},
					{1780, COMMON, 5},
					{7170, COMMON, 1},
					{402, COMMON, 10},
					{1438, COMMON, 1},
					{1608, COMMON, 5},
					{380, COMMON, 4},
					{1762, COMMON, 5},
			};

	/**
	 * The young impling jar items
	 */
	private final static int[][] YOUNG =
			{
					{855, UNCOMMON, 1},
					{1353, UNCOMMON, 1},
					{1097, RARE, 1},
					{1157, RARE, 1},
					{361, COMMON, 1},
					{1901, COMMON, 1},
					{2293, UNCOMMON, 1},
					{7178, UNCOMMON, 1},
					{247, COMMON, 1},
					{1784, COMMON, 1},
					{1523, COMMON, 1},
					{7937, UNCOMMON, 1},
					{5970, COMMON, 1},
					{453, UNCOMMON, 1},
					{1777, UNCOMMON, 1},
					{231, UNCOMMON, 1},
					{8779, RARE, 1},
					{133, RARE, 1},
					{2359, RARE, 1},
					{2347, UNCOMMON, 1},
			};

	/**
	 * The gourmet impling jar items
	 */
	private final static int[][] GOURMET =
			{
					{365, COMMON, 1},
					{361, COMMON, 1},
					{2011, COMMON, 1},
					{2327, COMMON, 1},
					{1897, COMMON, 1},
					{2293, COMMON, 1},
					{1883, UNCOMMON, 1},
					{247, UNCOMMON, 1},
					{380, UNCOMMON, 1},
					{386, UNCOMMON, 1},
					{7170, UNCOMMON, 1},
					{7179, RARE, 1},
					{374, RARE, 1},
					{2007, COMMON, 1},
					{5970, COMMON, 1},
					{2714, VERY_RARE, 1},
			};

	/**
	 * The earth impling jar items
	 */
	private static final int[][] EARTH =
			{
					{5535, COMMON, 1},
					{557, COMMON, 32},
					{6033, COMMON, 6},
					{1440, COMMON, 1},
					{5104, COMMON, 2},
					{2353, COMMON, 1},
					{444, COMMON, 1},
					{237, UNCOMMON, 1},
					{454, UNCOMMON, 6},
					{1784, UNCOMMON, 1},
					{1273, UNCOMMON, 1},
					{5311, UNCOMMON, 2},
					{5294, UNCOMMON, 2},
					{447, UNCOMMON, 1},
					{448, RARE, 3},
					{1606, RARE, 2},
					{1604, VERY_RARE, 1},
					{5303, VERY_RARE, 1},
			};

	/**
	 * The essence impling jar items
	 */
	private static final int[][] ESSENCE =
			{
					{562, COMMON, 4},
					{555, COMMON, 50},
					{558, COMMON, 35},
					{556, COMMON, 60},
					{559, COMMON, 50},
					{554, COMMON, 50},
					{564, COMMON, 4},
					{565, RARE, 7},
					{566, RARE, 11},
					{563, RARE, 13},
					{561, RARE, 13},
					{560, RARE, 13},
					{1442, RARE, 1},
					{1448, COMMON, 1},
					{4695, UNCOMMON, 4},
					{4696, UNCOMMON, 4},
					{4698, UNCOMMON, 4},
					{4697, RARE, 4},
					{4699, RARE, 4},
			};

	/**
	 * The eletric impling jar items
	 */
	private static final int[][] ELETRIC =
			{
					{1391, VERY_RARE, 1},
					{1273, COMMON, 1},
					{2493, RARE, 1},
					{1199, UNCOMMON, 1},
					{10083, RARE, 1},
					{1213, RARE, 1},
					{5970, COMMON, 1},
					{231, COMMON, 1},
					{444, COMMON, 1},
					{2358, UNCOMMON, 5},
					{450, RARE, 10},
					{556, COMMON, 41},
					{7936, UNCOMMON, 35},
					{237, UNCOMMON, 1},
					{1601, RARE, 1},
					{7208, RARE, 1},
					{8778, COMMON, 1},
					{5321, RARE, 1},
					{2802, VERY_RARE, 1},
			};

	/**
	 * The nature impling jar items
	 */
	private static final int[][] NATURE =
			{
					{5100, COMMON, 1},
					{5104, COMMON, 1},
					{5281, COMMON, 1},
					{5294, COMMON, 1},
					{5295, RARE, 1},
					{5297, UNCOMMON, 1},
					{5299, UNCOMMON, 1},
					{5303, VERY_RARE, 1},
					{5304, RARE, 1},
					{5313, UNCOMMON, 1},
					{5286, UNCOMMON, 1},
					{5285, UNCOMMON, 1},
					{3051, UNCOMMON, 1},
					{220, VERY_RARE, 1},
					{1513, COMMON, 1},
					{254, COMMON, 4},
					{270, VERY_RARE, 2},
			};

	/**
	 * The magpie impling jar items
	 */
	private static final int[][] MAGPIE =
			{
					{1682, COMMON, 3},
					{1732, COMMON, 3},
					{2569, COMMON, 3},
					{3391, COMMON, 1},
					{1333, UNCOMMON, 1},
					{1347, UNCOMMON, 1},
					{2571, UNCOMMON, 5},
					{4097, UNCOMMON, 1},
					{4095, UNCOMMON, 1},
					{1216, RARE, 6},
					{1185, RARE, 1},
			};

	/**
	 * The ninja impling jar items
	 */
	private static final int[][] NINJA =
			{
					{6328, COMMON, 1},
					{3385, COMMON, 1},
					{3391, COMMON, 1},
					{4097, COMMON, 1},
					{4095, COMMON, 1},
					{1113, UNCOMMON, 1},
					{3101, COMMON, 1},
					{1333, COMMON, 1},
					{1347, COMMON, 1},
					{5698, COMMON, 1},
					{892, COMMON, 70},
					{811, COMMON, 70},
					{868, COMMON, 40},
					{805, COMMON, 50},
					{9342, UNCOMMON, 2},
					{9194, RARE, 4},
			};

	/**
	 * The dragon impling jar items
	 */
	private static final int[][] DRAGON =
			{
					{4107, COMMON, 1},
					{4117, COMMON, 1},
					{1713, RARE, 3},
					{4099, RARE, 1},
					{4109, RARE, 1},
					{4095, RARE, 1},
					{4105, RARE, 1},
					{4101, VERY_RARE, 1},
					{4091, VERY_RARE, 1},
					{4093, VERY_RARE, 1},
					{4103, VERY_RARE, 1},
					{5547, VERY_RARE, 1},
					{11212, RARE, 20},
					{9244, COMMON, 8},
					{1305, COMMON, 1},
					{11230, UNCOMMON, 10},
					{5698, RARE, 1},
					{11232, COMMON, 150},
					{535, COMMON, 10},
					{537, RARE, 10},
					{1616, UNCOMMON, 6},
					{5300, RARE, 6},
					{7219, RARE, 15},
					{2740, VERY_RARE, 1},
			};

	/**
	 * Looting from impling jar
	 *
	 * @param player the player
	 * @param jar the jar
	 * @param impling the impling
	 */
	public static void lootJar(Player player, int[][] jar, int impling, int itemSlot) {
		/**
		 * The random factor
		 */
		int random = Misc.random(jar.length - 1);
		/**
		 * The item amount
		 */
		int amount = jar[random][2];

		//p.sendMessage("" + amount);
		/**
		 * Fixing amount
		 */
		if (amount < 1) {
			amount = 1;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getDH().sendStatement("You need at least 1 free inventory space to do this.");
			return;
		}
		/**
		 * Deletes the jar
		 */
		ItemAssistant.deleteItemFromInventory(player, impling, itemSlot, 1);
		/**
		 * The chance
		 */
		if (Misc.random(10) < jar[random][1]) {
			ItemAssistant.addItemToInventory(player, jar[random][0], amount, itemSlot, true);
			ItemAssistant.addItem(player, 11260, 1);
			player.playerAssistant.sendFilterableMessage(
					"You loot the " + ItemAssistant.getItemName(impling).toLowerCase() + " and receive " + amount + " " + ItemAssistant.getItemName(jar[random][0]).toLowerCase()
					+ ".");
		} else {
			ItemAssistant.addItem(player, ServerConstants.getMainCurrencyId(), GameType.isOsrsPvp() ? Misc.random(500) + 100 : Misc.random(375000) + 75000);
			player.playerAssistant.sendFilterableMessage(
					"You loot the " + ItemAssistant.getItemName(impling).toLowerCase() + " and receive some " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
		}
		//player.getItems().updateInventory();
	}

	/**
	 * Opening impling jar
	 *
	 * @param p the player
	 * @param jar the jar
	 */
	public static boolean openJar(Player p, int jar, int itemSlot) {
		/**
		 * Item doesn't exist
		 */
		//if (!p.getItems().playerHasItem(jar))
		//return false;

		/**
		 * The different jars
		 */
		switch (jar) {
			case 11238:
				lootJar(p, BABY, jar, itemSlot);
				return true;
			case 11240:
				lootJar(p, YOUNG, jar, itemSlot);
				return true;
			case 11242:
				lootJar(p, GOURMET, jar, itemSlot);
				return true;
			case 11244:
				lootJar(p, EARTH, jar, itemSlot);
				return true;
			case 11246:
				lootJar(p, ESSENCE, jar, itemSlot);
				return true;
			case 11248:
				lootJar(p, ELETRIC, jar, itemSlot);
				return true;
			case 11250:
				lootJar(p, NATURE, jar, itemSlot);
				return true;
			case 11252:
				lootJar(p, MAGPIE, jar, itemSlot);
				return true;
			case 11254:
				lootJar(p, NINJA, jar, itemSlot);
				return true;
			case 11256:
				lootJar(p, DRAGON, jar, itemSlot);
				return true;
		}
		return false;
	}
}
