package game.content.interfaces.donator;

import core.ServerConstants;
import game.content.donator.DonatorTokenUse;
import game.content.interfaces.InterfaceAssistant;
import game.content.shop.ShopAssistant;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
import game.player.Player;
import java.util.ArrayList;
import java.util.List;
import utility.FileUtility;
import utility.Misc;

/**
 * Donator shop interface.
 *
 * @author MGT Madness, created on 10-02-2018.
 */
public class DonatorShop {
	public static List<DonatorShop> donatorShopList = new ArrayList<DonatorShop>();

	private String itemName;

	private String shopTab;

	private String[] itemReward;

	private int originalPrice;

	private int discountPercentage;

	private int spriteId;

	private int priceAfterDiscount;

	private boolean isOnDonatorInterfaceMainTabOffer;

	private DonatorShop(String itemName, String[] itemReward, int originalPrice, int priceAfterDiscount, int discountPercentage, int spriteId, String shopTab) {
		this.itemName = itemName;
		this.itemReward = itemReward;
		this.originalPrice = originalPrice;
		this.discountPercentage = discountPercentage;
		this.shopTab = shopTab;
		this.spriteId = spriteId;
		this.priceAfterDiscount = priceAfterDiscount;
	}

	public String getItemName() {
		return itemName;
	}

	public String getShopTab() {
		return shopTab;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public String[] getItemReward() {
		return itemReward;
	}

	public int getOriginalPrice() {
		return originalPrice;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public int getPriceAfterDiscount() {
		return priceAfterDiscount;
	}

	public boolean isOnDonatorInterfaceMainTabOffer() {
		return isOnDonatorInterfaceMainTabOffer;
	}

	public void setOnDonatorInterfaceMainTabOffer(boolean isOnDonatorInterfaceMainTabOffer) {
		this.isOnDonatorInterfaceMainTabOffer = isOnDonatorInterfaceMainTabOffer;
	}

	public static void loadDonatorShopFile() {
		ArrayList<String> arraylist = FileUtility.readFileAndSaveEmptyLine(ServerConstants.getDataLocation() + "items/donator_shop.txt");

		String itemName = "";
		String shopTab = "";
		String[] itemReward = null;
		int originalPrice = 0;
		int discountPercentage = 0;
		int spriteId = 0;

		for (int index = 0; index < arraylist.size(); index++) {
			String line = arraylist.get(index);
			if (!line.isEmpty()) {
				String[] parse = line.split(": ");
				switch (parse[0]) {
					case "Shop tab":
						shopTab = parse[1];
						break;
					case "Name":
						itemName = parse[1];
						break;
					case "Item reward":
						itemReward = parse[1].split(" ");
						break;
					case "Price":
						originalPrice = Integer.parseInt(parse[1]);
						break;
					case "Discount":
						discountPercentage = Integer.parseInt(parse[1]);
						break;
					case "Sprite":
						spriteId = Integer.parseInt(parse[1]);
						break;
				}
			}
			if (line.isEmpty() || index == arraylist.size() - 1) {
				if (itemReward != null) {
					int priceAfterDiscount = 0;
					double discountPercentageDouble = discountPercentage / 100.0;
					priceAfterDiscount = (int) (originalPrice * discountPercentageDouble);
					priceAfterDiscount = originalPrice - priceAfterDiscount;
					if (normalShopIndexStart == -1 && shopTab.equals("Normal")) {
						normalShopIndexStart = donatorShopList.size();
					}
					donatorShopList.add(new DonatorShop(itemName, itemReward, originalPrice, priceAfterDiscount, discountPercentage, spriteId, shopTab));
					itemName = "";
					itemReward = null;
					originalPrice = 0;
					discountPercentage = 0;
					spriteId = 0;
				}
			}
		}
	}

	public static void loadBundlesTab(Player player) {
		int interfaceId = 31021;
		int itemsAdded = 0;
		for (int index = 0; index < donatorShopList.size(); index++) {
			DonatorShop instance = donatorShopList.get(index);
			if (!instance.getShopTab().equals("Bundles")) {
				break;
			}
			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, instance.getSpriteId());
			interfaceId += 5;
			player.getPA().sendFrame126(instance.getOriginalPrice() + " tokens", interfaceId);
			interfaceId++;
			player.getPA().sendFrame126(instance.getPriceAfterDiscount() + " tokens", interfaceId);
			interfaceId++;
			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 982);
			interfaceId++;
			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 984);
			interfaceId++;
			player.getPA().sendFrame126(instance.getDiscountPercentage() + "% discount", interfaceId);
			interfaceId++;
			itemsAdded++;
		}

		double rows = (double) itemsAdded / 4.0;
		rows = Misc.getDoubleRoundedUp(rows);
		int scrollLength = (int) rows * 65;
		if (scrollLength < 230) {
			scrollLength = 230;
		}
		InterfaceAssistant.setFixedScrollMax(player, 31020, scrollLength);

