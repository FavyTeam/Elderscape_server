package game.content.packet;

import game.content.dialogue.DialogueChain;
import game.player.Player;
import network.packet.PacketType;


/**
 * Dialogue
 **/
public class DialoguePacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		if (player.isJailed()) {
			return;
		}
		DialogueChain chain = player.getDialogueChain();

		if (chain != null) {
			if (chain.isOnLastLink()) {
				player.setDialogueChain(null);
				chain.end(player);
				if (player.getDialogueChain() == null) {
					player.getPA().closeInterfaces(true);
				}
			} else {
				chain.next(player);
			}
		} else {
			player.getDH().dialoguePacketAction();
		}
	}

}
