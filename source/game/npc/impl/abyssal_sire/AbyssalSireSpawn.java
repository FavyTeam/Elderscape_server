package game.npc.impl.abyssal_sire;

import core.GameType;
import game.entity.Entity;
import game.entity.EntityType;
import game.entity.MovementState;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-03-02 at 1:22 PM
 */
//@CustomNpcComponent(id = 5917, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 5917))
public class AbyssalSireSpawn extends Npc implements EntityCombatStrategy {

	private TransformationState state = TransformationState.UNTRANSFORMED;

	public AbyssalSireSpawn(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new AbyssalSireSpawn(index, npcType);
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
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		getEventHandler().addEvent(this, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (container.getExecutions() == 18) {
					transform(5917);
					state = TransformationState.TRANSFORMING;
					setMovementState(MovementState.DISABLED);
					resetFollowing();
					resetFace();
				} else if (container.getExecutions() == 20) {
					transform(5918);
					requestAnimation(7123);
				} else if (container.getExecutions() >= 22) {
					container.stop();
					state = TransformationState.TRANSFORMED;
					setMovementState(MovementState.WALKABLE);
					setMaximumHealthAndHeal(50);
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	@Override
	public void onDeath() {
		super.onDeath();

		getEventHandler().stopAll();
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

	/**
	 * Determines if the attacker can attack the defender, by default this is true.
	 *
	 * @param attacker the entity we're determining can attack or not.
	 * @param defender the entity potentially being attacked.
	 * @return {@code true} if the attacker can attack the defender, also {@code true} by default.
	 */
	@Override
	public boolean canAttack(Entity attacker, Entity defender) {
		return state != TransformationState.TRANSFORMING;
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
		return state != TransformationState.TRANSFORMING;
	}

	private enum TransformationState {
		UNTRANSFORMED,

		TRANSFORMING,

		TRANSFORMED
	}


}
