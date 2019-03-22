package game.content.packet.preeoc;

import core.GameType;
import game.content.skilling.summoning.Summoning;
import game.content.skilling.summoning.pet.impl.BabyTroll;
import game.content.skilling.summoning.pet.impl.Sparkles;
import game.npc.Npc;
import game.player.Player;

/**
 * Handles clicking npcs for pre eoc
 * 
 * @author 2012
 *
 */
public class ClickNpcPreEoc {

	/**
	 * Handles first npc click
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @return the click
	 */
	public static boolean firstClickNpcPreEoc(Player player, Npc npc) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		if (Summoning.pickupPet(player, npc, npc.npcType)) {
			return true;
		}
		return false;
	}

	/**
	 * Handles second npc click
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @return the click
	 */
	public static boolean secondClickNpcPreEocOnly(Player player, Npc npc) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		switch (npc.npcType) {
			// Pack yak
			case 6873:
				Summoning.openBobStorage(player);
				return true;
			case 14846:
				BabyTroll.sendRandomConversation(player);
				return true;
			case Sparkles.SPARKLES_PET:
				Sparkles.sendRandomConversation(player);
				return true;
		}
		return false;
	}

	/**
	 * Handles third npc click
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @param npcType the npc type
	 * @return the click
	 */
	public static boolean thirdClickNpcPreEoc(Player player, Npc npc, int npcType) {
		if (!GameType.isPreEoc()) {
			return false;
		}

		switch (npcType) {

		}
		return false;
	}

	/**
	 * Handles fourth npc click
	 * 
	 * @param player the player
	 * @param npc the npc
	 * @param npcType the npc type
	 * @return the click
	 */
	public static boolean fourthClickNpcPreEoc(Player player, Npc npc, int npcType) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		switch (npcType) {

		}
		return false;
	}
}
