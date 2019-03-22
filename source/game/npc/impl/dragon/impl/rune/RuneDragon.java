package game.npc.impl.dragon.impl.rune;

import core.GameType;
import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.CustomNpcComponent;
import game.npc.Npc;
import game.type.GameTypeIdentity;

/**
 * Created by Jason MK on 2018-06-28 at 3:04 PM
 */
@CustomNpcComponent(identities = @GameTypeIdentity(identity = 8091, type = GameType.OSRS))
public class RuneDragon extends Npc {

    private final EntityCombatStrategy combatStrategy = new RuneDragonCombatStrategy();

    public RuneDragon(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public Npc copy(int index) {
        return new RuneDragon(index, npcType);
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
