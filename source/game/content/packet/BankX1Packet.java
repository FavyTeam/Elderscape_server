package game.content.packet;

import game.content.interfaces.InterfaceAssistant;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Bank X Items
 **/
public class BankX1Packet implements PacketType {

	public static final int PART1 = 135;

	public static final int PART2 = 208;

	public int XremoveSlot, XinterfaceID, XremoveID, Xamount;

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		player.xRemoveSlot = player.getInStream().readSignedWordBigEndian();
		player.setxInterfaceId(player.getInStream().readUnsignedWordA());
		player.xRemoveId = player.getInStream().readSignedWordBigEndian();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "xRemoveSlot: " + player.xRemoveSlot);
			PacketHandler.saveData(player.getPlayerName(), "xInterfaceId: " + player.getxInterfaceId());
			PacketHandler.saveData(player.getPlayerName(), "xRemoveId: " + player.xRemoveId);
		}



		if (ItemAssistant.nulledItem(player.xRemoveId)) {
			return;
		}

		if (player.getxInterfaceId() == 3900) {
			InterfaceAssistant.closeDialogueOnly(player);
			InterfaceAssistant.showAmountInterface(player, "SHOP BUY X", "Enter amount");
		}
		else if (player.getxInterfaceId() == 3823) {
			InterfaceAssistant.closeDialogueOnly(player);
			InterfaceAssistant.showAmountInterface(player, "SHOP SELL X", "Enter amount");
		}
		if (packetType == PART1) {
			player.getOutStream().createFrame(27);
			player.getPA().sendMessage(":packet:enteramounttext Enter amount");
		}

	}
}
