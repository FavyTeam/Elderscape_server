package game.npc.impl.cerberus;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-02-09 at 11:34 AM
 */
@CustomNpcComponent(identities = {
		@GameTypeIdentity(type = GameType.OSRS, identity = 5862)
})
public class Cerberus extends Npc {

	private final CerberusCombatStrategy combatStrategy = new CerberusCombatStrategy();

	public Cerberus(int npcId, int type) {
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
		return new Cerberus(index, npcType);
	}

	/**
	 * A listener function that is referenced every sequence of the non-playable character update.
	 */
	@Override
	public void onSequence() {
		super.onSequence();


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
