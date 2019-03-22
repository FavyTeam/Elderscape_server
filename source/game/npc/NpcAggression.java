package game.npc;

import game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class NpcAggression {

	public static List<NpcAggression> npcAggression = new ArrayList<NpcAggression>();

	public long timeNpcTypeAggressed;

	public String playerName = "";

	public String npcName = "";


	public NpcAggression(String playerName, String npcName, long timeNpcTypeAggressed) {
		this.timeNpcTypeAggressed = timeNpcTypeAggressed;
		this.playerName = playerName;
		this.npcName = npcName;
	}

	// Player goes to east dragons, the dragons will keep on aggressing for 20 minutes then automatically stop untill i relog or i leave area and come back.
	public static void clearNpcAggressionForSpecificPlayer(Player player) {
		if (!player.aggressedByNpc) {
			return;
		}
		player.aggressedByNpc = false;

		for (int index = 0; index < npcAggression.size(); index++) {
			NpcAggression instance = npcAggression.get(index);
			if (instance.playerName.equals(player.getPlayerName())) {
				npcAggression.remove(index);
				index--;
			}
		}
	}

	/**
	 * How many minutes the npc can automatically aggress on the player.
	 */
	private final static int AGGRESSION_DURATION = 20;

	public static boolean npcCannotAggress(Player player, Npc npc) {
		for (int index = 0; index < npcAggression.size(); index++) {
			if (index > npcAggression.size() - 1) {
				break;
			}
			NpcAggression instance = npcAggression.get(index);
			// Server crashed due to nulled instance.
			if (instance == null) {
				continue;
			}
			if (instance.npcName.equals(npc.name) && player.getPlayerName().equals(instance.playerName)) {
				if (System.currentTimeMillis() - instance.timeNpcTypeAggressed > AGGRESSION_DURATION * 60000) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	public static void startNewAggression(Player player, Npc npc) {
		for (int index = 0; index < npcAggression.size(); index++) {
			NpcAggression instance = npcAggression.get(index);
			if (instance.npcName.equals(npc.name) && player.getPlayerName().equals(instance.playerName)) {
				return;
			}
		}
		player.aggressedByNpc = true;
		npcAggression.add(new NpcAggression(player.getPlayerName(), npc.name, System.currentTimeMillis()));
	}

}
