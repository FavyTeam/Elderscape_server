package game.content.packet;

import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.miscellaneous.PriceChecker;
import game.content.skilling.summoning.familiar.special.impl.BoBSummoningFamiliarSpecialAbility;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;

/**
 * Bank All Items
 **/
public class BankAllPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int removeSlot = player.getInStream().readUnsignedWordA();
		int interfaceId = player.getInStream().readUnsignedWord();
		int removeId = player.getInStream().readUnsignedWordA();
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "removeSlot: " + removeSlot);
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
			PacketHandler.saveData(player.getPlayerName(), "removeId: " + removeId);
		}


		if (ItemAssistant.nulledItem(removeId)) {
			return;
		}

		switch (interfaceId) {
			case BoBSummoningFamiliarSpecialAbility.BOB_STORAGE_CONTAINER:
				BoBSummoningFamiliarSpecialAbility.withdraw(player,
						new GameItem(removeId, Integer.MAX_VALUE));
				break;
			case 4393:
				PriceChecker.removeItem(player, removeId, removeSlot, Integer.MAX_VALUE);
				break;
			case 7423:
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				DepositBox.depositItemAmount(player, removeSlot, 0);
				break;
			case 3900:
				player.getShops().buyItem(removeId, removeSlot, 10);
				break;

			case 3823:
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				player.getShops().sellItemToShop(player, removeId, removeSlot, 10);
				break;

			case NpcDoubleItemsInterface.INVENTORY_OVERLAY_ITEMS_ID :
				if (NpcDoubleItemsInterface.storeItem(player, removeId, removeSlot, ItemAssistant.itemAmount(player, player.playerItems[removeSlot]))) {
					return;
				}
				break;
			case 5064:
				if (player.isInTrade()) {
					return;
				}
				if (trackPlayer) {
					if (removeSlot >= 0 && removeSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
					}
				}
				if (BoBSummoningFamiliarSpecialAbility.store(player,
						new GameItem(removeId, Integer.MAX_VALUE), removeSlot)) {
					break;
				}
				if (PriceChecker.storeItem(player, removeId, removeSlot, ItemAssistant.itemAmount(player, player.playerItems[removeSlot]), false)) {

				} else {
					if (ItemDefinition.getDefinitions()[removeId].stackable) {
						Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, player.playerItemsN[removeSlot], true);
					} else {
						Bank.bankItem(player, player.playerItems[removeSlot], removeSlot, ItemAssistant.itemAmount(player, player.playerItems[removeSlot]), true);
					}
				}
				break;

			case 5382: // Withdraw all
				if (trackPlayer) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.bankingItems[removeSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.bankingItemsN[removeSlot]);
					PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch());
				}
				Bank.withdrawFromBank(player, player.bankingItems[removeSlot], removeSlot, player.isUsingBankSearch() ? Integer.MAX_VALUE : player.bankingItemsN[removeSlot], false,
				                      false);
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

				Bank.withdrawFromBank(player, removeId, removeSlot, Integer.MAX_VALUE, true, false);

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
					if (ItemDefinition.getDefinitions()[removeId].stackable) {
						player.getTradeAndDuel().tradeItem(removeId, removeSlot, player.playerItemsN[removeSlot]);
					} else {
						player.getTradeAndDuel().tradeItem(removeId, removeSlot, 28);
					}
				} else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2) {
					if (trackPlayer) {
						if (removeSlot >= 0 && removeSlot <= 27) {
							PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[removeSlot]);
							PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[removeSlot]);
						}
					}
					if (ItemDefinition.getDefinitions()[removeId].stackable || ItemDefinition.getDefinitions()[removeId].note) {
						player.getTradeAndDuel().stakeItem(removeId, removeSlot, player.playerItemsN[removeSlot]);
					} else {
						player.getTradeAndDuel().stakeItem(removeId, removeSlot, 28);
					}
				}
				break;

			case 3415:
				if (player.getDuelStatus() <= 0) {
					if (ItemDefinition.getDefinitions()[removeId].stackable) {
						if (removeSlot > player.getTradeAndDuel().offeredItems.size() - 1) {
							return;
						}
						for (GameItem item : player.getTradeAndDuel().offeredItems) {
							if (item.getId() == removeId) {
								player.getTradeAndDuel().fromTrade(removeId, removeSlot, player.getTradeAndDuel().offeredItems.get(removeSlot).getAmount());
							}
						}
					} else {
						for (GameItem item : player.getTradeAndDuel().offeredItems) {
							if (item.getId() == removeId) {
								player.getTradeAndDuel().fromTrade(removeId, removeSlot, 28);
							}
						}
					}
				}
				break;

			case 6669:
				if (ItemDefinition.getDefinitions()[removeId].stackable || ItemDefinition.getDefinitions()[removeId].note) {
					if (removeSlot > player.getTradeAndDuel().myStakedItems.size() - 1) {
						return;
					}
					for (GameItem item : player.getTradeAndDuel().myStakedItems) {
						if (item.getId() == removeId) {
							player.getTradeAndDuel().fromDuel(removeId, removeSlot, player.getTradeAndDuel().myStakedItems.get(removeSlot).getAmount());
						}
					}

				} else {
					player.getTradeAndDuel().fromDuel(removeId, removeSlot, 28);
				}
				break;

		}
	}

}
