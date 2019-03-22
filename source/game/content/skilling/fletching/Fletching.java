package game.content.skilling.fletching;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Cooking;
import game.content.skilling.Skilling;
import game.content.skilling.crafting.LeatherCrafting;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

public class Fletching {

	private enum GemBolts {
		//emerald and sapphire to be added

		SAPPHIRE(1607, 9142, 56, 4, 4, 888, 9189, 12, 9337),
		EMERALD(1605, 9142, 58, 5, 5, 889, 9190, 12, 9338),
		RUBY(1603, 9143, 63, 6, 6, 887, 9191, 12, 9339),
		DIAMOND(1601, 9143, 65, 7, 7, 890, 9192, 12, 9340),
		DRAGONSTONE(1615, 9144, 71, 8, 8, 892, 9193, 12, 9341),
		ONYX(6573, 9144, 73, 9, 9, 890, 9194, 24, 9342);

		public int gemToCut, boltsToAttachTo, fletchLevelRequired, stoneCutXp, xpPerTipAttachedToBolt, cuttingGemAnimation, tipsProducedId, amountOfTipsPerGem, boltToProduce;

		private GemBolts(int gemToCut, int boltsToAttachTo, int fletchLevelRequired, int stoneCutXp, int xpPerTipAttachedToBolt, int cuttingGemAnimation, int tipsProducedId,
		                 int amountOfTipsPerGem, int boltToProduce) {
			this.gemToCut = gemToCut;
			this.boltsToAttachTo = boltsToAttachTo;
			this.fletchLevelRequired = fletchLevelRequired;
			this.stoneCutXp = stoneCutXp;
			this.xpPerTipAttachedToBolt = xpPerTipAttachedToBolt;
			this.cuttingGemAnimation = cuttingGemAnimation;
			this.tipsProducedId = tipsProducedId;
			this.amountOfTipsPerGem = amountOfTipsPerGem;
			this.boltToProduce = boltToProduce;
		}

		public int getGemToCut() {
			return gemToCut;
		}

		public int getBoltsToAttachTo() {
			return boltsToAttachTo;
		}

		public int getFletchLevelRequired() {
			return fletchLevelRequired;
		}

		public int getStoneCutXp() {
			return stoneCutXp;
		}

		public int getXpPerTipAttachedToBolt() {
			return xpPerTipAttachedToBolt;
		}

		public int getCuttingGemAnimation() {
			return cuttingGemAnimation;
		}

		public int getTipsProducedId() {
			return tipsProducedId;
		}

		public int getAmountOfTipsPerGem() {
			return amountOfTipsPerGem;
		}

		public int getBoltToProduce() {
			return boltToProduce;
		}
	}

	public static boolean isBoltFletchingRelated(Player player, int item1, int item2) {
		for (GemBolts data : GemBolts.values()) {
			if (item1 == data.getGemToCut() || item2 == data.getGemToCut()) {
				cutGem(player, item1, item2);
				return true;
			}
		}
		for (GemBolts data : GemBolts.values()) {
			if (item1 == data.getTipsProducedId() && item2 == data.getBoltsToAttachTo() || item2 == data.getTipsProducedId() && item1 == data.getBoltsToAttachTo()) {
				attachTipToBolt(player, item1, item2);
				return true;
			}
		}
		return false;
	}

