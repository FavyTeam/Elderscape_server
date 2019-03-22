package game.npc.impl;

import core.ServerConstants;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.Player;
import utility.Misc;

public class VetionCombat {
	private static int coolDown = 6;

	public static int skeletonHellHoundsAlive;

	/**
	 * True if Vet'ion has transformed
	 */
	public static boolean vetionRebornAlive;

	public static boolean vetionMinionsSpawned;

	public static boolean vetionRebornMinionsSpawned;

	public static void attackPlayer(Player player, Npc n) {

		coolDown--;
		if (coolDown == 0) {
			n.requestAnimation(5507);
			int xDifference = n.getX() - player.getX();
			int yDifference = n.getY() - player.getY();
			if (yDifference <= 11 && xDifference <= 11) {
				n.attackType = ServerConstants.MELEE_ICON;
				coolDown = 6;
				return;
			}
		}
		n.requestAnimation(5499);

		int random = Misc.random(gfxDropOffset.length - 1);
		int counter = 0;
		for (int index = 0; index < 3; index++) {
			player.getPA().createPlayersStillGfx(281, player.getX() + gfxDropOffset[random][counter], player.getY() + gfxDropOffset[random][counter + 1], 0, 95);
			counter += 2;
		}
		n.attackType = ServerConstants.MAGIC_ICON;
	}

	private static void spawnHellHounds(Npc n) {
		int vetionX = n.getX();
		int vetionY = n.getY();

		boolean spawnMinions = false;
		if (skeletonHellHoundsAlive == 0) {
			if (vetionRebornAlive) {
				if (!vetionRebornMinionsSpawned) {
					spawnMinions = true;
				}
			} else {
				if (!vetionMinionsSpawned) {
					spawnMinions = true;
				}
			}
		}
		if (n.getCurrentHitPoints() > 127) {
			spawnMinions = false;
		}
		if (spawnMinions) {
			int hellhoundNpcId = 6613; // Skeleton hellhound
			String text = "Kill, my pets!";
			if (vetionRebornAlive) {
				text = "Bahh! Go, Dogs!!";
				hellhoundNpcId = 6614; // Greater skeleton hellhound
				vetionRebornMinionsSpawned = true;
			} else {
				vetionMinionsSpawned = true;
			}
			n.forceChat(text);
			int skeletonX = vetionX + 2;
			int skeletonY = vetionY;
			int[] teleportCoords = new int[2];

			if (!Region.isStraightPathUnblocked(skeletonX, skeletonY, vetionX, vetionY, n.getHeight(), 2, 2, false)) {
				teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, vetionX, vetionY, n.getHeight(), 2, 2);
				skeletonX = teleportCoords[0];
				skeletonY = teleportCoords[1];
			}
			NpcHandler.spawnNpc(null, hellhoundNpcId, skeletonX, skeletonY, 0, false, false);


			skeletonX = vetionX - 2;
			skeletonY = vetionY;
			if (!Region.isStraightPathUnblocked(skeletonX, skeletonY, vetionX, vetionY, n.getHeight(), 2, 2, false)) {
				teleportCoords = NpcHandler.teleportPlayerNextToNpc(0, 0, false, vetionX, vetionY, n.getHeight(), 2, 2);
				skeletonX = teleportCoords[0];
				skeletonY = teleportCoords[1];
			}
			Npc skeletonHellhound = NpcHandler.spawnNpc(null, hellhoundNpcId, skeletonX, skeletonY, 0, false, false);
			skeletonHellhound.forceChat("GRRRRRRRRRRR");
			skeletonHellHoundsAlive += 2;
		}

	}

	/**
	 * Offsets from the player's x and y of where the gfx will land, similair to Osrs.
	 */
	private final static int[][] gfxDropOffset =
			{
					//@formatter:off
					{
							0, 0,
							0, 1,
							0, 2
					},
					{
							1, 0,
							0, 0,
							-1, 0
					},
					{
							0, 0,
							1, 0,
							2, 0
					},
					{
							0, 0,
							1, 1,
							-1, -1
					},
					{
							0, 0,
							-1, -1,
							-2, -2,
					},
					{
							0, 0,
							1, -1,
							2, -2,
					},
					//@formatter:on
			};

	public static void vetionRelatedNpcsDeath(int npcType) {
		// Skeleton hellhounds or Grater Skeleton hellhounds
		if (npcType == 6613 || npcType == 6614) {
			skeletonHellHoundsAlive--;
		}

		// Vet'ion, Vet'ion only dies when it is Reborn, also Reborn is same id as normal Vet'ion because i transformed it.
		if (npcType == 6611) {
			vetionRebornAlive = false;
			vetionMinionsSpawned = false;
			vetionRebornMinionsSpawned = false;
		}
	}

	public static void vetionDeath(Npc npc) {
		// Vet'ion
		if (npc.npcType != 6611) {
			return;
		}
		if (vetionRebornAlive) {
			return;
		}
		npc.setDead(false);
		vetionRebornAlive = true;
		npc.setCurrentHitPoints(npc.maximumHitPoints);

		// Transform into Vet'ion Reborn
		npc.transform(6612);

		npc.forceChat("Do it again!!");

	}

	public static boolean isInvincibleVetion(Npc npc) {
		// Vet'ion or Ve'ion Reborn
		if (npc.npcType == 6611) {
			if (VetionCombat.skeletonHellHoundsAlive > 0) {
				return true;
			}
			spawnHellHounds(npc);
		}
		return false;
	}
}
