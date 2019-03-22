package utility;

import core.Server;
import core.ServerConfiguration;
import game.player.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import network.connection.DonationManager;
import network.sql.SQLConstants;
import network.sql.SQLNetwork;
import network.sql.query.impl.DoubleParameter;
import network.sql.query.impl.StringParameter;

/**
 * Text file communication with the bot.
 * @author MGT Madness, created on 17-07-2018.
 */
public class OsBotCommunication {
	
	//C:\\Users\\MGT\\SSD Content\\Rsps SSD\\2- Server\\dawntained_server\\backup\\logs\\donations\\osrs_bot\\bot

	public final static String BOT_FILE_LOCATION = System.getProperty("user.home") + "/OSBot/Data/dawntained/bot";

	public final static String SERVER_FILE_LOCATION = System.getProperty("user.home") + "/OSBot/Data/dawntained/server";

	public final static String BOT_READY_FILE_LOCATION = System.getProperty("user.home") + "/OSBot/Data/dawntained/ready.txt";

	public final static String FORCE_DISABLE_LOCATION = "backup/logs/donations/osbot_disable.txt";

	public final static long READ_SECONDS_LOOP = Misc.getSecondsToMilliseconds(3);

	public static final int HOURS_ONLINE_TO_CALL_BOT = 2;

	public static Timer osBotTimer = new Timer();

	public static long timeBotCalledUsed;

	public static boolean useBot = true;

	public static boolean forceDisableBot;

	public static long botFailedFileModifiedTime;

	public static TimerTask osBotTask = new TimerTask() {
		@Override
		public void run() {
			if (forceDisableBot) {
				return;
			}
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			readAndDeleteAFileFromDirectory(SERVER_FILE_LOCATION, ".txt");
			long botModifiedTime = new File(OsBotCommunication.BOT_READY_FILE_LOCATION).lastModified();
			useBot = !Misc.timeElapsed(botModifiedTime, Misc.getSecondsToMilliseconds(10));
			if (!useBot && botModifiedTime != botFailedFileModifiedTime) {
				TwilioApi.callAdmin("OSBot: script loop failed.", "");
				botFailedFileModifiedTime = botModifiedTime;
			}
		}
	};

	public static void readAndDeleteAFileFromDirectory(String directory, String specificFileExtensions) {
		directory = directory.replace("/", "\\");
		List<File> tasks = new ArrayList<File>();
		tasks = FileUtility.getFilesList(directory);
		for (int index = 0; index < tasks.size(); index++) {
			String path = tasks.get(index).getPath();
			if (!specificFileExtensions.isEmpty() && !path.endsWith(specificFileExtensions)) {
				continue;
			}
			if (System.currentTimeMillis() - tasks.get(index).lastModified() <= 1000) {
				continue;
			}
			String content = FileUtility.readFirstLine(path);
			String parse[] = content.split("=", 2);
			textFound(parse[0], parse[1]);
			tasks.get(index).delete();
		}
	}

	public static void main(String[] args) {
		boolean read = true;
		if (read) {
			readAndDeleteAFileFromDirectory(BOT_FILE_LOCATION, ".txt");
		} else {
			addTextInDirectoryRandomized(BOT_FILE_LOCATION, "test=56");
		}
	}
	public static void addTextInDirectoryRandomized(String serverLogsLocation, String string) {
		for (int index = 0; index < 1_000; index++) {
			String path = serverLogsLocation + "/entry" + Misc.random(1, Integer.MAX_VALUE) + ".txt";
			if (!new File(path).exists()) {
				FileUtility.addLineOnTxt(path, string);
				break;
			}
		}

	}
	private static void textFound(String variable, String content) {
		switch (variable) {
			case "received" :
				osrsGpReceived(content);
				break;
			case "bankcheck" :
				bankCheck(content);
				break;
			case "log_in_failed" :
				TwilioApi.callAdmin("OSBot: " + content, "");
				EmailSystem.addPendingEmail("OsBot failed log-in.", content, "mohamed_ffs25ffs@yahoo.com");
				useBot = false;
				break;
			case "read_data_file_from_website" :
				Server.readDataFileFromWebsite();
				break;
		}
	}

