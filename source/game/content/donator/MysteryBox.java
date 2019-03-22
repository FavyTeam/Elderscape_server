package game.content.donator;

import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import utility.FileUtility;
import utility.Misc;

/**
 * Mystery box, CS:GO style.
 *
 * @author MGT Madness, created on 30-09-2017
 */
public class MysteryBox {
	public static ArrayList<String> mysteryBoxHistory = new ArrayList<String>();

	/**
	 * The chance of the rare items shown other than the winning item. Increase to decrease the rarity of rare items.
	 */
	private final static double VISUAL_CHANCE_MULTIPLIER = 2.5;

	/**
	 * Amount of items to send to the client.
	 */
	public final static int ITEMS_TO_SEND = 45;

	/**
	 * The item index of the winning item.
	 */
	public final static int WINNING_ITEM_INDEX = ITEMS_TO_SEND - 6;

	private static double MYSTERY_BOX_YELLOW_CHANCE;

	private static double MYSTERY_BOX_PURPLE_CHANCE;

	private static double MYSTERY_BOX_NORMAL_CHANCE;

	private static double SUPER_MYSTERY_BOX_YELLOW_CHANCE;

	private static double SUPER_MYSTERY_BOX_PURPLE_CHANCE;

	private static double SUPER_MYSTERY_BOX_NORMAL_CHANCE;

	private static double LEGENDARY_MYSTERY_BOX_YELLOW_CHANCE;

	private static double LEGENDARY_MYSTERY_BOX_PURPLE_CHANCE;

	private static double LEGENDARY_MYSTERY_BOX_NORMAL_CHANCE;

	private static double MEGA_MYSTERY_BOX_RED_CHANCE;

	private static double MEGA_MYSTERY_BOX_YELLOW_CHANCE;

	private static double MEGA_MYSTERY_BOX_PURPLE_CHANCE;

	private static double MEGA_MYSTERY_BOX_NORMAL_CHANCE;

	/**
	 * List of Mystery boxes from cheapest to most exepsive.
	 */
	public static enum MysteryBoxData {
		MYSTERY_BOX(16084),
		SUPER_MYSTERY_BOX(16086),
		LEGENDARY_MYSTERY_BOX(16088),
		MEGA_MYSTERY_BOX(16436);

		private int itemId;

		private MysteryBoxData(int itemId) {
			this.itemId = itemId;
		}

		public int getItemId() {
			return itemId;
		}
	}


	/**
	 * Store red tier items along with their y offset.
	 */
	public static ArrayList<String> redTierItems = new ArrayList<String>();

	/**
	 * Store yellow tier items along with their y offset.
	 */
	public static ArrayList<String> yellowTierItems = new ArrayList<String>();

	/**
	 * Store purple tier items along with their y offset.
	 */
	public static ArrayList<String> purpleTierItems = new ArrayList<String>();

	/**
	 * Store normal tier items along with their y offset.
	 */
	public static ArrayList<String> normalTierItems = new ArrayList<String>();

	/**
	 * Store low tier items along with their y offset.
	 */
	public static ArrayList<String> lowTierItems = new ArrayList<String>();

	/**
	 * @return True if it is a mystery box interface button.
	 */
	public static boolean isMysteryBoxButton(Player player, int buttonId) {
		switch (buttonId) {
			// Spin button
			case 103039:
				spinButton(player);
				return true;

			// Donate for more button
			case 103069:
				donateForMoreButton(player);
				return true;
		}
		return false;
	}

