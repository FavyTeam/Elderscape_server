package game.content.staff;

import core.ServerConstants;
import game.content.miscellaneous.Teleport;
import game.content.packet.PrivateMessagingPacket;
import game.player.Area;
import game.player.Player;

/**
 * Private admin area where no one can see me online or teleport to me.
 * 
 * @author MGT Madness, created on 08-05-2018.
 */
public class PrivateAdminArea {

	public final static int ADMIN_AREA_X_START = 3277;

	public final static int ADMIN_AREA_X_END = 3285;

	public final static int ADMIN_AREA_Y_START = 2764;

	public final static int ADMIN_AREA_Y_END = 2777;

	public final static int ADMIN_AREA_TELEPORT_X = 3281;

	public final static int ADMIN_AREA_TELEPORT_Y = 2770;

	public final static int ADMIN_AREA_TELEPORT_HEIGHT = 0;

	public static boolean playerIsInAdminArea(Player player) {
		if (Area.isWithInArea(player, ADMIN_AREA_X_START, ADMIN_AREA_X_END, ADMIN_AREA_Y_START, ADMIN_AREA_Y_END)) {
			return true;
		}
		return false;
	}

	public static boolean teleportToAdminArea(Player player, String command) {
		if (command.equals("private")) {
			Teleport.spellTeleport(player, ADMIN_AREA_TELEPORT_X, ADMIN_AREA_TELEPORT_Y, ADMIN_AREA_TELEPORT_HEIGHT, false);
			return true;
		}
		return false;
	}

	public static void spotLanded(Player player) {
		if (player.teleportToX == PrivateAdminArea.ADMIN_AREA_TELEPORT_X && player.teleportToY == PrivateAdminArea.ADMIN_AREA_TELEPORT_Y && player.teleportToHeight == PrivateAdminArea.ADMIN_AREA_TELEPORT_HEIGHT) {
			player.privateChat = ServerConstants.PRIVATE_OFF;
			PrivateMessagingPacket.setPlayerPrivateMessageStatusForWorld(player);
		}
	}

}
