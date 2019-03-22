package game.content.achievement;

import core.ServerConstants;
import game.content.interfaces.InterfaceAssistant;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

import java.util.ArrayList;

/**
 * The content on the achievement tab and the achievement interface.
 *
 * @author MGT Madness, created on 10-03-2015.
 */
public class Achievements {

	private final static int[] DIFFICULTY_BUTTONS =
			{86253, 87000, 87003, 87006};

	private final static int[] REWARDS_DIFFICULTY_BUTTONS =
			{75111, 75114, 75117, 75120};

	private final static String[] DIFFICULTY_NAMES =
			{"EASY", "MEDIUM", "HARD", "ELITE"};

	/**
	 * Used to update the colour of the achievement and the completed amount percentage.
	 */
	public static int getCurrentAchievementProgress(Player player, int achievementId, boolean add, boolean clicked) {
		for (int index = 0; index < AchievementDefinitions.npcTasksData.size(); index++) {
			String string[] = AchievementDefinitions.npcTasksData.get(index).split("-");
			if (string[0].equals(Integer.toString(achievementId))) {
				return getArraylistCount(string[1], player.npcKills);
			}
		}
		switch (achievementId) {

			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				return player.getHybridKills();

			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				return player.getBerserkerPureKills();
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				return player.getRangedTankKills();

			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
				return player.getPureKills();

			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
				return player.getMeleeMainKills();

			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
				return player.getF2pKills();
		}
		String achievementSaveName = "";
		for (int index = 0; index < AchievementDefinitions.idAndSaveName.size(); index++) {
			String string[] = AchievementDefinitions.idAndSaveName.get(index).split(" ");
			if (string[0].equals(Integer.toString(achievementId))) {
				achievementSaveName = string[1];
			}
		}
		if (add) {
			addToAchievementProgress(player, achievementSaveName);
		}
		return getArraylistCount(achievementSaveName, player.achievementProgress);
	}

	public static void updatePoints(Player player) {
		player.getPA().sendFrame126("Points: " + player.achievementPoint, 22264);
	}

	/**
	 * Achievements where you only do an action once.
	 */
	public static void checkCompletionSingle(Player player, int achievementId) {
		if (player.isCombatBot()) {
			return;
		}
		if (isAchievementCompleted(player, achievementId)) {
			return;
		}
		if (AchievementDefinitions.achievementIdAndDefinitionIndex.get(achievementId) == null) {
			return;
		}
		int achievementIndex = AchievementDefinitions.achievementIdAndDefinitionIndex.get(achievementId);
		achievementReward(player, achievementId, achievementIndex);
	}

	/**
	 * Achievements where you have to do 2 or more actions of the same thing, such as fish 2 sharks.
	 */
	public static void checkCompletionMultiple(Player player, String achievementIdList) {
		if (player.isCombatBot()) {
			return;
		}
		String string[] = achievementIdList.split(" ");
		for (int index = 0; index < string.length; index++) {
			int achievementId = Integer.parseInt(string[index]);
			int progress = getCurrentAchievementProgress(player, achievementId, true, false);
			if (isAchievementCompleted(player, achievementId)) {
				continue;
			}
			if (AchievementDefinitions.achievementIdAndDefinitionIndex.get(achievementId) == null) {
				continue;
			}
			int achievementIndex = AchievementDefinitions.achievementIdAndDefinitionIndex.get(achievementId);
			int completeAmount = AchievementDefinitions.getDefinitions()[achievementIndex].completeAmount;
			if (progress >= completeAmount) {
				achievementReward(player, achievementId, achievementIndex);
			}
		}
		player.achievementAddedOnce = false;
	}

	private static void achievementReward(Player player, int achievementId, int achievementIndex) {
		int pointsAmount = 0;
		player.achievementsCompleted.add(Integer.toString(achievementId));
		AchievementStatistics
				.showAchievementPopup(player, AchievementDefinitions.getDefinitions()[achievementIndex].descriptionSubText1, "Congratulations!", "You have unlocked new items.");
		if (achievementIndex <= AchievementDefinitions.easyAchievementsIndex[1]) {
			player.achievementTotal[EASY]++;
			pointsAmount = 1;
		}

		player.achievementPoint += pointsAmount;
		player.achievementPointHistory += pointsAmount;
		player.gfx100(199);
		boolean announce = false;
		if (!player.achievementDifficultyCompleted[EASY]) {
			player.achievementDifficultyCompleted[EASY] = true;
		}
		if (announce) {
			player.playerAssistant.announce(GameMode.getGameModeName(player) + " has completed all achievements.");
			player.getPA().sendScreenshot("all achievements", 2);
		}
	}

