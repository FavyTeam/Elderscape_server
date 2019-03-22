package game.player;

import core.GameType;
import game.position.Position;
import game.content.minigame.Minigame;
import game.content.minigame.MinigameArea;
import game.content.minigame.MinigameAreaCombatSafety;
import game.npc.Npc;

/**
 * Area constants.
 *
 * @author MGT Madness, created on 02-03-2015.
 */
public class Area {

	/**
	 * Tournament instance for example.
	 */
	public static boolean inAreaWhereItemsGetDeletedUponExit(Player player) {

		// Tournament instance.
		if (player.getHeight() == 20) {
			return true;
		}
		return false;
	}

	public static boolean inMageArena(int x, int y, int height) {
		if (Area.isWithInArea(x, y, height, 3093, 3118, 3927, 3938, 0)) {
			return true;
		}
		if (Area.isWithInArea(x, y, height, 3092, 3118, 3920, 3929, 0)) {
			return true;
		}
		if (Area.isWithInArea(x, y, height, 3093, 3118, 3938, 3945, 0)) {
			return true;
		}
		if (Area.isWithInArea(x, y, height, 3097, 3111, 3945, 3946, 0)) {
			return true;
		}
		return false;
	}

	public static boolean inCorporealBeastLair(int x, int y, int height) {
		if (Area.isWithInArea(x, y, height, 2962, 2999, 4367, 4399, 2)) {
			return true;
		}
		return false;
	}

	public static boolean inAHuntingArea(int x, int y, int height) {
		if (Area.isWithInArea(x, y, height, 3127, 3169, 3755, 3840, 0)) { //Black chins
			return true;
		}
		if (Area.isWithInArea(x, y, height, 2309, 2356, 3554, 3611, 0)) { //Area with imps / birds
			return true;
		}
		return false;
	}


	private static boolean isWithInArea(int currentX, int currentY, int currentHeight, int X1, int X2, int Y1, int Y2, int requiredHeight) {
		return currentX >= X1 && currentX <= X2 && currentY >= Y1 && currentY <= Y2 && currentHeight == requiredHeight;
	}


	public static boolean inMarketArea(Player player) {
		if (Area.isWithInArea(player, 2977, 3025, 3367, 3392)) {
			return true;
		}
		return false;
	}

	public static boolean inJailArea(Player player) {
		if (Area.isWithInArea(player, 2689, 2699, 4010, 4030)) {
			return true;
		}
		return false;
	}

	public static boolean inStaffZone(Player player) {
		if (Area.isWithInArea(player, 2660, 2705, 3244, 3254)) {
			return true;
		}
		return false;
	}

	public static boolean inDiceZone(Player player) {
		if (Area.isWithInArea(player, 1664, 1727, 4232, 4287)) {
			return true;
		}
		return false;
	}

	public static boolean inVarrockBasement(Player player) {
		if (Area.isWithInArea(player, 3180, 3196, 9812, 9824)) {
			return true;
		}
		return false;
	}

	public static boolean inEdgeBank(Player player) {
		if (Area.isWithInArea(player, 3091, 3098, 3488, 3499)) {
			return true;
		}
		return false;
	}

	public static boolean inHweenArea(Player player) {
		if (Area.isWithInArea(player, 3779, 3790, 9217, 9230)) {
			return true;
		}
		return false;
	}

	public static boolean inHalloweenQuestGraveArea(Player player) {
		if (Area.isWithInArea(player, 3537, 3567, 3460, 3480)) {
			return true;
		}
		return false;
	}

	public static boolean inChaosAltarZone(Player player) {
		if (Area.isWithInArea(player, 2948, 2949, 3819, 3822)) {
			return true;
		}
		return false;
	}

