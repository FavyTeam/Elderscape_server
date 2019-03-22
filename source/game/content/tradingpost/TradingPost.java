package game.content.tradingpost;

import core.GameType;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Announcement;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import tools.EconomyScan;
import utility.FileUtility;
import utility.Misc;

/**
 * Donator Tokens trading post.
 *
 * @author MGT Madness, created on 20-07-2017.
 */
public class TradingPost {


	public static String getAverageTokenPrice() {
		return GameType.isOsrsPvp() ? "4k" : "1.5m";
	}

	/**
	 * Average price of tokens, as a player guide price.
	 */
	public static int averagePrice = 5500;

	/**
	 * TradingPost debug. Saves on button call.
	 */
	public static ArrayList<String> debug = new ArrayList<String>();

	/**
	 * Store the Trading post database in this list.
	 */
	public static List<TradingPost> tradingPostData = new ArrayList<TradingPost>();

	/**
	 * Store the Trading post buy prices history
	 */
	public static ArrayList<String> buyHistory = new ArrayList<String>();

	public String name = "";

	public String action = "";

	public int itemAmountLeft;

	public int amountTraded;

	public int price;

	public long time;

	public int initialAmount;

	public String getName() {
		return name;
	}

	public String getAction() {
		return action;
	}

	public int getItemAmountLeft() {
		return itemAmountLeft;
	}

	public int getPrice() {
		return price;
	}

	public long getTime() {
		return time;
	}

	public int getAmountTraded() {
		return amountTraded;
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	/**
	 * This is the Trading post database
	 *
	 * @param name Name of the player that initiated the action
	 * @param action Buy/Sell
	 * @param itemAmountLeft Amount left
	 * @param amountTraded The amount of tokens bought/sold
	 * @param initialAmount The initial amount i started with, used to know if i sold all my amount.
	 */
	public TradingPost(String name, String action, int itemAmountLeft, int price, long time, int amountTraded, int initialAmount) {
		this.name = name;
		this.action = action;
		this.itemAmountLeft = itemAmountLeft;
		this.price = price;
		this.time = time;
		this.amountTraded = amountTraded;
		this.initialAmount = initialAmount;
	}

	/**
	 * True if trading post button.
	 */
	public static boolean tradingPostButton(Player player, int buttonId) {
		// Packet abuse check
		if (player.getActionIdUsed() != 28950) {
			return false;
		}
		switch (buttonId) {
			// Buying section.

			// Edit
			case 113035:
				editButton(player, "BUYING");
				return true;
			// Confirm
			case 113038:
				confirmButtonMain(player, "BUYING");
				return true;
			// Claim
			case 113041:
				claimItemsButton(player, "BUYING");
				return true;

			// Selling section.

			// Edit
			case 113044:

				editButton(player, "SELLING");
				return true;
			// Confirm
			case 113047:
				confirmButtonMain(player, "SELLING");
				return true;
			// Claim
			case 113050:
				claimItemsButton(player, "SELLING");
				return true;
		}
		return false;
	}

	private static void confirmButtonMain(Player player, String action) {
		if (player.getPA().isNewPlayerEco()) {
			player.getPA().sendMessage("You cannot use the trading post as a new player.");
			return;
		}
		if (GameMode.getGameModeContains(player, "IRON MAN")) {
			player.getPA().sendMessage("You cannot use the trading post as an Ironman.");
			return;
		}
		confirmButton(player, false, action);
		for (int index = 0; index < Integer.MAX_VALUE; index++) {
			boolean purchased = confirmButton(player, true, action);
			if (!purchased) {
				break;
			}
		}

	}

	private static void editButton(Player player, String string) {
		int currentTradingData = -1;
		currentTradingData = hasExistingOffer(player.getLowercaseName(), string);

		int interfaceId = 473;
		if (string.equals("SELLING")) {
			interfaceId++;
		}
		player.getDH().sendDialogues(currentTradingData >= 0 ? interfaceId + 2 : interfaceId);

	}

	/**
	 * Click on the claim button on the buying/selling section.
	 */
	private static void claimItemsButton(Player player, String action) {
		boolean itemClaimed = false;
		boolean itemsLeftToClaim = false;
		for (int index = 0; index < TradingPostItems.tradingPostItemsData.size(); index++) {
			if (index > TradingPostItems.tradingPostItemsData.size() - 1) {
				break;
			}
			TradingPostItems itemsData = TradingPostItems.tradingPostItemsData.get(index);
			if (itemsData.getName().equals(player.getLowercaseName())) {
				if (ItemAssistant.addItem(player, itemsData.getItemId(), itemsData.getItemAmount())) {

					debug(Misc.getDateAndTime() + "[" + player.getPlayerName() + "] claimItemsButton added: " + ItemAssistant.getItemName(itemsData.getItemId()) + " x"
					      + Misc.formatNumber(itemsData.getItemAmount()));
					itemClaimed = true;
					TradingPostItems.tradingPostItemsData.remove(index);
					index--;
					String string = "collected";
					if (itemsData.getAction().equals("CHANGE")) {
						string = "change collected";
					}
					if (itemsData.getAction().equals("BOUGHT")) {
						player.tradingPostHistory
								.add("B-" + itemsData.getItemAmount() + "-" + itemsData.getItemId() + "-" + itemsData.getItemPrice() + "-" + itemsData.getPartnerName());
					} else if (itemsData.getAction().equals("SOLD")) {
						player.tradingPostHistory.add("S-" + itemsData.getItemSoldAmount() + "-" + 7478 + "-" + itemsData.getItemPrice() + "-" + itemsData.getPartnerName());
					}
					if (player.tradingPostHistory.size() == 21) {
						player.tradingPostHistory.remove(0);
					}
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "Trading post: " + string + " x" + Misc.formatNumber(itemsData.getItemAmount()) + " " + ItemAssistant
							                                                                                                                                              .getItemName(
									                                                                                                                                              itemsData
											                                                                                                                                              .getItemId())
					                           + " from: " + Misc.capitalize(itemsData.getPartnerName()) + ".");
				} else {
					itemsLeftToClaim = true;
				}
			}
		}

		// If item claimed, then interface needs updating.
		if (itemClaimed) {
			int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
			if (currentTradingData >= 0) {
				TradingPost data = TradingPost.tradingPostData.get(currentTradingData);
				if (data.getAmountTraded() == data.getInitialAmount()) {
					TradingPost.tradingPostData.remove(currentTradingData);
					if (action.equals("BUYING")) {
						TradingPost.resetCurrentSectionInterfaces(player, action);
					} else {
						TradingPost.resetCurrentSectionInterfaces(player, action);
					}
				} else {
					if (!itemsLeftToClaim) {
						if (action.equals("BUYING")) {
							player.getPA().sendFrame126("Nothing to claim", 28983);
						} else {
							player.getPA().sendFrame126("Nothing to claim", 28986);
						}
					}
				}
			}
		}

		if (!itemClaimed && !itemsLeftToClaim) {
			player.getPA().sendMessage("There is nothing to claim.");
		}
		updateHistory(player);
	}

