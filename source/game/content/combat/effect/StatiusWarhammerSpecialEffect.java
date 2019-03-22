package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;

/**
 * Handles the statius warhammer special effect
 * 
 * @author 2012
 *
 */
public class StatiusWarhammerSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		/*
		 * No damage
		 */
		if (damage.getDamage() == 0) {
			return;
		}
		/*
		 * The attacker
		 */
		Player attacker = (Player) damage.getSender();
		/*
		 * Invalid attacker
		 */
		if (attacker == null) {
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
		 * The maximum drain
		 */
		int maximumDrain = (int) (Skilling.getLevelForExperience(victim.skillExperience[ServerConstants.DEFENCE]) * 0.70);
		victim.playerAssistant.sendMessage("You feel weakened.");
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = maximumDrain;
		Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
		RegenerateSkill.storeBoostedTime(victim, ServerConstants.DEFENCE);
	}
}
