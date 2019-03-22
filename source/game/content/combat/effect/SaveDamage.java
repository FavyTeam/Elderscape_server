package game.content.combat.effect;

import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.miscellaneous.SpecialAttackTracker;
import game.player.Player;

public class SaveDamage implements EntityDamageEffect {

	private String damage;

	public SaveDamage(String damage) {
		this.damage = damage;
	}

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		return null;
	}

	@Override
	public void onApply(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		switch (this.damage) {
			case "FIRST":
				SpecialAttackTracker.saveMaximumDamage(attacker, damage.getDamage(), this.damage, false);
				break;
			case "SECOND":
				SpecialAttackTracker.saveMaximumDamage(attacker, damage.getDamage(), this.damage, false);
				break;
			case "DRAGON CLAWS FIRST":
				SpecialAttackTracker.storeDragonClawsDamage(attacker, damage.getDamage(), -1, -1, -1);
				break;
			case "DRAGON CLAWS SECOND":
				SpecialAttackTracker.storeDragonClawsDamage(attacker, -1, damage.getDamage(), -1, -1);
				break;
			case "DRAGON CLAWS THIRD":
				SpecialAttackTracker.storeDragonClawsDamage(attacker, -1, -1, damage.getDamage(), -1);
				break;
			case "DRAGON CLAWS FOURTH":
				SpecialAttackTracker.storeDragonClawsDamage(attacker, -1, -1, -1, damage.getDamage());
				SpecialAttackTracker.saveDragonClawsMaximumDamage(attacker, false);
				break;
		}

	}

}