	private static void attachTipToBolt(Player player, int item1, int item2) {
		for (GemBolts data : GemBolts.values()) {
			if (item1 == data.getTipsProducedId() || item2 == data.getTipsProducedId()) {
				player.skillingData[0] = data.getTipsProducedId();
				player.skillingData[1] = data.getBoltsToAttachTo();
				player.skillingData[2] = data.getFletchLevelRequired();
				player.skillingData[3] = data.getXpPerTipAttachedToBolt();
				player.skillingData[4] = data.getBoltToProduce();
				player.skillingInterface = "ATTACH TIPS TO BOLT";
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 160, data.getBoltToProduce(), 20, 0);
				break;
			}
		}

	}

	private static void attachTipToBoltEvent(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.skillingData[5] == 0) {
					container.stop();
					return;
				}
				if (player.baseSkillLevel[ServerConstants.FLETCHING] < player.skillingData[2]) {
					player.getDH().sendStatement("You need at least " + player.skillingData[2] + " fletching to fletch " + ItemAssistant.getItemName(player.skillingData[4]) + ".");
					container.stop();
					return;
				}
				int amount = 15;
				int tipsAmount = ItemAssistant.getItemAmount(player, player.skillingData[0]);
				int boltAmount = ItemAssistant.getItemAmount(player, player.skillingData[1]);
				if (amount > tipsAmount) {
					amount = tipsAmount;
				}
				if (amount > boltAmount) {
					amount = boltAmount;
				}
				if (amount == 0) {
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[0], amount)) {
					player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(player.skillingData[0]));
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[1], amount)) {
					player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(player.skillingData[1]));
					container.stop();
					return;
				}
				player.skillingData[5]--;
				ItemAssistant.deleteItemFromInventory(player, player.skillingData[0], amount);
				ItemAssistant.deleteItemFromInventory(player, player.skillingData[1], amount);
				ItemAssistant.addItem(player, player.skillingData[4], amount);
				Skilling.addSkillExperience(player, player.skillingData[3] * amount, ServerConstants.FLETCHING, false);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	public static void attachTipToBoltAmount(Player player, int amount) {

		player.getPA().closeInterfaces(true);
		player.skillingData[5] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, "ATTACH TIPS TO BOLT", "Enter amount");
			return;
		}

		attachTipToBoltEvent(player);
	}

	public static void xAmountAttachTipToBolt(Player player, int amount) {
		player.skillingData[5] = amount;
		attachTipToBoltEvent(player);
	}

	private static void cutGem(Player player, int item1, int item2) {
		int original2 = item2;
		// Chisel.
		if (item2 == 1755 || item2 == 4051) {
			item2 = item1;
			item1 = original2;
		}
		for (GemBolts data : GemBolts.values()) {
			if (item2 == data.getGemToCut()) {
				player.skillingData[0] = data.getCuttingGemAnimation();
				player.skillingData[1] = data.getTipsProducedId();
				player.skillingData[2] = data.getStoneCutXp();
				player.skillingData[3] = data.getGemToCut();
				player.skillingData[4] = data.getFletchLevelRequired();
				player.skillingData[5] = data.getAmountOfTipsPerGem();
				player.skillingInterface = "CUT GEM INTO BOLT TIPS";
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 160, data.getTipsProducedId(), 20, 0);
			}
		}

	}

	private static void cutGemEvent(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.skillingData[6] == 0) {
					container.stop();
					return;
				}
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.baseSkillLevel[ServerConstants.FLETCHING] < player.skillingData[4]) {
					player.getDH().sendStatement("You need at least " + player.skillingData[4] + " to fletch " + ItemAssistant.getItemName(player.skillingData[1]) + ".");
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[3], 1)) {
					player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(player.skillingData[3]));
					container.stop();
					return;
				}
				player.skillingData[6]--;
				ItemAssistant.deleteItemFromInventory(player, player.skillingData[3], 1);
				ItemAssistant.addItem(player, player.skillingData[1], player.skillingData[5]);
				if (Skilling.hasMasterCapeWorn(player, 9783) && Misc.hasPercentageChance(10)) {
					ItemAssistant.addItemToInventoryOrDrop(player, player.skillingData[1], player.skillingData[5]);
					player.getPA().sendMessage("<col=a54704>Your cape allows you to fletch some extra bolt tips.");
				}
				Skilling.addSkillExperience(player, player.skillingData[2], ServerConstants.FLETCHING, false);
				player.startAnimation(player.skillingData[0]);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	public static void cutGemAmount(Player player, int amount) {

		player.getPA().closeInterfaces(true);
		player.skillingData[6] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, "CUT GEM INTO BOLT TIPS", "Enter amount");
			return;
		}

		cutGemEvent(player);
	}

	public static void xAmountCutGem(Player player, int amount) {
		player.skillingData[6] = amount;
		cutGemEvent(player);
	}

	private enum ArrowAndBoltData {
		HEADLESS_ARROW(52, 314, 53, 15, 1),
		BRONZE_ARROW(53, 39, 882, 40, 1),
		IRON_ARROW(53, 40, 884, 58, 15),
		STEEL_ARROW(53, 41, 886, 95, 30),
		MITHRIL_ARROW(53, 42, 888, 132, 45),
		ADAMANT_ARROW(53, 43, 890, 170, 60),
		RUNE_ARROW(53, 44, 892, 207, 75),
		BRONZE_BOLT(314, 9375, 877, 5, 9),
		IRON_BOLT(314, 9377, 9140, 15, 39),
		STEEL_BOLT(314, 9378, 9141, 35, 46),
		MITHRIL_BOLT(314, 9379, 9142, 50, 54),
		ADAMANT_BOLT(314, 9380, 9143, 70, 61),
		RUNE_BOLT(314, 9381, 9144, 100, 69),

		BRONZE_JAV(19570, 19584, 825, 15, 3),
		IRON_JAV(19572, 19584, 826, 30, 17),
		STEEL_JAV(19574, 19584, 827, 75, 32),
		MITHRIL_JAV(19576, 19584, 828, 120, 47),
		ADDY_JAV(19578, 19584, 829, 150, 62),
		RUNE_JAV(19580, 19584, 830, 186, 77),
		DRAGON_JAV(19582, 19584, 19484, 225, 92);

		public int item1, item2, outcome, xp, levelReq;

		private ArrowAndBoltData(int item1, int item2, int outcome, int xp, int levelReq) {
			this.item1 = item1;
			this.item2 = item2;
			this.outcome = outcome;
			this.xp = xp;
			this.levelReq = levelReq;
		}

		public int getItem1() {
			return item1;
		}

		public int getItem2() {
			return item2;
		}

		public int getOutcome() {
			return outcome;
		}

		public int getXp() {
			return xp;
		}

		public int getLevelReq() {
			return levelReq;
		}
	}

	public static boolean isArrowCombining(Player player, int item1, int item2) {
		for (ArrowAndBoltData data : ArrowAndBoltData.values()) {
			if (item1 == data.getItem1() || item1 == data.getItem2() || item2 == data.getItem1() || item2 == data.getItem2()) {
				combineArrowParts(player, item1, item2);
				return true;
			}
		}
		return false;
	}


	private static void combineArrowParts(Player player, int item1, int item2) {
		boolean found = false;
		int original1 = item1;
		for (ArrowAndBoltData data : ArrowAndBoltData.values()) {
			if (item1 == (player.isInZombiesMinigame() ? 4051 : data.getItem1()) && item2 == data.getItem2()) {
				found = true;
			} else if (item2 == (player.isInZombiesMinigame() ? 4051 : data.getItem1()) && item1 == data.getItem2()) {
				item1 = item2;
				item2 = original1;
				found = true;
			}

			if (found) {
				player.skillingData[0] = (player.isInZombiesMinigame() ? 4051 : data.getItem1());
				player.skillingData[1] = data.getItem2();
				player.skillingData[2] = data.getOutcome();
				player.skillingData[3] = data.getLevelReq();
				player.skillingData[4] = data.getXp();
				player.skillingInterface = "COMBINE ARROWS";
				InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 160, data.getOutcome(), 20, 0);
				break;
			}
		}

	}


	private static void combineArrowPartsEvent(final Player player) {
		if (RandomEvent.isBannedFromSkilling(player)) {
			return;
		}
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.skillingData[5] == 0) {
					container.stop();
					return;
				}
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.baseSkillLevel[ServerConstants.FLETCHING] < player.skillingData[3]) {
					player.getDH().sendStatement("You need at least " + player.skillingData[3] + " to fletch " + ItemAssistant.getItemName(player.skillingData[2]) + ".");
					container.stop();
					return;
				}

				int amount = 15;
				int amount1 = ItemAssistant.getItemAmount(player, player.skillingData[0]);
				if (player.isInZombiesMinigame()) {
					amount1 = 15;
				}
				int amount2 = ItemAssistant.getItemAmount(player, player.skillingData[1]);
				if (amount > amount1) {
					amount = amount1;
				}
				if (amount > amount2) {
					amount = amount2;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[0], 1)) {
					player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(player.skillingData[0]));
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.skillingData[1], 1)) {
					player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(player.skillingData[1]));
					container.stop();
					return;
				}
				player.skillingData[5]--;
				if (!player.isInZombiesMinigame()) {
					ItemAssistant.deleteItemFromInventory(player, player.skillingData[0], amount);
				}
				ItemAssistant.deleteItemFromInventory(player, player.skillingData[1], amount);
				ItemAssistant.addItem(player, player.skillingData[2], amount);
				Skilling.addSkillExperience(player, player.skillingData[4], ServerConstants.FLETCHING, false);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	public static void combineArrowPartsAmount(Player player, int amount) {

		player.getPA().closeInterfaces(true);
		player.skillingData[5] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, "COMBINE ARROWS", "Enter amount");
			return;
		}

		combineArrowPartsEvent(player);
	}

	public static void xAmountCombineArrowParts(Player player, int amount) {
		player.skillingData[5] = amount;
		combineArrowPartsEvent(player);
	}


	public static boolean normal(Player c, int itemUsed, int useWith) {
		if ((itemUsed == 946 || itemUsed == 4051) && useWith == 1511 || itemUsed == 1511 && (useWith == 946 || useWith == 4051)) {
			c.threeOptionType = "FLETCHING";
			showInterfaceFletching(c, new int[]
					                          {50, 48, 52}, 0);
			return true;
		}
		return false;
	}

	public static boolean others(Player player, int itemUsed, int useWith) {
		if (useWith != 946 && itemUsed != 946 && useWith != 4051 && itemUsed != 4051) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			if (((itemUsed == 946 || itemUsed == 4051) && useWith == data[i][0]) || (itemUsed == data[i][0] && (useWith == 946 || useWith == 4051))) {
				if (data[i][0] == 1515) // Yew log.
				{
					player.yewLog = true;
					LeatherCrafting.showCraftingInterface(player, "What would you like to make?", 68, 9452, 66, ItemAssistant.getItemName(9452), ItemAssistant.getItemName(68),
					                                      ItemAssistant.getItemName(66), 200);
					return true;
				}
				showInterfaceOthers(player, new int[]
						                            {data[i][1], data[i][4],}, data[i][7]);
				return true;
			}
		}
		return false;
	}

	// Using knife on logs is different because 3 options appear, 3rd one is arrow shafts.
	public static void attemptData(Player player, int amount, int type) {
		if (!player.threeOptionType.equals("FLETCHING")) {
			return;
		}
		player.playerSkillProp[9][0] = normal[0][0]; //LOG
		switch (type) {
			case 0: //LONGBOW
				player.playerSkillProp[9][1] = 48; // Item
				player.playerSkillProp[9][2] = 10; // Level
				player.playerSkillProp[9][3] = 10; // Xp
				break;
			case 1: //SHORTBOW
				player.playerSkillProp[9][1] = 50;
				player.playerSkillProp[9][2] = 1;
				player.playerSkillProp[9][3] = 5;
				break;
			case 2: //ARROW SHAFT
				player.playerSkillProp[9][1] = 52;
				player.playerSkillProp[9][2] = 1;
				player.playerSkillProp[9][3] = 5;
				break;
		}
		attemptData(player, amount, false);
	}

	private static int[][] data =
			{

					//log, shortbow, level, xp, longbow, level, xp, type
					//// Oak Log
					{1521, 54, 20, 20, 56, 25, 25, 0},
					// Willow.
					{1519, 60, 35, 34, 58, 40, 42, 1},
					// Maple.
					{1517, 64, 50, 50, 62, 55, 58, 2},
					// Yew.
					{1515, 68, 65, 68, 66, 70, 75, 3},
					// Magic.
					{1513, 72, 80, 83, 70, 85, 92, 4},
			};

	private static void showInterfaceOthers(Player player, int[] items, int type) {
		for (int i = 0; i < data.length; i++) {
			if (type == data[i][7]) {
				player.playerSkillProp[9][0] = data[i][0]; //LOG
				player.playerSkillProp[9][1] = data[i][1]; //SHORTBOW
				player.playerSkillProp[9][2] = data[i][2]; //LVL
				player.playerSkillProp[9][3] = data[i][3]; //XP
				player.playerSkillProp[9][4] = data[i][4]; //SHORTBOW
				player.playerSkillProp[9][5] = data[i][5]; //LVL
				player.playerSkillProp[9][6] = data[i][6]; //XP
				player.playerSkillProp[9][7] = data[i][7]; //TYPE
			}
		}
		player.getPA().sendFrame164(8866);
		player.getPA().sendMessage(":packet:senditemchat 8869 0 -15");
		player.getPA().sendMessage(":packet:senditemchat 8870 0 -16");
		player.getPA().sendFrame246(8869, Skilling.view190 ? 190 : 225, items[0]);
		player.getPA().sendFrame246(8870, Skilling.view190 ? 190 : 200, items[1]);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(items[0]) + "", 8874);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(items[1]) + "", 8878);
	}

	private static void showInterfaceFletching(Player player, int[] items, int type) {
		player.getPA().sendFrame164(8880);
		player.getPA().sendFrame126("What would you like to make?", 8879);
		player.getPA().sendMessage(":packet:senditemchat 8884 0 -13");
		player.getPA().sendMessage(":packet:senditemchat 8883 0 -13");
		player.getPA().sendMessage(":packet:senditemchat 8885 0 -13");
		player.getPA().sendFrame246(8884, Skilling.view190 ? 190 : 225, items[0]);
		player.getPA().sendFrame246(8883, Skilling.view190 ? 190 : 210, items[1]);
		player.getPA().sendFrame246(8885, Skilling.view190 ? 190 : 240, items[2]);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(items[1]) + "", 8889);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(items[0]) + "", 8893);
		player.getPA().sendFrame126("" + ItemAssistant.getItemName(items[2]) + "", 8897);
	}

	public static int[][] normal =
			{
					{1511, 50, 48, 50, 1, 0, 6684, 52},
			};

	public static void attemptData(final Player player, int amount, boolean second) {
		if (player.rawBeef) {
			if (player.rawBeefChosen) {
				Cooking.cookFish(player, 2132, 30, 1, 2146, 2142, 2728);
			} else {
				Cooking.cookSinew(player);
			}
			player.rawBeefChosen = false;
			return;
		}

		player.rawBeefChosen = false;
		int requirement = player.playerSkillProp[9][2];
		if (second) {
			requirement = player.playerSkillProp[9][5];
		}
		if (!Skilling.hasRequiredLevel(player, 9, requirement, "fletching", "fletch this")) {
			return;
		}

		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		int item = ItemAssistant.getItemAmount(player, player.playerSkillProp[9][0]);
		if (amount > item) {
			amount = item;
		}
		player.doAmount = amount;
		player.getPA().closeInterfaces(true);
		final int itemToDelete = player.playerSkillProp[9][0];
		int itemToAdd = player.playerSkillProp[9][1];
		int xpToAdd = player.playerSkillProp[9][3];
		if (second) {
			itemToAdd = player.playerSkillProp[9][4];
			xpToAdd = player.playerSkillProp[9][6];
		}
		final int addItem = itemToAdd;
		final int addXP = (xpToAdd);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					resetFletching(player);
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, player.playerSkillProp[9][0], 1) || player.doAmount <= 0) {
					resetFletching(player);
					container.stop();
				}
				if (player.playerSkillProp[9][0] > 0) {
					if (Skilling.hasMasterCapeWorn(player, 9783) && Misc.hasPercentageChance(10)) {
						player.getPA().sendMessage("<col=a54704>Your cape allows you to save a log.");
					} else {
						ItemAssistant.deleteItemFromInventory(player, itemToDelete, ItemAssistant.getItemSlot(player, itemToDelete), 1);
					}
					ItemAssistant.addItem(player, addItem, player.playerSkillProp[9][1] == 52 ? 15 : 1);
					String bowText = ItemAssistant.getItemName(addItem).toLowerCase().contains("shortbow") ? "shortbow" : "longbow";
					player.playerAssistant.sendFilterableMessage("You carefully cut the wood into a " + bowText + ".");
					Skilling.addSkillExperience(player, addXP, 9, false);
					player.startAnimation(1248);
				}
				Skilling.deleteTime(player);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	public static void resetFletching(Player c) {
		for (int i = 0; i < 9; i++) {
			c.playerSkillProp[9][i] = -1;
		}
		c.startAnimation(65535);
	}

	public static void yewStockInterfaceAction(final Player player, int imageType, int amount) {
		player.doAmount = amount;
		// image 1 = yew stock, image 3 = yew long.
		int requirement = 0;
		int xp = 0;
		int delete = 1515;
		int give = 0;
		switch (imageType) {
			case 1:
				requirement = 69;
				xp = 50;
				give = 9452;
				break;
			case 2:
				requirement = 65;
				xp = 68;
				give = 68;
				break;
			case 3:
				requirement = 70;
				xp = 75;
				give = 66;
				break;
		}
		final int requirement1 = requirement;
		final int xp1 = xp;
		final int delete1 = delete;
		final int give1 = give;
		player.getPA().closeInterfaces(true);
		if (Skilling.cannotActivateNewSkillingEvent(player)) {
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (Skilling.forceStopSkillingEvent(player)) {
					container.stop();
					return;
				}
				if (player.baseSkillLevel[ServerConstants.FLETCHING] < requirement1) {
					container.stop();
					player.getDH().sendStatement("You need " + requirement1 + " fletching.");
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, delete1)) {
					container.stop();
					return;
				}
				if (player.doAmount == 0) {
					container.stop();
					return;
				}
				ItemAssistant.deleteItemFromInventory(player, delete1, 1);
				ItemAssistant.addItem(player, give1, 1);
				Skilling.addSkillExperience(player, xp1, ServerConstants.FLETCHING, false);
				player.startAnimation(6702);
				player.doAmount--;
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);

	}
}
