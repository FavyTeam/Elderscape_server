package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;
import utility.Misc;

/**
 * Handles the morrigan throwing axe special effect
 * 
 * @author 2012
 *
 */
public class MorriganThrowingAxeSpecialEffect implements EntityDamageEffect {

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
		 * Drain energy
		 */
		if (System.currentTimeMillis() - victim.drainRunEnergyFaster > Misc.getMinutesToMilliseconds(1)) {
			victim.drainRunEnergyFaster = System.currentTimeMillis();
			victim.getPA().sendMessage("Your run energy has been greatly effected for the next 1 minute.");
		}
	}
}
