package utility;

import core.ServerConstants;
import game.player.punishment.Blacklist;

import java.util.ArrayList;
import java.util.List;

public class HackLogHistory {

	public static List<HackLogHistory> hackLogList = new ArrayList<HackLogHistory>();

	public String account = "";

	private String newIp = "";

	private String newUid = "";

	private String oldIp = "";

	private String oldUid = "";

	public void setAccount(String string) {
		account = string;
	}

	public HackLogHistory(String account, String newIp, String newUid, String oldIp, String oldUid) {
		this.account = account;
		this.newIp = newIp;
		this.newUid = newUid;
		this.oldIp = oldIp;
		this.oldUid = oldUid;
	}

	public static void main(String[] args) {
		readHackLog();
	}

	public static boolean isOriginalOwner(String account, String currentIp, String currentUid) {
		String lastUidOnAccount = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "addressUid", 3);
		String lastIpOnAccount = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "lastSavedIpAddress", 3);
		String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "originalIp", 3);
		String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "originalUid", 3);
		if (lastIpOnAccount.equalsIgnoreCase(currentIp) && !currentIp.isEmpty() || Misc.uidMatches(currentUid, lastUidOnAccount) || Misc.uidMatches(currentUid, originalUid)
		    || originalIp.equalsIgnoreCase(currentIp) && !currentIp.isEmpty()) {
			return true;
		}
		account = Misc.capitalize(account);
		for (int index = 0; index < hackLogList.size(); index++) {
			HackLogHistory instance = hackLogList.get(index);
			if (instance.account.equals(account)) {
				if (instance.oldIp.equals(currentIp) || Misc.uidMatches(currentUid, instance.oldUid)) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	public static void readHackLog() {
		ArrayList<String> arraylist = FileUtility.readFile("backup/logs/bruteforce/hacklog.txt");

		/*
		[Scoobymc] on 23/11/2017, 04:56: AM
		New Ip: 67.248.57.130
		New Uid: g7d4rh220171023105720.000000-300
		Last logged in (hours): 79
		Old Ip: 38.95.253.194
		Old Uid: to be filled by o.e.m.20171113115004.000000-480
		 */
		String account = "";
		String newIp = "";
		String newUid = "";
		String oldIp = "";
		String oldUid = "";
		for (int index = 0; index < arraylist.size(); index++) {
			String line = arraylist.get(index);
			if (line.startsWith("[")) {
				account = line.substring(1, line.indexOf("]"));
			} else if (line.startsWith("New Ip:")) {
				newIp = line.substring(8);
			} else if (line.startsWith("New Uid:")) {
				newUid = line.substring(9);
			} else if (line.startsWith("Old Ip:")) {
				oldIp = line.substring(8);
			} else if (line.startsWith("Old Uid:")) {
				oldUid = line.substring(9);
				hackLogList.add(new HackLogHistory(account, newIp, newUid, oldIp, oldUid));
			}
		}
	}

	public static boolean hasPreviousAccessToAccount(String account, String currentIp, String currentUid) {
		String lastUidOnAccount = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "addressUid", 3);
		String lastIpOnAccount = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "lastSavedIpAddress", 3);
		String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "originalIp", 3);
		String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + account + ".txt", "originalUid", 3);
		if (lastIpOnAccount.equalsIgnoreCase(currentIp) && !currentIp.isEmpty() || Misc.uidMatches(currentUid, lastUidOnAccount) || Misc.uidMatches(currentUid, originalUid)
		    || originalIp.equalsIgnoreCase(currentIp) && !currentIp.isEmpty()) {
			return true;
		}
		account = Misc.capitalize(account);
		for (int index = 0; index < hackLogList.size(); index++) {
			HackLogHistory instance = hackLogList.get(index);
			if (instance.account.equals(account)) {
				if (instance.newIp.equals(currentIp) && !currentIp.isEmpty() || Misc.uidMatches(currentUid, instance.newUid)
				    || instance.oldIp.equals(currentIp) && !currentIp.isEmpty() || Misc.uidMatches(currentUid, instance.oldUid)) {
					return true;
				}
			}
		}
		return false;
	}
}