	private static void bankCheck(String content) {
		String[] parse = content.split("#");
		long amount = Long.parseLong(parse[0]);
		long timeCheckedBank = Long.parseLong(parse[1]);
		double millionsAmount = (int) (amount / 1_000_000);
		if (Misc.timeElapsed(timeCheckedBank, Misc.getSecondsToMilliseconds(60))) {
			return;
		}
		FileUtility.addLineOnTxt("backup/logs/donations/osrs_bot_checker.txt", "[" + Misc.getDateAndTime() + "] real amount: " + millionsAmount + "m, expected: " + DonationManager.getOsrsInInventory() + "m");
		SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_OSRS_HISTORY) + " (time, entity, action, total_expected) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, "Bot"), new StringParameter(3, "OsBot has checked the bank and found " + millionsAmount + "m"), new DoubleParameter(4, DonationManager.getOsrsInInventory()));
		if (DonationManager.getOsrsInInventory() - millionsAmount >= DonationManager.OSRS_MISSING && Misc.timeElapsed(DonationManager.timeCalledOsrsPaymentMissing, Misc.getMinutesToMilliseconds(40))) {
			DonationManager.timeCalledOsrsPaymentMissing = System.currentTimeMillis();
			String text = "Osrs payment difference, expected: " + DonationManager.getOsrsInInventory() + "m, bank: " + millionsAmount + ".";
			TwilioApi.callAdmin(text, "");
			EmailSystem.addPendingEmail(text, "Osrs payment difference, expected: " + DonationManager.getOsrsInInventory() + "m, bank: " + millionsAmount + ".", "mohamed_ffs25ffs@yahoo.com");
		}

	}

	private static void osrsGpReceived(String content) {
		/*
		String[] parse = content.split("#");
		double millionsReceivedFromBotClient = Double.parseDouble(parse[0]);
		int failedFromBotClient = Boolean.parseBoolean(parse[1]) ? 1 : 0;
		String rsnFromBotClient = parse[2];
		SQLNetworkTransactionFuture<List<OsrsBotTransaction>> future = new SQLNetworkTransactionFuture<List<OsrsBotTransaction>>() {
			@Override
			public List<OsrsBotTransaction> request(Connection connection) throws SQLException {
				List<OsrsBotTransaction> list = new ArrayList<>();
				try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + SQLConstants.getServerSchemaTable(SQLConstants.OSRS_BOT) + " ORDER BY id DESC limit 1;")) {
					try (ResultSet results = statement.executeQuery()) {
						while (results.next()) {
							int id = results.getInt("id");
							String ign = results.getString("player_name");
							String ip = results.getString("player_ip");
							String uid = results.getString("player_uid");
							if (results.getString("rsn").equals(rsnFromBotClient)) {
								if (failedFromBotClient == 1) {
									OsBotFlaggedPlayers.addFlaggedPlayer(rsnFromBotClient, ip, uid);
								}
								list.add(new OsrsBotTransaction(id, "", ign, ip, uid, rsnFromBotClient, millionsReceivedFromBotClient, failedFromBotClient));
								break;
							}
						}
					}
				}
				return list;
			}
		};
		Server.getSqlNetwork().submit(future);
		SQLNetworkTransactionFutureCycleEvent<List<OsrsBotTransaction>> futureEvent = new SQLNetworkTransactionFutureCycleEvent<>(future, 20);
		CycleEventContainer<Object> futureEventContainer = CycleEventHandler.getSingleton().addEvent(new Object(), futureEvent, 1);
		futureEventContainer.addStopListener(() -> {
			if (futureEvent.isCompletedWithErrors()) {
				return;
			}
			int tokens = (int) DonationManager.getTokensAmountForUsd(millionsReceivedFromBotClient * DonationManager.OSRS_DONATION_MULTIPLIER);
			OsrsBotTransaction instance = future.getResult().get(0);
			ItemAssistant.addItemReward(null, instance.playerName, 7478, tokens, false, millionsReceivedFromBotClient * DonationManager.OSRS_DONATION_MULTIPLIER);
			ArrayList<String> text = new ArrayList<String>();
			text.add("Donator: " + rsnFromBotClient);
			text.add("Donator ip: " + instance.playerIp);
			text.add("Donator uid: " + instance.playerUid);
			text.add("Donated: " + millionsReceivedFromBotClient + "m for " + tokens + " Donator tokens");
			DonationManager.setOsrsInInventory(Misc.roundDoubleToNearestOneDecimalPlace(DonationManager.getOsrsInInventory() + millionsReceivedFromBotClient));
			DonationManager.osrsToday += (int) millionsReceivedFromBotClient;
			SQLNetwork.insert("INSERT INTO " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_PAYMENT_LATEST) + " (time, ign, usd, type) VALUES(?, ?, ?, ?)", new StringParameter(1, Misc.getDateAndTime()), new StringParameter(2, instance.playerName), new DoubleParameter(3, millionsReceivedFromBotClient * DonationManager.OSRS_DONATION_MULTIPLIER), new StringParameter(4, "OSRS"));
		
			EmailSystem.addPendingEmail("Osrs: " + DonationManager.getOsrsInInventory() + "m, payment by Bot: " + millionsReceivedFromBotClient + "m", text, "mgtdt@yahoo.com");
			Server.getSqlNetwork().submit(connection -> {
				try (PreparedStatement statement = connection.prepareStatement("UPDATE " + SQLConstants.getServerSchemaTable(SQLConstants.OSRS_BOT) + " SET millions_received=?, failed_collection=? WHERE id=?")) {
					statement.setDouble(1, millionsReceivedFromBotClient);
					statement.setInt(2, failedFromBotClient);
					statement.setInt(3, 4);
					statement.executeUpdate();
				}
			});
		});
		*/
	}

	public final static boolean DISABLE = false;

	public static void startScheduledTask() {
		if (DISABLE) {
			return;
		}
		OsBotCommunication.osBotTimer.schedule(OsBotCommunication.osBotTask, 0, OsBotCommunication.READ_SECONDS_LOOP);
	}

	public static void saveFile() {
		FileUtility.deleteAllLines(FORCE_DISABLE_LOCATION);
		FileUtility.addLineOnTxt(FORCE_DISABLE_LOCATION, forceDisableBot + "");
	}

	public static void readFile() {
		forceDisableBot = Boolean.parseBoolean(FileUtility.readFirstLine(FORCE_DISABLE_LOCATION));
	}

	public static class OsBotFlaggedPlayers {

		public String name = "";

		public String ip = "";

		public String uid = "";

		public long time;

		public OsBotFlaggedPlayers(String name, String ip, String uid, long time) {
			this.name = name;
			this.ip = ip;
			this.uid = uid;
			this.time = time;
		}

		public static List<OsBotFlaggedPlayers> osBotFlaggedPlayersList = new ArrayList<OsBotFlaggedPlayers>();

		public static void addFlaggedPlayer(String name, String ip, String uid) {
			osBotFlaggedPlayersList.add(new OsBotFlaggedPlayers(name, ip, uid, System.currentTimeMillis()));
		}

		/**
		 * The player has to be flagged {@linkplain FLAGGED_AMOUNT} times in the last {@linkplain HOURS_BANNED} hours.
		 */
		private final static int HOURS_BANNED = 24;

		private final static int FLAGGED_AMOUNT = 2;

		public static boolean isBanned(Player player) {
			int flaggedAmount = 0;
			for (int index = 0; index < osBotFlaggedPlayersList.size(); index++) {
				OsBotFlaggedPlayers instance = osBotFlaggedPlayersList.get(index);
				if (Misc.timeElapsed(instance.time, Misc.getHoursToMilliseconds(HOURS_BANNED))) {
					osBotFlaggedPlayersList.remove(index);
					index--;
					continue;
				}
				if (Misc.uidMatches(player.addressUid, instance.uid) || player.getPlayerName().equals(instance.name) || player.addressIp.equals(instance.ip)) {
					flaggedAmount++;
					if (flaggedAmount == FLAGGED_AMOUNT) {
						player.getPA().sendMessage("You are banned from using the bot for 24 hours.");
						return true;
					}
				}
			}
			return false;
		}

	}
}
