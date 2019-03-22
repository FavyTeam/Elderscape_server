package game.content.combat;

import core.ServerConstants;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Combat constants.
 *
 * @author MGT Madness, created on 26-03-2015.
 */
public class CombatConstants {

	/**
	 * The maxium distance to use magic combat spell from.
	 */
	public final static int MAGIC_FOLLOW_DISTANCE = 10;

	public final static int SHORT_BOW_DISTANCE = 7;

	public final static int KNIFE_DISTANCE = 4;

	public final static int DART_DISTANCE = 3;

	public final static int DARK_BOW_DISTANCE = 10;

	public final static int BLOWPIPE_DISTANCE = 5;

	public final static int CROSSBOW_DISTANCE = 7;

	public final static int ARMADYL_CROSSBOW_DISTANCE = 8;

	public final static int BALLISTA_DISTANCE = 9;

	public final static int LONG_BOW_DISTANCE = 9;

	public final static int CHINCHOMPA_DISTANCE = 9;

	/**
	 * The maximum distance to use a halberd.
	 */
	public final static int HALBERD_DISTANCE = 2;

	/**
	 * The distance of where you cannot see a player from.
	 */
	public final static int OUT_OF_VIEW_DISTANCE = 20;

	/**
	 * How long does the other player stay immune to the spell.
	 */
	public final static int[] REDUCE_SPELL_TIME =
			{250000, 250000, 250000, 500000, 500000, 500000};

	/**
	 * Reduce spell identities.
	 */
	public final static int[] REDUCE_SPELLS =
			{1153, 1157, 1161, 1542, 1543, 1562};

	/**
	 * Tele block spell id.
	 */
	public final static int TELE_BLOCK = 12445;

	public static int getRangedWeaponDistance(Player player) {
		int longRanged = 2;

		String weaponName = "";
		if (player.getWieldedWeapon() > 0) {
			weaponName = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();
		}

		int distance = 8;

		if (weaponName.contains("short")) {
			distance = SHORT_BOW_DISTANCE;
		} else if (weaponName.contains("knife")) {
			distance = KNIFE_DISTANCE;
		} else if (weaponName.contains("dragon thrownaxe")) {
			distance = KNIFE_DISTANCE;
		} else if (weaponName.contains("dart")) {
			distance = DART_DISTANCE;
		} else if (weaponName.contains("chinchompa")) {
			distance = CHINCHOMPA_DISTANCE;
		} else if (weaponName.contains("dark")) {
			distance = DARK_BOW_DISTANCE;
			longRanged = 0;
		} else if (weaponName.contains("blowpipe")) {
			distance = BLOWPIPE_DISTANCE;
		} else if (weaponName.contains("armadyl cross")) {
			distance = ARMADYL_CROSSBOW_DISTANCE;
		} else if (weaponName.contains("cross")) {
			distance = CROSSBOW_DISTANCE;
		} else if (weaponName.contains("ballista")) {
			distance = BALLISTA_DISTANCE;
			longRanged = 0;
		} else if (weaponName.contains("long")) {
			distance = LONG_BOW_DISTANCE;
			longRanged = 1;
		}
		else if (weaponName.contains("crystal bow")) {
			distance = 10;
			longRanged = 0;
		}
		else if (weaponName.contains("craw's bow")) {
			distance = LONG_BOW_DISTANCE;
			longRanged = 1;
		}


		if (!player.getCombatStyle(ServerConstants.LONG_RANGED)) {
			longRanged = 0;
		}
		return distance + longRanged;
	}

