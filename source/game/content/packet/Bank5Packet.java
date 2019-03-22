package game.content.packet;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.miscellaneous.PriceChecker;
import game.content.skilling.smithing.Smithing;
import game.content.skilling.summoning.familiar.special.impl.BoBSummoningFamiliarSpecialAbility;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.player.Player;
import game.shop.Shop;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Bank 5 Items
 **/
public class Bank5Packet implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int interfaceId = player.getInStream().readUnsignedWordBigEndianA();
		int removeId = player.getInStream().readSignedWordBigEndianA();
		int removeSlot = player.getInStream().readSignedWordBigEndian();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
			PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
			PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
		}

		if (ItemAssistant.nulledItem(removeId)) {
			return;
		}

		if (ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage(String.format("class=%s, interfaceId=%s, removeId=%s, removeSlot=%s", Bank5Packet.class, interfaceId, removeId, removeSlot));
		}
		switch (interfaceId) {
			case BoBSummoningFamiliarSpecialAbility.BOB_STORAGE_CONTAINER:
				BoBSummoningFamiliarSpecialAbility.withdraw(player, new GameItem(removeId, 5));
				break;
			case 4393:
				PriceChecker.removeItem(player, removeId, removeSlot, 5);
				break;
			case 7423:
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				DepositBox.depositItemAmount(player, removeSlot, 5);
				break;

			case 3900:
				player.getShops().buyItem(removeId, removeSlot, 1);
				break;

			case 3823:
				if (Shop.ENABLED && player.getShop() != null) {
					player.getShop().sell(player, removeId, 1, removeSlot);
					return;
				}
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				player.getShops().sellItemToShop(player, removeId, removeSlot, 1);
				break;

			case NpcDoubleItemsInterface.INVENTORY_OVERLAY_ITEMS_ID :
				if (NpcDoubleItemsInterface.storeItem(player, removeId, removeSlot, 5)) {
					return;
				}
				break;
			case 5064:
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				if (BoBSummoningFamiliarSpecialAbility.store(player, new GameItem(removeId, 5),
						removeSlot)) {
					break;
				}
				if (PriceChecker.storeItem(player, removeId, removeSlot, 5, false)) {

				} else {
					Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, 5, true);
				}
				break;

			case 5382:
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.bankingItems[removeSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.bankingItemsN[removeSlot]);
					PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch());
				}
				Bank.withdrawFromBank(player, removeId, removeSlot, 5, false, false);
				break;

			case 35001:
			case 35002:
			case 35003:
			case 35004:
			case 35005:
			case 35006:
			case 35007:
			case 35008:
				int previous = player.bankingTab;

				int tab = interfaceId - 35001 + 1;

				Bank.openCorrectTab(player, tab, false);

				Bank.withdrawFromBank(player, removeId, removeSlot, 5, true, false);

				Bank.openCorrectTab(player, previous, false);
				break;


			case 3322:
				if (player.getDuelStatus() <= 0 && player.isInTrade()) {
					if (trackPlayer) {
						if (removeSlot >= 0 && removeSlot <= 27) {
							PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
							PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
						}
					}
					player.getTradeAndDuel().tradeItem(removeId, removeSlot, 5);
				} else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2) {
					if (trackPlayer) {
						if (removeSlot >= 0 && removeSlot <= 27) {
							PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
							PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
						}
					}
					player.getTradeAndDuel().stakeItem(removeId, removeSlot, 5);
				}
				break;

			case 3415:
				if (player.getDuelStatus() <= 0) {
					player.getTradeAndDuel().fromTrade(removeId, removeSlot, 5);
				}
				break;

			case 6669:
				player.getTradeAndDuel().fromDuel(removeId, removeSlot, 5);
				break;


			case 1119:
			case 1120:
			case 1121:
			case 1122:
			case 1123:
				Smithing.readInput(player.baseSkillLevel[ServerConstants.SMITHING], Integer.toString(removeId), player, 5);
				break;
		}
	}

}
