package game.content.shop;

import core.ServerConstants;
import utility.FileUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Shops
 **/

public class ShopHandler {

	public static int MAXIMUM_SHOPS_AMOUNT = 120;

	public final static int MAXIMUM_ITEMS_IN_SHOP = 200;

	public static int[][] shopItems = new int[MAXIMUM_SHOPS_AMOUNT][MAXIMUM_ITEMS_IN_SHOP];

	public static int[][] shopItemsStockAmount = new int[MAXIMUM_SHOPS_AMOUNT][MAXIMUM_ITEMS_IN_SHOP];

	public static int[][] shopItemsPointsPrice = new int[MAXIMUM_SHOPS_AMOUNT][MAXIMUM_ITEMS_IN_SHOP];

	/**
	 * -1 for free, 0 means it costs points, such as merit points or any other point system etc..
	 */
	public static int[] shopCurrencyItemId = new int[MAXIMUM_SHOPS_AMOUNT];

	public final static int FREE = -1;

	public final static int POINTS = -1;

	public static String[] shopName = new String[MAXIMUM_SHOPS_AMOUNT];

	/**
	 * Shops with items shown on the interface.
	 */
	public final static boolean[] shopsWithPricesNotShownEco = new boolean[MAXIMUM_ITEMS_IN_SHOP];

	/**
	 * True if the shop id is a points shop.
	 */
	public final static boolean[] shopCostsPoints = new boolean[MAXIMUM_ITEMS_IN_SHOP];


	/**
	 * Items with original price when selling to shop.
	 */
	public final static boolean[] standardPriceItems = new boolean[ServerConstants.MAX_ITEM_ID];


	/**
	 * Items cannot be sold to the shop or price checked to sell to the shop.
	 */
	public final static boolean[] cannotSellToShopItems = new boolean[ServerConstants.MAX_ITEM_ID];

	private static void loadStandardPriceItems() {
		String line;
		BufferedReader Checker = null;
		try {
			Checker = new BufferedReader(new FileReader(ServerConstants.getDataLocation() + "items/standard price items.txt"));
			while ((line = Checker.readLine()) != null) {
				if (line.startsWith("//") || line.isEmpty()) {
					continue;
				}
				String[] parse = line.split(" ");
				standardPriceItems[Integer.parseInt(parse[0])] = true;
			}
			Checker.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadCannotSellToShopItems() {
		String line;
		BufferedReader Checker = null;
		try {
			Checker = new BufferedReader(new FileReader(ServerConstants.getDataLocation() + "items/items cannot sell.txt"));
			while ((line = Checker.readLine()) != null) {
				if (line.startsWith("//") || line.isEmpty()) {
					continue;
				}
				String parse[] = line.split(" ");
				cannotSellToShopItems[Integer.parseInt(parse[0])] = true;
			}
			Checker.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int index = 0; index < ServerConstants.getImmortalDonatorItems().length; index++) {
			cannotSellToShopItems[ServerConstants.getImmortalDonatorItems()[index]] = true;
		}
	}


	public ShopHandler() {
	}

	public static void loadShops() {
		for (int i = 0; i < MAXIMUM_SHOPS_AMOUNT; i++) {
			for (int j = 0; j < MAXIMUM_ITEMS_IN_SHOP; j++) {
				ResetItem(i, j);
			}
			shopName[i] = "";

		}
		loadShopsContent();
		loadStandardPriceItems();
		loadCannotSellToShopItems();
		loadShopsPricesShownOnInterface();
	}

	public static void ResetItem(int ShopID, int ArrayID) {
		shopItems[ShopID][ArrayID] = 0;
		shopItemsStockAmount[ShopID][ArrayID] = 0;
		shopItemsPointsPrice[ShopID][ArrayID] = 0;
	}

	public static void loadShopsContent() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getDataLocation() + "items/shops.txt"));
			String line;
			int shopId = 0;
			int shopCurrency = 0;
			String name = "";
			int itemIndex = 0;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					if (line.startsWith("Shop name:")) {
						name = line.substring(11);
						itemIndex = 0;
					} else if (line.startsWith("Shop id:")) {
						shopId = Integer.parseInt(line.substring(9));
						shopName[shopId] = name;
					} else if (line.startsWith("Shop currency:")) {
						String string = line.substring(15);
						if (string.equals("Free")) {
							shopCurrencyItemId[shopId] = -1;
							continue;
						} else if (string.equals("Points")) {
							shopCurrencyItemId[shopId] = 0;
							shopCostsPoints[shopId] = true;
							continue;
						}
						shopCurrency = Integer.parseInt(string);
						shopCurrencyItemId[shopId] = shopCurrency;
					} else {
						String parse[] = line.split(" ");
						shopItems[shopId][itemIndex] = Integer.parseInt(parse[0]);
						if (!parse[1].startsWith("//")) {
							shopItemsPointsPrice[shopId][itemIndex] = Integer.parseInt(parse[1]);
						}
						itemIndex++;
					}

				} else {

				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadShopsPricesShownOnInterface() {
		ArrayList<String> arraylist = FileUtility.readFile(ServerConstants.getEcoDataLocation() + "items/shop_prices_not_shown.txt");
		for (int index = 0; index < arraylist.size(); index++) {
			if (arraylist.get(index).startsWith("//")) {
				continue;
			}
			String parse[] = arraylist.get(index).split(" ");
			shopsWithPricesNotShownEco[Integer.parseInt(parse[0])] = true;
		}

	}
}
