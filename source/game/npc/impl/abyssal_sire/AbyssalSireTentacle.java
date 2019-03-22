package game.npc.impl.abyssal_sire;

import core.GameType;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-02-26 at 10:19 AM
 */
//@CustomNpcComponent(id = 5909, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 5909))
public class AbyssalSireTentacle extends Npc implements EntityCombatStrategy {

	public AbyssalSireTentacle(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		setAttackWithPathBlocked(true);
		setMovementState(MovementState.DISABLED);
		setNeverRandomWalks(true);
		setWalkingHomeDisabled(true);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new AbyssalSireTentacle(index, npcType);
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return this;
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
		return getAttributes().getOrDefault(AbyssalSire.AWAKE, false);
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

	/**
	 * The type of the attacker.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getAttackerType() {
		return EntityType.NPC;
	}

	/**
	 * The type of the defender.
	 *
	 * @return the entity type.
	 */
	@Override
	public EntityType getDefenderType() {
		return EntityType.PLAYER;
	}
}
