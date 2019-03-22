package game.content.miscellaneous;

import game.player.Player;

/**
 * Hours online.
 *
 * @author MGT Madness, created on 07-04-2014.
 */
public class PlayerGameTime {

	public static int getDaysSinceAccountCreated(Player player) {
		if (player.timeOfAccountCreation == 0) {
			return 0;
		}
		long days = (System.currentTimeMillis() - player.timeOfAccountCreation) / 1000;
		days /= 60;
		days /= 60;
		days /= 24;
		return (int) days;
	}

	/**
	 * Amount of hours the player has been online for.
	 *
	 * @return The amount of time the player has been online for in hours.
	 */
	public static int getHoursOnline(Player player) {
		int hours = (player.secondsBeenOnline / 60) / 60;
		return hours;
	}

	/**
	 * Start the player online calculating when the player logs in.
	 */
	public static void startMilliSecondsOnline(Player player) {
		player.millisecondsOnline = System.currentTimeMillis();
	}

	/**
	 * Calculate the amount of hours since the player last logged in.
	 *
	 * @param player The associated player.
	 */
	public static int calculateHoursFromLastVisit(Player player) {
		double time = System.currentTimeMillis() - player.logOutTime;
		time = ((time / 1000) / 60) / 60;
		return (int) time;
	}

	/**
	 * Save the time of when the player logged out.
	 *
	 * @param player The associated player.
	 */
	public static void loggedOffTime(Player player) {
		player.logOutTime = System.currentTimeMillis();
	}

}
