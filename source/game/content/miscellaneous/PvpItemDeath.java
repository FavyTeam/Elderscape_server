package game.content.miscellaneous;


/**
 * Handles PvP items turning into blood money on death
 *
 * @author Owain, created on 25-11-2017.
 */
public class PvpItemDeath {

	/**
	 * PvP item pieces are lost on death and drop blood money for killer (3/4 of the blood money value).
	 */
	public static enum PvpItems {
		ZURIELS_TOP(13858, 22653),
		ZURIELS_TOP_DEG(13860, 13860),
		ZURIELS_BOTTOM(13861, 22656),
		ZURIELS_BOTTOM_DEG(13863, 13863),
		ZURIELS_HOOD(13864, 22650),
		ZURIELS_HOOD_DEG(13866, 13866),
		MORRIGANS_BODY(13870, 22641),
		MORRIGANS_BODY_DEG(13872, 13872),
		MORRIGANS_CHAPS(13873, 22644),
		MORRIGANS_CHAPS_DEG(13875, 13875),
		MORRIGANS_COIF(13876, 22638),
		MORRIGANS_COIF_DEG(13878, 13878),
		STAT_BODY(13884, 22628),
		STAT_BODY_DEG(13886, 13886),
		VESTAS_BODY(13887, 22616),
		VESTAS_BODY_DEG(13889, 13889),
		STAT_LEGS(13890, 22631),
		STAT_LEGS_DEG(13892, 13892),
		VESTAS_SKIRT(13893, 22619),
		VESTAS_SKIRT_DEG(13895, 13895),
		STAT_HELM(13896, 22625),
		STAT_HELM_DEG(13898, 13898),
		//
		VESTA_SPEAR(13905, 22610),
		VESTA_SPEAR_DEG(13907, 13898),
		VESTA_LONGSWORD(13899, 22613),
		VESTA_LONGSWORD_DEG(13901, 13898),
		STAT_WARHAMMER(13902, 22622),
		STAT_WARHAMMER_DEG(13904, 13898),
		ZURIELS_STAFF(13867, 13898),
		ZURIELS_STAFF_DEG(13869, 13898),
		MORRIGANS_JAVELIN(13879, 22636),
		MORRIGANS_THROWINGAXE(13883, 22634);

		private int preEocId, osrsId;


		PvpItems(final int preEocId, final int osrsId) {
			this.preEocId = preEocId;
			this.osrsId = osrsId;
		}

		public int getPreEocId() {
			return preEocId;
		}

		public int getOsrsId() {
			return osrsId;
		}
	}

}
