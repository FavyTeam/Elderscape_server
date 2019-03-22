package game.content.title;

import core.ServerConstants;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TitleDefinitions {
	public static final TitleDefinitions[] DEFINITIONS = new TitleDefinitions[100];

	private static ArrayList<Integer> duplicateTitleIdsList = new ArrayList<Integer>();

	public TitleDefinitions(String titleTitle, boolean titleAfterName, String requirementsSubText1, String requirementsSubText2, int titleId, int completeAmount) {
		this.title = titleTitle;
		this.titleAfterName = titleAfterName;
		this.requirementsSubText1 = requirementsSubText1;
		this.requirementsSubText2 = requirementsSubText2;
		this.titleId = titleId;
		this.completeAmount = completeAmount;
	}

	public static TitleDefinitions[] getDefinitions() {
		return DEFINITIONS;
	}

	public final String title;

	public final boolean titleAfterName;

	public final String requirementsSubText1;

	public final String requirementsSubText2;

	public final int titleId;

	public final int completeAmount;

	public static void loadAllTitles() {
		skillingTitlesIndex[0] = 0;
		loadTitles("skilling");
		pkingTitlesIndex[0] = emptyDefinitionIndex;
		loadTitles("pking");
		miscTitlesIndex[0] = emptyDefinitionIndex;
		loadTitles("misc");
		storeTitleIdAndDefinitionIndex();
	}

	private final static String[] textData =
			{"Title:", "Title After Name:", "Requirements Sub Text:", "Requirements Sub Text:", "Title Id:", "Complete Amount:"};

	private static int emptyDefinitionIndex;

	public static int[] skillingTitlesIndex = new int[2];

	public static int[] pkingTitlesIndex = new int[2];

	public static int[] miscTitlesIndex = new int[2];

	private static boolean isDuplicateTitleId(int titleId) {
		if (duplicateTitleIdsList.contains(titleId)) {
			Misc.printWarning("Duplicate title id found at: " + titleId);
			return true;
		}
		return false;
	}

	public static void loadTitles(String name) {
		String line = "";
		boolean EndOfFile = false;
		BufferedReader fileLocation = null;
		try {
			fileLocation = new BufferedReader(new FileReader(ServerConstants.getOsrsGlobalDataLocation() + "content/titles/" + name + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Misc.print(name + " file not found.");
			return;
		}
		try {
			line = fileLocation.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int finishedReadingIndex = 0;
		String titleTitle = "";
		boolean titleAfterName = false;
		String requirementsSubText1 = "";
		String requirementsSubText2 = "";
		int titleId = -1;
		int completeAmount = 0;
		while (!EndOfFile && line != null) {


			finishedReadingIndex++;
			if (finishedReadingIndex == 7) {
				finishedReadingIndex = 0;
				if (!isDuplicateTitleId(titleId)) {
					duplicateTitleIdsList.add(titleId);
				}
				DEFINITIONS[emptyDefinitionIndex] = new TitleDefinitions(titleTitle, titleAfterName, requirementsSubText1, requirementsSubText2, titleId, completeAmount);
				emptyDefinitionIndex++;
			} else {
				for (int index = 0; index < textData.length; index++) {
					if (line.contains(textData[index])) {
						line = line.replaceAll(textData[finishedReadingIndex - 1] + " ", "");
						line = line.replaceAll(textData[finishedReadingIndex - 1], "");
						switch (finishedReadingIndex) {
							case 1:
								titleTitle = line;
								break;
							case 2:
								titleAfterName = line.equalsIgnoreCase("Yes") ? true : false;
								break;
							case 3:
								requirementsSubText1 = line;
								break;
							case 4:
								requirementsSubText2 = line;
								break;
							case 5:
								titleId = Integer.parseInt(line);
								break;
							case 6:
								if (line.isEmpty() || line.equals(" ")) {
									break;
								}
								completeAmount = Integer.parseInt(line);
								break;
						}
						break;
					}
				}
			}
			try {
				line = fileLocation.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		if (name.contains("skilling")) {
			skillingTitlesIndex[1] = emptyDefinitionIndex;
		} else if (name.contains("pking")) {
			pkingTitlesIndex[1] = emptyDefinitionIndex;
		} else if (name.contains("misc")) {
			miscTitlesIndex[1] = emptyDefinitionIndex;
		}
		if (!isDuplicateTitleId(titleId)) {
			duplicateTitleIdsList.add(titleId);
		}
		DEFINITIONS[emptyDefinitionIndex] = new TitleDefinitions(titleTitle, titleAfterName, requirementsSubText1, requirementsSubText2, titleId, completeAmount);
		emptyDefinitionIndex++;
		try {
			fileLocation.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, Integer> titleIdAndDefinitionIndex = new HashMap<Integer, Integer>();

	private static void storeTitleIdAndDefinitionIndex() {
		for (int index = 0; index < TitleDefinitions.getDefinitions().length; index++) {
			if (TitleDefinitions.getDefinitions()[index] == null) {
				return;
			}
			titleIdAndDefinitionIndex.put(TitleDefinitions.getDefinitions()[index].titleId, index);
		}
	}

}
