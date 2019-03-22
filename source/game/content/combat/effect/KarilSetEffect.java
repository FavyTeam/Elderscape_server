package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.combat.damage.EntityDamageType;
import game.player.Player;

public class KarilSetEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		int damageAmount = damage.getDamage();
		if (Combat.hasAmuletOfTheDamned(attacker)) {
			damageAmount = (int) (damageAmount * 0.50);
			if (damageAmount > victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS)) {
				damageAmount = victim.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
			}
			attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(victim, attacker, damageAmount, 1, EntityDamageType.RANGED, attacker.maximumDamageRanged, false, false));
		}
		// Effects.karilsEffect(attacker, victim, damage);
	}

}
