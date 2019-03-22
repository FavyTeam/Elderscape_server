package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;

/**
 * Handles the morrigans javelin special effect
 * 
 * @author 2012
 *
 */
public class MorrigansJavelinSpecialEffect implements EntityDamageEffect {

	/**
	 * The max damage to inflict
	 */
	private static final int MAX_DAMAGE = 5;

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
		 * The damage
		 */
		final int totalDamage = damage.getDamage();
		/*
		 * Performs the special attack
		 */
		attacker.getEventHandler().addEvent(attacker, new CycleEvent<Entity>() {

			/*
			 * The damage done
			 */
			int damageDone = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * More damage left to do than less damage
				 */
				if (totalDamage - damageDone >= MAX_DAMAGE) {
					/*
					 * Hits the player
					 */
					Combat.createHitsplatOnPlayerPvp(attacker, victim, MAX_DAMAGE,
							ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
					/*
					 * Add the damage
					 */
					damageDone += MAX_DAMAGE;
				} else {
					/*
					 * The damage left to do
					 */
					int leftDamage = totalDamage - damageDone;
					/*
					 * Hits the player
					 */
					Combat.createHitsplatOnPlayerPvp(attacker, victim, leftDamage,
							ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
					/*
					 * Add the damage
					 */
					damageDone += leftDamage;
				}
				/*
				 * Done all damage
				 */
				if (damageDone >= totalDamage) {
					container.stop();
				}
			}

			@Override
			public void stop() {

			}
		}, 3);
	}
}
