package game.content.miscellaneous;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.interfaces.InterfaceAssistant;
import game.content.skilling.Skilling;
import game.player.Area;
import game.player.Player;

/**
 * Set combat skill feature for 'Set Combat' accounts.
 *
 * @author MGT Madness, created on 10-01-2015.
 */
public class EditCombatSkill {

	/**
	 * True, if the player has all the requirements to change combat skills.
	 */
	private static boolean hasEditCombatSkillRequirements(Player player) {
		if (player.getHeight() == 20) {
			return false;
		}
		if (!player.getAbleToEditCombat()) {
			player.playerAssistant.sendMessage("You will be able to edit your combat stats after you have achieved maxed combat.");
			return false;
		}
		if (Area.inDangerousPvpArea(player)) {
			player.playerAssistant.sendMessage("You cannot use this in a Pvp area.");
			return false;
		}
		if (player.getDuelStatus() >= 1) {
			player.playerAssistant.sendMessage("You cannot use this in a duel.");
			return false;
		}
		for (int j = 0; j < player.playerEquipment.length; j++) {
			if (player.playerEquipment[j] > 0) {
				player.playerAssistant.sendMessage("Please remove all your equipment before using this.");
				return false;
			}
		}
		if ((player.getUnderAttackBy() > 0 || player.getNpcIndexAttackingPlayer() > 0 && Combat.wasAttackedByNpc(player))) {
			player.playerAssistant.sendMessage("You cannot use this in combat.");
			return false;
		}
		return true;
	}

	/**
	 * Change the combat skill.
	 *
	 * @param player The associated player.
	 * @param skill The skill to change.
	 */
	public static void editCombatSkillAction(Player player, String skill) {
		if (!hasEditCombatSkillRequirements(player) && !player.isAdministratorRank()) {
			return;
		}

		InterfaceAssistant.showAmountInterface(player, skill, "Enter amount");
	}

	public static void editCombatSkill(Player player, int xAmount) {
		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1) {
			return;
		}
		if (player.getAmountInterface().equals("ATTACK")) {
			changeCombatSkill(player, 0, xAmount);
		} else if (player.getAmountInterface().equals("DEFENCE")) {

			changeCombatSkill(player, 1, xAmount);
		} else if (player.getAmountInterface().equals("STRENGTH")) {

			changeCombatSkill(player, 2, xAmount);
		} else if (player.getAmountInterface().equals("RANGED")) {

			changeCombatSkill(player, 4, xAmount);
		} else if (player.getAmountInterface().equals("PRAYER")) {

			changeCombatSkill(player, 5, xAmount);
		} else if (player.getAmountInterface().equals("MAGIC")) {
			changeCombatSkill(player, 6, xAmount);
		}
	}

	public static void changeCombatSkill(Player player, int skill, int level) {
		if (!hasEditCombatSkillRequirements(player) && !player.isAdministratorRank()) {
			return;
		}

		if (level > 99) {
			level = 99;
		} else if (level < 0) {
			level = 1;
		}
		player.skillExperience[skill] = Skilling.getExperienceForLevel(level);
		player.baseSkillLevel[skill] = level;
		player.currentCombatSkillLevel[skill] = level;
		calculateHitPoints(player);
		// This has to be after calculateHitPoints, because giving extra
		//experience in attack skill for example, will give additional hitpoint experience.
		if (player.baseSkillLevel[skill] == 99) {
			player.skillExperience[skill] = (player.skillExperience[skill] + player.combatExperienceGainedAfterMaxed[skill]);
		}
		Skilling.updateSkillTabFrontTextMain(player, skill);
		player.getPA().setSkillLevel(skill, player.baseSkillLevel[skill], player.skillExperience[skill]);
		Combat.resetPrayers(player);
		player.setVengeance(false);
	}

	/**
	 * Called after the skill has been changed through clicking on skill icon.
	 * <p>
	 * Calculate the HP depending on the other skills.
	 */
	public static void calculateHitPoints(Player player) {
		int attackXp = player.skillExperience[0];
		if (attackXp > 13034431) {
			attackXp = 13034431;
		}
		int defenceXp = player.skillExperience[1];
		if (defenceXp > 13034431) {
			defenceXp = 13034431;
		}
		int strengthXp = player.skillExperience[2];
		if (strengthXp > 13034431) {
			strengthXp = 13034431;
		}
		int rangedXp = player.skillExperience[4];
		if (rangedXp > 13034431) {
			rangedXp = 13034431;
		}
		int totalMeleeStatsXp = attackXp + defenceXp + strengthXp;
		int totalHPXP = 0;
		totalHPXP = totalMeleeStatsXp / 4;
		totalHPXP += rangedXp / 6;
		totalHPXP *= 1.3;
		totalHPXP += 1154;
		player.skillExperience[ServerConstants.HITPOINTS] = totalHPXP;
		player.baseSkillLevel[ServerConstants.HITPOINTS] = Skilling.getLevelForExperience(player.skillExperience[ServerConstants.HITPOINTS]);
		if (player.baseSkillLevel[ServerConstants.HITPOINTS] == 99) {
			player.skillExperience[ServerConstants.HITPOINTS] = 13034431 + player.combatExperienceGainedAfterMaxed[ServerConstants.HITPOINTS];
		}
		player.setHitPoints(player.getBaseHitPointsLevel());
		Skilling.updateSkillTabExperienceHover(player, ServerConstants.HITPOINTS, false);
		player.playerAssistant.calculateCombatLevel();
		InterfaceAssistant.updateCombatLevel(player);
	}

}
