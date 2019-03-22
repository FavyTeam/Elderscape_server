package game.content.combat.effect;

import core.GameType;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;
import game.player.movement.Movement;
import utility.Misc;

/**
 * Handles dominion glove effect
 * 
 * @author 2012
 *
 */
public class DominionGloveEffect implements EntityDamageEffect {

	/**
	 * The skills
	 */
	private static final int[] SKILLS = {ServerConstants.ATTACK, ServerConstants.STRENGTH,
			ServerConstants.DEFENCE, ServerConstants.RANGED};

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		/*
		 * No gloves
		 */
		if (!hasGoliathGloves((Player) damage.getSender())) {
			return damage;
		}
		/*
		 * Increase damage
		 */
		if (Misc.hasPercentageChance(5)) {
			damage.setDamage((int) (damage.getDamage() * 1.25));
		}
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		/*
		 * No gloves
		 */
		if (!hasGoliathGloves((Player) damage.getSender())) {
			return;
		}
		/*
		 * The victim
		 */
		Player victim = (Player) damage.getTarget();
		/*
		 * Invalid victim
		 */
		if (victim == null) {
			return;
		}
		/*
		 * Lower skill
		 */
		if (Misc.hasPercentageChance(25)) {
			/*
			 * The skill to lower
			 */
			int lower = ServerConstants.ATTACK;
			/*
			 * Get the skill
			 */
			for (int skill : SKILLS) {
				/*
				 * Found the skill
				 */
				if (victim.currentCombatSkillLevel[skill] >= Skilling
						.getLevelForExperience(victim.skillExperience[skill])) {
					lower = skill;
					break;
				}
			}
			/*
			 * Lower the level
			 */
			victim.currentCombatSkillLevel[lower] -= 7;
			/*
			 * Message
			 */
			victim.playerAssistant.sendMessage("You feel weaken.");
			/*
			 * Update
			 */
			Skilling.updateSkillTabFrontTextMain(victim, lower);
			RegenerateSkill.storeBoostedTime(victim, lower);
		} else if (Misc.hasPercentageChance(25)) {
			/*
			 * Stun
			 */
			victim.setFrozenLength(5_000);
			victim.getPA().sendMessage("<col=ff0000>You have been stunned!");
			Movement.stopMovement(victim);
			Combat.resetPlayerAttack(victim);
		}
	}

	/**
	 * Checking whether has goliath gloves
	 * 
	 * @param player the player
	 * @return has gloves
	 */
	public static boolean hasGoliathGloves(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HAND_SLOT] == 22_358
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_359
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_360
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_361);
	}

	/**
	 * Checking whether has swift gloves
	 * 
	 * @param player the player
	 * @return has gloves
	 */
	public static boolean hasSwiftGloves(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HAND_SLOT] == 22_362
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_363
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_364
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_365);
	}

	/**
	 * Checking whether has spellcaster gloves
	 * 
	 * @param player the player
	 * @return has gloves
	 */
	public static boolean hasSpellcasterGloves(Player player) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return (player.playerEquipment[ServerConstants.HAND_SLOT] == 22_366
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_367
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_368
				|| player.playerEquipment[ServerConstants.HAND_SLOT] == 22_369);
	}
}
