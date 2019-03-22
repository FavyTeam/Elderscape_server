package game.content.packet;

import game.player.Player;
import network.packet.PacketType;

/**
 * Slient Packet
 **/
public class SilentPacket implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer) {

	}
}
