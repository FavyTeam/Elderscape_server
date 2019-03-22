package game.content.wildernessbonus;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.AchievementStatistics;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresF2p;
import game.content.highscores.HighscoresHallOfFame;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.Skull;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.npc.pet.BossPetDrops;
import game.player.Area;
import game.player.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import utility.FileUtility;
import utility.Misc;

/**
 * Handle the bonus loot that depends on EP and or Target kill.
 *
 * @author MGT Madness
 */

public class KillReward {

	/**
	 * Drop the loot for the player depending on their EP and or target kill.
	 */
	public static void giveLoot(Player attacker, Player victim) {
		if (!requirements(attacker, victim)) {
			killLog.add("No reward: [" + Misc.getDateAndTime() + "] =" + attacker.getCapitalizedName() + "= killed " + victim.getCapitalizedName() + " with "
			            + victim.damageTaken[attacker.getPlayerId()] + " damage at " + attacker.getX() + ", " + attacker.getY() + ", " + attacker.getHeight() + ", with risk: "
			            + Misc.formatRunescapeStyle(attacker.riskedWealth) + " vs " + Misc.formatRunescapeStyle(victim.riskedWealth));
			return;
		}
		killBonus(attacker, victim);
	}

	/**
	 * @param victim The player who died.
	 * @return True, if the attacker and victim have the requirements to continue.
	 */
	private static boolean requirements(Player attacker, Player victim) {
		if (ServerConfiguration.DEBUG_MODE) {
			return true;
		}
		if (victim.getLastAttackedBy() == victim.getPlayerId()) {
			return false;
		}

		if (!ItemAssistant.hasEquipment(attacker) || !ItemAssistant.hasEquipment(victim)) {
			return false;
		}
		if (attacker.addressIp.equals(victim.addressIp)) {
			return false;
		}
		boolean recentSameKillIp = false;
		int ipMatchCount = 0;
		for (int index = 0; index < attacker.killTimes.size(); index++) {
			String parse[] = attacker.killTimes.get(index).split("-");
			String ip = parse[0];
			long time = Long.parseLong(parse[1]);
			if (ip.equals(victim.addressIp)) {
				if (System.currentTimeMillis() - time < 120000) {
					recentSameKillIp = true;
				}
				ipMatchCount++;
			}
		}
		// If i recently killed the same ip or if i already killed this ip 3 times in my last 5 kills.
		// 2 basically means i already killed him twice and i am about to kill him for a 3rd time.
		if (recentSameKillIp || ipMatchCount >= 2) {
			return false;
		}
		return true;
	}


	public static ArrayList<String> killLog = new ArrayList<String>();


	public static ArrayList<String> killTypes = new ArrayList<String>();

