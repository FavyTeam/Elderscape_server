package game.content.packet;

import core.ServerConfiguration;
import game.content.bank.Bank;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Created by Jason MacKeigan on 2018-03-30 at 4:12 PM
 */
public class PlaceholderChangePacket implements PacketType {

	private static final boolean DISABLED = false;

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int component = player.getInStream().readDWord();

		int containerIndex = player.getInStream().readUnsignedWord();

		int itemId = player.getInStream().readUnsignedWord();

		if (ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage(String.format("class=%s, component=%s, containerIndex=%s, itemId=%s",
			                                         PlaceholderChangePacket.class.getSimpleName(), component, containerIndex, itemId));
		}

		if (DISABLED) {
			return;
		}

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "searching: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "component: " + component);
			PacketHandler.saveData(player.getPlayerName(), "containerIndex: " + containerIndex);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
		}



		if (component == 5382 || component >= 35_001 && component <= 35_008) {
			int previousTab = player.bankingTab;

			int tabId = component == 5382 ? player.bankingTab : 1 + (component - 35_001);

			int tabForItem = Bank.getTabforItemOrNone(player, itemId);
			if (tabForItem != tabId && !player.isUsingBankSearch() || tabForItem == -1) {
				return;
			}
			Bank.openCorrectTab(player, tabId, false);
			if (Bank.placeholderExists(player, containerIndex) && !player.isUsingBankSearch()) {
				Bank.releasePlaceholder(player, containerIndex, tabForItem);
			} else {
				Bank.createPlaceholder(player, containerIndex);
			}
			int containerId = tabId == 0 ? 5382 : 35_000 + tabId;

			if (player.isUsingBankSearch()) {
				return;
			}
				Bank.updateContainer(player, containerId,
			                     Bank.getItemsForContainer(player, containerId),
			                     Bank.getAmountsForContainer(player, containerId));
			Bank.updateAmount(player);

			Bank.openCorrectTab(player, previousTab, true);
			Bank.sendTabs(player, false);
		}
	}
}
