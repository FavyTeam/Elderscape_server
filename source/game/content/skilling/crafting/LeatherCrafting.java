package game.content.skilling.crafting;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.RandomEvent;
import game.content.skilling.Skilling;
import game.content.skilling.SkillingStatistics;
import game.content.skilling.fletching.Fletching;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Leather crafting
 *
 * @author MGT Madness, created on 23-03-2016.
 */
public class LeatherCrafting {
	/**
	 * Item to produce, button id for x1, button id for x5, button id for x10. This is the special interface for crafting leather only items.
	 */
	private final static int[][] leatherInterfaceData =
			{
					// @formatter:off
					{
							1129, 33187, 33186, 33185
					},
					{
							1059, 33190, 33189, 33188
					},
					{
							1061, 33193, 33192, 33191
					},
					{
							1063, 33196, 33195, 33194
					},
					{
							1095, 33199, 33198, 33197
					},
					{
							1169, 33202, 33201, 33200
					},
					{
							1167, 33205, 33204, 33203
					}
					// @formatter:on

			};


	public static enum LeatherCraftingData {
		LEATHER_BODY(1741, 1129, 14, 25, 1),
		LEATHER_GLOVES(1741, 1059, 1, 13.8, 1),
		LEATHER_BOOTS(1741, 1061, 7, 16.25, 1),
		LEATHER_VAMBRACES(1741, 1063, 11, 22, 1),
		LEATHER_CHAPS(1741, 1095, 18, 27, 1),
		LEATHER_COIF(1741, 1169, 38, 37, 1),
		LEATHER_COWL(1741, 1167, 9, 18.5, 1),
		HARD_LEATHER_BODY(1743, 1131, 28, 35, 1),
		SNAKESKIN_BODY(6289, 6322, 53, 55, 15),
		SNAKESKIN_CHAPS(6289, 6324, 51, 50, 12),
		SNAKESKIN_BANDANA(6289, 6326, 48, 45, 5),
		SNAKESKIN_BOOTS(6289, 6328, 45, 30, 6),
		SNAKESKIN_VAMBRACES(6289, 6330, 47, 35, 8),
		GREENVAMBS(1745, 1065, 57, 62, 1),
		GREENCHAPS(1745, 1099, 60, 124, 2),
		GREENBODY(1745, 1135, 63, 186, 3),
		BLUEVAMBS(2505, 2487, 66, 70, 1),
		BLUECHAPS(2505, 2493, 68, 140, 2),
		BLUEBODY(2505, 2499, 71, 210, 3),
		REDVAMBS(2507, 2489, 73, 78, 1),
		REDCHAPS(2507, 2495, 75, 156, 2),
		REDBODY(2507, 2501, 77, 234, 3),
		BLACKVAMBS(2509, 2491, 79, 86, 1),
		BLACKCHAPS(2509, 2497, 82, 172, 2),
		BLACKBODY(2509, 2503, 84, 258, 3);

		private int leatherRequired;

		private int itemProduced;

		private int requiredLevel;

		private int requiredAmount;

		private double experience;

		private LeatherCraftingData(int leatherRequired, int itemProduced, int requiredLevel, double experience, int requiredAmount) {
			this.leatherRequired = leatherRequired;
			this.itemProduced = itemProduced;
			this.requiredLevel = requiredLevel;
			this.experience = experience;
			this.requiredAmount = requiredAmount;
		}

		public int getLeatherRequired() {
			return leatherRequired;
		}

		public int getItemProduced() {
			return itemProduced;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public double getExperience() {
			return experience;
		}

		public int getRequiredAmount() {
			return requiredAmount;
		}

	}

	public static boolean useNeedleOnLeather(Player player, int itemUsedId, int itemUsedWith) {
		if (itemUsedId != 1733 && itemUsedWith != 1733 && itemUsedId != 4051 && itemUsedWith != 4051) {
			return false;
		}
		if (itemUsedId == 4051) {
			itemUsedId = itemUsedWith;
		}
		if (itemUsedId == 1741) // 1741 is leather.
		{

			player.leatherUsed = 1741;
			player.threeOptionType = "LEATHER";
			displayLeatherInterface(player);
			return true;
		}

		LeatherCraftingData leather = null;
		for (LeatherCraftingData data : LeatherCraftingData.values()) {
			if (itemUsedId == data.getLeatherRequired() || itemUsedWith == data.getLeatherRequired()) {
				leather = data;
				break;
			}
		}
		if (leather == null) {
			return false;
		}
		player.leatherUsed = leather.getLeatherRequired();
		player.threeOptionType = "LEATHER";
		displayLeatherCraftingInterface(player, leather);
		return true;
	}

