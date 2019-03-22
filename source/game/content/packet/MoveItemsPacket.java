package game.content.packet;

import core.ServerConfiguration;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Move Items
 **/
public class MoveItemsPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		byte insert = player.getInStream().readSignedByteC();
		int itemFrom = player.getInStream().readUnsignedWordBigEndianA();
		int itemTo = player.getInStream().readUnsignedWordBigEndian();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
			PacketHandler.saveData(player.getPlayerName(), "insert: " + insert);
			PacketHandler.saveData(player.getPlayerName(), "itemFrom: " + itemFrom);
			PacketHandler.saveData(player.getPlayerName(), "itemTo: " + itemTo);
		}
		if (ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage(String.format("interfaceId=%s, insert=%s, itemFrom=%s, itemTo=%s", interfaceId, insert, itemFrom, itemTo));
		}
		if (itemFrom < 0 || itemTo < 0) {
			return;
		}

		if (player.isInTrade()) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}
		if (player.getTradeStatus() == 1) {
			player.getTradeAndDuel().declineTrade1(true);
			return;
		}
		if (player.getDuelStatus() == 1) {
			player.getTradeAndDuel().declineDuel(false);
			return;
		}
		ItemAssistant.moveItems(player, itemFrom, itemTo, interfaceId, insert);
	}
}
