package game.content.packet;

import core.ServerConfiguration;
import game.player.Player;
import network.packet.PacketType;
import utility.Misc;

/**
 * Created by Jason MK on 2018-07-31 at 9:54 AM
 */
public class SecondPlayerClick implements PacketType {

    @Override
    public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer) {
        int index = c.getInStream().readSignedWord();
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("Second player click, Value: " + index);
		}
    }

}
