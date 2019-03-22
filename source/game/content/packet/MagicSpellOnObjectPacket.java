package game.content.packet;

import core.ServerConfiguration;
import game.object.clip.Region;
import game.player.Player;
import utility.Misc;

/**
 * Magic spell on object packet done using the command packet.
 *
 * @author MGT Madness, created on 07-09-2017.
 */
public class MagicSpellOnObjectPacket {

	/**
	 * Receive the magic spell on object packet and perform action.
	 */
	public static void receiveMagicOnObjectPacket(Player player, String data) {
		if (!ClickObjectPacket.canUseObjects(player)) {
			return;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		try {
			String[] parse = data.split(":");
			int objectX = Integer.parseInt(parse[1]);
			int objectY = Integer.parseInt(parse[3]);
			int objectId = Integer.parseInt(parse[4]);
			int spellId = Integer.parseInt(parse[2]);


			if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
				return;
			}
			if (ServerConfiguration.DEBUG_MODE) {
				Misc.print("MAGIC SPELL ON OBJECT [Object X: " + objectX + "] [Object Y: " + objectY + "] [Object Id: " + objectId + "] [Spell Id: " + spellId + "]");
			}
			player.turnPlayerTo(objectX, objectY);
			ClickObjectPacket.reset(player);
		} catch (Exception e) {

		}
	}
}
