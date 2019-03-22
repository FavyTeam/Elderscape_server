package utility;

import core.ServerConfiguration;
import core.ServerConstants;
import game.player.punishment.Blacklist;
import network.connection.DonationManager;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DonationChargebackDb {

	/**
	 * Store chargeback history.
	 */
	public static ArrayList<String> chargebackHistory = new ArrayList<String>();

	/**
	 * Store this current session's chargeback.
	 */
	public static ArrayList<String> currentSessionChargeback = new ArrayList<String>();

	/**
	 * The data returned from the sql query.
	 */
	public static boolean executeQuery(ResultSet rs) {
		try {
			int id = rs.getInt("id");
			int price = rs.getInt("price");
			String state = rs.getString("status");
			String username = rs.getString("username").toLowerCase();
			String email = rs.getString("email");
			String transactionId = rs.getString("txn_id");
			double fees = rs.getDouble("payment_fee");
			double profit = price - fees;
			if (price <= 0 || !state.equals("Completed")) {
				String save = id + ServerConstants.TEXT_SEPERATOR + price + ServerConstants.TEXT_SEPERATOR + state + ServerConstants.TEXT_SEPERATOR + username
				              + ServerConstants.TEXT_SEPERATOR + email + ServerConstants.TEXT_SEPERATOR + transactionId + ServerConstants.TEXT_SEPERATOR;
				for (int index = 0; index < chargebackHistory.size(); index++) {
					if (chargebackHistory.get(index).equals(save)) {
						return true;
					}
				}

				if (state.equals("Refunded")) {
					return true;
				}
				if (!state.equals("Pending")) {
					// The only time the price and fees is negative is when the txn_type=adjustment and payment_status=Completed
					// where you lose the chargeback and paypal charge you a 10-20$ fee.
					if (price < 0 && fees < 0 && state.equals("Completed")) {
						// What we do is add the price and fees because that is what paypal is deducting from the account.
						profit = price + fees;

						String parse[] = save.split(ServerConstants.TEXT_SEPERATOR);
						ArrayList<String> content = new ArrayList<String>();
						content.add("This is a chargeback adjustment where you lose the chargeback and they charge a fee. txn_type=adjustment");
						content.add("Fee: " + fees);
						content.add("Id: " + parse[0]);
						content.add("Price: " + parse[1]);
						content.add("State: " + parse[2]);
						content.add("Ign: " + parse[3]);
						content.add("Email: " + parse[4]);
						content.add("Transaction id: " + parse[5]);
						EmailSystem.addPendingEmail("Chargeback extra fee: " + fees + "$", content, "mgtdt@yahoo.com");
					}
				}
				chargebackHistory.add(save);
				if (state.equals("Canceled_Reversal")) {
					String parse[] = save.split(ServerConstants.TEXT_SEPERATOR);
					ArrayList<String> content = new ArrayList<String>();
					content.add("Id: " + parse[0]);
					content.add("Price: " + parse[1]);
					content.add("State: " + parse[2]);
					content.add("Ign: " + parse[3]);
					content.add("Email: " + parse[4]);
					content.add("Transaction id: " + parse[5]);
					EmailSystem.addPendingEmail("Chargeback cancelled: " + price + "$", content, "mgtdt@yahoo.com");
					return true;
				}

				if (state.equals("Pending")) {
					String parse[] = save.split(ServerConstants.TEXT_SEPERATOR);
					ArrayList<String> content = new ArrayList<String>();
					content.add("Check if they have made payments in the past so i can turn the Pending status to Completed so they can claim.");
					content.add("Id: " + parse[0]);
					content.add("Price: " + parse[1]);
					content.add("State: " + parse[2]);
					content.add("Ign: " + parse[3]);
					content.add("Email: " + parse[4]);
					content.add("Transaction id: " + parse[5]);
					EmailSystem.addPendingEmail("Pending payment: " + price + "$", content, "mgtdt@yahoo.com");
					return true;
				}
				currentSessionChargeback.add(save);
				//ChargebackPlayerAutoJail.notifyOfPlayer(username);
				//EmailSystem.addPendingEmail("Chargebacker ipbanned: " + username, "", "mgtdt@yahoo.com");
				Blacklist.blacklistPlayer(null, username);
				DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, "[Server] has ipbanned '" + username + "' for charging back.");
				return false;
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * This is executed after the sql query is executed.
	 */
	public static void connectionFinished(String actionType) {
		if (!actionType.equals("DONATION CHARGEBACK")) {
			return;
		}
		if (currentSessionChargeback.isEmpty()) {
			return;
		}
		FileUtility.saveArrayContentsSilent("backup/logs/donations/chargeback_history.txt", currentSessionChargeback);
		ArrayList<String> content = new ArrayList<String>();
		for (int index = 0; index < currentSessionChargeback.size(); index++) {
			String parse[] = currentSessionChargeback.get(index).split(ServerConstants.TEXT_SEPERATOR);
			content.add("Id: " + parse[0]);
			content.add("Price: " + parse[1]);
			content.add("State: " + parse[2]);
			content.add("Ign: " + parse[3]);
			content.add("Email: " + parse[4]);
			content.add("Transaction id: " + parse[5]);
			content.add("");
		}
		EmailSystem.addPendingEmail("Chargeback occured!", content, "mgtdt@yahoo.com");
	}

	/**
	 * Timer instance of the chargeback session.
	 */
	public static Timer donationChargebackTimer = new Timer();

	/**
	 * TimerTask instance of the chargeback session which will be executed every minute.
	 */
	public static TimerTask donationChargebackTask = new TimerTask() {
		@Override
		public void run() {
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			if (!ServerConfiguration.FORCE_DEDICATED_SERVER) {
				return;
			}
			new Thread(new WebsiteSqlConnector(null, "DONATION CHARGEBACK", WebsiteLogInDetails.DONATE_DATABASE_NAME, "SELECT * FROM `donation` order by id desc LIMIT 30;", "",
			                                   WebsiteLogInDetails.IP_ADDRESS, WebsiteLogInDetails.SQL_USERNAME, WebsiteLogInDetails.SQL_PASSWORD)).start();
		}
	};

	/**
	 * This is executed before the sql query.
	 */
	public static void preExecute(String actionType) {
		if (actionType.equals("DONATION CHARGEBACK")) {
			WebsiteLogInDetails.readLatestWebsiteLogInDetails();
			DonationChargebackDb.currentSessionChargeback.clear();
		}

	}

	public static void readFile() {
		chargebackHistory = FileUtility.readFile("backup/logs/donations/chargeback_history.txt");

	}
}
