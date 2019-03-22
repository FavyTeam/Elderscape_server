package game.content.packet;

import game.content.staff.StaffActivity;
import game.player.Player;
import network.packet.PacketType;


/**
 * Clicking in game
 **/
public class ClickingInGamePacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		StaffActivity.addStaffActivity(player);
	}

}