		player.getPA().setInterfaceClicked(31000, 31010, true);
		player.lastDonatorShopTabOpened = 3;
		ItemAssistant.resetItems(player, 3823); // Spawning items while in shop. Must be kept here to update inventory.
		player.getPA().displayInterface(31000);
		InterfaceAssistant.setInventoryOverlayId(player, 3822);
	}

	public static void openLastDonatorShopTab(Player player) {

		switch (player.lastDonatorShopTabOpened) {
			case 1:
				DonatorMainTab.loadMainTab(player);
				break;
			case 2:
				loadNormalShopTab(player);
				break;
			case 3:
				loadBundlesTab(player);
				break;
		}
	}

	public static boolean usingDonatorShopInterface(Player player) {
		return player.getInterfaceIdOpened() == 31000 || player.getInterfaceIdOpened() == 31200 || player.getInterfaceIdOpened() == 33000;
	}

	public static int normalShopIndexStart = -1;

	public static void loadNormalShopTab(Player player) {
		int interfaceId = 31202;
		int itemsAdded = 0;
		for (int index = 0; index < donatorShopList.size(); index++) {
			DonatorShop instance = donatorShopList.get(index);

			if (!instance.getShopTab().equals("Normal")) {
				continue;
			}
			if (instance.isOnDonatorInterfaceMainTabOffer()) {
				continue;
			}
			InterfaceAssistant.changeInterfaceSprite(player, interfaceId, 991);
			interfaceId += 5;

			player.getPA().sendFrame126(instance.getOriginalPrice() + " tokens", interfaceId);
			interfaceId++;
			int amount = 0;
			int itemId = Integer.parseInt(instance.getItemReward()[0]);

			// Blood money
			if (itemId == 16001) {
				amount = 10000;
			}
			player.getPA().sendFrame34(interfaceId, itemId, 0, amount);
			interfaceId++;
			itemsAdded++;
		}

		double rows = (double) itemsAdded / 6.0;
		rows = Misc.getDoubleRoundedUp(rows);
		int scrollLength = (int) rows * 65;
		if (scrollLength < 230) {
			scrollLength = 230;
		}
		InterfaceAssistant.setFixedScrollMax(player, 31201, scrollLength);
		player.getPA().setInterfaceClicked(31000, 31006, true);
		player.lastDonatorShopTabOpened = 2;
		ItemAssistant.resetItems(player, 3823); // Spawning items while in shop. Must be kept here to update inventory.
		player.getPA().displayInterface(31200);
		InterfaceAssistant.setInventoryOverlayId(player, 3822);
	}

	public static boolean isDonatorShopButton(Player player, int buttonId) {

		if (!usingDonatorShopInterface(player)) {
			return false;
		}

		// Tabs at the top buttons.
		switch (buttonId) {
			case 121026:
				DonatorMainTab.loadMainTab(player);
				return true;
			case 121030:
				loadNormalShopTab(player);
				return true;
			case 121034:
				loadBundlesTab(player);
				return true;
		}

		// Normal shop buttons.
		if (buttonId >= 121226 && buttonId <= 121255 || buttonId >= 122000 && buttonId <= 122219) {
			int itemIndex = 0;
			int optionIndex = 0;
			if (buttonId >= 121226 && buttonId <= 121255) {
				itemIndex = (buttonId - 121226) / 7;
				optionIndex = (buttonId - 121226) - (itemIndex * 7);
			}
			if (buttonId >= 122005 && buttonId <= 122219) {
				itemIndex = (buttonId - 122005) / 7;
				optionIndex = (buttonId - 122005) - (itemIndex * 7);
				itemIndex += 5;
			}
			if (buttonId == 122000) {
				itemIndex = 4;
				optionIndex = 2;
			}
			if (buttonId == 122001) {
				itemIndex = 4;
				optionIndex = 3;
			}
			if (buttonId == 122002) {
				itemIndex = 4;
				optionIndex = 4;
			}
			int indexExtra = 0;
			for (int index = 0; index < donatorShopList.size(); index++) {
				DonatorShop instance = donatorShopList.get(index);
				if (!instance.getShopTab().equals("Bundles")) {
					indexExtra = index;
					break;
				}
			}
			itemIndex += indexExtra;
			if (itemIndex > donatorShopList.size() - 1) {
				return true;
			}
			DonatorShop instance = null;
			int loops = 0;
			int itemFinalIndex = 0;
			for (int index = 0; index < donatorShopList.size(); index++) {
				DonatorShop item = donatorShopList.get(index);
				if (item.isOnDonatorInterfaceMainTabOffer()) {
					continue;
				}
				if (loops == itemIndex) {
					instance = item;
					itemFinalIndex = index;
					break;
				}
				loops++;

			}
			if (instance == null) {
				return true;
			}
			switch (optionIndex) {
				case 0:
					valueItemDonatorShop(player, instance, instance.getPriceAfterDiscount());
					break;
				case 1:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 1, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            instance.getPriceAfterDiscount());
					break;
				case 2:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 5, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            instance.getPriceAfterDiscount());
					break;
				case 3:
					ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), 10, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
					                            instance.getPriceAfterDiscount());
					break;
				case 4:
					player.xRemoveSlot = itemFinalIndex;
					InterfaceAssistant.showAmountInterface(player, "DONATOR SHOP NORMAL", "Enter amount");
					break;
			}
			return true;
		}
		// Bundles shop buttons.
		if (buttonId >= 121045 && buttonId <= 121155) {
			int itemIndex = 0;
			int optionIndex = 0;
			itemIndex = (buttonId - 121045) / 10;
			optionIndex = (buttonId - 121045) - (itemIndex * 10);
			if (itemIndex > donatorShopList.size() - 1) {
				return true;
			}
			DonatorShop instance = donatorShopList.get(itemIndex);
			switch (optionIndex) {
				case 0:
					valueItemDonatorShop(player, instance, instance.getPriceAfterDiscount());
					break;
				case 1:
					buyItemBundlesShop(player, 1, instance);
					break;
				case 2:
					buyItemBundlesShop(player, 5, instance);
					break;
				case 3:
					buyItemBundlesShop(player, 10, instance);
					break;
				case 4:
					buyItemBundlesShop(player, 1, instance);
					break;
			}
		}
		return false;
	}

	private static void buyItemBundlesShop(Player player, int amount, DonatorShop itemInstance) {

		int currencyStock = ItemAssistant.getItemAmount(player, 7478);
		if (currencyStock < itemInstance.getPriceAfterDiscount()) {
			player.getPA().sendMessage("You need " + Misc.formatNumber(itemInstance.getPriceAfterDiscount()) + " Donator tokens to purchase this.");
			return;
		}

		// Check inventory slot space.
		int freeSlots = ItemAssistant.getFreeInventorySlots(player);
		int spaceRequired = itemInstance.getItemReward().length;
		if (freeSlots < spaceRequired) {
			player.getPA().sendMessage("You need " + (spaceRequired - freeSlots) + " more inventory slots to purchase this package.");
			return;
		}
		ItemAssistant.deleteItemFromInventory(player, 7478, itemInstance.getPriceAfterDiscount());
		for (int i = 0; i < itemInstance.getItemReward().length; i++) {
			int item = Integer.parseInt(itemInstance.getItemReward()[i]);
			ItemAssistant.addItem(player, item, 1);
		}
		CoinEconomyTracker.addDonatorItemsBought(player, itemInstance.getItemName() + "#" + 1);
		DonatorTokenUse.upgradeToNextRank(player, itemInstance.getPriceAfterDiscount());
		player.getPA().sendMessage(ServerConstants.BLUE_COL + "You purchase the " + itemInstance.getItemName() + ".");
	}

	static void valueItemDonatorShop(Player player, DonatorShop itemInstance, int price) {
		player.playerAssistant.sendMessage(ServerConstants.BLUE_COL + itemInstance.getItemName() + ":");
		player.playerAssistant.sendMessage("Costs " + Misc.formatNumber(price) + " Donator tokens.");
		switch (Integer.parseInt(itemInstance.getItemReward()[0])) {
			case 16004:
				player.getPA().sendMessage("It contains a full gambling kit so you may roll the dice and plant flowers.");
				break;
			case 16254:
				player.getPA().sendMessage("Consume the scroll to gain a custom pet point, then open the custom pet shop at");
				player.getPA().sendMessage("the Donator npc to spend the point on 150+ unique custom pets!");
				break;
			case 1505:
				player.getPA().sendMessage("Change your name to another brand new name. Before doing so, use the profile");
				player.getPA().sendMessage("interface to search for a name that does not exist.");
				break;
			case 16266:
				player.getPA().sendMessage("Consume this scroll to unlock Max capes, Infernal cape, Imbued capes & 99 capes!");
				break;

			// Mystery boxes
			case 16084 :
			case 16086 :
			case 16088 :
			case 16436 :
				player.getPA().sendMessage("Check ::thread 1010 to find out all the Mystery box rewards!");
				break;
		}
	}

	public static void donatorShopNormalXAmount(Player player, int xAmount) {

		if (player.xRemoveSlot > donatorShopList.size() - 1) {
			return;
		}
		DonatorShop instance = donatorShopList.get(player.lastDonatorShopTabOpened == 1 ? DonatorMainTab.chosenIndexesOnOffer.get(player.xRemoveSlot) : player.xRemoveSlot);
		ShopAssistant.buyItemAction(player, Integer.parseInt(instance.getItemReward()[0]), xAmount, 0, ItemAssistant.getItemAmount(player, 7478), 7478, "Donator tokens",
		                            player.lastDonatorShopTabOpened == 1 ? DonatorMainTab.getPriceAfterDiscount(instance.getOriginalPrice()) : instance.getPriceAfterDiscount());
	}

}
