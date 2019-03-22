package game.content.packet;

import core.ServerConstants;
import game.container.impl.MoneyPouch;
import game.content.bank.Bank;
import game.content.bank.DepositBox;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.interfaces.donator.DonatorShop;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.DiceSystem;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.PriceChecker;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.prayer.BoneOnAltar;
import game.content.tradingpost.TradingPost;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Bank X Items
 **/

public class BankXPacket implements PacketType {
	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int xAmount = player.getInStream().readDWord();


		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch() + ", " + player.bankSearchString);
			PacketHandler.saveData(player.getPlayerName(), "xAmount: " + xAmount);
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + player.getxInterfaceId());
			PacketHandler.saveData(player.getPlayerName(), "slot: " + player.xRemoveSlot);
		}
		if (xAmount <= 0) {
			if (player.getAmountInterface().contains("TRADING POST")) {
				InterfaceAssistant.closeDialogueOnly(player);
				player.setAmountInterface("");
			}
			return;
		}
		if (player.getxInterfaceId() == MoneyPouch.MONEY_POUCH_LINE) {
			MoneyPouch.withdrawAmount(player, xAmount);
		} else
		if (player.getxInterfaceId() == 4393) {
			PriceChecker.removeItem(player, player.xRemoveId, player.xRemoveSlot, xAmount);
		}
		else if (player.getxInterfaceId() == NpcDoubleItemsInterface.INVENTORY_OVERLAY_ITEMS_ID) {
			if (trackPlayer) {
				if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
				}
			}
			if (NpcDoubleItemsInterface.storeItem(player, player.xRemoveId, player.xRemoveSlot, xAmount)) {
			}
		}
		else if (player.getxInterfaceId() == 5064) {
			if (trackPlayer) {
				if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
				}
			}
			if (PriceChecker.storeItem(player, player.xRemoveId, player.xRemoveSlot, xAmount, false)) {

			} else {
				Bank.bankItem(player, player.playerItems[player.xRemoveSlot], player.xRemoveSlot, xAmount, true);
			}

		} else if (player.getxInterfaceId() == 5382) {
			if (trackPlayer) {
				PacketHandler.saveData(player.getPlayerName(), "item: " + player.bankingItems[player.xRemoveSlot]);
				PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.bankingItemsN[player.xRemoveSlot]);
				PacketHandler.saveData(player.getPlayerName(), "search: " + player.isUsingBankSearch());
			}
			Bank.withdrawFromBank(player, player.bankingItems[player.xRemoveSlot], player.xRemoveSlot, xAmount, false, false);
			Bank.updateClientLastXAmount(player, xAmount);
		} else if (player.getxInterfaceId() >= 35001 && player.getxInterfaceId() <= 35008) {
			int previous = player.bankingTab;

			int tab = player.getxInterfaceId() - 35001 + 1;

			Bank.openCorrectTab(player, tab, false);

			Bank.withdrawFromBank(player, player.xRemoveId, player.xRemoveSlot, xAmount, true, false);

			Bank.openCorrectTab(player, previous, false);

			Bank.updateClientLastXAmount(player, xAmount);
		} else if (player.getxInterfaceId() == 3322) {
			if (player.getDuelStatus() <= 0 && player.isInTrade()) {
				if (trackPlayer) {
					if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
					}
				}
				player.getTradeAndDuel().tradeItem(player.xRemoveId, player.xRemoveSlot, xAmount);
			} else if (player.getDuelStatus() == 1 || player.getDuelStatus() == 2) {
				if (trackPlayer) {
					if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
						PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
						PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
					}
				}
				player.getTradeAndDuel().stakeItem(player.xRemoveId, player.xRemoveSlot, xAmount);
			}

		} else if (player.getxInterfaceId() == 3415) {
			if (player.getDuelStatus() <= 0) {
				player.getTradeAndDuel().fromTrade(player.xRemoveId, player.xRemoveSlot, xAmount);
			}

		} else if (player.getxInterfaceId() == 6669) {
			player.getTradeAndDuel().fromDuel(player.xRemoveId, player.xRemoveSlot, xAmount);

		} else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.HERBLORE])) {
			Herblore.xAmountHerbloreAction(player, xAmount);
		} else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING] + " LEATHER")) {
			LeatherCrafting.xAmountLeatherCraftingAction(player, xAmount);
		} else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING])) {
			GemCrafting.xAmountCraftingAction(player, xAmount);
		} else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.FLETCHING])) {
			BowStringFletching.xAmountFletchingAction(player, xAmount);
		} else if (player.getAmountInterface().equals("HARD LEATHER BODY")) {
			LeatherCrafting.xAmountHardLeatherBodyAction(player, xAmount);
		} else if (player.getAmountInterface().equals(ServerConstants.SKILL_NAME[ServerConstants.PRAYER])) {
			BoneOnAltar.xAmountPrayerAction(player, xAmount);
		} else if (player.getAmountInterface().equals("TANNING")) {
			LeatherCrafting.tan(player, player.skillingData[0], player.skillingData[1], xAmount, player.skillingData[2]);
		} else if (player.getAmountInterface().equals("STRINGING AMULET")) {
			JewelryCrafting.stringAmuletAmount(player, xAmount);
		} else if (player.getAmountInterface().equals("COMBINE ARROWS")) {
			Fletching.xAmountCombineArrowParts(player, xAmount);
		} else if (player.getAmountInterface().equals("CUT GEM INTO BOLT TIPS")) {
			Fletching.xAmountCutGem(player, xAmount);
		} else if (player.getAmountInterface().equals("ATTACH TIPS TO BOLT")) {
			Fletching.xAmountAttachTipToBolt(player, xAmount);
		} else if (player.getAmountInterface().equals("LOOT NOTIFICATION")) {
			player.valuableLoot = xAmount;
			player.getPA().sendMessage(
					"Loot notification will appear for items worth " + ServerConstants.RED_COL + Misc.formatNumber(player.valuableLoot) + ServerConstants.BLACK_COL + " "
					+ ServerConstants.getMainCurrencyName().toLowerCase() + " and above.");
		} else if (player.getAmountInterface().equals("SHOP BUY X")) {
			player.getShops().buyItem(player.xRemoveId, player.xRemoveSlot, xAmount);
		} else if (player.getAmountInterface().equals("SHOP SELL X")) {
			if (trackPlayer) {
				if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
				}
			}
			player.getShops().sellItemToShop(player, player.xRemoveId, player.xRemoveSlot, xAmount);
		} else if (player.getAmountInterface().equals("DONATOR SHOP NORMAL")) {
			DonatorShop.donatorShopNormalXAmount(player, xAmount);
		} else if (player.getAmountInterface().equals("WITHDRAW FROM VAULT")) {
			DiceSystem.withdrawFromVaultAction(player, xAmount);
		} else if (player.getAmountInterface().equals("DEPOSIT INTO VAULT")) {
			DiceSystem.depositIntoVaultAction(player, xAmount);
		} else if (player.getAmountInterface().equals("TRADING POST BUYING EDIT AMOUNT")) {
			TradingPost.editAmountXEntered(player, xAmount, "BUYING");
		} else if (player.getAmountInterface().equals("TRADING POST BUYING EDIT PRICE")) {
			TradingPost.editPriceXEntered(player, xAmount, "BUYING");
		} else if (player.getAmountInterface().equals("TRADING POST SELLING EDIT AMOUNT")) {
			TradingPost.editAmountXEntered(player, xAmount, "SELLING");
		} else if (player.getAmountInterface().equals("TRADING POST SELLING EDIT PRICE")) {
			TradingPost.editPriceXEntered(player, xAmount, "SELLING");
		} else if (player.getxInterfaceId() == 7423) {
			if (trackPlayer) {
				if (player.xRemoveSlot >= 0 && player.xRemoveSlot <= 27) {
					PacketHandler.saveData(player.getPlayerName(), "item: " + player.playerItems[player.xRemoveSlot]);
					PacketHandler.saveData(player.getPlayerName(), "itemAmount: " + player.playerItemsN[player.xRemoveSlot]);
				}
			}
			DepositBox.depositItemAmount(player, player.xRemoveSlot, xAmount);
		} else if (Lottery.receiveLotteryBuyAmount(player, player.getAmountInterface(), xAmount)) {

		} else {
			EditCombatSkill.editCombatSkill(player, xAmount);
		}
		player.setAmountInterface("");
		player.setxInterfaceId(0);
	}
}
