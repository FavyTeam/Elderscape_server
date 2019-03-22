package game.content.combat.effect;

import game.content.combat.Poison;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;
import utility.Misc;

public class AbyssalTentacleSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		victim.gfx100(341);
		victim.setFrozenLength(5000);
		victim.frozenBy = attacker.getPlayerId();
		if (Misc.hasPercentageChance(25)) {
			Poison.appendPoison(attacker, victim, false, 4);
		}
	}

}
