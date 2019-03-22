package core;

/**
 * Represents what type of gameplay is being used. Such as Osrs/Pre-eoc.
 * 
 * @author MGT Madness, revised onn 21-06-2018.
 *
 */
public enum GameType {
	ANY,
	OSRS,
	OSRS_PVP,
	OSRS_ECO,
	PRE_EOC;

	public final static int ALL_INTEGER = 0;

	public final static int OSRS_INTEGER = 1;

	public final static int PRE_EOC_INTEGER = 2;

	public static GameType gameType = OSRS_PVP;

	public static boolean isOsrs() {
		return gameType == OSRS_ECO || gameType == OSRS_PVP;
	}

	public static boolean isOsrsPvp() {
		return gameType == OSRS_PVP;
	}

	public static boolean isOsrsEco() {
		return gameType == OSRS_ECO;
	}

	public static boolean isPreEoc() {
		return gameType == PRE_EOC;
	}

	/**
	 * This is only used for enums for example, where the {@link match} given is dynamic and can be constantly changed.
	 */
	public static boolean getGameType(GameType match) {
		if (match == ANY) {
			return true;
		}
		if (match == OSRS && (gameType == OSRS_PVP || gameType == OSRS_ECO)) {
			return true;
		}
		return gameType == match;
	}
}
