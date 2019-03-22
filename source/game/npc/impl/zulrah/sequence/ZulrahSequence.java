package game.npc.impl.zulrah.sequence;

import game.npc.impl.zulrah.ZulrahLocation;
import game.npc.impl.zulrah.ZulrahTransformation;
import game.npc.impl.zulrah.attack.ZulrahAttackStrategy;

import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-02 at 1:55 PM
 */
public interface ZulrahSequence {

	ZulrahTransformation getTransformation();

	ZulrahLocation getLocation();

	List<ZulrahAttackStrategy> getAttacks();

}
