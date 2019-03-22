package tools;

import core.GameType;
import core.ServerConstants;
import game.content.commands.NormalCommand;
import game.content.miscellaneous.DiceSystem;
import game.content.tradingpost.TradingPost;
import game.content.tradingpost.TradingPostItems;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import tools.multitool.DawntainedMultiTool;
import utility.EcoScannerAlert;
import utility.FileUtility;
import utility.Misc;


public class EconomyScan {

	//Fix thissssssssss
	public static String charactersLocation() {
		if (charactersLocation.isEmpty()) {
			charactersLocation = ServerConstants.getCharacterLocationWithoutLastSlash();
		}
		return charactersLocation;
	}

	public static String charactersLocation = "";

	private static String oldCharactersLocation() {
		if (oldCharactersLocation.isEmpty()) {
			oldCharactersLocation = ServerConstants.getCharacterLocationWithoutLastSlash();
		}
		return oldCharactersLocation;
	}

	private static String oldCharactersLocation = "";

	private final static String administrators[] =
			{"mgt madness", "ronald"};

	private static boolean MGT_PC_SCAN = false;

	public static final void main(String[] args) {
		if (args[0].contains("DUPE_FINDER")) {
			MGT_PC_SCAN = true;
			resetVariables();
			runDuperFinderComparer();
			return;
		}
		resetVariables();
		runEconomyScan(args, false);
	}

	/**
	 * True if scanning economy using the automatic system.
	 */
	public static boolean doNotPrint;

	/**
	 * True if using PlayerAltScanner tool.
	 */
	public static boolean saveInArrayList;

	private static boolean wealthScan;

	private static int bankValueFlag;

	private static int itemToSearchFor; // Change to 0 to not search for any item.

	public static long economyTotalBloodMoney;

	public static long economyTotalBloodMoneyTemp;

	public static int donatorTokensGainedThisSession;

	public static int donatorTokensGainedThisHour;

	private static long totalOfItemSearchedInEconomy;

	public static boolean duperFinderScanFirstStage;

	public static boolean duperFinderScanSecondStage;

	private final static int WEALTH_DUPER_FINDER_FLAG = 100000;

	private final static int DUPER_INCREASE_FLAG_AMOUNT = 100000;

	/**
	 * Sore older character files folder data if their wealth is more than 100k. NAME-WEALTH
	 */
	public static ArrayList<String> olderFolderData = new ArrayList<String>();
	public static ArrayList<String> olderFolderDataNamesOnly = new ArrayList<String>();

	public static ArrayList<String> tempFolderDataNew = new ArrayList<String>();

	/**
	 * Store new character files folder data if their wealth is more than 100k. NAME-WEALTH
	 */
	public static ArrayList<String> newerFolderData = new ArrayList<String>();

	/**
	 * Accounts that never existed in the older folder and their banks is more than 100k.
	 */
	public static ArrayList<String> newAccountsData = new ArrayList<String>();

	public static String diceVaultLocation = "";

	public static String tradingPostOfferLocation = "";

	public static String tradingPostItemsLocation = "";


	/**
	 * Find who has gained the most wealth between 2 character file versions to find the possible duper.
	 */
	public static void runDuperFinderComparer() {
		String args[] = new String[2];
		args[0] = "0";
		args[1] = "0";
		tempFolderDataNew.clear();
		if (olderFolderData.isEmpty()) {
			Misc.print("Stage 1 starting.");
			setUpDuperFinderComparerLocations("backup", true);
			loadStartupFiles(false);
			duperFinderScanFirstStage = true;
			doNotPrint = true;
			runEconomyScan(args, false);
			Misc.print("Stage 1 finished.");
			economyTotalBloodMoneyTemp = economyTotalBloodMoney;
			if (MGT_PC_SCAN) {
				secondStageScan(args, true);
			}
		} else {
			secondStageScan(args, false);
		}
	}

