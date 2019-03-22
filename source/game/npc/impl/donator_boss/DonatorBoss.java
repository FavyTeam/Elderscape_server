package game.npc.impl.donator_boss;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Owain, 05/07/18
 */

@CustomNpcComponent(identities = @GameTypeIdentity(type = GameType.OSRS, identity = 7884))
public class DonatorBoss extends Npc {

	private final DonatorBossCombatStrategy strategy = new DonatorBossCombatStrategy();

	public DonatorBoss(int npcId, int type) {
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
		return new DonatorBoss(index, npcType);
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
