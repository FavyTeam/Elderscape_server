package core;

import game.content.commands.AdministratorCommand;
import game.content.tradingpost.TradingPost;
import game.log.GameTickLog;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import network.sql.SQLBackup;
import tools.EconomyScan;
import tools.discord.api.DiscordBot;
import utility.CharacterBackup;
import utility.DatedPrintStream;
import utility.EcoScannerAlert;
import utility.EmailSystem;
import utility.ErrorManager;
import utility.FileUtility;
import utility.GitHubApi;
import utility.Misc;
import utility.SimpleTimer;
import utility.TwilioApi;
import utility.WebsiteBackup;
import utility.WebsiteModified;
import utility.WebsiteTopicsNotification;

/**
 * If the main game tick thread is down, send an email to Owner.
 *
 * @author MGT Madness, created on 13-08-2017
 */
public class ServerCrashAlert {

	/**
	 * Store time of when file was modified.
	 */
	private static long timeFileModified;

	/**
	 * Modify the game tick file every x minutes on the game tick thread.
	 */
	private final static int EXECUTE_FILE_MODIFICATION_SECONDS = 5;

	/**
	 * Every 2 minutes write to a log that shows the server is online.
	 */
	public static void gameTickLoop() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		if (System.currentTimeMillis() - timeFileModified < (EXECUTE_FILE_MODIFICATION_SECONDS - 1) * 1000) {
			return;
		}
		timeFileModified = System.currentTimeMillis();
		FileUtility.deleteAllLines("backup/logs/system log/game_tick_online.txt");
	}

	public static void main(String args[]) {
		ServerConfiguration.DEBUG_MODE = false;
		new SimpleTimer();
		System.setOut(new DatedPrintStream(System.out));
		try {
			ErrorManager.loadErrorFile("./backup/logs/system log/external/error/history.txt", "./backup/logs/system log/external/error/error");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String combinedArguments = "";
		for (int index = 0; index < args.length; index++) {
			combinedArguments = combinedArguments + args[index] + " ";
		}
		if (combinedArguments.contains("DROPBOX")) {
			CharacterBackup.FORCE_LOCAL_SAVE = false;
		}
		EmailSystem.timer.schedule(EmailSystem.myTask, Misc.getMinutesToMilliseconds(1), Misc.getMinutesToMilliseconds(1));
		DiscordBot.startDiscordBot("EXTERNAL_BOT");
		SQLBackup.initiateSqlBackupTimers();
		CharacterBackup.timer1.schedule(CharacterBackup.myTask1, Misc.getHoursToMilliseconds(2), Misc.getHoursToMilliseconds(2));
		WebsiteBackup.timer.schedule(WebsiteBackup.myTask, 0, Misc.getHoursToMilliseconds(24));
		WebsiteModified.startWebsiteFilesModifiedCheck();
		EcoScannerAlert.startEconomyScanAlertThread();
		tradingPostTimer.schedule(tradingPostTask, 0, Misc.getHoursToMilliseconds(3));
		dropboxCustomPlayerRequestsTimer.schedule(dropboxCustomPlayerRequestsTask, 0, Misc.getHoursToMilliseconds(12));
		websiteOnlineTimer.schedule(websiteOnlineTask, 0, Misc.getMinutesToMilliseconds(5));
		WebsiteTopicsNotification.loadWebsiteTopicsNotificationHistory();
		topicNotificationTimer.schedule(topicNotificationTask, 0, Misc.getMinutesToMilliseconds(5));
	}



	public static Timer topicNotificationTimer = new Timer();

	public static TimerTask topicNotificationTask = new TimerTask() {
		@Override
		public void run() {
			WebsiteTopicsNotification.checkWebsiteForImportantTopicsUpdate();
		}
	};

	/**
	 * If token price being purchased is more than x, then track it.
	 */
	private static int DONATOR_TOKENS_PRICE_COUNTER = 2000;

	/**
	 * If total amount of tokens being purchased that is over DONATOR_TOKENS_PRICE_COUNTER is less than TOKENS_AMOUNT_INVESTED, then email me.
	 */
	private static int TOKENS_AMOUNT_INVESTED = 2000;

	private static int websitesDownAmount;

	public static Timer websiteOnlineTimer = new Timer();

	/**
	 * How many times in a row are all websites online.
	 */
	private static int allWebsitesOnlineStreak;

	public static TimerTask websiteOnlineTask = new TimerTask() {
		@Override
		public void run() {
			Misc.print("Checking if all websites are online.");
			boolean down = false;
			String total = "";
			int websitesAmount = 0;


			String websiteStatus = websiteOnline("https://dawntained.com", "<li><a href=\"https://facebook.com/dawntained\">Facebook</a></li>") + " ";
			total = websiteStatus;
			websitesAmount++;
			if (!websiteStatus.contains("online")) {
				websitesDownAmount++;
				down = true;
			}



			if (!down) {
				allWebsitesOnlineStreak++;
				if (allWebsitesOnlineStreak == websitesAmount * 3) {
					websitesDownAmount = 0;
					allWebsitesOnlineStreak = 0;
				}
			} else {
				allWebsitesOnlineStreak = 0;
			}
			if (websitesDownAmount == websitesAmount * 3) {
				TwilioApi.callAdmin(total, "Offline website");
				websitesDownAmount = 0;
			}
		}
	};



	public static Timer dropboxCustomPlayerRequestsTimer = new Timer();

	public static TimerTask dropboxCustomPlayerRequestsTask = new TimerTask() {
		@Override
		public void run() {
			try {
				GitHubApi.updateDropboxFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static Timer tradingPostTimer = new Timer();

	public static TimerTask tradingPostTask = new TimerTask() {
		@Override
		public void run() {
			readTokensVariables();
			EconomyScan.loadStartupFiles(false);
			int amountInvested = 0;
			int lowerInvested = 0;
			for (TradingPost data : TradingPost.tradingPostData) {
				if (data.getAction().equals("SELLING")) {
					continue;
				}
				if (data.getAmountTraded() == data.getInitialAmount()) {
					continue;
				}
				if (data.getPrice() >= DONATOR_TOKENS_PRICE_COUNTER) {
					amountInvested += data.getItemAmountLeft();
				}
				if (data.getPrice() >= DONATOR_TOKENS_PRICE_COUNTER - 400) {
					lowerInvested += data.getItemAmountLeft();
				}
			}
			Misc.print("Donator tokens being purchased above " + Misc.formatNumber(DONATOR_TOKENS_PRICE_COUNTER) + " are: " + Misc.formatNumber(amountInvested));
			if (amountInvested < TOKENS_AMOUNT_INVESTED) {
				// Email
				ArrayList<String> content = new ArrayList<String>();
				content.add("Trading post at: " + Misc.formatNumber(amountInvested) + " Donator tokens being purchased at " + Misc.formatNumber(DONATOR_TOKENS_PRICE_COUNTER)
				            + "+ each");
				content.add(Misc.formatNumber(lowerInvested) + " Donator tokens being purchased at " + Misc.formatNumber(DONATOR_TOKENS_PRICE_COUNTER - 400) + "+ each");
				EmailSystem.addPendingEmail("Trading post at: " + Misc.formatNumber(amountInvested) + " Donator tokens being purchased", content, "mgtdt@yahoo.com");
			}

		}
	};

	private static void readTokensVariables() {
		ArrayList<String> data = FileUtility.readFile("tokens.txt");

		DONATOR_TOKENS_PRICE_COUNTER = Integer.parseInt(data.get(0));
		TOKENS_AMOUNT_INVESTED = Integer.parseInt(data.get(1));

	}

	private static String websiteOnline(String website, String match) {
		try {
			URL tmp = new URL(website);
			BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
			String inputLine;

			while ((inputLine = br.readLine()) != null) {
				if (inputLine.contains(match)) {
					return website + " is online.";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return website + " is offline.";

	}

	public static Timer serverCrashTimer = new Timer();

	public static TimerTask serverCrashTask = new TimerTask() {
		@Override
		public void run() {
			gameCrashThreadLoop(true);
		}
	};

	public static Timer tempTimer = new Timer();

	public static TimerTask tempTask = new TimerTask() {
		@Override
		public void run() {
			File execute = new File("server_bin.bat");
			try {
				Desktop.getDesktop().open(execute);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	};

	private static boolean serverCrashed;

	private static void gameCrashThreadLoop(boolean serverProgram) {
		if (serverCrashed) {
			return;
		}
		if (AdministratorCommand.disableCrashDetection) {
			return;
		}
		File file = new File("backup/logs/system log/game_tick_online.txt");
		long timeSinceLastModification = System.currentTimeMillis() - file.lastModified();

		// If it has been x seconds since last modification, send an email saying the server crashed.
		if (timeSinceLastModification > (EXECUTE_FILE_MODIFICATION_SECONDS * 1000) + (serverProgram ? 2000 : 60000)) {
			serverCrashed = true;
			Misc.print("Time since last game tick: " + Misc.formatNumber(timeSinceLastModification) + "ms");
			GameTickLog.calculateStats();
			GameTickLog.printStats();
			if (serverProgram) {
				Server.saveAndCloseAction("Server crashed!");
				ServerCrashAlert.tempTimer.schedule(ServerCrashAlert.tempTask, Misc.getSecondsToMilliseconds(5), Misc.getSecondsToMilliseconds(5));
				TwilioApi.callAdmin(Misc.getDateAndTime() + ": Server has crashed!", "Server problems");
			} else {
				Misc.print("Server crash email sent.");
				TwilioApi.callAdmin(Misc.getDateAndTime() + ": Server has crashed!", "Server problems");
			}
		}

	}

}
