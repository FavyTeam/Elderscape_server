package game.content.miscellaneous;

import core.GameType;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.npc.NpcHandler;
import game.npc.pet.BossPetDrops;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * TzHaar fight caves minigame.
 *
 * @author MGT Madness, created on 02-01-2015.
 */
public class FightCaves {


	private static int FIRE_CAPE = 6570;

	private static void fightCavesReward(Player player) {
		if (GameType.isOsrsEco()) {
			ItemAssistant.addItemToInventoryOrDrop(player, FIRE_CAPE, 1);
			ItemAssistant.addItemToInventoryOrDrop(player, 6529, 140);
			// Has to be kept here.
			if (Misc.hasOneOutOf(GameMode.getDropRate(player, BossPetDrops.DROP_RATE_JAD_PET))) {
				BossPetDrops.awardBoss(player, 7041, 7041, 6506, "BOSS");
			}
		} else {
			ItemAssistant.addItemToInventoryOrDrop(player, 13307, 150);
		}

	}

	public static void exitFightCaves(Player player) {
		if (player.getY() != 5117) {
			return;
		}
		for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++) {
			if (NpcHandler.npcs[i] == null) {
				continue;
			}

			// TzTok-Jad
			if (NpcHandler.npcs[i].npcType != 6506) {
				continue;
			}
			if (NpcHandler.npcs[i].getSpawnedBy() != player.getPlayerId()) {
				continue;
			}
			NpcHandler.npcs[i] = null;
			break;
		}
		player.getPA().movePlayer(2438, 5168, 0);
		player.setUsingFightCaves(false);
	}

	/**
	 * Start the fight caves minigame.
	 *
	 * @param player The associated player.
	 */
	public static void startFightCaves(final Player player) {
		if (player.isUsingFightCaves()) {
			return;
		}
		player.setUsingFightCaves(true);
		player.getPA().movePlayer(2413, 5117, 24 + (player.getPlayerId() * 4));

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();

			}

			@Override
			public void stop() {
				player.setNpcType(2180);
				player.getDH().sendDialogues(172);
			}
		}, 1);

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();

			}

			@Override
			public void stop() {
				NpcHandler.spawnNpc(player, 6506, 2408, 5101, player.getHeight(), true, true);
			}
		}, 5);
	}


	/**
	 * Reward the player for completing the fight caves minigame.
	 *
	 * @param playerID The associated player.
	 * @param npcKilled The npc killed.
	 */
	public static void fightCavesReward(Player player, int npcKilled) {

		//TzTok-Jad
		if (npcKilled != 6506) {
			return;
		}
		if (player == null) {
			return;
		}
		player.startAnimation(862);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (container.getExecutions() == 4) {
					player.getPA().movePlayer(2438, 5168, 0);
				} else if (container.getExecutions() == 5) {
					fightCavesReward(player);
					player.setNpcType(2180);
					player.getDH().sendDialogues(171);
					player.setUsingFightCaves(false);
					container.stop();
				}

			}

			@Override
			public void stop() {
			}
		}, 1);

	}

}
