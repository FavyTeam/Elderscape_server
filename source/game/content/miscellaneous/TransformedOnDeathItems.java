package game.content.miscellaneous;

/**
 * Imbued rings.
 *
 * @author MGT Madness, created on 19-04-2016.
 */
public class TransformedOnDeathItems {

	public static enum TransformedOnDeathData {
		BERSERKER(6737, 11773),
		ARCHER(6733, 11771),
		SEER(6731, 11770),
		WARRIOR(6735, 11772),
		TREASONOUS(12605, 12692),
		RING_OF_THE_GODS(12601, 13202),
		TYRANNICAL(12603, 12691),
		RING_OF_SUFFERING_I(19550, 20655),
		RING_OF_SUFFERING_RI(19550, 20657),
		SARA_BLESSED_SWORD(11838, 12808),
		GRANITE_MAUL_OR(4153, 12848),
		DRAGON_FULL_HELM_ORNAMENTED(11335, 12417),
		DRAGON_CHAIN_OR(3140, 12414),
		DRAGON_PLATELEGS_OR(4087, 12415),
		DRAGON_SQ_SHIELD_OR(1187, 12418),
		DRAGON_PLATESKIRT_OR(4585, 12416),
		DRAGON_SCIMITAR_OR(4587, 20000),
		ARMADYL_GODSWORD_OR(11802, 20368),
		BANDOS_GODSWORD_OR(11804, 20370),
		SARA_GODSWORD_OR(11806, 20372),
		ZAMORAK_GODSWORD_OR(11808, 20374),
		AMULET_OF_FURY_OR(6585, 12436),
		AMULET_OF_TORTURE_OR(19553, 20366),
		OCCULT_NECKLACE_OR(12002, 19720),
		DARK_BOW_BLUE(11235, 12765),
		DARK_BOW_GREEN(11235, 12766),
		DARK_BOW_YELLOW(11235, 12767),
		DARK_BOW_WHITE(11235, 12768),
		ODIUM_WARD_OR(11926, 12807),
		MALEDICTION_WARD_OR(11924, 12806);

		private int normalId;

		private int specialId;


		private TransformedOnDeathData(int normalRingId, int imbuedRingId) {
			this.normalId = normalRingId;
			this.specialId = imbuedRingId;
		}

		public int getNormalId() {
			return normalId;
		}

		public int getSpecialId() {
			return specialId;
		}


	}
}
