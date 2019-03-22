package game.player.punishment;

import core.ServerConfiguration;
import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import network.connection.InvalidAttempt;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.multitool.DawntainedMultiTool;
import utility.FileUtility;
import utility.Misc;

/**
 * Blacklist system.
 *
 * @author MGT Madness, created on 01-02-2016.
 */
public class Blacklist {


	public static ArrayList<String> floodIps = new ArrayList<String>();

	public static ArrayList<String> floodAccountBans = new ArrayList<String>();

	public static ArrayList<String> floodIpsWhitelist = new ArrayList<String>();

	public static ArrayList<String> permanentIp = new ArrayList<String>();

	public static ArrayList<String> permanentMac = new ArrayList<String>();

	public static ArrayList<String> permanentUid = new ArrayList<String>();

	public static ArrayList<String> blacklistedIp = new ArrayList<String>();

	public static ArrayList<String> blacklistedMac = new ArrayList<String>();

	public static ArrayList<String> blacklistedUid = new ArrayList<String>();

	public static ArrayList<String> blacklistedAccounts = new ArrayList<String>();

	public static ArrayList<String> floodBlockReason = new ArrayList<String>();


	public static void readLatestFloodIps() {
		floodIps.clear();
		floodIps = FileUtility.readFile("backup/logs/blacklisted/flood_ip.txt");
		floodAccountBans.clear();
		floodAccountBans = FileUtility.readFile("backup/logs/blacklisted/flood_accounts.txt");
		floodIpsWhitelist.clear();
		floodIpsWhitelist = FileUtility.readFile("backup/logs/blacklisted/flood_ip_whitelist.txt");
	}

	public static void saveFloodIps() {

		FileUtility.deleteAllLines("backup/logs/blacklisted/flood_ip.txt");
		FileUtility.saveArrayContentsSilent("backup/logs/blacklisted/flood_ip.txt", floodIps);

		FileUtility.deleteAllLines("backup/logs/blacklisted/flood_accounts.txt");
		FileUtility.saveArrayContentsSilent("backup/logs/blacklisted/flood_accounts.txt", floodAccountBans);
	}

	/**
	 * Must be above 65k so the Client does not try to read it as a model.
	 */
	public static int clientFileNumber = 65001;

