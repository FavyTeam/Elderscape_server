package game.content.packet;

import game.content.staff.StaffActivity;
import game.player.Player;
import network.packet.PacketType;


/**
 * Clicking interfaces.
 **/
public class ClickingOtherPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		StaffActivity.addStaffActivity(player);
		player.isUsingDeathInterface = false;
		player.usingShop = false;
		player.setUsingBankInterface(false);
		player.getTradeAndDuel().claimStakedItems();
		if (player.isInTrade()) {
			if (!player.acceptedTrade) {
				Player o = player.getTradeAndDuel().getPartnerTrade();
				o.tradeAccepted = false;
				player.tradeAccepted = false;
				o.setTradeStatus(0);
				player.setTradeStatus(0);
				player.tradeConfirmed = false;
				player.tradeConfirmed2 = false;
				player.getTradeAndDuel().declineTrade1(true);
			}
		}

		Player o = player.getTradeAndDuel().getPartnerDuel();
		if (player.getDuelStatus() == 5) {
			return;
		}
		if (o != null) {
			if (player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
				player.getTradeAndDuel().declineDuel(true);
				o.getTradeAndDuel().declineDuel(false);
			}
		}
		player.getPA().closeInterfaces(false);
	}

}
