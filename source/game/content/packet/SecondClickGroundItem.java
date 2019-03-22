package game.content.packet;

import core.Server;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.HunterTrapCreationMethod;
import game.player.Player;
import network.packet.PacketType;

/**
 * Created by Jason MK on 2018-08-03 at 12:42 PM
 */
public class SecondClickGroundItem implements PacketType {

    @Override
    public void processPacket(Player c, int packetType, int packetSize, boolean trackPlayer) {
        int x = c.getInStream().readSignedWordBigEndian();

        int y = c.getInStream().readSignedWordBigEndianA();

        int id = c.getInStream().readUnsignedWordA();

        c.sendDebugMessageF("packet=%s, x=%s, y=%s, id=%s",
                getClass().getName(), x, y, id);

        if (!Server.itemHandler.itemExists(id, x, y)) {
            return;
        }
		switch (id) {
			case 10008:
				c.getHunterSkill().lay(c, HunterStyle.BOX_TRAPPING, HunterTrapCreationMethod.GROUND);
			case 10006:
				c.getHunterSkill().lay(c, HunterStyle.BIRD_SNARING, HunterTrapCreationMethod.GROUND);
				break;
			default:
				c.getPA().sendMessage("Nothing interesting happens.");
				break;
		}
    }
}
