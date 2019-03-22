package utility;

import core.ServerConfiguration;
import game.player.PlayerHandler;
import java.util.ArrayList;

/**
 * Every 24 hours, send to my main email the highest playerbase in the last 24 hours.
 *
 * @author MGT Madness, created on 16-08-2017
 */
public class HighestPlayerCount {
	private final static String PLAYER_COUNT_FILE_LOCATION = "backup/logs/player base/";

	/**
	 * Save the highest player count of the last 24 hours.
	 */
	public static int highestPlayerCountDaily;

	/**
	 * The time the player count email was sent.
	 */
	public static long timePlayerCountSent;

	/**
	 * When the time changes, check if it is around 11 am, then record max playercount for the last 24 hours.
	 */
	public static void timeChanged() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		String time = PlayerHandler.currentTime;
		if (time.startsWith("03") && time.endsWith("AM")) {
			// If it has been less than 2 hours, it means we already saved it somepoint at 11AM +
			if (Misc.getTimeMilliseconds() - timePlayerCountSent < Misc.getHoursToMilliseconds(2)) {
				return;
			}
			timePlayerCountSent = System.currentTimeMillis();
			FileUtility.deleteAllLines(PLAYER_COUNT_FILE_LOCATION + "daily playercount time.txt");
			FileUtility.addLineOnTxt(PLAYER_COUNT_FILE_LOCATION + "daily playercount time.txt", timePlayerCountSent + "");

			FileUtility.addLineOnTxt(PLAYER_COUNT_FILE_LOCATION + "daily playercount history.txt",
			                         Misc.millisecondsToDateOnly(System.currentTimeMillis() - Misc.getHoursToMilliseconds(7)) + ": " + highestPlayerCountDaily);
			ArrayList<String> unOrdered = new ArrayList<String>();
			unOrdered = FileUtility.readFile(PLAYER_COUNT_FILE_LOCATION + "daily playercount history.txt");
			ArrayList<String> content = new ArrayList<String>();
			for (int index = unOrdered.size() - 1; index >= 0; index--) {
				content.add(unOrdered.get(index));
			}
			EmailSystem.addPendingEmail("Players: " + highestPlayerCountDaily + " on " + Misc.getPreviousDayName(), content, "mohamed_ffs25ffs@yahoo.com");
			highestPlayerCountDaily = 0;
		}
	}

	/**
	 * Read the time of when highest player count was saved.
	 */
	public static void readHighestPlayerCountFiles() {
		ArrayList<String> content = new ArrayList<String>();
		content = FileUtility.readFile(PLAYER_COUNT_FILE_LOCATION + "daily playercount time.txt");
		if (content.size() > 0) {
			timePlayerCountSent = Long.parseLong(content.get(0));
		}

		content = FileUtility.readFile(PLAYER_COUNT_FILE_LOCATION + "daily current playercount.txt");
		if (content.size() > 0) {
			highestPlayerCountDaily = Integer.parseInt(content.get(0));
		}
	}

	/**
	 * Save the current playercount highest progress incase of server restart.
	 */
	public static void saveHighestPlayerCountProgress() {
		FileUtility.deleteAllLines(PLAYER_COUNT_FILE_LOCATION + "daily current playercount.txt");
		FileUtility.addLineOnTxt(PLAYER_COUNT_FILE_LOCATION + "daily current playercount.txt", highestPlayerCountDaily + "");
	}

}
