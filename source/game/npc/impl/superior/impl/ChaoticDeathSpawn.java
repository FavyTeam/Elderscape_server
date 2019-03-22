package game.npc.impl.superior.impl;

import game.entity.combat_strategy.EntityCombatStrategy;
import game.entity.combat_strategy.impl.NpcCombatStrategy;
import game.npc.Npc;

/**
 * Created by Jason MK on 2018-07-12 at 5:42 PM
 */
public class ChaoticDeathSpawn extends Npc {

    private final EntityCombatStrategy strategy = new ChaoticDeathSpawnCombatStrategy();

    public ChaoticDeathSpawn(int npcId, int npcType) {
        super(npcId, npcType);
    }

    @Override
    public Npc copy(int index) {
        return new ChaoticDeathSpawn(index, npcType);
    }

    @Override
    public EntityCombatStrategy getCombatStrategyOrNull() {
        return super.getCombatStrategyOrNull();
    }

    private static final class ChaoticDeathSpawnCombatStrategy extends NpcCombatStrategy {



    }
}
