package game.content.combat;

/**
 * Written by Owain, 06/07/18, replaces old method for handling spells in CombatConstants.
 */

public enum Spells {
	WIND_STRIKE(1152, 1, 711, 90, 91, 92, 2, 5, new int[][]
			{
		{556, 1, 558, 1}}, 1, 220, 221),
	WATER_STRIKE(1154, 5, 711, 93, 94, 95, 4, 7, new int[][]
	{
		{555, 1, 556, 1, 558, 1}}, 1, 211, 212),
	EARTH_STRIKE(1156, 9, 711, 96, 97, 98, 6, 9, new int[][]
	{
		{557, 2, 556, 1, 558, 1}}, 1, 132, 133),
	FIRE_STRIKE(1158, 13, 711, 99, 100, 101, 8, 11, new int[][]
	{
		{554, 3, 556, 2, 558, 1}}, 1, 160, 161),
	WIND_BOLT(1160, 17, 711, 117, 118, 119, 9, 13, new int[][]
	{
		{556, 2, 562, 1}}, 1, 218, 219),
	WATER_BOLT(1163, 23, 711, 120, 121, 122, 10, 16, new int[][]
	{
		{556, 2, 555, 2, 562, 1}}, 1, 209, 210),
	EARTH_BOLT(1166, 29, 711, 123, 124, 125, 11, 20, new int[][]
	{
		{556, 2, 557, 3, 562, 1}}, 1, 130, 131),
	FIRE_BOLT(1169, 35, 711, 126, 127, 128, 12, 22, new int[][]
	{
		{556, 3, 554, 4, 562, 1}}, 1, 157, 158),
	WIND_BLAST(1172, 41, 711, 132, 133, 134, 13, 25, new int[][]
	{
		{556, 3, 560, 1}}, 1, 216, 217),
	WATER_BLAST(1175, 47, 711, 135, 136, 137, 14, 28, new int[][]
	{
		{556, 3, 555, 3, 560, 1}}, 1, 207, 208),
	EARTH_BLAST(1177, 53, 711, 138, 139, 140, 15, 31, new int[][]
	{
		{556, 3, 557, 4, 560, 1}}, 1, 128, 129),
	FIRE_BLAST(1181, 59, 711, 129, 130, 131, 16, 35, new int[][]
	{
		{556, 4, 554, 5, 560, 1}}, 1, 155, 156),
	WIND_WAVE(1183, 62, 711, 158, 159, 160, 17, 36, new int[][]
	{
		{556, 5, 565, 1}}, 1, 222, 223),
	WATER_WAVE(1185, 65, 711, 161, 162, 163, 18, 37, new int[][]
	{
		{556, 5, 555, 7, 565, 1}}, 1, 213, 214),
	EARTH_WAVE(1188, 70, 711, 164, 165, 166, 19, 40, new int[][]
	{
		{556, 5, 557, 7, 565, 1}}, 1, 134, 135),
	FIRE_WAVE(1189, 75, 711, 155, 156, 157, 20, 42, new int[][]
	{
		{556, 5, 554, 7, 565, 1}}, 1, 162, 163),
	WIND_SURGE(11830, 81, 7855, 1455, 1456, 1457, 21, 44, new int[][]
	{
		{556, 7, 21880, 1}}, 1, 222, 223),
	WATER_SURGE(11850, 85, 7855, 1458, 1459, 1460, 22, 46, new int[][]
	{
		{556, 7, 555, 10, 21880, 1}}, 1, 213, 214),
	EARTH_SURGE(11880, 90, 7855, 1461, 1462, 1463, 23, 48, new int[][]
	{
		{556, 7, 557, 10, 21880, 1}}, 1, 134, 135),
	FIRE_SURGE(11890, 95, 7855, 1464, 1465, 1466, 24, 50, new int[][]
	{
		{556, 7, 554, 10, 21880, 1}}, 1, 162, 163),
	CONFUSE(1153, 3, 716, 102, 103, 104, 0, 13, new int[][]
	{
		{555, 3, 557, 2, 559, 1}}, 0, 119, 121),
	WEAKEN(1157, 11, 716, 105, 106, 107, 0, 20, new int[][]
	{
		{555, 3, 557, 2, 559, 1}}, 0, 3011, 3010),
	CURSE(1161, 19, 716, 108, 109, 110, 0, 29, new int[][]
	{
		{555, 2, 557, 3, 559, 1}}, 0, 127, 125),
	VULNERABILITY(1542, 66, 729, 167, 168, 169, 0, 76, new int[][]
	{
		{557, 5, 555, 5, 566, 1}}, 0, 3008, 3009),
	ENFEEBLE(1543, 73, 729, 170, 171, 172, 0, 83, new int[][]
	{
		{557, 8, 555, 8, 566, 1}}, 0, 148, 150),
	STUN(1562, 80, 729, 173, 174, 107, 0, 90, new int[][]
	{
		{557, 12, 555, 12, 556, 1}}, 0, 3004, 3005),
	BIND(1572, 20, 711, 177, 178, 181, 0, 30, new int[][]
	{
		{557, 3, 555, 3, 561, 2}}, 1, 99, 101),
	SNARE(1582, 50, 711, 177, 178, 180, 2, 60, new int[][]
	{
		{557, 4, 555, 4, 561, 3}}, 1, 3002, 3003),
	ENTANGLE(1592, 79, 711, 177, 178, 179, 4, 90, new int[][]
	{
		{557, 5, 555, 5, 561, 4}}, 1, 151, 153),
	CRUMBLE_UNDEAD(1171, 39, 724, 145, 146, 147, 15, 25, new int[][]
	{
		{556, 2, 557, 2, 562, 1}}, 1, 122, 124),
	IBAN_BLAST(1539, 50, 708, 87, 88, 89, 25, 42, new int[][]
	{
		{554, 5, 560, 1}}, 1, -1, -1),
	MAGIC_DART(12037, 50, 1576, 327, 328, 329, 19, 30, new int[][]
	{
		{560, 1, 558, 4}}, 1, -1, -1),
	SARADOMIN_STRIKE(1190, 60, 811, 0, 0, 76, 20, 60, new int[][]
	{
		{554, 2, 565, 2, 556, 4}}, 1, -1, -1),
	CLAWS_OF_GUTHIX(1191, 60, 811, 0, 0, 77, 20, 60, new int[][]
	{
		{554, 1, 565, 2, 556, 4}}, 1, -1, -1),
	FLAMES_OF_ZAMORAK(1192, 60, 811, 0, 0, 78, 20, 60, new int[][]
	{
		{554, 4, 565, 2, 556, 1}}, 1, -1, -1),
	TELEBLOCK(12445, 85, 1819, 0, 1299, 345, 0, 80, new int[][]
	{
		{563, 1, 562, 1, 560, 1}}, 0, -1, -1),
	SMOKE_RUSH(12939, 50, 1978, 0, 384, 385, 13, 30, new int[][]
	{
		{560, 2, 562, 2, 554, 1, 556, 1}}, 1, 183, 185),
	SHADOW_RUSH(12987, 52, 1978, 0, 378, 379, 14, 31, new int[][]
	{
		{560, 2, 562, 2, 566, 1, 556, 1}}, 1, 178, 179),
	BLOOD_RUSH(12901, 56, 1978, 0, 0, 373, 15, 33, new int[][]
	{
		{560, 2, 562, 2, 565, 1}}, 1, 106, 110),
	ICE_RUSH(12861, 58, 1978, 0, 360, 361, 16, 34, new int[][]
	{
		{560, 2, 562, 2, 555, 2}}, 1, 171, 173),
	SMOKE_BURST(12963, 62, 1979, 0, 0, 389, 19, 36, new int[][]
	{
		{560, 2, 562, 4, 556, 2, 554, 2}}, 1, 183, 182),
	SHADOW_BURST(13011, 64, 1979, 0, 0, 382, 20, 37, new int[][]
	{
		{560, 2, 562, 4, 556, 2, 566, 2}}, 1, 178, 177),
	BLOOD_BURST(12919, 68, 1979, 0, 0, 376, 21, 39, new int[][]
	{
		{560, 2, 562, 4, 565, 2}}, 1, 106, 105),
	ICE_BURST(12881, 70, 1979, 0, 0, 363, 22, 40, new int[][]
	{
		{560, 2, 562, 4, 555, 4}}, 1, 171, 170),
	SMOKE_BLITZ(12951, 74, 1978, 0, 386, 387, 23, 42, new int[][]
	{
		{560, 2, 554, 2, 565, 2, 556, 2}}, 1, 183, 181),
	SHADOW_BLITZ(12999, 76, 1978, 0, 380, 381, 24, 43, new int[][]
	{
		{560, 2, 565, 2, 556, 2, 566, 2}}, 1, 178, 176),
	BLOOD_BLITZ(12911, 80, 1978, 0, 374, 375, 25, 45, new int[][]
	{
		{560, 2, 565, 4}}, 1, 106, 104),
	ICE_BLITZ(12871, 82, 1978, 366, 0, 367, 26, 46, new int[][]
	{
		{560, 2, 565, 2, 555, 3}}, 1, 171, 169),
	SMOKE_BARRAGE(12975, 86, 1979, 0, 0, 391, 27, 48, new int[][]
	{
		{560, 4, 565, 2, 556, 4, 554, 4}}, 1, 183, 180),
	SHADOW_BARRAGE(13023, 88, 1979, 0, 0, 383, 28, 49, new int[][]
	{
		{560, 4, 565, 2, 556, 4, 566, 3}}, 1, 178, 175),
	BLOOD_BARRAGE(12929, 92, 1979, 0, 0, 377, 29, 51, new int[][]
	{
		{560, 4, 565, 4, 566, 1}}, 1, 106, 102),
	ICE_BARRAGE(12891, 94, 1979, 0, 0, 369, 30, 52, new int[][]
	{
		{560, 4, 565, 2, 555, 6}}, 1, 171, 168),
	CHARGE(-1, 80, 811, 301, 0, 0, 0, 0, new int[][]
	{
		{554, 3, 565, 3, 556, 3}}, 0, 1651, -1),
	LOW_LEVEL_ALCHEMY(-1, 21, 712, 112, 0, 0, 0, 31, new int[][]
	{
		{554, 3, 561, 1}}, 0, 98, -1),
	HIGH_LEVEL_ALCHEMY(-1, 55, 713, 113, 0, 0, 0, 65, new int[][]
	{
		{554, 5, 561, 1}}, 0, 97, -1),
	TELEGRAB(-1, 33, 728, 142, 143, 144, 0, 35, new int[][]
	{
		{556, 1, 563, 1}}, 0, 3006, -1),
	TRIDENT_OF_THE_SWAMP(-1, 75, 1167, 665, 1040, 1042, 32, 35, new int[][]
	{
		{}}, 1, -1, -1),
	VENGEANCE(-1, 0, 0, 0, 0, 0, 0, 0, new int[][]
	{
		{557, 10, 9075, 4, 560, 2}}, 0, 2907, -1),
	MIASMIC_RUSH(32_600, 61, 10513, 1845, 1846, 1847, 18, 18, new int[][]
	{
		{557, 1, 566, 1, 562, 2}}, 1, -1, -1),

