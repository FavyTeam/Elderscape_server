package game.content.worldevent;

import core.GameType;
import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresTournament;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.ClaimPrize;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.SnowPile;
import game.content.miscellaneous.Teleport;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import network.connection.DonationManager;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Tournament.
 *
 * @author MGT Madness, created on 06-03-2017.
 */
public class Tournament {

	/**
	 * How many game ticks untill tournament starts once it's announced.
	 */
	public final static int TOURNAMENT_WILL_START_IN = 200; // 100 is 1 minute.

	/**
	 * How many seconds untill tournament starts once it's announced.
	 */
	public final static int TOURNAMENT_STARTED_WAIT_TIME = 300; // How many game ticks for the players to gear up once Cowkiller spawns.

	/**
	 * Get prize reward per player entered tournament.
	 */
	private static int getTournamentRewardAmount() {
		return GameType.isOsrsPvp() ? 3000 : 1500000;
	}

	private final static String LAST_EVENT_LOCATION = "backup/logs/tournament event.txt";

	private final static String[] eventLists =
			{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows", "Dharok melee", "Max hybrid", "Main Nh", "Barrows tribrid", "F2P"};

	public final static int[] eventShopIds =
			{78, 79, 76, 77, 80, 81, 82, 86, 87};

	/**
	 * Used for setting combat skills.
	 */
	private final static String[] eventSkillString =
			{"PURE", "BERSERKER", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN", "F2P"};

	public final static int[] ITEMS_CANNOT_DROP =
			{
					//@formatter:off
					10499, // Ava's accumulator
					12695, // Super combat potion(4)
					2503, // Black d'hide body
					8850, // Rune defender
					4587, // Dragon scimitar
					1079, // Rune platelegs
					10551, // Fighter torso
					1215, // Dragon dagger
					5698, // Dragon dagger p++
					4151, // Abyssal whip
					12954, // Dragon defender
					12006, // Abyssal tentacle
					4720, // Dharok's platebody
					4722, // Dharok's platelegs
					4751, // Torag's platelegs
					4749, // Torag's platebody
					4728, // Guthan's platebody
					4730, // Guthan's chainskirt
					4757, // Verac's brassard
					4759, // Verac's plateskirt
					4736, // Karil's leathertop
					11834, // Bandos tassets
					11832, // Bandos chestplate
					//@formatter:on
			};

	public static int locationIndex = -1;

	/**
	 * Using tick count instead of time.
	 */
	private static int tournamentTickCount;

	/**
	 * Amount of players lost this round.
	 */
	private static int currentLostAmount;

	/**
	 * Amount of losses needed to start a new round.
	 */
	private static int lossesNeeded;

	/**
	 * Current tournament status.
	 */
	private static String tournamentStatus = "";

	/**
	 * True if the event is active, started by an Admin via a command.
	 */
	private static boolean tournamentActive;

	public static int playersEnteredTournament;


	/**
	 * List of players that entered the lobby.
	 */
	public static ArrayList<Integer> playerListLobby = new ArrayList<Integer>();


	/**
	 * List of players that are participating in the tournament.
	 */
	public static ArrayList<Integer> playerListTournament = new ArrayList<Integer>();


	/**
	 * List of players that have won the titles, so i can remove it from character file when the next winner of the same title is announced.
	 */
	public static ArrayList<String> tournamentTitleWinners = new ArrayList<String>();

	public static ArrayList<String> debug = new ArrayList<String>();

	/**
	 * Store the time of when the tournament was first announced.
	 */
	public static long timeTournamentAnnounced;

	/**
	 * Current event type.
	 */
	public static String eventType = eventLists[eventLists.length - 1];

	public static void tournamenTick() {
		if (!isTournamentActive()) {
			return;
		}
		tournamentTickCount++;
		debug.add(Misc.getDateAndTime() + ": Here1: " + tournamentTickCount + ", " + getTournamentStatus() + ", " + playerListTournament.size() + ", " + playerListLobby.size() + ", "
		          + lossesNeeded + ", " + currentLostAmount);
		switch (getTournamentStatus()) {
			case "TOURNAMENT ANNOUNCED":
				if (tournamentTickCount == TOURNAMENT_WILL_START_IN) {
					debug.add(Misc.getDateAndTime() + ": Here2");
					tournamentTickCount = 0;
					setTournamentStatus("TOURNAMENT LOBBY WAIT");
					Announcement.announce("The " + eventType + " tournament starts in 3 minutes.", ServerConstants.DARK_BLUE);
					Announcement.announce("Talk to Cow31337Killer at ::tournament to join!", ServerConstants.DARK_BLUE);
					NpcHandler.spawnNpc(null, 4420, 3108, 3499, 0, false, false);
					locationIndex = 0;

					Object object = new Object();
					CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							int secondsLeft = (int) ((TOURNAMENT_STARTED_WAIT_TIME - tournamentTickCount) * 0.6);
							String stringToShow = "Next round in: " + secondsLeft;
							if (!getTournamentStatus().equals("TOURNAMENT LOBBY WAIT")) {
								container.stop();
								stringToShow = "Next round in: ";
							}
							for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
								Player loop = PlayerHandler.players[index];
								if (loop == null) {
									continue;
								}
								if (loop.getHeight() != 20) {
									continue;
								}
								loop.getPA().sendFrame126(stringToShow, 25984);
							}
						}

						@Override
						public void stop() {
						}
					}, 1);
				}
				break;
			case "TOURNAMENT LOBBY WAIT":
				debug.add(Misc.getDateAndTime() + ": Here3");
				if (tournamentTickCount == TOURNAMENT_STARTED_WAIT_TIME) {
					debug.add(Misc.getDateAndTime() + ": Here4");
					tournamentStart();
				} else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 100)) {
					debug.add(Misc.getDateAndTime() + ": Here5");
					Announcement.announce("The " + eventType + " tournament starts in 1 minute.", ServerConstants.DARK_BLUE);
					Announcement.announce("Talk to Cow31337Killer at ::tournament to join!", ServerConstants.DARK_BLUE);
				} else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 200)) {
					debug.add(Misc.getDateAndTime() + ": Here6");
					Announcement.announce("The " + eventType + " tournament starts in 2 minutes.", ServerConstants.DARK_BLUE);
					Announcement.announce("Talk to Cow31337Killer at ::tournament to join!", ServerConstants.DARK_BLUE);
				}
				break;

			case "TOURNAMENT NEXT ROUND":
				debug.add(Misc.getDateAndTime() + ": Here7");
				// 60 seconds passed.
				if (tournamentTickCount == 100) {
					debug.add(Misc.getDateAndTime() + ": Here8");
					currentLostAmount = 0;
					int playersLeft = playerListTournament.size();
					boolean isEvenNumber = playersLeft % 2 == 0;

					if (isEvenNumber) {
						lossesNeeded = playersLeft / 2;
					} else {

						lossesNeeded = (playersLeft - 1) / 2;
					}
					debug.add(Misc.getDateAndTime() + ": Here33: " + lossesNeeded + ", " + playersLeft + ", " + isEvenNumber);
					teleportPlayersToArena();
				}
				break;
		}
	}

	/**
	 * When the tournament has started.
	 */
	private static void tournamentStart() {

		debug.add(Misc.getDateAndTime() + ": Here8: " + playerListLobby.size());
		boolean has8 = playerListLobby.size() >= 8;

		int maximumEntries = 0;
		if (has8) {
			maximumEntries = playerListLobby.size() & ~1; // Rounds the given number to the lowest even number.
			lossesNeeded = maximumEntries / 2;
			debug.add(Misc.getDateAndTime() + ": Here30: " + playerListLobby.size() + ", " + lossesNeeded + ", " + maximumEntries);
		} else {
			Announcement.announce("Not enough players to start, cancelled.", ServerConstants.DARK_BLUE);
			cancelTournament();
			return;
		}
		tournamentTickCount = 0;
		setTournamentStatus("TOURNAMENT STARTED");
		for (int index = 0; index < playerListLobby.size(); index++) {
			Player loop = PlayerHandler.players[playerListLobby.get(index)];
			if (loop == null) {
				lossesNeeded--;
				debug.add(Misc.getDateAndTime() + ": Here32: " + lossesNeeded);
				continue;
			}
			if (loop.isTeleporting()) {
				lossesNeeded--;
				debug.add(Misc.getDateAndTime() + ": Here31: " + lossesNeeded);
				continue;
			}
			playerListTournament.add(playerListLobby.get(index));
		}
		updateText(null);
		playersEnteredTournament = playerListTournament.size();
		Announcement.announce(
				"The " + eventType + " tournament has started. Winner will receive " + Misc.formatRunescapeStyle((playersEnteredTournament * getTournamentRewardAmount())) + " "
				+ ServerConstants.getMainCurrencyName().toLowerCase() + "!", ServerConstants.DARK_BLUE);
		teleportPlayersToArena();

	}

	private static void cancelTournament() {
		debug.add(Misc.getDateAndTime() + ": Tournament cancelled.");
		// Delete cow1337killer npc.
		for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
			Npc npc = NpcHandler.npcs[index];
			if (npc == null) {
				continue;
			}
			if (npc.npcType == 4420) {
				Pet.deletePet(npc);
				break;
			}
		}

		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}
			Teleport.spellTeleport(loop, 3105 + Misc.random(3), 3498 + Misc.random(4), 0, false);
			//Teleport.spellTeleport(player, 3086 + Misc.random(3), 3508 + Misc.random(4), 0, false);
		}
		eventType = "";
		setTournamentActive(false);
		tournamentTickCount = 0;
		setTournamentStatus("");
		currentLostAmount = 0;
		lossesNeeded = 0;
		playerListLobby.clear();
		playerListTournament.clear();
		locationIndex = -1;
		playersEnteredTournament = 0;
	}

	public final static int TOURNAMENT_ARENA_X = 3328;

	public final static int TOURNAMENT_ARENA_Y = 4769;

	public final static int MAXIMUM_ROAM_DISTANCE = 40;

	private final static int TELEPORT_START_X = 3328;

	private final static int TELEPORT_START_Y = 4757;

	private static void teleportPlayersToArena() {
		Collections.shuffle(playerListTournament);

		int pairs = 0;
		int x = 0;
		int y = 0;
		for (int index = 0; index < playerListTournament.size(); index++) {
			Player loop = PlayerHandler.players[playerListTournament.get(index)];
			if (loop == null) {
				continue;
			}
			pairs++;
			if (pairs == 1) {
				x = TOURNAMENT_ARENA_X - 14 + Misc.random(28);
				y = TOURNAMENT_ARENA_Y - 9 + Misc.random(19);
				// If a partner won't be available because it is an odd number player list, then stop and inform the odd player out.
				if ((index + 1) > playerListTournament.size() - 1) {
					// The other finalist logs off.
					if (playerListTournament.size() == 1) {
						finalistDisconnection(loop);
						return;
					}
					Skull.redSkull(loop);
					loop.skullTimer = 144000;
					loop.getPA().sendMessage(ServerConstants.BLUE_COL + "Could not find a challenger for you, you are still in the tournament.");
					return;
				}
				loop.tournamentTarget = playerListTournament.get(index + 1);
				loop.getPA().movePlayer(x, y, 20);
				Player challenger = PlayerHandler.players[playerListTournament.get(index + 1)];
				loop.getPA().sendMessage(ServerConstants.RED_COL + "Your challenger is " + challenger.getPlayerName() + "!");
				loop.getPA().createPlayerHints(10, challenger.getPlayerId());
				loop.setDuelCount(4);
				Skull.redSkull(loop);
				loop.skullTimer = 144000;

				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						loop.duelForceChatCount--;
						loop.getPA().sendMessage(ServerConstants.RED_COL + loop.duelForceChatCount + "");
						if (loop.duelForceChatCount == 0) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						loop.getPA().sendMessage(ServerConstants.RED_COL + "FIGHT!");
						loop.duelForceChatCount = 4;
						loop.resetDamageTaken();
						loop.setDuelCount(0);
					}
				}, 2);

			} else if (pairs == 2) {
				loop.tournamentTarget = playerListTournament.get(index - 1);
				loop.getPA().movePlayer(x + 1, y, 20);
				Player challenger = PlayerHandler.players[playerListTournament.get(index - 1)];
				loop.getPA().sendMessage(ServerConstants.RED_COL + "Your challenger is " + challenger.getPlayerName() + "!");
				loop.getPA().createPlayerHints(10, challenger.getPlayerId());
				pairs = 0;
				loop.setDuelCount(4);
				Skull.redSkull(loop);
				loop.skullTimer = 144000;

				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						loop.duelForceChatCount--;
						loop.getPA().sendMessage(ServerConstants.RED_COL + "" + loop.duelForceChatCount + "");
						if (loop.duelForceChatCount == 0) {
							container.stop();
						}
					}

					@Override
					public void stop() {
						loop.getPA().sendMessage(ServerConstants.RED_COL + "FIGHT!");
						loop.duelForceChatCount = 4;
						loop.resetDamageTaken();
						loop.setDuelCount(0);
					}

				}, 2);
			}
		}

	}

	/**
	 * Added to log out method and log-in, incase server crashes so it won't register the log out part.
	 *
	 * @param player
	 */
	public static void logOutUpdate(Player player, boolean logIn) {
		if (logIn) {
			switch (getTournamentStatus()) {
				case "TOURNAMENT ANNOUNCED":
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " tournament has been announced.");
					break;
				case "TOURNAMENT LOBBY WAIT":
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " tournament will start soon, talk to Cow31337Killer.");
					break;

				case "TOURNAMENT NEXT ROUND":
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "The " + eventType + " tournament is active!");
					break;
			}
		}
		if (player.getHeight() != 20) {
			return;
		}

		// Log in and remove player must be kept incase the server disconnected during tournament, so they are not left lingering around at height 20.
		if (logIn && player.getHeight() == 20) {
			player.getPA().movePlayer(player.getX(), player.getY(), 0);
		}

		ItemAssistant.deleteAllItems(player);
		Combat.updatePlayerStance(player);
		Skull.clearSkull(player);
		if (GameType.isOsrsEco()) {
			for (int index = 0; index < player.baseSkillLevelStoredBeforeTournament.length; index++) {
				player.baseSkillLevel[index] = player.baseSkillLevelStoredBeforeTournament[index];
				player.skillExperience[index] = player.skillExperienceStoredBeforeTournament[index];
			}
			QuickSetUp.heal(player, false, true);
		}

		if (!logIn) {
			player.getPA().createPlayerHints(10, -1);
			if (player.tournamentTarget >= 0) {
				Player other = PlayerHandler.players[player.tournamentTarget];
				if (other != null) {
					playerDied(other, player);
				}
			}
		}
		player.tournamentTarget = -1;
		removeFromTournamentLobby(player.getPlayerId());
	}

	/**
	 * Other player disconnected while in lobby and we are both in finals.
	 *
	 * @param player
	 */
	public static void finalistDisconnection(Player player) {
		player.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
		player.getPA().createPlayerHints(10, -1);
		player.tournamentTarget = -1;
		player.getPA().sendMessage(ServerConstants.RED_COL + "The finalist has disonnected, you are given an automatic win!");
		awardWinner(player);
		cancelTournament();
	}

	private static void awardWinner(Player player) {
		int amount = (playersEnteredTournament * getTournamentRewardAmount());
		if (GameMode.getGameModeContains(player, "IRON MAN")) {
			amount /= 10;
		}
		String title = Misc.capitalize(eventType);
		title = title.replace("Main Hybrid Welfare", "Main Hybrid");
		title = title.replace("Main Hybrid Barrows", "Main Hybrid");
		title = title.replace("Max Hybrid", "Main Hybrid");
		Announcement.announce(
				player.getPlayerName() + " has won " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " and claimed the '#1 "
				+ title + "' title!", ServerConstants.DARK_BLUE);
		ClaimPrize.eventNames.add(player.getPlayerName().toLowerCase() + "-" + amount);
		player.getPA().sendMessage(ServerConstants.GREEN_COL + "::claimevent to claim your tournament prize.");
		player.getPA().sendScreenshot(title, 1);
		removePreviousTitleWinners("#1 " + title);
		PlayerTitle.setTitle(player, "#1 " + title, false, true);
		tournamentTitleWinners.add(player.getPlayerName().toLowerCase() + "-" + "#1 " + title);
		switch (eventType) {
			case "Pure tribrid":
			case "Main Nh":
			case "Barrows tribrid":
				player.tribridTournamentsWon1++;
				break;

			case "Berserker hybrid":
			case "Main hybrid welfare":
			case "Main hybrid barrows":
			case "Max hybrid":
				player.hybridTournamentsWon1++;
				break;

			case "Dharok melee":
				player.meleeTournamentsWon1++;
				break;
		}

		HighscoresTournament.getInstance().sortHighscores(player);

	}


	/**
	 * Using the tournamentTitleWinners arraylist, find the names of the players with the same given titleString and remove it from them if they are online or offline.
	 */
	private static void removePreviousTitleWinners(String titleString) {
		for (int index = 0; index < tournamentTitleWinners.size(); index++) {
			String parse[] = tournamentTitleWinners.get(index).split("-");
			String name = parse[0];
			String title = parse[1];

			// Match found, remove from online or offline.
			if (titleString.equals(title)) {
				boolean online = false;
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].getPlayerName().equalsIgnoreCase(name)) {
							PlayerTitle.setTitle(PlayerHandler.players[i], "", false, false);
							online = true;
							break;
						}
					}
				}

				if (!online) {
					name = name.toLowerCase();
					final String name1 = name;
					new Thread(new Runnable() {
						public void run() {
							try {
								BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name1 + ".txt"));
								String line;
								String input = "";
								while ((line = file.readLine()) != null) {
									if (line.startsWith("playerTitle =")) {
										line = "playerTitle = ";
									}
									input += line + '\n';
								}
								FileOutputStream File = new FileOutputStream(ServerConstants.getCharacterLocation() + name1 + ".txt");
								File.write(input.getBytes());
								file.close();
								File.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();

				}
				tournamentTitleWinners.remove(index);
				break;
			}
		}

	}

	private static void announceToLobby(String text) {
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}
			loop.getPA().sendMessage(text);
		}
	}

	public static void playerDied(Player killer, Player victim) {
		if (Tournament.locationIndex == -1) {
			return;
		}
		victim.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
		victim.tournamentTarget = -1;
		Skull.clearSkull(victim);
		if (killer != null) {
			killer.getPA().createPlayerHints(10, -1);
			killer.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
			killer.tournamentTarget = -1;
			QuickSetUp.heal(killer, false, true);
			announceToLobby(ServerConstants.RED_COL + killer.getPlayerName() + " has knocked out " + victim.getPlayerName() + "!");
		}
		victim.getPA().createPlayerHints(10, -1);
		QuickSetUp.heal(victim, false, true);
		currentLostAmount++;
		for (int index = 0; index < playerListTournament.size(); index++) {
			if (victim.getPlayerId() == playerListTournament.get(index)) {
				playerListTournament.remove(index);
				updateText(null);
				break;
			}
		}
		// 1 player left, means winner!
		if (playerListTournament.size() == 1) {
			if (killer != null) {
				awardWinner(killer);
			}
			awardRunnerUp(victim);
			cancelTournament();
			return;
		}
		if (currentLostAmount == lossesNeeded) {
			tournamentTickCount = 0;
			setTournamentStatus("TOURNAMENT NEXT ROUND");
			Object object = new Object();
			CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					int secondsLeft = (int) ((100 - tournamentTickCount) * 0.6);
					String stringToShow = "Next round in: " + secondsLeft;
					if (secondsLeft < 0) {
						container.stop();
						stringToShow = "Next round in: ";
					}
					for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
						Player loop = PlayerHandler.players[index];
						if (loop == null) {
							continue;
						}
						if (loop.getHeight() != 20) {
							continue;
						}
						loop.getPA().sendFrame126(stringToShow, 25984);
					}
				}

				@Override
				public void stop() {
				}
			}, 1);

			String stage = "finals";
			int playersLeft = playerListTournament.size();
			if (playersLeft == 2) {
				stage = "finals";
			} else if (playersLeft <= 5) {
				stage = "semi-finals";
			} else if (playersLeft <= 8) {
				stage = "quarter-finals";
			} else if (playersLeft <= 16) {
				stage = "group stage 1";
			} else if (playersLeft <= 32) {
				stage = "group stage 2";
			} else if (playersLeft <= 64) {
				stage = "group stage 3";
			} else if (playersLeft <= 128) {
				stage = "group stage 4";
			}
			if (stage.equals("finals")) {
				Player playerOne = PlayerHandler.players[playerListTournament.get(0)];
				Player playerTwo = PlayerHandler.players[playerListTournament.get(1)];
				Announcement.announce("The " + eventType + " final is between " + playerOne.getPlayerName() + " and " + playerTwo.getPlayerName() + "!", ServerConstants.DARK_BLUE);
			} else {
				Announcement.announce("The " + eventType + " tournament has reached the " + stage + "!", ServerConstants.DARK_BLUE);
			}
		}
	}

	private static void awardRunnerUp(Player victim) {
		int amount = (playersEnteredTournament * (getTournamentRewardAmount() / 2));
		if (GameMode.getGameModeContains(victim, "IRON MAN")) {
			amount /= 10;
		}
		ClaimPrize.eventNames.add(victim.getPlayerName().toLowerCase() + "-" + amount);
		Announcement.announce(
				"The runner up " + victim.getPlayerName() + " has won " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + ".",
				ServerConstants.DARK_BLUE);
		victim.getPA().sendMessage(ServerConstants.GREEN_COL + "::claimevent to claim your tournament prize.");

	}

	public static void loadNewTournament(String command) {
		int tournamentId = 0;
		try {
			tournamentId = Integer.parseInt(command.substring(11));
		} catch (Exception e) {

		}
		if (tournamentId > eventLists.length - 1) {
			return;
		}
		debug.add(Misc.getDateAndTime() + ": Here22: " + tournamentId);
		startNewTournament(tournamentId);
	}

	private static void startNewTournament(int tournamentId) {
		cancelTournament();
		setTournamentActive(true);
		setTournamentStatus("TOURNAMENT ANNOUNCED");
		eventType = eventLists[tournamentId];
		//int lowest = getTournamentRewardAmount() * 10;
		int highest = getTournamentRewardAmount() * 50;
		Announcement.announce("The " + eventType + " tournament will start in 2 minutes!", ServerConstants.DARK_BLUE);
		Announcement.announce("Finalists will receive up to " + Misc.formatRunescapeStyle(highest) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "!",
		                      ServerConstants.DARK_BLUE);
		Announcement.announce("Head to ::tournament and wait for Cow31337Killer to spawn to receive free items.", ServerConstants.DARK_BLUE);
		timeTournamentAnnounced = System.currentTimeMillis();

		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				int highest = getTournamentRewardAmount() * 50;
				Announcement.announce("The " + eventType + " tournament will start in 1 minute!", ServerConstants.DARK_BLUE);
				Announcement.announce("Finalists will receive up to " + Misc.formatRunescapeStyle(highest) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "!",
				                      ServerConstants.DARK_BLUE);
				Announcement.announce("Head to ::tournament and wait for Cow31337Killer to spawn to receive free items.", ServerConstants.DARK_BLUE);
			}
		}, 100);

	}

	/**
	 * Load the last event used from the text file and load the location data.
	 */
	public static void tournamentStartUp() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(LAST_EVENT_LOCATION));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					eventType = line;
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/tournament titles.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					tournamentTitleWinners.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the last event used on server shutdown.
	 */
	public static void saveLastEvenType() {
		FileUtility.deleteAllLines(LAST_EVENT_LOCATION);
		FileUtility.addLineOnTxt(LAST_EVENT_LOCATION, eventType);
	}

	public static void talkToCowKiller(Player player) {
		if (!Tournament.getTournamentStatus().equals("TOURNAMENT LOBBY WAIT") && !Tournament.getTournamentStatus().equals("TOURNAMENT STARTED") && !Tournament.getTournamentStatus()
				                                                                                                                                  .equals("TOURNAMENT NEXT ROUND")) {
			return;
		}

		if (!Bank.hasBankingRequirements(player, true)) {
			return;
		}
		QuickSetUp.bankInventoryAndEquipment(player);
		if (ItemAssistant.hasEquipment(player)) {
			player.getPA().sendMessage("You cannot enter the tournament with items.");
			return;
		}
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] > 0) {
				player.getPA().sendMessage("You cannot enter the tournament with items.");
				return;
			}
		}
		if (player.getPetId() > 0 && player.getPetId() != 6869) {
			player.getPA().sendMessage("Pick up your pet before entering tournament!");
			return;
		}
		// 5 would be the player id.
		player.getPA().closeInterfaces(true);
		if (!Teleport.spellTeleport(player, TELEPORT_START_X, TELEPORT_START_Y, 5 * 4, false)) {
			return;
		}
		player.teleBlockEndTime = System.currentTimeMillis() + 20000;
		for (int index = 0; index < player.baseSkillLevelStoredBeforeTournament.length; index++) {
			player.baseSkillLevelStoredBeforeTournament[index] = player.baseSkillLevel[index];
			player.skillExperienceStoredBeforeTournament[index] = player.skillExperience[index];
		}
		for (int index = 0; index < eventLists.length; index++) {
			if (eventLists[index].equals(eventType)) {
				final int value = index;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (eventType.equals("Dharok melee")) {
							QuickSetUp.mainDharokTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("Barrows tribrid")) {
							QuickSetUp.mainBarrowsBridTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("F2P")) {
							QuickSetUp.pureF2pTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("Max hybrid")) {
							QuickSetUp.maxBridTournament(player, eventType, eventSkillString[value]);
						} else {
							QuickSetUp.mainHybridTournament(player, eventType, eventSkillString[value]);
						}
						container.stop();
					}

					@Override
					public void stop() {
					}
				}, 10);
				break;
			}
		}
		playerListLobby.add(player.getPlayerId());
		updateText(player);
	}

	public static void removeFromTournamentLobby(int playerId) {
		if (!isTournamentActive()) {
			return;
		}
		for (int index = 0; index < playerListLobby.size(); index++) {
			if (playerId == playerListLobby.get(index)) {
				playerListLobby.remove(index);
				break;
			}
		}
		for (int index = 0; index < playerListTournament.size(); index++) {
			if (playerId == playerListTournament.get(index)) {
				playerListTournament.remove(index);
				break;
			}
		}
		updateText(null);
	}

	public static void updateText(Player player) {
		if (player != null) {
			player.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
			player.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
		}
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}

			loop.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
			loop.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
		}

	}

	public static void openShop(Player player) {
		int shopId = 0;
		for (int index = 0; index < eventLists.length; index++) {
			if (eventLists[index].equals(eventType)) {
				shopId = eventShopIds[index];
				break;
			}
		}
		if (shopId == 0) {
			return;
		}
		player.getShops().openShop(shopId);
	}

	public static String currentScheduledEvent = "";

	/**
	 * This method is called every minute.
	 */
	public static void currentTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
		Calendar cal = Calendar.getInstance();
		String time = dateFormat.format(cal.getTime());
		Lottery.announcePreDraw(time);
		for (int index = 0; index < eventTimes.size(); index++) {
			if (eventTimes.get(index).contains(time)) {
				currentScheduledEvent = eventTimes.get(index);
				if (eventTimes.get(index).contains("Tournament")) {
					debug.add(Misc.getDateAndTime() + ": Here21: " + eventTimes.get(index));
					Tournament.startNewTournament(Misc.random(eventLists.length - 1));
				} else if (eventTimes.get(index).contains("Blood key")) {
					BloodKey.spawnBloodKey("KEY");
				} else if (eventTimes.get(index).contains("Skotizo")) {
					BloodKey.spawnBloodKey("BOSS");
				} else if (eventTimes.get(index).contains("Daily highscores")) {
					HighscoresDaily.getInstance().dateChanged();
				} else if (eventTimes.get(index).contains("Pk/Pvm wild")) {
					WorldEvent.start20MinutesLastingEvent("WILDERNESS EVENT");
				} else if (eventTimes.get(index).contains("Skilling")) {
					WorldEvent.start20MinutesLastingEvent("SKILLING");
				}
				break;
			}
		}
		if (DonationManager.EVENT_SALE.equals("Christmas")) {
			//@formatter:off
			if (time.equals("10:40 PM") ||
			    time.equals("01:40 AM") ||
			    time.equals("06:40 AM") ||
			    time.equals("10:40 AM") ||
			    time.equals("02:00 PM") ||
			    time.equals("05:00 PM")) {
				SnowPile.spawnSnowpile();
			}
		}
		//@formatter:on
	}

	public static ArrayList<String> eventTimes = new ArrayList<String>();

	public static void loadEventTimes() {
		if (GameType.isOsrsPvp()) {
			eventTimes.add("01:00 PM-Blood key");
			eventTimes.add("02:00 PM-Pk/Pvm wild");
			eventTimes.add("03:00 PM-Daily highscores");
			eventTimes.add("04:00 PM-Skotizo");
			eventTimes.add("05:00 PM-Pk/Pvm wild");
			eventTimes.add("06:00 PM-Blood key");
			eventTimes.add("07:00 PM-Tournament");
			eventTimes.add("08:00 PM-Blood key");
			eventTimes.add("09:00 PM-Skotizo");
			eventTimes.add("10:00 PM-Blood key");
			eventTimes.add("11:00 PM-Skotizo");
			eventTimes.add("12:00 AM-Pk/Pvm wild");
			eventTimes.add("01:00 AM-Tournament");
			eventTimes.add("02:00 AM-Skotizo");
			eventTimes.add("03:00 AM-Daily highscores");
			eventTimes.add("04:00 AM-Pk/Pvm wild");
			eventTimes.add("05:00 AM-Blood key");
			eventTimes.add("06:00 AM-Pk/Pvm wild");
			eventTimes.add("07:00 AM-Skotizo");
			eventTimes.add("09:00 AM-Blood key");
			eventTimes.add("10:00 AM-Tournament");
			eventTimes.add("11:00 AM-Skotizo");
			eventTimes.add("12:00 PM-Pk/Pvm wild");
		} else {
			eventTimes.add("01:00 PM-Blood key");
			eventTimes.add("02:00 PM-Skilling");
			eventTimes.add("03:00 PM-Daily highscores");
			eventTimes.add("04:00 PM-Skotizo");
			eventTimes.add("05:00 PM-Pk/Pvm wild");
			eventTimes.add("06:00 PM-Skilling");
			eventTimes.add("07:00 PM-Tournament");
			eventTimes.add("08:00 PM-Blood key");
			eventTimes.add("09:00 PM-Skotizo");
			eventTimes.add("10:00 PM-Blood key");
			eventTimes.add("11:00 PM-Skilling");
			eventTimes.add("12:00 AM-Pk/Pvm wild");
			eventTimes.add("01:00 AM-Tournament");
			eventTimes.add("02:00 AM-Skotizo");
			eventTimes.add("03:00 AM-Daily highscores");
			eventTimes.add("04:00 AM-Pk/Pvm wild");
			eventTimes.add("05:00 AM-Skilling");
			eventTimes.add("06:00 AM-Pk/Pvm wild");
			eventTimes.add("07:00 AM-Skotizo");
			eventTimes.add("09:00 AM-Blood key");
			eventTimes.add("10:00 AM-Tournament");
			eventTimes.add("11:00 AM-Skilling");
			eventTimes.add("12:00 PM-Pk/Pvm wild");
		}

	}

	public static void tournamentKick(Player player, String name) {
		try {
			name = name.substring(15);
			Player killer = null;
			Player victim = null;
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.getHeight() != 20) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name) && loop.tournamentTarget >= 0) {
					victim = loop;
					Player killer1 = PlayerHandler.players[loop.tournamentTarget];
					if (killer1.tournamentTarget == loop.getPlayerId()) {
						killer = killer1;
					}
				}
			}
			if (killer == null || victim == null) {
				player.getPA().sendMessage("Victim or killer is not available.");
				return;
			}
			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has tournament kicked '" + victim.getPlayerName() + "'");
			Announcement.announce("Tournament stalling kick, forced loser: " + victim.getPlayerName() + ", forced winner: " + killer.getPlayerName());
			Tournament.playerDied(killer, victim);
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::tournamentkick mgt madness");
		}

	}

	public static boolean isTournamentActive() {
		return tournamentActive;
	}

	public static void setTournamentActive(boolean tournamentActive) {
		Tournament.tournamentActive = tournamentActive;
	}

	public static String getTournamentStatus() {
		return tournamentStatus;
	}

	public static void setTournamentStatus(String tournamentStatus) {
		Tournament.tournamentStatus = tournamentStatus;
	}



}
