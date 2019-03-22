package game.content.miscellaneous;

import core.ServerConstants;
import game.content.consumable.RegenerateSkill;
import game.content.interfaces.InterfaceAssistant;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.npc.pet.Pet;
import game.player.Player;

/**
 * Wolpertinger.
 *
 * @author MGT Madness, created on 16-01-2013.
 */
public class Wolpertinger {

	/**
	 * Wolpertinger special attack requirements.
	 * <p>
	 * Once they are met, the special attack method is called.
	 *
	 * @param player The associated player.
	 */
	public static void specialAttackRequirements(Player player) {
		if (player.getPetId() != 6869) {
			player.playerAssistant.sendMessage("You need a wolpertinger summoned in order to use this special attack.");
			return;
		}
		if (System.currentTimeMillis() - player.lastWolpertingerSpecialAttack < 3000) {
			player.playerAssistant.sendMessage("Your Wolpertinger is exhausted.");
			return;
		}
		if (!ItemAssistant.hasItemInInventory(player, 12437)) {
			player.playerAssistant.sendMessage("You do not have any magic focus scrolls left.");
			return;
		}
		initiateSpecialAttack(player);
	}

	/**
	 * Start the special attack.
	 *
	 * @param player The associated player.
	 */
	public static void initiateSpecialAttack(Player player) {
		player.playerAssistant.stopAllActions();
		player.lastWolpertingerSpecialAttack = System.currentTimeMillis();
		ItemAssistant.deleteItemFromInventory(player, 12437, 1);
		player.startAnimation(7660);
		player.gfx0(1303);
		increaseMagicLevel(player);
	}

	/**
	 * Increase magic level.
	 *
	 * @param player The associated player.
	 */
	public static void increaseMagicLevel(Player player) {
		player.currentCombatSkillLevel[ServerConstants.MAGIC] += 5;
		if (player.currentCombatSkillLevel[ServerConstants.MAGIC] > player.getBaseMagicLevel() + 5) {
			player.currentCombatSkillLevel[ServerConstants.MAGIC] = player.getBaseMagicLevel() + 5;
		}
		RegenerateSkill.storeBoostedTime(player, ServerConstants.MAGIC);
		Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
	}

	/**
	 * Summon Wolpertinger.
	 *
	 * @param player The associated player.
	 * @param deleteItem True, to delete the wolpertinger pouch.
	 */
	public static void summonWolpertinger(Player player, boolean deleteItem) {
		if (player.getPetSummoned()) {
			return;
		}
		player.setPetSummoned(true);
		if (deleteItem) {
			ItemAssistant.deleteItemFromInventory(player, 12089, 1);
		}
		Pet.summonNpcOnValidTile(player, 6869, false);
		informClientOn(player);
	}

	/**
	 * Inform the client that the Wolpertinger is not summoned. This will make the summoning orb not look active.
	 */
	public static void informClientOff(Player player) {
		player.playerAssistant.sendMessage(":summoningoff:");
		InterfaceAssistant.summoningOrbOff(player);
	}

	/**
	 * Inform the client that the Wolpertinger is summoned. This will make the summoning orb look active.
	 */
	public static void informClientOn(Player player) {
		player.playerAssistant.sendMessage(":summoningon:");
		InterfaceAssistant.summoningOrbOn(player);
	}

	/**
	 * Update the summoning orb, weather it has the active look or not.
	 *
	 * @param player The associated player.
	 */
	public static void updateSummoningOrb(Player player) {
		if (player.getPetId() == 6869) {
			informClientOn(player);
		} else {
			informClientOff(player);
		}
	}

}
