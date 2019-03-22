package game.content.clanchat;

import core.Server;
import game.player.Player;

/**
 * Default clan chat.
 *
 * @author MGT Madness, created on 14-04-2014.
 */
public class DefaultClanChat {


	/**
	 * Create the default clan chat if it's non-existant, if it does exist, then join default clan chat.
	 *
	 * @param player The associated player.
	 */
	public static void createDefaultClanChat(Player player) {
		if (!player.lastClanChatJoined.isEmpty()) {
			Server.clanChat.joinClanChat(player, player.lastClanChatJoined, true);
		} else {
			// Needed because when i switch accounts and the other one does not have a cc joined, then it will show outdated texts.
			player.getPA().sendFrame126("Join Chat", 18135);
			Server.clanChat.clearClanChat(player, true);
		}
	}

}
