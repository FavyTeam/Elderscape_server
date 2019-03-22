package game.content.combat;

/**
 * Weapon speed.
 *
 * @author MGT Madness, created on 29-03-2015.
 */
public class WeaponSpeed {

	/**
	 * @param list The String array to search in.
	 * @param match The string to find in the given String array.
	 * @return True, if the given string is found in the String array.
	 */
	public static boolean matching(String[] list, String match) {
		for (int value = 0; value < list.length; value++) {
			if (match.contains(list[value])) {
				return true;
			}
		}
		return false;
	}

	//@formatter:off
	public final static String[] SPEED_2_TICKS = {"dart", "knife"};

	public final static String[] SPEED_3_TICKS = {"toktz-xil-ul", "toxic blowpipe", "event rpg"};

	public final static String[] SPEED_4_TICKS = {"hasta", "dragon thrownaxe", "unarmed", "saradomin sword", "blessed sword", "karil", "short", "sword", "dagger", "scimitar",
			"rapier", "claw", "toktz-xil-ak", "toktz-xil-ek", "whip", "abyssal tentacle", "staff of the dead"
	};

	public final static String[] SPEED_5_TICKS = {"torags hammers", "guthans warspear", "veracs flail", "staff", "long", "longsword", "mace", "pickaxe", "thrownaxe", "axe",
			"spear", "tzhaar-ket-em"
	};

	public final static String[] SPEED_6_TICKS = {"ahrims staff", "godsword", "battleaxe", "warhammer", "toktz-mej-tal", "chaotic maul", "javelin", "elder maul",};

	public final static String[] SPEED_7_TICKS = {"hand cannon", "anchor", "greataxe", "halberd", "granite maul", "2h", "tzhaar-ket-om", "hill giant club"};

	public final static String[] SPEED_8_TICKS = {"dark bow",};

	//@formatter:on
}
