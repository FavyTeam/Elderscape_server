package game.content.profile;

import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresHallOfFame;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.RecipeForDisaster;
import game.npc.data.NpcDefinition;
import game.npc.pet.BossPetDrops;
import game.player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utility.Misc;

/**
 * Npc kill count tracker.
 *
 * @author MGT Madness, created on 05-01-2015.
 */
public class NpcKillTracker {

	public static int getAllBossKills(Player player) {
		int total = 0;
		for (int index = 0; index < BossPetDrops.NORMAL_BOSS_DATA.length; index ++) {
			total += getSpecificBossKillCount(player, NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[index]].name);
		}
		return total;
	}

	public static void addNpcKill(Player player, int npcId) {

		for (int index = 0; index < RecipeForDisaster.NPC_WAVE_LIST.length; index++) {
			if (npcId == RecipeForDisaster.NPC_WAVE_LIST[index]) {
				return;
			}
		}

		String npcName = NpcDefinition.getDefinitions()[npcId].name;
		if (npcName.contains("Revenant")) {
			npcName = "Revenant";
		}
		sendBossKillCountMessage(player, npcName, npcId);
		player.npcKills.add(npcName + "=1");
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < player.npcKills.size(); index++) {
			String currentString = player.npcKills.get(index);
			int lastIndex = currentString.lastIndexOf("=");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;
			for (int i = 0; i < finalIncomeList.size(); i++) {
				int lastIndex1 = finalIncomeList.get(i).lastIndexOf("=");
				String matchToFind1 = finalIncomeList.get(i).substring(0, lastIndex1);
				if (matchToFind1.equals(matchToFind)) {
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
		player.npcKills = finalIncomeList;
		sortNpcKills(player);
		player.bossScoreCapped = getBossKillsScore(player.npcKills, true);
		player.bossScoreUnCapped = getBossKillsScore(player.npcKills, false);
		PlayerTitle.checkCompletionMultiple(player, "66 67 68 69 70 71", "");
		ProfileRank.rankPopUp(player, "ADVENTURER");
		Achievements.checkNpcTask(player, npcName);
		if (player.bossScoreUnCapped >= 3000) {
			HighscoresHallOfFame.enterToHallOfFame(player, "3000 Boss Score");
		} else if (player.bossScoreUnCapped >= 2000) {
			HighscoresHallOfFame.enterToHallOfFame(player, "2000 Boss Score");
		} else if (player.bossScoreUnCapped >= 1000) {
			HighscoresHallOfFame.enterToHallOfFame(player, "1000 Boss Score");
		}
	}

	private static void sendBossKillCountMessage(Player player, String npcName, int npcId) {
		boolean forceEnter = false;
		if (HighscoresDaily.getInstance().currentDailyHighscores.equals("Boss kills")) {
			forceEnter = true;
		}
		if (!player.bossKillCountMessage && !forceEnter) {
			return;
		}
		boolean sendMessage = false;

		// TzTok-Jad
		if (npcId == 6506) {
			sendMessage = true;
		}
		for (int i = 0; i < BossPetDrops.NORMAL_BOSS_DATA.length; i++) {
			if (NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]] == null) {
				Misc.printDontSave("Boss is null: " + BossPetDrops.NORMAL_BOSS_DATA[i]);
				continue;
			}
			if (npcName.equals(NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]].name)) {
				sendMessage = true;
				break;
			}
		}
		if (sendMessage) {
			if (HighscoresDaily.getInstance().currentDailyHighscores.equals("Boss kills")) {
				HighscoresDaily.getInstance()
				               .sortHighscores(player, 1, NpcKillTracker.getNpcAmount(player.npcKills, "BOSS"), NpcKillTracker.getHighestBossKilled(player), player.getGameMode());
			}
			if (!player.bossKillCountMessage) {
				return;
			}
			player.getPA().sendMessage("Your " + npcName + " kill count is: " + ServerConstants.RED_COL + (getSpecificBossKillCount(player, npcName) + 1) + ".");
		}
	}

	/**
	 * Sort npc kills on log-out.
	 *
	 * @param player
	 */
	@SuppressWarnings(
			{"unchecked", "rawtypes"})
	public static void sortNpcKills(Player player) {

		// Sorting. in order.
		Map<String, Integer> valueTest = new HashMap<String, Integer>();
		for (int i = 0; i < player.npcKills.size(); i++) {
			String[] args = player.npcKills.get(i).split("=");
			valueTest.put(args[0], Integer.parseInt(args[1]));
		}

		List list = new LinkedList(valueTest.entrySet());

		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// Swap o2 and o1 below to reverse the order.
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		player.npcKills.clear();
		for (Object string : sortedMap.keySet()) {
			Object kills = sortedMap.get(string);
			player.npcKills.add(string.toString() + "=" + kills.toString());
		}
	}

	/**
	 * if i add more onto this, add it to the end of the array and be sure to edit
	 * the getNpcAmount & getAdventurerRankNumber & addToPlayerNpcKillCount & getBossKillsScore method to
	 * calculate the boss/monsters correctly
	 * /**
	 *
	 * @return The highest boss name and kill count.
	 */
	public static String getHighestBossKilled(Player player) {
		int killCountBoss = 0;
		String bossName = "";
		for (int index = 0; index < player.npcKills.size(); index++) {
			String[] args = player.npcKills.get(index).split("=");
			String npcName = args[0];
			for (int i = 0; i < BossPetDrops.NORMAL_BOSS_DATA.length; i++) {
				if (npcName.equals(NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]].name)) {
					int npcKills = Integer.parseInt(args[1]);
					if (npcKills > killCountBoss) {
						killCountBoss = npcKills;
						bossName = npcName;
					}
				}
			}

		}
		int maximumNameLength = bossName.length() <= 12 ? bossName.length() : 12;
		return bossName.substring(0, maximumNameLength) + " " + killCountBoss;
	}

	public static int getSpecificBossKillCount(Player player, String npcName) {
		for (int index = 0; index < player.npcKills.size(); index++) {
			String[] args = player.npcKills.get(index).split("=");
			if (args[0].equals(npcName)) {
				int npcKills = Integer.parseInt(args[1]);
				return npcKills;
			}

		}
		return 0;
	}

	/**
	 * Calculate elo/score for boss kills.
	 *
	 * @param capped True, to limit the elo per boss. This is only true when calculating Adventurer rank which is 100% needed.
	 */
	public static int getBossKillsScore(ArrayList<String> npcKillArray, boolean capped) {
		int elo = 0;

		// Calculate boss elo excluding Jad.
		for (int index = 0; index < npcKillArray.size(); index++) {
			String[] args = npcKillArray.get(index).split("=");
			String npcName = args[0];
			for (int i = 0; i < BossPetDrops.NORMAL_BOSS_DATA.length; i++) {
				if (NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]] == null) {
					continue;
				}
				if (npcName.equals(NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]].name)) {
					int npcKills = Integer.parseInt(args[1]);
					if (npcKills <= 50) {
						elo += npcKills * 6;
					} else {
						// If capped, maximim elo from an Npc is 300, which is 500 kills.
						elo += 50 * 6;

						// if Un-capped, then player can get above 600 elo, but after that it is *3 instead of *6 per kill.
						if (!capped) {
							npcKills -= 100;
							elo += npcKills * 3;
						}
					}
				}
			}

		}

		// Calculate Jad kills elo.
		for (int index = 0; index < npcKillArray.size(); index++) {
			String[] args = npcKillArray.get(index).split("=");
			String npcName = args[0];
			if (npcName.equals("TzTok-Jad")) {
				int jadKills = Integer.parseInt(args[1]);
				if (jadKills <= 20) {
					elo += jadKills * 10;
				} else {
					elo += 20 * 10;
					if (!capped) {
						jadKills -= 20;
						elo += jadKills * 5;
					}
				}

			}
		}
		return elo;
	}


	/**
	 * @param npcType To count the total kills of the given type.
	 * @return The npc amount of the npcType given.
	 */
	public static int getNpcAmount(ArrayList<String> npcKills, String npcType) {
		int totalBossKills = 0;
		int totalNormalKills = 0;
		for (int index = 0; index < npcKills.size(); index++) {
			String[] args = npcKills.get(index).split("=");
			String npcName = args[0];
			boolean boss = false;
			for (int i = 0; i < BossPetDrops.NORMAL_BOSS_DATA.length; i++) {
				if (npcName.equals(NpcDefinition.getDefinitions()[BossPetDrops.NORMAL_BOSS_DATA[i]].name)) {
					totalBossKills += Integer.parseInt(args[1]);
					boss = true;
					break;
				}
			}

			if (npcType.equals("NORMAL") && !boss) {
				totalNormalKills += Integer.parseInt(args[1]);
			}
		}
		if (npcType.equals("BOSS")) {

			return totalBossKills;
		} else {

			return totalNormalKills;
		}
	}

	/**
	 * Update the Pvm page with all the npc kill counts, in order from highest to lowest.
	 *
	 * @param player The associated player.
	 */
	public static void viewSortedStatistics(Player player, ArrayList<String> npcKills) {
		int index = 0;
		for (int i = 0; i < npcKills.size(); i++) {
			String[] args = npcKills.get(index).split("=");
			String npcName = args[0];
			int npcKillCount = Integer.parseInt(args[1]);
			player.getPA().sendFrame126(npcName + ": " + npcKillCount, 25755 + i);
			index++;
		}
		double amount = (index) * 15.1;
		InterfaceAssistant.setFixedScrollMax(player, 25741, (int) amount);
		InterfaceAssistant.clearFrames(player, 25755 + index, 25854);

	}
}
