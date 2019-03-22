package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.skilling.Skilling;
import game.player.Player;

public class SaradominGodswordSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();

		if (damage.getDamage() > 0) {
			int heal = damage.getDamage() / 2;
			if (heal < 10) {
				heal = 10;
			}
			if (attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) < attacker.getBaseHitPointsLevel()) {
				if (heal + attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > attacker.getBaseHitPointsLevel()) {
					heal = attacker.getBaseHitPointsLevel() - attacker.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}
				attacker.addToHitPoints(heal);
			}


			int prayer = damage.getDamage() / 4;
			if (prayer < 5) {
				prayer = 5;
			}
			if (attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER) < attacker.getBasePrayerLevel()) {
				if (prayer + attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > attacker.getBasePrayerLevel()) {
					prayer = attacker.getBasePrayerLevel() - attacker.getCurrentCombatSkillLevel(ServerConstants.PRAYER);
				}
				attacker.currentCombatSkillLevel[ServerConstants.PRAYER] += prayer;
				Skilling.updateSkillTabFrontTextMain(attacker, ServerConstants.PRAYER);
			}
		}
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
	}
}
