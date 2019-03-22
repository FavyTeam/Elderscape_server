package game.log;

import game.player.Player;
import utility.FileUtility;
import utility.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * In-depth tracking of where coins come and go from.
 *
 * @author MGT Madness, created on 08-08-2015.
 */
public class CoinEconomyTracker {
	private final static String LOCATION = "./backup/logs/coin income.txt";

	private final static String ECO_SINK_FILE_LOCATION = "./backup/logs/coin sink.txt";

	/**
	 * incomeList.add("ACHIEVEMENT-INCOME 90000");
	 */
	public static ArrayList<String> incomeList = new ArrayList<String>();

	public static ArrayList<String> ecoSinkList = new ArrayList<String>();


	public static ArrayList<String> donatorItemsBought = new ArrayList<String>();

	public static void addDonatorItemsBought(Player player, String string) {
		if (player.isAdministratorRank()) {
			return;
		}
		donatorItemsBought.add(string);
	}

	public static void addIncomeList(Player player, String string) {
		String[] split = string.split(" ");
		if (split[1].isEmpty()) {
			Misc.print("Invalid addIncomeList: " + string);
			return;
		}
		if (!Misc.isNumeric(split[1])) {
			Misc.print("Invalid addIncomeList: " + string);
			return;
		}
		if (player.isAdministratorRank()) {
			return;
		}
		incomeList.add(string);
	}

	public static void addSinkList(Player player, String string) {
		if (player != null) {
			if (player.isAdministratorRank()) {
				return;
			}
		}
		ecoSinkList.add(string);
	}

	/**
	 * Read the coin economy logs.
	 */
	public static void readCoinEconomyLog() {
		try (BufferedReader file = new BufferedReader(new FileReader(LOCATION))) {
			String line;

			while ((line = file.readLine()) != null) {
				line = line.replaceAll(",", "");
				incomeList.add(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try (BufferedReader file = new BufferedReader(new FileReader("./backup/logs/donatoritemsbought.txt"))) {

			String line;
			while ((line = file.readLine()) != null) {
				line = line.replaceAll(",", "");
				donatorItemsBought.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (BufferedReader file = new BufferedReader(new FileReader(ECO_SINK_FILE_LOCATION))) {
			String line;
			while ((line = file.readLine()) != null) {
				line = line.replaceAll(",", "");
				ecoSinkList.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the coin economy logs.
	 */
	public static void updateCoinEconomyLog() {
		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList = new ArrayList<String>();
		for (int index = 0; index < incomeList.size(); index++) {
			String currentString = incomeList.get(index);
			int lastIndex = currentString.lastIndexOf(" ");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;

			for (int i = 0; i < finalIncomeList.size(); i++) {
				if (finalIncomeList.get(i).startsWith(matchToFind)) {
					try {
						int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
						int finalNumberValue = Integer.parseInt(finalIncomeList.get(i).replaceAll(",", "").substring(lastIndex + 1));
						int finalValueAdded = (finalNumberValue + numberValue);
						finalIncomeList.remove(i);
						finalIncomeList.add(i, matchToFind + " " + Misc.formatNumber(finalValueAdded));
						finalIncomeListHas = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (!finalIncomeListHas) {
				String[] split = currentString.split(" ");
				if (!split[1].contains(",")) {
					split[1] = Misc.formatNumber(Integer.parseInt(split[1]));
				}
				finalIncomeList.add(split[0] + " " + split[1]);
			}
		}
		FileUtility.deleteAllLines(LOCATION);
		FileUtility.saveArrayContents(LOCATION, finalIncomeList);

		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		ArrayList<String> finalIncomeList1 = new ArrayList<String>();
		for (int index = 0; index < donatorItemsBought.size(); index++) {
			String currentString = donatorItemsBought.get(index);
			int lastIndex = currentString.lastIndexOf("#");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;

			for (int i = 0; i < finalIncomeList1.size(); i++) {
				if (finalIncomeList1.get(i).startsWith(matchToFind)) {
					try {
						int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
						String string5 = finalIncomeList1.get(i).replaceAll(",", "").substring(lastIndex + 1);
						int finalNumberValue = Integer.parseInt(string5);
						int finalValueAdded = (finalNumberValue + numberValue);
						finalIncomeList1.remove(i);
						finalIncomeList1.add(i, matchToFind + "#" + Misc.formatNumber(finalValueAdded));
						finalIncomeListHas = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (!finalIncomeListHas) {
				String[] split = currentString.split("#");
				if (!split[1].contains(",")) {
					split[1] = Misc.formatNumber(Integer.parseInt(split[1]));
				}
				finalIncomeList1.add(split[0] + "#" + split[1]);
			}
		}
		FileUtility.deleteAllLines("./backup/logs/donatoritemsbought.txt");
		FileUtility.saveArrayContents("./backup/logs/donatoritemsbought.txt", finalIncomeList1);

		// Add up all the entries into a final arraylist and then write the final arraylist to a .txt file.
		finalIncomeList1 = new ArrayList<String>();
		for (int index = 0; index < ecoSinkList.size(); index++) {
			String currentString = ecoSinkList.get(index);
			int lastIndex = currentString.lastIndexOf(" ");
			String matchToFind = currentString.substring(0, lastIndex);
			boolean finalIncomeListHas = false;

			for (int i = 0; i < finalIncomeList1.size(); i++) {
				if (finalIncomeList1.get(i).startsWith(matchToFind)) {
					try {
						int numberValue = Integer.parseInt(currentString.substring(lastIndex + 1));
						int finalNumberValue = Integer.parseInt(finalIncomeList1.get(i).replaceAll(",", "").substring(lastIndex + 1));
						int finalValueAdded = (finalNumberValue + numberValue);
						finalIncomeList1.remove(i);
						finalIncomeList1.add(i, matchToFind + " " + Misc.formatNumber(finalValueAdded));
						finalIncomeListHas = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (!finalIncomeListHas) {
				String[] split = currentString.split(" ");
				if (!split[1].contains(",")) {
					split[1] = Misc.formatNumber(Integer.parseInt(split[1]));
				}
				finalIncomeList1.add(split[0] + " " + split[1]);
			}
		}
		FileUtility.deleteAllLines("./backup/logs/coin sink.txt");
		FileUtility.saveArrayContents("./backup/logs/coin sink.txt", finalIncomeList1);
	}

}
