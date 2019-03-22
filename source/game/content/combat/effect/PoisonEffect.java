package game.content.combat.effect;

import game.content.combat.Poison;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;

public class PoisonEffect implements EntityDamageEffect {

	private int poisonDamage;

	public PoisonEffect(int poisonDamage) {
		this.poisonDamage = poisonDamage;
	}

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		Poison.appendPoison(attacker, victim, false, poisonDamage);
	}

}
