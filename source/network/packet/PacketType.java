package network.packet;

import game.player.Player;



public interface PacketType {
	public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer);
}