	MIASMIC_BURST(32_620, 73, 10516, 1848, 0, 1849, 24, 24, new int[][]
	{
		{557, 2, 566, 2, 562, 4}}, 1, -1, -1),

	MIASMIC_BLITZ(32_640, 85, 10518, 1850, 1852, 1851, 28, 28, new int[][]
	{
		{557, 3, 566, 3, 560, 2}}, 1, -1, -1),

	MIASMIC_BARRAGE(32_660, 97, 10518, 1853, 0, 1854, 32, 32, new int[][]
	{
		{557, 4, 566, 4, 560, 4}}, 1, -1, -1),
	POLYPORE_STAFF(32_680, 80, 15448, 2034, 2035, 2036, 32, 22, new int[][]
	{
		{}}, 1, -1, -1),
	AIR_SURGE_PRE_EOC(41239, 81, 10546, 457, 462, 2700, 21, 21, new int[][]
	{
		{556, 7, 565, 1, 560, 1}}, 1, -1, -1),

	WATER_SURGE_PRE_EOC(41242, 85, 10542, 2701, 2707, 2712, 22, 22, new int[][]
	{
		{555, 10, 556, 1, 565, 1, 560, 1}}, 1, -1, -1),

	EARTH_SURGE_PRE_EOC(41245, 90, 14209, 2717, 2722, 2727, 23, 23, new int[][]
	{
		{557, 10, 556, 1, 565, 1, 560, 1}}, 1, -1, -1),

