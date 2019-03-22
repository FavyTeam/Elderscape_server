package game.content.skilling;

import core.GameType;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

public class BirdNests {
	@SuppressWarnings("unused")
	private static final int VERY_RARE = 1;

	@SuppressWarnings("unused")
	private static final int RARE = 5;

	@SuppressWarnings("unused")
	private static final int UNCOMMON = 40;

	private static final int COMMON = 75;

	private static final int ALWAYS = 100;

	private final static int[][] RING =
			{
					{1635, COMMON, 1},
					{1637, COMMON, 1},
					{1639, COMMON, 1},
					{1641, COMMON, 1},
					{1643, COMMON, 1},
					{1645, COMMON, 1}
			};

	private final static int[][] SEED =
			{
					{1635, COMMON, 1},
					{1637, COMMON, 1},
					{1639, COMMON, 1}
			};

	private final static int[][] EGG1 =
			{
					{5076, ALWAYS, 1}
			};

	private final static int[][] EGG2 =
			{
					{5078, ALWAYS, 1}
			};

	private final static int[][] EGG3 =
			{
					{5077, ALWAYS, 1}
			};

	private final static int[] RARE_RINGS =
			{6575, 2572, 20017, 20005};

	public static void NestTask(Player player, int[][] nest, int nestItemId, int itemSlot) {
		/**
		 * The random factor
		 */
		int random = Misc.random(nest.length - 1);
		/**
		 * The item amount
		 */
		int amount = nest[random][2];
		int itemId = nest[random][0];
		if (Misc.hasOneOutOf(5) && GameType.isOsrsEco() && nestItemId == 5074) {
			amount = 1;
			itemId = RARE_RINGS[Misc.random(RARE_RINGS.length - 1)];
			//Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has received a " + ItemAssistant.getItemName(nest[random][0]) + " from a Bird nest!");
		}
		if (amount < 1) {
			amount = 1;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getDH().sendStatement("You need at least 1 free inventory space to do this.");
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, nestItemId, itemSlot, 1);
		ItemAssistant.addItemToInventory(player, 5075, 1, itemSlot, true);
		ItemAssistant.addItem(player, itemId, amount);
		player.playerAssistant.sendFilterableMessage("You loot the bird nest and find " + amount + " " + ItemAssistant.getItemName(nest[random][0]) + ".");

	}

	public static boolean LootNest(Player p, int nestItemId, int itemSlot) {

		switch (nestItemId) {
			case 5070:
				NestTask(p, EGG1, nestItemId, itemSlot);
				return true;
			case 5071:
				NestTask(p, EGG2, nestItemId, itemSlot);
				return true;
			case 5072:
				NestTask(p, EGG3, nestItemId, itemSlot);
				return true;
			case 5073:
				NestTask(p, SEED, nestItemId, itemSlot);
				return true;
			case 5074:
				NestTask(p, RING, nestItemId, itemSlot);
				return true;
		}
		return false;
	}
}
