package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

/**
 * Monkey greegree.
 *
 * @author MGT Madness, Created on 05-01-2015.
 */
public class MonkeyGreeGree {

	/**
	 * Choose the monkey type to transform to and wear the Monkey greegree.
	 *
	 * @param player The associated player.
	 * @param monkeyType The monkey type.
	 * @param monkeyNPCid The monkey NPC identity.
	 * @param standAnimation Change player stand animation.
	 * @param walkAnimation Change player walk animation.
	 * @param runAnimation Change player run animation.
	 */
	public static void chooseTransformation(Player player, int monkeyType, int monkeyNPCid, int standAnimation, int walkAnimation, int runAnimation) {
		if (!ItemAssistant.hasItemInInventory(player, 4024)) {
			return;
		}
		ItemAssistant.wearItem(player, 4024, ItemAssistant.getItemSlot(player, 4024));
		transformToMonkey(player, monkeyType, monkeyNPCid, standAnimation, walkAnimation, runAnimation);
		player.getPA().closeInterfaces(true);
	}

	/**
	 * Transform the player into a Monkey.
	 *
	 * @param player The associated player.
	 * @param monkeyType The monkey type.
	 * @param monkeyNPCid The monkey NPC identity.
	 * @param standAnimation Change player stand animation.
	 * @param walkAnimation Change player walk animation.
	 * @param runAnimation Change player run animation.
	 */
	public static void transformToMonkey(Player player, int monkeyType, int monkeyNPCid, int standAnimation, int walkAnimation, int runAnimation) {
		player.setTransformed(monkeyType);
		player.playerStandIndex = standAnimation;
		player.playerWalkIndex = walkAnimation;
		player.playerRunIndex = runAnimation;
		player.playerTurn180Index = walkAnimation;
		player.playerTurnIndex = walkAnimation;
		player.playerTurn90CWIndex = walkAnimation;
		player.playerTurn90CCWIndex = walkAnimation;
		player.npcId2 = monkeyNPCid;
		player.setUpdateRequired(true);
		player.setAppearanceUpdateRequired(true);
	}
}
