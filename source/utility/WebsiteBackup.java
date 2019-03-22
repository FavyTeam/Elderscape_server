package utility;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import core.ServerConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Every 24 hours, it will read the website apache dedicated server and check for a backup that is stored there and it will download it if the dropbox does not have it.
 *
 * @author MGT Madness, created on 12-08-2017.
 */
public class WebsiteBackup {
	private final static String WEBSITE_BACKUP_LOCATION = "/var/webuzo/backup";

	private final static String DROPBOX_BACKUP_LOCATION = System.getProperty("user.home") + "/Dropbox/website_backup/";

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			Misc.print("Website backup download check.");
			WebsiteLogInDetails.readLatestWebsiteLogInDetails();
			downloadWebsiteBackup();
		}
	};

	private static void downloadWebsiteBackup() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_USERNAME, WebsiteLogInDetails.WEBSITE_URL, WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_PORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(WebsiteLogInDetails.WEBSITE_DEDICATED_SERVER_PASSWORD);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp connection = (ChannelSftp) channel;

			// Store all the backup names of the website
			ArrayList<String> websiteFiles = new ArrayList<String>();
			@SuppressWarnings("rawtypes")
			Vector filelist = connection.ls(WEBSITE_BACKUP_LOCATION);
			for (int i = 0; i < filelist.size(); i++) {
				LsEntry entry = (LsEntry) filelist.get(i);
				String fileName = entry.getFilename();

				// Some times it just prints a dot and nothing after it.
				if (fileName.startsWith(".")) {
					continue;
				}

				// Some times there is a directory named /var/webuzo/backup/tmp
				if (!fileName.contains(".")) {
					continue;
				}
				websiteFiles.add(fileName);
			}


			ArrayList<String> dropboxFiles = new ArrayList<String>();
			final File folder = new File(DROPBOX_BACKUP_LOCATION);
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					continue;
				}
				dropboxFiles.add(fileEntry.getName());
			}
			for (int index = 0; index < websiteFiles.size(); index++) {
				String websiteFileName = websiteFiles.get(index);
				if (dropboxFiles.contains(websiteFileName)) {
					continue;
				}
				connection.get(WEBSITE_BACKUP_LOCATION + "/" + websiteFileName, DROPBOX_BACKUP_LOCATION + websiteFileName);
			}
			connection.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public static boolean downloadWebsiteFile(String websiteFolderLocation, String websiteFileLocation, String receiveDownloadLocation) {
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

			@SuppressWarnings(
					{"rawtypes", "unused"})
			Vector filelist = connection.ls(websiteFolderLocation);
			connection.get(websiteFolderLocation + "/" + websiteFileLocation, receiveDownloadLocation);
			connection.exit();
			session.disconnect();
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
			return false;
		} catch (SftpException e) {
			e.printStackTrace();
			return false;
		}
	}



	/*// Execute a command and grab its output.
	String command = "ls -la";
	Channel channel = session.openChannel("exec");
	((ChannelExec) channel).setCommand(command);
	channel.setInputStream(null);
	((ChannelExec) channel).setErrStream(System.err);
	
	InputStream input = null;
	try
	{
		input = channel.getInputStream();
	}
	catch (IOException e)
	{
		e.printStackTrace();
	}
	channel.connect();
	
	Misc.print("Channel Connected to machine " + WebsiteLogInDetails.WEBSITE_URL + " server with command: " + command);
	
	try
	{
		InputStreamReader inputReader = new InputStreamReader(input);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		String line = null;
	
		while ((line = bufferedReader.readLine()) != null)
		{
			Misc.print(line);
		}
		bufferedReader.close();
		inputReader.close();
	}
	catch (IOException ex)
	{
		ex.printStackTrace();
	}
	*/
}