	/**
	 * @param itemId
	 * @return True if the itemId given is a Dark bow.
	 */
	public static boolean isDarkBow(int itemId) {
		int[] darkBows =
				{11235, 12765, 12766, 12767, 12768};
		for (int i = 0; i < darkBows.length; i++) {
			if (itemId == darkBows[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean isChinchompa(int itemId) {
		int[] chins =
				{10033, 10034, 11959};
		for (int i = 0; i < chins.length; i++) {
			if (itemId == chins[i]) {
				return true;
			}
		}
		return false;
	}

	public final static int[][] MAGIC_SPELLS =
			{
					// example {magicId, level req, animation, startGFX, projectile Id,
					// endGFX, maxhit, exp gained, rune 1, rune 1 amount, rune 2, rune 2
					// amount, rune 3, rune 3 amount, rune 4, rune 4 amount, doesDamage = 1}
					// Modern Spells
					{1152, 1, 711, 90, 91, 92, 3, 5, 556, 1, 558, 1, 0, 0, 0, 0, 1
							// Wind strike.
					},
					{1154, 5, 711, 93, 94, 95, 4, 7, 555, 1, 556, 1, 558, 1, 0, 0, 1
							// Water strike.
					},
					{1156, 9, 711, 96, 97, 98, 6, 9, 557, 2, 556, 1, 558, 1, 0, 0, 1
							// Earth strike.
					},
					{1158, 13, 711, 99, 100, 101, 8, 11, 554, 3, 556, 2, 558, 1, 0, 0, 1
							// Fire strike.
					},
					{1160, 17, 711, 117, 118, 119, 9, 13, 556, 2, 562, 1, 0, 0, 0, 0, 1
							// Wind bolt.
					},
					{1163, 23, 711, 120, 121, 122, 10, 16, 556, 2, 555, 2, 562, 1, 0, 0, 1
							// Water bolt.
					},
					{1166, 29, 711, 123, 124, 125, 11, 20, 556, 2, 557, 3, 562, 1, 0, 0, 1
							// Earth bolt.
					},
					{1169, 35, 711, 126, 127, 128, 12, 22, 556, 3, 554, 4, 562, 1, 0, 0, 1
							// Fire bolt.
					},
					{1172, 41, 711, 132, 133, 134, 13, 25, 556, 3, 560, 1, 0, 0, 0, 0, 1
							// Wind blast.
					},
					{1175, 47, 711, 135, 136, 137, 14, 28, 556, 3, 555, 3, 560, 1, 0, 0, 1
							// Water blast.
					},
					{1177, 53, 711, 138, 139, 140, 15, 31, 556, 3, 557, 4, 560, 1, 0, 0, 1
							// Earth blast.
					},
					{1181, 59, 711, 129, 130, 131, 16, 35, 556, 4, 554, 5, 560, 1, 0, 0, 1
							// Fire blast.
					},
					{1183, 62, 711, 158, 159, 160, 17, 36, 556, 5, 565, 1, 0, 0, 0, 0, 1
							// Wind wave.
					},
					{1185, 65, 711, 161, 162, 163, 18, 37, 556, 5, 555, 7, 565, 1, 0, 0, 1
							// Water wave.
					},
					{1188, 70, 711, 164, 165, 166, 19, 40, 556, 5, 557, 7, 565, 1, 0, 0, 1
							// Earth wave.
					},
					{1189, 75, 711, 155, 156, 157, 20, 42, 556, 5, 554, 7, 565, 1, 0, 0, 1
							// Fire wave.
					},
					/*
					{11830, 81, 7855, 1455, 1456, 1457, 21, 44, 556, 7, 21880, 1, 0, 0, 0, 0, 1
					// Wind surge.
					},
					{11850, 85, 7855, 1458, 1459, 1460, 22, 46, 556, 7, 555, 10, 21880, 1, 0, 0, 1
					// Water surge.
					},
					{11880, 90, 7855, 1461, 1462, 1463, 23, 48, 556, 7, 557, 10, 21880, 1, 0, 0, 1
					// Earth surge.
					},
					{11890, 95, 7855, 1464, 1465, 1466, 24, 50, 556, 7, 554, 10, 21880, 1, 0, 0, 1
					// Fire surge.
					},
					*/
					{1153, 3, 716, 102, 103, 104, 0, 13, 555, 3, 557, 2, 559, 1, 0, 0, 0
							// Confuse.
					},
					{1157, 11, 716, 105, 106, 107, 0, 20, 555, 3, 557, 2, 559, 1, 0, 0, 0
							// Weaken.
					},
					{1161, 19, 716, 108, 109, 110, 0, 29, 555, 2, 557, 3, 559, 1, 0, 0, 0
							// Curse.
					},
					{1542, 66, 729, 167, 168, 169, 0, 76, 557, 5, 555, 5, 566, 1, 0, 0, 0
							// Vulnerability.
					},
					{1543, 73, 729, 170, 171, 172, 0, 83, 557, 8, 555, 8, 566, 1, 0, 0, 0
							// Enfeeble.
					},
					{1562, 80, 729, 173, 174, 107, 0, 90, 557, 12, 555, 12, 556, 1, 0, 0, 0
							// Stun.
					},
					{1572, 20, 711, 177, 178, 181, 0, 30, 557, 3, 555, 3, 561, 2, 0, 0, 1
							// Bind.
					},
					{1582, 50, 711, 177, 178, 180, 2, 60, 557, 4, 555, 4, 561, 3, 0, 0, 1
							// Snare.
					},
					{1592, 79, 711, 177, 178, 179, 4, 90, 557, 5, 555, 5, 561, 4, 0, 0, 1
							// Entangle.
					},
					{1171, 39, 724, 145, 146, 147, 15, 25, 556, 2, 557, 2, 562, 1, 0, 0, 1
							// Crumble undead.
					},
					{1539, 50, 708, 87, 88, 89, 25, 42, 554, 5, 560, 1, 0, 0, 0, 0, 1
							// Iban blast.
					},
					{12037, 50, 1576, 327, 328, 329, 19, 30, 560, 1, 558, 4, 0, 0, 0, 0, 1
							// Bagic dart.
					},
					{1190, 60, 811, 0, 0, 76, 20, 60, 554, 2, 565, 2, 556, 4, 0, 0, 1
							// Saradomin strike.
					},
					{1191, 60, 811, 0, 0, 77, 20, 60, 554, 1, 565, 2, 556, 4, 0, 0, 1
							// Cause of Guthix.
					},
					{1192, 60, 811, 0, 0, 78, 20, 60, 554, 4, 565, 2, 556, 1, 0, 0, 1
							// Flame of Zamorak.
					},
					{12445, 85, 1819, 0, 1299, 345, 0, 80, 563, 1, 562, 1, 560, 1, 0, 0, 0
							// Teleblock.
					},
					{12939, 50, 1978, 0, 384, 385, 13, 30, 560, 2, 562, 2, 554, 1, 556, 1, 1
							// Smoke rush.
					},
					{12987, 52, 1978, 0, 378, 379, 14, 31, 560, 2, 562, 2, 566, 1, 556, 1, 1
							// Shadow rush.
					},
					{12901, 56, 1978, 0, 0, 373, 15, 33, 560, 2, 562, 2, 565, 1, 0, 0, 1
							// Blood rush.
					},
					{12861, 58, 1978, 0, 360, 361, 16, 34, 560, 2, 562, 2, 555, 2, 0, 0, 1
							// Ice rush.
					},
					{12963, 62, 1979, 0, 0, 389, 19, 36, 560, 2, 562, 4, 556, 2, 554, 2, 1
							// Smoke burst.
					},
					{13011, 64, 1979, 0, 0, 382, 20, 37, 560, 2, 562, 4, 556, 2, 566, 2, 1
							// Shadow burst.
					},
					{12919, 68, 1979, 0, 0, 376, 21, 39, 560, 2, 562, 4, 565, 2, 0, 0, 1
							// Blood burst.
					},
					{12881, 70, 1979, 0, 0, 363, 22, 40, 560, 2, 562, 4, 555, 4, 0, 0, 1
							// Ice burst.
					},
					{12951, 74, 1978, 0, 386, 387, 23, 42, 560, 2, 554, 2, 565, 2, 556, 2, 1
							// Smoke blitz.
					},
					{12999, 76, 1978, 0, 380, 381, 24, 43, 560, 2, 565, 2, 556, 2, 566, 2, 1
							// Shadow blitz.
					},
					{12911, 80, 1978, 0, 374, 375, 25, 45, 560, 2, 565, 4, 0, 0, 0, 0, 1
							// Blood blitz.
					},
					{12871, 82, 1978, 366, 0, 367, 26, 46, 560, 2, 565, 2, 555, 3, 0, 0, 1
							// Ice blitz.
					},
					{12975, 86, 1979, 0, 0, 391, 27, 48, 560, 4, 565, 2, 556, 4, 554, 4, 1
							// Smoke barrage.
					},
					{13023, 88, 1979, 0, 0, 383, 28, 49, 560, 4, 565, 2, 556, 4, 566, 3, 1
							// Shadow barrage.
					},
					{12929, 92, 1979, 0, 0, 377, 29, 51, 560, 4, 565, 4, 566, 1, 0, 0, 1
							// Blood barrage.
					},
					{12891, 94, 1979, 0, 0, 369, 30, 52, 560, 4, 565, 2, 555, 6, 0, 0, 1
							// Ice barrage.
					},
					{-1, 80, 811, 301, 0, 0, 0, 0, 554, 3, 565, 3, 556, 3, 0, 0, 0
							// Charge.
					},
					{-1, 21, 712, 112, 0, 0, 0, 31, 554, 3, 561, 1, 0, 0, 0, 0, 0
							// Low alch.
					},
					{-1, 55, 713, 113, 0, 0, 0, 65, 554, 5, 561, 1, 0, 0, 0, 0, 0
							// High alch.
					},
					// Telegrab.
					{-1, 33, 728, 142, 143, 144, 0, 35, 556, 1, 563, 1, 0, 0, 0, 0, 0},
					// Trident of the swamp
					{-1, 75, 1167, 665, 1040, 1042, 32, 35, 0, 0, 0, 0, 0, 0, 0, 0, 1},
		// Sanguinesti staff, heal gfx is 1542
		{-1, 75, 1167, 1540, 1539, 1541, 34, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
					// Vengeance.
					{-1, 0, 0, 0, 0, 0, 0, 0, 557, 10, 9075, 4, 560, 2, 0, 0, 0},


					{32600, 61, 10513, 1845, 1846, 1847, 18, 18, 557, 1, 566, 1, 562, 2, 0, 0, 1
					// Miasmic rush.
					},

					{32_620, 73, 10516, 1848, 0, 1849, 24, 24, 557, 2, 566, 2, 562, 4, 0, 0, 1
					// Miasmic burst.
					},

					{32_640, 85, 10518, 1850, 1852, 1851, 28, 28, 557, 3, 566, 3, 560, 2, 0, 0, 1
					// Miasmic blitz.
					},

					{32_660, 97, 10518, 1853, 0, 1854, 32, 32, 557, 4, 566, 4, 560, 4, 0, 0, 1
					// Miasmic barrage.
					},

					{32_680, 80, 15448, 2034, 2035, 2036, 32, 22, 0, 0, 0, 0, 0, 0, 0, 0, 1
					// polypore staff
					},

			/*
			// example {magicId, level req, animation, startGFX, projectile Id,
			// endGFX, maxhit, exp gained, rune 1, rune 1 amount, rune 2, rune 2
			// amount, rune 3, rune 3 amount, rune 4, rune 4 amount, doesDamage = 1}
			// Modern Spells
			
			{41239, 81, 10546, 457, 462, 2700, 21, 21, 556, 7, 565, 1, 560, 1, 0, 0, 1
			// air surge
			},
			
			{41242, 85, 10542, 2701, 2707, 2712, 22, 22, 555, 10, 556, 1, 565, 1, 560, 1, 1
			// water surge
			},
			
			{41245, 90, 14209, 2717, 2722, 2727, 23, 23, 557, 10, 556, 1, 565, 1, 560, 1, 1
			// earth surge
			},
			
			{41248, 95, 2791, 2728, 2735, 2741, 24, 23, 554, 10, 556, 1, 565, 1, 560, 1, 1
			// fire surge
			},
			*/
			};
	//{ -1, 75, 719, 1251, 1252, 1253, 29, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, // Trident of the seas

	public final static int TELEGRAB_INDEX = 51;

	public final static int HIGH_ALCH_INDEX = 50;

	public final static int LOW_ALCH_INDEX = 49;

	public static int getAttackDistance(Player player) {
		if (player.isUsingMediumRangeRangedWeapon() || player.isWieldingRangedWeaponWithNoArrowSlotRequirement()) {
			return CombatConstants.getRangedWeaponDistance(player);
		}
		if (player.hasLastCastedMagic()) {
			return CombatConstants.MAGIC_FOLLOW_DISTANCE;
		}
		return 1;
	}

	public static int getShadowSpellAttackLevelReductionPercentage(int spellId) {
		switch (spellId) {
			case 12987: // Shadow rush
				return 10;
			case 13011: // Shadow burst
				return 10;
			case 12999: // Shadaow blitz
				return 15;
			case 13023: // Shadow barrage
				return 15;
		}
		return 0;
	}
}
