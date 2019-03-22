package game.content.interfaces.donator;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.donator.MysteryBox;
import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.GameTimeSpent;
import game.content.shop.ShopAssistant;
import game.content.tradingpost.TradingPost;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import java.util.List;
import network.connection.DonationManager;
import utility.FileUtility;
import utility.Misc;

public class DonatorMainTab {

	private final static int PERCENTAGE_DISCOUNT = 25;

	private final static int MINIMUM_HOURS_PER_WEEK_PLAYED = 5;

	private final static int DAYS_BANNED_FROM_OFFERS_ON_OTHER_ACCOUNTS = 10;

	/**
	 * If its set to 35$, It will offer the player 35$ donation goal, but if the player keeps ignoring the offer by not completing it, it will eventually go down to 5$.
	 */
	private final static int MINIMUM_AVERAGE_PAYMENT = 35;

	/**
	 * Days inactive, percentage reward increase.
	 */
	private final static int[][] DAYS_INACTIVE_AND_REWARD_MULTIPLIER =
			{
					{5, 25},
					{10, 50},
					{15, 80},
					{20, 120},
					{30, 160},
					{40, 200},
					{50, 250},
					{60, 300},
					{70, 350},
					{80, 400},
					{90, 450},
					{100, 500},
			};

	private final static String FILE_LOCATION = "backup/logs/donations/on_offer.txt";

	public static List<DonatorShop> itemsOnOfferList = new ArrayList<DonatorShop>();

	public static ArrayList<Integer> chosenIndexesOnOffer = new ArrayList<Integer>();

	public static ArrayList<String> accountOfferLog = new ArrayList<String>();

