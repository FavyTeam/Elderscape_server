package game.content.miscellaneous;

import game.player.Player;
import game.player.movement.Movement;

/**
 * Transform into monkey/egg etc..
 *
 * @author MGT Madness, created on 02-03-2015.
 */
public class Transform {

	/**
	 * Transform into a random Easter egg.
	 *
	 * @param player The associated player.
	 */
	public static void npcTransform(Player player, int npcId) {
		if (player.getDuelStatus() >= 1) {
			return;
		}
		Movement.stopMovement(player);
		player.justTransformed = true;
		player.npcId2 = npcId;
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
		player.isAnEgg = true;
		player.setTransformed(npcId);
	}

	/**
	 * Transform the player back to normal.
	 *
	 * @param player The associated player.
	 */
	public static void unTransform(Player player) {
		if (player.npcId2 == -1) {
			return;
		}
		player.npcId2 = -1;
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
		player.isAnEgg = false;
		player.setTransformed(0);
	}

	/*
	 *
	 * Sheep
		int npcId = 2798;
		int standAnimation = 5339;
		int walkAnimation = 5340;
	 */
	public static void transformToNpc(Player player, int npcId, int stand, int walk) {
		MonkeyGreeGree.transformToMonkey(player, npcId, npcId, stand, walk, walk);
	}

}