	/**
	 * Reward player for getting a kill
	 *
	 * @param victim The player who died.
	 */
	private static void killBonus(Player killer, Player victim) {
		if (!victim.isCombatBot() && killer != null) {
			String logText = "Reward: [" + Misc.getDateAndTime() + "] =" + killer.getCapitalizedName() + "= killed " + victim.getCapitalizedName() + " with " + victim.damageTaken[killer.getPlayerId()] + " damage at " + killer.getX() + ", " + killer.getY() + ", " + killer.getHeight() + ", with risk: " + Misc.formatRunescapeStyle(killer.riskedWealth) + " vs " + Misc.formatRunescapeStyle(victim.riskedWealth);
			killLog.add(logText);
			updateKillStreak(killer, victim);
			killer.killTimes.add(victim.addressIp + "-" + System.currentTimeMillis());
			if (killer.killTimes.size() == 6) {
				killer.killTimes.remove(0);
			}
			dharokPet(killer);
			PlayerTitle.checkCompletionMultiple(killer, "76 77 78 79", "");
			victim.setWildernessDeaths(victim.getWildernessDeaths(false) + 1);
			killer.setWildernessKills(killer.getWildernessKills(false) + 1);
			AchievementStatistics.updateAchievementStatistics(killer, true, victim);
			AchievementStatistics.updateAchievementStatistics(victim, false, null);
			Achievements.checkCompletionMultiple(killer, "76 77 78 79");
			Achievements.checkCompletionMultiple(killer, "1065 1123");
			if (killer.getWildernessKills(true) == 300) {
				HighscoresHallOfFame.enterToHallOfFame(killer, "Kill 300 Players");
			}
			if (Area.inMulti(victim.getX(), victim.getY())) {
				killer.killsInMulti++;
			}
			boolean isF2p = QuickSetUp.isUsingF2pOnly(killer, false, true);
			if (isF2p) {
				victim.deathTypes[5]++;
				killer.setF2pKills(killer.getF2pKills() + 1);
				HighscoresF2p.getInstance().sortHighscores(killer);
				HighscoresF2p.getInstance().sortHighscores(victim);
				killer.lastKillType = "F2P";
				PlayerTitle.unlockPkingTitle(killer, "F2P");
				Achievements.checkCompletionMultiple(killer, "26 27 28 29 30");
			}
			if (WorldEvent.getActiveEvent("F2P PK")) {
				if (isF2p) {
					Artefacts.dropArtefactsAmountWorldEvent(killer, victim, 3);
				}
			}
			if (HighscoresDaily.getInstance().currentDailyHighscores.equals("F2p kills")) {
				if (isF2p) {
					HighscoresDaily.getInstance()
					               .sortHighscores(killer, 1, killer.getF2pKills(), Misc.getKDR(killer.getF2pKills(), killer.deathTypes[5]) + "", killer.getGameMode());
				}
			}
			Artefacts.dropArtefacts(killer, victim);
		} else if (killer != null) {
			killedBotBonus(killer, victim);
		}
	}

	/**
	 * Dharok pet chance.
	 *
	 * @param killer
	 */
	private static void dharokPet(Player killer) {
		if (killer.playerEquipment[ServerConstants.HEAD_SLOT] == 4716 && killer.playerEquipment[ServerConstants.BODY_SLOT] == 4720
		    && killer.playerEquipment[ServerConstants.LEG_SLOT] == 4722) {
			if (Misc.hasOneOutOf(GameMode.getDropRate(killer, Artefacts.DHAROK_PET_DROPRATE))) {
				BossPetDrops.awardBoss(killer, 16015, 16015, 0, "Dharok pking");
			}
		}

	}

	/**
	 * Karil pet chance.
	 *
	 * @param killer
	 */
	public static void karilPet(Player killer) {
		if (Misc.hasOneOutOf(GameMode.getDropRate(killer, Artefacts.KARIL_PET_CHANCE))) {
			BossPetDrops.awardBoss(killer, 16123, 16123, 0, "Ranged tank pking");
		}

	}


	public final static int playerRarity = 105;

	public final static int botMultiplier = 3;

	private static void killedBotBonus(Player killer, Player victim) {
		killer.getPA().sendMessage(ServerConstants.RED_COL + "You have killed a bot instead of a player.");
		killer.playerBotCurrentKillstreak++;
		killer.playerBotKills++;
		if (killer.playerBotCurrentKillstreak > killer.playerBotHighestKillstreak) {
			killer.playerBotHighestKillstreak = killer.playerBotCurrentKillstreak;
		}
		Achievements.checkCompletionSingle(killer, 1006);
		Achievements.checkCompletionMultiple(killer, "1032 1122");
		PlayerTitle.checkCompletionMultiple(killer, "72 73 74 75", "");
		if (GameType.isOsrsPvp()) {
			int amount = Misc.random(30, 100);
			Server.itemHandler
					.createGroundItem(killer, ServerConstants.getMainCurrencyId(), victim.getX(), victim.getY(), victim.getHeight(), amount, false, 0, true, victim.getPlayerName(),
							"", "", "", "killedBotBonus " + victim.getPlayerName());
		}
	}


