package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.skilling.agility.AgilityAssistant;
import game.player.Player;

public class ToragSetEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		victim.runEnergy *= 0.8;
		AgilityAssistant.updateRunEnergyInterface(victim);
		victim.gfx100(399);
		victim.getPA().sendMessage("Your opponent has drained your run energy!");
	}

}
