package game.npc.impl.crazy_archaeologist;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-06-06 at 1:14 PM
 */
//@CustomNpcComponent(id = 7806, type = GameType.OSRS)
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = 7806))
public class DerangedArcheologist extends Archaeologist {

	private final EntityCombatStrategy combatStrategy = new DerangedArchaeologistCombatStrategy();

	public DerangedArcheologist(int index, int id) {
		super(index, id);
	}

	/**
	 * Creates a new instance of this npc with the given index.
	 *
	 * @param index the new index of this npc.
	 * @return the new instance.
	 */
	@Override
	public DerangedArcheologist copy(int index) {
		return new DerangedArcheologist(index, npcType);
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
