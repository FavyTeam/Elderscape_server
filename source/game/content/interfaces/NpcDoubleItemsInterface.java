package game.content.interfaces;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.donator.MysteryBox;
import game.content.interfaces.donator.DonatorShop;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.Announcement;
import game.content.starter.GameMode;
import game.entity.Entity;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.punishment.DuelArenaBan;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import network.sql.SQLConstants;
import network.sql.SQLMethods;
import network.sql.SQLNetwork;
import network.sql.query.impl.IntParameter;
import network.sql.query.impl.LongParameter;
import network.sql.query.impl.StringParameter;
import utility.FileUtility;
import utility.Misc;

/**
 * Npc double items interface.
 * @author MGT Madness, created on 08-08-2018
 */
public class NpcDoubleItemsInterface {

	public final static int NPC_ID = 5417;

	private final static double CHANCE_FOR_X_3 = 60;
	
	private final static double CHANCE_FOR_X_3_VISUAL = 16;
	
	private final static double CHANCE_FOR_X_2 = 2.35;
	
	private final static double CHANCE_FOR_X_2_AT_HIGHER_WEALTH = 2.35;

	private final static double CHANCE_FOR_X_2_VISUAL = 1.80;

	private final static int BANK_TO_ROLL = 500_000;

	private static int getMinimumItemValue() {
		return GameType.isOsrsEco() ? 4_000_000 : 10_000;
	}

	private static long getItemAnnounceAmount() {
		return GameType.isOsrsPvp() ? 50_000 : 35_000_000;
	}

	private final static String[] DIALOGUES = {
			"Talk to me to double your items!",
			"Have a try at doubling your items over here!",
			"Want to get rich? Try your luck and double your items.",
			"Let me double your items today!",
	};

	private final static int[][] BAIT_ITEMS = {
			{11832, 10, 811}, 
			{13307 + 9, 300_000, 1288}, 
			{1044, 2, 811}, 
			{16088, 6, 1288}, 
			{13307 + 9, 900_000, 1288}, 
			{11802, 4, 811}, 
			{21003, 2, 811}, 
			{6585, 20, 811}, 
			{11828, 2, 811}, 
	};


	private final static int INTERFACE_ID = 26702;

	public final static int INVENTORY_OVERLAY_ITEMS_ID = 25585;

	private final static int INVENTORY_OVERLAY_ID = 25584;

	private final static int ANNOUNCE_MINUTES_DELAY = 5;

	public final static String FILE_LOCATION_LOG_OF_ITEM_SINK = "backup/logs/double_items_sink.txt";

	public final static String FILE_LOCATION_OF_AMOUNT_SUNK_AMOUNT_TODAY = "backup/logs/double_items_sink_today_amount.txt";

	/*
	  CREATE TABLE `stats_double_items_npc_daily` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`date` varchar(45) DEFAULT NULL,
	`sunk` varchar(45) DEFAULT NULL,
	`sunk_raw` bigint(20) DEFAULT NULL,
	PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
	
	CREATE TABLE `stats_payment_daily_total` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`date` varchar(255) NOT NULL,
	`total` int(11) NOT NULL,
	`bmt_total` int(11) NOT NULL,
	`osrs_total` int(11) NOT NULL,
	`custom_total` int(11) NOT NULL,
	PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
	
	
	 */

	public static long amountSunkFromEconomyToday;

	private static long timeAnnounced;

	public static class DoubleItemsLog {

		public static List<DoubleItemsLog> doubleItemsLog = new ArrayList<DoubleItemsLog>();

		private int itemId;

		/**
		 * If its positive, it means we sunk items from economy. if its negative, it means we spawned into the economy.
		 */
		private long amountSunkFromEconomy;

		private String itemName;

		public String getItemName() {
			return itemName;
		}

		private int getItemId() {
			return itemId;
		}

		private long getAmountSunkFromEconomy() {
			return amountSunkFromEconomy;
		}

		private void setAmountSunkFromEconomy(long amount) {
			amountSunkFromEconomy = amount;
		}

		public DoubleItemsLog(int itemId, long amountSunkFromEconomy, String itemName) {
			this.itemId = itemId;
			this.amountSunkFromEconomy = amountSunkFromEconomy;
			this.itemName = itemName;
		}

