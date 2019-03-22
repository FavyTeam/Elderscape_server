package game.content.staff;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import utility.FileUtility;
import utility.Misc;
import utility.TimeChanged;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Find out which hours of the day has little staff activity.
 *
 * @author MGT Madness, created on 31-10-2017.
 */
public class StaffPresence {

	private static long timeStaffPresenceStatisticsSent;

	public static List<StaffPresence> staffPresenceList = new ArrayList<StaffPresence>();

	private String hour = "";

	private int minutesPresent;

	public String getHour() {
		return hour;
	}

	public int getMinutesPresent() {
		return minutesPresent;
	}

	private void setMinutesPresent(int amount) {
		minutesPresent = amount;
	}

	private StaffPresence(String hour, int minutesPresent) {
		this.hour = hour;
		this.minutesPresent = minutesPresent;
	}

	public static void checkStaffPresence(String time) {


		// Send results in discord every 24 hours. Check for the whole hour incase the server restarted at 12:00 am and was offline for a few minutes.
		if (time.startsWith("12") && time.endsWith("AM")) {
			// If it has been less than 2 hours, it means we already saved it somepoint at 12 AM +
			if (Misc.getTimeMilliseconds() - timeStaffPresenceStatisticsSent < Misc.getHoursToMilliseconds(2)) {
				return;
			}
			timeStaffPresenceStatisticsSent = System.currentTimeMillis();
			TimeChanged.newDayStartedUpdate();
			return;
		}
		boolean moderatorOnline = false;
		for (int index = 0; index < ServerConstants.MAXIMUM_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.isModeratorRank()) {
				moderatorOnline = true;
				break;
			}
		}

		boolean objectFound = false;
		int objectIndex = -1;
		for (int index = 0; index < staffPresenceList.size(); index++) {
			StaffPresence instance = staffPresenceList.get(index);
			if (getCurrentHour(time).equals(instance.getHour())) {
				objectFound = true;
				objectIndex = index;
				break;
			}
		}

		// We add a new entry this way, because it is more accurate compared to waiting for time to become 07:00 PM for example. because
		// the server may have restarted at the start of an hour.
		if (!objectFound) {
			staffPresenceList.add(new StaffPresence(getCurrentHour(time), moderatorOnline ? 1 : 0));
		}

		if (moderatorOnline && objectFound) {
			StaffPresence instance = staffPresenceList.get(objectIndex);
			instance.setMinutesPresent(instance.getMinutesPresent() + 1);
		}
	}

	public static void saveStaffPresence() {
		FileUtility.deleteAllLines("backup/logs/staff/staff_presence_sent_time.txt");
		FileUtility.addLineOnTxt("backup/logs/staff/staff_presence_sent_time.txt", timeStaffPresenceStatisticsSent + "");

		FileUtility.deleteAllLines("backup/logs/staff/staff_presence_data.txt");
		ArrayList<String> line = new ArrayList<String>();
		for (StaffPresence data : StaffPresence.staffPresenceList) {
			line.add(data.getHour() + ServerConstants.TEXT_SEPERATOR + data.getMinutesPresent());
		}
		FileUtility.saveArrayContentsSilent("backup/logs/staff/staff_presence_data.txt", line);
	}

	public static void readStaffPresence() {
		ArrayList<String> data = FileUtility.readFile("backup/logs/staff/staff_presence_sent_time.txt");
		try {
			timeStaffPresenceStatisticsSent = Long.parseLong(data.get(0));
		} catch (Exception e) {

		}
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/staff/staff_presence_data.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
					String hour = parse[0];
					int minutesPresent = Integer.parseInt(parse[1]);
					StaffPresence.staffPresenceList.add(new StaffPresence(hour, minutesPresent));
				}
			}
			file.close();
		} catch (Exception e) {
		}
	}

	private static String getCurrentHour(String time) {
		String[] splitColon = time.split(":");
		String[] splitSpace = time.split(" ");
		return splitColon[0] + splitSpace[1];
	}
}
