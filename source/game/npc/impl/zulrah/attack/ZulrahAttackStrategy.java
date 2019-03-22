package game.npc.impl.zulrah.attack;

import game.entity.combat_strategy.EntityCombatStrategy;
import game.npc.Npc;

/**
 * Created by Jason MacKeigan on 2018-04-02 at 2:22 PM
 */
public interface ZulrahAttackStrategy extends EntityCombatStrategy {

	boolean isComplete();

	default void onStart(Npc zulrah) {

	}

	default void onEnd(Npc zulrah) {

	}

}
