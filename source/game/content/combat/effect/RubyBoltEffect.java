package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;

public class RubyBoltEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		attacker.subtractFromHitPoints(attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 10);
		victim.subtractFromHitPoints(victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) / 8);
	}

}
