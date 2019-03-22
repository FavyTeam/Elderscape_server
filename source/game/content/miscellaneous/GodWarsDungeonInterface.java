package game.content.miscellaneous;

import game.npc.Npc;
import game.player.Player;

/**
 * God wars dungeon interface system.
 *
 * @author MGT Madness, created on 21-01-2016.
 */
public class GodWarsDungeonInterface {

	/**
	 * Update the Gwd interface.
	 *
	 * @param player
	 */
	public static void updateGwdInterface(Player player) {
		for (int i = 0; i < 4; i++) {
			player.getPA().sendFrame126("@cya@" + player.gwdKills[0 + i] + "", 25963 + i);
		}
	}

	/**
	 * Add to the Gwd interface variables.
	 *
	 * @param player
	 * @param npc
	 */
	public static void addGwdKillCount(Player player, Npc npc) {
		boolean added = false;
		switch (npc.npcType) {
			case 6222:
				player.gwdKills[0]++;
				added = true;
				break;
			case 6260:
				player.gwdKills[1]++;
				added = true;
				break;
			case 6247:
				player.gwdKills[2]++;
				added = true;
				break;
			case 6203:
				player.gwdKills[3]++;
				added = true;
				break;
		}
		if (added) {
			updateGwdInterface(player);
		}
	}

	/**
	 * Reset the Gwd data.
	 *
	 * @param player
	 */
	public static void resetGwdData(Player player) {
		for (int i = 0; i < 4; i++) {
			player.gwdKills[0 + i] = 0;
		}
	}
}
