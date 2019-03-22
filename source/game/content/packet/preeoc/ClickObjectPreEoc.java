package game.content.packet.preeoc;

import core.GameType;
import game.player.Player;

/**
 * Clicking object
 * 
 * @author 2012
 *
 */
public class ClickObjectPreEoc {

	/**
	 * First click object
	 * 
	 * @param player the player
	 * @param objectId the object id
	 * @param objectX the object x
	 * @param objectY the object y
	 */
	public static boolean firstClickObject(final Player player, int objectId, int objectX,
			int objectY) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		return false;
	}

	/**
	 * Second click object
	 * 
	 * @param player the player
	 * @param objectId the object id
	 * @param objectX the object x
	 * @param objectY the object y
	 */
	public static boolean secondClickObject(final Player player, int objectId, int objectX,
			int objectY) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.clickObjectType = 0;
		return false;
	}

	/**
	 * Third click object
	 * 
	 * @param player the player
	 * @param objectId the object id
	 * @param objectX the object x
	 * @param objectY the object y
	 */
	public static boolean thirdClickObject(final Player player, int objectId, int objectX,
			int objectY) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.clickObjectType = 0;
		return false;
	}

	/**
	 * Fourth click object
	 * 
	 * @param player the player
	 * @param objectId the object id
	 * @param objectX the object x
	 * @param objectY the object y
	 */
	public static boolean fourthClickObject(final Player player, int objectId, int objectX,
			int objectY) {
		if (!GameType.isPreEoc()) {
			return false;
		}
		player.turnPlayerTo(player.getObjectX(), player.getObjectY());
		player.clickObjectType = 0;
		return false;
	}
}
