package game.entity.combat_strategy;

import game.entity.EntityType;

/**
 * Created by Jason MacKeigan on 2018-01-05 at 1:51 PM
 * <p>
 * A static-factory class used to determine certain characteristics of a combat strategy.
 */
public final class EntityCombatStrategyFactory {

	private EntityCombatStrategyFactory() {
		throw new AssertionError("Cannot create instance of this class, it is a static-factory class.");
	}

	/**
	 * Determines if the strategies attacker and defender types meet that of the arguments provided.
	 *
	 * @param strategy the combat strategy.
	 * @param attackerType the entity type of the attacker.
	 * @param defenderType the entity type of the defender.
	 * @return {@code true} of the attacker and defender type meet that of the strategy.
	 */
	private static boolean isType(EntityCombatStrategy strategy, EntityType attackerType, EntityType defenderType) {
		return strategy.getAttackerType() == attackerType && strategy.getDefenderType() == defenderType;
	}

	/**
	 * Determines if the strategy is player versus npc.
	 *
	 * @param strategy the strategy of combat.
	 * @return {@code true} if the attacker is of type {@link EntityType#PLAYER} and the defender is of type {@link EntityType#NPC}.
	 */
	public static boolean isPlayerVersusNpc(EntityCombatStrategy strategy) {
		return isType(strategy, EntityType.PLAYER, EntityType.NPC);
	}

	/**
	 * Determines if the strategy is npc versus player.
	 *
	 * @param strategy the combat strategy.
	 * @return {@code true} if the attacker is of type {@link EntityType#NPC} and the defender is of type {@link EntityType#PLAYER}.
	 */
	public static boolean isNpcVersusPlayer(EntityCombatStrategy strategy) {
		return isType(strategy, EntityType.NPC, EntityType.PLAYER);
	}

}