	public static boolean inDonatorZone(int myX, int myY) {
		if (isWithInArea(myX, myY, 2170, 2237, 3219, 3264)) {
			return true;
		}
		if (isWithInArea(myX, myY, 2496, 2559, 2700, 2744)) {
			return true;
		}
		if (isWithInArea(myX, myY, 2180, 2220, 3230, 3270)) {
			return true;
		}
		return false;
	}

	public static boolean inDonatorBossInstance(Player player) {
		if (Area.isWithInArea(player, 1682, 1709, 4560, 4587)) {
			return true;
		}
		return false;
	}

	public static boolean isWithInArea(Player player, int X1, int X2, int Y1, int Y2) {
		return player.getX() >= X1 && player.getX() <= X2 && player.getY() >= Y1 && player.getY() <= Y2;
	}

	public static boolean inResourceWildernessOsrs(Player player) {
		return isWithInArea(player, 3174, 3196, 3924, 3944) && GameType.isOsrs();
	}

	public static boolean inZombieWaitingRoom(Player player) {
		return isWithInArea(player, 3651, 3668, 3512, 3528);
	}

	public static boolean isWithInArea(int currentX, int currentY, int X1, int X2, int Y1, int Y2) {
		return currentX >= X1 && currentX <= X2 && currentY >= Y1 && currentY <= Y2;
	}

	public static boolean inRfdArea(int x, int y) {
		if (isWithInArea(x, y, 1889, 1911, 5345, 5367)) {
			return true;
		}
		return false;
	}

	public static boolean isInBarrowsChestArea(Player player) {
		return player.getX() >= 3520 && player.getX() <= 3590 && player.getY() >= 9680 && player.getY() <= 9730 && player.getHeight() == 3;
	}


	public static boolean inWildernessAgilityCourse(Player player) {
		if (isWithInArea(player, 2988, 3007, 3931, 3968)) {
			return true;
		}

		return false;
	}

	/**
	 * @param player
	 * @return True, if the player is at the traditional Edgeville wilderness spots.
	 */
	public static boolean inEdgevilleWilderness(Player player) {
		if (isWithInArea(player, 3025, 3137, 3520, 3559) && inDangerousPvpArea(player)) {
			return true;
		}
		return false;
	}

	/**
	 * Edgeville pvp specific
	 *
	 * @param height
	 * @return
	 */
	public static boolean inEdgevilleBankPvpInstance(int x, int y, int height) {
		if (height != 4) {
			return false;
		}
		if (isWithInArea(x, y, 3091, 3098, 3488, 3499)) {
			return true;
		}
		if (isWithInArea(x, y, 3090, 3090, 3494, 3496)) {
			return true;
		}
		return false;
	}

