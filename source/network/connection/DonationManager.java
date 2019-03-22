/*Change the package if this isn't yours!*/
package network.connection;

import core.ServerConstants;
import game.content.bank.Bank;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.tradingpost.TradingPost;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import network.sql.SQLConstants;
import network.sql.SQLNetwork;
import network.sql.query.impl.DoubleParameter;
import network.sql.query.impl.StringParameter;
import tools.EconomyScan;
import utility.EmailSystem;
import utility.FileUtility;
import utility.Misc;
import utility.WebsiteLogInDetails;
import utility.WebsiteSqlConnector;


public class DonationManager {

	public final static int OSRS_MISSING = 5;

	public static double OSRS_DONATION_MULTIPLIER = 1.0;

	public final static String osrsDonationMultiplierFileLocation = "backup/logs/donations/07rates.txt";

	public static ArrayList<String> donationReceivedHistory = new ArrayList<String>();

	public static double totalPaymentsToday;

	public static int osrsToday;

	private static double osrsInInventory;

	public static double bmtPaymentsToday;

	public static int customPaymentsToday;

	public final static String EVENT_SALE = "";

	public final static double EVENT_SALE_MULTIPLIER = 1.4;
	
	public final static int OSRS_PAYMENT_FOR_UNBAN = 15;

	public static long timeCalledOsrsPaymentMissing;

	public static boolean botCheckRunning;

	/**
	 * The BMT Micro product id, the price in USD paid by the player.
	 */
	public final static long[][] BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT =
			{
					{13645000001L, 5},
					{13645000000L, 10},
					{13645000003L, 15},
					{13645000004L, 20},
					{13645000005L, 35},
					{13645000006L, 50},
					{13645000007L, 100},
					{13645000008L, 200},
					{13645000009L, 300},
			};

	public final static double[] BMT_PRODUCT_PROFIT =
			{
					3.75,
					8.75,
					13.58,
					18.1,
					31.68,
					45.25,
					90.5,
					181,
					271.5,
			};

	/**
	 * This is not saved on restart.
	 */
	public static int osrsReceivedThisServerSession;