	public static boolean isDonatorInterfaceMainTabButton(Player player, int buttonId) {

		switch (buttonId) {
			// Purchase tokens
			case 129022:
				player.getPA().openWebsite("www.dawntained.com/store", false);
				return true;
			// View mystery box rewards
			case 129027:
				player.getPA().openWebsite("www.dawntained.com/forum/topic/1010-storepayment-faq/?tab=comments#comment-6874", false);
				return true;
			// Claim donation rewards
			case 129047:
				DonationManager.claimDonation(player);
				return true;
			// Claim account offer rewards
			case 129052:
				claimAccountRewards(player, true);
				return true;
			// Donator notification button.
			case 129177:
				donatorNotificationButton(player);
				return true;
		}
		if (buttonId >= 128234 && buttonId <= 128249 || buttonId >= 129000 && buttonId <= 129015) {
			int itemIndex = 0;
			int optionIndex = 0;
			if (buttonId >= 128234 && buttonId <= 128249) {
				itemIndex = (buttonId - 128234) / 11;
				optionIndex = (buttonId - 128234) - (itemIndex * 11);
			} else if (buttonId >= 129000 && buttonId <= 129015) {
				itemIndex = (buttonId - 129000) / 11;
				optionIndex = (buttonId - 129000) - (itemIndex * 11);
				itemIndex += 5;
			}
			if (itemIndex == 5) {
				itemIndex = 2;
			}
			if (itemIndex == 6) {
				itemIndex = 3;
			}
			DonatorShop instance = DonatorShop.donatorShopList.get(chosenIndexesOnOffer.get(itemIndex));
			if (instance == null) {
				return true;
			}
			switch (optionIndex) {
				case 0:
					DonatorShop.valueItemDonatorShop(player, instance, getPriceAfterDiscount(instance.getOriginalPrice()));
					break;
				case 1:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 1, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            getPriceAfterDiscount(instance.getOriginalPrice()));
					break;
				case 2:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 5, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            getPriceAfterDiscount(instance.getOriginalPrice()));
					break;
				case 3:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 10, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            getPriceAfterDiscount(instance.getOriginalPrice()));
					break;
				case 4:
					player.xRemoveSlot = itemIndex;
					InterfaceAssistant.showAmountInterface(player, "DONATOR SHOP NORMAL", "Enter amount");
					break;
			}
			return true;
		}
		return false;
	}

	private static void donatorNotificationButton(Player player) {
		if (!Bank.hasBankingRequirements(player, true)) {
			player.getPA().sendMessage("You cannot do this now.");
			return;
		}
		player.accountOfferNotificationPopUpTime = System.currentTimeMillis();
		player.getPA().sendMessage(":packet:donatornotificationoff");
		DonatorMainTab.loadMainTab(player);
	}

	public static void rotateItemsOnOfferList(boolean searchForPlayersToUpdate) {

		if (!chosenIndexesOnOffer.isEmpty()) {
			for (int index = 0; index < chosenIndexesOnOffer.size(); index++) {
				DonatorShop instance = DonatorShop.donatorShopList.get(chosenIndexesOnOffer.get(index));
				instance.setOnDonatorInterfaceMainTabOffer(false);
			}
		}
		chosenIndexesOnOffer.clear();
		for (int index = 0; index < 4; index++) {
			boolean chosen = false;
			while (!chosen) {
				int random = Misc.random(DonatorShop.normalShopIndexStart, DonatorShop.donatorShopList.size() - 1);
				if (!chosenIndexesOnOffer.contains(random)) {
					chosenIndexesOnOffer.add(random);
					chosen = true;
					DonatorShop instance = DonatorShop.donatorShopList.get(chosenIndexesOnOffer.get(index));
					instance.setOnDonatorInterfaceMainTabOffer(true);
				}
			}
		}
		if (searchForPlayersToUpdate) {
			Announcement.announce("4 new items have now gone on discount for 24 hours! Check ::store", "<img=20><col=0000ff>");
			for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.getInterfaceIdOpened() == 33000) {
					loadMainTab(loop);
				}
			}
		}
	}

	public static void readItemsOnOfferFile() {
		chosenIndexesOnOffer = FileUtility.readFileInteger(FILE_LOCATION);
		if (chosenIndexesOnOffer.isEmpty()) {
			rotateItemsOnOfferList(false);
		} else {
			for (int index = 0; index < chosenIndexesOnOffer.size(); index++) {
				DonatorShop instance = DonatorShop.donatorShopList.get(chosenIndexesOnOffer.get(index));
				instance.setOnDonatorInterfaceMainTabOffer(true);
			}
		}
	}

	public static void saveItemsOnOfferFile() {
		FileUtility.deleteAllLines(FILE_LOCATION);
		FileUtility.saveArrayContentsSilent(FILE_LOCATION, chosenIndexesOnOffer);
	}

	public static int getPriceAfterDiscount(int originalPrice) {

		double price = originalPrice;
		double modifier = 1.0 - (PERCENTAGE_DISCOUNT / 100.0);
		price *= modifier;
		return (int) price;
	}

	public static void loadMainTab(Player player) {
		int interfaceId = 33002;
		for (int index = 0; index < chosenIndexesOnOffer.size(); index++) {
			DonatorShop instance = DonatorShop.donatorShopList.get(chosenIndexesOnOffer.get(index));
			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 995);
			interfaceId += 5;

			int itemId = Integer.parseInt(instance.getItemReward()[0]);
			player.getPA().sendFrame34(interfaceId, itemId, 0, 1);
			interfaceId++;

			player.getPA().sendFrame126(instance.getOriginalPrice() + " tokens", interfaceId);
			interfaceId++;

			player.getPA().sendFrame126(getPriceAfterDiscount(instance.getOriginalPrice()) + " tokens", interfaceId);
			interfaceId++;

			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 982);
			interfaceId++;

			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 984);
			interfaceId++;

			player.getPA().sendFrame126(PERCENTAGE_DISCOUNT + "% discount", interfaceId);
			interfaceId++;
		}
		long targetTime = Misc.dateToMilliseconds(Misc.getDateOnlySlashes() + ", 12:00: AM", "dd/MM/yyyy, hh:mm: a") + Misc.getHoursToMilliseconds(24);
		targetTime = targetTime - System.currentTimeMillis();
		targetTime /= 1000;
		if (targetTime < 0) {
			targetTime = 0;
		}
		InterfaceAssistant.setTextCountDownSecondsLeft(player, 33001, (int) targetTime);
		player.getPA().setInterfaceClicked(31000, 31002, true);
		player.lastDonatorShopTabOpened = 1;
		ItemAssistant.resetItems(player, 3823); // Spawning items while in shop. Must be kept here to update inventory.
		updateAccountOffer(player, true);
		updateAccountDonatorProgress(player);
		player.getPA().displayInterface(33000);
		InterfaceAssistant.setInventoryOverlayId(player, 3822);
	}

	private static void updateAccountDonatorProgress(Player player) {
		int highestRankIndex = -1;
		for (DonatorRankSpentData data : DonatorRankSpentData.values()) {
			if (player.donatorTokensRankUsed >= data.getTokensRequired()) {
				highestRankIndex = data.ordinal();
			}
		}
		DonatorRankSpentData nextRankInstance = null;
		DonatorRankSpentData myCurrentRankInstance = null;
		int currentRankTokensRequired = 0;
		int currentRankPlayerRights = 0;
		String currentRankName = "player";
		if (highestRankIndex >= 0) {
			myCurrentRankInstance = DonatorRankSpentData.values()[highestRankIndex];

			currentRankTokensRequired = myCurrentRankInstance.getTokensRequired();
			currentRankPlayerRights = myCurrentRankInstance.getPlayerRights();
			currentRankName = DonatorTokenUse.getDonatorRankName(myCurrentRankInstance);
		}
		if (highestRankIndex + 1 > DonatorRankSpentData.values().length - 1) {
			nextRankInstance = myCurrentRankInstance;
		} else {
			nextRankInstance = DonatorRankSpentData.values()[highestRankIndex + 1];
		}
		int tokensLeftToRankUp = nextRankInstance.getTokensRequired() - player.donatorTokensRankUsed;
		int nextRankAt = nextRankInstance.getTokensRequired();
		double percentage = 0.0;
		percentage = 100.0 - (((double) tokensLeftToRankUp / (double) (nextRankAt - currentRankTokensRequired)) * 100.0);
		if (nextRankInstance == myCurrentRankInstance) {
			percentage = 100;
			tokensLeftToRankUp = 0;
		}
		player.getPA().sendFrame126("<img=" + currentRankPlayerRights + "> > <img=" + nextRankInstance.getPlayerRights() + ">", 33068);
		player.getPA().sendFrame126("You have consumed " + Misc.formatNumber(player.donatorTokensRankUsed) + " tokens as " + Misc.getAorAn(currentRankName) + ".", 33069);
		player.getPA().sendFrame126(
				Misc.formatNumber(tokensLeftToRankUp) + " tokens left to " + DonatorTokenUse.getDonatorRankName(nextRankInstance) + ", " + (int) percentage + "% complete", 33070);
		InterfaceAssistant.setSpriteLoadingPercentage(player, 33067, (int) percentage);
	}

	private static int getAveragePaymentHistory(Player player) {
		int total = 0;
		for (int index = 0; index < player.donationPriceClaimedHistory.size(); index++) {
			String[] parse = player.donationPriceClaimedHistory.get(index).split("-");
			total += Integer.parseInt(parse[0]);
		}
		if (total > 0) {
			return total / player.donationPriceClaimedHistory.size();
		}
		return 0;
	}

	public static void updateAccountOffer(Player player, boolean action) {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		long daysSinceLastPayment = (System.currentTimeMillis() - player.getTimeLastClaimedDonation()) / Misc.getHoursToMilliseconds(24);
		int averagePaymentHistory = getAveragePaymentHistory(player);
		if (averagePaymentHistory < MINIMUM_AVERAGE_PAYMENT) {
			averagePaymentHistory = MINIMUM_AVERAGE_PAYMENT;
		}
		int recommendedPayment = 0;
		int recommendedPaymentIndex = -1;
		for (int index = 0; index < DonationManager.BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT.length; index++) {
			if (averagePaymentHistory >= DonationManager.BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[index][1]) {
				recommendedPayment = (int) DonationManager.BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[index][1];
				recommendedPaymentIndex = index;
			}
		}

		boolean hide = true;
		boolean getNewOffer = false;

		if (System.currentTimeMillis() - player.timeAccountOfferShown > Misc.getHoursToMilliseconds(24) && !player.accountOfferCompleted && player.accountOfferClaimTargetGoal > 0) {
			claimAccountRewards(player, false); // Cancel the offer.
		}

		if (player.accountOfferCompleted) {
			hide = false;
		}
		if (daysSinceLastPayment >= DAYS_INACTIVE_AND_REWARD_MULTIPLIER[0][0] || player.accountOfferClaimTargetGoal > 0) {
			hide = false;
			// Means that the player recently completed an offer.
			if (System.currentTimeMillis() - player.timeAccountOfferShown < Misc.getHoursToMilliseconds(48) && !player.accountOfferCompleted
			    && player.accountOfferClaimTargetGoal == 0) {
				hide = true;
			}
			if (System.currentTimeMillis() - player.timeAccountOfferShown > Misc.getHoursToMilliseconds(48)) {
				if (GameTimeSpent.getActivePlayTimeHoursInLastWeek(player) > MINIMUM_HOURS_PER_WEEK_PLAYED) {
					getNewOffer = true;
					if (AccountOffersGiven.isOnAnotherAccount(player)) {
						getNewOffer = false;
						hide = true;
					}
				} else {
					if (!player.accountOfferCompleted && player.accountOfferClaimTargetGoal == 0) {
						hide = true;
					}
				}
			}
		}
		if (hide) {

			if (action) {
				InterfaceAssistant.changeInterfaceSprite(player, 33059, 958);
				player.getPA().sendFrame126("No account offer right now.", 33060);
				player.getPA().sendFrame126("", 33061);
				InterfaceAssistant.setTextCountDownSecondsLeft(player, 33063, 0);
				player.getPA().sendFrame126("", 33063);
				player.getPA().sendFrame126("", 33064);
				player.getPA().sendFrame126("", 33065);
				InterfaceAssistant.setSpriteLoadingPercentage(player, 33057, 0);
				player.getPA().sendFrame126("", 33062);
			}
		}
		//Show offer
		else {
			if (!action) {
				if (System.currentTimeMillis() - player.accountOfferNotificationPopUpTime < Misc.getHoursToMilliseconds(12)) {
					return;
				}
				player.getPA().sendMessage(":packet:donatornotification");
				return;
			}
			if (getNewOffer) {
				findNewAccountOffer(player, (int) daysSinceLastPayment, recommendedPayment, recommendedPaymentIndex);
			}
			InterfaceAssistant.changeInterfaceSprite(player, 33059, 1006);
			String osrsPaymentAmount = (int) Misc.getDoubleRoundedUp(player.accountOfferClaimTargetGoal / DonationManager.OSRS_DONATION_MULTIPLIER) + "m osrs";
			player.getPA().sendFrame126("Donate " + player.accountOfferClaimTargetGoal + "$ or " + osrsPaymentAmount + " to receive", 33060);
			player.getPA().sendFrame126("x" + Misc.formatNumber(player.accountOfferRewardItemAmount) + " " + ItemAssistant.getItemName(player.accountOfferRewardItemId) + " as a bonus reward!", 33061);
			int percentage = (int) (((double) player.accountOfferClaimTargetProgress / (double) player.accountOfferClaimTargetGoal) * 100.0);
			InterfaceAssistant.setSpriteLoadingPercentage(player, 33057, percentage);
			double usdLeft = (double) player.accountOfferClaimTargetGoal - player.accountOfferClaimTargetProgress;
			usdLeft = Misc.roundDoubleToNearestOneDecimalPlace(usdLeft);
			if (usdLeft < 0) {
				usdLeft = 0;
			}
			String osrsPaymentExactlyLeft = (int) Misc.getDoubleRoundedUp(usdLeft / DonationManager.OSRS_DONATION_MULTIPLIER) + "m osrs";
			player.getPA().sendFrame126(usdLeft + "$ or " + osrsPaymentExactlyLeft + " payment left, " + percentage + "% complete", 33062);
			long timeLeft = (Misc.getHoursToMilliseconds(24) - (System.currentTimeMillis() - player.timeAccountOfferShown)) / 1000;
			InterfaceAssistant.setTextCountDownSecondsLeft(player, 33063, (int) timeLeft);
			player.getPA().sendFrame126("until account", 33064);
			player.getPA().sendFrame126("offer expires", 33065);
		}
	}

	public static class AccountOffersGiven {

		public static List<AccountOffersGiven> accountOffersGiven = new ArrayList<AccountOffersGiven>();

		/**
		 * Save the details of a player and the time the player received the account offer.
		 * <p>
		 * Used to make sure the player does not get another offer for 5 days on any account. However, he can still receive an offer on the name he was originally offered on.
		 */
		public AccountOffersGiven(String ip, String uid, String name, long timeOfferOccured) {
			this.ip = ip;
			this.uid = uid;
			this.name = name;
			this.timeOfferOccured = timeOfferOccured;
		}

		private String ip = "";

		private String uid = "";

		public String name = "";

		private long timeOfferOccured;

		/**
		 * True if the player is attempting to get the offer within a {@link DonatorMainTab#DAYS_BANNED_FROM_OFFERS_ON_OTHER_ACCOUNTS} day period of receiving it on another account.
		 */
		public static boolean isOnAnotherAccount(Player player) {
			for (int index = 0; index < accountOffersGiven.size(); index++) {
				AccountOffersGiven instance = accountOffersGiven.get(index);
				long timeSinceOfferGiven = System.currentTimeMillis() - instance.timeOfferOccured;
				if (timeSinceOfferGiven > Misc.getHoursToMilliseconds(24) * DAYS_BANNED_FROM_OFFERS_ON_OTHER_ACCOUNTS) {
					accountOffersGiven.remove(index);
					index--;
					continue;
				}
				if (player.addressIp.equals(instance.ip) || Misc.uidMatches(player.addressUid, instance.uid)) {
					if (player.getPlayerName().equals(instance.name)) {
						return false;
					}
					return true;
				}
			}
			return false;
		}

		private final static String FILE_LOCATION = "backup/logs/donations/account_offers_given.txt";

		public static void saveAccountOffersGiven() {
			ArrayList<String> text = new ArrayList<String>();
			for (int index = 0; index < accountOffersGiven.size(); index++) {
				AccountOffersGiven instance = accountOffersGiven.get(index);
				text.add(instance.ip + ServerConstants.TEXT_SEPERATOR + instance.uid + ServerConstants.TEXT_SEPERATOR + instance.name + ServerConstants.TEXT_SEPERATOR
				         + instance.timeOfferOccured);
			}
			FileUtility.deleteAllLines(FILE_LOCATION);
			FileUtility.saveArrayContentsSilent(FILE_LOCATION, text);
		}

		public static void readAccountOffersGivenFile() {
			ArrayList<String> text = FileUtility.readFile(FILE_LOCATION);
			for (int index = 0; index < text.size(); index++) {
				String[] parse = text.get(index).split(ServerConstants.TEXT_SEPERATOR);
				accountOffersGiven.add(new AccountOffersGiven(parse[0], parse[1], parse[2], Long.parseLong(parse[3])));
			}
		}
	}

	private static void findNewAccountOffer(Player player, int daysSinceLastPayment, int recommendedPayment, int recommendedPaymentIndex) {
		for (int index = 0; index < AccountOffersGiven.accountOffersGiven.size(); index++) {
			AccountOffersGiven instance = AccountOffersGiven.accountOffersGiven.get(index);
			if (instance.name.equals(player.getPlayerName())) {
				AccountOffersGiven.accountOffersGiven.remove(index);
				break;
			}
		}
		AccountOffersGiven.accountOffersGiven.add(new AccountOffersGiven(player.addressIp, player.addressUid, player.getPlayerName(), System.currentTimeMillis()));
		player.timeAccountOfferShown = System.currentTimeMillis();
		int level = -1;
		if (player.accountOffersSkippedStreak > 0) {
			level = player.accountOffersSkippedStreak / 4;
		}
		if (recommendedPayment == 0) {
			recommendedPayment = (int) DonationManager.BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[0][1];
			recommendedPaymentIndex = 0;
		}
		if (recommendedPaymentIndex > 0 && level >= 1) {
			recommendedPaymentIndex -= level;
			if (recommendedPaymentIndex < 0) {
				recommendedPaymentIndex = 0;
			}
			recommendedPayment = (int) DonationManager.BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[recommendedPaymentIndex][1];
		}
		player.accountOfferClaimTargetGoal = recommendedPayment;
		int rewardMultiplier = 0;
		for (int index = 0; index < DAYS_INACTIVE_AND_REWARD_MULTIPLIER.length; index++) {
			if (daysSinceLastPayment >= DAYS_INACTIVE_AND_REWARD_MULTIPLIER[index][0]) {
				rewardMultiplier = DAYS_INACTIVE_AND_REWARD_MULTIPLIER[index][1];
			}
		}
		int normalTokensAmount = DonationManager.getTokensAmountForUsd(recommendedPayment);
		int boostedTokensAmount = (int) (normalTokensAmount * (1.0 + (rewardMultiplier / 100.0)));
		int extraTokensAmount = boostedTokensAmount - normalTokensAmount;

		calculateReward(player, extraTokensAmount, recommendedPayment);
	}

	private static void calculateReward(Player player, int extraTokensAmount, int paymentNeeded) {
		int itemId = 0;
		int amount = 0;
		int random = 0;
		if (extraTokensAmount < 20) {
			random = Misc.random(1, 5);
			if (random <= 3) {
				itemId = ServerConstants.getMainCurrencyId();
				amount = (int) (extraTokensAmount * (TradingPost.averagePrice * 0.65));
			} else if (random == 4) {
				itemId = 7478;
				amount = extraTokensAmount;
			} else if (random == 5) {
				itemId = MysteryBox.MysteryBoxData.MYSTERY_BOX.getItemId();
				amount = 1;
			}
		} else if (extraTokensAmount < 80) {
			random = Misc.random(1, 5);
			if (random <= 3) {
				itemId = ServerConstants.getMainCurrencyId();
				amount = (int) (extraTokensAmount * (TradingPost.averagePrice * 0.65));
			} else if (random == 4) {
				itemId = 7478;
				amount = extraTokensAmount;
			} else if (random == 5) {
				itemId = MysteryBox.MysteryBoxData.MYSTERY_BOX.getItemId();
				amount = extraTokensAmount / 20;
			}
		} else if (extraTokensAmount < 200) {
			random = Misc.random(1, 5);
			if (random <= 3) {
				itemId = ServerConstants.getMainCurrencyId();
				amount = (int) (extraTokensAmount * (TradingPost.averagePrice * 0.65));
			} else if (random == 4) {
				itemId = 7478;
				amount = extraTokensAmount;
			} else if (random == 5) {
				itemId = MysteryBox.MysteryBoxData.SUPER_MYSTERY_BOX.getItemId();
				amount = extraTokensAmount / 50;
			}
		} else {
			random = Misc.random(1, 5);
			if (random <= 3) {
				itemId = ServerConstants.getMainCurrencyId();
				amount = (int) (extraTokensAmount * (TradingPost.averagePrice * 0.65));
			} else if (random == 4) {
				itemId = 7478;
				amount = extraTokensAmount;
			} else if (random == 5) {
				itemId = MysteryBox.MysteryBoxData.LEGENDARY_MYSTERY_BOX.getItemId();
				amount = extraTokensAmount / 100;
			}
		}
		accountOfferLog.add(Misc.getDateAndTimeLog() + " " + player.getPlayerName() + ", " + ItemAssistant.getItemName(itemId) + " x" + Misc.formatNumber(amount) + " for " + paymentNeeded + "$");
		player.accountOfferRewardItemId = itemId;
		player.accountOfferRewardItemAmount = amount;
	}

	public static void trackItemOnOfferBought(Player player, int itemId, int amountToBuy) {
		if (player.getInterfaceIdOpened() != 33000) {
			return;
		}
		FileUtility.addLineOnTxt("backup/logs/donatoritemsbought_on_offer.txt",
		                         "[" + Misc.getDateAndTime() + "] " + player.getPlayerName() + " purchased x" + amountToBuy + " " + ItemAssistant.getItemName(itemId));
	}

	public static void paymentClaimedInUsd(Player player, double usdPayment) {
		player.totalPaymentAmount += usdPayment;
		player.totalPaymentAmount = Misc.roundDoubleToNearestOneDecimalPlace(player.totalPaymentAmount);
		player.setTimeLastClaimedDonation(System.currentTimeMillis());
		player.donationPriceClaimedHistory.add((int) usdPayment + "-" + System.currentTimeMillis());
		if (player.donationPriceClaimedHistory.size() > 10) {
			player.donationPriceClaimedHistory.remove(0);
		}
		if (player.accountOfferClaimTargetGoal > 0) {
			player.accountOfferClaimTargetProgress += usdPayment;
		}
		if (player.getInterfaceIdOpened() == 33000) {
			loadMainTab(player);
		}
		if (player.accountOfferCompleted) {
			return;
		}
		if (player.accountOfferClaimTargetProgress >= player.accountOfferClaimTargetGoal && player.accountOfferClaimTargetGoal > 0) {
			player.accountOfferCompleted = true;
			player.getPA().sendMessage(ServerConstants.GREEN_COL + "You have completed your account offer!");
			claimAccountRewards(player, true);
		}
	}

	private static void claimAccountRewards(Player player, boolean giveRewards) {
		if (giveRewards) {
			if (!player.accountOfferCompleted) {
				if (player.accountOfferClaimTargetGoal == 0) {
					player.getPA().sendMessage("You do not have an account offer currently.");
					return;
				}
				player.getPA().sendMessage("You have not completed your account offer.");
				return;
			}
			if (!Bank.hasBankingRequirements(player, true)) {
				player.getPA().sendMessage("You cannot do this here.");
				return;
			}
			ItemAssistant.addItemReward(null, player.getPlayerName(), player.accountOfferRewardItemId, player.accountOfferRewardItemAmount, false, 0);
			FileUtility.addLineOnTxt("backup/logs/donator_account_offers.txt",
			                         "[" + Misc.getDateAndTime() + "] " + player.getPlayerName() + ", " + player.accountOfferClaimTargetGoal + "$, x"
			                         + player.accountOfferRewardItemAmount + " " + ItemAssistant.getItemName(player.accountOfferRewardItemId));
		}
		player.accountOfferCompleted = false;
		player.accountOfferClaimTargetGoal = 0;
		player.accountOfferClaimTargetProgress = 0;
		player.accountOfferRewardItemAmount = 0;
		player.accountOfferRewardItemId = 0;
		if (giveRewards) {
			player.accountOffersSkippedStreak = 0;
			updateAccountOffer(player, true);
		} else {
			player.accountOffersSkippedStreak++;
		}
	}


}
