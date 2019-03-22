package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Handles the vesta spear special effect
 * 
 * @author 2012
 *
 */
public class VestaSpearSpecialEffect implements EntityDamageEffect {

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
		 * Immune to melee attacks
		 */
		attacker.immuneToMeleeAttacks = System.currentTimeMillis();
		/*
		 * In multi
		 */
		if (Area.inMulti(attacker.getX(), attacker.getY())) {
			/*
			 * Local players
			 */
			for (Player players : damage.getSender().getLocalPlayers()) {
				/*
				 * Too far
				 */
				if (!players.getPA().withInDistance(attacker.getX(), attacker.getY(), players.getX(),
						players.getY(), 2)) {
					continue;
				}
				/*
				 * Hits the players
				 */
				Combat.createHitsplatOnPlayerPvp(attacker, victim, 1 + Misc.random(23),
						ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, true, "", -1);
			}
		}
	}
}