	private static void displayLeatherInterface(Player player) {
		player.getPA().displayInterface(2311);
	}

	public static void showCraftingInterface(Player player, String title, int leftImageItem, int middleImageItem, int rightImageItem, String leftImageName, String middleImageName,
	                                         String rightImageName, int zoom) {
		player.getPA().sendFrame164(8880);
		player.getPA().sendFrame126(title, 8879);
		player.getPA().sendFrame246(8884, zoom, leftImageItem); // middle
		player.getPA().sendFrame246(8883, zoom, middleImageItem); // left picture
		player.getPA().sendFrame246(8885, zoom, rightImageItem); // right pic
		player.getPA().sendFrame126(leftImageName, 8889);
		player.getPA().sendFrame126(middleImageName, 8893);
		player.getPA().sendFrame126(rightImageName, 8897);
	}

	public static void hardLeatherBodyInterfaceAction(Player player, int amount) {
		player.getPA().closeInterfaces(true);
		player.skillingData[3] = amount;
		if (amount == 100) {
			InterfaceAssistant.showAmountInterface(player, "HARD LEATHER BODY", "Enter amount");
			return;
		}

		LeatherCraftingData leather = LeatherCraftingData.HARD_LEATHER_BODY;
		craftLeatherEvent(player, leather);
	}

	public static void xAmountHardLeatherBodyAction(Player player, int amount) {
		player.skillingData[3] = amount;
		LeatherCraftingData leather = LeatherCraftingData.HARD_LEATHER_BODY;
		craftLeatherEvent(player, leather);
	}

	private static void displayLeatherCraftingInterface(Player player, LeatherCraftingData leather) {
		if (leather.getLeatherRequired() == 1745) // Normal leather.
		{
			showCraftingInterface(player, "What would you like to make?", 1099, 1065, 1135, "Vambs", "Chaps", "Body", 200);
		} else if (leather.getLeatherRequired() == 1743) // Hard leather.
		{
			InterfaceAssistant.showSkillingInterface(player, "How many would you like to make?", 234, 1131, -10, 0);
			player.skillingInterface = "HARD LEATHER BODY";
		} else if (leather.getLeatherRequired() == 2505) {
			showCraftingInterface(player, "What would you like to make?", 2493, 2487, 2499, "Vambs", "Chaps", "Body", 200);
		} else if (leather.getLeatherRequired() == 2507) {
			showCraftingInterface(player, "What would you like to make?", 2495, 2489, 2501, "Vambs", "Chaps", "Body", 200);
		} else if (leather.getLeatherRequired() == 2509) {
			showCraftingInterface(player, "What would you like to make?", 2497, 2491, 2503, "Vambs", "Chaps", "Body", 200);
		}
	}

	public final static int[][] leatherInterfaceButtons =
			{
					// Left image.
					{34185, 1, 1},
					{34184, 1, 5},
					{34183, 1, 10},
					{34182, 1, 27},
					// Middle image.
					{34189, 2, 1}, //4
					{34188, 2, 5},
					{34187, 2, 10},
					{34186, 2, 27},
					// Right image.
					{34193, 3, 1}, //8,
					{34192, 3, 5},
					{34191, 3, 10},
					{34190, 3, 27}
			};

	private static boolean craftLeather(Player player, int amount, boolean skipXCheck) {
		// Send x amount interface.
		if (amount == 27 && !skipXCheck) {
			InterfaceAssistant.showAmountInterface(player, ServerConstants.SKILL_NAME[ServerConstants.CRAFTING] + " LEATHER", "Enter amount");
			return true;
		}
		player.skillingData[3] = amount;
		LeatherCraftingData leather = null;
		if (player.leatherUsed == 1741) // Leather.
		{
			for (LeatherCraftingData data : LeatherCraftingData.values()) {
				if (player.leatherUsed == data.getLeatherRequired() && player.leatherItemToProduceType == data.getItemProduced()) {
					leather = data;
					break;
				}
			}
		} else {
			for (LeatherCraftingData data : LeatherCraftingData.values()) {
				if (player.leatherUsed == data.getLeatherRequired() && player.leatherItemToProduceType == data.getRequiredAmount()) {
					leather = data;
					break;
				}
			}
			if (leather == null) {
				return false;
			}
			if (player.baseSkillLevel[ServerConstants.CRAFTING] < leather.getRequiredLevel()) {
				player.getDH().sendStatement("You need " + leather.getRequiredLevel() + " crafting to use this leather.");
				return true;
			}
		}
		player.getPA().closeInterfaces(true);
		craftLeatherEvent(player, leather);
		return false;
	}