	public static void addToAchievementProgress(Player player, String achievementSaveName) {
		if (achievementSaveName.isEmpty()) {
			return;
		}
		if (player.achievementAddedOnce) {
			return;
		}
		player.achievementAddedOnce = true;
		player.achievementProgress.add(achievementSaveName + "=1");
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < player.achievementProgress.size(); index++) {
			String currentString = player.achievementProgress.get(index);
			int lastIndex = currentString.lastIndexOf("=");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;

			for (int i = 0; i < finalIncomeList.size(); i++) {
				if (finalIncomeList.get(i).contains(matchToFind)) {
					int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
					int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).substring(lastIndex + 1));
					int finalValueAdded = (finalNumberValue + numberValue);
					finalIncomeList.remove(i);
					finalIncomeList.add(i, matchToFind + "=" + finalValueAdded);
					finalIncomeListHas = true;
				}
			}

			if (!finalIncomeListHas) {
				finalIncomeList.add(currentString);
			}
		}
		player.achievementProgress = finalIncomeList;
	}
	private static void displayScrollTitles(Player player) {
		int minimumIndex = 0;
		int maximumIndex = 0;
		if (player.lastAchievementDifficulty.contains("EASY")) {
			minimumIndex = AchievementDefinitions.easyAchievementsIndex[0];
			maximumIndex = AchievementDefinitions.easyAchievementsIndex[1] + 1;
		}

		int totalAmount = maximumIndex - minimumIndex;
		double amount = (double) totalAmount * 15.1;
		InterfaceAssistant.setFixedScrollMax(player, 22294, (int) amount);

		int indexOrder = 0;
		for (int index = minimumIndex; index < maximumIndex; index++) {
			if (isAchievementCompleted(player, AchievementDefinitions.getDefinitions()[index].achievementId)) {
				player.getPA().changeTextColour(22295 + indexOrder, ServerConstants.GREEN_HEX);
			} else if (getCurrentAchievementProgress(player, AchievementDefinitions.getDefinitions()[index].achievementId, false, false) > 0) {
				player.getPA().changeTextColour(22295 + indexOrder, ServerConstants.YELLOW_HEX);
			} else {
				player.getPA().changeTextColour(22295 + indexOrder, ServerConstants.RED_HEX);
			}
			String title = AchievementDefinitions.getDefinitions()[index].achievementTitle;
			player.getPA().sendFrame126(title, 22295 + indexOrder);
			indexOrder++;
		}

		InterfaceAssistant.clearFrames(player, 22295 + indexOrder, 22345);
	}

	private static void updateAchievementDetails(Player player, int indexButton, boolean clicked) {
		if (player.lastAchievementDifficulty.contains("EASY")) {
			if (indexButton > AchievementDefinitions.easyAchievementsIndex[1]) {
				return;
			}
		}
		String description1 = AchievementDefinitions.getDefinitions()[indexButton].descriptionSubText1;
		player.getPA().sendFrame126(description1, 22284);
		player.getPA().sendFrame126(AchievementDefinitions.getDefinitions()[indexButton].descriptionSubText2, 22285);
		player.getPA().sendFrame126("Title unlocked: " + AchievementDefinitions.getDefinitions()[indexButton].titleUnlocked, 22292);
		for (int index = 0; index < AchievementDefinitions.getDefinitions()[indexButton].itemRewards.length; index++) {
			int achievementItem = AchievementDefinitions.getDefinitions()[indexButton].itemRewards[index];
			if (achievementItem == 0) {
				player.getPA().sendFrame34(22293, 0, index, 1);
				continue;
			}
			player.getPA().sendFrame34(22293, achievementItem, index, 1);
		}
		int completeAmount = AchievementDefinitions.getDefinitions()[indexButton].completeAmount;
		int currentProgress = Achievements.getCurrentAchievementProgress(player, AchievementDefinitions.getDefinitions()[indexButton].achievementId, false, clicked);
		if (currentProgress == 0 && isAchievementCompleted(player, AchievementDefinitions.getDefinitions()[indexButton].achievementId)) {
			currentProgress = completeAmount;
		}
		player.getPA().sendMessage(":packet:achievementpercentage " + calculatePercentage(currentProgress, completeAmount));
		updateProgressText(player, currentProgress, indexButton);
	}

	private static void updateProgressText(Player player, int currentProgress, int indexButton) {
		int completeAmount = AchievementDefinitions.getDefinitions()[indexButton].completeAmount;
		player.getPA().sendFrame126(
				currentProgress >= completeAmount ? "Complete!" : "Progress: " + Misc.formatRunescapeStyle(currentProgress) + "/" + Misc.formatRunescapeStyle(completeAmount),
				22263);
	}

	public static int getArraylistCount(String matchToFind, ArrayList<String> string) {
		for (int index = 0; index < string.size(); index++) {
			String currentString = string.get(index);
			if (currentString.isEmpty()) {
				continue;
			}
			int lastIndex = currentString.lastIndexOf("=");
			String name = currentString.substring(0, lastIndex);
			currentString = currentString.replace(name, "");
			int amount = Integer.parseInt(currentString.replace("=", ""));

			if (name.equals(matchToFind)) {
				return amount;
			}
		}
		return 0;
	}

	public static boolean isAchievementCompleted(Player player, int achievementId) {
		for (int index = 0; index < player.achievementsCompleted.size(); index++) {
			if (Integer.toString(achievementId).equals(player.achievementsCompleted.get(index))) {
				return true;
			}
		}
		return false;
	}

	private static void updateCompletedAmount(Player player) {
		int amount = 0;
		if (player.lastAchievementDifficulty.contains("EASY")) {
			amount = AchievementDefinitions.easyAchievementsIndex[1] + 1;
		}


		for (int index = 0; index < DIFFICULTY_NAMES.length; index++) {
			if (player.lastAchievementDifficulty.equals(DIFFICULTY_NAMES[index])) {
				player.getPA().sendFrame126("Completed: " + player.achievementTotal[index] + "/" + amount, 22265);
				break;
			}
		}
	}

	private static void clearAchievementDetails(Player player) {
		player.getPA().sendFrame126("", 22284);
		player.getPA().sendFrame126("", 22285);
		player.getPA().sendFrame126("", 22292);
		player.getPA().sendMessage(":packet:achievementpercentage " + 0);
		for (int index = 0; index < 12; index++) {
			player.getPA().sendFrame34(22293, 0, index, 1);
		}
	}

	public static void displayAchievementInterface(Player player) {

		clearAchievementDetails(player);
		player.getPA().setTextClicked(player.lastAchievementClicked + 22295, true);
		displayScrollTitles(player);
		updateCompletedAmount(player);
		if (player.lastAchievementClicked >= 0) {
			updateAchievementDetails(player, player.lastAchievementClicked, false);
		}

		player.getPA().displayInterface(22260);
	}

	public static boolean isAchievementButton(Player player, int buttonId) {
		switch (buttonId) {
			case 109096:
				displayAchievementInterface(player);
				break;

			// Back button on Achievement rewards interface.
			case 75108:
				displayAchievementInterface(player);
				break;
		}
		if (buttonId >= 87023 && buttonId <= 87082) {
			player.getPA().setTextClicked((buttonId - 87023) + 22295, true);
			updateAchievementDetails(player, (buttonId - 87023), true);
			player.lastAchievementClicked = (buttonId - 87023);
			return true;
		}
		for (int index = 0; index < DIFFICULTY_BUTTONS.length; index++) {
			if (buttonId == DIFFICULTY_BUTTONS[index]) {
				InterfaceAssistant.scrollUp(player);
				player.lastAchievementDifficulty = DIFFICULTY_NAMES[index];
				player.playerAssistant.sendMessage(":packet:cleartextclicked");
				player.getPA().setInterfaceClicked(22260, 22269 + (index * 3), true);
				displayScrollTitles(player);
				clearAchievementDetails(player);
				updateCompletedAmount(player);
				player.lastAchievementClicked = -1;
				return true;
			}
		}


		for (int index = 0; index < REWARDS_DIFFICULTY_BUTTONS.length; index++) {
			if (buttonId == REWARDS_DIFFICULTY_BUTTONS[index]) {
				player.lastAchievementRewardsDifficulty = DIFFICULTY_NAMES[index];
				player.getPA().setInterfaceClicked(19302, 19311 + (index * 3), true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculate the percentage of the current task/level completion.
	 *
	 * @param currentAmount The current amount of Nomad kills/Barrows runs being assessed.
	 * @param minimumAmount The minimum value to show 0% completion.
	 * @param maximumAmount The maximum value to show 100% completion.
	 * @return The percentage of the current task/level completion.
	 */
	public static int calculatePercentage(int currentAmount, int maximumAmount) {
		double value1 = maximumAmount;
		double value2 = currentAmount;
		value2 /= value1;
		value2 *= 100.0;
		if (currentAmount == 0) {
			return 0;
		} else if (currentAmount > maximumAmount) {
			return 100;
		}
		return (int) (value2);
	}


	public static int[] ACHIEVEMENT_KILLS_LIST =
			{10, 30, 60, 100, 150};

	public final static int EASY = 0;

	public final static int MEDIUM = 1;

	public final static int HARD = 2;

	public final static int ELITE = 3;

	public final static int LEGENDARY = 4;

	/**
	 * Check npc achievement completionist. All achievementSaveName that is "NPC TASK: Abyssal Demon" etc.
	 *
	 * @param player
	 * @param npcName
	 */
	public static void checkNpcTask(Player player, String npcName) {
		for (int index = 0; index < AchievementDefinitions.npcTasksData.size(); index++) {
			String string[] = AchievementDefinitions.npcTasksData.get(index).split("-");
			if (string[1].equals(npcName)) {
				Achievements.checkCompletionMultiple(player, string[0]);
				// Do not add break, because there is Chaos elemental task in medium, hard and elite.
			}
		}
	}

	public static void claimReward(Player player) {
		int itemId = 13145;
		if (player.achievementDifficultyCompleted[Achievements.EASY] && !player.achievementRewardClaimed[Achievements.EASY]) {
			if (!ItemAssistant.hasInventorySlotsAlert(player, 2)) {
				return;
			}
			ItemAssistant.addItem(player, 995, 500000);
			ItemAssistant.addItem(player, itemId, 1);
			player.achievementRewardClaimed[Achievements.EASY] = true;
		} else if (player.achievementDifficultyCompleted[Achievements.MEDIUM] && !player.achievementRewardClaimed[Achievements.MEDIUM]) {
			if (!ItemAssistant.hasInventorySlotsAlert(player, 3)) {
				return;
			}
			ItemAssistant.addItem(player, 995, 1500000);
			ItemAssistant.addItem(player, itemId, 2);
			player.achievementRewardClaimed[Achievements.MEDIUM] = true;
		} else if (player.achievementDifficultyCompleted[Achievements.HARD] && !player.achievementRewardClaimed[Achievements.HARD]) {
			if (!ItemAssistant.hasInventorySlotsAlert(player, 5)) {
				return;
			}
			ItemAssistant.addItem(player, 995, 3000000);
			ItemAssistant.addItem(player, 13069, 1);
			ItemAssistant.addItem(player, itemId, 3);
			player.achievementRewardClaimed[Achievements.HARD] = true;
		} else if (player.achievementDifficultyCompleted[Achievements.ELITE] && !player.achievementRewardClaimed[Achievements.ELITE]) {
			if (!ItemAssistant.hasInventorySlotsAlert(player, 2)) {
				return;
			}
			ItemAssistant.addItem(player, 14011, 1);
			player.achievementRewardClaimed[Achievements.ELITE] = true;
		} else {
			player.getPA().sendMessage("No rewards to claim.");
			player.getPA().closeInterfaces(true);
		}
	}
}
