package game.content.profile;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Search feature.
 *
 * @author MGT Madness, created on 09-01-2016.
 */
public class ProfileSearch {

	/**
	 * Store character file data into these fields, these fields are later used on to display on the interface.
	 */

	// When updating this, also update the if player is online version.

	public static int playerRights;

	public static String gameMode = "";

	public static boolean ableToEditCombat;

	public static int meleeMainKills;

	public static int hybridKills;

	public static int berserkerPureKills;

	public static int pureKills;

	public static int rangedTankKills;

	public static int[] timeSpent = new int[3];

	public static int[] skillLevel = new int[22];

	public static int[] skillExperience = new int[22];

	public static int[] combatExperienceGainedAfterMaxed = new int[7];

	public static int wildernessKills;

	public static int wildernessDeaths;

	public static int[] deathTypes = new int[6];

	public static int[] totalDamage = new int[6];

	public static int barragesCasted;

	public static int killStreaksRecord;

	public static int pvpTasksCompleted;

	public static int targetsKilled;

	public static int killsInMulti;

	public static int[] maximumSpecialAttack = new int[35];

	public static int[] weaponAmountUsed = new int[35];

	public static int[] maximumSpecialAttackNpc = new int[35];

	public static int[] weaponAmountUsedNpc = new int[35];

	public static int[] skillingStatistics = new int[25];

	public static int[] titleTotal = new int[3];

	public static int[] achievementTotal = new int[4];

	public static ArrayList<String> npcKills = new ArrayList<String>();

	public static ArrayList<String> achievementProgress = new ArrayList<String>();

	public static int totalLevel;

	public static int barrowsRunCompleted;

	public static int clueScrollsCompleted;

	public static boolean profilePrivacyOn;

	public static String biographyLine1 = "";

	public static String biographyLine2 = "";

	public static String biographyLine3 = "";

	public static String biographyLine4 = "";

	public static String biographyLine5 = "";

	public static int safeDeaths;

	public static int safeKills;

	public static int duelArenaStakes;

	public static int tradesCompleted;

	public static int teleportsUsed;

	public static int switches;

	public static int potionDrank;

	public static int foodAte;

	public static int tilesWalked;

	public static int deathsToNpc;

	public static int playerBotKills;

	public static int playerBotDeaths;

	public static int bossScoreCapped;

	public static int bossScoreUnCapped;

	public static int highestZombieWave;

	public static String zombiePartner = "";

	// When updating this, also update the if player is online version.

