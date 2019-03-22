package game.npc.impl.kalphite_queen;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-01-12 at 10:05 AM
 */
//@CustomNpcComponent(id = 963, type = GameType.OSRS)
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type=GameType.OSRS, identity = 963)
})
public class KalphiteQueen extends Npc {

	private final KalphiteQueenCombatStrategy strategy = new KalphiteQueenCombatStrategy();

	public KalphiteQueen(int npcId, int npcType) {
		super(npcId, npcType);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public Npc copy(int index) {
		return new KalphiteQueen(index, npcType);
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