	/**
	 * All pvp areas.
	 *
	 * @param player
	 * @return
	 */
	public static boolean inCityPvpArea(Player player) {
		if (player.getHeight() != 4) {
			return false;
		}
		if (!inEdgevilleBankPvpInstance(player.getX(), player.getY(), player.getHeight()) && player.getX() >= 0 && player.getY() >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param player
	 * @return True, if the player is at the traditional Edgeville wilderness spots.
	 */
	public static boolean inWestDragons(Player player) {
		if (isWithInArea(player, 2962, 2995, 3573, 3605)) {
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player is in the safe PK zone where players can attack each other.
	 */
	public static boolean inSafePkFightZone(Player player) {
		if (Area.inClanWarsDangerousArea(player)) {
			return true;
		}
		return false;
	}

	/**
	 * Counting the part behind the line too, as in the whole clan wars map region
	 */
	public static boolean inSafePkFightZoneAll(Player player) {
		if (isWithInArea(player, 3250, 3400, 4740, 4860)) {
			return true;
		}
		return false;
	}

	public static boolean inClanWarsDangerousArea(Player player) {
		if (isWithInArea(player, 3250, 3400, 4760, 4860)) {
			return true;
		}
		return false;
	}

	/**
	 * @param player The associated player.
	 * @return True, if the player is in the Wilderness.
	 */
	public static boolean inDangerousPvpArea(Player player) {
		if (inWilderness(player.getX(), player.getY(), player.getHeight())) {
			return true;
		}
		return false;
	}

	public static boolean inWilderness(int x, int y, int height) {
		if (isWithInArea(x, y, 2825, 2942, 10052, 10234)) //Rev caves
		{
			return true;
		}
		if (isWithInArea(x, y, 2995, 3025, 3524, 3534)) {
			return false;
		}
		if (isWithInArea(x, y, 3001, 3025, 3535, 3538)) {
			return false;
		}
		if (isWithInArea(x, y, 3005, 3025, 3539, 3543)) {
			return false;
		}
		if (isWithInArea(x, y, 3026, 3029, 3524, 3532)) {
			return false;
		}
		if (isWithInArea(x, y, 3030, 3035, 3522, 3527)) {
			return false;
		}
		if (isWithInArea(x, y, 2944, 2993, 3524, 3524)) {
			return false;//fix for south of ::wests
		}
		if (isWithInArea(x, y, 2994, 2994, 3525, 3534)) {
			return false;//fix for south of ::wests
		}
		if (isWithInArea(x, y, 3066, 3123, 3520, 3523)) {
			return true;
		}
		if (isWithInArea(x, y, 2942, 3391, 3524, 3966) || isWithInArea(x, y, 2942, 3391, 9919, 10365)) {
			return true;
		}
		if (height == 4) {
			if (!Area.inEdgevilleBankPvpInstance(x, y, height) && y >= 0 && x >= 0) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Used to check if the player is in a PVP area.
	 *
	 * @return The current state.
	 */
	public static boolean inDangerousPvpAreaOrClanWars(Player player) {
		if (inSafePkFightZone(player)) {
			return true;
		}
		if (inDangerousPvpArea(player)) {
			return true;
		}
		Minigame minigame = player.getMinigame();

		if (minigame != null) {
			MinigameArea minigameArea = minigame.getAreaOrNull(player);

			if (minigameArea.getCombatSafety() == MinigameAreaCombatSafety.UNSAFE) {
				return true;
			}
		}
		return false;
	}

	public static boolean inDuelArenaRing(Player player) {
		if (isWithInArea(player, 3332, 3390, 3243, 3259)) {
			return true;
		}
		return false;
	}

	public static boolean inDuelArena(Player player) {
		if (isWithInArea(player, 3323, 3393, 3196, 3290) || isWithInArea(player, 3312, 3322, 3224, 3247)) {
			return true;
		}
		return false;
	}

	public static boolean inGodWarsDungeon(Player player) {
		if (isWithInArea(player, 2823, 2941, 5255, 5374)) {
			return true;
		}
		return false;
	}

	/**
	 * True if the npc is in multi.
	 */
	public static boolean npcInMulti(Npc npc, int x, int y) {
		switch (npc.npcType) {
			case 493: // Enormous tentacle/whirlpool
			case 496: // Kraken/whirlpool
				return true;
		}

		return inMulti(x, y);
	}

	public static boolean inMulti(int x, int y) {
		// Do not add Zombie areas to this, make a different one where if the npc is spawned in a zombie instance, then it is multi, and player is in zombie instance, then multi.
		if (isWithInArea(x, y, 2863, 2877, 5350, 5374) || // Bandos Boss Chamber
		    isWithInArea(x, y, 2889, 2908, 5255, 5276) || // Saradomin Boss Chamber
		    isWithInArea(x, y, 2915, 2941, 5316, 5332) || // Zamorak Boss Chamber
		    isWithInArea(x, y, 2823, 2843, 5295, 5309) || // Armadyl Boss Chamber
		    isWithInArea(x, y, 2624, 2690, 2550, 2619) || // Pest control
		    isWithInArea(x, y, 2896, 2927, 3595, 3630) || // Troll map(used on 317 servers for GWD)
		    isWithInArea(x, y, 2892, 2932, 4435, 4464) || // Dagannoth kings.
		    isWithInArea(x, y, 2975, 2999, 9625, 9659) || // Barrelchest
		    isWithInArea(x, y, 3305, 3324, 9362, 9392) || // Corporeal Beast
		    isWithInArea(x, y, 2365, 2500, 5057, 5186) || // Tzhaar.
		    isWithInArea(x, y, 2888, 2942, 10054, 10228) || // Rev caves 1
		    isWithInArea(x, y, 2825, 2887, 10062, 10234) || // Rev caves 2
		    isWithInArea(x, y, 2250, 2290, 3060, 3090) ||  //zulrah area

		    // Vorkath area
		    isWithInArea(x, y, 2262, 2282, 4056, 4076) ||

		    // KQ area
		    isWithInArea(x, y, 3460, 3509, 9480, 9520) ||
		    // King black dragon.
		    isWithInArea(x, y, 2256, 2287, 4680, 4711) ||
		    //Fally multi zone
		    isWithInArea(x, y, 2944, 3008, 3304, 3455) ||
		    //Sand crabs
		    isWithInArea(x, y, 1832, 1884, 3539, 3573) ||
		    // Wilderness multi zones.
		    isWithInArea(x, y, 3009, 3071, 3599, 3712)
		    //
		    || isWithInArea(x, y, 2945, 2957, 3816, 3827)
		    //
		    || isWithInArea(x, y, 3138, 3327, 3523, 3651)
		    //
		    || isWithInArea(x, y, 3189, 3327, 3651, 3751)
		    //
		    || isWithInArea(x, y, 3151, 3327, 3751, 3903)
		    //
		    || isWithInArea(x, y, 3199, 3393, 3841, 3968)
		    //
		    || isWithInArea(x, y, 3133, 3151, 3839, 3903)
		    //
		    || isWithInArea(x, y, 3111, 3133, 3871, 3903)
		    //
		    || isWithInArea(x, y, 3071, 3119, 3877, 3903)
		    //
		    || isWithInArea(x, y, 3049, 3071, 3897, 3903)
		    //
		    || isWithInArea(x, y, 3007, 3049, 3856, 3903)
		    //
		    || isWithInArea(x, y, 3049, 3053, 3863, 3870)
		    //
		    || isWithInArea(x, y, 2983, 3009, 3912, 3967)
		    // Scorpio area
		    || isWithInArea(x, y, 3218, 3250, 10330, 10355)
		    // Lizard shaman area
		    || isWithInArea(x, y, 1464, 1583, 3668, 3710)

		    || isWithInArea(x, y, 1220, 1260, 1230, 1270)

		    || isWithInArea(x, y, 2974, 3000, 4368, 4400)

		    || isWithInArea(x, y, 2950, 3132, 4737, 4860)

			// End of Wilderness multi zones.
				) {
			return true;
		}
		return false;
	}

	public static boolean inCylopsRoom(Player player) {
		if (player.getHeight() != 2) {
			return false;
		}
		if (isWithInArea(player.getX(), player.getY(), 2847, 2877, 3532, 3557)) {
			return true;
		}
		if (isWithInArea(player.getX(), player.getY(), 2838, 2846, 3543, 3555)) {
			return true;
		}
		return false;
	}

	public static boolean inDragDefenderRoom(Player player) {
		if (isWithInArea(player.getX(), player.getY(), 2905, 2940, 9957, 9965)) {
			return true;
		}
		if (isWithInArea(player.getX(), player.getY(), 2912, 2940, 9966, 9973)) {
			return true;
		}
		return false;
	}

	private static final Boundary ZULRAH_AREA = new Boundary(2250, 3060, 2290, 3090);

	public static void removeFromUnaccessibleArea(Player player) {
		if (Boundary.isIn(player, ZULRAH_AREA)) {
			player.move(new Position(3102, 3495, 0));
		}
	}
}
