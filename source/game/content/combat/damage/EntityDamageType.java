package game.content.combat.damage;

import core.ServerConstants;

/**
 * Created by Jason MacKeigan on 2018-05-02 at 10:49 AM
 */
public enum EntityDamageType {
	MELEE(ServerConstants.MELEE_ICON, ServerConstants.NORMAL_HITSPLAT_COLOUR),
	RANGED(ServerConstants.RANGED_ICON, ServerConstants.NORMAL_HITSPLAT_COLOUR),
	MAGIC(ServerConstants.MAGIC_ICON, ServerConstants.NORMAL_HITSPLAT_COLOUR),
	POISON(ServerConstants.NO_ICON, ServerConstants.POISON_HITSPLAT_COLOUR),
	VENOM(ServerConstants.NO_ICON, ServerConstants.VENOM_HITSPLAT_COLOUR),
	DRAGON_FIRE(ServerConstants.NO_ICON, ServerConstants.DARK_RED_HITSPLAT_COLOUR);

	private final int preEocHitsplatIcon;
	
	private final int preEocHitsplatColour;

	EntityDamageType(int preEocHitsplatIcon, int preEocHitsplatColour) {
		this.preEocHitsplatIcon = preEocHitsplatIcon;
		this.preEocHitsplatColour =preEocHitsplatColour;
	}

	public int getPreEocHitsplatIcon() {
		return preEocHitsplatIcon;
	}
	
	public int getPreEocHitsplatColour()
	{
		return preEocHitsplatColour;
	}
}