	FIRE_SURGE_PRE_EOC(41248, 95, 2791, 2728, 2735, 2741, 24, 23, new int[][]
	{
		{554, 10, 556, 1, 565, 1, 560, 1}}, 1, -1, -1);


	private int spellId, level, animation, startGfx, projectile, endGfx, maxHit, experience, doesDamage, castingSound, impactSound;

	private int[][] runesRequired;

	private Spells(int spellId, int level, int animation, int startGfx, int projectile, int endGfx, int maxHit, int experience, int[][] runesRequired, int doesDamage, int castingSound, int impactSound) {
		this.spellId = spellId;
		this.level = level;
		this.animation = animation;
		this.startGfx = startGfx;
		this.projectile = projectile;
		this.endGfx = endGfx;
		this.maxHit = maxHit;
		this.experience = experience;
		this.runesRequired = runesRequired;
		this.doesDamage = doesDamage;
		this.castingSound = castingSound;
		this.impactSound = impactSound;
	}

	public int getId() {
		return spellId;
	}

	public int getLevel() {
		return level;
	}

	public int getAnimation() {
		return animation;
	}

	public int getStartingGfx() {
		return startGfx;
	}

	public int getProjectile() {
		return projectile;
	}

	public int getEndingGfx() {
		return endGfx;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public int getExperience() {
		return experience;
	}

	public int[][] getRunesRequired() {
		return runesRequired;
	}

	public int doesDamage() {
		return doesDamage;
	}

	public int getCastingSound() {
		return castingSound;
	}

	public int getImpactSound() {
		return impactSound;
	}
}