		public static void addToLog(int itemId, long amountSunkFromEconomy) {

			long temporary = amountSunkFromEconomy;
			if (temporary < 0) {
				temporary = -temporary;
			}
			long value = ServerConstants.getItemValue(itemId) * temporary;
			if (amountSunkFromEconomy < 0) {
				value = -value;
			}
			amountSunkFromEconomyToday += value;
			itemId = ItemAssistant.getUnNotedItem(itemId);
			for (int index = 0; index < doubleItemsLog.size(); index++) {
				DoubleItemsLog instance = doubleItemsLog.get(index);
				if (instance.getItemId() == itemId) {
					instance.setAmountSunkFromEconomy(instance.getAmountSunkFromEconomy() + amountSunkFromEconomy);
					if (instance.getAmountSunkFromEconomy() == 0) {
						doubleItemsLog.remove(index);
					}
					return;
				}
			}
			doubleItemsLog.add(new DoubleItemsLog(itemId, amountSunkFromEconomy, ItemAssistant.getItemName(itemId)));
		}

		public static void readLog() {
			if (!FileUtility.fileExists(FILE_LOCATION_LOG_OF_ITEM_SINK)) {
				return;
			}
			amountSunkFromEconomyToday = Long.parseLong(FileUtility.readFirstLine(FILE_LOCATION_OF_AMOUNT_SUNK_AMOUNT_TODAY));
			ArrayList<String> arraylist = FileUtility.readFile(FILE_LOCATION_LOG_OF_ITEM_SINK);
			for (int index = 0; index < arraylist.size(); index++) {
				String line = arraylist.get(index);
				if (line.startsWith("----")) {
					continue;
				}
				String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
				doubleItemsLog.add(new DoubleItemsLog(Integer.parseInt(parse[0]), Integer.parseInt(parse[1]), parse[2]));
			}
		}