	private static void secondStageScan(String args[], boolean mgtPcScan) {
		long previousScanBloodMoneyTotal = economyTotalBloodMoneyTemp;
		Misc.print("Stage 2 starting.");
		// Second round of scanning new folder
		setUpDuperFinderComparerLocations("backup", false);
		economyTotalBloodMoney = 0;
		loadStartupFiles(true);
		duperFinderScanFirstStage = false;
		duperFinderScanSecondStage = true;
		doNotPrint = true;
		runEconomyScan(args, mgtPcScan);
		economyTotalBloodMoney -= (donatorTokensGainedThisHour / 10) * ServerConstants.getDonatorShopBloodMoneyRewardAmount();
		Misc.print("Stage 2 finished: " + Misc.formatNumber(economyTotalBloodMoney) + ", tokens this hour: " + Misc.formatNumber(donatorTokensGainedThisHour));
		Misc.print("Previous economy scan an hour ago: " + Misc.formatNumber(previousScanBloodMoneyTotal));

		// Print out results.
		Misc.print("Old folder size: " + olderFolderData.size());
		Misc.print("New folder size: " + newerFolderData.size());
		Misc.print("Fresh accounts size: " + newAccountsData.size());

		ArrayList<String> compiledWealth = new ArrayList<String>();
		for (int index = 0; index < olderFolderData.size(); index++) {
			String parse[] = olderFolderData.get(index).split("-");
			String name = parse[0];
			long wealth = Long.parseLong(parse[1]);

			for (int a = 0; a < newerFolderData.size(); a++) {
				String secondParse[] = newerFolderData.get(a).split("-");
				if (name.equals(secondParse[0])) {
					long wealthIncrease = Long.parseLong(secondParse[1]) - wealth;
					if (wealthIncrease >= DUPER_INCREASE_FLAG_AMOUNT) {
						compiledWealth.add(name + "-" + wealthIncrease);
						// Misc.print(name + " gained: " + Misc.formatRunescapeStyle(wealthIncrease));
					}
					newerFolderData.remove(a);
					break;
				}
			}
		}
		for (int index = 0; index < newAccountsData.size(); index++) {
			String parse[] = newAccountsData.get(index).split("-");
			String name = parse[0];
			long wealth = Long.parseLong(parse[1]);
			compiledWealth.add(name + "(new)-" + wealth);
		}
		compiledWealth = NormalCommand.sort(compiledWealth, "-");

		long ecoIncreasedBy = economyTotalBloodMoney - previousScanBloodMoneyTotal;
		if (ecoIncreasedBy >= 1) {
			String duperList = "";
			for (int index = 0; index < compiledWealth.size(); index++) {
				String parse[] = compiledWealth.get(index).split("-");
				String name = parse[0];
				long wealth = Long.parseLong(parse[1]);
				duperList = duperList + name + " gained " + Misc.formatRunescapeStyle(wealth) + ", ";
				if (index == EcoScannerAlert.RICHEST_PLAYERS_TO_FLAG - 1) {
					break;
				}
			}
			ArrayList<String> content = new ArrayList<String>();
			content.add(Misc.getDateAndTimeLog());
			content.add("Stage 2 finished: " + Misc.formatNumber(economyTotalBloodMoney) + ", tokens this hour: " + Misc.formatNumber(donatorTokensGainedThisHour));
			content.add("Previous economy scan an hour ago: " + Misc.formatNumber(previousScanBloodMoneyTotal));
			content.add("Economy now: " + Misc.formatNumber(economyTotalBloodMoney));
			content.add("Economy increased by: " + Misc.formatRunescapeStyle(economyTotalBloodMoney - previousScanBloodMoneyTotal));
			content.add(duperList);
			boolean dupe = ecoIncreasedBy >= (EcoScannerAlert.ECONOMY_MILLIONS_FLAG_AMOUNT * 1000000);
			if (dupe) {
				String string = "Possible dupe: " + Misc.formatRunescapeStyle(ecoIncreasedBy);
				for (int index = 0; index < content.size(); index++) {
					string = string + ", " + content.get(index);
				}
				if (!MGT_PC_SCAN) {
					//TwilioApi.callAdmin(string, "");
				}
			}
			FileUtility.saveArrayContents("backup/logs/economy_richest_players.txt", content);
			// TODO phone me, add these flagged players to packetlog ip, uid & flag their names and scan
			// everyone online for alts that are online.
			// Since this is on a seperate program as Server crash program, make it communicate through a text
			// file to inform server to check online
			// players for alts, then flag them.
		}

		Misc.print("Economy increased by: " + Misc.formatRunescapeStyle(economyTotalBloodMoney - previousScanBloodMoneyTotal));
		duperFinderScanSecondStage = false;
		newAccountsData.clear();
		economyTotalBloodMoneyTemp = economyTotalBloodMoney;
		economyTotalBloodMoney = 0;
		olderFolderData.clear();
		for (int index = 0; index < tempFolderDataNew.size(); index++) {
			olderFolderData.add(tempFolderDataNew.get(index));
		}
		donatorTokensGainedThisHour = 0;
	}

