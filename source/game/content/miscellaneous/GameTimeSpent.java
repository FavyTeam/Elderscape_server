package game.content.miscellaneous;

import core.ServerConstants;
import game.content.staff.StaffActivity;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import utility.Misc;

/**
 * Track what kind of gameplay the player does the most. Such as 30% skilling, 10% Pking, 60% Pvm.
 *
 * @author MGT Madness, created on 03-01-2015.
 */
public class GameTimeSpent {


	public int minutes;

	public String date = "";

	/**
	 * Store a player's active minutes played in a specific date.
	 *
	 * @param minutes
	 * @param date
	 */
	public GameTimeSpent(int minutes, String date) {
		this.minutes = minutes;
		this.date = date;
	}

	/**
	 * Add +1 minute to the player's game time on a specific date.
	 */
	public static void addGameTime(Player player) {
		if (System.currentTimeMillis() - player.timeWeeklyGameTimeUsed < Misc.getMinutesToMilliseconds(1)) {
			return;
		}
		String date = Misc.getDateOnlyDashes();
		player.timeWeeklyGameTimeUsed = System.currentTimeMillis();
		for (int index = 0; index < player.activePlayedTimeDates.size(); index++) {
			GameTimeSpent instance = player.activePlayedTimeDates.get(index);
			if (instance.date.equals(date)) {
				instance.minutes++;
				return;
			}
		}
		player.activePlayedTimeDates.add(new GameTimeSpent(1, date));
		if (player.activePlayedTimeDates.size() > 7) {
			player.activePlayedTimeDates.remove(0);
		}
	}

	public static int getActivePlayTimeHoursInLastWeek(Player player) {
		int totalSeconds = 0;
		for (int index = 0; index < player.activePlayedTimeDates.size(); index++) {
			GameTimeSpent instance = player.activePlayedTimeDates.get(index);
			if (Misc.dateToMilliseconds(instance.date, "dd-MM-yyyy") < (System.currentTimeMillis() - Misc.getHoursToMilliseconds(24 * 7))) {
				continue;
			}
			totalSeconds += instance.minutes;
		}
		return (totalSeconds / 60);
	}


	public final static String PKING = "PKING";

	public final static String SKILLING = "SKILLING";

	public final static String PVM = "PVM";

	/**
	 * @param player The associated player.
	 * @param type The type of gameplay.
	 * @return The percentage of the given type of gameplay.
	 */
	public static int getPercentagePlayed(int[] timeSpent, String type) {
		double percentage = 0;
		double totalSpent = timeSpent[0] + timeSpent[1] + timeSpent[2];

		switch (type) {
			case PKING:
				percentage = timeSpent[0] / totalSpent;
				break;

			case SKILLING:
				percentage = timeSpent[1] / totalSpent;
				break;

			case PVM:
				percentage = timeSpent[2] / totalSpent;
				break;
		}
		percentage *= 100;
		return (int) percentage;
	}

	/**
	 * Increase the game spent time spent.
	 *
	 * @param player The associated player.
	 * @param type The activity type.
	 */
	public static void increaseGameTime(Player player, String type) {
		if (System.currentTimeMillis() - player.lastTimeSpentUsed < 5000) {
			return;
		}
		player.lastTimeSpentUsed = System.currentTimeMillis();
		switch (type) {
			case PKING:
				player.timeSpent[0]++;
				if (Area.inDangerousPvpArea(player) || Area.inCityPvpArea(player)) {
					GameTimeSpent.setLastActivity(player, "PVP DANGEROUS");
				} else if (Area.inClanWarsDangerousArea(player)) {
					GameTimeSpent.setLastActivity(player, "PVP CLAN WARS");
				}
				break;
			case SKILLING:
				player.timeSpent[1]++;
				if (Area.inDangerousPvpArea(player)) {
					GameTimeSpent.setLastActivity(player, "SKILL DANGEROUS");
				} else {
					GameTimeSpent.setLastActivity(player, "SKILL SAFE");
				}
				break;
			case PVM:
				player.timeSpent[2]++;
				if (Area.inDangerousPvpArea(player)) {
					GameTimeSpent.setLastActivity(player, "PVM DANGEROUS");
				} else {
					GameTimeSpent.setLastActivity(player, "PVM SAFE");
				}
				break;
		}
	}

	/**
	 * Set the player's last activity, used to know what activity the player did last.
	 */
	public static void setLastActivity(Player player, String type) {
		StaffActivity.addStaffActivity(player);
		player.lastActivity = type;
		player.lastActivityTime = System.currentTimeMillis();
	}

	private final static int PLAYERS_AMOUNT_TO_PRINT = 30;

	public static void printActivePlayers() {

		DiscordCommands.addOutputText("[" + Misc.getDateAndTime() + "] The " + PLAYERS_AMOUNT_TO_PRINT + " most active players who are currently online. {name:hours active in the last 7 days}");

		class Active {

			public String name;
			public int hours;
			public Active(String name, int hours) {
				this.name = name;
				this.hours = hours;
			}
		}
		List<Active> activeList = new ArrayList<Active>();
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player instance = PlayerHandler.players[index];
			if (instance == null) {
				continue;
			}
			activeList.add(new Active(instance.getPlayerName(), GameTimeSpent.getActivePlayTimeHoursInLastWeek(instance)));
		}

		Collections.sort(activeList, new Comparator<Active>() {
			public int compare(Active o1, Active o2) {
				// Sort ascending.
				if (o1.hours < o2.hours) {
					return 1;
				}
				if (o1.hours == o2.hours) {
					return 0;
				}
				return -1;
			}
		});
		for (int index = 0; index < activeList.size(); index++) {
			if (index > PLAYERS_AMOUNT_TO_PRINT - 1) {
				break;
			}
			DiscordCommands.addOutputText(activeList.get(index).name + ": " + activeList.get(index).hours + " hours");
		}
		DiscordBot.sendMessage(DiscordConstants.STAFF_CHAT_CHANNEL, DiscordCommands.queuedBotString, true);
	}

}
