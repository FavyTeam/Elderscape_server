package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import utility.Misc;

/**
 * Handles the abyssal vine whip special effect
 * 
 * @author 2012
 *
 */
public class AbyssalVineWhipSpecialEffect implements EntityDamageEffect {

	/**
	 * The max attacks
	 */
	private static final int MAX_ATTACKS = 10;

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
		 * Performs the special
		 */
		attacker.getEventHandler().addEvent(attacker, new CycleEvent<Entity>() {
			/*
			 * The attacks
			 */
			int totalAttacks = 0;

			@Override
			public void execute(CycleEventContainer<Entity> container) {
				/*
				 * Too far
				 */
				if (attacker.getPA().withInDistance(attacker.getX(), attacker.getY(), victim.getX(),
						victim.getY(), 2)) {
					/*
					 * Hits player
					 */
					Combat.createHitsplatOnPlayerPvp(attacker, victim, 1 + Misc.random(9),
							ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
					/*
					 * The chance of poison
					 */
					if (Misc.random(10) == 1) {
						Poison.appendPoison(attacker, victim, false, 5);
					}
				}
				/*
				 * Increase attacks
				 */
				totalAttacks++;
				/*
				 * Finished special
				 */
				if (totalAttacks == MAX_ATTACKS) {
					container.stop();
				}
			}

			@Override
			public void stop() {

			}
		}, 3);
	}
}
