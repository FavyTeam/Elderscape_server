package game.content.minigame;

import core.Server;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Warrior's Guild minigame.
 *
 * @author MGT Madness, created on 18-02-2016.
 */
public class WarriorsGuild {

	/**
	 * Helm, Body, Leg, Npc.
	 */
	public static final int[][] ARMOUR_DATA =
			{
					// Bronze.
					{1075, 1117, 1155, 2450},
					// Iron.
					{1067, 1115, 1153, 2451},
					// Steel.
					{1069, 1119, 1157, 2452},
					// Black.
					{1077, 1125, 1165, 2453},
					// Mithril.
					{1071, 1121, 1159, 2454},
					// Adamant.
					{1073, 1123, 1161, 2455},
					// Rune.
					{1079, 1127, 1163, 2456}
			};

	private static final int[] TOKEN_REWARDS =
			{5, 10, 15, 20, 25, 30, 40};

	private static final int[] DEFENDERS = new int[]
			                                       {8844, 8845, 8846, 8847, 8848, 8849, 8850, 12954};

	/**
	 * Drop the tokens depending on the npc.
	 */
	public static void dropAnimatedTokens(Player player, Npc npc) {
		for (int index = 0; index < ARMOUR_DATA.length; index++) {
			if (ARMOUR_DATA[index][3] == npc.npcType) {
				player.summonedAnimator = false;

				int chance = 0;

				Server.itemHandler
						.createGroundItem(player, 8851, npc.getX(), npc.getY(), player.getHeight(), TOKEN_REWARDS[index] * (Misc.hasPercentageChance(chance) ? 2 : 1), false, 0,
								true, "", "", "", "", "dropAnimatedTokens first");
				Server.itemHandler
						.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][0], npc.getX(), npc.getY(), player.getHeight(), 1, false, 0, true, "", "", "", "", "dropAnimatedTokens second");
				Server.itemHandler
						.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][1], npc.getX(), npc.getY(), player.getHeight(), 1, false, 0, true, "", "", "", "", "dropAnimatedTokens third");
				Server.itemHandler
						.createGroundItem(player, ARMOUR_DATA[player.warriorsGuildArmourIndex][2], npc.getX(), npc.getY(), player.getHeight(), 1, false, 0, true, "", "", "", "", "dropAnimatedTokens fourth");
			}
		}
	}

	public static void dropDefender(Player player, Npc npc) {
		if (npc.npcType == 2463 || npc.npcType == 2464 || npc.npcType == 2465 || npc.npcType == 2466) {
			if (!Misc.hasOneOutOf(GameMode.getDropRate(player, 10))) {
				return;
			}

			int defenderIndex = -1;

			for (int index = 0; index < DEFENDERS.length; index++) {
				if (ItemAssistant.hasItemEquipped(player, DEFENDERS[index])) {
					defenderIndex = index;
				}
			}
			for (int index = 0; index < DEFENDERS.length; index++) {
				if (ItemAssistant.hasItemInInventory(player, DEFENDERS[index])) {
					defenderIndex = index;
				}
			}

			// If already has dragon defender, set it to rune defender, so the player can receive a dragon defender.
			if (defenderIndex == 7) {
				defenderIndex = 6;
			}

			defenderIndex++;

			int npcX = npc.getVisualX();
			int npcY = npc.getVisualY();
			Server.itemHandler.createGroundItem(player, DEFENDERS[defenderIndex], npcX, npcY, npc.getHeight(), 1, false, 0, true, "", "", "", "", "dropDefender");

			// If item to receive is a dragon defender.
			if (defenderIndex == 7) {
				player.playerAssistant.sendMessage("<col=005f00>You have received a " + ItemAssistant.getItemName(DEFENDERS[defenderIndex]) + "!");
			} else {
				player.playerAssistant.sendMessage(
						"<col=005f00>You have received a " + ItemAssistant.getItemName(DEFENDERS[defenderIndex]) + "! Your next drop will be " + Misc.getAorAn(
								ItemAssistant.getItemName(DEFENDERS[defenderIndex + 1]) + "."));
			}
		}
	}

	/**
	 * The action when the player uses a piece of armour with the animator object.
	 */
	public static boolean spawnAnimatorAction(final Player player, int objectId, int itemId) {
		if (objectId != 23955) {
			return false;
		}

		if (player.summonedAnimator) {
			player.playerAssistant.sendMessage("You already summoned a set of animated armour.");
			return true;
		}

		// If player has already spawned an animator, return.
		for (int i = 0; i < ARMOUR_DATA.length; i++) {
			for (int f = 0; f < ARMOUR_DATA[0].length; f++) {
				if (itemId == ARMOUR_DATA[i][f]) {
					if (ItemAssistant.hasItemInInventory(player, ARMOUR_DATA[i][0]) && ItemAssistant.hasItemInInventory(player, ARMOUR_DATA[i][1]) && ItemAssistant
							                                                                                                                                  .hasItemInInventory(
									                                                                                                                                  player,
									                                                                                                                                  ARMOUR_DATA[i][2])) {
						createAnimatorEvent(player, ARMOUR_DATA[i][3], ARMOUR_DATA[i][0], ARMOUR_DATA[i][1], ARMOUR_DATA[i][2]);
						player.warriorsGuildArmourIndex = i;
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Spawning the animator event.
	 */
	private static void createAnimatorEvent(final Player player, final int npcId, final int item1, final int item2, final int item3) {
		player.summonedAnimator = true;
		player.doingActionTimer = 1;
		player.warriorsGuildEventTimer++;
		player.startAnimation(827);
		player.getDH().sendStartInfo("", "", "You place your armour on the platform where it disappears...", "", "");
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.warriorsGuildEventTimer++;
				switch (player.warriorsGuildEventTimer) {
					case 4:
						ItemAssistant.deleteItemFromInventory(player, item1, 1);
						ItemAssistant.deleteItemFromInventory(player, item2, 1);
						ItemAssistant.deleteItemFromInventory(player, item3, 1);
						break;

					case 5:
						player.getDH().sendStartInfo("", "", "The animator hums, something appears to be working. You stand", "back...", "");
						break;

					case 6:
						Movement.travelTo(player, 0, 3);
						player.turnPlayerTo(player.getX(), 3536);
						break;

					case 7:
						NpcHandler.spawnNpc(player, npcId, player.getX(), 3536, 0, true, true);
						container.stop();
						break;

				}
			}

			@Override
			public void stop() {
				player.warriorsGuildEventTimer = 0;
				player.doingActionTimer = 0;
			}
		}, 1);
	}

	public static boolean canAttackCyclops(Player player, int npcId) {
		if (npcId != 2463 || npcId != 2464 || npcId != 2465 || npcId != 2466) {
			return true;
		}
		if (!ItemAssistant.hasItemAmountInInventory(player, 8851, 10)) {
			player.playerAssistant.sendMessage("You have run out of tokens.");
			return false;
		}
		if (!Area.inCylopsRoom(player)) {
			return false;
		}
		return true;
	}

	public static void startTokenDrainingEvent(final Player player) {
		if (player.usingCyclopsEvent) {
			return;
		}
		if (!Area.inCylopsRoom(player) && !Area.inDragDefenderRoom(player)) {
			return;
		}
		player.usingCyclopsEvent = true;
		player.warriorsGuildCyclopsTimer = 10;
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				player.warriorsGuildCyclopsTimer--;
				if (Area.inCylopsRoom(player) || Area.inDragDefenderRoom(player)) {
					if (player.warriorsGuildCyclopsTimer == 0) {
						if (ItemAssistant.hasItemAmountInInventory(player, 8851, 10)) {
							ItemAssistant.deleteItemFromInventory(player, 8851, 10);
							player.playerAssistant.sendMessage("<col=0008f7>10 tokens have vanished.");
							player.warriorsGuildCyclopsTimer = 10;
						} else {
							player.getDH().sendStatement("You have run out of tokens, and have been removed.");
							player.playerAssistant.sendMessage("You have run out of tokens, and have been removed.");
							player.getPA().movePlayer(2843, 3540, 2);
							container.stop();
						}
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
				player.usingCyclopsEvent = false;
			}
		}, 10);

	}
}
