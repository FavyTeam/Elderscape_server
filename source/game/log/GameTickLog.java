package game.log;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotManager;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;

import java.util.ArrayList;

/**
 * Track the game tick.
 *
 * @author MGT Madness, created on 12-12-2013.
 */
public class GameTickLog {

	private final static int GAME_TICK_DURATION_TO_PRINT = 400;

	private static int loop;

	public static int totalTickTimeTaken;

	private static long loopDuration;

	public static long cycleEventTickDuration;

	public static long packetTickDuration;

	public static long itemTickDuration;

	public static long playerTickDuration;

	public static long playerTickDuration2;

	public static long playerTickDuration3;

	public static long npcTickDuration;

	public static long objectTickDuration;

	public static long minigameTickDuration;

	public static ArrayList<String> saveTicks = new ArrayList<String>();

	public static int saveTicksTimer;

	public static ArrayList<String> lagDebugList = new ArrayList<String>();

	public static ArrayList<String> singlePlayerPacketLog = new ArrayList<String>();

	/**
	 * Stage 1 of the loop debug.
	 */
	public static void loopDebugPart1() {
		loopDuration = System.currentTimeMillis();
		loop++;
	}

	private final static int MINUTES_AGO_USED_WILDERNESS = 5;

	/***
	 * Last loop duration.
	 */
	public static int durationAmount;

	/**
	 * Stage 2 of the loop debug.
	 */
	public static void loopDebugPart2() {
		if ((System.currentTimeMillis() - Server.timeServerOnline) <= 10000) {
			return;
		}
		loopDuration = System.currentTimeMillis() - loopDuration;
		durationAmount = (int) loopDuration;
		saveTicksTimer++;
		if (saveTicksTimer == 10) {
			saveTicksTimer = 0;
			saveTicks.add(durationAmount + "");
			if (saveTicks.size() > 90) {
				saveTicks.remove(0);
			}
		}
		totalTickTimeTaken += loopDuration;

		if (ServerConfiguration.STABILITY_TEST) {
			Misc.print("Main loop took: " + BotManager.BOTS_AMOUNT + " bots, " + loopDuration + " ms.");
			printStats();
		}

		if (loop == 3000 && !ServerConfiguration.DEBUG_MODE) // 30 minutes.
		{
			int playersUsedWilderness = 0;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null && System.currentTimeMillis() - PlayerHandler.players[i].wildernessEnteredTime <= (MINUTES_AGO_USED_WILDERNESS * 60000)) {
					playersUsedWilderness++;
				}
			}
			Misc.print("[Main loop took: " + totalTickTimeTaken / loop + " ms] [Players online: " + PlayerHandler.getRealPlayerCount() + "] [Players online used Wilderness in past "
			           + MINUTES_AGO_USED_WILDERNESS + " mins: " + playersUsedWilderness + "]");
			totalTickTimeTaken = 0;
			loop = 0;
		}
		if (!ServerConfiguration.DEBUG_MODE && loopDuration >= GAME_TICK_DURATION_TO_PRINT) {
			Misc.print("Lag spike has been logged: " + loopDuration + "ms, with " + PlayerHandler.getRealPlayerCount() + " players.");
			appendLagLog(loopDuration);
		}
		resetStats();
	}

	public static void printStats() {
		if (cycleEventTickDuration != -1) {
			Misc.print("Cycle event: " + cycleEventTickDuration);
		}
		if (packetTickDuration != -1) {
			Misc.print("Packet: " + packetTickDuration);
		}
		if (itemTickDuration != -1) {
			Misc.print("Item: " + itemTickDuration);
		}
		if (playerTickDuration != -1) {
			Misc.print("Player: " + playerTickDuration);
		}
		if (playerTickDuration2 != -1) {
			Misc.print("Player2: " + playerTickDuration2);
		}
		if (playerTickDuration3 != -1) {
			Misc.print("Player3: " + playerTickDuration3);
		}
		if (npcTickDuration != -1) {
			Misc.print("NPC: " + npcTickDuration);
		}
		if (objectTickDuration != -1) {
			Misc.print("Object: " + objectTickDuration);
		}
		if (minigameTickDuration != -1) {
			Misc.print("Minigame: " + minigameTickDuration);
		}
	}

	public static void calculateStats() {
		if (cycleEventTickDuration != -1 && cycleEventTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.cycleEventTickDuration = System.currentTimeMillis() - GameTickLog.cycleEventTickDuration;
		}
		if (packetTickDuration != -1 && packetTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.packetTickDuration = System.currentTimeMillis() - GameTickLog.packetTickDuration;
		}
		if (itemTickDuration != -1 && itemTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.itemTickDuration = System.currentTimeMillis() - GameTickLog.itemTickDuration;
		}
		if (playerTickDuration != -1 && playerTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.playerTickDuration = System.currentTimeMillis() - GameTickLog.playerTickDuration;
		}
		if (playerTickDuration2 != -1 && playerTickDuration2 >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.playerTickDuration2 = System.currentTimeMillis() - GameTickLog.playerTickDuration2;
		}
		if (playerTickDuration3 != -1 && playerTickDuration3 >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.playerTickDuration3 = System.currentTimeMillis() - GameTickLog.playerTickDuration3;
		}
		if (npcTickDuration != -1 && npcTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.npcTickDuration = System.currentTimeMillis() - GameTickLog.npcTickDuration;
		}
		if (objectTickDuration != -1 && objectTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.objectTickDuration = System.currentTimeMillis() - GameTickLog.objectTickDuration;
		}
		if (minigameTickDuration != -1 && minigameTickDuration >= Misc.getMinutesToMilliseconds(1)) {
			GameTickLog.minigameTickDuration = System.currentTimeMillis() - GameTickLog.minigameTickDuration;
		}
	}

	public static void resetStats() {
		GameTickLog.cycleEventTickDuration = -1;
		GameTickLog.packetTickDuration = -1;
		GameTickLog.itemTickDuration = -1;
		GameTickLog.playerTickDuration = -1;
		GameTickLog.playerTickDuration2 = -1;
		GameTickLog.playerTickDuration3 = -1;
		GameTickLog.npcTickDuration = -1;
		GameTickLog.objectTickDuration = -1;
		GameTickLog.minigameTickDuration = -1;
	}

	/**
	 * Create the lag log.
	 */
	private static void appendLagLog(long loopDuration) {
		lagDebugList.add("[" + Misc.getDateAndTime() + "] " + "Main tick took: " + loopDuration + "ms with " + PlayerHandler.getRealPlayerCount() + " players.");
		lagDebugList.add("Cycle event: " + cycleEventTickDuration);
		lagDebugList.add("Packet: " + packetTickDuration);
		lagDebugList.add("Item: " + itemTickDuration);
		lagDebugList.add("Player: " + playerTickDuration);
		lagDebugList.add("Player2: " + playerTickDuration2);
		lagDebugList.add("Player3: " + playerTickDuration3);
		lagDebugList.add("NPC: " + npcTickDuration);
		lagDebugList.add("Object: " + objectTickDuration);
		lagDebugList.add("Minigame: " + minigameTickDuration);
		lagDebugList.add("--------------");
	}

	/**
	 * Save the lag log arraylist to the file upon server restart.
	 */
	public static void saveLagLogFile() {
		FileUtility.saveArrayContents("backup/logs/system log/lag spike.txt", lagDebugList);
		lagDebugList.clear();
	}

}