	/**
	 * @return True if the item is the Mystery box item id.
	 */
	public static boolean isMysteryBox(Player player, int itemId) {
		for (MysteryBoxData data : MysteryBoxData.values()) {
			if (data.getItemId() == itemId) {
				openMysteryBox(player, itemId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Donate for more mystery box button pressed.
	 */
	private static void donateForMoreButton(Player player) {
		player.getPA().openWebsite("www.dawntained.com/store", true);
	}

	/**
	 * Click the mystery box to display interface.
	 */
	private static void openMysteryBox(Player player, int itemId) {
		sendFirst9Items(player, itemId);
		player.getPA().displayInterface(26400);

	}

	/**
	 * Display the first 9 items.
	 */
	private static void sendFirst9Items(Player player, int itemId) {
		if (player.isUsedMysteryBox()) {
			return;
		}
		int count = 0;
		String data = "";
		ArrayList<String> allItemsCombined = getItemsDisplayed(getBoxUsed(player), 9, VISUAL_CHANCE_MULTIPLIER);
		for (int index = 0; index < 9; index++) {
			String itemData = "";
			itemData = allItemsCombined.get(Misc.random(allItemsCombined.size() - 1));
			count++;
			data = data + itemData + "#";
			if (count == 7) {
				player.getPA().sendMessage(":packet:latestboxarray9:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:latestboxarray9:" + data);
		}
		player.getPA().sendMessage(":packet:latestboxarrayend9:" + data);
	}

	/**
	 * Launch a cycle event which will trigger if the player logs off before the spin is complete, so they do not lose their winning item.
	 */
	private static void mysteryBoxEvent(Player player) {
		if (player.mysteryBoxEventForDc) {
			return;
		}
		player.mysteryBoxEventForDc = true;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (!player.isUsedMysteryBox()) {
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				player.mysteryBoxEventForDc = false;
				giveWinningItem(player);
			}
		}, 1);

	}

	/**
	 * True if the command used is a Mystery box command. This is sent automatically by the client.
	 */
	public static boolean isMysteryBoxCommand(Player player, String command) {
		if (command.equals("mysteryboxend")) {
			giveWinningItem(player);
			return true;
		}
		return false;
	}

	/**
	 * Award the player with the winning item.
	 */
	private static void giveWinningItem(Player player) {
		if (!player.isUsedMysteryBox()) {
			return;
		}
		mysteryBoxHistory.add(Misc.getDateAndTime() + ": " + player.getPlayerName() + ", reward added: " + ItemAssistant.getItemName(player.mysteryBoxWinningItemId));
		if (!ItemAssistant.addItem(player, player.mysteryBoxWinningItemId, 1)) {
			ItemAssistant.addItemReward(null, player.getPlayerName(), player.mysteryBoxWinningItemId, 1, false, -1);
		}
		if (player.announceMysteryBoxWinningItem) {
			String itemName = ItemAssistant.getItemName(player.mysteryBoxWinningItemId);
			Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has won " + Misc.getAorAn(itemName) + " from the " + ItemAssistant.getItemName(
					player.mysteryBoxItemIdUsed) + "!");
			player.getPA().sendScreenshot("mystery box " + itemName, 2);
		}
		player.setUsedMysteryBox(false);
		player.announceMysteryBoxWinningItem = false;
		player.mysteryBoxItemIdUsed = 0;

	}

	/**
	 * @param boxData The specific mystery box enum.
	 * @param amountRequested The amount of items to add into the arraylist.
	 * @param chanceVisualMultiplier Increase this to decrease the rarity of rare items.
	 * @return A String arraylist of the requested amount of mystery box items.
	 */
	public static ArrayList<String> getItemsDisplayed(MysteryBoxData boxData, int amountRequested, double chanceVisualMultiplier) {
		ArrayList<String> itemList = new ArrayList<String>();
		double chanceForRedTier = 0;
		double chanceForYellowTier = 0;
		double chanceForPurpleTier = 0;
		double chanceForNormalTier = 0;

		switch (boxData.toString()) {
			case "MYSTERY_BOX":
				chanceForYellowTier = MYSTERY_BOX_YELLOW_CHANCE;
				chanceForPurpleTier = MYSTERY_BOX_PURPLE_CHANCE;
				chanceForNormalTier = MYSTERY_BOX_NORMAL_CHANCE;
				break;
			case "SUPER_MYSTERY_BOX":
				chanceForYellowTier = SUPER_MYSTERY_BOX_YELLOW_CHANCE;
				chanceForPurpleTier = SUPER_MYSTERY_BOX_PURPLE_CHANCE;
				chanceForNormalTier = SUPER_MYSTERY_BOX_NORMAL_CHANCE;
				break;
			case "LEGENDARY_MYSTERY_BOX":
				chanceForYellowTier = LEGENDARY_MYSTERY_BOX_YELLOW_CHANCE;
				chanceForPurpleTier = LEGENDARY_MYSTERY_BOX_PURPLE_CHANCE;
				chanceForNormalTier = LEGENDARY_MYSTERY_BOX_NORMAL_CHANCE;
				break;
			case "MEGA_MYSTERY_BOX" :
				chanceForRedTier = MEGA_MYSTERY_BOX_RED_CHANCE;
				chanceForYellowTier = MEGA_MYSTERY_BOX_YELLOW_CHANCE;
				chanceForPurpleTier = MEGA_MYSTERY_BOX_PURPLE_CHANCE;
				chanceForNormalTier = MEGA_MYSTERY_BOX_NORMAL_CHANCE;
				break;
		}
		chanceForRedTier /= chanceVisualMultiplier;
		chanceForYellowTier /= chanceVisualMultiplier;
		chanceForPurpleTier /= chanceVisualMultiplier;
		chanceForNormalTier /= chanceVisualMultiplier;
		double redChanceBefore = chanceForRedTier;
		double yellowChanceBefore = chanceForYellowTier;
		double purpleChanceBefore = chanceForPurpleTier;
		double normalChanceBefore = chanceForNormalTier;
		for (int index = 0; index < amountRequested; index++) {
			// When sending 45 items to the client, show the rare items more often at the end of the spin.
			if (amountRequested == 45 && index == 35) {
				chanceForRedTier /= 2;
				chanceForYellowTier /= 2;
				chanceForPurpleTier /= 2;
				chanceForNormalTier /= 2;
			}

			// Reverse the rarity of the items at the end of the spin to what it was originally.
			else if (amountRequested == 45 && index == 42) {
				chanceForRedTier = redChanceBefore;
				chanceForYellowTier = yellowChanceBefore;
				chanceForPurpleTier = purpleChanceBefore;
				chanceForNormalTier = normalChanceBefore;
			}

			// If the amount of items requested is 45 (which is the normal spin) or 9 (which is the first 9 items shown after you open the mystery box
			// Then make sure their visual chance is never too low, so rare items do not look too common.
			if (amountRequested == 45 || amountRequested == 9) {
				if (chanceForYellowTier < 4) {
					chanceForYellowTier = 4;
				}
				if (chanceForPurpleTier < 3) {
					chanceForPurpleTier = 3;
				}
			}
			boolean given = false;
			if (chanceForRedTier > 0) {
				if (Misc.hasOneOutOf(chanceForRedTier)) {
					itemList.add(getRedTierItem(amountRequested == 1 ? true : false));
					given = true;
				}
			}
			if (given) {

			} else if (Misc.hasOneOutOf(chanceForYellowTier)) {
				itemList.add(getYellowTierItem(amountRequested == 1 ? true : false));
			} else if (Misc.hasOneOutOf(chanceForPurpleTier)) {
				itemList.add(purpleTierItems.get(Misc.random(purpleTierItems.size() - 1)));
			} else if (Misc.hasOneOutOf(chanceForNormalTier)) {
				itemList.add(normalTierItems.get(Misc.random(normalTierItems.size() - 1)));
			} else {
				itemList.add(lowTierItems.get(Misc.random(lowTierItems.size() - 1)));
			}
		}
		return itemList;
	}
	private static String getRedTierItem(boolean winningItem) {
		String item = "";
		if (winningItem) {
			item = redTierItems.get(Misc.random(redTierItems.size() - 1));
			if (Misc.hasPercentageChance(78)) {
				item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
			}
		} else {
			item = redTierItems.get(Misc.random(redTierItems.size() - 1));
		}
		return item;
	}

	/**
	 * @return The winning yellow tier item. This is just used to make Rainbow partyhats 10x more rarer than other yellow tier items.
	 */
	private static String getYellowTierItem(boolean winningItem) {
		String item = "";
		if (winningItem) {
			if (GameType.isOsrs()) {
				// This makes rainbow partyhat 10x more rarer.
				for (int index = 0; index < 2; index++) {
					item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
					// Rainbow partyhat
					if (!item.contains("11863")) {
						break;
					}
				}

				// Black partyhat is now 3x more rarer than normal partyhat
				if (item.contains("11862") && Misc.hasPercentageChance(78)) {
					item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
				}
				// Ghrazi rapier and Avernic defender
				else if (GameType.isOsrsPvp() && (item.contains("22322") || item.contains("22324")) && Misc.hasPercentageChance(95)) {
					item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
				}
				// Elysian spirit shield
				else if (GameType.isOsrsPvp() && item.contains("12817") && Misc.hasPercentageChance(95)) {
					item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
				}
			}
		} else {
			item = yellowTierItems.get(Misc.random(yellowTierItems.size() - 1));
		}
		return item;
	}

	/**
	 * Spin button pressed.
	 */
	private static void spinButton(Player player) {
		if (player.isUsedMysteryBox()) {
			return;
		}
		MysteryBoxData mysteryBoxData = getBoxUsed(player);
		if (mysteryBoxData == null) {
			player.getPA().sendMessage(ServerConstants.GREEN_COL_PLAIN + "You have run out of Mystery boxes, buy more to get rich! ::donate");
			return;
		}
		mysteryBoxHistory.add(Misc.getDateAndTime() + ": " + player.getPlayerName() + ", box deleted: " + ItemAssistant.getItemName(mysteryBoxData.getItemId()));
		ItemAssistant.deleteItemFromInventory(player, mysteryBoxData.getItemId(), 1);
		player.setUsedMysteryBox(true);
		int count = 0;
		String data = "";
		ArrayList<String> allItemsCombined = getItemsDisplayed(mysteryBoxData, 45, VISUAL_CHANCE_MULTIPLIER);
		String winningItemData = "";
		for (int index = 0; index < ITEMS_TO_SEND; index++) {
			String itemData = "";
			if (index == WINNING_ITEM_INDEX) {
				ArrayList<String> winningItem = getItemsDisplayed(mysteryBoxData, 1, 1);
				itemData = winningItem.get(0);
				String[] parse = itemData.split(" ");
				player.mysteryBoxWinningItemId = Integer.parseInt(parse[0]);
				player.mysteryBoxItemIdUsed = mysteryBoxData.getItemId();
				if (parse[2].contains("813") || parse[2].contains("812") || parse[2].contains("1293")) {
					player.announceMysteryBoxWinningItem = true;
				}
				winningItemData = itemData;
				mysteryBoxEvent(player);
			} else {
				itemData = allItemsCombined.get(index);
			}
			count++;
			data = data + itemData + "#";
			if (count == 7) {
				player.getPA().sendMessage(":packet:latestboxarray:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:latestboxarray:" + data);
		}
		String[] parseWin = winningItemData.split(" ");
		player.getPA().sendMessage(":packet:winningitemid " + parseWin[0] + " " + parseWin[1]);
		player.getPA().sendMessage(":packet:latestmysteryboxstart");
	}

	/**
	 * Scan the player's inventory for the lowest tier Mystery box to open.
	 */
	private static MysteryBoxData getBoxUsed(Player player) {
		for (MysteryBoxData data : MysteryBoxData.values()) {
			if (ItemAssistant.hasItemInInventory(player, data.getItemId())) {
				// Update Title of interface.
				player.getPA().sendFrame126(Misc.capitalize(data.toString()).replaceAll("_", " "), 26411);
				return data;
			}
		}
		return null;
	}

	/**
	 * Load the item tiers from text file.
	 */
	public static void loadMysteryBoxFiles() {
		redTierItems.clear();
		yellowTierItems.clear();
		purpleTierItems.clear();
		normalTierItems.clear();
		lowTierItems.clear();
		ArrayList<String> data = FileUtility.readFile(ServerConstants.getDataLocation() + "content/mystery_box.txt");

		String tier = "";
		for (int index = 0; index < data.size(); index++) {
			String string = data.get(index);

			if (string.startsWith("//") && string.endsWith("TIER")) {
				tier = string;
			} else {
				string = string.substring(0, string.indexOf("//") - 1);
				switch (tier) {
					case "// RED TIER" :
						redTierItems.add(string + " " + 1293);
						break;
					case "// YELLOW TIER":
						yellowTierItems.add(string + " " + 813);
						break;
					case "// PURPLE TIER":
						purpleTierItems.add(string + " " + 812);
						break;
					case "// NORMAL TIER":
						normalTierItems.add(string + " " + 806);
						break;
					case "// LOW TIER":
						lowTierItems.add(string + " " + 806);
						break;
				}
			}
		}
		try {
			FileInputStream config = new FileInputStream(ServerConstants.getDataLocation() + "content/mystery_box_config.txt");
			Properties property = new Properties();
			property.load(config);
			MYSTERY_BOX_YELLOW_CHANCE = Misc.readPropertyDouble(property, "MYSTERY_BOX_YELLOW_CHANCE");
			MYSTERY_BOX_PURPLE_CHANCE = Misc.readPropertyDouble(property, "MYSTERY_BOX_PURPLE_CHANCE");
			MYSTERY_BOX_NORMAL_CHANCE = Misc.readPropertyDouble(property, "MYSTERY_BOX_NORMAL_CHANCE");
			SUPER_MYSTERY_BOX_YELLOW_CHANCE = Misc.readPropertyDouble(property, "SUPER_MYSTERY_BOX_YELLOW_CHANCE");
			SUPER_MYSTERY_BOX_PURPLE_CHANCE = Misc.readPropertyDouble(property, "SUPER_MYSTERY_BOX_PURPLE_CHANCE");
			SUPER_MYSTERY_BOX_NORMAL_CHANCE = Misc.readPropertyDouble(property, "SUPER_MYSTERY_BOX_NORMAL_CHANCE");
			LEGENDARY_MYSTERY_BOX_YELLOW_CHANCE = Misc.readPropertyDouble(property, "LEGENDARY_MYSTERY_BOX_YELLOW_CHANCE");
			LEGENDARY_MYSTERY_BOX_PURPLE_CHANCE = Misc.readPropertyDouble(property, "LEGENDARY_MYSTERY_BOX_PURPLE_CHANCE");
			LEGENDARY_MYSTERY_BOX_NORMAL_CHANCE = Misc.readPropertyDouble(property, "LEGENDARY_MYSTERY_BOX_NORMAL_CHANCE");
			MEGA_MYSTERY_BOX_RED_CHANCE = Misc.readPropertyDouble(property, "MEGA_MYSTERY_BOX_RED_CHANCE");
			MEGA_MYSTERY_BOX_YELLOW_CHANCE = Misc.readPropertyDouble(property, "MEGA_MYSTERY_BOX_YELLOW_CHANCE");
			MEGA_MYSTERY_BOX_PURPLE_CHANCE = Misc.readPropertyDouble(property, "MEGA_MYSTERY_BOX_PURPLE_CHANCE");
			MEGA_MYSTERY_BOX_NORMAL_CHANCE = Misc.readPropertyDouble(property, "MEGA_MYSTERY_BOX_NORMAL_CHANCE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
