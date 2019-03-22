package game.content.minigame.barrows;

import java.util.ArrayList;
import core.GameType;
import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.highscores.HighscoresBarrowsRun;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.Teleport;
import game.content.profile.ProfileRank;
import game.content.profile.RareDropLog;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Barrows related objects handled here
 *
 * @author MGT Madness
 */

public class Barrows {

	public static void updateBarrowsInterface(Player player) {
		player.getPA().sendFrame126((player.barrowsNpcs[4][1] == 2 ? "@gr3@" : "@red@") + "Dharok", 22046);
		player.getPA().sendFrame126((player.barrowsNpcs[3][1] == 2 ? "@gr3@" : "@red@") + "Guthan", 22047);
		player.getPA().sendFrame126((player.barrowsNpcs[5][1] == 2 ? "@gr3@" : "@red@") + "Ahrim", 22048);
		player.getPA().sendFrame126((player.barrowsNpcs[1][1] == 2 ? "@gr3@" : "@red@") + "Torag", 22049);
		player.getPA().sendFrame126((player.barrowsNpcs[0][1] == 2 ? "@gr3@" : "@red@") + "Verac", 22050);
		player.getPA().sendFrame126((player.barrowsNpcs[2][1] == 2 ? "@gr3@" : "@red@") + "Karil", 22051);
	}

	public static void killedBarrows(Player player, Npc npc) {
		if (npc == null) {
			return;
		}
		if (player == null) {
			return;
		}
		for (int o = 0; o < player.barrowsNpcs.length; o++) {
			if (npc.npcType == player.barrowsNpcs[o][0]) {
				player.barrowsNpcs[o][1] = 2; // 2 for dead
				player.barrowsKillCount++;
				updateBarrowsInterface(player);
				player.barrowsBrothersKilled[o] = true;
			}
		}
	}

	public static void updateSavedBarrowsProgress(Player player) {
		for (int index = 0; index < player.barrowsBrothersKilled.length; index++) {
			if (player.barrowsBrothersKilled[index]) {
				player.barrowsNpcs[index][1] = 2;
				player.barrowsKillCount++;
			}
		}
	}

	/**
	 * Starts the digging emote and action for the Barrows minigame
	 */
	public static void startDigging(final Player player) {
		if (Area.isWithInArea(player, 3553, 3561, 3294, 3301)) {
			player.getPA().movePlayer(3578, 9706, 3);
		} else if (Area.isWithInArea(player, 3550, 3557, 3278, 3287)) {
			player.getPA().movePlayer(3568, 9683, 3);
		} else if (Area.isWithInArea(player, 3561, 3568, 3285, 3292)) {
			player.getPA().movePlayer(3557, 9703, 3);
		} else if (Area.isWithInArea(player, 3570, 3579, 3293, 3302)) {
			player.getPA().movePlayer(3556, 9718, 3);
		} else if (Area.isWithInArea(player, 3571, 3582, 3278, 3285)) {
			player.getPA().movePlayer(3534, 9704, 3);
		} else if (Area.isWithInArea(player, 3562, 3569, 3273, 3279)) {
			player.getPA().movePlayer(3546, 9684, 3);
		}
	}

	/**
	 * Coffin identity, Barrows brother identitiy.
	 */
	public static final int[][] COFFIN_AND_BROTHERS =
			{
					{20772, 1677}, //veracs
					{20721, 1676}, //torags
					{20771, 1675}, //karils
					{20722, 1674}, //guthans
					{20720, 1673}, //dh
					{20770, 1672}, //ahrim
			};

	/**
	 * Picking the random coffin
	 **/
	public static int getRandomCoffin() {
		return Misc.random(COFFIN_AND_BROTHERS.length - 1);
	}

	/**
	 * Selects the coffin and shows the interface if coffin id matches random coffin
	 **/
	public static boolean selectCoffin(Player player, int coffinId) {
		if (player.randomCoffin == 0) {
			player.randomCoffin = getRandomCoffin();
		}

		if (COFFIN_AND_BROTHERS[player.randomCoffin][0] == coffinId) {
			player.getDH().sendDialogues(163);
			return true;
		}
		return false;
	}

	public static void resetBarrows(Player player) {
		for (int index = 0; index < player.barrowsBrothersKilled.length; index++) {
			player.barrowsBrothersKilled[index] = false;
			player.barrowsNpcs[index][1] = 0;
		}
		player.barrowsKillCount = 0;
		player.randomCoffin = Misc.random(3) + 1;
	}

