package utility;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DropboxApi {

	public static final String ACCESS_TOKEN = "_hxb_NXEB8AAAAAAAAAAC-e_Ekjo-DHPmUZ2Q3BklJTQUkliJM1sM6IJ42fkZCam";

	private static String outputLocationOnDropbox = "/bin.zip";
	public static DbxClientV2 dropboxClient = null;
	public static void main(String args[]) throws DbxException, IOException
	{
		WebsiteModified.updateDropboxImportantClientFiles("data.txt", false);
		WebsiteModified.updateDropboxImportantClientFiles("dawntained_client.jar", false);
		Misc.print("Completed");
	}

	public static Timer dropboxBinTimer = new Timer();

	public static TimerTask dropboxBinTask = new TimerTask() {
		@Override
		public void run() {
			try {
				if (dropboxClient == null) {
					return;
				}
				int number = dropboxClient.files().getMetadata(outputLocationOnDropbox).hashCode();
				Misc.print(number);
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	};
	/**
	 * Update the item.txt and items.txt file on dropbox, so the players can read the latest taken customs.
	 */
	public static void updateCustomItems(DbxClientV2 client, String text) {

		String lines[] = text.split("\\r?\\n");
		ArrayList<String> customItems = new ArrayList<String>();

		customItems.add("Custom items taken:");
		for (int index = 0; index < lines.length; index++) {
			String string = lines[index];
			if (string.contains("};")) {
				break;
			}

			if (string.contains("//")) {
				string = string.substring(string.indexOf("// ") + 3, string.length());
				customItems.add(string);
			}
		}
		FileUtility.deleteAllLines("backup/logs/github/items.txt");
		FileUtility.saveArrayContentsSilent("backup/logs/github/items.txt", customItems);
		uploadAndReplaceFile(client, "backup/logs/github/items.txt", "/items.txt");

	}


	/**
	 * Update the pet.txt and items.txt file on dropbox, so the players can read the latest taken customs.
	 */
	public static void updateCustomPets(DbxClientV2 client, String text) {

		String lines[] = text.split("\\r?\\n");
		String petType = "";

		ArrayList<String> itemPets = new ArrayList<String>();
		ArrayList<String> npcPets = new ArrayList<String>();

		for (int index = 0; index < lines.length; index++) {
			String string = lines[index];
			if (string.contains("// Custom item pets")) {
				petType = "item pets";
				itemPets.add("- Custom item pets that are taken:");
				continue;
			} else if (string.contains("// Custom npc pets below")) {
				petType = "npc pets";
				npcPets.add("");
				npcPets.add("");
				npcPets.add("- Custom npc pets that are taken:");
				continue;
			}

			if (!petType.isEmpty()) {
				if (string.contains("//")) {
					if (string.contains("formatter")) {
						continue;
					}
					string = string.trim();
					switch (petType) {
						case "item pets":
							itemPets.add(string.replaceAll("// ", ""));
							break;
						case "npc pets":
							npcPets.add(string.replaceAll("// ", ""));
							break;
					}
				}
			}
		}
		FileUtility.deleteAllLines("backup/logs/github/pets.txt");
		FileUtility.saveArrayContentsSilent("backup/logs/github/pets.txt", itemPets);
		FileUtility.saveArrayContentsSilent("backup/logs/github/pets.txt", npcPets);
		uploadAndReplaceFile(client, "backup/logs/github/pets.txt", "/pets.txt");

	}

	public static void uploadAndReplaceFile(DbxClientV2 client, String fileToUploadLocation, String dropboxOutputLocation) {
		// The file on your pc to upload.
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(fileToUploadLocation));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// The directory of where the file will be uploaded to on dropbox.
		try {
			@SuppressWarnings("unused")
			FileMetadata file = client.files().uploadBuilder(dropboxOutputLocation).withMode(WriteMode.OVERWRITE).withAutorename(true).withMute(true).uploadAndFinish(input);
		} catch (UploadErrorException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private static void uploadIfFileDoesNotExist(DbxClientV2 client, String inputLocation, String outputLocation) throws UploadErrorException, DbxException, IOException {
		// Upload "test.txt" to Dropbox. Will only upload if the file does not exist on the dropbox.
		try (InputStream in = new FileInputStream(inputLocation)) {
			FileMetadata metadata = client.files().uploadBuilder("/" + outputLocation).uploadAndFinish(in);
		}

	}

	@SuppressWarnings("unused")
	private static void printDropboxContent(DbxClientV2 client) throws ListFolderErrorException, DbxException {
		// Get files and folder metadata from Dropbox root directory
		ListFolderResult result = client.files().listFolder("");
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				Misc.print(metadata.getPathLower());
			}

			if (!result.getHasMore()) {
				break;
			}
			result = client.files().listFolderContinue(result.getCursor());
		}
	}

}
