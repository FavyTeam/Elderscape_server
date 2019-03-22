package game.content.profile;


import game.content.achievement.AchievementStatistics;
import game.content.highscores.HighscoresHallOfFame;
import game.content.starter.GameMode;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Player killing rank and Adventurer rank system.
 *
 * @author MGT Madness, created on 14-01-2015.
 */
public class ProfileRank {

	public final static String[] ranks =
			{
					"Bronze V",
					"Bronze IV",
					"Bronze III",
					"Bronze II",
					"Bronze I",

					"Iron V",
					"Iron IV",
					"Iron III",
					"Iron II",
					"Iron I",

					"Steel V",
					"Steel IV",
					"Steel III",
					"Steel II",
					"Steel I",

					"Black V",
					"Black IV",
					"Black III",
					"Black II",
					"Black I",

					"Mithril V",
					"Mithril IV",
					"Mithril III",
					"Mithril II",
					"Mithril I",

					"Adamant V",
					"Adamant IV",
					"Adamant III",
					"Adamant II",
					"Adamant I",

					"Rune V",
					"Rune IV",
					"Rune III",
					"Rune II",
					"Rune I",

					"Dragon V",
					"Dragon IV",
					"Dragon III",
					"Dragon II",
					"Dragon I",

					"Barrows VI",
					"Barrows V",
					"Barrows IV",
					"Barrows III",
					"Barrows II",
					"Barrows I",

					"Godsword V",
					"Godsword IV",
					"Godsword III",
					"Godsword II",
					"Godsword I"
			};

	public static int getPkingRankNumber(int meleeMainKills, int hybridKills, int pureKills, int berserkerKills, int rangedTankKills) {
		double elo = 0;
		int[] killsArray = new int[5];
		killsArray[0] = meleeMainKills;
		killsArray[1] = hybridKills;
		killsArray[2] = pureKills;
		killsArray[3] = berserkerKills;
		killsArray[4] = rangedTankKills;

		for (int i = 0; i < killsArray.length; i++) {
			double perElo = 0;
			perElo = (double) killsArray[i];
			if (perElo > 200.0) {
				perElo = 200.0;
			}
			elo += perElo;
		}
		elo = (elo / 20.0);
		return (int) elo;
	}

	public static void rankPopUp(Player player, String rankType) {
		switch (rankType) {
			case "ADVENTURER":
				int updatedRank = getAdventurerRankNumber(player.getTotalLevel(), player.bossScoreCapped, player.getBarrowsRunCompleted(), player.getClueScrollsCompleted(),
				                                          player.titleTotal, player.achievementTotal);
				if (player.currentAdventurerRank != updatedRank) {
					AchievementStatistics.showAchievementPopup(player, "Adventurer Rank Level Up!", "Congratulations!", "Promoted to <col=ffb000>" + ranks[updatedRank]);
					player.currentAdventurerRank = updatedRank;
					player.gfx100(199);
					if (ranks[updatedRank].contains("Dragon") || ranks[updatedRank].contains("Barrows") || ranks[updatedRank].contains("Godsword")) {
						player.playerAssistant.announce(GameMode.getGameModeName(player) + " has been promoted to " + ranks[updatedRank] + " Adventurer!");
						player.getPA().sendScreenshot(ranks[updatedRank] + " adventurer", 2);
					}
				}
				break;

			case "PKER":
				int updatedRank1 = getPkingRankNumber(player.getMeleeMainKills(), player.getHybridKills(), player.getPureKills(), player.getBerserkerPureKills(),
				                                      player.getRangedTankKills());
				if (player.currentPkerRank != updatedRank1) {
					AchievementStatistics.showAchievementPopup(player, "Pker Rank Level Up!", "Congratulations!", "Promoted to: <col=ffb000>" + ranks[updatedRank1]);
					player.currentPkerRank = updatedRank1;
					player.gfx100(199);
					if (ranks[updatedRank1].contains("Dragon") || ranks[updatedRank1].contains("Barrows") || ranks[updatedRank1].contains("Godsword")) {
						player.playerAssistant.announce(GameMode.getGameModeName(player) + " has been promoted to " + ranks[updatedRank1] + " Pker!");

						if (ranks[updatedRank1].contains("Rune")) {
							HighscoresHallOfFame.enterToHallOfFame(player, "Rune Pking");
						} else if (ranks[updatedRank1].contains("Adamant")) {
							HighscoresHallOfFame.enterToHallOfFame(player, "Adamant Pking");
						} else if (ranks[updatedRank1].contains("Dragon")) {
							HighscoresHallOfFame.enterToHallOfFame(player, "Dragon Pking");
						} else if (ranks[updatedRank1].contains("Barrows")) {
							HighscoresHallOfFame.enterToHallOfFame(player, "Barrows Pking");
						} else if (ranks[updatedRank1].contains("Godsword")) {
							HighscoresHallOfFame.enterToHallOfFame(player, "Godsword Pking");
						}
						player.getPA().sendScreenshot(ranks[updatedRank1] + " pker", 2);
					}
				}
				break;
		}
	}

	/**
	 * Save current pker and adventurer ranks.
	 *
	 * @param player The associated player.
	 */
	public static void saveCurrentRanks(Player player) {
		player.currentAdventurerRank = getAdventurerRankNumber(player.getTotalLevel(), player.bossScoreCapped, player.getBarrowsRunCompleted(), player.getClueScrollsCompleted(),
		                                                       player.titleTotal, player.achievementTotal);
		player.currentPkerRank = getPkingRankNumber(player.getMeleeMainKills(), player.getHybridKills(), player.getPureKills(), player.getBerserkerPureKills(),
		                                            player.getRangedTankKills());
	}

