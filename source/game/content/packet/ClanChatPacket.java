package game.content.packet;

import core.Server;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Chat
 **/
public class ClanChatPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		String textSent = Misc.longToPlayerName(player.getInStream().readQWord());
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "textSent: " + textSent);
		}
		textSent = Misc.capitalize(textSent);
		Server.clanChat.joinClanChat(player, textSent, false);
	}
}