	/**
	 * Load the pking data from the character file.
	 */
	private static void loadPkingData(String tab, String token, String token2, String[] token3) {
		if (!tab.equals("PKING") && !tab.equals("WEAPON")) {
			return;
		}

		if (token.equals("wildernessKills")) {
			wildernessKills = Integer.parseInt(token2);
		} else if (token.equals("wildernessDeaths")) {
			wildernessDeaths = Short.parseShort(token2);
		} else if (token.equals("deathTypes")) {
			for (int j = 0; j < token3.length; j++) {
				deathTypes[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("totalDamage")) {
			for (int j = 0; j < token3.length; j++) {
				totalDamage[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("barragesCasted")) {
			barragesCasted = Integer.parseInt(token2);
		} else if (token.equals("killStreaksRecord")) {
			killStreaksRecord = Integer.parseInt(token2);
		} else if (token.equals("pvpTasksCompleted")) {
			pvpTasksCompleted = Integer.parseInt(token2);
		} else if (token.equals("targetsKilled")) {
			targetsKilled = Integer.parseInt(token2);
		} else if (token.equals("killsInMulti")) {
			killsInMulti = Integer.parseInt(token2);
		} else if (token.equals("pvpTasksCompleted")) {
			pvpTasksCompleted = Integer.parseInt(token2);
		} else if (token.equals("playerBotKills")) {
			playerBotKills = Integer.parseInt(token2);
		} else if (token.equals("playerBotDeaths")) {
			playerBotDeaths = Integer.parseInt(token2);
		} else if (token.equals("maximumSpecialAttack")) {
			for (int j = 0; j < token3.length; j++) {
				maximumSpecialAttack[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("weaponAmountUsed")) {
			for (int j = 0; j < token3.length; j++) {
				weaponAmountUsed[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("maximumSpecialAttackNpc")) {
			for (int j = 0; j < token3.length; j++) {
				maximumSpecialAttackNpc[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("maximumSpecialAttackNpc")) {
			for (int j = 0; j < token3.length; j++) {
				maximumSpecialAttackNpc[j] = Integer.parseInt(token3[j]);
			}
		}
	}

	private static void loadMiscData(String tab, String token, String token2, String[] token3) {
		if (!tab.equals("MISC")) {
			return;
		}
		if (token.equals("safeKills")) {
			safeKills = Integer.parseInt(token2);
		} else if (token.equals("safeDeaths")) {
			safeDeaths = Integer.parseInt(token2);
		} else if (token.equals("barrowsRunCompleted")) {
			barrowsRunCompleted = Integer.parseInt(token2);
		} else if (token.equals("clueScrollsCompleted")) {
			clueScrollsCompleted = Integer.parseInt(token2);
		} else if (token.equals("duelArenaStakes")) {
			duelArenaStakes = Integer.parseInt(token2);
		} else if (token.equals("tradesCompleted")) {
			tradesCompleted = Integer.parseInt(token2);
		} else if (token.equals("teleportsUsed")) {
			teleportsUsed = Integer.parseInt(token2);
		} else if (token.equals("switches")) {
			switches = Integer.parseInt(token2);
		} else if (token.equals("potionDrank")) {
			potionDrank = Integer.parseInt(token2);
		} else if (token.equals("foodAte")) {
			foodAte = Integer.parseInt(token2);
		} else if (token.equals("tilesWalked")) {
			tilesWalked = Integer.parseInt(token2);
		} else if (token.equals("highestZombieWave")) {
			highestZombieWave = Integer.parseInt(token2);
		} else if (token.equals("zombiePartner")) {
			zombiePartner = token2;
		} else if (token.equals("deathsToNpc")) {
			deathsToNpc = Integer.parseInt(token2);
		} else if (token.equals("achievementProgress")) {
			for (int j = 0; j < token3.length; j++) {
				if (!token3[j].isEmpty()) {
					achievementProgress.add(token3[j]);
				}
			}
		}
	}

	/**
	 * Load the info tab data from the character file.
	 */
	private static void loadInfoData(String tab, String token, String token2, String[] token3) {
		if (!tab.equals("INFO")) {
			return;
		}
		if (token.equals("authority")) {
			playerRights = Integer.parseInt(token2);
		} else if (token.equals("gameMode")) {
			gameMode = token2;
		} else if (token.equals("barrowsRunCompleted")) {
			barrowsRunCompleted = Integer.parseInt(token2);
		} else if (token.equals("bossScoreUnCapped")) {
			bossScoreUnCapped = Integer.parseInt(token2);
		} else if (token.equals("bossScoreCapped")) {
			bossScoreCapped = Integer.parseInt(token2);
		} else if (token.equals("clueScrollsCompleted")) {
			clueScrollsCompleted = Integer.parseInt(token2);
		} else if (token.equals("ableToEditCombat")) {
			ableToEditCombat = Boolean.parseBoolean(token2);
		} else if (token.equals("meleeMainKills")) {
			meleeMainKills = Integer.parseInt(token2);
		} else if (token.equals("hybridKills")) {
			hybridKills = Integer.parseInt(token2);
		} else if (token.equals("berserkerPureKills")) {
			berserkerPureKills = Integer.parseInt(token2);
		} else if (token.equals("pureKills")) {
			pureKills = Integer.parseInt(token2);
		} else if (token.equals("rangedTankKills")) {
			rangedTankKills = Integer.parseInt(token2);
		} else if (token.equals("biographyLine1")) {
			biographyLine1 = token2;
		} else if (token.equals("biographyLine2")) {
			biographyLine2 = token2;
		} else if (token.equals("biographyLine3")) {
			biographyLine3 = token2;
		} else if (token.equals("biographyLine4")) {
			biographyLine4 = token2;
		} else if (token.equals("biographyLine5")) {
			biographyLine5 = token2;
		} else if (token.equals("timeSpent")) {
			for (int j = 0; j < token3.length; j++) {
				timeSpent[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("titleTotal")) {
			for (int j = 0; j < token3.length; j++) {
				titleTotal[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("achievementTotal")) {
			for (int j = 0; j < token3.length; j++) {
				achievementTotal[j] = Integer.parseInt(token3[j]);
			}
		}

	}

	/**
	 * Load the character file.
	 */
	public static void loadCharacterFile(String name, String tab) {
		if (tab.equals("PVM")) {
			npcKills.clear(); // This is the only field that needs clearing.
		} else if (tab.equals("MISC")) {
			achievementProgress.clear(); // This is the only field that needs clearing.
		}
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		boolean EndOfFile = false;
		int ReadMode = 0;
		if (!FileUtility.accountExists(name)) {
			return;
		}
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name.toLowerCase() + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (characterfile == null) {
			return;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token3 = token2.split("\t");
				switch (ReadMode) {
					case 2:
						loadInfoData(tab, token, token2, token3);
						loadPkingData(tab, token, token2, token3);
						loadSkillingData(tab, token, token2, token3);
						loadPvmData(tab, token, token2, token3);
						loadMiscData(tab, token, token2, token3);
						break;

					case 3:
						if (!tab.equals("INFO") && !tab.equals("SKILLING")) {
							break;
						}

						if (token.equals("character-skill")) {
							skillLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
							skillExperience[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
							if (Integer.parseInt(token3[0]) == 20) {
								getOriginalTotalLevel();
							}
						}

						break;
				}
			} else {
				if (line.equals("[CREDENTIALS]")) {
					ReadMode = 1;
				} else if (line.equals("[MAIN]")) {
					ReadMode = 2;
				} else if (line.equals("[SKILLS]")) {
					ReadMode = 3;
				} else if (line.equals("[INVENTORY]")) {
					EndOfFile = true;

				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load the skilling tab data from the character file.
	 */
	private static void loadSkillingData(String tab, String token, String token2, String[] token3) {
		if (!tab.equals("SKILLING")) {
			return;
		}
		if (token.equals("skillingStatistics")) {
			for (int j = 0; j < token3.length; j++) {
				skillingStatistics[j] = Integer.parseInt(token3[j]);
			}
		} else if (token.equals("combatExperienceGainedAfterMaxed")) {
			for (int j = 0; j < token3.length; j++) {
				combatExperienceGainedAfterMaxed[j] = Integer.parseInt(token3[j]);
			}
		}
	}

	/**
	 * Load the Pvm tab data from the character file.
	 */
	private static void loadPvmData(String tab, String token, String token2, String[] token3) {
		if (!tab.equals("PVM")) {
			return;
		}

		if (token.equals("npcKills")) {
			for (int j = 0; j < token3.length; j++) {
				if (!token3[j].isEmpty()) {
					npcKills.add(token3[j]);
				}
			}
		} else if (token.equals("profilePrivacyOn")) {
			profilePrivacyOn = Boolean.parseBoolean(token2);
		}
	}

	/**
	 * Calculate total experience.
	 */
	public static long getExperienceTotal(int[] skillExperience, boolean ableToEditCombat, int[] combatExperienceGainedAfterMaxed) {
		boolean reAdd = false;
		long xp = 0;
		if (ableToEditCombat) {
			for (int i = 0; i <= 6; i++) {
				xp += (13034431 + combatExperienceGainedAfterMaxed[i]);
			}
			reAdd = true;
		}
		for (int i = reAdd ? 7 : 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
			xp += skillExperience[i];
		}
		return xp;
	}

	public static String getHighestSkillXp(boolean maxed, int[] skillExperience, int[] combatExperienceGainedAfterMaxed) {
		int skillIndex = 0;
		int mostXp = 0;
		for (int i = 0; i < skillExperience.length; i++) {
			int experience = skillExperience[i];
			if (i <= 6 && maxed) {
				experience = 13034431 + combatExperienceGainedAfterMaxed[i];
			}
			if (experience > mostXp) {
				mostXp = experience;
				skillIndex = i;
			}
		}

		return ServerConstants.SKILL_NAME[skillIndex] + " " + (mostXp / 1000000) + "m";
	}

	/**
	 * Receive the search string from the client.
	 */
	public static void receiveClientString(Player player, String string) {
		if (string.isEmpty()) {
			return;
		}
		if (string.length() < 7) {
			return;
		}
		if (System.currentTimeMillis() - player.timeSearchedProfile <= 600) {
			return;
		}
		player.timeSearchedProfile = System.currentTimeMillis();
		string = string.substring(7);
		if (string.isEmpty()) {
			return;
		}
		player.isProfileSearchOnline = false;

		// Check if online.
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerName().toLowerCase().equals(string.toLowerCase())) {
				player.profileSearchOnlineName = loop.getPlayerName();
				player.isProfileSearchOnline = true;
				player.setProfileSearchOnlinePlayerId(loop.getPlayerId());
				player.setProfileNameSearched(loop.getPlayerName());
				break;
			}
		}


		// If not online, check character files.
		if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + string + ".txt")) {
			player.playerAssistant.sendMessage(Misc.capitalize(string) + " does not exist.");
			return;
		}
		player.setProfileNameSearched(string);
		Profile.viewCorrectTab(player, player.lastProfileTabText, false);
	}

	/**
	 * Calculate total level.
	 */
	public static int getOriginalTotalLevel() {
		int total = getTotalLevel();
		if (ableToEditCombat) {
			total += 693;
		} else {
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.ATTACK]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.STRENGTH]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.DEFENCE]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.HITPOINTS]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.MAGIC]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.RANGED]);
			total += Skilling.getLevelForExperience(skillExperience[ServerConstants.PRAYER]);
		}
		totalLevel = total;
		return total;
	}

	/**
	 * Calculate total level.
	 */
	public static int getTotalLevel() {
		int total = 0;
		for (int i = 7; i < ServerConstants.getTotalSkillsAmount(); i++) {
			total += skillLevel[i];
		}
		return total;
	}

}
