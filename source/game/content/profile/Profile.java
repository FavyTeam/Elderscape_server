package game.content.profile;



import game.content.achievement.AchievementStatistics;
import game.content.achievement.Achievements;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.PlayerRank;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.skilling.SkillingStatistics;
import game.content.starter.GameMode;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

/**
 * Profile system.
 *
 * @author MGT Madness, created on 28-12-2015.
 */
public class Profile {

	/**
	 * @param player The associated player.
	 * @param button The button used by the player.
	 * @return True, if the button belongs to the profile interface.
	 */
	public static boolean isProfileButton(Player player, int button) {
		String tabSelected = "";
		switch (button) {

			// Profile button.
			case 109100:
				openUpProfileInterface(player);
				break;

			case 99234:
				checkSameOnlinePlayerSearched(player, false);
				SpecialAttackTracker.displayInterface(player, false);
				break;

			case 79027:
				checkSameOnlinePlayerSearched(player, false);
				SpecialAttackTracker.displayInterface(player, true);
				break;

			// Back button.
			case 99177:
				player.lastProfileTabText = "PKING";
				Profile.viewCorrectTab(player, player.lastProfileTabText, true);
				break;

			case 98244:
				tabSelected = "INFO";
				player.getPA().setInterfaceClicked(25320, 25332, true);
				break;

			case 98248:
				tabSelected = "PKING";
				player.getPA().setInterfaceClicked(25320, 25336, true);
				break;

			case 98252:
				tabSelected = "SKILLING";
				player.getPA().setInterfaceClicked(25320, 25340, true);
				break;

			case 99000:
				tabSelected = "PVM";
				player.getPA().setInterfaceClicked(25320, 25344, true);
				break;

			case 99004:
				tabSelected = "MISC";
				player.getPA().setInterfaceClicked(25320, 25348, true);
				break;
		}

		if (!tabSelected.isEmpty()) {
			changeTab(player, tabSelected);
			return true;
		}

		return false;
	}


	public static void openUpProfileInterface(Player player) {
		if (System.currentTimeMillis() - player.timeClickedProfileButton <= 600) {
			return;
		}
		player.timeClickedProfileButton = System.currentTimeMillis();
		player.setProfileNameSearched(player.getPlayerName());

		if (player.lastProfileTabText.equals("INFO")) {
			player.getPA().setInterfaceClicked(25320, 25332, true);
		}
		player.profileSearchOnlineName = player.getPlayerName();
		player.isProfileSearchOnline = true;
		player.setProfileSearchOnlinePlayerId(player.getPlayerId());
		Profile.viewCorrectTab(player, player.lastProfileTabText, false);

	}


