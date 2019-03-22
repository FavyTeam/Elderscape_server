package game.npc.impl;

import core.GameType;
import core.ServerConstants;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.Teleport;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import utility.Misc;

import java.util.ArrayList;

public class KrakenCombat {

	public static int getKrakenInstancedRoomCost() {
		return GameType.isOsrsPvp() ? 1500 : 1100000;
	}

	private final static int[][] WHIRLPOOL_RESPAWN_COORDINATES =
			{
					{2275, 10038},
					{2275, 10034},
					{2284, 10038},
					{2284, 10034},
			};

	public static void instancedRoom(Player player) {
		if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), KrakenCombat.getKrakenInstancedRoomCost())) {
			Teleport.spellTeleport(player, 2280, 10022, 24 + (player.getPlayerId() * 4), false);
		} else {
			player.getDH().sendStatement("You need at least x" + Misc.formatNumber(KrakenCombat.getKrakenInstancedRoomCost()) + " to enter an instanced room.");
		}
	}

	public static void spawnInstancedRoom(Player player) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				//There is a weird bug where the kraken does not get deleted, so just delete all Kraken related npcs before spawning.
				for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
					Npc npc = NpcHandler.npcs[index];
					if (npc == null) {
						continue;
					}
					if (npc.getHeight() != player.getHeight()) {
						continue;
					}
					// Whirlpool & Master whirlpool
					if (npc.npcType == 493 || npc.npcType == 496) {
						Pet.deletePet(npc);
					}
				}
				Npc npc = NpcHandler.spawnNpc(player, 496, 2278, 10035, 24 + (player.getPlayerId() * 4), false, false);
				npc.instancedNpcForceRespawn = true;
				for (int index = 0; index < KrakenCombat.WHIRLPOOL_RESPAWN_COORDINATES.length; index++) {
					NpcHandler.spawnNpc(player, 493, KrakenCombat.WHIRLPOOL_RESPAWN_COORDINATES[index][0], KrakenCombat.WHIRLPOOL_RESPAWN_COORDINATES[index][1],
					                    24 + (player.getPlayerId() * 4), false, false);
				}
			}
		}, 1);

	}

	public static void tentacleAttack(Player player, Npc npc) {
		npc.attackType = ServerConstants.MAGIC_ICON;
		int nX = npc.getX();
		int nY = npc.getY();
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;
		int distance = npc.distanceTo(player.getX(), player.getY());
		int speed = 65 + (distance * 5);
		player.getPA().createPlayersProjectile(nX, nY, offX, offY, 51, speed, 162, 53, 33, -player.getPlayerId() - 1, 40, 15);
	}

	public static void krakenAttack(Player player, Npc npc) {

		int nX = npc.getX() + 2;
		int nY = npc.getY() + 2;
		int pX = player.getX();
		int pY = player.getY();
		int offX = (nY - pY) * -1;
		int offY = (nX - pX) * -1;

		int distance = npc.distanceTo(player.getX(), player.getY());

		int speed = 75 + (distance * 5);
		player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, speed, 156, 43, 33, -player.getPlayerId() - 1, 65, 15);

		npc.attackType = ServerConstants.MAGIC_ICON;
		npc.endGfx = 157;
		//n.getDamage().add(new Damage(player, n, ServerConstants.MAGIC_ICON, 3, -1, Misc.random(krakenMaxHit)));
	}

	public static Npc disturbWhirpool(Npc npc) {
		// Whirlpool
		if (npc.npcType == 493 && npc.transformId == -1) {
			npc.transform(5535);
			npc.requestAnimation(3860);
			npc.doNotRespawn = true;
		}
		return npc;
	}

	public static Npc disturbMasterWhirlpool(Player player, Npc npc) {

		// Master Whirlpool
		if (npc.npcType == 496 && npc.transformId == -1) {
			if (isAllWhirpoolsDisturbed(npc.getHeight())) {

				npc.transform(494);
				npc.requestAnimation(7135);
				return npc;
			} else {
				player.getPA().sendMessage("You need to disturb all whirpools before awakening the Kraken.");
			}
		}
		return npc;
	}

	public static void krakenRespawn(Npc masterNpc) {
		if (masterNpc.npcType != 496) {
			return;
		}
		for (int index = 0; index < WHIRLPOOL_RESPAWN_COORDINATES.length; index++) {
			NpcHandler.spawnDefaultNpc(493, "Whirlpool", WHIRLPOOL_RESPAWN_COORDINATES[index][0], WHIRLPOOL_RESPAWN_COORDINATES[index][1], masterNpc.getHeight(), "", 0, -1);
		}
	}

	public static void krakenDeath(Player player, Npc masterNpc) {
		if (masterNpc.npcType != 496) {
			return;
		}
		RandomEvent.randomEvent(player, 30);
		for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
			Npc npc = NpcHandler.npcs[index];
			if (npc == null) {
				continue;
			}
			if (npc.getHeight() != masterNpc.getHeight()) {
				continue;
			}
			if (npc.isDead()) {
				continue;
			}

			// Whirlpool
			if (npc.npcType == 493) {
				npc.requestAnimation(3620);
				npc.doNotRespawn = true;
				npc.setDead(true);

			}
		}
	}

	public static boolean isAllWhirpoolsDisturbed(int playerHeight) {
		int whirpoolsAlive = 0;
		for (int index = 0; index < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; index++) {
			Npc npc = NpcHandler.npcs[index];
			if (npc == null) {
				continue;
			}
			if (npc.getHeight() != playerHeight) {
				continue;
			}
			if (npc.isDead()) {
				continue;
			}

			// Whirlpool
			if (npc.npcType == 493 && npc.transformId == -1) {
				whirpoolsAlive++;
			}
		}
		return whirpoolsAlive == 0;
	}
}