	public static boolean getContinueLoopingAndExecuteQueryDonation(ResultSet rs, Player player, String paymentMethod) {
		// Must be kept here because this fixes issues where a player claims a donation and then logs off before the reward appears and ends up not getting reward
		// and it says claimed=1 on the sql db.
		// This player instance is on a different thread.
		if (PlayerHandler.players[player.getPlayerId()] == null || !PlayerHandler.players[player.getPlayerId()].getPlayerName().equals(player.getPlayerName())) {
			return false;
		}
		if (ItemAssistant.getFreeInventorySlots(player) == 0) {
			player.getPA().sendMessage("You need inventory space before you can claim your donation.");
			player.websiteMessaged = true;
			return false;
		}
		if (!Bank.hasBankingRequirements(player, true)) {
			player.getPA().sendMessage("You cannot claim the donation right now.");
			player.websiteMessaged = true;
			return false;
		}
		try {
			long productId = 0;
			int id = 0;
			int price = 0;
			String transactionId = "";
			String state = "";
			String email = "";
			double fees = 0;
			double profit = 0;
			int quantity = 1;

			boolean bmt = paymentMethod.equals("BMT");
			boolean paypal = paymentMethod.equals("PAYPAL");
			if (paypal) {
				productId = rs.getInt("productid");
				id = rs.getInt("id");
				price = rs.getInt("price");
				transactionId = rs.getString("txn_id");
				state = rs.getString("status");
				email = rs.getString("email");
				fees = rs.getDouble("payment_fee");
				profit = price - fees;
			}
			if (bmt) {
				id = rs.getInt("id");
				productId = rs.getLong("pid");
				email = rs.getString("email");
				quantity = rs.getInt("quantity");
				transactionId = rs.getString("orderid");
				for (int index = 0; index < BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT.length; index++) {
					if (productId == BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[index][0]) {
						price = (int) BMT_PRODUCT_ID_AND_PLAYER_PAID_AMOUNT[index][1];
						profit = Misc.roundDoubleToNearestTwoDecimalPlaces(BMT_PRODUCT_PROFIT[index]);
					}
				}
				profit *= quantity;
			}
			profit = Misc.roundDoubleToNearestTwoDecimalPlaces(profit);
			if (paypal) {
				if (state.equals("Pending")) {
					player.getPA().sendMessage("Your donation status is pending approval by Paypal.");
					player.getPA().sendMessage("You will be able to claim it in 24 hours.");
					return true;
				}
				if (!state.equals("Completed")) {
					return true;
				}
			}

			// Some charge backs are completed state but price is less than 0.
			if (price <= 0) {
				return true;
			}

			int tokensReward = DonationManager.getTokensAmountForUsd(price * quantity);
			int tokensBefore = ItemAssistant.getItemAmount(player, 7478);
			if (!ItemAssistant.addItem(player, 7478, (int) tokensReward)) {
				player.getPA().sendMessage("You need at least 1 inventory space to claim your donation.");
				player.websiteMessaged = true;
				return false;
			}
			EconomyScan.donatorTokensGainedThisSession += (int) tokensReward;
			EconomyScan.donatorTokensGainedThisHour += (int) tokensReward;
			int tokensAfter = ItemAssistant.getItemAmount(player, 7478);
			donationReceivedHistory
					.add(paymentMethod + "- " + Misc.getDateAndTime() + ": " + player.getPlayerName() + ", tokens before: " + tokensBefore + ", tokens after: " + tokensAfter
					     + ", added: " + (int) tokensReward);
			player.websiteMessaged = true;
			// Successfull donation.
			Misc.print("----[Payment via " + paymentMethod + "]: " + player.getPlayerName() + " of " + profit + "$");
			totalPaymentsToday += profit;
			totalPaymentsToday = Misc.roundDoubleToNearestTwoDecimalPlaces(DonationManager.totalPaymentsToday);

			if (bmt) {
				bmtPaymentsToday += profit;
				bmtPaymentsToday = Misc.roundDoubleToNearestTwoDecimalPlaces(bmtPaymentsToday);
			}
			FileUtility.addLineOnTxt("backup/logs/donations/donation history.txt",
			                         profit + "$, " + player.getPlayerName() + ", " + Misc.getDateAndTime() + ", pid: " + productId + ", pp id: " + transactionId + ", email: "
			                         + email + ", id: " + id + ", method: " + paymentMethod);
			player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have been rewarded with x" + Misc.formatNumber(tokensReward) + " Donator tokens!");
			player.getPA().sendMessage(ServerConstants.BLUE_COL + "Thank you so much for donating which will keep " + ServerConstants.getServerName() + " growing!");
			player.getPA().sendMessage(ServerConstants.BLUE_COL + "Spend these at the Donator shop to earn a Donator rank or Sell the tokens");
			player.getPA().sendMessage(
					ServerConstants.BLUE_COL + "to other players for " + TradingPost.getAverageTokenPrice() + " " + ServerConstants.getMainCurrencyName().toLowerCase()
					+ " each at the Donator npc trading post.");
			player.donatorTokensReceived += tokensReward;
			DonatorMainTab.paymentClaimedInUsd(player, price * quantity);
			SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PAYMENT_LATEST) + " (time, ign, usd, type) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, player.getPlayerName()), new DoubleParameter(3, profit), new StringParameter(4, paymentMethod));

			// Email
			ArrayList<String> content = new ArrayList<String>();
			content.add(paymentMethod + " payment " + profit + "$");
			content.add("Name: " + player.getPlayerName());
			if (paypal) {
				content.add("Email: " + email);

			}
			content.add("Price: " + price);
			content.add("Profit: " + profit);
			if (paypal) {
				content.add("Fees: " + fees);
			}
			content.add("Transaction id: " + transactionId);
			content.add("Id: " + id);
			content.add("Product id: " + productId);
			content.add("Tokens: " + (int) tokensReward);
			content.add("Quantity: " + quantity);
			content.add("Ip: " + player.addressIp);
			content.add("Uid: " + player.addressUid);
			double paymentTypeBalance = bmtPaymentsToday;
			EmailSystem.addPendingEmail(paymentMethod + ": " + paymentTypeBalance + "$, payment: " + (price * quantity) + "$", content, "mgtdt@yahoo.com");

			// Update database to claimed.
			rs.updateInt("claimed", 1);
			rs.updateRow();
			return false;
		} catch (Exception e) {
			Misc.print("Donation manager failure.");
			e.printStackTrace();
		}
		return true;
	}

	public static int getTokensAmountForUsd(double usd) {
		int tokens = (int) Misc.getDoubleRoundedUp(usd * 10.0);
		if (usd >= 300) {
			tokens *= 1.3;
		} else if (usd >= 200) {
			tokens *= 1.25;
		} else if (usd >= 100) {
			tokens *= 1.2;
		} else if (usd >= 50) {
			tokens *= 1.15;
		} else if (usd >= 35) {
			tokens *= 1.10;
		} else if (usd >= 20) {
			tokens *= 1.05;
		}
		if (!DonationManager.EVENT_SALE.isEmpty()) {
			tokens *= 1.2;
		}
		return tokens;
	}

	public static void saveDailyOsrsData(String oldDate) {
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_osrs_data.txt", oldDate + ": " + osrsToday);
		osrsToday = 0;
	}

	public static void saveDailyOsrsProgress() {
		FileUtility.deleteAllLines("./backup/logs/donations/daily_osrs_progress.txt");
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_osrs_progress.txt", osrsToday + "");
	}

	public static void saveDailyDonationsData(String oldDate) {
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_data.txt", oldDate + ": " + (int) totalPaymentsToday);
		ArrayList<String> unOrdered = new ArrayList<String>();
		unOrdered = FileUtility.readFile("./backup/logs/donations/daily_data.txt");
		ArrayList<String> content = new ArrayList<String>();
		for (int index = unOrdered.size() - 1; index >= 0; index--) {
			content.add(unOrdered.get(index));
		}
		EmailSystem.addPendingEmail("Payments: " + Misc.roundDoubleToNearestTwoDecimalPlaces(totalPaymentsToday) + " on " + oldDate, content, "mohamed_ffs25ffs@yahoo.com");
		FileUtility.addLineOnTxt("./backup/logs/donations/bmt_payments_today.txt", oldDate + ": " + (int) bmtPaymentsToday);
		totalPaymentsToday = 0;
		bmtPaymentsToday = 0;
	}

	public static void saveDailyDonationsProgress() {
		FileUtility.deleteAllLines("./backup/logs/donations/daily_progress.txt");
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_progress.txt", totalPaymentsToday + "");
	}

	public static void saveDailyCustomPayments() {
		FileUtility.deleteAllLines("./backup/logs/donations/daily_custom_payments_progress.txt");
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_custom_payments_progress.txt", customPaymentsToday + "");
	}

	public static void readDailyCustomPayments() {
		File file = new File("./backup/logs/donations/daily_custom_payments_progress.txt");

		if (!file.exists()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					customPaymentsToday = Integer.parseInt(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveDailyCustomPaymentsData(String oldDate) {
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_custom_payments_data.txt", oldDate + ": " + customPaymentsToday);
		customPaymentsToday = 0;
	}

	public static void saveDailyBmtPayments() {
		FileUtility.deleteAllLines("./backup/logs/donations/daily_bmt_payments_progress.txt");
		FileUtility.addLineOnTxt("./backup/logs/donations/daily_bmt_payments_progress.txt", bmtPaymentsToday + "");
	}

	public static void readDailyBmtPayments() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("./backup/logs/donations/daily_bmt_payments_progress.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					bmtPaymentsToday = Double.parseDouble(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readDailyOsrsProgress() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("./backup/logs/donations/daily_osrs_progress.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					osrsToday = Integer.parseInt(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readDailyDonationsProgress() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("./backup/logs/donations/daily_progress.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					totalPaymentsToday = Double.parseDouble(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Location of current osrs balance.
	 */
	private final static String CURRENT_OSRS_BALANCE_LOCATION = "backup/logs/donations/current_osrs_balance.txt";

	/**
	 * Save current donations balance.
	 */
	public static void currentOsrsBalanceSave() {
		FileUtility.deleteAllLines(CURRENT_OSRS_BALANCE_LOCATION);
		FileUtility.addLineOnTxt(CURRENT_OSRS_BALANCE_LOCATION, getOsrsInInventory() + "");
	}

	/**
	 * Read current donations balance.
	 */
	public static void readCurrentOsrsBalance() {
		ArrayList<String> arraylist = FileUtility.readFile(CURRENT_OSRS_BALANCE_LOCATION);
		try {
			setOsrsInInventory(Double.parseDouble(arraylist.get(0)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readLatest07Rates() {
		try {
			if (!Files.exists(Paths.get(osrsDonationMultiplierFileLocation))) {
				return;
			}
			String line = FileUtility.readFirstLine(osrsDonationMultiplierFileLocation);

			if (line.isEmpty()) {
				return;
			}
			OSRS_DONATION_MULTIPLIER = Double.parseDouble(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 //Keep here for future Halloween
	{
		"npc_description": "Halloween dealer",
		"npc_type": 11086,
		"x": 3088,
		"y": 3502,
		"height": 0,
		"face_action": "ROAM"
	},
	{
		"npc_description": "Shady figure at the boss area",
		"npc_type": 7638,
		"x": 3784,
		"y": 9228,
		"height": 0,
		"face_action": "SOUTH"
	},
	{
		"npc_description": "Mage of Zamorak at the boss area",
		"npc_type": 7425,
		"x": 3785,
		"y": 9228,
		"height": 0,
		"face_action": "SOUTH"
	},
	{
		"npc_description": "Shady figure at the graveyard area",
		"npc_type": 7638,
		"x": 3542,
		"y": 3466,
		"height": 0,
		"face_action": "ROAM"
	},
	{
		"npc_description": "Mage of Zamorak at the graveyard area",
		"npc_type": 7425,
		"x": 3546,
		"y": 3467,
		"height": 0,
		"face_action": "ROAM"
	},
	{
		"npc_description": "Mage of Zamorak at the chaos altar temple outside wilderness",
		"npc_type": 7425,
		"x": 2934,
		"y": 3515,
		"height": 0,
		"face_action": "ROAM"
	},
	{
		"npc_description": "Shady figure at the quest starting point halloween",
		"npc_type": 7632,
		"x": 3120,
		"y": 3488,
		"height": 0,
		"face_action": "NORTH"
	},
	 */

	public static void claimDonation(Player player) {
		/*
		new Thread(new WebsiteSqlConnector(player, "CLAIM DONATION", WebsiteLogInDetails.DONATE_DATABASE_NAME, "SELECT * FROM `donation` WHERE `username` = ? AND `claimed`=0", "Paypal is processing your payment, it might take 1-24 hours.", WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
		
				if (container.getExecutions() == 7)
				{
					container.stop();
				}
			}
		
			@Override
			public void stop() {
				new Thread(new WebsiteSqlConnector(player, "CLAIM BMT DONATION", "bmt_payments", "SELECT * FROM `bmt_payments` WHERE `username` = ? AND `claimed`=0", "Bmt Micro is processing your payment, it might take 1-24 hours.", WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();
			}
		}, 1);
		*/
		new Thread(new WebsiteSqlConnector(player, "CLAIM BMT DONATION", "bmt_payments", "SELECT * FROM `bmt_payments` WHERE `username` = ? AND `claimed`=0",
		                                   "Bmt Micro is processing your payment, it might take 1-24 hours.", WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME,
		                                   WebsiteLogInDetails.SQL_PASSWORD)).start();
	}

	private final static String RATES_GOLD_WEBSITE_URL =
			"https://www.partypeteshop.com/buy-osrs-gold/";

	public final static double PERCENTAGE_PROFIT_PER_MIL = 73;


	public static Timer read07WebsiteTimer = new Timer();

	public static TimerTask read07WebsiteTask = new TimerTask() {
		@Override
		public void run() {
			readLatest07RatesFromGoldWebsite();
		}
	};

	public static void readLatest07RatesFromGoldWebsite() {

		ArrayList<String> text = Misc.readWebsiteContent(RATES_GOLD_WEBSITE_URL);
		for (int index = 0; index < text.size(); index++) {
			String line = text.get(index);
				if (line.contains("<input type=\"text\" id=\"currency-3\" value=\"")) {
					line = line.substring(line.indexOf("value=\"") + 7);
					line = line.substring(0, line.indexOf("\""));
					double rates = Double.parseDouble(line) / 15.0;
					// Round to the nearest tenth of a number.
					// So 0.76 becomes 0.8
					DecimalFormat df2 = new DecimalFormat("0.0");
					DonationManager.OSRS_DONATION_MULTIPLIER = Double.parseDouble(df2.format(rates));
					if (DonationManager.OSRS_DONATION_MULTIPLIER >= 1.6) {
						DonationManager.OSRS_DONATION_MULTIPLIER = 1.6;
					}
				Misc.print("Osrs rates latest: " + rates + ", final: " + DonationManager.OSRS_DONATION_MULTIPLIER);
				}
			}
	}

	public static class OsrsPaymentLimit {


		public static List<OsrsPaymentLimit> osrsPaymentLimitList = new ArrayList<OsrsPaymentLimit>();

		public int osrsPaymentAmount;
		public long timeOccured;

		public OsrsPaymentLimit(int osrsPaymentAmount, long timeOccured) {
			this.osrsPaymentAmount = osrsPaymentAmount;
			this.timeOccured = timeOccured;
		}

		public static boolean canAcceptOsrsPayment(Player player, int amount) {
			// if cannot, let them know how many more minutes till x can be given.
			int amountTakenInLast24Hours = 0;
			long currentTime = System.currentTimeMillis();
			long lastPaymentTime = 0;
			int lastPaymentAmount = 0;
			for (int index = 0; index < osrsPaymentLimitList.size(); index++) {
				OsrsPaymentLimit instance = osrsPaymentLimitList.get(index);
				if (currentTime - instance.timeOccured <= Misc.getHoursToMilliseconds(24)) {
					amountTakenInLast24Hours += instance.osrsPaymentAmount;
					if (lastPaymentTime == 0) {
						lastPaymentTime = instance.timeOccured;
						lastPaymentAmount = instance.osrsPaymentAmount;
					}
				}
			}
			if (amountTakenInLast24Hours + amount > WebsiteLogInDetails.OSRS_DONATION_LIMIT) {
				int canTakeCurrently = WebsiteLogInDetails.OSRS_DONATION_LIMIT - amountTakenInLast24Hours;
				if (canTakeCurrently > 0) {
					player.getPA().sendMessage("Limit is nearly reached, staff can accept " + canTakeCurrently + "m 07 maximum.");
					long canTakeInXMinutes = (lastPaymentTime - (currentTime - Misc.getHoursToMilliseconds(24))) / Misc.getMinutesToMilliseconds(1);
					player.getPA().sendMessage("The limit will be extended to " + (lastPaymentAmount + canTakeCurrently) + "m in " + canTakeInXMinutes + " minutes.");
				}
				else
				{
					long canTakeInXMinutes = (lastPaymentTime - (currentTime - Misc.getHoursToMilliseconds(24))) / Misc.getMinutesToMilliseconds(1);
					
					player.getPA().sendMessage("Limit has been reached, you can accept a " + lastPaymentAmount + "m payment in " + canTakeInXMinutes + " minutes.");
				}
				return false;
			}
			return true;
		}
	}

	public static void saveLatest07Rates() {
		FileUtility.deleteAllLines(DonationManager.osrsDonationMultiplierFileLocation);
		FileUtility.addLineOnTxt(DonationManager.osrsDonationMultiplierFileLocation, DonationManager.OSRS_DONATION_MULTIPLIER + "");
	}


	public static double getOsrsInInventory() {
		return osrsInInventory;
	}

	public static void setOsrsInInventory(double osrsInInventory) {
		DonationManager.osrsInInventory = osrsInInventory;
	}
}
