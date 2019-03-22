package game.content.shop;

import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.AchievementShop;
import game.content.achievement.Achievements;
import game.content.combat.EdgeAndWestsRule;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.interfaces.donator.DonatorShop;
import game.content.minigame.RecipeForDisaster;
import game.content.miscellaneous.TransformedOnDeathItems;
import game.content.miscellaneous.XpLamp;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.worldevent.Tournament;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.log.CoinEconomyTracker;
import game.player.Player;
import game.player.PlayerHandler;
import utility.Misc;

public class ShopAssistant {

	private Player player;

	public ShopAssistant(Player player) {
		this.player = player;
	}

	/**
	 * The item value able to sell back by.
	 */
	public final static double SELL_TO_SHOP_PRICE_MODIFIER = 0.7;

	public final static int ECO_MAXIMUM_ITEM_SELL_AND_ALCH_PRICE = 80_000;


	public static boolean buyFromPointsShop(Player player, int itemId, int amount, int itemSlot) {
		int currentAmount = getCurrentPointsAmount(player);
		switch (player.getShopId()) {
			case 48:
				player.setMeritPoints(buyItemAction(player, itemId, amount, itemSlot, currentAmount, 0, ShopAssistant.shopCurrencyName(player), getItemShopPrice(player, itemId)));
				return true;
			case 46:
				if (GameType.isOsrsEco()) {
					player.setSlayerPoints(
							buyItemAction(player, itemId, amount, itemSlot, currentAmount, 0, ShopAssistant.shopCurrencyName(player), getItemShopPrice(player, itemId)));
					return true;
				}
				break;
			case 16:
				player.setCustomPetPoints(
						buyItemAction(player, itemId, amount, itemSlot, currentAmount, 0, ShopAssistant.shopCurrencyName(player), getItemShopPrice(player, itemId)));
				return true;
		}

		buyItemAction(player, itemId, amount, itemSlot, currentAmount, ShopHandler.shopCurrencyItemId[player.getShopId()], ShopAssistant.shopCurrencyName(player),
		              getItemShopPrice(player, itemId));
		return true;
	}

	/**
	 * Get the current points amount if using a points shop.
	 */
	public static int getCurrentPointsAmount(Player player) {
		switch (player.getShopId()) {
			case 48:
				return player.getMeritPoints();
			case 46:
				if (GameType.isOsrsEco()) {
					return player.getSlayerPoints();
				}
				break;
			case 16:
				return player.getCustomPetPoints();
		}
		return 0;
	}

	/**
	 * Convert noted blood money items into proper blood money.
	 */
	private static int[] itemPurchasedModified(Player player, int itemId, int amountToBuy) {
		int[] modified = new int[2];
		switch (itemId) {

			// Noted blood money 8k
			case 16001:
				itemId = ServerConstants.getMainCurrencyId();
				amountToBuy = ServerConstants.getDonatorShopBloodMoneyRewardAmount() * amountToBuy;
				CoinEconomyTracker.addIncomeList(player, "DONATING " + amountToBuy);
				break;

			// Noted blood money 1k
			case 16000:
				itemId = ServerConstants.getMainCurrencyId();
				amountToBuy = ServerConstants.getVoteShopBloodMoneyRewardAmount() * amountToBuy;
				CoinEconomyTracker.addIncomeList(player, "VOTING " + amountToBuy);
				break;

			// Noted coins 750k
			case 16244:
				itemId = ServerConstants.getMainCurrencyId();
				amountToBuy = ServerConstants.getVoteShopBloodMoneyRewardAmount() * amountToBuy;
				CoinEconomyTracker.addIncomeList(player, "VOTING " + amountToBuy);
				break;

			// Noted coins 5m
			case 16245:
				itemId = ServerConstants.getMainCurrencyId();
				amountToBuy = ServerConstants.getDonatorShopBloodMoneyRewardAmount() * amountToBuy;
				CoinEconomyTracker.addIncomeList(player, "DONATING " + amountToBuy);
				break;

			// Noted coins 10,000
			case 16248:
				itemId = ServerConstants.getMainCurrencyId();
				amountToBuy = 10000 * amountToBuy;
				CoinEconomyTracker.addIncomeList(player, "BLOOD_MONEY_EXCHANGE_COINS " + amountToBuy);
				break;

			// Blood money x1
			case 16253:
				itemId = 13307;
				break;
		}
		modified[0] = itemId;
		modified[1] = amountToBuy;
		return modified;
	}