	private static void craftLeatherEvent(final Player player, final LeatherCraftingData leather) {
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
				if (!ItemAssistant.hasItemAmountInInventory(player, leather.getLeatherRequired(), leather.getRequiredAmount())) {
					player.getDH().sendStatement(
							"You need " + leather.getRequiredAmount() + " " + ItemAssistant.getItemName(leather.getLeatherRequired()) + " to craft a " + ItemAssistant.getItemName(
									leather.getItemProduced()) + ".");
					container.stop();
					return;
				}
				if (!ItemAssistant.hasItemAmountInInventory(player, 1734, 1) && !player.isInZombiesMinigame()) {
					player.getPA().sendMessage("You have run out of thread.");
					container.stop();
					return;
				}
				if (player.skillingData[3] == 0) {
					container.stop();
					return;
				}
				successfulLeather(player, leather);
			}

			@Override
			public void stop() {
				Skilling.endSkillingEvent(player);
			}
		}, 2);
	}

	private static void successfulLeather(Player player, LeatherCraftingData leather) {
		if (leather.toString().equals(LeatherCraftingData.GREENBODY.toString())) {
			Achievements.checkCompletionMultiple(player, "1033");
		} else if (leather.toString().equals(LeatherCraftingData.BLACKBODY.toString())) {
			Achievements.checkCompletionMultiple(player, "1054 1132");
		} else if (leather.toString().equals(LeatherCraftingData.REDBODY.toString())) {
			Achievements.checkCompletionMultiple(player, "1075");
		}
		Skilling.addSkillExperience(player, (int) leather.getExperience(), ServerConstants.CRAFTING, false);
		player.startAnimation(1249);
		ItemAssistant.deleteItemFromInventory(player, leather.getLeatherRequired(), leather.getRequiredAmount());
		ItemAssistant.deleteItemFromInventory(player, 1734, 1); // Thread.
		int chance = 0;

		if (player.isInZombiesMinigame()) {
			chance = 0;
		}

		ItemAssistant.addItem(player, leather.getItemProduced(), 1 * (Misc.hasPercentageChance(chance) ? 2 : 1));
		player.skillingData[3]--;
		player.skillingStatistics[SkillingStatistics.LEATHER_CRAFTED]++;
	}

	public static boolean isLeatherCraftingButton(Player player, int buttonId) {
		if (buttonId == 9118) {
			player.getPA().closeInterfaces(true);
			return true;
		}
		if (!player.threeOptionType.equals("LEATHER") && !player.yewLog) {
			return false;
		}
		for (int index = 0; index < leatherInterfaceButtons.length; index++) {
			if (buttonId == leatherInterfaceButtons[index][0]) {
				if (player.yewLog) {
					Fletching.yewStockInterfaceAction(player, leatherInterfaceButtons[index][1], leatherInterfaceButtons[index][2]);
					return true;
				}
				player.leatherItemToProduceType = leatherInterfaceButtons[index][1];
				return craftLeather(player, leatherInterfaceButtons[index][2], false);
			}
		}
		for (int index = 0; index < leatherInterfaceData.length; index++) {
			int number = 1;
			boolean engage = false;
			if (buttonId == leatherInterfaceData[index][number]) {
				engage = true;
			} else {
				number++;
			}
			if (buttonId == leatherInterfaceData[index][number]) {
				engage = true;
			} else {
				number++;
			}
			if (buttonId == leatherInterfaceData[index][number]) {
				engage = true;
			} else {
				number++;
			}
			if (engage) {
				player.leatherItemToProduceType = leatherInterfaceData[index][0];
				for (LeatherCraftingData data : LeatherCraftingData.values()) {
					if (player.leatherItemToProduceType == data.getItemProduced()) {
						if (player.baseSkillLevel[ServerConstants.CRAFTING] < data.getRequiredLevel()) {
							player.getDH().sendStatement("You need " + data.getRequiredLevel() + " crafting to use this leather.");
							return true;
						}
					}
				}
				int amount = number == 1 ? 1 : number == 2 ? 5 : 10;
				craftLeather(player, amount, false);
				return true;
			}
		}
		return false;
	}

	public static void xAmountLeatherCraftingAction(Player player, int xAmount) {
		if (player.yewLog) {
			return;
		}
		craftLeather(player, xAmount, true);
	}

	/**
	 * DRAGON HIDE, DRAGON LEATHER, button1, button2, button3, button4, cost
	 */
	private final static int[][] hideData =
			{
					// Soft leather.
					{1739, 1741, 57225, 57217, 57209, 57201, 1},
					// Hard leather.
					{1739, 1743, 57226, 57218, 57210, 57202, 3},
					// Green.
					{1753, 1745, 57229, 57221, 57213, 57205, 20},
					// Blue.
					{1751, 2505, 57230, 57222, 57214, 57206, 20},
					// Red.
					{1749, 2507, 57231, 57223, 57215, 57207, 20},
					// Black.
					{1747, 2509, 57232, 57224, 57216, 57208, 20}
			};

	public static boolean isTanningButton(Player player, int buttonId) {
		for (int index = 0; index < hideData.length; index++) {
			int number = 2;
			boolean engage = false;
			for (int loop = 0; loop < 4; loop++) {
				if (buttonId == hideData[index][number]) {
					engage = true;
				} else {
					number++;
				}
			}
			if (engage) {
				int amount = number == 2 ? 1 : number == 3 ? 5 : number == 4 ? 100 : 27;
				if (amount == 100) {
					player.skillingData[0] = hideData[index][0];
					player.skillingData[1] = hideData[index][1];
					player.skillingData[2] = hideData[index][6];
					InterfaceAssistant.showAmountInterface(player, "TANNING", "Enter amount");
					return true;
				}
				tan(player, hideData[index][0], hideData[index][1], amount, hideData[index][6]);
				return true;
			}
		}
		return false;
	}

	public static void displayTanningInterface(Player player) {
		player.getPA().sendFrame126("Soft leather", 14777);
		player.getPA().sendFrame126("@gre@ 1 Coin", 14785);
		player.getPA().sendFrame126("Hard leather", 14778);
		player.getPA().sendFrame126("@gre@ 3 Coins", 14786);
		/*
		 * 
		player.getPA().sendFrame126("Snake skin", 14779);
		player.getPA().sendFrame126("@gre@ 15 Coins", 14787);
		player.getPA().sendFrame126("Snake skin", 14780);
		player.getPA().sendFrame126("@gre@ 20 Coins", 14788);
		 */
		player.getPA().sendFrame126(" ", 14779);
		player.getPA().sendFrame126(" ", 14787);
		player.getPA().sendFrame126(" ", 14780);
		player.getPA().sendFrame126(" ", 14788);
		player.getPA().sendFrame126("Green D'hide", 14781);
		player.getPA().sendFrame126("@gre@ 20 Coins", 14789);
		player.getPA().sendFrame126("Blue D'hide", 14782);
		player.getPA().sendFrame126("@gre@ 20 Coins", 14790);
		player.getPA().sendFrame126("Red d'hide", 14783);
		player.getPA().sendFrame126("@gre@ 20 Coins", 14791);
		player.getPA().sendFrame126("Black D'hide", 14784);
		player.getPA().sendFrame126("@gre@ 20 Coins", 14792);
		player.getPA().sendFrame246(14769, 220, 1739);
		player.getPA().sendFrame246(14770, 220, 1739);
		player.getPA().sendFrame246(14773, 220, 1753);
		player.getPA().sendFrame246(14774, 220, 1751);
		player.getPA().sendFrame246(14775, 220, 1749);
		player.getPA().sendFrame246(14776, 220, 1747);
		player.getPA().displayInterface(14670);
	}

	public static void tan(Player player, int hide, int toProduce, int amount, int cost) {
		boolean foundHide = false;
		foundHide = true;
		int hidesAmount = amount;
		int coinAmount = ItemAssistant.getItemAmount(player, 995);
		int amountInInventory = ItemAssistant.getItemAmount(player, hide);
		if (amount > amountInInventory) {
			hidesAmount = amountInInventory;
		}

		if (coinAmount == 0) {
			player.getDH().sendStatement("You do not have enough coins.");
			return;
		}
		if (coinAmount < hidesAmount * cost) {
			hidesAmount = coinAmount / cost;
		}
		if (hidesAmount == 0) {
			player.getDH().sendStatement("You do not have enough coins.");
			return;
		}

		ItemAssistant.deleteItemFromInventory(player, 995, hidesAmount * cost);
		ItemAssistant.deleteItemFromInventory(player, hide, hidesAmount);
		ItemAssistant.addItem(player, toProduce, hidesAmount);
		player.getDH().sendStatement("Ellis tans " + hidesAmount + " " + ItemAssistant.getItemName(hide) + " for " + (hidesAmount * cost) + " coins.");
		if (!foundHide) {
			player.getDH().sendStatement("You do not have hides to tan.");
		}

	}
}
