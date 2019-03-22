package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.consumable.RegenerateSkill;
import game.content.skilling.Skilling;
import game.player.Player;
import utility.Misc;

public class BarrelchestAnchorSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		int reduction = damage.getDamage() / 10;
		if (reduction > 0) {
			int index = ServerConstants.ATTACK;
			if (Misc.hasPercentageChance(30)) {
				index = ServerConstants.DEFENCE;
			} else if (Misc.hasPercentageChance(30)) {
				index = ServerConstants.RANGED;
			} else if (Misc.hasPercentageChance(30)) {
				index = ServerConstants.MAGIC;
			}
			int amount = victim.getCurrentCombatSkillLevel(index) - reduction;
			if (amount < 1) {
				amount = 1;
			}
			victim.currentCombatSkillLevel[index] = amount;
			Skilling.updateSkillTabFrontTextMain(victim, index);
			RegenerateSkill.storeBoostedTime(victim, index);
		}
	}
}