	/**
	 * Update kill streak statistics of the player.
	 *
	 * @param killer The player who got the kill.
	 * @param victim The player who died.
	 */
	private static void updateKillStreak(Player killer, Player victim) {

		int bloodMoneyLootAmount = 0;
		if (victim.currentKillStreak >= Skull.KILL_STREAK_LEAST) {
			Announcement.announce(killer.getPlayerName() + " has shut down " + victim.getPlayerName() + ", ending a killing spree of " + victim.currentKillStreak + "!",
			                      "<img=29><col=ef1020>");
			for (int index = Skull.SkullData.values().length - 1; index >= 0; index--) {
				Skull.SkullData instance = Skull.SkullData.values()[index];
				if (victim.currentKillStreak >= instance.killstreak) {
					if (GameType.isOsrsPvp()) {
						CoinEconomyTracker.addIncomeList(killer, "shutdown(pking) " + Skull.getShutDownBloodMoney(index));
					}
					bloodMoneyLootAmount += Skull.getShutDownBloodMoney(index);
					killer.getPA().sendMessage("You receive x" + Misc.formatNumber(Skull.getShutDownBloodMoney(index)) + " shutdown blood money!");
					break;
				}
			}
		}
		victim.currentKillStreak = 0;
		victim.playerBotCurrentKillstreak = 0;
		killer.currentKillStreak++;
		if (killer.skullVisualType >= 0) {
			killer.skullVisualType = Skull.getSkullToShow(killer);
		}

		int highestIndex = -1;
		for (int index = 0; index < Skull.SkullData.values().length; index++) {
			Skull.SkullData instance = Skull.SkullData.values()[index];
			if (killer.currentKillStreak >= instance.killstreak) {
				highestIndex = index;
			}
			if (killer.currentKillStreak == instance.killstreak) {
				killer.setAppearanceUpdateRequired(true);
				if (killer.currentKillStreak >= Skull.KILL_STREAK_LEAST) {
					Announcement.announce(killer.getPlayerName() + " is on a killing spree of " + killer.currentKillStreak + ", shut them down for " + Misc.formatNumber(
							Skull.getShutDownBloodMoney(index)) + " blood money!", "<img=29><col=ef1020>");
				}
			}
		}
		if (highestIndex >= 0) {
			if (GameType.isOsrsPvp()) {
				CoinEconomyTracker.addIncomeList(killer, "killstreak(pking) " + Skull.getSkullKillstreakBloodMoney(highestIndex));
			}
			killer.getPA().sendMessage("You receive x" + Misc.formatNumber(Skull.getSkullKillstreakBloodMoney(highestIndex)) + " bonus blood money for being on a killstreak!");
			bloodMoneyLootAmount += Skull.getSkullKillstreakBloodMoney(highestIndex);
		}

		if (killer.currentKillStreak > killer.killStreaksRecord && killer.currentKillStreak > 1) {
			killer.killStreaksRecord = killer.currentKillStreak;
			killer.playerAssistant.sendMessage("You have set a new kill streak of " + killer.killStreaksRecord + "!");
		}
		if (bloodMoneyLootAmount > 0) {
			Server.itemHandler
					.createGroundItem(killer, 13307, victim.getX(), victim.getY(), victim.getHeight(), bloodMoneyLootAmount, false, 0, true, victim.getPlayerName(), "", "", "", "updateKillStreak bloodMoneyLootAmount 13307");
		}
	}

	public final static String LOCATION = "backup/logs/pvp/killtypes.txt";

	public static void readKillTypeLog() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(LOCATION));
			String line;
			while ((line = file.readLine()) != null) {
				killTypes.add(line);
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveKillTypeLog() {
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < killTypes.size(); index++) {
			String currentString = killTypes.get(index);
			int lastIndex = currentString.lastIndexOf(" ");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;

			for (int i = 0; i < finalIncomeList.size(); i++) {
				if (finalIncomeList.get(i).contains(matchToFind)) {
					try {
						int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
						int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).substring(lastIndex + 1));
						int finalValueAdded = (finalNumberValue + numberValue);
						finalIncomeList.remove(i);
						finalIncomeList.add(i, matchToFind + " " + finalValueAdded);
						finalIncomeListHas = true;
					} catch (Exception e) {
						e.printStackTrace();
						Misc.print(e + "");
					}
				}
			}

			if (!finalIncomeListHas) {
				finalIncomeList.add(currentString);
			}
		}
		FileUtility.deleteAllLines(LOCATION);
		FileUtility.saveArrayContents(LOCATION, finalIncomeList);
	}

}
