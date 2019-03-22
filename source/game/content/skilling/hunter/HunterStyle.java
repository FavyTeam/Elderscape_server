package game.content.skilling.hunter;

import game.content.skilling.hunter.technique.HunterTechnique;
import game.content.skilling.hunter.technique.impl.bird_snaring.BirdSnaringTechnique;
import game.content.skilling.hunter.technique.impl.bird_snaring.BoxTrappingTechnique;
import game.content.skilling.hunter.technique.impl.bird_snaring.ImplingCatchingTechnique;

/**
 * Created by Jason MacKeigan on 2018-01-22 at 9:41 AM
 * <p>
 * The different styles of hunting creatures, with given levels and equipment next to them.
 */
public enum HunterStyle {
//    TRACKING(HunterEquipment.NOOSE_WAND),

	BIRD_SNARING(HunterEquipment.BIRD_SNARE, new BirdSnaringTechnique()),
//
    BUTTERFLY_NETTING(HunterEquipment.BUTTERFLY_NET, new ImplingCatchingTechnique()),
//
    BOX_TRAPPING(HunterEquipment.BOX_TRAP, new BoxTrappingTechnique()),
//
//    RABBIT_SNARING(HunterEquipment.RABBIT_SNARE),
//
//    PITFALL_TRAPPING(HunterEquipment.TEASING_STICK),
//
//    MAGIC_IMP_HUNTING(HunterEquipment.MAGIC_BOX)
	;

	private final HunterEquipment equipment;

	private final HunterTechnique technique;

	HunterStyle(HunterEquipment equipment, HunterTechnique technique) {
		this.equipment = equipment;
		this.technique = technique;
	}

	public HunterEquipment getEquipment() {
		return equipment;
	}

	public HunterTechnique getTechnique() {
		return technique;
	}
}
