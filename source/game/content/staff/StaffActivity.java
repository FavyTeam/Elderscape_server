package game.content.staff;

import core.ServerConstants;
import game.content.miscellaneous.GameTimeSpent;
import game.player.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import utility.FileUtility;
import utility.Misc;

/**
 * Find out the activity of each staff member.
 *
 * @author MGT Madness, created on 31-10-2017.
 */
public class StaffActivity {

	public static long timeDailyStaffActivityChecked;

	public static long timeWeeklyStaffActivityChecked;

	public static List<StaffActivity> staffActivityList = new ArrayList<StaffActivity>();

	private String name = "";

	public String getName() {
		return name;
	}

	private int minutesActive;

	public int getMinutesActive() {
		return minutesActive;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	private void setMinutesActive(int amount) {
		minutesActive = amount;
	}

	private long timeMinuteActivitySent;

	private long getTimeMinuteActivitySent() {
		return timeMinuteActivitySent;
	}

	private void setTimeMinuteActivitySent(long amount) {
		timeMinuteActivitySent = amount;
	}

	private StaffActivity(String name, int minutesActive, long timeMinuteActivitySent) {
		this.name = name;
		this.minutesActive = minutesActive;
		this.timeMinuteActivitySent = timeMinuteActivitySent;
	}

	public static void addStaffActivity(Player player) {
		player.setTimePlayerLastActive(System.currentTimeMillis());
		GameTimeSpent.addGameTime(player);
		if (player.isAdministratorRank()) {
			return;
		}
		if (!player.isModeratorRank() && !player.isSupportRank()) {
			return;
		}
		if (player.privateChat != ServerConstants.PRIVATE_ON) {
			return;
		}

		StaffActivity instance = getInstance(player);
		if (instance == null) {
			staffActivityList.add(new StaffActivity(player.getPlayerName(), 1, System.currentTimeMillis()));
		} else {
			if (System.currentTimeMillis() - instance.getTimeMinuteActivitySent() < 60000) {
				return;
			}
			instance.setMinutesActive(instance.getMinutesActive() + 1);
			instance.setTimeMinuteActivitySent(System.currentTimeMillis());
		}
	}

	private static StaffActivity getInstance(Player player) {
		for (int index = 0; index < staffActivityList.size(); index++) {
			StaffActivity instance = staffActivityList.get(index);
			if (instance.getName().equals(player.getPlayerName())) {
				return instance;
			}
		}
		return null;
	}

	public static void checkStaffActivity(String time) {
		// Every 24 hours, it will check if it has been 7 days since statistics was last sent.
		if (time.startsWith("12") && time.endsWith("AM")) {
			// If it has been less than 2 hours, it means we already saved it somepoint at 12 AM +
			if (Misc.getTimeMilliseconds() - timeDailyStaffActivityChecked < Misc.getHoursToMilliseconds(2)) {
				return;
			}
			timeDailyStaffActivityChecked = System.currentTimeMillis();

			//168 hours is 7 days, so lets make it 166 hours incase something goes wrong.
			if (Misc.getTimeMilliseconds() - timeWeeklyStaffActivityChecked < Misc.getHoursToMilliseconds(166)) {
				return;
			}
			timeWeeklyStaffActivityChecked = System.currentTimeMillis();


			DiscordCommands.addOutputText("Staff activity statistics for the last 7 days:");
			for (int index = 0; index < staffActivityList.size(); index++) {
				StaffActivity instance = staffActivityList.get(index);
				String text = instance.getName() + ": " + Misc.roundDoubleToNearestTwoDecimalPlaces(((double) instance.getMinutesActive() / 60.0)) + " hours active.";
				DiscordCommands.addOutputText(text);
			}
			DiscordBot.sendMessage(DiscordConstants.STAFF_ACTIVITY_LOG_CHANNEL, DiscordCommands.queuedBotString, true);
			staffActivityList.clear();
		}
	}


	public static void saveStaffActivity() {
		ArrayList<String> line = new ArrayList<String>();
		line.add(timeDailyStaffActivityChecked + "");
		line.add(timeWeeklyStaffActivityChecked + "");
		FileUtility.deleteAllLines("backup/logs/staff/staff_activity_sent_times.txt");
		FileUtility.saveArrayContentsSilent("backup/logs/staff/staff_activity_sent_times.txt", line);

		FileUtility.deleteAllLines("backup/logs/staff/staff_activity_data.txt");
		ArrayList<String> list = new ArrayList<String>();
		for (StaffActivity data : StaffActivity.staffActivityList) {
			list.add(data.getName() + ServerConstants.TEXT_SEPERATOR + data.getMinutesActive() + ServerConstants.TEXT_SEPERATOR + data.getTimeMinuteActivitySent());
		}
		FileUtility.saveArrayContentsSilent("backup/logs/staff/staff_activity_data.txt", list);
	}

	public static void readStaffActivity() {
		ArrayList<String> data = FileUtility.readFile("backup/logs/staff/staff_activity_sent_times.txt");
		try {
			timeDailyStaffActivityChecked = Long.parseLong(data.get(0));
			timeWeeklyStaffActivityChecked = Long.parseLong(data.get(1));
		} catch (Exception e) {

		}

		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/staff/staff_activity_data.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
					String name = parse[0];
					int minutesActive = Integer.parseInt(parse[1]);
					long timeMinuteActivitySent = Long.parseLong(parse[2]);
					staffActivityList.add(new StaffActivity(name, minutesActive, timeMinuteActivitySent));
				}
			}
			file.close();
		} catch (Exception e) {
		}
	}

	public static void printCurrentActivity() {
		boolean sent = false;
		try {
			long time = (System.currentTimeMillis() - timeWeeklyStaffActivityChecked) / Misc.getHoursToMilliseconds(1);
			double days = (double) time / 24.0;
			DiscordCommands.addOutputText("Staff activity statistics for the last " + Misc.roundDoubleToNearestTwoDecimalPlaces(days) + " days:");
			sent = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!sent) {

			DiscordCommands.addOutputText("Staff activity statistics for the last ?  days:");
		}
		for (int index = 0; index < staffActivityList.size(); index++) {
			StaffActivity instance = staffActivityList.get(index);
			String text = instance.getName() + ": " + Misc.roundDoubleToNearestTwoDecimalPlaces(((double) instance.getMinutesActive() / 60.0)) + " hours active.";
			DiscordCommands.addOutputText(text);
		}
		DiscordBot.sendMessage(DiscordConstants.STAFF_CHAT_CHANNEL, DiscordCommands.queuedBotString, true);
	}

}
