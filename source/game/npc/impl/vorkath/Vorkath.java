package game.npc.impl.vorkath;

import core.GameType;
import game.position.Position;
import game.entity.Entity;
import game.entity.MovementState;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.TransientAttributeKey;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-02-06 at 11:27 AM
 */
//@CustomNpcComponent(id = 8059, type = GameType.OSRS)
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type=GameType.OSRS, identity = 8059)
})
public class Vorkath extends Npc {

	private final VorkathCombatStrategy combatStrategy = new VorkathCombatStrategy();

	public static AttributeKey<VorkathState> STATE_ATTRIBUTE = new TransientAttributeKey<>(VorkathState.UNPOKED);

	public Vorkath(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();
		super.setNeverRandomWalks(true);
		super.setMovementState(MovementState.DISABLED);
		setIdleAnimation(7946);
		setFacingEntityDisabled(true);
	}

	public void poke(Player player) {
		if (player.getHeight() == 0) {
			return;
		}
		VorkathState state = getAttributes().getOrDefault(STATE_ATTRIBUTE);

		if (state == VorkathState.POKED || state == VorkathState.POKING) {
			return;
		}
		player.startAnimation(827);
		getAttributes().put(STATE_ATTRIBUTE, VorkathState.POKING);
		getEventHandler().addEvent(this, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (container.getExecutions() == 1) {
					requestAnimation(7950);
				} else if (container.getExecutions() >= 6) {
					container.stop();

					getAttributes().put(STATE_ATTRIBUTE, VorkathState.POKED);
					requestAnimation(7948);
					transform(8061);
					setFacingEntityDisabled(false);
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	@Override
	public Position getDropPosition() {
		return new Position(2272, 4060, getHeight());
	}

	public void reset() {
		if (getAttributes().getOrDefault(STATE_ATTRIBUTE) == VorkathState.UNPOKED) {
			return;
		}
		getAttributes().put(STATE_ATTRIBUTE, VorkathState.UNPOKED);
		transform(8059);
		setIdleAnimation(7946);
		setFacingEntityDisabled(true);
	}

	@Override
	public void onAddToLocalList(Player player) {
		super.onAddToLocalList(player);

		VorkathState state = getAttributes().getOrDefault(STATE_ATTRIBUTE);

		setIdleAnimation(state == VorkathState.POKED ? 7948 : 7946);
		transform(state == VorkathState.POKED ? 8061 : 8058);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new Vorkath(index, npcType);
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return combatStrategy;
	}
}
