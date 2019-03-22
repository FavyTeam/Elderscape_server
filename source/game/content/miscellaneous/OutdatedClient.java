package game.content.miscellaneous;

import core.Server;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * Inform player that the client is outdated.
 *
 * @author MGT Madness, created on 02-07-2017.
 */
public class OutdatedClient {

	public final static String[] text =
			{
					"@red@Your client is outdated! You will experience bugs unless it is updated!",
					"",
					"Please restart your client to update it.",
					"If it does not work, redownload the client from www.dawntained.com",
					"or type in ::forums and click on the play button at the top.",
			};

	public static void warnPlayer(Player player) {
		if (player.clientVersion < Server.clientVersion) {
			int frameIndex = 0;
			player.getPA().sendFrame126("@red@Outdated Client Warning!", 25003);
			for (int index = 0; index < text.length; index++) {
				player.getPA().sendFrame126(text[index], 25008 + frameIndex);
				frameIndex++;
			}
			InterfaceAssistant.setFixedScrollMax(player, 25007, frameIndex * 22);
			InterfaceAssistant.clearFrames(player, 25008 + frameIndex, 25298);
			player.getPA().displayInterface(25000);
		}

	}

}