		public static void saveLog() {

			FileUtility.deleteAllLines(FILE_LOCATION_OF_AMOUNT_SUNK_AMOUNT_TODAY);
			FileUtility.addLineOnTxt(FILE_LOCATION_OF_AMOUNT_SUNK_AMOUNT_TODAY, amountSunkFromEconomyToday + "");
			Collections.sort(doubleItemsLog, new Comparator<DoubleItemsLog>() {
				public int compare(DoubleItemsLog firstValue, DoubleItemsLog secondValue) {
					long firstValueAmountPositive = firstValue.getAmountSunkFromEconomy();
					if (firstValueAmountPositive < 0) {
						firstValueAmountPositive = -firstValueAmountPositive;
					}
					long secondValueAmountPositive = secondValue.getAmountSunkFromEconomy();
					if (secondValueAmountPositive < 0) {
						secondValueAmountPositive = -secondValueAmountPositive;
					}

					long firstValueTotal = ServerConstants.getItemValue(firstValue.getItemId()) * firstValueAmountPositive;
					boolean firstValueSpawnedIntoEconomy = firstValue.getAmountSunkFromEconomy() < 0;

					long secondValueTotal = ServerConstants.getItemValue(secondValue.getItemId()) * secondValueAmountPositive;
					boolean secondValueSpawnedIntoEconomy = secondValue.getAmountSunkFromEconomy() < 0;

					if (firstValueSpawnedIntoEconomy && !secondValueSpawnedIntoEconomy) {
						return 1;
					}
					if (!firstValueSpawnedIntoEconomy && secondValueSpawnedIntoEconomy) {
						return -1;
					}
					if (firstValueSpawnedIntoEconomy && secondValueSpawnedIntoEconomy) {
						if (firstValueTotal < secondValueTotal) {
							return 1;
						}
						if (firstValueTotal > secondValueTotal) {
							return -1;
						}
						return 0;
					}
					if (firstValueTotal > secondValueTotal) {
						return -1;
					}
					if (firstValueTotal == secondValueTotal) {
						return 0;
					}
					if (firstValueTotal < secondValueTotal) {
						return 1;
					}
					return 0;
				}
			});

			Server.getSqlNetwork().submit(connection -> {
				try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_DOUBLE_ITEMS_NPC))) {
					statement.executeUpdate();
				}
			});
			FileUtility.deleteAllLines(FILE_LOCATION_LOG_OF_ITEM_SINK);
			ArrayList<String> lines = new ArrayList<String>();
			boolean lineDrawn = false;
			for (int index = 0; index < doubleItemsLog.size(); index++) {
				DoubleItemsLog instance = doubleItemsLog.get(index);
				if (instance.getAmountSunkFromEconomy() < 0 && !lineDrawn) {
					lineDrawn = true;
					lines.add("-------");
				}
				SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_DOUBLE_ITEMS_NPC) + " (item_id, amount_sunk_from_economy, item_name, amount_sunk_from_economy_string) VALUES(?, ?, ?, ?)", new IntParameter(1, instance.getItemId()), new LongParameter(2, instance.getAmountSunkFromEconomy()), new StringParameter(3, instance.getItemName()), new StringParameter(4, Misc.formatRunescapeStyle(instance.getAmountSunkFromEconomy())));
				lines.add(instance.getItemId() + ServerConstants.TEXT_SEPERATOR + instance.getAmountSunkFromEconomy() + ServerConstants.TEXT_SEPERATOR + instance.getItemName());
			}
			FileUtility.saveArrayContentsSilent(FILE_LOCATION_LOG_OF_ITEM_SINK, lines);

		}
	}

	public static boolean button(Player player, int buttonId) {

		if (player.getInterfaceIdOpened() != INTERFACE_ID) {
			return false;
		}
		switch (buttonId) {

			// Roll!
			case 104085 :
				roll(player);
				return true;
			// Donate for more items.
			case 104123 :
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				DonatorShop.openLastDonatorShopTab(player);
				return true;

			// Open bank
			case 104128 :
				if (!Bank.hasBankingRequirements(player, true)) {
					return true;
				}
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
				return true;
		}
		return false;
	}

	private static void roll(Player player) {
		if (player.isUsedMysteryBox()) {
			return;
		}
		if (ItemAssistant.getAccountBankValueLongWithDelay(player) <= BANK_TO_ROLL) {
			player.getPA().sendMessage("You must have a bank of at least " + Misc.formatRunescapeStyle(BANK_TO_ROLL) + " " + ServerConstants.getMainCurrencyName() + ".");
			return;
		}
		if (DuelArenaBan.isDuelBanned(player, true)) {
			return;
		}
		if (player.npcDoubleItemsInterfaceStoredItems.isEmpty()) {
			player.getPA().sendMessage("Your item gamble is empty! Please select an item from your inventory.");
			return;
		}
		int itemIdToGamble = player.npcDoubleItemsInterfaceStoredItems.get(0).getId();
		int itemAmountToGamble = player.npcDoubleItemsInterfaceStoredItems.get(0).getAmount();
		if (ServerConstants.getItemValue(itemIdToGamble) * itemAmountToGamble < getMinimumItemValue()) {
			player.getPA().sendMessage("The minimum gamble value is " + Misc.formatNumber(getMinimumItemValue()) + " " + ServerConstants.getMainCurrencyName() + ".");
			player.getPA().sendMessage("Your current gamble is worth " + Misc.formatNumber(ServerConstants.getItemValue(itemIdToGamble) * itemAmountToGamble) + " " + ServerConstants.getMainCurrencyName());
			return;
		}
		int maxGambleAmount = Integer.MAX_VALUE / 3;
		if (itemAmountToGamble > maxGambleAmount) {
			player.getPA().sendMessage("You cannot gamble more than " + Misc.formatNumber(maxGambleAmount) + " of an item.");
			return;
		}
		if (!ItemAssistant.hasItemAmountInInventory(player, itemIdToGamble, itemAmountToGamble)) {
			player.getPA().sendMessage("You do not have enough of this item.");
			return;
		}
		SQLMethods.insertStatsDoubleItemsNpcPlayer(player.getPlayerName(), ItemAssistant.getAccountBankValueLongWithDelay(player));
		player.getPA().sendMessage("You have gambled x" + Misc.formatRunescapeStyle(itemAmountToGamble) + " " + ItemAssistant.getItemName(itemIdToGamble) + ".");
		player.gambleNpcItemUsedTracker = itemIdToGamble;
		player.gambleNpcItemAmountUsedTracker = itemAmountToGamble;
		ItemAssistant.deleteItemFromInventory(player, itemIdToGamble, itemAmountToGamble);
		ItemAssistant.resetItems(player, INVENTORY_OVERLAY_ITEMS_ID);
		player.setUsedMysteryBox(true);
		player.npcDoubleItemsInterfaceStoredItems.clear();
		player.getPA().sendFrame126("Prize:", 26746);
		player.mysteryBoxWinningItemId = 0;
		player.gambleNpcWinningItemIdAmount = 0;
		int count = 0;
		String data = "";
		ArrayList<String> allItemsCombined = getItemsDisplayed(itemIdToGamble, itemAmountToGamble, 45, true);
		String winningItemData = "";
		for (int index = 0; index < MysteryBox.ITEMS_TO_SEND; index++) {
			String itemData = "";
			if (index == MysteryBox.WINNING_ITEM_INDEX) {
				ArrayList<String> winningItem = getItemsDisplayed(itemIdToGamble, itemAmountToGamble, 1, false);
				itemData = winningItem.get(0);
				String[] parse = itemData.split(" ");
				player.mysteryBoxWinningItemId = Integer.parseInt(parse[0]);
				player.gambleNpcWinningItemIdAmount = (int) winningItemAmount;
				if (ServerConstants.getMainCurrencyId() + 9 == player.mysteryBoxWinningItemId) {
					player.mysteryBoxWinningItemId -= 9;
				}
				winningItemData = itemData;
				gambleNpcEvent(player);
			} else {
				itemData = allItemsCombined.get(index);
			}
			count++;
			data = data + itemData + "#";
			if (count == 7) {
				player.getPA().sendMessage(":packet:latestgamblearray:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:latestgamblearray:" + data);
		}
		String[] parseWin = winningItemData.split(" ");
		String amount = winningItemAmount > 0 ? ("x" + Misc.formatRunescapeStyle(winningItemAmount)) : "";
		player.getPA().sendMessage(":packet:gamblewinningitemid " + amount);
		player.getPA().sendMessage(":packet:winningitemid " + parseWin[0] + " " + 10);
		player.getPA().sendMessage(":packet:latestgamblestart");
		player.getPA().sendFrame126Specific("", 26756, false);
	}

	private static void gambleNpcEvent(Player player) {
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

	private static void giveWinningItem(Player player) {
		if (!player.isUsedMysteryBox()) {
			return;
		}
		player.bankWealthCheckedTime = 0;
		if (player.mysteryBoxWinningItemId > 0) {
			String itemName = ItemAssistant.getItemName(player.mysteryBoxWinningItemId);
			Npc gambler = NpcHandler.getNpcByNpcIndex(npcArrayIndex);

			gambler.requestAnimation(Lottery.EMOTES[Misc.random(Lottery.EMOTES.length - 1)]);
			gambler.forceChat(player.getPlayerName() + " has just won x" + Misc.formatRunescapeStyle(player.gambleNpcWinningItemIdAmount) + " " + itemName + "!");
			ItemAssistant.addItemToInventoryOrDrop(player, player.mysteryBoxWinningItemId, player.gambleNpcWinningItemIdAmount);
			ItemAssistant.resetItems(player, INVENTORY_OVERLAY_ITEMS_ID);
			long gained = ServerConstants.getItemValue(player.gambleNpcItemUsedTracker) * (player.gambleNpcWinningItemIdAmount - player.gambleNpcItemAmountUsedTracker);
			if (gained >= getItemAnnounceAmount()) {
				if (Misc.timeElapsed(timeAnnounced, Misc.getMinutesToMilliseconds(ANNOUNCE_MINUTES_DELAY))) {
					timeAnnounced = System.currentTimeMillis();
					Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(player) + " has won x" + Misc.formatRunescapeStyle(player.gambleNpcWinningItemIdAmount) + " " + itemName + " from the Npc item doubler at ::doubler");
				}
				player.getPA().sendScreenshot("npc double items " + itemName, 2);
			}
			player.getPA().sendMessage("You have won x" + Misc.formatRunescapeStyle(player.gambleNpcWinningItemIdAmount) + " " + ItemAssistant.getItemName(player.mysteryBoxWinningItemId) + "!");
		}
		else {
			player.getPA().sendMessage("You have lost the gamble.");
		}
		if (player.mysteryBoxWinningItemId > 0) {
			player.gambleNpcItemAmountUsedTracker = -(player.gambleNpcWinningItemIdAmount - player.gambleNpcItemAmountUsedTracker);
		}
		DoubleItemsLog.addToLog(player.gambleNpcItemUsedTracker, player.gambleNpcItemAmountUsedTracker);
		player.setUsedMysteryBox(false);
		player.gambleNpcItemAmountUsedTracker = 0;
		player.gambleNpcItemUsedTracker = 0;
		player.announceMysteryBoxWinningItem = false;
		player.mysteryBoxItemIdUsed = 0;
		player.gambleNpcWinningItemIdAmount = 0;
	}

	public static void displayInterface(Player player) {
		if (player.isUsedMysteryBox()) {
			return;
		}
		player.getPA().sendMessage(":packet:gambleresetinterface");
		// Show the first 9 items as static, random set of items to persuade player to roll
		sendFirst9Items(player);
		player.getPA().sendFrame126("Gamble:", 26746);
		ItemAssistant.resetItems(player, INVENTORY_OVERLAY_ITEMS_ID);
		player.getPA().sendFrame248(INTERFACE_ID, INVENTORY_OVERLAY_ID);
		player.getPA().sendFrame126Specific("", 26756, false);
	}

	public static boolean storeItem(Player player, int itemId, int itemSlot, long amount) {
		if (player.getInterfaceIdOpened() != INTERFACE_ID) {
			return false;
		}
		if (player.isUsedMysteryBox()) {
			return true;
		}
		if (!Bank.hasBankingRequirements(player, true)) {
			return true;
		}
		if (ItemAssistant.isNulledSlot(itemSlot)) {
			return true;
		}
		if (ItemAssistant.nulledItem(itemId)) {
			return true;
		}
		if (ItemAssistant.cannotTradeAndStakeItemItem(itemId)) {
			player.getPA().sendMessage("You cannot use untradeable items.");
			return true;
		}

		if (!ItemAssistant.hasItemInInventory(player, itemId)) {
			return true;
		}

		int itemStockInInventory = ItemAssistant.getItemAmount(player, itemId);
		if (amount > itemStockInInventory) {
			amount = itemStockInInventory;
		}
		if (!player.npcDoubleItemsInterfaceStoredItems.isEmpty()) {
			int storedItemId = player.npcDoubleItemsInterfaceStoredItems.get(0).getId();
			long storedItemAmount = player.npcDoubleItemsInterfaceStoredItems.get(0).getAmount();
			if (itemId == storedItemId && (storedItemAmount + amount) > itemStockInInventory) {
				amount = itemStockInInventory - storedItemAmount;
			}
		}
		if (amount == 0) {
			return true;
		}

		boolean added = false;
		for (int index = 0; index < player.npcDoubleItemsInterfaceStoredItems.size(); index++) {
			GameItem instance = player.npcDoubleItemsInterfaceStoredItems.get(index);
			if (instance.getId() == itemId) {
				player.npcDoubleItemsInterfaceStoredItems.set(index, new GameItem(instance.getId(), (int) (instance.getAmount() + amount)));
				added = true;
				break;
			}
		}
		if (!added) {
			if (!player.npcDoubleItemsInterfaceStoredItems.isEmpty()) {
				player.npcDoubleItemsInterfaceStoredItems.clear();
			}
			player.npcDoubleItemsInterfaceStoredItems.add(new GameItem(itemId, (int) amount));
		}

		int displayItemId = player.npcDoubleItemsInterfaceStoredItems.get(0).getId();
		if (displayItemId == ServerConstants.getMainCurrencyId()) {
			displayItemId += 9;
		}
		player.getPA().sendMessage(":packet:gambleinterfaceenablemodel");
		displayItemId = ItemAssistant.getUnNotedItem(displayItemId);
		player.getPA().sendMessage(":packet:winningitemid " + displayItemId + " " + 10);
		player.getPA().sendFrame126Specific("x" + Misc.formatRunescapeStyle(player.npcDoubleItemsInterfaceStoredItems.get(0).getAmount()), 26756, false);
		player.getPA().sendFrame126("Gamble:", 26746);
		return true;
	}

	public static boolean command(Player player, String command) {
		if (command.equals("gambleend")) {
			giveWinningItem(player);
			return true;
		}
		return false;
	}

	private static void sendFirst9Items(Player player) {
		if (player.isUsedMysteryBox()) {
			return;
		}
		int count = 0;
		String data = "";
		ArrayList<String> allItemsCombined = getItemsDisplayed(-1, -1, 9, true);
		for (int index = 0; index < 9; index++) {
			String itemData = "";
			itemData = allItemsCombined.get(index);
			count++;
			data = data + itemData + "#";
			if (count == 7) {
				player.getPA().sendMessage(":packet:latestgamblearray9:" + data);
				count = 0;
				data = "";
			}
		}
		if (!data.isEmpty()) {
			player.getPA().sendMessage(":packet:latestgamblearray9:" + data);
		}
		player.getPA().sendMessage(":packet:latestgamblearrayend9:" + data);
	}

	public static long winningItemAmount;

	public static ArrayList<String> getItemsDisplayed(int itemGambled, long itemGambledAmount, int amountRequested, boolean visualUsage) {
		winningItemAmount = 0;
		ArrayList<String> itemList = new ArrayList<String>();
		double chanceForX3 = CHANCE_FOR_X_3;
		boolean wealthyGamble = itemGambled > 0 && (ServerConstants.getItemValue(itemGambled) * itemGambledAmount) >= (getItemAnnounceAmount() * 2);
		double chanceForX2 = wealthyGamble ? CHANCE_FOR_X_2_AT_HIGHER_WEALTH :  CHANCE_FOR_X_2;
		if (visualUsage) {
			chanceForX3 = CHANCE_FOR_X_3_VISUAL;
			chanceForX2 = CHANCE_FOR_X_2_VISUAL;
		}
		for (int index = 0; index < amountRequested; index++) {

			if (itemGambled == -1) {
				itemGambled = BAIT_ITEMS[index][0];
				itemGambledAmount = BAIT_ITEMS[index][1];
				itemList.add(itemGambled + " x" + Misc.formatRunescapeStyle(itemGambledAmount) + " " + BAIT_ITEMS[index][2]);
				itemGambled = -1;
				continue;
			}
			if (itemGambled == ServerConstants.getMainCurrencyId()) {
				itemGambled += 9;
			}
			if (Misc.hasOneOutOf(chanceForX3)) {
				itemList.add(itemGambled + " x" + Misc.formatRunescapeStyle(itemGambledAmount * 3) + " 1288");
				if (amountRequested == 1) {
					winningItemAmount = itemGambledAmount * 3;
				}
			} else if (Misc.hasOneOutOf(chanceForX2)) {
				itemList.add(itemGambled + " x" + Misc.formatRunescapeStyle(itemGambledAmount * 2) + " 811");
				if (amountRequested == 1) {
					winningItemAmount = itemGambledAmount * 2;
				}
			} else {
				itemList.add("0 0 806");
			}
		}
		return itemList;
	}


	public static void interfaceClosed(Player player) {
		if (player.getInterfaceIdOpened() != INTERFACE_ID) {
			return;
		}
		if (player.isUsedMysteryBox()) {
			return;
		}
		player.npcDoubleItemsInterfaceStoredItems.clear();
	}

	public static void interactWithNpc(Player player, NpcDefinition definition,  String interactionType) {
		switch (interactionType) {
			case "FIRST CLICK" :
				//@formatter:off
				player.setDialogueChain(new DialogueChain().option((p, option) -> {
					if (option == 1) {
						player.setDialogueChain(new DialogueChain().
								
								npc(definition, FacialAnimation.DEFAULT,
								"It is a game where you have 45% chance of doubling your", 
								"items! There is also a slim chance of tripling your items.", 
								"Once you open the double items interface, select an item", 
								"from your inventory to gamble it and click the roll button.").
								
								npc(definition, FacialAnimation.DEFAULT, "Would you like to try out the double items game?").option((p1, anotherOption) -> {
									if (anotherOption == 1) {
										displayInterface(player);
									}
									else if (anotherOption == 2) {
										player.getPA().closeInterfaces(true);
									}
								}, "Select an Option", "Yes", "No, i'll come back later")).start(player);

					}
					else if (option == 2) {
						displayInterface(player);
					}
				}, "Select an Option", "How does the double items game work?", "Let me play the double items game!")).start(player);
				//@formatter:on
				break;
			case "SECOND CLICK" :
				displayInterface(player);
				break;
		}

	}

	private static int dialogueIndex;

	public static int npcArrayIndex;

	public static void npcEvent() {
		Npc npc = NpcHandler.getNpcByNpcId(NPC_ID);
		if (npc == null) {
			Misc.printWarning("Gambling npc missing.");
			return;
		}
		npcArrayIndex = npc.npcIndex;
		npc.getEventHandler().addEvent(npc, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (dialogueIndex > DIALOGUES.length - 1) {
					dialogueIndex = 0;
				}
				npc.forceChat(DIALOGUES[dialogueIndex]);
				dialogueIndex++;
			}

			@Override
			public void stop() {

			}
		}, 30);
	}

	public static void dayChanged() {
		SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_DOUBLE_ITEMS_NPC_DAILY) + " (date, sunk, sunk_raw) VALUES(?, ?, ?)", new StringParameter(1, Misc.getDateOnlyDashes()), new StringParameter(2, Misc.formatRunescapeStyle(amountSunkFromEconomyToday)), new LongParameter(3, amountSunkFromEconomyToday));
		amountSunkFromEconomyToday = 0;
	}

}
