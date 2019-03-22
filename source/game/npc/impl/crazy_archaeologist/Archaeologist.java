package game.npc.impl.crazy_archaeologist;

import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;

/**
 * Created by Jason MacKeigan on 2018-01-09 at 12:14 PM
 */
public abstract class Archaeologist extends Npc {

	public Archaeologist(int npcId, int type) {
		super(npcId, type);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public abstract Npc copy(int index);

	/**
	 * The combat strategy for the entity, or null if there is no combat strategy to be used.
	 *
	 * @return the strategy used against other entities.
	 */
	@Override
	public abstract EntityCombatStrategy getCombatStrategyOrNull();
}
