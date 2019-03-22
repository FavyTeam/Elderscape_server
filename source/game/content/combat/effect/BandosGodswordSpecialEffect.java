package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;

public class BandosGodswordSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player victim = (Player) damage.getTarget();
		int defence = victim.currentCombatSkillLevel[ServerConstants.DEFENCE] - damage.getDamage();
		victim.playerAssistant.sendMessage("You feel weak.");
		if (defence < 1) {
			defence = 1;
		}
		victim.currentCombatSkillLevel[ServerConstants.DEFENCE] = defence;
		Skilling.updateSkillTabFrontTextMain(victim, ServerConstants.DEFENCE);
		RegenerateSkill.storeBoostedTime(victim, ServerConstants.DEFENCE);
	}

}
