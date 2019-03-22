package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import core.GameType;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.commands.NormalCommand;
import tools.EconomyScan;

/**
 * It will scan economy every 12 hours, if it has increased too much, it will email me.
 *
 * @author MGT Madness, created on 15-08-2017
 */
public class EcoScannerAlert {

	/**
	 * The amount of hours to set the economy scan interval at.
	 */
	private final static int ECONOMY_SCAN_HOURS_INTERVAL = 1; // Keep at 1 hour.

	public final static int ECONOMY_MILLIONS_FLAG_AMOUNT = 40;

	private final static int HOURLY_BACK_UPS_TO_KEEP = 24;

	private static String ECONOMY_HISTORY_LOCATION = "backup/logs/economy_scan_auto.txt";

	public final static int RICHEST_PLAYERS_TO_FLAG = 15;

	public static void startEconomyScanAlertThread() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		// Delayed started so it does not clash with the other eco scanner
		EcoScannerAlert.timer.schedule(EcoScannerAlert.myTask, 10000, Misc.getHoursToMilliseconds(ECONOMY_SCAN_HOURS_INTERVAL));
	}

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			Misc.print("Economy scan check.");
			economyScanLoop();
		}
	};

	public static void main(String[] args) {
		long time = 0;
		while (true) {
			if (System.currentTimeMillis() - time <= 5000) {
				continue;
			}
			EconomyScan.runDuperFinderComparer();
			if (time > 0) {
				break;
			}
			time = System.currentTimeMillis();
		}
	}

	private static void economyScanLoop() {

		// Zip character files every hour , maximum of 24 backups kept
		// So if a duper dupes at 4pm and is found out at 5pm by hourly duper scanner, i can rollback to a period at 3pm for example.
		hourlyLogsBackup();

		// Find hourly duper advanced scan.
		EconomyScan.runDuperFinderComparer();

		// Scan economy every 12 hours to find wealth gained in that period.
		ArrayList<String> arraylist = FileUtility.readFile(ECONOMY_HISTORY_LOCATION);
		long latestScannedTime = 0;
		long latestEconomyAmount = 0;
		if (arraylist.size() == 0) {

		} else {
			String[] parse = arraylist.get(arraylist.size() - 1).split("-");
			latestScannedTime = Long.parseLong(parse[2]);
			latestEconomyAmount = Long.parseLong(parse[1]);
		}
		if (System.currentTimeMillis() - latestScannedTime < Misc.getHoursToMilliseconds(12) - 60000) {
			return;
		}
		Misc.print("12 hour economy increase check.");
		EconomyScan.runScheduledEcoIncreaseScan();
		long increasedAmount = EconomyScan.economyTotalBloodMoney - latestEconomyAmount;
		if (increasedAmount < 0) {
			increasedAmount = 0;
			return;
		}
		increasedAmount -= (EconomyScan.donatorTokensGainedThisSession / 10) * ServerConstants.getDonatorShopBloodMoneyRewardAmount();
		EconomyScan.donatorTokensGainedThisSession = 0;
		FileUtility
				.addLineOnTxt(ECONOMY_HISTORY_LOCATION, Misc.formatRunescapeStyle(increasedAmount) + "-" + EconomyScan.economyTotalBloodMoney + "-" + System.currentTimeMillis());
		ArrayList<String> content = new ArrayList<String>();
		String reason = "Economy increased by: " + Misc.formatRunescapeStyle(increasedAmount) + " in 12 hours.";
		EmailSystem.addPendingEmail(reason, content, "mgtdt@yahoo.com");

	}

	private static void hourlyLogsBackup() {
		Misc.print("Hourly local backup started.");
		final File folder = new File("archives");
		ArrayList<String> sort = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			sort.add(fileEntry.getPath() + "#" + fileEntry.lastModified());
		}

		// Sort the order from the recently modified file to the oldest file
		sort = NormalCommand.sort(sort, "#");
		for (int index = 0; index < sort.size(); index++) {
			String[] parse = sort.get(index).split("#");
			if (index >= HOURLY_BACK_UPS_TO_KEEP - 1) {
				File delete = new File(parse[0]);
				delete.delete();
			}
		}

		CharacterBackup.createBackUp(false, false);


	}

}
