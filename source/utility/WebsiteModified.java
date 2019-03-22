package utility;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import core.ServerConfiguration;
import core.ServerConstants;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Every 10 minutes, it will scan important files on the website and check its last modified date to see weather it changed, incase a hacker installed something on it.
 *
 * @author MGT Madness, created on 17-08-2017.
 */
public class WebsiteModified {
	/**
	 * List of important files on the website which could be changed by a hacker.
	 */
	private final static String[][] IMPORTANT_FILES = {
			//@formatter:off
			{"/home/admin/public_html/index.php", ""},
			{"/home/admin/public_html/store/index.php", ""},
			{"/home/admin/public_html/storebmt/index.php", ""},
			{"/home/admin/public_html/storebmt/settings.php", ""},
			{"/home/admin/public_html/vote/index.php", ""},
			{"/home/admin/public_html/game/dawntained_client.jar", "DROPBOX"},
			{"/home/admin/public_html/game/dawntained.jar", ""},
			{"/home/admin/public_html/game/data.txt", "DROPBOX"},
			{"/home/admin/public_html/game/dawntained_setup.exe", ""},
			{"/home/admin/public_html/game/jarfix.exe", ""},
			//@formatter:on
	};

	private final static String WEBSITE_FILE_HISTORY_LOCATION = "backup/logs/website_history.txt";

	public final static int WEBSITE_FILE_CHECK_INTERVAL = 10;

	private static void loadWebsiteFileModifiedHistory() {
		saveFileUid = FileUtility.readFile(WEBSITE_FILE_HISTORY_LOCATION);
	}

	/**
	 * File location#uid
	 * <p>
	 * The uid is the file modified time. It changes depending on your Pc's timezone or website webuzo cpanel timezone.
	 */
	public static ArrayList<String> saveFileUid = new ArrayList<String>();

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			Misc.print("Website modified check.");
			loadWebsiteFileModifiedHistory();
			WebsiteLogInDetails.readLatestWebsiteLogInDetails();
			checkWebsiteFilesForModification();
		}
	};

	public static void checkWebsiteFilesForModification() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_USERNAME, WebsiteLogInDetails.WEBSITE_URL, WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_PORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_PASSWORD);
			try {
				session.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp connection = (ChannelSftp) channel;

			ArrayList<String> newSaveFileUid = new ArrayList<String>();
			for (int index = 0; index < IMPORTANT_FILES.length; index++) {
				String fileLocationToCheck = IMPORTANT_FILES[index][0];
				boolean dropboxFile = IMPORTANT_FILES[index][1].equals("DROPBOX");
				try {
					SftpATTRS attrs = connection.lstat(fileLocationToCheck);

					// This is used as file uid.
					long fileModifiedTime = attrs.getMTime() * 1000L;
					boolean fileModified = true;
					for (int i = 0; i < saveFileUid.size(); i++) {
						String[] parse = saveFileUid.get(i).split(ServerConstants.TEXT_SEPERATOR);
						if (fileLocationToCheck.equals(parse[0]) && fileModifiedTime == Long.parseLong(parse[1])) {
							fileModified = false;
							break;
						}
					}
					if (fileModified) {
						// Email
						ArrayList<String> content = new ArrayList<String>();
						content.add("This file was changed within the last 10 minutes: " + fileLocationToCheck);
						content.add("Timezone of file user access time:  " + Misc.millisecondsToDateAndTime(attrs.getATime() * 1000L));
						content.add("Timezone of file user modified time:  " + Misc.millisecondsToDateAndTime(attrs.getMTime() * 1000L));
						EmailSystem.addPendingEmail("Website changed: " + fileLocationToCheck, content, "mohamed_ffs25ffs@yahoo.com");
						if (dropboxFile) {
							String[] parse = fileLocationToCheck.split("/");
							updateDropboxImportantClientFiles(parse[parse.length - 1], true);
						}
						if (fileLocationToCheck.endsWith("/game/data.txt")) {
							OsBotCommunication.addTextInDirectoryRandomized(OsBotCommunication.SERVER_FILE_LOCATION, "read_data_file_from_website=1");
						}
					}
					newSaveFileUid.add(fileLocationToCheck + ServerConstants.TEXT_SEPERATOR + fileModifiedTime);
				} catch (Exception e) {
					Misc.print("File does not exist? " + fileLocationToCheck);
					e.printStackTrace();
				}
			}
			if (newSaveFileUid.size() > 0) {
				FileUtility.deleteAllLines(WEBSITE_FILE_HISTORY_LOCATION);
				FileUtility.saveArrayContentsSilent(WEBSITE_FILE_HISTORY_LOCATION, newSaveFileUid);
				saveFileUid = newSaveFileUid;
			}

			connection.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDropboxImportantClientFiles(String fileName, boolean downloadFromWebsite) {
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
		DropboxApi.dropboxClient = new DbxClientV2(config, DropboxApi.ACCESS_TOKEN);
		if (downloadFromWebsite) {
			WebsiteLogInDetails.readLatestWebsiteLogInDetails();
			WebsiteBackup.downloadWebsiteFile("/home/admin/public_html/game", fileName, "backup/logs/website_resource/" + fileName);
		}
		DropboxApi.uploadAndReplaceFile(DropboxApi.dropboxClient, "backup/logs/website_resource/" + fileName, "/" + fileName);
	}

	public static void startWebsiteFilesModifiedCheck() {
		//Start a new thread execued in intervals.
		WebsiteModified.timer.schedule(WebsiteModified.myTask, 0, Misc.getMinutesToMilliseconds(WEBSITE_FILE_CHECK_INTERVAL));
	}
}
