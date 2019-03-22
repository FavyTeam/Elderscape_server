package game.npc.impl.demonic_gorilla;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-01-16 at 2:51 PM
 */
//@CustomNpcComponent(id = 7144, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 7144))

public class DemonicGorilla extends Npc {

	private final NpcCombatStrategy strategy = new DemonicGorillaCombatStrategy();

	public DemonicGorilla(int npcId, int npcType) {
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
		return new DemonicGorilla(index, npcType);
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