	/**
	 * Open a shop.
	 */
	public void openShop(int shopId) {
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("Opened shop: " + shopId);
		}
		ItemAssistant.resetItems(player, 3823); // Spawning items while in shop. Must be kept here to update inventory.
		player.usingShop = true;
		player.setShopId(shopId);
		if (!player.doNotOpenShopInterface) {
			player.getPA().sendFrame248(3824, 3822);
		}
		ShopAssistant.updateShopPointsTitle(player);
		configureNormalShop(shopId);
		configureUntradeablesShop(shopId);
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
		player.setInterfaceIdOpened(3900);
		player.doNotOpenShopInterface = false;
	}

	/**
	 * @return The item price in the shop.
	 */
	private static int getItemShopPrice(Player player, int itemId) {
		int price = 0;
		for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
			if (ShopHandler.shopItems[player.getShopId()][i] == itemId) {
				price = ShopHandler.shopItemsPointsPrice[player.getShopId()][i];
				break;
			}
		}

		if (price == 0) {
			if (ShopHandler.shopCurrencyItemId[player.getShopId()] == 13307) {
				price = BloodMoneyPrice.getBloodMoneyPrice(itemId);
			} else {
				price = getItemCoinPrice(itemId);
			}
		}
		if (player.getShopId() == 11) {
			price /= 10;
			price += 50;
		}
		if (ShopHandler.shopCurrencyItemId[player.getShopId()] == ShopHandler.FREE) {
			price = 0;
		}

		// Xp lamp 2 million, Xp lamp 350k
		if (itemId == 16251 || itemId == 16252) {
			price = 25;
		}
		return price;
	}

	private void configureNormalShop(int shopId) {
		if (shopId == 11) {
			return;
		}
		int totalItems = 0;
		for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
			if (ShopHandler.shopItems[shopId][i] > 0) {
				totalItems++;
			}
		}
		if (totalItems > ShopHandler.MAXIMUM_ITEMS_IN_SHOP) {
			totalItems = ShopHandler.MAXIMUM_ITEMS_IN_SHOP;
		}
		if (!player.shopSearchString.isEmpty()) {
			totalItems = 0;
			for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
				int itemId = ShopHandler.shopItems[shopId][i];
				if (itemId > 0) {
					if (ItemAssistant.getItemName(itemId).toLowerCase().contains(player.shopSearchString)) {
						totalItems++;
						continue;
					}
				}
			}
		}

		double scrollAmount = ((double) totalItems) / 10.0;

		// Round up the double into the highest integer.
		double dAbs = Math.abs(scrollAmount);

		int a = (int) dAbs;

		double result = dAbs - (double) a;
		if (result == 0.0) {
		} else {
			scrollAmount = scrollAmount < 0 ? -(a + 1) : a + 1;
		}

		InterfaceAssistant.setFixedScrollMax(player, 19683, (int) (scrollAmount * 51.6));
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(3900);
		player.getOutStream().writeWord(totalItems);
		int totalCount = 0;

		for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
			int itemId = ShopHandler.shopItems[shopId][i];
			if (itemId > 0) {
				boolean skip = false;
				if (!player.shopSearchString.isEmpty()) {
					if (!ItemAssistant.getItemName(itemId).toLowerCase().contains(player.shopSearchString)) {
						skip = true;
					}
				}
				if (!skip) {
					int valueToSend = ShopHandler.shopItemsStockAmount[shopId][i];
					if (GameType.isOsrsEco()) {
						if (ShopHandler.shopsWithPricesNotShownEco[player.getShopId()]) {
							valueToSend = ShopHandler.shopItemsStockAmount[shopId][i];
						} else {
							valueToSend = ShopAssistant.getItemShopPrice(player, itemId);
						}
					} else {
						valueToSend = ShopAssistant.getItemShopPrice(player, itemId);
					}
					if (valueToSend > 254) {
						player.getOutStream().writeByte(255);
						player.getOutStream().writeDWord_v2(valueToSend);
					} else {
						player.getOutStream().writeByte(valueToSend);
					}
					itemId = changeItemInShop(player, itemId, false);
					if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0) {
						itemId = ServerConstants.MAX_ITEM_ID;
					}
					player.getOutStream().writeWordBigEndianA(itemId + 1);
					totalCount++;
				}
			}
			if (totalCount > totalItems) {
				break;
			}
		}

	}

	/**
	 * Change an item in the shop, visually and when buying it aswell.
	 */
	private static int changeItemInShop(Player player, int itemId, boolean playerInput) {
		if (!playerInput) {
			// Xp lamp 1 million
			if (itemId == 13146 && GameType.isOsrsEco()) {
				return itemId = GameMode.getDifficulty(player, "GLADIATOR") ? XpLamp.XpLampData.XP_LAMP_350k.getLampItemId() : XpLamp.XpLampData.XP_LAMP_2_MILLION.getLampItemId();
			}
		}

		if (playerInput) {
			for (XpLamp.XpLampData data : XpLamp.XpLampData.values()) {
				if (itemId == data.getLampItemId()) {
					return 13146;
				}
			}
		}
		return itemId;
	}

	/**
	 * Verify if the item bought is in the shop list. Packet abuse precaution.
	 */
	public boolean itemInShop(int itemId, boolean buy) {
		// Untradeables shop.
		if (player.getShopId() == 11) {
			for (int index = 0; index < player.itemsToShop.size(); index++) {
				String[] args = player.itemsToShop.get(index).split(" ");
				int itemBuying = Integer.parseInt(args[0]);
				if (itemId == itemBuying) {
					return true;
				}
			}
		} else {
			for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
				if (itemId == (ShopHandler.shopItems[player.getShopId()][i])) {
					return true;
				}

				// If i am trying to sell a noted abyssal whip and there is only a normal abyssal whip in merchant shop. Then let me sell it.
				// Merchant shop
				if (player.getShopId() == 54 && ItemDefinition.getDefinitions()[ItemAssistant.getNotedItem(itemId)].note && itemId == (ShopHandler.shopItems[player.getShopId()][i]
				                                                                                                                       - 1)) {
					return true;
				}
			}
		}

		return false;
	}

	public static int getItemCoinPrice(int itemId) {
		if (itemId <= 0) {
			return 0;
		}
		itemId = ItemAssistant.getUnNotedItem(itemId);
		if (ItemDefinition.getDefinitions()[itemId] == null) {
			return 1;
		}
		return ItemDefinition.getDefinitions()[itemId].price;
	}

	public static int getSellToShopPrice(Player player, int itemId, boolean shopPriceCheck) {
		if (!canSellItemToShop(player, itemId)) {
			return 0;
		}
		if (itemId <= 0) {
			return 0;
		}
		itemId = ItemAssistant.getUnNotedItem(itemId);
		int itemPrice = ServerConstants.getItemValue(itemId);
		int value = itemPrice;
		if (itemPrice > 1) {
			value = (int) (itemPrice * SELL_TO_SHOP_PRICE_MODIFIER);
		}
		if (GameType.isOsrsPvp() && player != null) {
			for (int index = 0; index < player.resourcesHarvested.size(); index++) {
				String[] parse = player.resourcesHarvested.get(index).split(" ");
				if (Integer.parseInt(parse[0]) == itemId) {
					if (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)] == null) {
						break;
					}
					value = BloodMoneyPrice.getDefinitions()[itemId].harvestedPrice;
					if (shopPriceCheck) {
						player.getPA().sendMessage(ItemAssistant.getItemName(itemId) + " price is boosted because you harvested it.");
					}
					break;
				}
			}

		}
		if (GameType.isOsrsEco() && value > ECO_MAXIMUM_ITEM_SELL_AND_ALCH_PRICE) {
			value = ECO_MAXIMUM_ITEM_SELL_AND_ALCH_PRICE;
		}
		if (ShopHandler.standardPriceItems[itemId] == true) {
			value = itemPrice;
		}
		return value;
	}

	public void checkShopPrice(int itemId, int removeSlot) {
		itemId = ItemAssistant.getUnNotedItem(itemId);
		itemId = changeItemInShop(player, itemId, true);
		if (handleImbuedRing(player, itemId, BloodMoneyPrice.getBloodMoneyPrice(itemId), "PRICE")) {
			return;
		}
		if (!canPurchaseSkillcape(player, itemId, removeSlot)) {
			return;
		}
		if (!canBuyFromShop(player)) {
			return;
		}
		int tournamentBrewAmount = canPurchaseMoreBrews(player, itemId);
		if (tournamentBrewAmount == 0) {
			return;
		}

		if (!RecipeForDisaster.hasGlovesRequirements(player, itemId)) {
			return;
		}

		itemId = changeItemInShop(player, itemId, false);
		int itemPrice = ShopAssistant.getItemShopPrice(player, itemId);
		if (itemPrice == 0) {
			if (AchievementShop.hasAchievementItemRequirements(player, itemId, true, true)) {
				player.playerAssistant.sendMessage(ItemAssistant.getItemName(itemId) + " is free.");
			}
		} else {
			player.getPA().sendMessage(GameType.isOsrsEco() ?
					                           "You can buy <col=560055>" + ItemAssistant.getItemName(itemId) + "<col=000000> for <col=0008f7>" + Misc.formatNumber(itemPrice)
					                           + " <col=000000>" + ShopAssistant.shopCurrencyName(player) + "." :
					                           ItemAssistant.getItemName(itemId) + ": currently costs " + Misc.formatNumber(itemPrice) + " " + ShopAssistant
							                                                                                                                           .shopCurrencyName(player)
					                           + ".");
		}
	}

	private static boolean canBuyFromShop(Player player) {
		if (player.getShopId() == 27 || player.getShopId() == 30) {
			if (!player.isDonator()) {
				DonatorContent.canUseFeature(player, DonatorRankSpentData.DONATOR);
				return false;
			}
		}
		return true;
	}

	private static int reduceQuantityFromShop(Player player, int itemId, int amount, int itemPrice) {
		// General store
		if (player.getShopId() != 8) {
			return amount;
		}
		int coinsSlot = ItemAssistant.getItemSlot(player, 995);
		int coinsAmount = player.playerItemsN[coinsSlot];
		if (itemPrice * amount > coinsAmount) {
			double result = Math.floor(coinsAmount / itemPrice);
			amount = (int) result;
		}
		for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
			if (ShopHandler.shopItems[player.getShopId()][i] == itemId) {
				if (amount > ShopHandler.shopItemsStockAmount[player.getShopId()][i]) {
					amount = ShopHandler.shopItemsStockAmount[player.getShopId()][i];
				}
				if (player.playerItemsN[coinsSlot] < itemPrice) {
					return 0;
				}
				ShopHandler.shopItemsStockAmount[player.getShopId()][i] -= amount;
				if (ShopHandler.shopItemsStockAmount[player.getShopId()][i] == 0) {
					ShopHandler.shopItems[player.getShopId()][i] = 0;
				}
				updateShopForActiveShoppers();
				break;
			}
		}
		player.getShops().openShop(player.getShopId());
		return amount;
	}

	public static int buyItemAction(Player player, int itemId, int amount, int itemSlot, int currentCurrencyAmount, int currencyItemId, String pointsName, int itemPrice) {
		boolean hasEnoughSpace = true;

		// Incase the player buys an item worth 500bm and enters 2147m as amount, the totalPrice will go negative and he will end up getting it for free, dupe.
		if (itemPrice > 0) {
			int MAX_AMOUNT = Integer.MAX_VALUE / itemPrice;
			if (amount > MAX_AMOUNT) {
				amount = MAX_AMOUNT;
			}
		}
		amount = reduceQuantityFromShop(player, itemId, amount, itemPrice);
		int totalPrice = itemPrice * amount;
		// If physical currency, grab the current amount.
		if (currencyItemId > 0) {
			currentCurrencyAmount = ItemAssistant.getItemAmount(player, currencyItemId);
		}
		// If item is for free.
		if (itemPrice == 0) {
			if (!AchievementShop.hasAchievementItemRequirements(player, itemId, false, true)) {
				return 0;
			}
			if (!canPurchaseSkillcape(player, itemId, itemSlot)) {
				return 0;
			}
			if (!canBuyFromShop(player)) {
				return 0;
			}
			int tournamentBrewAmount = canPurchaseMoreBrews(player, itemId);
			if (tournamentBrewAmount >= 0) {
				amount = tournamentBrewAmount;
				if (amount == 0) {
					return 0;
				}
			}
			if (!ItemAssistant.isStackable(itemId)) {
				amount = Math.min(amount, ItemAssistant.getFreeInventorySlots(player));
			}
			ItemAssistant.addItem(player, itemId, amount);
		} else {
			//i have 9gp
			//it costs 5gp
			//i try to buy 5
			int amountToBuy = amount;

			// Adjust amount to buy depending on my current currency amount verses the currency amount required.
			if (currentCurrencyAmount < totalPrice) {
				amountToBuy = (int) ((double) currentCurrencyAmount / (double) itemPrice);
				totalPrice = amountToBuy * itemPrice;
			}
			if (handleImbuedRing(player, itemId, itemPrice, "BUY")) {
				return currentCurrencyAmount;
			}

			// Delete x amount of currency if i can handle receiving it.

			int freeInventorySlots = ItemAssistant.getFreeInventorySlots(player);

			boolean currencyCompletelyDepleted = false;
			// If physical currency. Check if the currency will be completely depleted, if so add an extra inventory space available.
			if (currencyItemId > 0) {
				if (currentCurrencyAmount - totalPrice == 0) {
					freeInventorySlots++;
					currencyCompletelyDepleted = true;
				}
			}

			// Adjust my buy amount depending if i have less inventory spaces available.
			if (!ItemAssistant.isStackable(itemId)) {
				if (amountToBuy > freeInventorySlots) {
					amountToBuy = freeInventorySlots;
					totalPrice = amountToBuy * itemPrice;
					if (amountToBuy == 0) {
						hasEnoughSpace = false;
					}
				}

				// Without the code below, for example:
				// If i have 4k blood money and 2 inventory spaces and i buy 5 spirit shields worth 1k each, it would
				// take away 3k and only give me 2 spirit shields.
				if (currencyCompletelyDepleted) {
					if (totalPrice != currentCurrencyAmount) {
						freeInventorySlots--;
						amountToBuy = freeInventorySlots;
						totalPrice = amountToBuy * itemPrice;
					}
				}
			}

			// Item being purchased is stackable and i have 0 inventory slots and i do not have the item being purchased in inventory.
			else if (ItemAssistant.isStackable(itemId) && freeInventorySlots == 0) {
				if (!ItemAssistant.hasItemInInventory(player, itemId)) {
					hasEnoughSpace = false;
				}
			}

			if (hasEnoughSpace) {
				if (amountToBuy == 0) {
					player.getPA().sendMessage("You do not have enough " + pointsName + " to buy this item.");
					return currentCurrencyAmount;
				}
				if (player.getShopId() == 11) {
					for (int index = 0; index < player.itemsToShop.size(); index++) {
						String[] args = player.itemsToShop.get(index).split(" ");
						int itemsToShopItemId = Integer.parseInt(args[0]);
						int itemsToShopAmount = Integer.parseInt(args[1]);
						if (itemId == itemsToShopItemId) {
							amountToBuy = itemsToShopAmount;
							totalPrice = itemPrice;
							player.itemsToShop.remove(index);
							player.getShops().openShop(11);
							player.bloodMoneySpent += totalPrice;
							Achievements.checkCompletionMultiple(player, "1060 1124");
							break;
						}
					}
				}
				if (currencyItemId > 0) {
					ItemAssistant.deleteItemFromInventory(player, currencyItemId, totalPrice);
				}

				// Turn noted blood money from Donator shop or vote shop into blood money.
				int[] modified = new int[2];
				int originalBuyAmount = amountToBuy;
				modified = itemPurchasedModified(player, itemId, amountToBuy);
				itemId = modified[0];
				amountToBuy = modified[1];
				ItemAssistant.addItem(player, itemId, amountToBuy);
				// Donator tokens shop.
				if (player.getShopId() == 71 || DonatorShop.usingDonatorShopInterface(player)) {
					CoinEconomyTracker.addDonatorItemsBought(player, ItemAssistant.getItemName(itemId) + "#" + originalBuyAmount);
					DonatorMainTab.trackItemOnOfferBought(player, itemId, originalBuyAmount);
					DonatorTokenUse.upgradeToNextRank(player, totalPrice);
				}
				if (itemId == 2740 || itemId == 2742 || itemId == 2744) {
					CoinEconomyTracker.addSinkList(player, "CASKET " + totalPrice);
				}
				if (currencyItemId == 0) {
					return currentCurrencyAmount - totalPrice;
				}
			} else {
				player.getPA().sendMessage("You do not have enough space to buy this item.");
				return currentCurrencyAmount;
			}
		}
		return currentCurrencyAmount;
	}

	private static int canPurchaseMoreBrews(Player player, int itemId) {
		for (int index = 0; index < Tournament.eventShopIds.length; index++) {
			if (player.getShopId() == Tournament.eventShopIds[index]) {
				if (ItemDefinition.getDefinitions()[itemId].name.startsWith("Saradomin brew")) {
					EdgeAndWestsRule.hasExcessBrews(player, 100);
					int doses = Integer.parseInt(ItemDefinition.getDefinitions()[itemId].name.replace("Saradomin brew(", "").replace(")", "")) - 2;
					if (doses < 0) {
						doses = 0;
					}
					int maximumBrews = (player.getShopId() == 82 ? 4 : player.getShopId() == 81 ? 3 : 2);
					if (player.brewCount + doses > maximumBrews * 2) {
						player.getPA().sendMessage("No more brews cheater!");
						return 0; // Cannot buy any brews.
					}
					return 1; // Can buy 1 brew
				}
			}
		}
		return -1; // Item being purchased not a brew.
	}

	private static boolean canPurchaseSkillcape(Player player, int itemId, int slot) {
		if (player.getShopId() == 9) {
			if (player.isUberDonator() || player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
				return true;
			}
			if (slot < ServerConstants.getTotalSkillsAmount()) {
				if (player.baseSkillLevel[slot] < 99) {
					player.getPA().sendMessage("You need 99 " + ServerConstants.SKILL_NAME[slot] + " to purchase this cape.");
					return false;
				}
			} else if (slot <= 43) {
				slot -= 22;
				if (player.baseSkillLevel[slot] < 99) {
					player.getPA().sendMessage("You need 99 " + ServerConstants.SKILL_NAME[slot] + " to purchase this trimmed cape.");
					return false;
				}
				int total99s = 0;
				for (int index = 7; index < ServerConstants.getTotalSkillsAmount(); index++) {
					if (player.baseSkillLevel[index] == 99) {
						total99s++;
					}
					if (total99s == 1) {
						break;
					}
				}
				if (total99s != 1) {
					player.getPA().sendMessage("You need 2 maxed skills to buy a trimmed cape.");
					return false;
				}
			} else if (slot <= 65) {
				slot -= 44;
				if (player.baseSkillLevel[slot] < 99) {
					player.getPA().sendMessage("You need 99 " + ServerConstants.SKILL_NAME[slot] + " to purchase this hood.");
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public void buyItem(int itemId, int fromSlot, int amount) {
		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}
		// General store
		if (player.getShopId() == 8 && GameMode.getGameModeContains(player, "IRON MAN")) {
			player.getPA().sendMessage("Ironmen cannot use this.");
			return;
		}

		itemId = changeItemInShop(player, itemId, true);

		if (!itemInShop(itemId, true)) {
			return;
		}

		if (!RecipeForDisaster.hasGlovesRequirements(player, itemId)) {
			return;
		}

		itemId = changeItemInShop(player, itemId, false);
		if (ShopAssistant.buyFromPointsShop(player, itemId, amount, fromSlot)) {
			ShopAssistant.updateShopPointsTitle(player);
			return;
		}

	}

	private void configureUntradeablesShop(int shopId) {
		if (shopId != 11) {
			return;
		}
		int totalItems = 0;
		for (int i = 0; i < player.itemsToShop.size(); i++) {
			totalItems++;
		}
		if (totalItems > ShopHandler.MAXIMUM_ITEMS_IN_SHOP) {
			totalItems = ShopHandler.MAXIMUM_ITEMS_IN_SHOP;
			player.playerAssistant.sendMessage("Too many items to display, buy some items to view the rest.");
		}
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(3900);
		player.getOutStream().writeWord(totalItems);
		int TotalCount = 0;
		for (int i = 0; i < player.itemsToShop.size(); i++) {
			String[] args = player.itemsToShop.get(i).split(" ");
			int itemId = Integer.parseInt(args[0]);
			itemId++;
			int itemAmount = Integer.parseInt(args[1]);

			if (itemAmount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(itemAmount);
			} else {
				player.getOutStream().writeByte(itemAmount);
			}
			if (itemId > ServerConstants.MAX_ITEM_ID || itemId < 0) {
				itemId = ServerConstants.MAX_ITEM_ID;
			}
			player.getOutStream().writeWordBigEndianA(itemId);
			TotalCount++;
			if (TotalCount > totalItems) {
				break;
			}

		}
	}

	public static boolean canSellItemToShop(Player player, int itemId) {
		itemId = ItemAssistant.getUnNotedItem(itemId);
		if (player != null) {
			if (GameMode.getGameModeContains(player, "IRON MAN")) {
				player.getPA().sendMessage("You cannot sell as an Ironman.");
				return false;
			}
		}
		if (ShopHandler.cannotSellToShopItems[itemId] == true) {
			if (player != null) {
				player.getPA().sendMessage("This item cannot be sold to the shop.");
			}
			return false;
		}
		if (GameType.isOsrsEco()) {
			if (ItemAssistant.cannotTradeAndStakeItemItem(itemId)) {
				if (player != null) {
					player.getPA().sendMessage("You cannot sell this item to the shop.");
				}
				return false;
			}
		}
		if (GameType.isOsrsPvp() && player != null) {
			for (int index = 0; index < player.resourcesHarvested.size(); index++) {
				String[] parse = player.resourcesHarvested.get(index).split(" ");
				int resourceItemId = Integer.parseInt(parse[0]);
				if (resourceItemId == itemId) {
					if (itemId == 556 || itemId == 558 || itemId == 555 || itemId == 557 || itemId == 554 || itemId == 559 || itemId == 564 || itemId == 562 || itemId == 9075
					    || itemId == 561) {
						player.playerAssistant.sendMessage("The only runes you may sell are Law runes and above.");
						return false;
					}
				}
			}
		}
		return true;

	}

	public void priceCheckItemToSell(Player player, int itemId) {
		itemId = ItemAssistant.getUnNotedItem(itemId);
		if (!canSellItemToShop(player, itemId)) {
			return;
		}
		int value = getSellToShopPrice(player, itemId, true);
		if (value == 0) {
			player.getPA().sendMessage(ItemAssistant.getItemName(itemId) + ": can be sold for nothing.");
			return;
		}
		if (GameType.isOsrsEco()) {
			player.playerAssistant.sendMessage(
					"You can sell " + ItemAssistant.getItemName(itemId) + " for <col=0008f7>" + Misc.formatNumber(value) + " <col=000000>" + ServerConstants.getMainCurrencyName()
					+ ".");
		} else {
			player.playerAssistant
					.sendMessage(ItemAssistant.getItemName(itemId) + ": can be sold for " + Misc.formatNumber(value) + " " + ServerConstants.getMainCurrencyName() + ".");
		}
	}

	public void sellItemToShop(Player player, int itemId, int itemSlot, int amount) {
		if (ItemAssistant.isNulledSlot(itemSlot)) {
			return;
		}
		if (player.getShopId() == 0 && !DonatorShop.usingDonatorShopInterface(player)) {
			return;
		}
		if (!canSellItemToShop(player, itemId)) {
			return;
		}
		if (!ItemAssistant.playerHasItem(player, itemId, 1, itemSlot)) {
			return;
		}

		if (amount > ItemAssistant.getItemAmount(player, itemId)) {
			amount = ItemAssistant.getItemAmount(player, itemId);
		}
		int itemPrice = getSellToShopPrice(player, itemId, false);

		if (ItemDefinition.getDefinitions()[itemId].stackable && ItemAssistant.getFreeInventorySlots(player) == 0) {
			// So i can sell all my dragon arrows while having no inventory space and no main currency in inventory.
			if (amount != ItemAssistant.getItemAmount(player, itemId)) {
				// Stop a situation where i have 49 dragon arrows and i try to sell 40 of them with no inventory space and no main currency in inventory.
				if (!ItemAssistant.hasItemInInventory(player, ServerConstants.getMainCurrencyId())) {
					player.playerAssistant.sendMessage("Not enough inventory space.");
					return;
				}
			}
		}

		if (GameType.isOsrsPvp()) {
			// Sell harvested resource. The amount changes depending on the harvested amount.
			String parse[] = Skilling.sellHarvestedResource(player, itemId, amount).split(" ");
			if (parse[1].equals("true")) {
				amount = Integer.parseInt(parse[0]);
				if (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)] != null) {
					itemPrice = BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].harvestedPrice;
					CoinEconomyTracker.addIncomeList(player, "SKILLING " + (BloodMoneyPrice.getDefinitions()[ItemAssistant.getUnNotedItem(itemId)].harvestedPrice * amount));
				}
			}
		}

		if (itemPrice == 0) {
			player.getPA().sendMessage("You cannot sell this item.");
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, itemId, amount);
		if (player.getHeight() != 20) {
			ItemAssistant.addItemToInventoryOrDrop(player, ServerConstants.getMainCurrencyId(), itemPrice * amount);
		}
		addQuantityToShop(itemId, amount);
	}

	public static void updateShopForActiveShoppers() {
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player player = PlayerHandler.players[i];
			if (player == null) {
				continue;
			}
			if (player.usingShop) {
				if (player.getShopId() == 8) {
					player.getShops().openShop(8);
				}
				if (player.getShopId() == 54) {
					player.getShops().openShop(54);
				}
			}
		}
	}

	public void addQuantityToShop(int itemId, int amount) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		boolean itemInShop = false;

		int getUnnotedId = ItemAssistant.getUnNotedItem(itemId); // Get unnoted id of the item i'm selling.
		int getNotedId = ItemAssistant.getNotedItem(itemId); // Get noted id of the item i'm selling.

		// Selling Merchant shop item to the general store.
		if (player.getShopId() == 8) {
			// Add amount to existing item.
			for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
				if (ShopHandler.shopItems[54][i] == getUnnotedId || ShopHandler.shopItems[54][i] == getNotedId) {
					itemInShop = true;
					ShopHandler.shopItemsStockAmount[54][i] += amount;
					player.playerAssistant.sendMessage("The General store owner immediately resells the " + ItemAssistant.getItemName(getUnnotedId) + " to the merchant.");
					updateShopForActiveShoppers();
				}
			}
		}

		// Add amount to existing item.
		if (!itemInShop) {
			for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
				if (ShopHandler.shopItems[8][i] == getUnnotedId || ShopHandler.shopItems[8][i] == getNotedId) {
					itemInShop = true;
					ShopHandler.shopItemsStockAmount[8][i] += amount;
					updateShopForActiveShoppers();
				}
			}
		}


		// Add new item to general store.
		if (!itemInShop) {
			for (int i = 0; i < ShopHandler.MAXIMUM_ITEMS_IN_SHOP; i++) {
				if (ShopHandler.shopItems[8][i] == 0) {
					itemInShop = true;
					ShopHandler.shopItems[8][i] = itemId;
					ShopHandler.shopItemsStockAmount[8][i] += amount;
					updateShopForActiveShoppers();
					break;
				}
			}
		}
		if (player.getShopId() == 8 || player.getShopId() == 54) {
			player.getShops().openShop(player.getShopId());
		}
	}

	public static String shopCurrencyName(Player player) {
		if (ShopHandler.shopCostsPoints[player.getShopId()]) {
			return ShopHandler.shopName[player.getShopId()].replace(" store", "").replace(" shop", "");
		}
		if (ShopHandler.shopCurrencyItemId[player.getShopId()] > 0) {
			return ItemDefinition.getDefinitions()[ShopHandler.shopCurrencyItemId[player.getShopId()]].name + "";
		} else {
			return "";
		}

	}

	public static void updateShopPointsTitle(Player player) {
		String title = "";
		if (ShopHandler.shopCostsPoints[player.getShopId()]) {
			title = ShopHandler.shopName[player.getShopId()] + ": " + getCurrentPointsAmount(player) + " points";
		} else {
			title = ShopHandler.shopName[player.getShopId()];
		}
		player.getPA().sendFrame126(title, 19301);
	}

	public static boolean handleImbuedRing(Player player, int itemIdToBuy, int price, String action) {
		int normalRing = 0;
		for (TransformedOnDeathItems.TransformedOnDeathData data : TransformedOnDeathItems.TransformedOnDeathData.values()) {
			if (itemIdToBuy == data.getSpecialId()) {
				normalRing = data.getNormalId();
			}
		}
		if (normalRing == 0) {
			return false;
		}
		String ringName = ItemAssistant.getItemName(normalRing);
		boolean hasRequiredRing = ItemAssistant.hasItemInInventory(player, normalRing);
		if (action.equals("PRICE")) {
			player.playerAssistant
					.sendMessage("It costs " + Misc.formatNumber(price) + " Blood money & one " + ringName + " to upgrade to " + ItemAssistant.getItemName(itemIdToBuy) + ".");
			return true;
		}
		if (hasRequiredRing && ItemAssistant.checkAndDeleteStackableFromInventory(player, 13307, price)) {
			ItemAssistant.deleteItemFromInventory(player, normalRing, 1);
			ItemAssistant.addItem(player, itemIdToBuy, 1);
			player.playerAssistant.sendMessage("You upgrade the " + ringName + " to an imbued version.");
			return true;
		} else {
			player.playerAssistant.sendMessage("You need a " + ringName + " to upgrade.");
			return true;
		}
	}
}
