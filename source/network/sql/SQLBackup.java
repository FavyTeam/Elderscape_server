package network.sql;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import utility.CharacterBackup;
import utility.Misc;

/**
 * Creating Sql backups.
 * @author MGT Madness, created on 21-08-2018
 */
public class SQLBackup {

	private final static String SQL_FOLDER_LOCATION_ON_WINDOWS = "C:/ProgramData/MySQL/MySQL Server 5.7/Data/server";

	public static void initiateSqlBackupTimers() {
		hourlyTimerInstance.schedule(hourlyTaskInstance, 0, Misc.getHoursToMilliseconds(1));
		dailyTimerInstance.schedule(dailyTaskInstance, Misc.getHoursToMilliseconds(2), Misc.getHoursToMilliseconds(24));
	}
	private static Timer hourlyTimerInstance = new Timer();

	private static TimerTask hourlyTaskInstance = new TimerTask() {
		@Override
		public void run() {
			createBackup(SQL_FOLDER_LOCATION_ON_WINDOWS, "archives/sql_" + CharacterBackup.getDate() + ".zip");
		}
	};
	private static Timer dailyTimerInstance = new Timer();

	private static TimerTask dailyTaskInstance = new TimerTask() {
		@Override
		public void run() {
			createBackup(SQL_FOLDER_LOCATION_ON_WINDOWS, System.getProperty("user.home") + "/Dropbox/server_backup/sql_" + CharacterBackup.getDate() + ".zip");
		}
	};

	public static void createBackup(String from, String to) {
		File fromFolder = new File(from);
		File toFolder = new File(to);
		if (!toFolder.exists()) {
			try {
				if (fromFolder.list().length == 0) {
					return;
				}
				CharacterBackup.zipDirectory(fromFolder, toFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