	/**
	 * Change the profile tab.
	 *
	 * @param player The associated player.
	 * @param tabText The tab to change to.
	 */
	public static void changeTab(Player player, String tabText) {

		if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
			return;
		}
		if (player.profilePrivacySearched && tabText.equals("PVM") && !player.getPlayerName().equals(player.getProfileNameSearched())) {
			player.playerAssistant.sendMessage(Misc.capitalize(player.getProfileNameSearched()) + " has Pvm privacy on.");
			return;
		}
		player.lastProfileTabText = tabText;
		viewCorrectTab(player, tabText, false);
	}

	/**
	 * Check if the player i searched for that was online, is still online and same person.
	 */
	public static void checkSameOnlinePlayerSearched(Player player, boolean forceOfflineSearch) {
		if (forceOfflineSearch) {
			player.isProfileSearchOnline = false;
			return;
		}
		if (player.isProfileSearchOnline) {
			Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
			boolean nulled = searched == null;
			if (nulled || !searched.getPlayerName().equals(player.profileSearchOnlineName)) {
				player.isProfileSearchOnline = false;
			}
		}
	}

	/**
	 * View the data of the tab changed to.
	 *
	 * @param player The associated player.
	 * @param forceOfflineSearch
	 */
	public static void viewCorrectTab(Player player, String tabText, boolean forceOfflineSearch) {

		checkSameOnlinePlayerSearched(player, forceOfflineSearch);

		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (!player.isProfileSearchOnline) {
			ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), "PVM");
			ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), "PKING");
			player.profilePrivacySearched = ProfileSearch.profilePrivacyOn;
		} else {
			player.profilePrivacySearched = searched.profilePrivacyOn;
		}
		if (player.profilePrivacySearched && !player.getProfileNameSearched().equals(player.getPlayerName())) {
			player.getPA().sendFrame126("@red@X", 25347);
		} else {
			player.getPA().sendFrame126("Pvm", 25347);
		}

		if (!player.isProfileSearchOnline) {
			ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), "INFO");
		}
		if (!player.isProfileSearchOnline) {
			if (ProfileSearch.gameMode.equals("Bot")) {
				player.getPA().sendMessage(Misc.capitalize(player.getProfileNameSearched()) + " is a bot, profile does not exist.");
				return;
			}
		} else {
			if (GameMode.getGameMode(player, "Bot")) {
				player.getPA().sendMessage(Misc.capitalize(player.getProfileNameSearched()) + " is a bot, profile does not exist.");
				return;
			}
		}
		if (!player.getProfileNameSearched().toLowerCase().equals(player.getPlayerName().toLowerCase())) {
			Achievements.checkCompletionSingle(player, 1007);
		}
		switch (tabText) {
			case "INFO":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();
				if (!player.isProfileSearchOnline) {
					ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), tabText);
				}
				viewInfoPage(player);
				break;
			case "PKING":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();
				if (!player.isProfileSearchOnline) {
					ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), tabText);
				}
				viewPlayerKillingPage(player);
				break;
			case "SKILLING":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();

				if (!player.isProfileSearchOnline) {
					ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), tabText);
				}
				viewSkillingPage(player);
				break;
			case "PVM":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();
				viewPvmPage(player);
				break;
			case "MISC":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();

				if (!player.isProfileSearchOnline) {
					ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), tabText);
				}
				viewMiscPage(player);
				break;
			case "WEAPON PVP":
				if (System.currentTimeMillis() - player.profileSearchDelay < 300) {
					break;
				}
				player.profileSearchDelay = System.currentTimeMillis();

				if (!player.isProfileSearchOnline) {
					ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), tabText);
				}
				SpecialAttackTracker.displayInterface(player, false);
				break;
		}
	}

	public static void viewInfoPage(Player player) {
		// Only use file data because other players will see different data because char file hasn't updated.
		player.getPA().sendFrame126("Profile of " + Misc.capitalize(player.getProfileNameSearched()), 25325);
		String rank = "";

		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		rank = PlayerRank.getIconText(player.isProfileSearchOnline ? searched.playerRights : ProfileSearch.playerRights, true) + PlayerRank.getRankName(rank,
		                                                                                                                                                player.isProfileSearchOnline ?
				                                                                                                                                                searched.playerRights :
				                                                                                                                                                ProfileSearch.playerRights);
		if (rank.isEmpty()) {
			rank = "None";
		}
		player.getPA().sendFrame126("Server rank: " + rank, 25353);
		player.getPA().sendFrame126("Game mode: " + Misc.capitalize(player.isProfileSearchOnline ? searched.getGameMode() : ProfileSearch.gameMode), 25354);
		player.getPA().sendFrame126("Total level: " + (player.isProfileSearchOnline ? searched.getTotalLevel() : ProfileSearch.totalLevel), 25355);
		player.getPA().sendFrame126("Boss score: " + (player.isProfileSearchOnline ? searched.bossScoreUnCapped : ProfileSearch.bossScoreUnCapped), 25356);
		player.getPA().sendFrame126("Wilderness kills: " + (player.isProfileSearchOnline ? searched.getWildernessKills(false) : ProfileSearch.wildernessKills), 25357);
		player.getPA().sendFrame126(player.isProfileSearchOnline ? searched.biographyLine1 : ProfileSearch.biographyLine1, 25359);
		player.getPA().sendFrame126(player.isProfileSearchOnline ? searched.biographyLine2 : ProfileSearch.biographyLine2, 25360);
		player.getPA().sendFrame126(player.isProfileSearchOnline ? searched.biographyLine3 : ProfileSearch.biographyLine3, 25361);
		player.getPA().sendFrame126(player.isProfileSearchOnline ? searched.biographyLine4 : ProfileSearch.biographyLine4, 25362);
		player.getPA().sendFrame126(player.isProfileSearchOnline ? searched.biographyLine5 : ProfileSearch.biographyLine5, 25373);
		player.getPA().sendFrame126(
				"Time spent: " + GameTimeSpent.getPercentagePlayed(player.isProfileSearchOnline ? searched.timeSpent : ProfileSearch.timeSpent, GameTimeSpent.PKING) + "% Pk, "
				+ GameTimeSpent.getPercentagePlayed(player.isProfileSearchOnline ? searched.timeSpent : ProfileSearch.timeSpent, GameTimeSpent.SKILLING) + "% Skill, "
				+ GameTimeSpent.getPercentagePlayed(player.isProfileSearchOnline ? searched.timeSpent : ProfileSearch.timeSpent, GameTimeSpent.PVM) + "% Pvm", 25371);
		ProfileRank.applyRank(player, "PLAYER KILLING");
		ProfileRank.applyRank(player, "ADVENTURER");
		player.getPA().displayInterface(25320);
	}

	private final static int[] SKILL_TAB_SKILL_ID_ORDER =
			{0, 2, 1, 4, 5, 6, 20, 3, 16, 15, 17, 12, 9, 18, 14, 13, 10, 7, 11, 8, 19};

	/**
	 * View the Pvm page.
	 *
	 * @param player The associated player.
	 */
	public static void viewPvmPage(Player player) {

		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (player.profilePrivacySearched && !player.getPlayerName().equals(player.getProfileNameSearched())) {
			player.playerAssistant.sendMessage(Misc.capitalize(player.getProfileNameSearched()) + " has Pvm privacy on.");
			if (!player.isProfileSearchOnline) {
				ProfileSearch.loadCharacterFile(player.getProfileNameSearched(), "INFO");
			}
			player.profilePrivacySearched = player.isProfileSearchOnline ? searched.profilePrivacyOn : ProfileSearch.profilePrivacyOn;
			changeTab(player, "INFO");
			viewInfoPage(player);
			return;
		}
		NpcKillTracker.viewSortedStatistics(player, player.isProfileSearchOnline ? searched.npcKills : ProfileSearch.npcKills);
		player.getPA().sendFrame126(
				"Monster kills: " + NpcKillTracker.getNpcAmount(player.isProfileSearchOnline ? searched.npcKills : ProfileSearch.npcKills, "NORMAL") + ", Boss kills: "
				+ NpcKillTracker.getNpcAmount(player.isProfileSearchOnline ? searched.npcKills : ProfileSearch.npcKills, "BOSS"), 25745);
		player.getPA().sendFrame126("Profile of " + Misc.capitalize(player.getProfileNameSearched()), 25325);
		if (!FileUtility.fileExists("backup/logs/rare drop log/" + player.getProfileNameSearched() + ".txt")) {
			player.getPA().sendFrame126("No rare drops", 25746);
			InterfaceAssistant.clearFrames(player, 25855, 25954);
			player.getPA().displayInterface(25740);
			return;
		}
		RareDropLog.viewRareDropLog(player, player.getProfileNameSearched());
		player.getPA().sendFrame126("Rare drops: " + RareDropLog.amountOfLines, 25746);
		player.getPA().displayInterface(25740);
	}

	/**
	 * View the Skilling page.
	 *
	 * @param player The associated player.
	 */
	public static void viewSkillingPage(Player player) {
		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		long totalXp = (ProfileSearch.getExperienceTotal(player.isProfileSearchOnline ? searched.skillExperience : ProfileSearch.skillExperience,
		                                                 player.isProfileSearchOnline ? searched.getAbleToEditCombat() : ProfileSearch.ableToEditCombat,
		                                                 player.isProfileSearchOnline ? searched.combatExperienceGainedAfterMaxed : ProfileSearch.combatExperienceGainedAfterMaxed)
		                / 1000000);
		String[] text =
				{
						"Total experience: " + totalXp + "m",
						"Highest: " + ProfileSearch.getHighestSkillXp(player.isProfileSearchOnline ? searched.getAbleToEditCombat() : ProfileSearch.ableToEditCombat,
						                                              player.isProfileSearchOnline ? searched.skillExperience : ProfileSearch.skillExperience,
						                                              player.isProfileSearchOnline ?
								                                              searched.combatExperienceGainedAfterMaxed :
								                                              ProfileSearch.combatExperienceGainedAfterMaxed),
						"Bones buried: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.BONES_BURIED] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.BONES_BURIED]),
						"Bones on altar: " + (player.isProfileSearchOnline ?
								                      searched.skillingStatistics[SkillingStatistics.BONES_ON_ALTAR] :
								                      ProfileSearch.skillingStatistics[SkillingStatistics.BONES_ON_ALTAR]),
						"High alchemy: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.HIGH_ALCHEMY] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.HIGH_ALCHEMY]),
						"Runes crafted: " + (player.isProfileSearchOnline ?
								                     searched.skillingStatistics[SkillingStatistics.RUNE_ESSENCE_CRAFTED] :
								                     ProfileSearch.skillingStatistics[SkillingStatistics.RUNE_ESSENCE_CRAFTED]),
						"Agility laps: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.AGILITY_LAPS] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.AGILITY_LAPS]),
						"Potions made: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.POTIONS_MADE] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.POTIONS_MADE]),
						"Stalls thieved: " + (player.isProfileSearchOnline ?
								                      searched.skillingStatistics[SkillingStatistics.STALLS_THIEVED] :
								                      ProfileSearch.skillingStatistics[SkillingStatistics.STALLS_THIEVED]),
						"Pickpockets: " + (player.isProfileSearchOnline ?
								                   searched.skillingStatistics[SkillingStatistics.PICKPOCKETS] :
								                   ProfileSearch.skillingStatistics[SkillingStatistics.PICKPOCKETS]),
						"Gems crafted: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.GEMS_CRAFTED] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.GEMS_CRAFTED]),
						"Leather crafted: " + (player.isProfileSearchOnline ?
								                       searched.skillingStatistics[SkillingStatistics.LEATHER_CRAFTED] :
								                       ProfileSearch.skillingStatistics[SkillingStatistics.LEATHER_CRAFTED]),
						"Bows strung: " + (player.isProfileSearchOnline ?
								                   searched.skillingStatistics[SkillingStatistics.BOWS_MADE] :
								                   ProfileSearch.skillingStatistics[SkillingStatistics.BOWS_MADE]),
						"Slayer tasks: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.SLAYER_TASKS] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.SLAYER_TASKS]),
						"Ores mined: " + (player.isProfileSearchOnline ?
								                  searched.skillingStatistics[SkillingStatistics.ORES_MINED] :
								                  ProfileSearch.skillingStatistics[SkillingStatistics.ORES_MINED]),
						"Bars smelted: " + (player.isProfileSearchOnline ?
								                    searched.skillingStatistics[SkillingStatistics.BARS_SMELTED] :
								                    ProfileSearch.skillingStatistics[SkillingStatistics.BARS_SMELTED]),
						"Items smithed: " + (player.isProfileSearchOnline ?
								                     searched.skillingStatistics[SkillingStatistics.ITEMS_SMITHED] :
								                     ProfileSearch.skillingStatistics[SkillingStatistics.ITEMS_SMITHED]),
						"Fish caught: " + (player.isProfileSearchOnline ?
								                   searched.skillingStatistics[SkillingStatistics.FISH_CAUGHT] :
								                   ProfileSearch.skillingStatistics[SkillingStatistics.FISH_CAUGHT]),
						"Fish cooked: " + (player.isProfileSearchOnline ?
								                   searched.skillingStatistics[SkillingStatistics.FISH_COOKED] :
								                   ProfileSearch.skillingStatistics[SkillingStatistics.FISH_COOKED]),
						"Logs burnt: " + (player.isProfileSearchOnline ?
								                  searched.skillingStatistics[SkillingStatistics.LOGS_BURNT] :
								                  ProfileSearch.skillingStatistics[SkillingStatistics.LOGS_BURNT]),
						"Logs from trees: " + (player.isProfileSearchOnline ?
								                       searched.skillingStatistics[SkillingStatistics.LOGS_GAINED] :
								                       ProfileSearch.skillingStatistics[SkillingStatistics.LOGS_GAINED]),
						"Seeds planted: " + (player.isProfileSearchOnline ?
								                     searched.skillingStatistics[SkillingStatistics.SEEDS_PLANTED] :
								                     ProfileSearch.skillingStatistics[SkillingStatistics.SEEDS_PLANTED]),
						"Jewelry created: " + (player.isProfileSearchOnline ?
								                       searched.skillingStatistics[SkillingStatistics.JEWELRY_CREATED] :
								                       ProfileSearch.skillingStatistics[SkillingStatistics.JEWELRY_CREATED]),
				};
		for (int i = 0; i < text.length; i++) {
			player.getPA().sendFrame126(text[i], 25688 + i);
		}

		int interfaceIdIndex = 0;
		for (int i = 0; i < SKILL_TAB_SKILL_ID_ORDER.length; i++) {
			if (SKILL_TAB_SKILL_ID_ORDER[i] <= 6 && (player.isProfileSearchOnline ? searched.getAbleToEditCombat() : ProfileSearch.ableToEditCombat)) {
				player.getPA().sendFrame126("Xp: " + (13034431 + (player.isProfileSearchOnline ?
						                                                  searched.combatExperienceGainedAfterMaxed[SKILL_TAB_SKILL_ID_ORDER[i]] :
						                                                  ProfileSearch.combatExperienceGainedAfterMaxed[SKILL_TAB_SKILL_ID_ORDER[i]])),
				                            (25602 + interfaceIdIndex));
			} else {
				player.getPA().sendFrame126("Xp: " + (player.isProfileSearchOnline ?
						                                      searched.skillExperience[SKILL_TAB_SKILL_ID_ORDER[i]] :
						                                      ProfileSearch.skillExperience[SKILL_TAB_SKILL_ID_ORDER[i]]), (25602 + interfaceIdIndex));
			}
			interfaceIdIndex += 4;
		}

		interfaceIdIndex = 0;
		for (int i = 0; i < SKILL_TAB_SKILL_ID_ORDER.length; i++) {
			if (SKILL_TAB_SKILL_ID_ORDER[i] <= 6 && (player.isProfileSearchOnline ? searched.getAbleToEditCombat() : ProfileSearch.ableToEditCombat)) {
				player.getPA().sendFrame126("99", (25603 + interfaceIdIndex));
			} else {
				player.getPA().sendFrame126(
						(player.isProfileSearchOnline ? searched.baseSkillLevel[SKILL_TAB_SKILL_ID_ORDER[i]] : ProfileSearch.skillLevel[SKILL_TAB_SKILL_ID_ORDER[i]]) + "",
						(25603 + interfaceIdIndex));
			}
			interfaceIdIndex += 4;
		}

		player.getPA().sendFrame126("Total Xp: " + totalXp, 25686);
		player.getPA().sendFrame126("Total level: " + (player.isProfileSearchOnline ? searched.getTotalLevel() : ProfileSearch.totalLevel), 25687);
		player.getPA().sendFrame126("Profile of " + Misc.capitalize(player.getProfileNameSearched()), 25325);

		player.getPA().displayInterface(25590);
	}

	/**
	 * View misc page.
	 *
	 * @param player The associated player.
	 */
	public static void viewMiscPage(Player player) {
		player.getPA().sendFrame126("Profile of " + Misc.capitalize(player.getProfileNameSearched()), 25325);

		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (player.isProfileSearchOnline) {
			ProfileSearch.highestZombieWave = searched.highestZombieWave;
			ProfileSearch.zombiePartner = searched.zombiePartner;
			ProfileSearch.clueScrollsCompleted = searched.getClueScrollsCompleted();
			ProfileSearch.barrowsRunCompleted = searched.getBarrowsRunCompleted();
			ProfileSearch.safeKills = searched.safeKills;
			ProfileSearch.safeDeaths = searched.safeDeaths;
			ProfileSearch.deathsToNpc = searched.deathsToNpc;
			ProfileSearch.duelArenaStakes = searched.duelArenaStakes;
			ProfileSearch.tradesCompleted = searched.tradesCompleted;
			ProfileSearch.teleportsUsed = searched.teleportsUsed;
			ProfileSearch.tilesWalked = searched.tilesWalked;
			ProfileSearch.switches = searched.switches;
			ProfileSearch.foodAte = searched.foodAte;
			ProfileSearch.potionDrank = searched.potionDrank;
		}
		String[] text =
				{
						"Zombie wave: " + ProfileSearch.highestZombieWave + " with " + ProfileSearch.zombiePartner,
						"Clue scrolls completed: " + ProfileSearch.clueScrollsCompleted,
						"Barrows runs: " + ProfileSearch.barrowsRunCompleted,
						"Safe pking kills: " + ProfileSearch.safeKills,
						"Safe pking deaths: " + ProfileSearch.safeDeaths,
						"Deaths to Npc: " + ProfileSearch.deathsToNpc,
						"Duel arena stakes: " + ProfileSearch.duelArenaStakes,
						"Trades completed: " + ProfileSearch.tradesCompleted,
						"Teleports used: " + ProfileSearch.teleportsUsed,
						"Tiles walked: " + Misc.formatNumber(ProfileSearch.tilesWalked),
						"Switches: " + Misc.formatNumber(ProfileSearch.switches),
						"Food ate: " + Misc.formatNumber(ProfileSearch.foodAte),
						"Potion doses drank: " + Misc.formatNumber(ProfileSearch.potionDrank),
						"Crystal keys used: " + Achievements.getArraylistCount("crystalKeysUsed",
						                                                       player.isProfileSearchOnline ? searched.achievementProgress : ProfileSearch.achievementProgress),
						"",
						"",
						"",
						"",
						"",
						"",
						"",
				};
		for (int i = 0; i < text.length; i++) {
			player.getPA().sendFrame126(text[i], 25550 + i);
		}

		player.getPA().displayInterface(22052);
	}

	/**
	 * View player kiling page.
	 *
	 * @param player The associated player.
	 */
	public static void viewPlayerKillingPage(Player player) {
		player.getPA().sendFrame126("Profile of " + Misc.capitalize(player.getProfileNameSearched()), 25325);

		Player searched = PlayerHandler.players[player.getProfileSearchOnlinePlayerId()];
		if (player.isProfileSearchOnline) {
			ProfileSearch.wildernessKills = searched.getWildernessKills(false);
			ProfileSearch.wildernessDeaths = searched.getWildernessDeaths(false);
			ProfileSearch.meleeMainKills = searched.getMeleeMainKills();
			ProfileSearch.hybridKills = searched.getHybridKills();
			ProfileSearch.berserkerPureKills = searched.getBerserkerPureKills();
			ProfileSearch.pureKills = searched.getPureKills();
			ProfileSearch.rangedTankKills = searched.getRangedTankKills();
			ProfileSearch.barragesCasted = searched.barragesCasted;
			ProfileSearch.killStreaksRecord = searched.killStreaksRecord;
			ProfileSearch.playerBotKills = searched.playerBotKills;
			ProfileSearch.playerBotDeaths = searched.playerBotDeaths;
			ProfileSearch.pvpTasksCompleted = searched.pvpTasksCompleted;
			ProfileSearch.targetsKilled = searched.targetsKilled;
			ProfileSearch.killsInMulti = searched.killsInMulti;
		}
		double kdr = Misc.getKDR(ProfileSearch.wildernessKills, ProfileSearch.wildernessDeaths);
		double kdr1 = Misc.getKDR(ProfileSearch.meleeMainKills, player.isProfileSearchOnline ?
				                                                        searched.deathTypes[AchievementStatistics.MELEE_MAIN] :
				                                                        ProfileSearch.deathTypes[AchievementStatistics.MELEE_MAIN]);
		double kdr2 = Misc.getKDR(ProfileSearch.hybridKills,
		                          player.isProfileSearchOnline ? searched.deathTypes[AchievementStatistics.HYBRID] : ProfileSearch.deathTypes[AchievementStatistics.HYBRID]);
		double kdr3 = Misc.getKDR(ProfileSearch.berserkerPureKills,
		                          player.isProfileSearchOnline ? searched.deathTypes[AchievementStatistics.BERSERKER] : ProfileSearch.deathTypes[AchievementStatistics.BERSERKER]);
		double kdr4 = Misc.getKDR(ProfileSearch.pureKills,
		                          player.isProfileSearchOnline ? searched.deathTypes[AchievementStatistics.PURE] : ProfileSearch.deathTypes[AchievementStatistics.PURE]);
		double kdr5 = Misc.getKDR(ProfileSearch.rangedTankKills, player.isProfileSearchOnline ?
				                                                         searched.deathTypes[AchievementStatistics.RANGED_TANK] :
				                                                         ProfileSearch.deathTypes[AchievementStatistics.RANGED_TANK]);
		String[] text =
				{
						"Kills: " + ProfileSearch.wildernessKills + ", Deaths: " + ProfileSearch.wildernessDeaths + ", Ratio: " + kdr,
						"@yel@Melee main",
						ProfileSearch.meleeMainKills + " | " + (player.isProfileSearchOnline ?
								                                        searched.deathTypes[AchievementStatistics.MELEE_MAIN] :
								                                        ProfileSearch.deathTypes[AchievementStatistics.MELEE_MAIN]) + " | " + kdr1,
						"@yel@Hybrid",
						ProfileSearch.hybridKills + " | " + (player.isProfileSearchOnline ?
								                                     searched.deathTypes[AchievementStatistics.HYBRID] :
								                                     ProfileSearch.deathTypes[AchievementStatistics.HYBRID]) + " | " + kdr2,
						"@yel@Berserker pure",
						ProfileSearch.berserkerPureKills + " | " + (player.isProfileSearchOnline ?
								                                            searched.deathTypes[AchievementStatistics.BERSERKER] :
								                                            ProfileSearch.deathTypes[AchievementStatistics.BERSERKER]) + " | " + kdr3,
						"@yel@Pure",
						ProfileSearch.pureKills + " | " + (player.isProfileSearchOnline ?
								                                   searched.deathTypes[AchievementStatistics.PURE] :
								                                   ProfileSearch.deathTypes[AchievementStatistics.PURE]) + " | " + kdr4,
						"@yel@Ranged tank",
						ProfileSearch.rangedTankKills + " | " + (player.isProfileSearchOnline ?
								                                         searched.deathTypes[AchievementStatistics.RANGED_TANK] :
								                                         ProfileSearch.deathTypes[AchievementStatistics.RANGED_TANK]) + " | " + kdr5,

						"Kills in multi: " + ProfileSearch.killsInMulti,
						"Targets killed: " + ProfileSearch.targetsKilled,
						"Highest killstreak: " + ProfileSearch.killStreaksRecord,
						"Bot kills: " + ProfileSearch.playerBotKills,
						"Bot deaths: " + ProfileSearch.playerBotDeaths,
						"Pvp tasks completed: " + ProfileSearch.pvpTasksCompleted,
						"Total Melee damage: " + Misc.formatNumber(player.isProfileSearchOnline ? searched.totalDamage[0] : ProfileSearch.totalDamage[0]),
						"Total Ranged damage: " + Misc.formatNumber(player.isProfileSearchOnline ? searched.totalDamage[1] : ProfileSearch.totalDamage[1]),
						"Total Magic damage: " + Misc.formatNumber(player.isProfileSearchOnline ? searched.totalDamage[2] : ProfileSearch.totalDamage[2]),
						"Total Vengeance damage: " + Misc.formatNumber(player.isProfileSearchOnline ? searched.totalDamage[3] : ProfileSearch.totalDamage[3]),
						"Total Recoil damage: " + Misc.formatNumber(player.isProfileSearchOnline ? searched.totalDamage[4] : ProfileSearch.totalDamage[4]),
						"Barrages casted (Pvp & Pvm): " + ProfileSearch.barragesCasted,
				};
		for (int i = 0; i < text.length; i++) {
			player.getPA().sendFrame126(text[i], 25550 + i);
		}

		player.getPA().displayInterface(25380);
	}



}
