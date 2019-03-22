package utility;

import core.ServerConfiguration;
import core.ServerConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General file related methods.
 *
 * @author MGT Madness, created on 25-01-2015.
 */
public class FileUtility {

	public static void editCharacterFile(String name, String variable, String result) {
		new Thread(new Runnable() {
			public void run() {
				try {
					BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name + ".txt"));
					String line;
					String input = "";
					while ((line = file.readLine()) != null) {
						if (line.startsWith(variable + " =")) {
							line = variable + " = " + result;
						}
						input += line + '\n';
					}
					FileOutputStream File = new FileOutputStream(ServerConstants.getCharacterLocation() + name + ".txt");
					File.write(input.getBytes());
					file.close();
					File.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void deleteInactiveFilesInDirectory(boolean deleteEmptyFolders, String directory, int daysInactive, String specificFileExtensions) {
		if (!new File(directory).exists()) {
			return;
		}
		directory = directory.replace("/", "\\");
		List<File> tasks = new ArrayList<File>();
		tasks = getFilesList(directory);
		
		for (int index = 0; index < tasks.size(); index++) {
			String path = tasks.get(index).getPath();
			if (!specificFileExtensions.isEmpty() && !path.endsWith(specificFileExtensions)) {
				continue;
			}
			long daysFileInactive = (System.currentTimeMillis() - tasks.get(index).lastModified()) / Misc.getHoursToMilliseconds(24);
			if (daysFileInactive >= daysInactive) {
				tasks.get(index).delete();
				if (deleteEmptyFolders) {
					int folderLength = tasks.get(index).getParentFile().list().length;
					if (folderLength == 0 && !tasks.get(index).getParentFile().toString().equals(directory)) {
						tasks.get(index).getParentFile().delete();
					}
				}
			}
		}
	}
	public static List<File> getFilesList(String directoryName) {
		File directory = new File(directoryName);

		List<File> resultList = new ArrayList<File>();

		// get all the files from a directory
		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			if (file.isFile()) {
			} else if (file.isDirectory()) {
				resultList.addAll(getFilesList(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
	public static ArrayList<String> readFile(String location) {
		ArrayList<String> arraylist = new ArrayList<String>();
		if (!new File(location).exists()) {
			return arraylist;
		}
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					arraylist.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
		}
		return arraylist;
	}

	public static ArrayList<Integer> readFileInteger(String location) {
		ArrayList<Integer> arraylist = new ArrayList<Integer>();
		if (!new File(location).exists()) {
			return arraylist;
		}
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					arraylist.add(Integer.parseInt(line));
				}
			}
			file.close();
		} catch (Exception e) {
		}
		return arraylist;
	}

	public static ArrayList<String> readFileAndSaveEmptyLine(String location) {
		ArrayList<String> arraylist = new ArrayList<String>();
		if (!new File(location).exists()) {
			return arraylist;
		}
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				arraylist.add(line);
			}
			file.close();
		} catch (Exception e) {
		}
		return arraylist;
	}

	/**
	 * Delete all lines in a file.
	 *
	 * @param fileLocation The location of the file.
	 */
	public static void deleteAllLines(String fileLocation) {
		if (!FileUtility.fileExists(fileLocation)) {
			return;
		}
		try {
			FileOutputStream writer = new FileOutputStream(fileLocation);
			writer.write((new String()).getBytes());
			writer.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Check if the file exists.
	 *
	 * @param location The location of the file.
	 * @return True, if the file exists.
	 */
	public static boolean fileExists(String location) {
		File firstFolder = new File(location);
		if (firstFolder.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * True if the character file exists.
	 */
	public static boolean accountExists(String name) {
		if (FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt")) {
			return true;
		}
		return false;
	}


	/**
	 * Add a line of txt in a .txt file.
	 *
	 * @param location Location of the .txt file.
	 * @param line The line to add in the .txt file.
	 */
	public static void addLineOnTxt(String location, String line) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(location, true));
			bw.write(line);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void saveArrayContentsSilent(String location, ArrayList<?> arraylist) {
		if (arraylist.isEmpty()) {
			return;
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(location, true));

			for (int index = 0; index < arraylist.size(); index++) {
				bw.write("" + arraylist.get(index));
				bw.newLine();
			}

			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static boolean saveArrayContents(String location, ArrayList<?> arraylist) {
		boolean wrote = false;
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(location, true));

			for (int index = 0; index < arraylist.size(); index++) {
				bw.write("" + arraylist.get(index));
				bw.newLine();
				wrote = true;
			}

			bw.flush();
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		if (wrote) {
			if (ServerConfiguration.DEBUG_MODE) {
				Misc.printDontSave("Written to file: " + location);
			}
		}
		return wrote;
	}

	/**
	 * @return The first line in a text file.
	 */
	public static String readFirstLine(String location) {
		if (!FileUtility.fileExists(location)) {
			return "";
		}
		try (BufferedReader file = new BufferedReader(new FileReader(location))) {
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					return line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
}
