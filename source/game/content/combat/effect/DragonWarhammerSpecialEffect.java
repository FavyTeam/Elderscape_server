package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;

public class DragonWarhammerSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		if (damage.getDamage() > 0) {
			int defence = (int) (victim.currentCombatSkillLevel[ServerConstants.DEFENCE] * 0.7);
			if (defence < 1) {
				defence = 1;
			}
			victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = defence;
			Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
			RegenerateSkill.storeBoostedTime(victim, ServerConstants.DEFENCE);
		}
	}

}
