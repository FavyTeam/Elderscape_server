package game.content.packet;

import core.ServerConfiguration;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

public class ItemOnGroundItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int value4 = player.getInStream().readSignedWordBigEndian();
		int itemUsed = player.getInStream().readSignedWordA();
		int groundItem = player.getInStream().readUnsignedWord();
		int value1 = player.getInStream().readSignedWordA();
		int value2 = player.getInStream().readSignedWordBigEndianA();
		int value3 = player.getInStream().readUnsignedWord();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "value4: " + value4);
			PacketHandler.saveData(player.getPlayerName(), "itemUsed: " + itemUsed);
			PacketHandler.saveData(player.getPlayerName(), "groundItem: " + groundItem);
			PacketHandler.saveData(player.getPlayerName(), "value1: " + value1);
			PacketHandler.saveData(player.getPlayerName(), "value2: " + value2);
			PacketHandler.saveData(player.getPlayerName(), "value3: " + value3);
		}
		if (ItemAssistant.nulledItem(itemUsed)) {
			return;
		}
		if (ItemAssistant.nulledItem(groundItem)) {
			return;
		}

		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		switch (itemUsed) {

			default:
				if (ServerConfiguration.DEBUG_MODE) {
					Misc.print("ItemUsed " + itemUsed + " on Ground Item " + groundItem);
				}
				break;
		}
	}

}
