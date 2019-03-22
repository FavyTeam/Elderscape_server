package utility;

import core.Server;
import core.ServerConfiguration;
import game.content.miscellaneous.ServerShutDownUpdate;
import game.player.PlayerHandler;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip the character folder & logs and store into a safe location.
 *
 * @author MGT Madness, created on ? Last edited on 16-08-2017
 */
public class CharacterBackup {

	public static String CHARACTER_FOLDER = "./backup/";

	public static String BACKUP_FOLDER = "";

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public static String getDate() {
		String output = Misc.getDateAndTime();
		output = output.replaceAll(" ", "");
		output = output.replaceAll("/", "-");
		output = output.replaceAll(":", "-");
		output = output.replaceAll(",", " (");
		return output;
	}

	public static void backUpSystem() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		if (PlayerHandler.canTakeAction) {
			if (PlayerHandler.restart) {
				Server.restart();
			}
			if (PlayerHandler.logOut) {
				System.exit(0);
			}
			PlayerHandler.canTakeAction = false;
		}
		ServerShutDownUpdate.serverAutoLogsSaveLoop();
	}

	public static Timer timer1 = new Timer();

	public static TimerTask myTask1 = new TimerTask() {
		@Override
		public void run() {
			// Executed every 24 hours.
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			String timeBackedUpToDropbox = FileUtility.readFirstLine("backup/logs/dropbox_time.txt");
			boolean shouldBackup = false;
			if (timeBackedUpToDropbox.isEmpty()) {
				shouldBackup = true;
			}
			if (!timeBackedUpToDropbox.isEmpty()) {
				long time = Long.parseLong(timeBackedUpToDropbox);
				if (System.currentTimeMillis() - time >= Misc.getHoursToMilliseconds(24)) {
					shouldBackup = true;
				}
			}
			if (shouldBackup) {
				FileUtility.deleteAllLines("backup/logs/dropbox_time.txt");
				FileUtility.addLineOnTxt("backup/logs/dropbox_time.txt", System.currentTimeMillis() + "");
				Misc.print("Every 24 hours server backup to dropbox.");
				createBackUp(false, true);
			}
		}
	};

	public static boolean FORCE_LOCAL_SAVE = true;

	private static boolean zippingActive;

	public static void createBackUp(boolean shutdown, boolean forceDropbox) {
		long time = System.currentTimeMillis();
		File folder = new File(CHARACTER_FOLDER);
		if ((ServerConfiguration.DEBUG_MODE || FORCE_LOCAL_SAVE) && !forceDropbox) {
			BACKUP_FOLDER = "archives/server_" + getDate() + ").zip";
		} else {
			BACKUP_FOLDER = System.getProperty("user.home") + "/Dropbox/server_backup/server_" + getDate() + ").zip";
		}
		File zipped = new File(BACKUP_FOLDER);
		if (!zipped.exists()) {
			try {
				if (folder.list().length == 0) {
					return;
				}
				if (zippingActive) {
					return;
				}
				zippingActive = true;
				zipDirectory(folder, zipped);
				zippingActive = false;

				if (shutdown) {
					PlayerHandler.canTakeAction = true;
					Misc.print("Backup time elapsed: " + (System.currentTimeMillis() - time));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (shutdown) {
				PlayerHandler.canTakeAction = true;
				Misc.print("Backup already exists.");
			}
		}
		if (shutdown) {
			File execute = new File("economy server only.bat");
			try {
				Desktop.getDesktop().open(execute);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Misc.print("Backup time elapsed: " + (System.currentTimeMillis() - time));
	}

	/**
	 * Zip.
	 *
	 * @param directory the directory
	 * @param base the base
	 * @param zos the zos
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[20000];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				if (!files[i].exists()) {
					continue;
				}
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}

	/**
	 * Zip directory.
	 *
	 * @param folder the f
	 * @param zf the zf
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void zipDirectory(File folder, File zf) throws IOException {
		ZipOutputStream zipped = new ZipOutputStream(new FileOutputStream(zf));
		zip(folder, folder, zipped);
		zipped.close();
	}
}
