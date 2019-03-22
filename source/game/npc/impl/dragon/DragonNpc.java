package game.npc.impl.dragon;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MacKeigan on 2018-06-18 at 2:35 PM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(type=GameType.OSRS, identity = { 266 }))
public class DragonNpc extends Npc {

	private final EntityCombatStrategy combatStrategy = new DragonCombatStrategy();

	public DragonNpc(int npcId, int npcType) {
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
		return new DragonNpc(index, npcType);
	}

	@Override
	public EntityCombatStrategy getCombatStrategyOrNull() {
		return combatStrategy;
	}

}