	private final static String OLD_CHARACTERS_MGT_PC_SCAN = "28-04-2018 (11-54-PM)";
	private final static String NEW_CHARACTERS_MGT_PC_SCAN = "29-04-2018 (12-54-AM)";
	private static void setUpDuperFinderComparerLocations(String folderDate, boolean old) {
		charactersLocation = folderDate + "/characters/players" + (GameType.isPreEoc() ? "_pre-eoc" : "");
		if (MGT_PC_SCAN) {
			// Old
			charactersLocation = System.getProperty("user.home") + "/Desktop/" + OLD_CHARACTERS_MGT_PC_SCAN + "/characters/players" + (GameType.isPreEoc() ? "_pre-eoc" : "");
		}
		if (old) {
			oldCharactersLocation = charactersLocation;
			if (MGT_PC_SCAN) {
				// New
				oldCharactersLocation = System.getProperty("user.home") + "/Desktop/" + NEW_CHARACTERS_MGT_PC_SCAN + "/characters/players" + (GameType.isPreEoc() ? "_pre-eoc" : "");
			}
		}
		diceVaultLocation = folderDate + "/logs/dice/vault.txt";
		tradingPostOfferLocation = folderDate + "/logs/trading_post/offers.txt";
		tradingPostItemsLocation = folderDate + "/logs/trading_post/items.txt";
		if (MGT_PC_SCAN) {
			diceVaultLocation = System.getProperty("user.home") + "/Desktop/" + OLD_CHARACTERS_MGT_PC_SCAN + "/logs/dice/vault.txt";
			tradingPostOfferLocation = System.getProperty("user.home") + "/Desktop/" + OLD_CHARACTERS_MGT_PC_SCAN + "/logs/trading_post/offers.txt";
			tradingPostItemsLocation = System.getProperty("user.home") + "/Desktop/" + OLD_CHARACTERS_MGT_PC_SCAN + "/logs/trading_post/items.txt";
		}
	}

	private static void resetVariables() {
		doNotPrint = false;
		wealthScan = false;
		bankValueFlag = 0;
		itemToSearchFor = 0;
		economyTotalBloodMoney = 0;
		totalOfItemSearchedInEconomy = 0;
	}

	/**
	 * Check if eco has increased more than x million blood money in the last 12 hours.
	 */
	public static void runScheduledEcoIncreaseScan() {
		resetVariables();
		doNotPrint = true;
		String args[] = new String[2];
		args[0] = "0";
		args[1] = "0";
		EconomyScan.loadStartupFiles(false);
		runEconomyScan(args, false);

	}


	public static void runAltScan() {
		doNotPrint = true;
		saveInArrayList = true;
		String args[] = new String[2];
		args[0] = "0";
		args[1] = "0";
		runEconomyScan(args, false);

	}

	private static void runEconomyScan(String[] args, boolean mgtPcScan) {
		if (mgtPcScan) {
			charactersLocation = oldCharactersLocation;
			if (MGT_PC_SCAN) {
				diceVaultLocation = System.getProperty("user.home") + "/Desktop/" + NEW_CHARACTERS_MGT_PC_SCAN + "/logs/dice/vault.txt";
				tradingPostOfferLocation = System.getProperty("user.home") + "/Desktop/" + NEW_CHARACTERS_MGT_PC_SCAN + "/logs/trading_post/offers.txt";
				tradingPostItemsLocation = System.getProperty("user.home") + "/Desktop/" + NEW_CHARACTERS_MGT_PC_SCAN + "/logs/trading_post/items.txt";
				EconomyScan.loadStartupFiles(false);
			}
		}
		long start = System.currentTimeMillis();
		if (!doNotPrint) {
			Misc.printDontSave("Scanning started.");
		}
		if (args.length == 1) {
			wealthScan = true;
		} else {
			itemToSearchFor = Integer.parseInt(args[0]);
			bankValueFlag = Integer.parseInt(args[1]);
		}
		if (itemToSearchFor > 0) {
			if (!doNotPrint) {
				Misc.printDontSave("Scanning for item: " + itemToSearchFor);
			}
		} else {
			if (!doNotPrint) {
				Misc.printDontSave("Scanning for bank wealth above: " + bankValueFlag);
			}
		}

		// If i re-read the trading post data, it will dupe everyone's entries. This is because doNotPrint is set to true only when using the same game thread.
		if (!doNotPrint) {
			loadStartupFiles(false);
		}
		scanFolder();
		if (!doNotPrint) {
			Misc.printDontSave("Scanning finished took: " + (System.currentTimeMillis() - start) + " milliseconds");
		}
	}