	public static void blacklistPlayer(Player player, String name) {

		if (DawntainedMultiTool.isIpBannedName(name)) {
			return;
		}
		boolean online = false;
		String ip = "";
		String mac = "";
		String uid = "";
		ip = readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
		mac = readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressMac", 3);
		uid = readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
		mac = mac.toLowerCase();
		uid = uid.toLowerCase();
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			final Player playerLoop = PlayerHandler.players[i];
			if (playerLoop == null) {
				continue;
			}
			if (playerLoop.getPlayerName().equalsIgnoreCase(name) || ip.equals(playerLoop.addressIp) || Misc.uidMatches(uid, playerLoop.addressUid)) {
				online = true;
				ip = playerLoop.addressIp;
				uid = playerLoop.addressUid;
				//tellClientToCreateBlacklistFile(playerLoop);
				if (player != null) {
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							playerLoop.setDisconnected(true, "blacklisted event");
						}
					}, 3);
				} else {
					// playerLoop.getPA().forceToLogInScreen(); // It will show error on server.
					playerLoop.setDisconnected(true, "blacklsited");
				}
			}
		}
		if (!online) {
			ip = readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
			uid = readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
		}

		uid = uid.toLowerCase();

		if (!ip.isEmpty() && !arrayListContains(blacklistedIp, ip + " (" + name + ")")) {
			addNewBlacklistData(name, "IP ADDRESS: ", ip);
		}

		if (useAbleData(uid)) {
			if (!arrayListContains(blacklistedUid, uid + " (" + name + ")")) {
				addNewBlacklistData(name, "UID ADDRESS: ", uid);
			}
		}

		if (!arrayListContains(blacklistedAccounts, name)) {
			addNewBlacklistData(name, "NAME: ", name);
		}
	}

	public static void loadPermanentBlacklist() {
		permanentIp.clear();
		permanentMac.clear();
		permanentUid.clear();
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/blacklisted/permanent.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.startsWith("//") && !line.isEmpty()) {
					line = line.toLowerCase();
					if (line.startsWith("ip: ")) {
						permanentIp.add(line.toLowerCase().substring(4));
					} else if (line.startsWith("mac: ")) {
						permanentMac.add(line.toLowerCase().substring(5));
					} else if (line.startsWith("uid")) {
						permanentUid.add(line.toLowerCase().substring(5));
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void addNewBlacklistData(String name, String blacklistType, String data) {
		if (data.isEmpty()) {
			return;
		}
		if (!blacklistType.equals("NAME")) {
			if (!useAbleData(data)) {
				return;
			}
		}
		switch (blacklistType) {
			case "IP ADDRESS: ":
				blacklistedIp.add(data + " (" + name + ")");
				break;
			case "MAC ADDRESS: ":
				blacklistedMac.add(data + " (" + name + ")");
				break;
			case "UID ADDRESS: ":
				blacklistedUid.add(data + " (" + name + ")");
				break;
			case "NAME: ":
				blacklistedAccounts.add(data);
				break;
		}
		writeToBlacklistOriginFile(name, blacklistType, data);
	}

	public static boolean useAbleData(String data) {
		if (data.isEmpty() || data.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	public static String bannedReasonResponse;

	public static boolean isBlacklisted(String name, String ip, String mac, String password, String uid, boolean logInAttempt) {

		// Banned only accounts.
		for (int index = 0; index < Ban.bannedList.size(); index++) {
			if (Ban.bannedList.get(index).equalsIgnoreCase(name)) {
				if (logInAttempt) {
					InvalidAttempt.addBannedLogInAttempt(ip, mac, uid, name, password, "Account banned");
				}
				bannedReasonResponse = "You are banned on " + name;
				return true;
			}
		}

		boolean blacklisted = false;
		name = name.toLowerCase();
		mac = mac.toLowerCase();
		uid = uid.toLowerCase();
		String originalBlacklistedName = "";


		for (int index = 0; index < permanentIp.size(); index++) {
			if (permanentIp.get(index).equals(ip)) {
				if (logInAttempt) {
					InvalidAttempt.addBannedLogInAttempt(ip, mac, uid, name, password, "Permanent ip");
				}
				bannedReasonResponse = "You are not welcomed on Dawntained.";
				return true;
			}
		}
		for (int index = 0; index < permanentUid.size(); index++) {
			String storedUid = permanentUid.get(index);

			if (Misc.uidMatches(uid, storedUid) || uid.toLowerCase().contains(storedUid.toLowerCase())) {
				if (logInAttempt) {
					InvalidAttempt.addBannedLogInAttempt(ip, mac, uid, name, password, "Permanent uid");
				}
				bannedReasonResponse = "You are not welcomed on Dawntained.";
				return true;
			}
		}

		for (int i = 0; i < blacklistedIp.size(); i++) {
			if (ip.equals(blacklistedIp.get(i).substring(0, blacklistedIp.get(i).indexOf("(") - 1))) {
				blacklisted = true;
				originalBlacklistedName = blacklistedIp.get(i).substring(blacklistedIp.get(i).indexOf("("));
				originalBlacklistedName = originalBlacklistedName.replace("(", "");
				originalBlacklistedName = originalBlacklistedName.replace(")", "");
				bannedReasonResponse = "You are banned on " + originalBlacklistedName;
				break;
			}
		}

		if (useAbleData(uid)) {
			for (int i = 0; i < blacklistedUid.size(); i++) {
				String uidStored = blacklistedUid.get(i).substring(0, blacklistedUid.get(i).indexOf("(") - 1);
				if (Misc.uidMatches(uid, uidStored)) {
					blacklisted = true;
					originalBlacklistedName = blacklistedUid.get(i).substring(blacklistedUid.get(i).indexOf("("));
					originalBlacklistedName = originalBlacklistedName.replace("(", "");
					originalBlacklistedName = originalBlacklistedName.replace(")", "");
					bannedReasonResponse = "You are banned on " + originalBlacklistedName;
					break;
				}
			}
		}

		for (int i = 0; i < blacklistedAccounts.size(); i++) {
			if (name.equalsIgnoreCase(blacklistedAccounts.get(i))) {
				if (logInAttempt) {
					InvalidAttempt.addBannedLogInAttempt(ip, mac, uid, name, password, "Blacklisted account name");
				}
				bannedReasonResponse = "You are banned on " + name;
				return true;
			}
		}
		if (blacklisted) {
			if (!originalBlacklistedName.isEmpty()) {
				originalBlacklistedName = originalBlacklistedName.replace("(", "");
				originalBlacklistedName = originalBlacklistedName.replace(")", "");
			}
			if (logInAttempt) {
				InvalidAttempt.addBannedLogInAttempt(ip, mac, uid, name, password, "Blacklisted");
			}
		}

		return blacklisted;
	}


	public static void blacklistCommand(Player player, String command) {
		String name = command.substring(6);
		if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
			player.playerAssistant.sendMessage(name + " character file does not exist.");
			return;
		}
		blacklistPlayer(player, name);
		player.playerAssistant.sendMessage("You have blacklisted: " + name);
		DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has ip banned '" + name + "'");
	}

	private static boolean arrayListContains(ArrayList<String> arraylist, String match) {
		for (int i = 0; i < arraylist.size(); i++) {
			if (arraylist.get(i).equalsIgnoreCase(match)) {
				return true;
			}
		}
		return false;
	}

	private static void writeToBlacklistOriginFile(String name, String blacklistType, String data) {
		String location = "backup/logs/blacklisted/names/" + name + ".txt";
		File file = new File(location);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileUtility.addLineOnTxt(location, blacklistType + data + " (" + Misc.getDateAndTime() + ")");
	}

	public static String readBlacklistedData(String location) {
		String name = location.substring(24);
		name = name.replace(".txt", "");
		name = name.replace("names/", "");
		String result = "";
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (line.startsWith("IP ADDRESS")) {
					result = line.substring(12);
					result = result.substring(0, result.indexOf("(") - 1);
					blacklistedIp.add(result + " (" + name + ")");
				} else if (line.startsWith("MAC ADDRESS")) {
					result = line.substring(13);
					result = result.substring(0, result.indexOf("(") - 1);
					blacklistedMac.add(result + " (" + name + ")");
				} else if (line.startsWith("UID ADDRESS")) {
					result = line.substring(13);
					result = result.substring(0, result.indexOf("(") - 1);
					blacklistedUid.add(result + " (" + name + ")");
				} else if (line.startsWith("name")) {
					result = line.substring(6);
					result = result.substring(0, result.indexOf("(") - 1);
					blacklistedAccounts.add(result);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String readOfflinePlayerData(String location, String variable, int space) {
		File file1 = new File(location);
		if (!file1.exists()) {
			return "";
		}
		String result = "";
		try {
			BufferedReader file = new BufferedReader(new FileReader(location));
			String line;
			while ((line = file.readLine()) != null) {
				if (line.contains(variable)) {
					result = line.substring(variable.length() + space);
					file.close();
					return result;
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void loadStartUpBlacklistedData() {
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		Blacklist.loadBlacklistedData();
		clientFileNumber = Integer.parseInt(readOfflinePlayerData("backup/logs/blacklisted/client file number.txt", "number", 2));
	}

	public static void loadBlacklistedData() {
		Blacklist.blacklistedAccounts.clear();
		Blacklist.blacklistedIp.clear();
		Blacklist.blacklistedMac.clear();
		Blacklist.blacklistedUid.clear();
		final File folder = new File("backup/logs/blacklisted/names");
		String accountName = "";
		for (final File fileEntry : folder.listFiles()) {
			// If the location is not a folder directory, then it has to be a file.
			if (!fileEntry.isDirectory()) {
				readBlacklistedData("backup/logs/blacklisted/names/" + fileEntry.getName());
				accountName = fileEntry.getName().replace(".txt", "");
				blacklistedAccounts.add(accountName);
			}
		}
		loadPermanentBlacklist();
	}

	public static void clearBlacklist(Player player) {
		player.playerAssistant.sendMessage("Old client file number: " + clientFileNumber);
		clientFileNumber++;
		String location = "backup/logs/blacklisted/client file number.txt";
		FileUtility.deleteAllLines(location);
		FileUtility.addLineOnTxt(location, "number: " + clientFileNumber);
		loadBlacklistedData();
		player.playerAssistant.sendMessage("Black list cleared and new client file number set to: " + clientFileNumber);
	}

	/**
	 * So when i use this in logs and it is empty, it saves invalid, so it does crash when i parse the saved log data.
	 */
	public static String formatUid(String uid) {
		if (uid.isEmpty()) {
			return "invalid";
		}
		return uid;
	}

}
