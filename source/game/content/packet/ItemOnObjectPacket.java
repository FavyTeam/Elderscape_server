package game.content.packet;

import core.ServerConfiguration;
import game.item.ItemAssistant;
import game.item.ItemOnObject;
import game.object.clip.Region;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

public class ItemOnObjectPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		player.getInStream().readUnsignedWord();
		int objectId = player.getInStream().readSignedWordBigEndian();
		int objectY = player.getInStream().readSignedWordBigEndianA();
		int slot = player.getInStream().readUnsignedWord();
		int objectX = player.getInStream().readSignedWordBigEndianA();
		int itemId = player.getInStream().readUnsignedWord();
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "objectId: " + objectId);
			PacketHandler.saveData(player.getPlayerName(), "objectY: " + objectY);
			PacketHandler.saveData(player.getPlayerName(), "value1: " + slot);
			PacketHandler.saveData(player.getPlayerName(), "objectX: " + objectX);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}
		slot /= 256;
		if (ItemAssistant.isNulledSlot(slot)) {
			return;
		}
		if (!ItemAssistant.playerHasItem(player, itemId, 1, slot)) {
			return;
		}



		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		boolean skip = false;
		if (!Region.objectExists(player, objectId, objectX, objectY, player.getHeight())) {
			skip = true;
		}
		if (skip && !ServerConfiguration.DEBUG_MODE) {
			return;
		} else if (skip) {
			if (ServerConfiguration.DEBUG_MODE) {
				Misc.print("Un-verified [Item: " + itemId + "] on [Object: " + objectId + "][Object X: " + objectX + "][Object Y: " + objectY + "]");
				return;
			}
		}
		ItemOnObject.itemOnObject(player, objectId, objectX, objectY, itemId, slot);
	}

}
