package game.content.packet;

import core.ServerConfiguration;
import game.content.bank.Bank;
import game.player.Player;
import network.packet.PacketType;
import org.apache.commons.lang3.Range;

/**
 * Created by Jason MacKeigan on 2018-03-27 at 11:02 AM
 */
public class MoveItemFromContainerPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int fromInterfaceId = player.getInStream().readUnsignedWordBigEndianA();

		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();

		byte insert = player.getInStream().readSignedByteC();

		int itemFrom = player.getInStream().readUnsignedWordBigEndianA();

		int itemTo = player.getInStream().readUnsignedWordBigEndian();

		if (ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage(String.format("interfaceId=%s, fromInterfaceId=%s, insert=%s, itemFrom=%s, itemTo=%s",
			                                         interfaceId, fromInterfaceId, insert, itemFrom, itemTo));
		}

		if (interfaceId == 22024 && (fromInterfaceId >= 35_001 && fromInterfaceId <= 35_008)) {
			int tabWithdrawFrom = 1 + (fromInterfaceId - 35_001);

			int previousTab = player.bankingTab;

			Bank.openCorrectTab(player, tabWithdrawFrom, false);
			Bank.toTab(player, 0, itemFrom, true, interfaceId == 22_024 ? true : false);
			Bank.openCorrectTab(player, previousTab, false);
			return;
		}

		if (interfaceId == 22024 && fromInterfaceId == 5382 && player.bankingTab != 0) {
			int tabWithdrawFrom = player.bankingTab;

			int previousTab = player.bankingTab;

			Bank.openCorrectTab(player, tabWithdrawFrom, false);
			Bank.toTab(player, 0, itemFrom, true, false);

			if (Bank.getBankItems(player, previousTab) > 0) {
				Bank.openCorrectTab(player, previousTab, false);
			}
			return;
		}

		if (interfaceId >= 22035 && interfaceId <= 22042 && (fromInterfaceId == 5382 || Range.between(35_001, 35_008).contains(fromInterfaceId))) {
			int toTabId = 1 + (interfaceId - 22035);

			int fromTabId = fromInterfaceId == 5382 ? player.bankingTab : 1 + (fromInterfaceId - 35001);
			int previousTab = player.bankingTab;
			if (previousTab != fromTabId) {
				Bank.openCorrectTab(player, fromTabId, false);
			}
			player.ignoreReOrder = true;
			Bank.toTab(player, toTabId, itemFrom, false, false);
			player.ignoreReOrder = false;

			if (previousTab != fromTabId) {
				Bank.openCorrectTab(player, previousTab, true);
			}
			if (Bank.checkEmpty(Bank.getBankArrayByBankingTab(player, previousTab))) {
				Bank.openUpBank(player, player.bankingTab, false, false);
			}
		} else if (interfaceId >= 35001 && interfaceId <= 35008 && fromInterfaceId >= 35001 && fromInterfaceId <= 35008
		           || interfaceId == 5382 && fromInterfaceId >= 35001 && fromInterfaceId <= 35008
		           || fromInterfaceId == 5382 && interfaceId >= 35001 && interfaceId <= 35008) {
			int toTabId = interfaceId == 5382 ? 0 : 1 + (interfaceId - 35001);

			int fromTabId = fromInterfaceId == 5382 ? 0 : 1 + (fromInterfaceId - 35001);

			if (toTabId == fromTabId) {
				player.getPA().sendMessage("You cannot move an item to the same tab.");
				return;
			}
			Bank.moveFromTabToTab(player, fromTabId, itemFrom, toTabId, itemTo, true);
		}

	}


}
