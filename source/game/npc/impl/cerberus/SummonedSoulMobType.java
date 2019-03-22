package game.npc.impl.cerberus;

import core.ServerConstants;

public enum SummonedSoulMobType {
	RANGE(5867, ServerConstants.PROTECT_FROM_RANGED),
	MAGIC(5868, ServerConstants.PROTECT_FROM_MAGIC),
	MELEE(5869, ServerConstants.PROTECT_FROM_MELEE);


	private final int mobId;

	private final int protectionPrayer;

	private SummonedSoulMobType(int mobId, int protectionPrayer) {
		this.mobId = mobId;
		this.protectionPrayer = protectionPrayer;
	}

	public final int getMobId() {
		return mobId;
	}

	public final int getProtectionPrayer() {
		return protectionPrayer;
	}
}
