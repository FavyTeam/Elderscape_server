package game.log;

import core.ServerConfiguration;
import utility.FileUtility;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Used to track new player ips used to create accounts.
 *
 * @author MGT Madness, created on 06-09-2016.
 */
public class NewPlayerIpTracker {

	/**
	 * All ips which have been used to create accounts on the server's history.
	 */
	public static ArrayList<String> ipCollectionList = new ArrayList<String>();

	/**
	 * All ips and name which have been used to create accounts on the server's history.
	 * Use name to search character file for playtime.
	 */
	public static ArrayList<String> ipCollectionListIpName = new ArrayList<String>();

	private static String currentDate = "";

	/**
	 * Save player ip if brand new.
	 */
	public static void saveIp(String ip, String name) {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		for (int index = 0; index < ipCollectionList.size(); index++) {
			if (ipCollectionList.get(index).equals(ip)) {
				return;
			}
		}
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		if (!currentDate.equals(dateFormat.format(cal.getTime()))) {
			save("DATE CHANGED ONLY SAVE DAYS FILE", currentDate);
		}
		ipCollectionList.add(ip);
		ipCollectionListIpName.add(ip + "-" + name); // Dash because name have spaces, so this is easier to parse.
	}

	public static void loadNewPlayerIpTrackerFiles() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		loadCollectionsFile();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		if (!loadLastDateStringFile(currentDate).equals(currentDate)) {
			currentDate = loadLastDateStringFile(currentDate);
			save("DATE CHANGED ONLY SAVE DAYS FILE", currentDate);
		}

	}

	/**
	 * Load the date of where the server was last saved.
	 */
	private static String loadLastDateStringFile(String date) {
		try {
			@SuppressWarnings("resource")
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/player base/last date.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					return line;
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	private static void loadCollectionsFile() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/player base/collection.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					ipCollectionList.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveCollectionAndCurrentIps(boolean oldDate, String olderDate) {
		FileUtility.deleteAllLines("backup/logs/player base/collection.txt");
		FileUtility.saveArrayContents("backup/logs/player base/collection.txt", ipCollectionList);
	}

	/**
	 * This is called when the date has changed or server save.
	 */
	public static void save(String action, String oldDate) {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();

		if (action.contains("DATE CHANGED")) {
			if (action.contains("ONLY SAVE DAYS FILE")) {
				//Keep empty, it had old unused code here.
			} else {
				saveCollectionAndCurrentIps(false, "");
			}
			currentDate = dateFormat.format(cal.getTime());
		}

		if (action.equals("SAVE DATA")) {
			saveCollectionAndCurrentIps(true, currentDate);
			FileUtility.deleteAllLines("backup/logs/player base/last date.txt");
			FileUtility.addLineOnTxt("backup/logs/player base/last date.txt", currentDate);
		}
	}

}
