package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.player.Player;

public class GuthanSetEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		attacker.addToHitPoints(damage.getDamage());
		victim.gfx0(398);
	}

}
