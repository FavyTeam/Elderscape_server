package game.npc.impl.scorpia;

import game.entity.Entity;
import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;

/**
 * Created by Jason MacKeigan on 2018-01-08 at 10:32 AM
 */
public class ScorpiaGuardianCombatStrategy implements EntityCombatStrategy {

	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

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
	 * Determines if the target is lost when the {@link #canAttack(Entity, Entity)} condition returns false.
	 *
	 * @return {@code true} if the target should be reset when they cannot be attacked, by default {@code false}.
	 */
	@Override
	public boolean resetsTargetOnCannotAttack() {
		return true;
	}
}
