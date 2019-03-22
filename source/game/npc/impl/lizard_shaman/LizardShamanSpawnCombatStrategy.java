package game.npc.impl.lizard_shaman;

import game.entity.Entity;
import game.entity.combat_strategy.impl.NpcCombatStrategy;

/**
 * Created by Jason MacKeigan on 2018-01-17 at 11:14 AM
 */
public class LizardShamanSpawnCombatStrategy extends NpcCombatStrategy {

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return false;
	}

	/**
	 * Determines if the defender can be attacked by the attacker.
	 *
	 * @param attacker the entity attacking the defender.
	 * @param defender the entity defending off the attack.
	 * @return {@code true} if the defender can be attacked, and {@code true} by default.
	 */
	@Override
	public boolean canBeAttacked(Entity attacker, Entity defender) {
		return false;
	}
}
