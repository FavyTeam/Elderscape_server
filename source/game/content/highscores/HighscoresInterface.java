package game.content.highscores;


import core.GameType;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.profile.Profile;
import game.content.profile.ProfileRank;
import game.player.Player;
import java.util.ArrayList;
import utility.Misc;

/**
 * Highscores interface.
 *
 * @author MGT Madness, created on 27-01-2016.
 */
public class HighscoresInterface {

	private static ArrayList<String> highscoresTextCategory = new ArrayList<String>();

	public static void loadHighscoresTextCategory() {

		highscoresTextCategory.add("Hall Of Fame");
		highscoresTextCategory.add("Tournament");
		highscoresTextCategory.add("Daily");
		highscoresTextCategory.add("Pker");
		highscoresTextCategory.add("Melee");
		highscoresTextCategory.add("Hybrid");
		highscoresTextCategory.add("Pure");
		highscoresTextCategory.add("Berserker");
		highscoresTextCategory.add("Ranged Tank");
		highscoresTextCategory.add("F2p");
		highscoresTextCategory.add("Boss");
		if (GameType.isOsrsEco()) {
			highscoresTextCategory.add("Skills Normal");
			highscoresTextCategory.add("Skills Gladiator");
		} else {
			highscoresTextCategory.add("Total level");
		}
		highscoresTextCategory.add("Barrows");
	}

	/**
	 * @param player The associated player.
	 * @param buttonId The button used by the player.
	 * @return True, if the button belongs to the highscores interface.
	 */
	public static boolean isHighscoresButton(Player player, int buttonId) {
		int buttonBase = 82088;

		// Clicking on player name buttons.
		for (int index = 0; index < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; index++) {
			if (buttonId == buttonBase + (index * 8)) {
				if (index > (player.currentHighscoresNameList.size() - 1)) {
					return true;
				}
				player.setProfileNameSearched(player.currentHighscoresNameList.get(index));
				Profile.viewCorrectTab(player, player.lastProfileTabText, true);
				return true;
			}
			if (index == 20) // +752 because after index 20, the button is + 752 instead of the usual +8.
			{
				buttonBase += 752;
			}
		}
		if (buttonId >= 82067 && buttonId <= 82082) {
			player.getPA().setTextClicked((buttonId - 82067) + 21059, true);
			player.highscoresTabClicked = (buttonId - 82067);
			displayHighscoresInterface(player);
			InterfaceAssistant.scrollUp(player);
			return true;
		}
		return false;
	}


	private static void sendEmptyFrames(Player player, int index) {
		player.getPA().sendFrame126(" ", 21080 + index);
		player.getPA().sendFrame126(" ", 21080 + (index + 1));
		player.getPA().sendFrame126(" ", 21080 + (index + 2));
		player.getPA().sendFrame126(" ", 21080 + (index + 3));
	}

