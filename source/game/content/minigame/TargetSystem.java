package game.content.minigame;

import core.GameType;
import core.Server;
import core.ServerConstants;
import game.content.highscores.HighscoresDaily;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.Artefacts.ArtefactsData;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

/**
 * Pvp target system.
 *
 * @author MGT Madness, created on 26-02-2017.
 */
public class TargetSystem {
	/**
	 * 1 Activity point is 10 seconds of activity.
	 */
	private final static int ACTIVITY_POINTS_NEEDED_FOR_TARGET = 84;

	private final static int SECONDS_UNTIL_TARGET_LOST_FROM_EXITING_WILD = 120;

	public static void doingWildActivity(Player player) {
		if (player.bot) {
			return;
		}
		if (!Area.inDangerousPvpArea(player)) {
			return;
		}
		if (player.targetActivityPoints >= ACTIVITY_POINTS_NEEDED_FOR_TARGET) {
			eligibleForTarget(player);
			return;
		}
		if (System.currentTimeMillis() - player.targetActivityTime < 10000) {
			return;
		}
		player.targetActivityTime = System.currentTimeMillis();
		player.targetActivityPoints++;
		if (player.targetActivityPoints >= ACTIVITY_POINTS_NEEDED_FOR_TARGET) {
			player.getPA().sendMessage(ServerConstants.RED_COL + "You are now eligible for a target.");
			findTarget(player);
		}
		// If it has been 2 minutes since my target left wild, then find another target.
	}

	private static void eligibleForTarget(Player player) {
		if (player.targetPlayerId == -1) {
			findTarget(player);
		} else {
			Player target = PlayerHandler.players[player.targetPlayerId];
			if (target != null) {
				if ((System.currentTimeMillis() - target.timeExitedWildFromTarget >= (SECONDS_UNTIL_TARGET_LOST_FROM_EXITING_WILD * 1000)) && !Area.inDangerousPvpArea(target)
				    && target.timeExitedWildFromTarget != 0) {
					player.getPA().sendMessage(ServerConstants.RED_COL + target.getPlayerName() + " has stayed outside for long, another target will be found shortly.");
					resetTarget(target);
					target.getPA().sendMessage(ServerConstants.RED_COL + "You have been issued a target penalty for staying outside for long");
					findTarget(player);
				}
			}
		}

	}

	public static void logOut(Player player) {

		if (player.targetPlayerId != -1) {
			Player target = PlayerHandler.players[player.targetPlayerId];
			if (target != null) {
				target.getPA().sendMessage(ServerConstants.RED_COL + player.getPlayerName() + " has logged out, another target will be found shortly.");
				findTarget(target);
			}
			resetTarget(player);
		}
	}

	public static void leftWild(Player player) {
		if (player.targetPlayerId == -1) {
			return;
		}
		if (Area.inDangerousPvpArea(player)) {
			player.timeExitedWildFromTarget = System.currentTimeMillis();
		}
		if (player.timeExitedWildFromTarget == 0) {
			return;
		}
		if (System.currentTimeMillis() - player.targetLeftTime <= 30000) {
			return;
		}
		player.targetLeftTime = System.currentTimeMillis();
		int left = (int) (120 - (System.currentTimeMillis() - player.timeExitedWildFromTarget) / 1000);
		if (left <= 0) {
			return;
		}
		player.getPA().sendMessage(ServerConstants.RED_COL + "You will receive a target penalty if you do not return to the wild in " + left + " seconds.");

	}


	private static void resetTarget(Player player) {
		if (player.targetActivityPoints >= ACTIVITY_POINTS_NEEDED_FOR_TARGET) {
			player.targetActivityPoints = 0;
		}
		player.targetPlayerId = -1;
		player.getPA().createPlayerHints(10, -1);
	}

	/**
	 * Item id, chance.
	 * 20 targets can be killed a day.
	 */
	public final static int[][] spiritShields =
			{

					{12817, 260}, // Elysian spirit shield
					{12825, 65}, // Arcane spirit shield
					{12821, 65}, // Spectral spirit shield
					{12831, 15}, // Blessed spirit shield
					{12829, 10}, // Spirit shield
			};

	public static void death(Player victim, Player killer) {
		if (killer == null) {
			return;
		}
		if (killer.targetPlayerId == victim.getPlayerId() && victim.targetPlayerId == killer.getPlayerId()) {
			killer.targetsKilled++;
			victim.targetDeaths++;
			if (HighscoresDaily.getInstance().currentDailyHighscores.equals("Target kills")) {
				HighscoresDaily.getInstance().sortHighscores(killer, 1, killer.targetsKilled, Misc.getKDR(killer.targetsKilled, killer.targetDeaths) + "", killer.getGameMode());
			}
			int artefactsDropped = 3;
			for (int index = 0; index < artefactsDropped; index++) {
				int artefact = Artefacts.artefactDrop();
				if (GameType.isOsrsPvp()) {
					CoinEconomyTracker.addIncomeList(killer, "TARGET " + BloodMoneyPrice.getBloodMoneyPrice(artefact));
				}
				Server.itemHandler.createGroundItem(killer, artefact, victim.getX(), victim.getY(), victim.getHeight(), 1, false, 0, true, victim.getPlayerName(), "", "", "", "death only " + artefact);
				if (artefact == ArtefactsData.AncientStatuette.getId()) {
					if (!killer.profilePrivacyOn) {
						Announcement.announce(ServerConstants.GREEN_COL + GameMode.getGameModeName(killer) + " received an Ancient statuette from Pking!");
					}
					RareDropLog.appendRareDrop(killer, "Pking: Ancient statuette");
					killer.getPA().sendScreenshot(ItemAssistant.getItemName(artefact), 2);
				}
			}
			resetTarget(killer);
			resetTarget(victim);
		}
	}

	private static void findTarget(Player player) {
		player.targetPlayerId = -1;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerId() == player.getPlayerId()) {
				continue;
			}
			if (loop.bot) {
				continue;
			}
			if (loop.targetActivityPoints < ACTIVITY_POINTS_NEEDED_FOR_TARGET) {
				continue;
			}
			if (loop.targetPlayerId != -1) {
				continue;
			}
			if (!Area.inDangerousPvpArea(loop)) {
				continue;
			}
			assignTarget(loop, player);
			assignTarget(player, loop);
			break;
		}
	}

	private static void assignTarget(Player one, Player two) {
		one.getPA().sendMessage(ServerConstants.RED_COL + "You have been assigned " + two.getPlayerName() + " as a target!");
		String location = "level " + two.getWildernessLevel() + " wilderness.";
		if (Area.inCityPvpArea(two)) {
			location = "::edgepvp";
		}
		one.getPA().sendMessage(ServerConstants.RED_COL + two.getPlayerName() + " is level " + two.getCombatLevel() + " combat, at " + location);
		one.targetPlayerId = two.getPlayerId();
		one.getPA().createPlayerHints(10, two.getPlayerId());
	}

	public static void updateArrowHint(Player player) {
		if (player.targetPlayerId == -1 && player.getDuelStatus() <= 4) {
			return;
		}
		int targetId = player.targetPlayerId;
		if (targetId == -1) {
			targetId = player.getDuelingWith();
		}
		if (targetId <= 0) {
			return;
		}
		Player target = PlayerHandler.players[targetId];
		if (target != null) {
			target.getPA().sendMessage(":packet:targethint:" + player.getX() + ":" + player.getY() + ":" + player.getPlayerId());
		}

	}

}