	/**
	 * Click on the confirm button on the buying section.
	 */
	private static boolean confirmButton(Player player, boolean skipRequirements, String action) {
		InterfaceAssistant.closeDialogueOnly(player);
		int initiatorIndex = -1; // Index of the buyer if buying, index of the seller if selling.
		long myTradingPostAmount = -1;
		long myTradingPostPrice = -1;
		boolean purchased = false;
		String text2 = action.equals("BUYING") ? "buy" : "sell";
		int interfaceIdOffset = action.equals("SELLING") ? 3 : 0;
		if (action.equals("BUYING")) {
			myTradingPostAmount = player.tradingPostBuyAmount;
			myTradingPostPrice = player.tradingPostBuyPrice;
		} else {
			myTradingPostAmount = player.tradingPostSellAmount;
			myTradingPostPrice = player.tradingPostSellPrice;
		}
		int currency = action.equals("SELLING") ? 7478 : ServerConstants.getMainCurrencyId();
		if (!skipRequirements) {
			int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
			if (currentTradingData >= 0) {
				player.getPA().sendMessage("You already have a " + text2 + " offer.");
				return false;
			}
			boolean canContinue = true;
			if (myTradingPostAmount == 0) {
				player.getPA().sendMessage("You need to set the " + text2 + " amount before confirming.");
				canContinue = false;
			}
			if (myTradingPostPrice == 0) {
				player.getPA().sendMessage("You need to set the " + text2 + " price before confirming.");
				canContinue = false;
			}
			if (!canContinue) {
				return false;
			}
			long currencyAmountRequired = action.equals("SELLING") ? myTradingPostAmount : myTradingPostAmount * myTradingPostPrice;
			if (ItemAssistant.getItemAmount(player, currency) < currencyAmountRequired) {
				player.getPA().sendMessage("You need " + Misc.formatNumber(currencyAmountRequired) + " " + ItemAssistant.getItemName(currency) + " to confirm.");
				if (!updateCurrentSectionInterfaces(player, action)) {
					resetCurrentSectionInterfaces(player, action);
				}
				return false;
			}
			ItemAssistant.deleteItemFromInventory(player, currency, (int) currencyAmountRequired);
			TradingPost.tradingPostData.add(new TradingPost(player.getLowercaseName(), action, (int) myTradingPostAmount, (int) myTradingPostPrice, System.currentTimeMillis(), 0,
			                                                (int) myTradingPostAmount));
			initiatorIndex = tradingPostData.size() - 1;
			debug(Misc.getDateAndTime() + "[" + player.getPlayerName() + "] Initiated a " + action + " delete: " + ItemAssistant.getItemName(currency) + "x " + Misc.formatNumber(
					currencyAmountRequired) + ". Other data:" + myTradingPostAmount + ", " + myTradingPostPrice + ", " + skipRequirements);
		} else {
			initiatorIndex = hasExistingOffer(player.getLowercaseName(), action);
			if (initiatorIndex == -1) {
				return false;
			}
			TradingPost initiatorData = tradingPostData.get(initiatorIndex);
			myTradingPostPrice = initiatorData.getPrice();
		}


		// Search for a seller/buyer to trade with.
		long dataTime = Long.MAX_VALUE;
		int tradingWithIndex = -1;
		long dataPriceDifference = Integer.MAX_VALUE; // difference of data price to my price.
		int currentIndex = -1;
		boolean tradePartnerExists = false;
		for (TradingPost data : tradingPostData) {
			currentIndex++;
			if (data.getAction().equals(action)) {
				continue;
			}
			if (data.getAmountTraded() == data.getInitialAmount()) {
				continue;
			}
			if (data.getName().equals(player.getLowercaseName())) {
				continue;
			}
			tradePartnerExists = true;
			if (action.equals("BUYING")) {
				if (data.getPrice() > myTradingPostPrice) {
					continue;
				}
			} else {
				if (data.getPrice() < myTradingPostPrice) {
					continue;
				}
			}
			// If available price is less than my price. Then check if difference is less than last difference.
			boolean check1 = action.equals("BUYING") ? data.getPrice() < myTradingPostPrice : data.getPrice() > myTradingPostPrice;
			long result1 = action.equals("BUYING") ? myTradingPostPrice - data.getPrice() : data.getPrice() - myTradingPostPrice;
			if (check1) {
				if (result1 < dataPriceDifference) {
					dataTime = data.getTime();
					dataPriceDifference = result1;
					tradingWithIndex = currentIndex;
				}
			}

			// If available price is equal to my buy price, then check if time is less than last time.
			else if (data.getPrice() == myTradingPostPrice) {
				if (data.getTime() < dataTime) {
					dataTime = data.getTime();
					dataPriceDifference = result1;
					tradingWithIndex = currentIndex;
				}
			}
		}

		// Trading partner found.
		if (tradingWithIndex >= 0) {
			TradingPost myData = tradingPostData.get(initiatorIndex);
			TradingPost tradingPartnerData = tradingPostData.get(tradingWithIndex);

			int stockToSubtract = tradingPartnerData.getItemAmountLeft();
			if (myData.getItemAmountLeft() < tradingPartnerData.getItemAmountLeft()) {
				stockToSubtract = myData.getItemAmountLeft();
			}
			if (stockToSubtract <= 0) {
				return false;
			}

			if (tradingWithIndex > initiatorIndex) {
				tradingPostData.remove(tradingWithIndex);
				tradingPostData.remove(initiatorIndex);
			} else {
				tradingPostData.remove(initiatorIndex);
				tradingPostData.remove(tradingWithIndex);
			}
			TradingPost.tradingPostData.add(new TradingPost(myData.getName(), myData.getAction(), myData.getItemAmountLeft() - stockToSubtract, myData.getPrice(), myData.getTime(),
			                                                myData.getAmountTraded() + stockToSubtract, myData.getInitialAmount()));
			TradingPost.tradingPostData.add(new TradingPost(tradingPartnerData.getName(), tradingPartnerData.getAction(), tradingPartnerData.getItemAmountLeft() - stockToSubtract,
			                                                tradingPartnerData.getPrice(), tradingPartnerData.getTime(), tradingPartnerData.getAmountTraded() + stockToSubtract,
			                                                tradingPartnerData.getInitialAmount()));
			if (action.equals("BUYING")) {
				buyHistory.add("[" + Misc.getDateAndTime() + "] " + ItemAssistant.getItemName(7478) + " x" + stockToSubtract + " for " + Misc.formatNumber(
						tradingPartnerData.getPrice()) + " each");
				TradingPostItems.tradingPostItemsData
						.add(new TradingPostItems("BOUGHT", myData.getName(), tradingPartnerData.getName(), 7478, stockToSubtract, tradingPartnerData.getPrice(), 0, 0));
			} else {
				buyHistory
						.add("[" + Misc.getDateAndTime() + "] " + ItemAssistant.getItemName(7478) + " x" + Misc.formatRunescapeStyle(stockToSubtract) + " for " + Misc.formatNumber(
								myData.getPrice()) + " each");
				TradingPostItems.tradingPostItemsData
						.add(new TradingPostItems("BOUGHT", tradingPartnerData.getName(), myData.getName(), 7478, stockToSubtract, myData.getPrice(), 0, 0));
			}

			// Give change to the buyer if i paid 1.9k for example and the seller is only selling for 1.8k.
			boolean giveChange = action.equals("BUYING") ? myData.getPrice() > tradingPartnerData.getPrice() : tradingPartnerData.getPrice() > myData.getPrice();
			if (giveChange) {
				int change = 0;
				change = action.equals("BUYING") ? myData.getPrice() - tradingPartnerData.getPrice() : tradingPartnerData.getPrice() - myData.getPrice();
				if (action.equals("BUYING")) {
					TradingPostItems.tradingPostItemsData
							.add(new TradingPostItems("CHANGE", myData.getName(), tradingPartnerData.getName(), ServerConstants.getMainCurrencyId(), stockToSubtract * change, 0, 0,
							                          0));
				} else {
					TradingPostItems.tradingPostItemsData
							.add(new TradingPostItems("CHANGE", tradingPartnerData.getName(), myData.getName(), ServerConstants.getMainCurrencyId(), stockToSubtract * change, 0, 0,
							                          0));
				}
			}
			//i buy 500, he sells 500
			//i buy 200 he sells 100
			//i buy 300 he sells 400
			// Stock is finished, so delete the seller's trade. Example buyer is buying 100 tokens, seller is selling 100 tokens.
			if (action.equals("BUYING")) {
				TradingPostItems.tradingPostItemsData.add(new TradingPostItems("SOLD", tradingPartnerData.getName(), myData.getName(), ServerConstants.getMainCurrencyId(),
				                                                               stockToSubtract * tradingPartnerData.getPrice(), tradingPartnerData.getPrice(), 0, stockToSubtract));
			} else {
				TradingPostItems.tradingPostItemsData
						.add(new TradingPostItems("SOLD", myData.getName(), tradingPartnerData.getName(), ServerConstants.getMainCurrencyId(), stockToSubtract * myData.getPrice(),
						                          myData.getPrice(), 0, stockToSubtract));
			}
			String text4 = action.equals("BUYING") ? "selling" : "buying";
			String text5 = action.equals("BUYING") ? "to" : "from";
			String text6 = action.equals("BUYING") ? "buying" : "selling";
			String text7 = action.equals("BUYING") ? "from" : "to";
			String playerName = myData.getName();
			player.getPA().sendMessage(
					ServerConstants.BLUE_COL + "Trading post: Finished " + text6 + " x" + Misc.formatNumber(stockToSubtract) + " Donator tokens " + text7 + ": " + Misc.capitalize(
							tradingPartnerData.getName()) + ".");
			updateInterfaceForTradedWith(player, tradingPartnerData.getName(),
			                             "Finished " + text4 + " x" + Misc.formatNumber(stockToSubtract) + " Donator tokens " + text5 + ": " + Misc.capitalize(playerName) + ".");
			purchased = true;
		} else {
			String text = action.equals("BUYING") ? "sellers" : "buyers";
			String text4 = action.equals("BUYING") ? "low" : "high";
			if (!tradePartnerExists) {
				player.getPA().sendFrame126("Status: @whi@no " + text + " found", 28959 + interfaceIdOffset);
			} else {
				player.getPA().sendFrame126("Status: @whi@price set " + text4, 28959 + interfaceIdOffset);
				player.getPA().sendFrame126("@whi@waiting for offers", action.equals("BUYING") ? 14099 : 14098);
			}
		}
		player.tradingPostBuyAmount = 0;
		player.tradingPostBuyPrice = 0;
		player.tradingPostSellAmount = 0;
		player.tradingPostSellPrice = 0;
		return purchased;
	}