	/**
	 * Load required item defs, blood money price, dice logs, trading post logs for economy scan.
	 */
	public static void loadStartupFiles(boolean ignoreItemDefs) {
		if (!ignoreItemDefs) {
			ItemDefinition.loadItemDefinitionsAll();
			BloodMoneyPrice.loadBloodMoneyPrice();
		}
		DiceSystem.readDepositVaultFile();
		TradingPost.readTradingPostOffers();
		TradingPostItems.readTradingPostItems();
	}

	public static long lastTimeDonated;

	public static String lastDonationName = "";

	public static void scanAccount(File paramFile) {
		if (!paramFile.exists()) {
			return;
		}
		long bankWorth = 0;
		String name = paramFile.getName().replaceAll(".txt", "");
		for (int i = 0; i < administrators.length; i++) {
			if (administrators[i].equalsIgnoreCase(name)) {
				return;
			}
		}
		String str1 = "";
		String str2 = "";
		String str3 = "";
		String[] arrayOfString = new String[3];
		int j = 0;
		int k = 0;
		int[] lootingBagStorageItemId = new int[28];
		int[] lootingBagStorageItemAmount = new int[28];
		BufferedReader localBufferedReader = null;
		try {
			localBufferedReader = new BufferedReader(new FileReader(paramFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		long amountFound = 0;
		while ((j == 0) && (str1 != null)) {
			str1 = str1.trim();
			int n = str1.indexOf("=");
			if (n > -1) {
				str2 = str1.substring(0, n);
				str2 = str2.trim();
				str3 = str1.substring(n + 1);
				str3 = str3.trim();
				arrayOfString = str3.split("\t");
				int id = 0;
				long value = 0;
				long amount = 0;
				switch (k) {
					case 1:
						id = Integer.parseInt(arrayOfString[1]);
						amount = Integer.parseInt(arrayOfString[2]);
						value = ServerConstants.getItemValue(id);
						if (id == itemToSearchFor && itemToSearchFor != 0) {
							if (bankValueFlag == 0) {
								if (!doNotPrint) {
									Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
								}
							}
							totalOfItemSearchedInEconomy += amount;
							amountFound += amount;
						} else {
							if (str2.equals("character-equip") && value > 0) {
								bankWorth += value;
							}
						}
						break;
					case 2:
						id = Integer.parseInt(arrayOfString[1]) - 1;
						id = ItemAssistant.getUnNotedItem(id);
						amount = Integer.parseInt(arrayOfString[2]);
						value = ServerConstants.getItemValue(id) * amount;
						if (id == itemToSearchFor && itemToSearchFor != 0) {
							if (bankValueFlag == 0) {
								if (!doNotPrint) {
									Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
								}
							}
							totalOfItemSearchedInEconomy += amount;
							amountFound += amount;
						} else {
							if (str2.equals("inventory-slot") && value > 0) {
								bankWorth += value;
							}
						}
						break;
					case 3:
						id = Integer.parseInt(arrayOfString[1]) - 1;
						amount = Integer.parseInt(arrayOfString[2]);
						value = ServerConstants.getItemValue(id) * amount;
						if (id == itemToSearchFor && itemToSearchFor != 0) {
							if (bankValueFlag == 0) {
								if (!doNotPrint) {
									Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
								}
							}
							totalOfItemSearchedInEconomy += amount;
							amountFound += amount;
						} else {
							if (str2.startsWith("character-bank") && value > 0) {
								bankWorth += value;
							}
						}
						break;

					//[OTHER] to scan at lootingbag.
					case 4:
						if (str2.equals("lootingBagStorageItemId")) {
							for (int z = 0; z < arrayOfString.length; z++) {
								lootingBagStorageItemId[z] = Integer.parseInt(arrayOfString[z]);
							}
						} else if (str2.equals("lootingBagStorageItemAmount")) {
							for (int a = 0; a < arrayOfString.length; a++) {
								lootingBagStorageItemAmount[a] = Integer.parseInt(arrayOfString[a]);
							}
						} else if (str2.equals("timeLastClaimedDonation")) {
							if (Long.parseLong(str3) > 0) {
								lastTimeDonated = Long.parseLong(str3);
								lastDonationName = name;
							}
						}
						break;
				}
			} else if (str1.equals("[CREDENTIALS]")) {
				k = -1;
			} else if (str1.equals("[APPEARANCE]")) {
				k = -1;
			} else if (str1.equals("[OTHER]")) {
				k = 4;
			} else if (str1.equals("[EQUIPMENT]")) {
				k = 1;
			} else if (str1.equals("[LOOK]")) {
				k = -1;
			} else if (str1.equals("[SKILLS]")) {
				k = -1;
			} else if (str1.equals("[INVENTORY]")) {
				k = 2;
			} else if (str1.equals("[BANK]")) {
				k = 3;
			} else if (str1.equals("[FRIENDS]")) {
				k = -1;
			} else if (str1.equals("[IGNORES]")) {
				k = -1;
			} else if (str1.equals("[EOF]")) {
				try {
					localBufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (localBufferedReader != null) {
					str1 = localBufferedReader.readLine();
				}
			} catch (IOException e) {
				//e.printStackTrace(); it will spam console
				j = 1;
			}
		}
		try {
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int index = 0; index < lootingBagStorageItemId.length; index++) {
			int itemId = lootingBagStorageItemId[index];
			long amount = lootingBagStorageItemAmount[index];
			if (itemId > 0 && lootingBagStorageItemAmount[index] > 0) {
				if (itemId == itemToSearchFor && itemToSearchFor != 0) {
					if (bankValueFlag == 0) {
						if (!doNotPrint) {
							Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(itemId));
						}
					}
					totalOfItemSearchedInEconomy += amount;
					amountFound += amount;
				} else {
					bankWorth += ServerConstants.getItemValue(itemId) * amount;
				}
			}
		}
		int vault = 0;
		for (int index = 0; index < DiceSystem.depositVault.size(); index++) {
			String parse[] = DiceSystem.depositVault.get(index).split("-");
			if (parse[0].equalsIgnoreCase(name)) {
				vault += Integer.parseInt(parse[1]);
				break;
			}
		}
		int tradingPostValue = 0;
		for (TradingPost data : TradingPost.tradingPostData) {
			if (data.getName().equals(name)) {
				if (data.getAction().equals("SELLING")) {
					tradingPostValue += (data.getItemAmountLeft() * ServerConstants.getItemValue(7478));
				} else {
					tradingPostValue += (data.getItemAmountLeft() * data.getPrice());
				}
			}
		}

		for (TradingPostItems data : TradingPostItems.tradingPostItemsData) {
			if (data.getName().equals(name)) {
				tradingPostValue += ServerConstants.getItemValue(data.getItemId()) * data.getItemAmount();
			}
		}
		bankWorth += vault;
		bankWorth += tradingPostValue;
		if (bankWorth >= bankValueFlag && itemToSearchFor == 0) {
			String text = "Bank value [" + Misc.formatRunescapeStyle(bankWorth) + "] ";
			if (vault > 0) {
				text = text + "vault: " + Misc.formatRunescapeStyle(vault) + ", ";
			}
			if (tradingPostValue > 0) {
				text = text + "post: " + Misc.formatRunescapeStyle(tradingPostValue) + ", ";
			}
			String string = text = text + "name: " + name;
			if (!doNotPrint) {
				Misc.printDontSave(string);
			}
			if (saveInArrayList) {
				boolean update = false;
				if (DawntainedMultiTool.isBannedOrIpBannedName(name)) {
					string = string + ", BANNED!";
				}
				DawntainedMultiTool.altData.add(string + "#" + bankWorth);
				DawntainedMultiTool.altNames.add(name);
				if (DawntainedMultiTool.altData.size() % 5 == 0) {
					update = true;
				} else if (DawntainedMultiTool.altData.size() == DawntainedMultiTool.flaggedPlayers.size()) {
					update = true;
				}
				if (update) {

					DawntainedMultiTool.setStatusText("Scanned: " + DawntainedMultiTool.altData.size() + "/" + DawntainedMultiTool.flaggedPlayers.size());
				}
			}
		}
		if (amountFound >= bankValueFlag && itemToSearchFor > 0) {
			if (!doNotPrint) {
				Misc.printDontSave(name + " has x" + amountFound + " " + ItemAssistant.getItemName(itemToSearchFor));
			}
		}
		economyTotalBloodMoney += bankWorth;
		if (bankWorth >= WEALTH_DUPER_FINDER_FLAG) {
			tempFolderDataNew.add(name + "-" + bankWorth);
			if (duperFinderScanFirstStage) {
				olderFolderData.add(name + "-" + bankWorth);
				olderFolderDataNamesOnly.add(name);
			} else if (duperFinderScanSecondStage) {
				if (olderFolderDataNamesOnly.contains(name)) {
					newerFolderData.add(name + "-" + bankWorth);
				} else {
					newAccountsData.add(name + "-" + bankWorth);
				}
			}
		}
		accountWealth = bankWorth;
	}

	public static long accountWealth;

	public static String scanSpecificName = "";

	public static void scanFolder() {
		File localFile;
		File[] arrayOfFile;
		int i;
		if (wealthScan || saveInArrayList) {
			if (saveInArrayList) {
				for (int index = 0; index < DawntainedMultiTool.flaggedPlayers.size(); index++) {
					scanAccount(localFile = new File(charactersLocation() + "/" + DawntainedMultiTool.flaggedPlayers.get(index) + ".txt"));
				}
			} else {
				try {
					BufferedReader file = new BufferedReader(new FileReader("backup/characters/wealth scan.txt"));
					String line;
					while ((line = file.readLine()) != null) {
						if (!line.isEmpty()) {
							//cold2.txt contains 'f0-b4-79-15-df-a2'.
							if (line.contains(".txt")) {
								line = line.substring(0, line.indexOf(".txt"));
								String nameToScan = line;
								if (!FileUtility.fileExists(charactersLocation() + "/" + nameToScan + ".txt")) {
									if (!doNotPrint) {
										Misc.printDontSave("Account does not exist: " + nameToScan);
									}
									continue;
								}
								scanAccount(localFile = new File(charactersLocation() + "/" + nameToScan + ".txt"));
							}
						}
					}
					file.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			localFile = new File(charactersLocation());
			arrayOfFile = localFile.listFiles();
			if (arrayOfFile != null) {
				for (i = 0; i < arrayOfFile.length; i++) {
					if ((arrayOfFile[i] != null) && (arrayOfFile[i].getName().endsWith(".txt"))) {
						scanAccount(arrayOfFile[i]);
					}
				}
			}
		}
		if (duperFinderScanFirstStage) {
			Misc.print(Misc.formatRunescapeStyle(economyTotalBloodMoney) + " total wealth in economy in first stage.");
		} else if (duperFinderScanSecondStage) {
			Misc.print(Misc.formatRunescapeStyle(economyTotalBloodMoney) + " total wealth in economy in second stage.");
		}
		if (itemToSearchFor == 0) {
			if (!doNotPrint) {
				Misc.printDontSave("Economy blood money value: " + Misc.formatRunescapeStyle(economyTotalBloodMoney));
			}
			if (!wealthScan) {
				FileUtility.addLineOnTxt("backup/logs/economy log.txt", Misc.formatRunescapeStyle(economyTotalBloodMoney) + " at: " + Misc.getDateAndTime());
			}
		} else {
			if (!doNotPrint) {
				Misc.printDontSave("Total of " + ItemAssistant.getItemName(itemToSearchFor) + " in economy: " + totalOfItemSearchedInEconomy);
			}
		}
	}
}
