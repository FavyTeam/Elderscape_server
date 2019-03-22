package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.skilling.agility.AgilityAssistant;
import game.player.Player;

public class AbyssalWhipSpecial implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		if (damage.getDamage() == 0) {
			return;
		}
		Player victim = (Player) damage.getTarget();
		Player attacker = (Player) damage.getSender();
		victim.gfx100(341);
		int energyStolen = (int) (victim.runEnergy / 10);
		victim.runEnergy -= energyStolen;
		AgilityAssistant.updateRunEnergyInterface(victim);
		attacker.runEnergy += energyStolen;
		if (attacker.runEnergy > 100) {
			attacker.runEnergy = 100;
		}
		AgilityAssistant.updateRunEnergyInterface(attacker);
	}

}
