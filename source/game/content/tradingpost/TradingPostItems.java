package game.content.tradingpost;

import core.ServerConstants;
import game.player.Player;
import tools.EconomyScan;
import utility.FileUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Store the trading post items to claim here.
 *
 * @author MGT Madness, created on 21-07-2017.
 */
public class TradingPostItems {
	/**
	 * Store the Trading post database in this list.
	 */
	public static List<TradingPostItems> tradingPostItemsData = new ArrayList<TradingPostItems>();

	public String action = "";

	public String name = "";

	public String partnerName = "";

	public int itemId;

	public int itemAmount;

	public int itemPrice;

	public int itemSold;

	public int itemSoldAmount;

	public String getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public int getItemPrice() {
		return itemPrice;
	}

	public int getItemSold() {
		return itemSold;
	}

	public int getItemSoldAmount() {
		return itemSoldAmount;
	}

	/**
	 * @param name Name of the person to receive the item.
	 * @param partnerName Name of the person i traded with.
	 * @param itemId Item id to receive
	 * @param amount The remaining amount of the stock left/amount needed
	 * @param itemPrice The price of the item traded
	 * @param itemSold The item id of the item sold, not used right now.
	 * @param itemSoldAmount The amount of the item sold
	 */
	public TradingPostItems(String action, String name, String partnerName, int itemId, int amount, int itemPrice, int itemSold, int itemSoldAmount) {
		this.action = action;
		this.name = name;
		this.partnerName = partnerName;
		this.itemId = itemId;
		this.itemAmount = amount;
		this.itemPrice = itemPrice;
		this.itemSold = itemSold;
		this.itemSoldAmount = itemSoldAmount;
	}

	public static void tradingPostItemsLogInNotice(Player player) {
		for (int index = 0; index < TradingPostItems.tradingPostItemsData.size(); index++) {
			TradingPostItems itemsData = TradingPostItems.tradingPostItemsData.get(index);
			if (itemsData.getName().equals(player.getLowercaseName())) {
				player.getPA().sendMessage(ServerConstants.BLUE_COL + "Your Trading post has been updated!");
				break;
			}
		}
	}


	/**
	 * Location of TradingPost text file save.
	 */
	private final static String LOCATION = "backup/logs/trading_post/items.txt";

	/**
	 * Read the TradingPost text file save.
	 */
	public static void readTradingPostItems() {
		TradingPostItems.tradingPostItemsData.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader(EconomyScan.tradingPostItemsLocation.isEmpty() ? LOCATION : EconomyScan.tradingPostItemsLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
					String action = parse[0];
					String name = parse[1];
					String partnerName = parse[2];
					int itemId = Integer.parseInt(parse[3]);
					int amount = Integer.parseInt(parse[4]);
					int itemPrice = Integer.parseInt(parse[5]);
					int itemSold = Integer.parseInt(parse[6]);
					int itemSoldAmount = Integer.parseInt(parse[7]);
					TradingPostItems.tradingPostItemsData.add(new TradingPostItems(action, name, partnerName, itemId, amount, itemPrice, itemSold, itemSoldAmount));
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
	public static void saveTradingPostItems() {
		FileUtility.deleteAllLines(LOCATION);
		ArrayList<String> line = new ArrayList<String>();
		for (TradingPostItems data : TradingPostItems.tradingPostItemsData) {
			line.add(data.getAction() + ServerConstants.TEXT_SEPERATOR + data.getName() + ServerConstants.TEXT_SEPERATOR + data.getPartnerName() + ServerConstants.TEXT_SEPERATOR
			         + data.getItemId() + ServerConstants.TEXT_SEPERATOR + data.getItemAmount() + ServerConstants.TEXT_SEPERATOR + data.getItemPrice()
			         + ServerConstants.TEXT_SEPERATOR + data.getItemSold() + ServerConstants.TEXT_SEPERATOR + data.getItemSoldAmount());
		}
		FileUtility.saveArrayContentsSilent(LOCATION, line);
	}


}
