package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.skilling.Skilling;
import game.player.Player;

public class AncientMaceSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		Combat.applyPrayerReduction(attacker, victim, damage.getDamage(), false);
		// Ancient mace allows for over 99 prayer points.
		attacker.currentCombatSkillLevel[ServerConstants.PRAYER] += damage.getDamage();
		Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.PRAYER);
	}
}
