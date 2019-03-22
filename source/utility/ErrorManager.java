package utility;

import core.ServerConfiguration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

public class ErrorManager {

	private static int number;

	private static String historyLocation = "";

	public static String currentErrorLocation = "";

	private static String errorLocation = "";

	public static void loadErrorFile(String history, String error) throws FileNotFoundException {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		historyLocation = history;
		errorLocation = error;
		getLatestErrorFile();
		createNewErrorFile();
		updateHistory();
		FileOutputStream file = new FileOutputStream(ErrorManager.currentErrorLocation);
		Logger logger = new Logger(file, System.out);
		System.setErr(logger);
	}

	private static void getLatestErrorFile() {
		String text;
		try {
			BufferedReader file = new BufferedReader(new FileReader(historyLocation));
			String line;
			while ((line = file.readLine()) != null) {
				if (line.contains("latest")) {
					text = line.substring(7);
					number = Integer.parseInt(text);
					currentErrorLocation = errorLocation + (number + 1) + ".txt";
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtility.deleteAllLines(historyLocation);
	}

	private static void createNewErrorFile() {
		try {
			BufferedReader characterfile = new BufferedReader(new FileReader(currentErrorLocation));
			characterfile.close();
		} catch (Exception e) {
		}
	}

	private static void updateHistory() {
		FileUtility.addLineOnTxt(historyLocation, "latest " + Integer.toString(number + 1));
	}

}