	public static int getAdventurerRankNumber(int totalLevel, int bossScoreCapped, int barrowsRuns, int clueScrolls, int[] titles, int[] achievements) {
		double elo = 0;
		elo += (totalLevel / 5.0044); // 415.4 elo maximum
		elo += (bossScoreCapped / 7.0); // Max is 4400 (including Jad), so divided by 7 = 628 elo maximum

		//69 titles on 19-11-2016.
		int titlesCompleted = titles[0] + titles[1] + titles[2];
		elo += titlesCompleted * 8; // +552 elo.

		// 143 achievements on 19-11-2016.
		int achievementsDone = achievements[0] + achievements[1] + achievements[2] + achievements[3];
		elo += achievementsDone * 6; // +858 elo.

		//2654 maximum.
		elo = (elo / 53.0);

		// Incase i add new content and forget to balance this.
		if (elo > 50) {
			elo = 50;
		}
		return (int) elo;
	}

	private final static int[][] items =
			{

					// Dagger, sword, scimitar, battleaxe, 2h.

					// Bronze.
					{1205, 1117, 1075},
					{1277, 1117, 1075},
					{1321, 1117, 1075},
					{1375, 1117, 1075},
					{1307, 1117, 1075},

					// Iron.
					{1203, 1115, 1067},
					{1279, 1115, 1067},
					{1323, 1115, 1067},
					{1363, 1115, 1067},
					{1309, 1115, 1067},

					// Steel.
					{1207, 1119, 1069},
					{1281, 1119, 1069},
					{1325, 1119, 1069},
					{1365, 1119, 1069},
					{1311, 1119, 1069},

					// Black.
					{1217, 1125, 1077},
					{1277, 1125, 1077},
					{1321, 1125, 1077},
					{1367, 1125, 1077},
					{1307, 1125, 1077},

					// Mithril.
					{1209, 1121, 1071},
					{1285, 1121, 1071},
					{1329, 1121, 1071},
					{1369, 1121, 1071},
					{1315, 1121, 1071},

					// Adamant.
					{1211, 1123, 1073},
					{1287, 1123, 1073},
					{1331, 1123, 1073},
					{1371, 1123, 1073},
					{1317, 1123, 1073},

					// Rune.
					{1213, 1127, 1079},
					{1289, 1127, 1079},
					{1333, 1127, 1079},
					{1373, 1127, 1079},
					{1319, 1127, 1079},

					// Dragon
					{1377, 3140, 4087},
					{1434, 3140, 4087},
					{7158, 3140, 4087},
					{4587, 3140, 4087},
					{1215, 3140, 4087},

					// ahrim, karil, torag, verac, guthan, dharok
					{4710, 4712, 4714},
					{4734, 4736, 4738},
					{4747, 4749, 4751},
					{4755, 4757, 4759},
					{4726, 4728, 4730},
					{4718, 4720, 4722},

					// Godsword
					{11838, 11828, 11830},
					{11808, 11828, 11830},
					{11806, 11828, 11830},
					{11804, 11832, 11834},
					{11802, 11832, 11834},
			};

	public static void applyRank(Player player, String rankType) {
		String rankName = "";
		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (rankType.equals("PLAYER KILLING")) {

			int rank = getPkingRankNumber(player.isProfileSearchOnline ? searched.getMeleeMainKills() : ProfileSearch.meleeMainKills,
			                              player.isProfileSearchOnline ? searched.getHybridKills() : ProfileSearch.hybridKills,
			                              player.isProfileSearchOnline ? searched.getPureKills() : ProfileSearch.pureKills,
			                              player.isProfileSearchOnline ? searched.getBerserkerPureKills() : ProfileSearch.berserkerPureKills,
			                              player.isProfileSearchOnline ? searched.getRangedTankKills() : ProfileSearch.rangedTankKills);
			rankName = ranks[rank];
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][0] + " " + 25374);
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][1] + " " + 25375);
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][2] + " " + 25376);
		} else if (rankType.equals("ADVENTURER")) {
			int rank = getAdventurerRankNumber(player.isProfileSearchOnline ? searched.getTotalLevel() : ProfileSearch.totalLevel,
			                                   player.isProfileSearchOnline ? searched.bossScoreCapped : ProfileSearch.bossScoreCapped,
			                                   player.isProfileSearchOnline ? searched.getBarrowsRunCompleted() : ProfileSearch.barrowsRunCompleted,
			                                   player.isProfileSearchOnline ? searched.getClueScrollsCompleted() : ProfileSearch.clueScrollsCompleted,
			                                   player.isProfileSearchOnline ? searched.titleTotal : ProfileSearch.titleTotal,
			                                   player.isProfileSearchOnline ? searched.achievementTotal : ProfileSearch.achievementTotal);
			rankName = ranks[rank];
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][0] + " " + 25377);
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][1] + " " + 25378);
			player.playerAssistant.sendMessage(":packet:itemchange " + items[rank][2] + " " + 25379);
		}

		player.getPA().sendFrame126(rankName, rankType.equals("PLAYER KILLING") ? 25367 : 25368);
	}

}
