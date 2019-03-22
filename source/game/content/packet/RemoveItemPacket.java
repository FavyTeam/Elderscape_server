package game.content.packet;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.combat.vsplayer.magic.AutoCast;
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
 * Remove Item
 **/
public class RemoveItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int interfaceId = player.getInStream().readUnsignedWordA();
		int removeSlot = player.getInStream().readUnsignedWordA();
		int removeId = player.getInStream().readUnsignedWordA();
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
			PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
			PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
		}
		if (player.getPlayerName().equalsIgnoreCase("jason") && ServerConfiguration.DEBUG_MODE) {
			player.getPA().sendMessage(String.format("interfaceId=%s, removeSlot=%s, removeId=%s", interfaceId, removeSlot, removeId));
		}
		if (ItemAssistant.nulledItem(removeId)) {
			return;
		}
		switch (interfaceId) {
			case BoBSummoningFamiliarSpecialAbility.BOB_STORAGE_CONTAINER:
				BoBSummoningFamiliarSpecialAbility.withdraw(player, new GameItem(removeId, 1));
				break;
			case 4393:
				PriceChecker.removeItem(player, removeId, removeSlot, 1);
				break;
			case 7423:
				DepositBox.depositItemAmount(player, removeSlot, 1);
				break;
			case 1688:
				if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
					return;
				}
				if (removeId == 22_494) {
					AutoCast.resetAutocast(player, false);
				}
				ItemAssistant.removeItem(player, removeId, removeSlot, false);
				break;

			case NpcDoubleItemsInterface.INVENTORY_OVERLAY_ITEMS_ID :
				if (NpcDoubleItemsInterface.storeItem(player, removeId, removeSlot, 1)) {
					return;
				}
				break;

			case 5064:
				if (BoBSummoningFamiliarSpecialAbility.store(player, new GameItem(removeId, 1),
						removeSlot)) {
					break;
				}
				if (PriceChecker.storeItem(player, removeId, removeSlot, 1, false)) {

				} else {
					Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, 1, true);
				}
				break;

			case 5382:
				Bank.withdrawFromBank(player, removeId, removeSlot, 1, false, false);
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

				Bank.withdrawFromBank(player, removeId, removeSlot, 1, true, false);

				Bank.openCorrectTab(player, previous, false);
				break;

			case 3900:
				if (Shop.ENABLED) {
					Shop shop = player.getShop();

					if (shop == null) {
						return;
					}
					shop.buy(player, removeId, 1, removeSlot);
					return;
				}
				player.getShops().checkShopPrice(removeId, removeSlot);
				break;

			case 3823:
				player.getShops().priceCheckItemToSell(player, removeId);
				break;

			case 3322:
				if (player.getDuelStatus() <= 0 && player.isInTrade()) {
					player.getTradeAndDuel().tradeItem(removeId, removeSlot, 1);
				} else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2) {
					player.getTradeAndDuel().stakeItem(removeId, removeSlot, 1);
				}
				break;

			case 3415:
				if (player.getDuelStatus() <= 0) {
					player.getTradeAndDuel().fromTrade(removeId, removeSlot, 1);
				}
				break;

			case 6669:
				player.getTradeAndDuel().fromDuel(removeId, removeSlot, 1);
				break;

			case 1119:
			case 1120:
			case 1121:
			case 1122:
			case 1123:
				Smithing.readInput(player.baseSkillLevel[ServerConstants.SMITHING], Integer.toString(removeId), player, 1);
				break;
		}
	}

}
