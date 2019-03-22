package game.npc.impl.zulrah.attack.impl;

import core.ServerConstants;

/**
 * Created by Jason MacKeigan on 2018-04-17 at 2:25 PM
 */
public class LongrangeMagicAttackStrategy extends LongrangeAttackStrategy {

	@Override
	int getMaximumNumberOfAttacks() {
		return 5;
	}

	@Override
	int getProjectile() {
		return 1046;
	}

	@Override
	int getAttackType() {
		return ServerConstants.MAGIC_ICON;
	}
}