	public final static int BARROWS_ITEMS[] =
			{4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759};

	public static int Runes[] =
			{4740, 558, 560, 565, 4740};

	public static int randomBarrows() {
		return BARROWS_ITEMS[(int) (Math.random() * BARROWS_ITEMS.length)];
	}

	public static int randomRunes() {
		return Runes[(int) (Math.random() * Runes.length)];
	}

	/**
	 * Perform actions of the objects in Barrows.
	 */
	public static boolean isBarrowsObject(final Player player, int objectType) {
		switch (objectType) {

			//Chest
			case 20973:
				barrowsChest(player);
				return true;

			//Verac
			case 20672: //steps id
				player.getPA().movePlayer(3556, 3298, 0);
				if (player.barrowsNpcs[0][1] != 2) {
					player.barrowsNpcs[0][1] = 0;
				}
				return true;
			case 20772:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[0][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1677);
					player.barrowsNpcs[0][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Torag
			case 20671: //steps id
				player.getPA().movePlayer(3553, 3283, 0);
				if (player.barrowsNpcs[1][1] != 2) {
					player.barrowsNpcs[1][1] = 0;
				}
				return true;
			case 20721:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[1][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1676);
					player.barrowsNpcs[1][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Karil
			case 20670: //steps id
				player.getPA().movePlayer(3565, 3276, 0);
				if (player.barrowsNpcs[2][1] != 2) {
					player.barrowsNpcs[2][1] = 0;
				}
				return true;

			case 20771:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[2][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1675);
					player.barrowsNpcs[2][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Guthan
			case 20669: //steps id
				player.getPA().movePlayer(3578, 3284, 0);
				if (player.barrowsNpcs[3][1] != 2) {
					player.barrowsNpcs[3][1] = 0;
				}
				return true;
			case 20722:
				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[3][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1674);
					player.barrowsNpcs[3][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Dharok
			case 20668: //steps id
				player.getPA().movePlayer(3574, 3298, 0);
				if (player.barrowsNpcs[4][1] != 2) {
					player.barrowsNpcs[4][1] = 0;
				}
				return true;
			case 20720:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[4][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1673);
					player.barrowsNpcs[4][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Ahrim
			case 20667: //steps id
				player.getPA().movePlayer(3565, 3290, 0);
				if (player.barrowsNpcs[5][1] != 2) {
					player.barrowsNpcs[5][1] = 0;
				}
				return true;
			case 20770:
				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[5][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1672);
					player.barrowsNpcs[5][1] = 1;
				} else {
					player.playerAssistant.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

		}
		return false;
	}

	/**
	 * Spawn the given npcType on a tile next to the player that the player can walk to.
	 */
	private static void spawnBarrowsNpcOnCorrectTile(Player player, int npcType) {
		int[] teleportCoords = new int[2];
		teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, player.getX(), player.getY(), player.getHeight(), 1, 1);
		NpcHandler.spawnNpc(player, npcType, teleportCoords[0], teleportCoords[1], player.getHeight(), true, true);

	}

	public final static double BARROWS_CHANCE = 1.05;

	private static void barrowsChest(Player player) {
		if (player.barrowsKillCount < 5) {
			player.playerAssistant.sendMessage("You haven't killed all the brothers.");
			return;
		} else if (player.barrowsKillCount == 5 && player.barrowsNpcs[player.randomCoffin][1] == 1) {
			player.playerAssistant.sendMessage("You have already summoned this npc.");
			return;
		}
		if (player.barrowsNpcs[player.randomCoffin][1] == 0 && player.barrowsKillCount >= 5) {
			spawnBarrowsNpcOnCorrectTile(player, player.barrowsNpcs[player.randomCoffin][0]);
			player.barrowsNpcs[player.randomCoffin][1] = 1;
			return;
		}
		if (ItemAssistant.getFreeInventorySlots(player) < 2) {
			player.playerAssistant.sendMessage("You need at least 2 inventory slots empty.");
			return;
		}
		if ((player.barrowsKillCount > 5 || player.barrowsNpcs[player.randomCoffin][1] == 2)) {
			player.setBarrowsRunCompleted(player.getBarrowsRunCompleted() + 1);
			player.getPA().sendFilterableMessage("Your barrows run count is: " + ServerConstants.RED_COL + player.getBarrowsRunCompleted() + ".");
			ArrayList<String> list = new ArrayList<String>();
			resetBarrows(player);
			int runeId = randomRunes();
			int runeAmount = Misc.random(150) + 100;
			ItemAssistant.addItem(player, runeId, runeAmount);
			list.add(runeId + " " + runeAmount);
			//Achievements.checkCompletionMultiple(player, "1045 1051 1119"); // Will be added for Economy.
			ProfileRank.rankPopUp(player, "ADVENTURER");
			if (Misc.hasOneOutOf(GameMode.getDropRate(player, BARROWS_CHANCE))) {
				list.add(giveBarrowsItemReward(player));
			}
			Teleport.spellTeleport(player, 3564, 3288, 0, false);
			updateBarrowsInterface(player);
			InterfaceAssistant.displayReward(player, list);
			saveBarrowsRunTime(player);
			PlayerTitle.checkCompletionMultiple(player, "54 55 56 57 58 59", "");
		}
	}

	private static String giveBarrowsItemReward(Player player) {
		int randomBarrows = randomBarrows();
		if (!player.itemsCollected.contains(randomBarrows)) {
			player.itemsCollected.add(randomBarrows);
		}
		ItemAssistant.addItemToInventoryOrDrop(player, randomBarrows, 1);
		if (GameType.isOsrsEco()) {
			RareDropLog.appendRareDrop(player, "Barrows: " + ItemAssistant.getItemName(randomBarrows));
			if (!player.profilePrivacyOn) {
				Announcement.announce(
						ServerConstants.GREEN_COL + Misc.capitalize(player.getPlayerName()) + " has received " + ItemAssistant.getItemName(randomBarrows) + " from Barrows.");
			}
			player.getPA().sendScreenshot(ItemAssistant.getItemName(randomBarrows), 2);
		} else {
			player.getPA().sendMessage("You have received " + Misc.getAorAn(ItemAssistant.getItemName(randomBarrows)) + "!");
		}
		return randomBarrows + " 1";
	}

	public static void saveBarrowsRunTime(Player player) {
		if (player.barrowsTimer == 0) {
			return;
		}
		long barrowsRunTime = (System.currentTimeMillis() - player.barrowsTimer) / 1000;
		int oldTime = player.barrowsPersonalRecord;
		boolean broken = false;
		if ((int) barrowsRunTime < player.barrowsPersonalRecord || oldTime == 0) {
			player.barrowsPersonalRecord = (int) barrowsRunTime;
			broken = true;
		}
		if (barrowsRunTime < HighscoresBarrowsRun.getInstance().highscoresList[29].barrowsTime) {
			int prizeAmount = GameType.isOsrsPvp() ? 5000 : 500000;
			player.getPA().announce(
					GameMode.getGameModeName(player) + " has broken the barrows record with " + player.barrowsPersonalRecord + "s and " + Misc.formatRunescapeStyle(prizeAmount)
					+ " " + ServerConstants.getMainCurrencyName() + " prize!");
			player.getPA().sendScreenshot("barrows record " + player.barrowsPersonalRecord, 2);
			ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), prizeAmount);
		}
		if (broken) {
			if (oldTime != 0) {
				player.getPA().sendMessage("<col=005f00>You have broken your previous record of " + oldTime + " seconds with " + player.barrowsPersonalRecord + " seconds.");
			} else {
				player.getPA().sendMessage("You have completed the Barrows run in " + player.barrowsPersonalRecord + " seconds.");
			}
		} else {
			player.getPA().sendMessage("You have completed the Barrows run in " + barrowsRunTime + " seconds.");
		}
		HighscoresBarrowsRun.getInstance().sortHighscores(player);

	}

	public static void startBarrowsTimer(Player attacker, Npc npc) {
		if (attacker.barrowsKillCount > 0) {
			return;
		}
		boolean brotherFound = false;
		for (int index = 0; index < COFFIN_AND_BROTHERS.length; index++) {
			if (npc.npcType == COFFIN_AND_BROTHERS[index][1]) {
				brotherFound = true;
				break;
			}
		}

		if (!brotherFound) {
			return;
		}
		if (npc.getCurrentHitPoints() < npc.maximumHitPoints) {
			return;
		}
		attacker.barrowsTimer = System.currentTimeMillis();
	}

	public static void resetCoffinStatus(Player player) {
		for (int index = 0; index < player.barrowsNpcs.length; index++) {
			if (player.barrowsNpcs[index][1] == 1) {
				player.barrowsNpcs[index][1] = 0;
			}
		}
	}


}