	/**
	 * Update the interface for the other player i traded with.
	 *
	 * @param name
	 */
	private static void updateInterfaceForTradedWith(Player player, String name, String string) {
		if (player.getActionIdUsed() == 28950) {
			if (!updateCurrentSectionInterfaces(player, "BUYING")) {
				resetCurrentSectionInterfaces(player, "BUYING");
			}
			if (!updateCurrentSectionInterfaces(player, "SELLING")) {
				resetCurrentSectionInterfaces(player, "SELLING");
			}
		}
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getLowercaseName().equals(name)) {
				loop.getPA().sendMessage(ServerConstants.BLUE_COL + "Trading post: " + string);
				if (loop.getActionIdUsed() == 28950) {
					if (!updateCurrentSectionInterfaces(loop, "BUYING")) {
						resetCurrentSectionInterfaces(loop, "BUYING");
					}
					if (!updateCurrentSectionInterfaces(loop, "SELLING")) {
						resetCurrentSectionInterfaces(loop, "SELLING");
					}
				}
				break;
			}
		}

	}

	/**
	 * Debug, will be used to log.
	 */
	private static void debug(String string) {
		debug.add(string);

	}

	public static boolean TRADING_POST_DISABLED = false;

	/**
	 * Display trading post interface.
	 */
	public static void displayTradingPost(Player player) {
		if (TRADING_POST_DISABLED) {
			player.getPA().closeInterfaces(true);
			player.getPA().sendMessage("The trading post is currently disabled.");
			return;
		}
		if (!Bank.hasBankingRequirements(player, true)) {
			player.getPA().closeInterfaces(true);
			return;
		}
		player.setActionIdUsed(28950);
		if (!updateCurrentSectionInterfaces(player, "BUYING")) {
			resetCurrentSectionInterfaces(player, "BUYING");
		}
		if (!updateCurrentSectionInterfaces(player, "SELLING")) {
			resetCurrentSectionInterfaces(player, "SELLING");
		}
		showAveragePrice(player);
		updateHistory(player);
		player.getPA().displayInterface(28950);
	}

	public static void tokensBeingPurchasedMessage() {
		int tokensBeingBought = 0;
		int highestPrice = 0;
		for (TradingPost data : tradingPostData) {
			if (data.getAction().equals("SELLING")) {
				continue;
			}
			if (data.getAmountTraded() == data.getInitialAmount()) {
				continue;
			}
			if (data.getPrice() > highestPrice) {
				highestPrice = data.getPrice();
			}
			tokensBeingBought += data.getItemAmountLeft();
		}

		if (tokensBeingBought > 50) {
			Announcement.announce(
					"::donate quickly and sell " + Misc.formatNumber(tokensBeingBought) + " Donator tokens for " + Misc.formatNumber(highestPrice) + " each at the Trading post!",
					"<img=20><col=0000ff>");
			Announcement.announce("Talk to the Donator assistant at ::shops", "<img=20><col=0000ff>");
		} else {
			Announcement.donateAnnouncementAction();
		}
	}

	/**
	 * Show the buy interface frames with the TradingPost data
	 *
	 * @param player
	 * @return
	 */
	private static boolean updateCurrentSectionInterfaces(Player player, String action) {
		String text = action.equals("BUYING") ? "sellers" : "buyers";
		int tradingPostIndex = TradingPost.hasExistingOffer(player.getLowercaseName(), action);
		int interfaceIdOffset = action.equals("SELLING") ? 3 : 0;
		if (tradingPostIndex >= 0) {
			TradingPost playerTradingPostData = tradingPostData.get(tradingPostIndex);
			player.getPA().sendFrame126("@gr3@" + playerTradingPostData.getAmountTraded() + "/" + playerTradingPostData.getInitialAmount() + "", 28957 + interfaceIdOffset);
			player.getPA().sendFrame126("Price: @gr3@" + playerTradingPostData.getPrice(), 28958 + interfaceIdOffset);
			String status = "@whi@no " + text + " found";
			for (TradingPost data : tradingPostData) {
				if (data.getAction().equals(action)) {
					continue;
				}
				if (data.getAmountTraded() == data.getInitialAmount()) {
					continue;
				}
				if (action.equals("BUYING")) {
					if (data.getPrice() > playerTradingPostData.getPrice()) {
						status = "@whi@price set low";
						player.getPA().sendFrame126("@whi@waiting for offers", 14099);
						break;
					}
				} else {
					if (data.getPrice() < playerTradingPostData.getPrice()) {
						status = "@whi@price set high";
						player.getPA().sendFrame126("@whi@waiting for offers", 14098);
						break;
					}
				}
			}
			boolean itemsToClaim = false;
			for (TradingPostItems itemsData : TradingPostItems.tradingPostItemsData) {
				if (itemsData.getName().equals(player.getLowercaseName())) {
					player.getPA().sendFrame126("@gr3@Items to claim", 28983 + interfaceIdOffset);
					itemsToClaim = true;
					break;
				}
			}
			if (!itemsToClaim) {
				player.getPA().sendFrame126("Nothing to claim.", 28983 + interfaceIdOffset);
			}
			if (playerTradingPostData.getAmountTraded() == playerTradingPostData.getInitialAmount()) {
				status = "@gr3@completed";
			}
			player.getPA().sendFrame126("Status: " + status, 28959 + interfaceIdOffset);
			return true;
		}
		return false;
	}

	/**
	 * Show the average price on the interface frame.
	 */
	private static void showAveragePrice(Player player) {
		player.getPA().sendFrame126("Donator tokens trading post: (avrg x" + averagePrice + " bm)", 28952);

	}

	/**
	 * Reset the interface ids in the Buy section.
	 */
	public static void resetCurrentSectionInterfaces(Player player, String action) {
		int interfaceIdOffset = action.equals("SELLING") ? 3 : 0;
		player.getPA().sendFrame126("none", 28957 + interfaceIdOffset);
		player.getPA().sendFrame126("Price: N/A", 28958 + interfaceIdOffset);
		player.getPA().sendFrame126("Status: empty", 28959 + interfaceIdOffset);
		player.getPA().sendFrame126("Nothing to claim", 28983 + interfaceIdOffset);
		player.getPA().sendFrame126("", action.equals("BUYING") ? 14099 : 14098);
	}

	/**
	 * Returns >= 0 if the given name has an existing offer with the matching action in the Trading post. The number given is also the index in the TradingPost list.
	 * Returns -1 if no offer is found.
	 */
	private static int hasExistingOffer(String lowercaseName, String action) {
		int index = 0;
		for (TradingPost data : TradingPost.tradingPostData) {
			if (data.getName().equals(lowercaseName) && data.getAction().equals(action)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Edit amount dialogue option on buying section.
	 */
	public static void editAmountDialogueOption(Player player, String action) {
		int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
		String string = action.equals("BUYING") ? "buy" : "sell";
		if (currentTradingData >= 0) {
			player.getPA().sendMessage("You already have a " + string + " offer.");
			return;
		}
		InterfaceAssistant.showAmountInterface(player, "TRADING POST " + action + " EDIT AMOUNT", "Enter amount");
	}


	/**
	 * The value entered into the dialogue interface when asked for the edit amount.
	 */
	public static void editAmountXEntered(Player player, int xAmount, String action) {
		int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
		String string = action.equals("BUYING") ? "buy" : "sell";
		int interfaceIdOffset = action.equals("SELLING") ? 3 : 0;
		if (currentTradingData >= 0) {
			player.getPA().sendMessage("You already have a " + string + " offer.");
			return;
		}
		if (action.equals("BUYING")) {
			player.tradingPostBuyAmount = xAmount;
		} else {
			player.tradingPostSellAmount = xAmount;
		}
		player.getPA().sendFrame126(xAmount + "", 28957 + interfaceIdOffset);
		player.getPA().sendFrame126("Status: @red@unconfirmed", 28959 + interfaceIdOffset);
		player.hasDialogueOptionOpened = false;
		player.getDH().sendDialogues(473 + (action.equals("SELLING") ? 1 : 0));
	}


	/**
	 * Edit price dialogue option on buying section.
	 */
	public static void editPriceDialogueOption(Player player, String action) {
		int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
		String string = action.equals("BUYING") ? "buy" : "sell";
		if (currentTradingData >= 0) {
			player.getPA().sendMessage("You already have a " + string + " offer.");
			return;
		}
		InterfaceAssistant.showAmountInterface(player, "TRADING POST " + action + " EDIT PRICE", "Enter amount");
	}

	/**
	 * Edit the price of the buying section.
	 *
	 * @param player
	 * @param xAmount
	 */
	public static void editPriceXEntered(Player player, int xAmount, String action) {
		if (xAmount > averagePrice * 3) {
			player.getPA().sendMessage("The maximum price you may enter is " + averagePrice * 3 + ".");
			InterfaceAssistant.closeDialogueOnly(player);
			if (!updateCurrentSectionInterfaces(player, action)) {
				resetCurrentSectionInterfaces(player, action);
			}
			return;
		}
		if (xAmount < 801) {
			player.getPA().sendMessage("The minimum price you may enter is 801.");
			InterfaceAssistant.closeDialogueOnly(player);
			if (!updateCurrentSectionInterfaces(player, action)) {
				resetCurrentSectionInterfaces(player, action);
			}
			return;
		}
		int currentTradingData = hasExistingOffer(player.getLowercaseName(), action);
		String string = action.equals("BUYING") ? "buy" : "sell";
		int interfaceIdOffset = action.equals("SELLING") ? 3 : 0;
		if (currentTradingData >= 0) {
			player.getPA().sendMessage("You already have a " + string + " offer.");
			return;
		}
		if (action.equals("BUYING")) {
			player.tradingPostBuyPrice = xAmount;
		} else {
			player.tradingPostSellPrice = xAmount;
		}
		player.getPA().sendFrame126("Price: @gr3@" + xAmount, 28958 + interfaceIdOffset);
		player.getPA().sendFrame126("Status: @red@unconfirmed", 28959 + interfaceIdOffset);
		player.hasDialogueOptionOpened = false;
		player.getDH().sendDialogues(473 + (action.equals("SELLING") ? 1 : 0));
	}

	/**
	 * Close dialogue option on buying section.
	 */
	public static void closeButton(Player player) {
		InterfaceAssistant.closeDialogueOnly(player);
	}


	/**
	 * Cancel the offer of the buying section.
	 *
	 * @param player
	 */
	public static void cancelOfferButton(Player player, String action) {
		TradingPost.claimItemsButton(player, action);
		int playerTradingPostAmount = action.equals("BUYING") ? player.tradingPostBuyAmount : player.tradingPostSellAmount;
		int playerTradingPostPrice = action.equals("BUYING") ? player.tradingPostBuyPrice : player.tradingPostSellPrice;
		String string = action.equals("BUYING") ? "Buy" : "Sell";
		boolean entered = false;
		if (playerTradingPostAmount > 0 || playerTradingPostPrice > 0) {
			if (action.equals("BUYING")) {
				player.tradingPostBuyAmount = 0;
				player.tradingPostBuyPrice = 0;
			} else {
				player.tradingPostSellAmount = 0;
				player.tradingPostSellPrice = 0;
			}
			resetCurrentSectionInterfaces(player, action);
			player.hasDialogueOptionOpened = false;
			player.getDH().sendDialogues(473 + (action.equals("SELLING") ? 1 : 0));
			entered = true;
		}
		int currentTradingPostIndex = TradingPost.hasExistingOffer(player.getLowercaseName(), action);
		if (currentTradingPostIndex >= 0) {
			TradingPost data = tradingPostData.get(currentTradingPostIndex);
			int currency = action.equals("BUYING") ? ServerConstants.getMainCurrencyId() : 7478;
			int currencyAmount = action.equals("BUYING") ? data.getItemAmountLeft() * data.getPrice() : data.getItemAmountLeft();
			if (data != null) {
				if (!ItemAssistant.addItem(player, currency, currencyAmount)) {
					ItemAssistant.addItemReward(player, player.getPlayerName(), currency, currencyAmount, false, -1);
				}
				debug(Misc.getDateAndTime() + "[" + player.getPlayerName() + "] cancelOfferButton added: " + ItemAssistant.getItemName(currency) + " x" + Misc.formatNumber(
						currencyAmount));
				tradingPostData.remove(currentTradingPostIndex);
				resetCurrentSectionInterfaces(player, action);
				InterfaceAssistant.closeDialogueOnly(player);
				player.getPA().sendMessage(
						ServerConstants.BLUE_COL + "Trading post: " + string + " offer cancelled, collected x" + Misc.formatNumber(currencyAmount) + " " + ItemAssistant
								                                                                                                                                   .getItemName(
										                                                                                                                                   currency)
						+ ".");
			}
			entered = true;
		}
		if (!entered) {
			InterfaceAssistant.closeDialogueOnly(player);
		}
	}

	/**
	 * Location of TradingPost text file save.
	 */
	private final static String LOCATION = "backup/logs/trading_post/offers.txt";

	/**
	 * Read the TradingPost text file save.
	 */
	public static void readTradingPostOffers() {
		TradingPost.tradingPostData.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader(EconomyScan.tradingPostOfferLocation.isEmpty() ? LOCATION : EconomyScan.tradingPostOfferLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
					String name = parse[0];
					String action = parse[1];
					int amount = Integer.parseInt(parse[2]);
					int price = Integer.parseInt(parse[3]);
					long time = Long.parseLong(parse[4]);
					int amountTraded = Integer.parseInt(parse[5]);
					int initialAmount = Integer.parseInt(parse[6]);
					// in offers.txt
					//prays#=#SELLING#=#0#=#3300#=#1508521516484#=#190#=#190
					TradingPost.tradingPostData.add(new TradingPost(name, action, amount, price, time, amountTraded, initialAmount));
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save TradingPost list data into a text file.
	 */
	public static void saveTradingPostOffers() {
		FileUtility.deleteAllLines(LOCATION);
		ArrayList<String> line = new ArrayList<String>();
		for (TradingPost data : TradingPost.tradingPostData) {
			line.add(data.getName() + ServerConstants.TEXT_SEPERATOR + data.getAction() + ServerConstants.TEXT_SEPERATOR + data.getItemAmountLeft() + ServerConstants.TEXT_SEPERATOR
			         + data.getPrice() + ServerConstants.TEXT_SEPERATOR + data.getTime() + ServerConstants.TEXT_SEPERATOR + data.getAmountTraded() + ServerConstants.TEXT_SEPERATOR
			         + data.getInitialAmount());
		}
		FileUtility.saveArrayContentsSilent(LOCATION, line);
	}

	public static void updateHistory(Player player) {
		//28992
		//Sold x10 Donator token for 2,000 each to: Mgt Madness.
		InterfaceAssistant.clearFrames(player, 28992, 28992 + 19);
		//S-10-7478-2000-mgt madness
		int increment = 0;
		for (int index = player.tradingPostHistory.size() - 1; index >= 0; index--)
		//for (int index = 0; index < player.tradingPostHistory.size(); index++)
		{
			String parse[] = player.tradingPostHistory.get(index).split("-");
			String text = "";
			String toOrFrom = "to";
			if (parse[0].equals("S")) {
				text = parse[0].replace("S", "Sold");
			} else {
				text = parse[0].replace("B", "Purchased");
				toOrFrom = "from";
			}
			player.getPA().sendFrame126(
					text + " x" + Misc.formatNumber(Integer.parseInt(parse[1])) + " " + ItemAssistant.getItemName(Integer.parseInt(parse[2])) + " for " + Misc.formatNumber(
							Integer.parseInt(parse[3])) + " each " + toOrFrom + ": " + Misc.capitalize(parse[4]) + ".", 28992 + increment);
			increment++;
		}
	}

	public static void serverSave() {
		FileUtility.saveArrayContentsSilent("backup/logs/trading_post/debug.txt", TradingPost.debug);
		TradingPost.debug.clear();
		FileUtility.saveArrayContentsSilent("backup/logs/trading_post/buy_history.txt", TradingPost.buyHistory);
		TradingPost.buyHistory.clear();
		TradingPost.saveTradingPostOffers();
		TradingPostItems.saveTradingPostItems();
	}
}
