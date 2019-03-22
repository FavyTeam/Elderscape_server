package game.content.achievement;


import core.ServerConstants;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AchievementDefinitions {
	private static ArrayList<Integer> duplicateAchievementIdsList = new ArrayList<Integer>();

	public static final AchievementDefinitions[] DEFINITIONS = new AchievementDefinitions[50];

	public AchievementDefinitions(String achievementTitle, String descriptionSubText1, String descriptionSubText2, String titleUnlocked, int[] itemRewards,
	                              String achievementSaveName, int completeAmount, int achievementId) {
		this.achievementTitle = achievementTitle;
		this.descriptionSubText1 = descriptionSubText1;
		this.descriptionSubText2 = descriptionSubText2;
		this.titleUnlocked = titleUnlocked;
		this.itemRewards = itemRewards;
		this.achievementSaveName = achievementSaveName;
		this.completeAmount = completeAmount;
		this.achievementId = achievementId;
	}

	public static AchievementDefinitions[] getDefinitions() {
		return DEFINITIONS;
	}

	public final String achievementTitle;

	public final String descriptionSubText1;

	public final String descriptionSubText2;

	public final String titleUnlocked;

	public final int[] itemRewards;

	public final String achievementSaveName;

	public final int completeAmount;

	public final int achievementId;

	public static void loadAllAchievements() {
		easyAchievementsIndex[0] = 0;
		loadAchievements("easy achievements");

		storeAchievementIdAndDefinitionIndex();
	}

	private final static String[] textData =
			{"Achievement title:", "Description Sub Text:", "Description Sub Text:", "Title Unlocked:", "Item Rewards:", "Achievement Save Name:", "Complete Amount:",
					"Achievement Id:"
			};

	private static int emptyDefinitionIndex;

	public static int[] easyAchievementsIndex = new int[2];

	public static void loadAchievements(String name) {
		String line = "";
		boolean EndOfFile = false;
		BufferedReader fileLocation = null;
		try {
			fileLocation = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "content/achievements/" + name + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Misc.print(name + " file not found.");
			return;
		}
		try {
			line = fileLocation.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int finishedReadingIndex = 0;
		String achievementTitle = "";
		String descriptionSubText1 = "";
		String descriptionSubText2 = "";
		String titleUnlocked = "";
		int[] itemRewards = new int[10];
		String achievementSaveName = "";
		int completeAmount = -1;
		int achievementId = -1;
		while (!EndOfFile && line != null) {


			finishedReadingIndex++;
			if (finishedReadingIndex == 9) {
				finishedReadingIndex = 0;

				if (!isDuplicateAchievementId(achievementId)) {
					duplicateAchievementIdsList.add(achievementId);
				}
				DEFINITIONS[emptyDefinitionIndex] = new AchievementDefinitions(achievementTitle, descriptionSubText1, descriptionSubText2, titleUnlocked, itemRewards,
				                                                               achievementSaveName, completeAmount, achievementId);
				emptyDefinitionIndex++;
			} else {
				for (int index = 0; index < textData.length; index++) {
					if (line.contains(textData[index])) {
						line = line.replaceAll(textData[finishedReadingIndex - 1] + " ", "");
						line = line.replaceAll(textData[finishedReadingIndex - 1], "");
						switch (finishedReadingIndex) {
							case 1:
								itemRewards = new int[10];
								achievementTitle = line;
								break;
							case 2:
								descriptionSubText1 = line;
								break;
							case 3:
								descriptionSubText2 = line;
								break;
							case 4:
								titleUnlocked = line;
								break;
							case 5:
								String[] split = line.split(" ");
								for (int b = 0; b < split.length; b++) {
									itemRewards[b] = Integer.parseInt(split[b]);
								}
								break;
							case 6:
								achievementSaveName = line;
								break;
							case 7:
								completeAmount = Integer.parseInt(line);
								break;
							case 8:
								achievementId = Integer.parseInt(line);
								if (achievementSaveName.contains("NPC TASK")) {
									String npc = achievementSaveName.substring(achievementSaveName.lastIndexOf(": ") + 2);
									npcTasksData.add(achievementId + "-" + npc);
								}
								if (!achievementSaveName.isEmpty()) {
									idAndSaveName.add(achievementId + " " + achievementSaveName);
								}
								break;
						}
						break;
					}
				}
			}
			try {
				line = fileLocation.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		easyAchievementsIndex[1] = emptyDefinitionIndex;
		if (!isDuplicateAchievementId(achievementId)) {
			duplicateAchievementIdsList.add(achievementId);
		}
		DEFINITIONS[emptyDefinitionIndex] = new AchievementDefinitions(achievementTitle, descriptionSubText1, descriptionSubText2, titleUnlocked, itemRewards, achievementSaveName,
		                                                               completeAmount, achievementId);
		emptyDefinitionIndex++;
		try {
			fileLocation.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, Integer> achievementIdAndDefinitionIndex = new HashMap<Integer, Integer>();

	private static void storeAchievementIdAndDefinitionIndex() {
		for (int index = 0; index < AchievementDefinitions.getDefinitions().length; index++) {
			if (AchievementDefinitions.getDefinitions()[index] == null) {
				return;
			}
			achievementIdAndDefinitionIndex.put(AchievementDefinitions.getDefinitions()[index].achievementId, index);
		}
	}


	/**
	 * Store achievement id and Npc name. Used to complete npc kill tasks.
	 */
	public static ArrayList<String> npcTasksData = new ArrayList<String>();

	/**
	 * Store achievement id and achievementSaveName.
	 */
	public static ArrayList<String> idAndSaveName = new ArrayList<String>();

	private static boolean isDuplicateAchievementId(int titleId) {
		if (duplicateAchievementIdsList.contains(titleId)) {
			Misc.printWarning("Duplicate achievement id found at: " + titleId);
			return true;
		}
		return false;
	}
}
