package utility;

import core.ServerConstants;
import game.content.commands.AdministratorCommand;
import game.content.interfaces.InterfaceAssistant;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Blacklist;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Inform the Owner via email that a flagged player has logged in. A player who chargedback etc..
 *
 * @author MGT Madness, created on 07-10-2017.
 */
public class ChargebackPlayerAutoJail {

	/**
	 * Type = as in ip/uid/ign
	 * Match = as in 127.0.0.1 or their uid or their in-game name
	 * originalName-type-match
	 */
	public static ArrayList<String> notifyList = new ArrayList<String>();

	/**
	 * Download the Paypal ipn every 5 minutes, this will be used to check of the chargebacker has cancelled all chargebacks.
	 */
	public static void downloadPaypalIpnAndUnjailCancelledChargeBackers() {
		WebsiteLogInDetails.readLatestWebsiteLogInDetails();
		String tempFileLocation = "backup/logs/donations/ipn/ipn_temp_" + Misc.random(10) + System.currentTimeMillis() + ".txt";
		WebsiteBackup.downloadWebsiteFile("/home/admin/public_html/store/class", "payments.txt", tempFileLocation);
		try {
			File file = new File(IPN_FULL_LOCATION);
			file.delete();
			file = new File(tempFileLocation);
			File dest = new File(IPN_FULL_LOCATION);
			file.renameTo(dest);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (!loop.notifyFlagged) {
				continue;
			}
			new Thread(new Runnable() {
				public void run() {
					int notifyIndexTemp = isNotifyFlaggedPlayer(loop);
					if (notifyIndexTemp >= 0) {
						String[] parse = notifyList.get(notifyIndexTemp).split(ServerConstants.TEXT_SEPERATOR);
						ArrayList<String> text = checkIfChargebackCancelled(parse[0]);
						if (text.isEmpty()) {
							removeNotify(null, parse[0]);
							DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
							                           "[Server] has unjailed '" + loop.getPlayerName() + "' for cancelling chargeback.");
						} else {

						}
					}
				}
			}).start();

		}
	}

	public static Timer downloadPaypalIpnTimer = new Timer();

	public static TimerTask downloadPaypalIpnTask = new TimerTask() {
		@Override
		public void run() {
			ChargebackPlayerAutoJail.downloadPaypalIpnAndUnjailCancelledChargeBackers();
		}
	};

	public final static String IPN_FULL_LOCATION = "backup/logs/donations/ipn/ipn_full.txt";

	public final static String IPN_TEMP_LOCATION = "backup/logs/donations/ipn/ipn_temp_full.txt";

	public static ArrayList<String> checkIfChargebackCancelled(String playerName) {
		ArrayList<String> ipn = new ArrayList<String>();
		ipn = FileUtility.readFile(IPN_FULL_LOCATION);
		ipn.addAll(FileUtility.readFile(IPN_TEMP_LOCATION));

		String username = "";
		String email = "";
		String price = "";
		String fee = "";
		ArrayList<String> flaggedUsernames = new ArrayList<String>();
		ArrayList<String> flaggedEmails = new ArrayList<String>();
		String transactionType = "";
		int loopAmount = 1;
		for (int a = 0; a < loopAmount; a++) {
			for (int index = 0; index < ipn.size(); index++) {
				String line = ipn.get(index);
				if (line.startsWith("[")) {
					if (playerName.equalsIgnoreCase(username)) {
						if (!flaggedUsernames.contains(username.toLowerCase())) {
							flaggedUsernames.add(username.toLowerCase());
						}
					}
					if (flaggedUsernames.contains(username.toLowerCase())) {
						if (!flaggedEmails.contains(email.toLowerCase())) {
							flaggedEmails.add(email.toLowerCase());
							loopAmount++;
						}
					}
					if (flaggedEmails.contains(email.toLowerCase())) {
						if (!flaggedUsernames.contains(username.toLowerCase())) {
							flaggedUsernames.add(username.toLowerCase());
							loopAmount++;
						}
					}
					username = "";
					email = "";
					price = "";
					fee = "";
					transactionType = "";
				} else {
					if (transactionType.equals("adjustment")) {
						continue;
					}
					if (transactionType.equals("web_accept")) {
						continue;
					}
					if (transactionType.equals("new_case")) {
						continue;
					}
					//Canceled_Reversal
					//Reversed
					//item_name=210 Virtual Credits to 'Peanut' (NOT REFUNDABLE) #70745758
					//payer_email=kiloznounces@gmail.com
					String[] parse = line.split("=");
					if (parse[0].equals("item_name")) {
						String firstIndexOf = "credits to '";
						parse[1] = parse[1].toLowerCase();
						username = parse[1].substring(parse[1].indexOf(firstIndexOf) + firstIndexOf.length(), parse[1].indexOf("' (not refundable)"));
					} else if (parse[0].equals("payer_email")) {
						email = parse[1];
					} else if (parse[0].equals("txn_type")) {
						if (parse.length == 2) {
							transactionType = parse[1];
						}
					} else if (parse[0].equals("payment_gross")) {
						price = parse[1];
					} else if (parse[0].equals("payment_fee")) {
						fee = parse[1];
					}
				}
			}
		}

		ArrayList<String> chargebackedPayments = new ArrayList<String>();
		ArrayList<String> cancelledPayments = new ArrayList<String>();
		String date = "";
		boolean cancelled = false;
		for (int index = 0; index < ipn.size(); index++) {
			String line = ipn.get(index);
			if (line.startsWith("[")) {
				if (flaggedUsernames.contains(username)) {
					date = line;
					date = date.replace("[", "");
					String parseDate[] = date.split(" ");
					date = parseDate[0];
					String parseDateDash[] = date.split("-");
					date = parseDateDash[0] + ", month: " + parseDateDash[1] + ", day: " + parseDateDash[2] + ", ";
					double totalAmount = Math.abs(Double.parseDouble(price)) + Math.abs(Double.parseDouble(fee));
					if (cancelled) {
						cancelledPayments.add(totalAmount + "");
					} else {
						chargebackedPayments.add(totalAmount + ServerConstants.TEXT_SEPERATOR + date + username + ", " + email);
					}
				}
				username = "";
				email = "";
				price = "";
				fee = "";
				transactionType = "";
				cancelled = false;
			} else {
				if (transactionType.equals("adjustment")) {
					continue;
				}
				if (transactionType.equals("web_accept")) {
					continue;
				}
				if (transactionType.equals("new_case")) {
					continue;
				}
				//Canceled_Reversal
				//Reversed
				//item_name=210 Virtual Credits to 'Peanut' (NOT REFUNDABLE) #70745758
				//payer_email=kiloznounces@gmail.com
				String[] parse = line.split("=");
				if (parse[0].equals("item_name")) {
					String firstIndexOf = "credits to '";
					parse[1] = parse[1].toLowerCase();
					username = parse[1].substring(parse[1].indexOf(firstIndexOf) + firstIndexOf.length(), parse[1].indexOf("' (not refundable)"));
				} else if (parse[0].equals("payer_email")) {
					email = parse[1];
				} else if (parse[0].equals("txn_type") && parse.length == 2) {
					transactionType = parse[1];
				} else if (parse[0].equals("payment_gross")) {
					price = parse[1];
				} else if (parse[0].equals("payment_fee")) {
					fee = parse[1];
				} else if (parse[0].equals("payment_status") && parse.length == 2) {
					if (parse[1].equals("Canceled_Reversal")) {
						cancelled = true;
					}
				}
			}
		}
		for (int index = 0; index < chargebackedPayments.size(); index++) {
			String parse[] = chargebackedPayments.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String chargedBackPaymentString = parse[0];

			for (int a = 0; a < cancelledPayments.size(); a++) {
				if (chargedBackPaymentString.equals(cancelledPayments.get(a))) {
					chargebackedPayments.remove(index);
					cancelledPayments.remove(a);
					index--;
					break;
				}
			}
		}
		return chargebackedPayments;
	}

	public static void addToNotifyList(String toAdd) {
		if (notifyList.contains(toAdd)) {
			return;
		}
		notifyList.add(toAdd);
	}

	public static void isPlayerToNotifyLoggedIn(Player player) {
		int notifyIndexTemp = isNotifyFlaggedPlayer(player);
		if (notifyIndexTemp >= 0) {
			player.notifyFlagged = true;
			ipJailPlayer(player, notifyIndexTemp);

			// If player is flagged and their name is not, then add their name to flag list
			if (isPlayerToNotify("ign", player.getPlayerName()) == -1) {
				String[] parse = notifyList.get(notifyIndexTemp).split(ServerConstants.TEXT_SEPERATOR);
				String add = parse[0] + ServerConstants.TEXT_SEPERATOR + "ign" + ServerConstants.TEXT_SEPERATOR + player.getLowercaseName();
				addToNotifyList(add);
			}
		}
	}

	public static int isNotifyFlaggedPlayer(Player player) {
		boolean found = false;
		int notifyIndexTemp = isPlayerToNotify("ign", player.getPlayerName());
		if (notifyIndexTemp >= 0) {
			found = true;
		}

		if (!found) {
			notifyIndexTemp = isPlayerToNotify("ip", player.addressIp);
			if (notifyIndexTemp >= 0) {
				found = true;
			}
		}

		if (!found) {
			notifyIndexTemp = isPlayerToNotify("uid", player.addressUid);
			if (notifyIndexTemp >= 0) {
				found = true;
			}
		}
		return notifyIndexTemp;
	}

	public static void searchAllPlayersToNotify() {
		Player notifyPlayerInstance = null;
		int notifyIndexTemp = -1;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			notifyIndexTemp = isPlayerToNotify("ign", loop.getPlayerName());
			if (notifyIndexTemp >= 0) {
				notifyPlayerInstance = loop;
				break;
			}

			notifyIndexTemp = isPlayerToNotify("ip", loop.addressIp);
			if (notifyIndexTemp >= 0) {
				notifyPlayerInstance = loop;
				break;
			}

			notifyIndexTemp = isPlayerToNotify("uid", loop.addressUid);
			if (notifyIndexTemp >= 0) {
				notifyPlayerInstance = loop;
				break;
			}
		}
		if (notifyPlayerInstance != null) {
			notifyPlayerInstance.notifyFlagged = true;
			ipJailPlayer(notifyPlayerInstance, notifyIndexTemp);
		}
	}

	private static void ipJailPlayer(Player notifyPlayerInstance, int notifyIndexTemp) {
		/*
		int[][] random =
		{
			{2698, 4012},
			{2694, 4012},
			{2698, 4021},
			{2694, 4021},
			{2693, 4024},
			{2694, 4027},};
		int value = Misc.random(random.length - 1);
		notifyPlayerInstance.getPA().movePlayer(random[value][0], random[value][1], 0);
		notifyPlayerInstance.setJailed(true);
		notifyPlayerInstance.notifyFlagged = true;
		
		new Thread(new Runnable() {
			public void run() {
				ArrayList<String> text = null;
				int notifyIndexTemp = isNotifyFlaggedPlayer(notifyPlayerInstance);
				if (notifyIndexTemp >= 0) {
					String[] parse = notifyList.get(notifyIndexTemp).split(ServerConstants.TEXT_SEPERATOR);
					text = checkIfChargebackCancelled(parse[0]);
				}
				showInterfaceToNotifiedPlayer(notifyPlayerInstance, text);
			}
		}).start();
		
		
		
		// If player is flagged and their name is not, then add their name to flag list
		if (isPlayerToNotify("ign", notifyPlayerInstance.getPlayerName()) == -1) {
			String[] parse = notifyList.get(notifyIndexTemp).split(ServerConstants.TEXT_SEPERATOR);
			String add = parse[0] + ServerConstants.TEXT_SEPERATOR + "ign" + ServerConstants.TEXT_SEPERATOR + notifyPlayerInstance.getLowercaseName();
			addToNotifyList(add);
		}
		
		ArrayList<String> content = new ArrayList<String>();
		content.add("Original chargeback username: " + parse[0]);
		content.add("Type: " + parse[1]);
		content.add("Match: " + parse[2]);
		content.add("His current ign: " + notifyPlayerInstance.getPlayerName());
		content.add("His current ip: " + notifyPlayerInstance.addressIp);
		content.add("His current uid: " + notifyPlayerInstance.addressUid);
		 */
		String[] parse = notifyList.get(notifyIndexTemp).split(ServerConstants.TEXT_SEPERATOR);
		//EmailSystem.addPendingEmail("Chargebacker ipbanned: " + notifyPlayerInstance.getPlayerName() + " and " + parse[0], "", "mgtdt@yahoo.com");
		Blacklist.blacklistPlayer(notifyPlayerInstance, notifyPlayerInstance.getPlayerName());
		Blacklist.blacklistPlayer(null, parse[0]);
		DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
		                           "[Server] has ipbanned '" + notifyPlayerInstance.getPlayerName() + "' for charging back on '" + parse[0] + "'");
	}

	public static void showInterfaceToNotifiedPlayer(Player notifyPlayerInstance, ArrayList<String> chargebackText) {
		String[] message =
				{
						"@red@You have been jailed for charging back a donation.",
						"",
						"Please cancel all the chargebacks/disputes and you will be",
						"automatically unjailed by the server. So you can start playing again.",
						"" + ServerConstants.getServerName() + " costs 7,000$+ every month to stay online.",
						"Every donation means alot to keep " + ServerConstants.getServerName() + " running.",
						"",
						"Call Paypal if you do not know how to cancel chargebacks.",
						"If you did not pay through Paypal, please call your card company",
						"and tell them to cancel the chargeback.",
						"",
						"You can also pay the amount you charged back in 07 gp to",
						"a Mod on ::staff to be unjailed, buy gold from ::goldwebsites",
						"",
						"The game will instantly unjail you once the chargeback has been",
						"cancelled, it may take up to 2 hours for the chargeback to",
						"fully process. If it did not work, call Paypal/Card comany again.",
						"",
						"@red@[Chargebacks not cancelled]",
				};

		int frameIndex = 0;
		notifyPlayerInstance.getPA().sendFrame126("Charge back alert!", 25003);
		for (int index = 0; index < message.length; index++) {
			notifyPlayerInstance.getPA().sendFrame126(message[index], 25008 + frameIndex);
			frameIndex++;
		}

		for (int index = 0; index < chargebackText.size(); index++) {
			String text = chargebackText.get(index);
			String[] parse = text.split(ServerConstants.TEXT_SEPERATOR);
			notifyPlayerInstance.getPA().sendFrame126(parse[0] + "$ " + parse[1], 25008 + frameIndex);
			frameIndex++;
		}
		InterfaceAssistant.setFixedScrollMax(notifyPlayerInstance, 25007, (int) (frameIndex * 21));
		InterfaceAssistant.clearFrames(notifyPlayerInstance, 25008 + frameIndex, 25298);
		notifyPlayerInstance.getPA().displayInterface(25000);

	}

	public static int isPlayerToNotify(String notifyType, String notifyTypeResult) {
		notifyTypeResult.toLowerCase();
		for (int index = 0; index < notifyList.size(); index++) {
			String[] parse = notifyList.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String type = parse[1];
			String typeResult = parse[2];
			if (notifyType.equals(type) && notifyTypeResult.equals(typeResult)) {
				return index;
			}
		}
		return -1;
	}

	public static void loadNotifyList() {
		notifyList = FileUtility.readFile("backup/logs/donations/notify.txt");
	}

	/**
	 * The username of the player who charged back.
	 */
	public static void notifyOfPlayer(String username) {
		// Sometimes a player does not enter a username and then chargesback.
		if (username.isEmpty()) {
			return;
		}
		FileUtility.addLineOnTxt("notify_debug.txt", "Here2: " + username);
		String add = username + ServerConstants.TEXT_SEPERATOR + "ign" + ServerConstants.TEXT_SEPERATOR + username.toLowerCase();
		addToNotifyList(add);
		String ip = Misc.getOfflineIpAddress(username);
		String uid = Misc.getOfflineUid(username);
		FileUtility.addLineOnTxt("notify_debug.txt", "Here3: " + username + ", " + add + ", " + ip + ", " + uid);
		if (Blacklist.useAbleData(ip)) {
			add = username + ServerConstants.TEXT_SEPERATOR + "ip" + ServerConstants.TEXT_SEPERATOR + ip;
			addToNotifyList(add);
		}
		if (Blacklist.useAbleData(uid)) {
			add = username + ServerConstants.TEXT_SEPERATOR + "uid" + ServerConstants.TEXT_SEPERATOR + uid;
			addToNotifyList(add);
		}

		searchAllPlayersToNotify();
	}

	public static boolean removeNotify(Player player, String originalName) {
		boolean removed = false;
		boolean unjailedMain = false;
		for (int index = 0; index < ChargebackPlayerAutoJail.notifyList.size(); index++) {
			if (ChargebackPlayerAutoJail.notifyList.get(index).startsWith(originalName.toLowerCase())) {
				if (player != null) {
					player.getPA().sendMessage("Removed: " + ChargebackPlayerAutoJail.notifyList.get(index));
				}

				if (!unjailedMain) {
					AdministratorCommand.unJail(player, "unjail " + originalName, true);
					unjailedMain = true;
				}


				String[] parse = notifyList.get(index).split(ServerConstants.TEXT_SEPERATOR);
				String type = parse[1];

				String typeResult = parse[2];
				if (type.equals("ign")) {
					AdministratorCommand.unJail(player, "unjail " + typeResult, true);
				}

				ChargebackPlayerAutoJail.notifyList.remove(index);
				index--;
				removed = true;
			}
		}
		return removed;
	}
}


