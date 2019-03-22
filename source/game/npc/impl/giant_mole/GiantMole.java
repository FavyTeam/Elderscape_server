package game.npc.impl.giant_mole;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-01-18 at 2:25 PM
 */
//@CustomNpcComponent(id = 5779, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 5779))
public class GiantMole extends Npc {

	private final GiantMoleCombatStrategy strategy = new GiantMoleCombatStrategy();

	public GiantMole(int npcId, int type) {
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
		return new GiantMole(index, npcType);
	}

	/**
	 * Referenced when the npc is added to the world.
	 */
	@Override
	public void onAdd() {
		super.onAdd();

		setWalkingHomeDisabled(true);
	}

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return strategy;
	}
}