	public static void displayHighscoresInterface(Player player) {
		Achievements.checkCompletionSingle(player, 1000);
		for (int index = 0; index < highscoresTextCategory.size(); index++) {
			player.getPA().sendFrame126(highscoresTextCategory.get(index), 21059 + index);
		}
		if (player.highscoresTabClicked == -1) {
			player.getPA().setTextClicked((82069 - 82067) + 21059, true);
			player.highscoresTabClicked = 2;
		}
		int index = 0;
		player.currentHighscoresNameList.clear();
		String title = "Highscores";
		switch (player.highscoresTabClicked) {

			case 0:
				HighscoresHallOfFame.displayInterface(player);
				break;

			case 1:
				player.getPA().sendFrame126("Hybrid won:", 21055);
				player.getPA().sendFrame126("Tribrid won:", 21056);
				player.getPA().sendFrame126("Melee won:", 21057);
				for (int i = HighscoresTournament.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresTournament.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresTournament.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresTournament.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresTournament.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(HighscoresTournament.getInstance().highscoresList[i].hybridWon + "", 21080 + (index + 1));
					player.getPA().sendFrame126(HighscoresTournament.getInstance().highscoresList[i].tribridWon + "", 21080 + (index + 2));
					player.getPA().sendFrame126(HighscoresTournament.getInstance().highscoresList[i].meleeWon + "", 21080 + (index + 3));
					index += 8;
				}
				break;

			case 2:
				String dayType = "AM";
				if (Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft("03:00 AM", "TECHNICAL")) > Integer.parseInt(HighscoresDaily.getInstance().getTimeLeft("03:00 PM", "TECHNICAL"))) {
					dayType = "PM";
				}
				title = "Daily " + HighscoresDaily.getInstance().currentDailyHighscores + ": " + HighscoresDaily.getInstance().getTimeLeft("03:00 " + dayType, "HIGHSCORES")
				        + " left, 25k Bm prize!";
				String main = "Boss kills:";
				String extra1 = "Total kills";
				String extra2 = "Most killed:";

				if (!HighscoresDaily.getInstance().currentDailyHighscores.equals("Boss kills")) {
					main = "Kills:";
					extra1 = "Total kills:";
					extra2 = "KDR:";
				}
				player.getPA().sendFrame126(main, 21055);
				player.getPA().sendFrame126(extra1, 21056);
				player.getPA().sendFrame126(extra2, 21057);
				int loops = 0;
				for (int i = HighscoresDaily.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (loops == 30) {
						break;
					}
					loops++;
					if (HighscoresDaily.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresDaily.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							30 - (ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - loops) + ". " + Misc.getIronManCrown(HighscoresDaily.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresDaily.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(HighscoresDaily.getInstance().highscoresList[i].main + "", 21080 + (index + 1));
					player.getPA().sendFrame126(HighscoresDaily.getInstance().highscoresList[i].extra1 + "", 21080 + (index + 2));
					player.getPA().sendFrame126(HighscoresDaily.getInstance().highscoresList[i].extra2 + "", 21080 + (index + 3));
					index += 8;
				}
				break;

			case 3:
				player.getPA().sendFrame126("Rank:", 21055);
				player.getPA().sendFrame126("Kills:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresPker.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresPker.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresPker.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresPker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresPker.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresPker.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresPker.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126((HighscoresPker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + ProfileRank.ranks[HighscoresPker
							                                                                                                                                             .getInstance().highscoresList[i].rankNumber]
					                            + "", 21080 + (index + 1));
					player.getPA().sendFrame126(
							(HighscoresPker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPker.getInstance().highscoresList[i].kill + "",
							21080 + (index + 2));
					player.getPA().sendFrame126(
							(HighscoresPker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPker.getInstance().highscoresList[i].kdr + "",
							21080 + (index + 3));
					index += 8;
				}
				break;

			case 4:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresMelee.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresMelee.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresMelee.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresMelee.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresMelee.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresMelee.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresMelee.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(
							(HighscoresMelee.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresMelee.getInstance().highscoresList[i].kills
							+ "", 21080 + (index + 1));
					player.getPA().sendFrame126(
							(HighscoresMelee.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresMelee.getInstance().highscoresList[i].deaths
							+ "", 21080 + (index + 2));
					player.getPA().sendFrame126(
							(HighscoresMelee.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresMelee.getInstance().highscoresList[i].kdr
							+ "", 21080 + (index + 3));
					index += 8;
				}
				break;

			case 5:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresHybrid.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresHybrid.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresHybrid.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresHybrid.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresHybrid.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresHybrid.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresHybrid.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(
							(HighscoresHybrid.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresHybrid.getInstance().highscoresList[i].kills
							+ "", 21080 + (index + 1));
					player.getPA().sendFrame126(
							(HighscoresHybrid.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresHybrid.getInstance().highscoresList[i].deaths
							+ "", 21080 + (index + 2));
					player.getPA().sendFrame126(
							(HighscoresHybrid.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresHybrid.getInstance().highscoresList[i].kdr
							+ "", 21080 + (index + 3));
					index += 8;
				}
				break;

			case 6:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresPure.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresPure.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresPure.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresPure.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresPure.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresPure.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresPure.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(
							(HighscoresPure.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPure.getInstance().highscoresList[i].kills
							+ "", 21080 + (index + 1));
					player.getPA().sendFrame126(
							(HighscoresPure.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPure.getInstance().highscoresList[i].deaths
							+ "", 21080 + (index + 2));
					player.getPA().sendFrame126(
							(HighscoresPure.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPure.getInstance().highscoresList[i].kdr + "",
							21080 + (index + 3));
					index += 8;
				}
				break;

			case 7:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresBerserker.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresBerserker.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresBerserker.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresBerserker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresBerserker.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresBerserker.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresBerserker.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126((HighscoresBerserker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresBerserker
							                                                                                                                                .getInstance().highscoresList[i].kills
					                            + "", 21080 + (index + 1));
					player.getPA().sendFrame126((HighscoresBerserker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresBerserker
							                                                                                                                                .getInstance().highscoresList[i].deaths
					                            + "", 21080 + (index + 2));
					player.getPA().sendFrame126((HighscoresBerserker.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresBerserker
							                                                                                                                                .getInstance().highscoresList[i].kdr
					                            + "", 21080 + (index + 3));
					index += 8;
				}
				break;
			case 8:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresRangedTank.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresRangedTank.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresRangedTank.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresRangedTank.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresRangedTank.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresRangedTank.getInstance().highscoresList[i].gameMode)
							+ Misc.capitalize(HighscoresRangedTank.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126((HighscoresRangedTank.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresRangedTank
							                                                                                                                                 .getInstance().highscoresList[i].kills
					                            + "", 21080 + (index + 1));
					player.getPA().sendFrame126((HighscoresRangedTank.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresRangedTank
							                                                                                                                                 .getInstance().highscoresList[i].deaths
					                            + "", 21080 + (index + 2));
					player.getPA().sendFrame126((HighscoresRangedTank.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresRangedTank
							                                                                                                                                 .getInstance().highscoresList[i].kdr
					                            + "", 21080 + (index + 3));
					index += 8;
				}
				break;
			case 9:
				player.getPA().sendFrame126("Kills:", 21055);
				player.getPA().sendFrame126("Deaths:", 21056);
				player.getPA().sendFrame126("KDR:", 21057);
				for (int i = HighscoresF2p.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresF2p.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresF2p.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresF2p.getInstance().highscoresList[i].gameMode) + Misc.capitalize(
									HighscoresF2p.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(HighscoresF2p.getInstance().highscoresList[i].kills + "", 21080 + (index + 1));
					player.getPA().sendFrame126(HighscoresF2p.getInstance().highscoresList[i].deaths + "", 21080 + (index + 2));
					player.getPA().sendFrame126(HighscoresF2p.getInstance().highscoresList[i].kdr + "", 21080 + (index + 3));
					index += 8;
				}
				break;
			case 10:
				player.getPA().sendFrame126("Boss Score:", 21055);
				player.getPA().sendFrame126("Kills:", 21056);
				player.getPA().sendFrame126("Most killed:", 21057);
				for (int i = HighscoresPvm.getInstance().highscoresList.length - 1; i > -1; i--) {
					if (HighscoresPvm.getInstance().highscoresList[i].name.isEmpty()) {
						sendEmptyFrames(player, index);
						index += 8;
						continue;
					}
					player.currentHighscoresNameList.add(HighscoresPvm.getInstance().highscoresList[i].name);
					player.getPA().changeTextColour(21080 + index, HighscoresPvm.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
							                                               ServerConstants.YELLOW_HEX :
							                                               HighscoresPvm.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
									                                               0xb0b0b0 :
									                                               ServerConstants.ORANGE_HEX);
					player.getPA().sendFrame126(
							(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresPvm.getInstance().highscoresList[i].gameMode) + Misc.capitalize(
									HighscoresPvm.getInstance().highscoresList[i].name), 21080 + index);
					player.getPA().sendFrame126(
							(HighscoresPvm.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPvm.getInstance().highscoresList[i].score + "",
							21080 + (index + 1));
					player.getPA().sendFrame126(
							(HighscoresPvm.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPvm.getInstance().highscoresList[i].bossKills
							+ "", 21080 + (index + 2));
					player.getPA().sendFrame126((HighscoresPvm.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ? "@yel@" : "") + HighscoresPvm
							                                                                                                                          .getInstance().highscoresList[i].mostKilledBoss
					                            + "", 21080 + (index + 3));
					index += 8;
				}
				break;
			case 11:
				displayTotalLevelNormal(player);
				break;

			case 12:
				if (GameType.isOsrsEco()) {
					displayTotalLevelGladiator(player);
				} else {
					displayBarrows(player);
				}
				break;

			case 13:
				if (GameType.isOsrsEco()) {
					displayBarrows(player);
				}
				break;

		}
		player.getPA().changeTextColour(21044, ServerConstants.YELLOW_HEX);
		player.getPA().sendFrame126(title, 21044);
		if (player.highscoresTabClicked != 0) {
			player.getPA().displayInterface(21030);
		}
	}

	private static void displayTotalLevelNormal(Player player) {
		int index = 0;
		player.getPA().sendFrame126("Total level:", 21055);
		player.getPA().sendFrame126("Total xp:", 21056);
		player.getPA().sendFrame126("Highest skill:", 21057);
		for (int i = HighscoresTotalLevel.getInstance().highscoresList.length - 1; i > -1; i--) {
			if (HighscoresTotalLevel.getInstance().highscoresList[i].name.isEmpty()) {
				sendEmptyFrames(player, index);
				index += 8;
				continue;
			}
			player.currentHighscoresNameList.add(HighscoresTotalLevel.getInstance().highscoresList[i].name);
			player.getPA().changeTextColour(21080 + index, ServerConstants.ORANGE_HEX);
			player.getPA().sendFrame126(
					(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresTotalLevel.getInstance().highscoresList[i].gameMode) + Misc.capitalize(
							HighscoresTotalLevel.getInstance().highscoresList[i].name), 21080 + index);
			player.getPA().sendFrame126(HighscoresTotalLevel.getInstance().highscoresList[i].totalLevel + "", 21080 + (index + 1));
			player.getPA().sendFrame126(HighscoresTotalLevel.getInstance().highscoresList[i].xp + "m", 21080 + (index + 2));
			player.getPA().sendFrame126(HighscoresTotalLevel.getInstance().highscoresList[i].highestSkill + "", 21080 + (index + 3));
			index += 8;
		}
	}

	private static void displayTotalLevelGladiator(Player player) {
		int index = 0;
		player.getPA().sendFrame126("Total level:", 21055);
		player.getPA().sendFrame126("Total xp:", 21056);
		player.getPA().sendFrame126("Highest skill:", 21057);
		for (int i = HighscoresTotalLevelGladiator.getInstance().highscoresList.length - 1; i > -1; i--) {
			if (HighscoresTotalLevelGladiator.getInstance().highscoresList[i].name.isEmpty()) {
				sendEmptyFrames(player, index);
				index += 8;
				continue;
			}
			player.currentHighscoresNameList.add(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].name);
			player.getPA().changeTextColour(21080 + index, ServerConstants.ORANGE_HEX);
			player.getPA().sendFrame126(
					(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].gameMode)
					+ Misc.capitalize(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].name), 21080 + index);
			player.getPA().sendFrame126(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].totalLevel + "", 21080 + (index + 1));
			player.getPA().sendFrame126(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].xp + "m", 21080 + (index + 2));
			player.getPA().sendFrame126(HighscoresTotalLevelGladiator.getInstance().highscoresList[i].highestSkill + "", 21080 + (index + 3));
			index += 8;
		}
	}

	private static void displayBarrows(Player player) {
		int index = 0;
		player.getPA().sendFrame126("Barrows time (s):", 21055);
		player.getPA().sendFrame126("Runs:", 21056);
		player.getPA().sendFrame126("Days ago:", 21057);
		for (int i = HighscoresBarrowsRun.getInstance().highscoresList.length - 1; i > -1; i--) {
			if (HighscoresBarrowsRun.getInstance().highscoresList[i].name.isEmpty()) {
				sendEmptyFrames(player, index);
				index += 8;
				continue;
			}
			long daysAgo = System.currentTimeMillis() - HighscoresBarrowsRun.getInstance().highscoresList[i].time;
			daysAgo /= Misc.getHoursToMilliseconds(24);
			player.currentHighscoresNameList.add(HighscoresBarrowsRun.getInstance().highscoresList[i].name);
			player.getPA().changeTextColour(21080 + index, HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
					                                               ServerConstants.YELLOW_HEX :
					                                               HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ?
							                                               0xb0b0b0 :
							                                               ServerConstants.ORANGE_HEX);
			player.getPA().sendFrame126(
					(ServerConstants.HIGHSCORES_PLAYERS_AMOUNT - i) + ". " + Misc.getIronManCrown(HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode) + Misc.capitalize(
							HighscoresBarrowsRun.getInstance().highscoresList[i].name), 21080 + index);
			player.getPA().sendFrame126((HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
					                             "@yel@" :
					                             HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ? "@gr4@" : "") + HighscoresBarrowsRun
							                                                                                                                                .getInstance().highscoresList[i].barrowsTime
			                            + "", 21080 + (index + 1));
			player.getPA().sendFrame126((HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
					                             "@yel@" :
					                             HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ? "@gr4@" : "") + HighscoresBarrowsRun
							                                                                                                                                .getInstance().highscoresList[i].barrowsRunAmount
			                            + "", 21080 + (index + 2));
			player.getPA().sendFrame126((HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("GLADIATOR") ?
					                             "@yel@" :
					                             HighscoresBarrowsRun.getInstance().highscoresList[i].gameMode.equals("IRON MAN") ? "@gr4@" : "") + daysAgo + "",
			                            21080 + (index + 3));
			index += 8;
		}
	}

}
