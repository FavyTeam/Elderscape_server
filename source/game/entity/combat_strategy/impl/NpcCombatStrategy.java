package game.entity.combat_strategy.impl;

import game.entity.EntityType;
import game.entity.combat_strategy.EntityCombatStrategy;

/**
 * Created by Jason MacKeigan on 2018-01-16 at 5:28 PM
 * <p>
 * Represents a strategy used by non-playable characters.
 */
public class NpcCombatStrategy implements EntityCombatStrategy {

	/**
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	@Override
	public final EntityType getAttackerType() {
		return EntityType.NPC;
	}

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	@Override
	public final EntityType getDefenderType() {
		return EntityType.PLAYER;
	}

}
