package game.npc.impl.zulrah.attack.impl;

import core.ServerConstants;

/**
 * Created by Jason MacKeigan on 2018-04-17 at 2:26 PM
 */
public class LongrangeRangeAttackStrategy extends LongrangeAttackStrategy {

	@Override
	int getMaximumNumberOfAttacks() {
		return 5;
	}

	@Override
	int getProjectile() {
		return 1044;
	}

	@Override
	int getAttackType() {
		return ServerConstants.RANGED_ICON;
	}
}
