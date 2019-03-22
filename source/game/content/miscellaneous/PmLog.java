package game.content.miscellaneous;

import game.player.Player;
import utility.Misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Private messaging log, to catch RWT.
 *
 * @author MGT Madness, created on 16-04-2017.
 */
public class PmLog {
	/**
	 * Store data of new ip logging into an account.
	 */
	public static ArrayList<String> pmLog = new ArrayList<String>();


	public final static String FILE_LOCATION = "backup/logs/pm/";

	/**
	 * Flagged terms alert for RWT.
	 * Becareful with pp because dropped/opposing/supplies/happen will be picked up
	 */
	private final static String[] flag =
			{"world", "w38", "w 38", "07", "osrs", "oldschool", "old school", "to 38", "at 38", "paypal", "pay pal", " pp", "pp ", "pp?", "$", "rsgp", "usd", "dupe", "exploit",
					"bug", "glitch"
			};

	public static ArrayList<String> alertLog = new ArrayList<String>();

	public static void savePmLog() {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());

		File folder = new File(FILE_LOCATION + date);
		if (!folder.exists()) {
			folder.mkdir();
		}
		int totalLoops = pmLog.size();
		for (int index = 0; index < totalLoops; index++) {
			if (pmLog.size() == 0) {
				break;
			}
			String parse1[] = pmLog.get(0).split("-");
			String nameToSave = parse1[0];
			String location = FILE_LOCATION + date + "/" + nameToSave + ".txt";
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(location, true));
				for (int pmLogIndex = 0; pmLogIndex < pmLog.size(); pmLogIndex++) {
					if (pmLogIndex < 0 || pmLogIndex > pmLog.size() - 1) {
						break;
					}
					try {
						String parse[] = pmLog.get(pmLogIndex).split("-");
						if (parse[0].equals(nameToSave)) {

							bw.write(parse[1]);
							bw.newLine();
							pmLog.remove(pmLogIndex);
							pmLogIndex--;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				bw.flush();
				bw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		pmLog.clear();
	}

	public static void addPmLog(Player fromPlayerInstance, Player toPlayerInstance, String fromName, String message) {
		String toName = toPlayerInstance.getPlayerName();
		String fromText = fromName + "-" + Misc.getDateAndTime() + " [" + Misc.capitalize(fromName) + "] to [" + Misc.capitalize(toName) + "]: " + message;
		pmLog.add(fromText);
		pmLog.add(toName + "-" + Misc.getDateAndTime() + " [" + Misc.capitalize(fromName) + "] to [" + Misc.capitalize(toName) + "]: " + message);
		toPlayerInstance.rwtChat.add("[" + Misc.getDateAndTime() + "] PM from [" + Misc.capitalize(fromName) + "] to [" + Misc.capitalize(toName) + "]: " + message);
		fromPlayerInstance.rwtChat.add("[" + Misc.getDateAndTime() + "] PM from [" + Misc.capitalize(fromName) + "] to [" + Misc.capitalize(toName) + "]: " + message);
		for (int index = 0; index < flag.length; index++) {
			if (message.toLowerCase().contains(flag[index])) {
				if (flag[index].equals(" pp") && message.toLowerCase().contains("ppl")) {
					continue;
				}
				alertLog.add(Misc.getDateAndTime() + " [" + fromName + "] to [" + toName + "] flagged for saying '" + flag[index] + "' message: " + message);
				break;
			}
		}
	}

}
