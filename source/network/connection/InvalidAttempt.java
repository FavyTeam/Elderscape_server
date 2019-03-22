package network.connection;

import core.ServerConfiguration;
import core.ServerConstants;
import game.player.punishment.Blacklist;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import utility.FileUtility;
import utility.Misc;

public class InvalidAttempt {

	public static List<InvalidAttempt> invalidAttempts = new ArrayList<InvalidAttempt>();

	/**
	 * Store log in attempts of banned players and where they are banned from.
	 */
	public static ArrayList<String> bannedAttemptsHistory = new ArrayList<String>();

	/**
	 * Saved on shutdown.
	 */
	public static ArrayList<String> invalidAttemptsLog = new ArrayList<String>();

	public String addressIp = "";

	public String macAddress = "";

	public String uidAddress = "";

	public String accountAttempted = "";

	public boolean notSamePerson;

	public long time;

	public String passwordAttempted = "";

	/**
	 * @param address
	 * @param mac
	 * @param uid
	 * @param accountAttempted
	 * @param notSamePerson True if it is a completely different person who attempted to log in, different ip, uid and mac.
	 */
	public InvalidAttempt(String address, String mac, String uid, String accountAttempted, boolean notSamePerson, String passwordAttempted) {
		this.addressIp = address;
		this.macAddress = mac;
		this.uidAddress = uid;
		this.accountAttempted = accountAttempted;
		this.notSamePerson = notSamePerson;
		this.passwordAttempted = passwordAttempted;
		this.time = System.currentTimeMillis();
	}

	public static boolean canConnect(String ip, String mac, String uid, String accountAttempted) {
		int count = 0;
		for (int index = 0; index < invalidAttempts.size(); index++) {
			if (invalidAttempts.get(index).addressIp.equals(ip) || invalidAttempts.get(index).uidAddress.equals(uid)) {
				if (System.currentTimeMillis() - invalidAttempts.get(index).time < 60000) {
					count++;
				}
			}
			if (count >= ServerConstants.getMaximumInvalidAttempts()) {
				return false;
			}
		}
		return true;
	}

	public static String lastInvalidAttempt = "";

	public static void addToLog(String name, String ip, String mac, String uid, String pass, String originalMac, String originalUid, String originalIp) {
		name = name.toLowerCase();
		String attempt =
				Misc.getDateAndTime() + " [" + uid + "] [" + mac + "] [" + ip + "] " + "attempted to log into: [" + name + "] [" + originalUid + "] [" + originalMac + "] ["
				+ originalIp + "] with pass: " + pass;
		if (lastInvalidAttempt.equals(attempt)) {
			return;
		}
		lastInvalidAttempt = attempt;
		invalidAttemptsLog.add(attempt);
	}

	public static void saveInvalidAttemptLog() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		FileUtility.saveArrayContents("backup/logs/bruteforce/bruteforce_history.txt", invalidAttemptsLog);
		invalidAttemptsLog.clear();
	}

	public static boolean autBlacklistUpdated;

	public static ArrayList<String> autoBlacklistReason = new ArrayList<String>();

	public static class AutoBlacklisted {

		public final static int HOURS_FLAGGED_FOR = 24;

		private final static String FILE_LOCATION = "backup/logs/bruteforce/autoblacklisted_new.txt";

		public static List<AutoBlacklisted> autoBlacklistedList = new ArrayList<AutoBlacklisted>();

		public String ip;

		public String uid;

		public long time;

		public AutoBlacklisted(String ip, String uid, long time) {
			this.ip = ip;
			this.uid = uid;
			this.time = time;
		}

		public static void save() {
			FileUtility.deleteAllLines(FILE_LOCATION);

			ArrayList<String> text = new ArrayList<String>();
			for (int index = 0; index < AutoBlacklisted.autoBlacklistedList.size(); index++) {
				AutoBlacklisted instance = AutoBlacklisted.autoBlacklistedList.get(index);
				text.add(instance.ip + ServerConstants.TEXT_SEPERATOR + instance.uid + ServerConstants.TEXT_SEPERATOR + instance.time);
			}
			FileUtility.saveArrayContentsSilent(FILE_LOCATION, text);
		}

		public static void loadAutoBlacklist() {
			autoBlacklistedList.clear();
			ArrayList<String> text = FileUtility.readFile(FILE_LOCATION);
			for (int index = 0; index < text.size(); index++) {
				String parse[] = text.get(index).split(ServerConstants.TEXT_SEPERATOR);
				autoBlacklistedList.add(new AutoBlacklisted(parse[0], parse[1], Long.parseLong(parse[2])));
			}
		}
	}

	public static boolean autoBlackListed(String currentIp, String currentUid) {
		for (int index = 0; index < AutoBlacklisted.autoBlacklistedList.size(); index++) {
			AutoBlacklisted instance = AutoBlacklisted.autoBlacklistedList.get(index);
			if (Misc.timeElapsed(instance.time, Misc.getHoursToMilliseconds(AutoBlacklisted.HOURS_FLAGGED_FOR))) {
				AutoBlacklisted.autoBlacklistedList.remove(index);
				index--;
				continue;
			}
				if (currentIp.equals(instance.ip)) {
				return true;
			}
			if (Misc.uidMatches(currentUid, instance.uid)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBruteforceHacker(String ip, String mac, String uid, String attemptedName) {
		String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + attemptedName + ".txt", "addressUid", 3);
		String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + attemptedName + ".txt", "addressMac", 3);
		String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + attemptedName + ".txt", "lastSavedIpAddress", 3);
		if (ip.equals(originalIp) || Misc.macMatches(mac, originalMac) || Misc.uidMatches(uid, originalUid)) {
			return false;
		}
		int flaggedCount = 0;
		ArrayList<String> data = new ArrayList<String>();

		// Store passes attempted, and make sure none my current hack attempt contains any of my previous pass attempted.
		ArrayList<String> passAttempted = new ArrayList<String>();
		for (int index = 0; index < invalidAttempts.size(); index++) {
			// If ip matches or uid matches or mac & uid match or mac & ip match.
			boolean ipMatches = invalidAttempts.get(index).addressIp.equals(ip);
			boolean uidMatches = Misc.uidMatches(uid, invalidAttempts.get(index).uidAddress);
			if (ipMatches || uidMatches) {
				if (invalidAttempts.get(index).notSamePerson) {
					if (Misc.timeElapsed(invalidAttempts.get(index).time, Misc.getHoursToMilliseconds(AutoBlacklisted.HOURS_FLAGGED_FOR))) {
						invalidAttempts.remove(index);
						index--;
						continue;
					}
					if (data.isEmpty()) {
						data.add(invalidAttempts.get(index).accountAttempted + "###@" + invalidAttempts.get(index).passwordAttempted);
						passAttempted.add(invalidAttempts.get(index).passwordAttempted);
						flaggedCount++;
					} else {
						boolean match = false;
						for (int i = 0; i < data.size(); i++) {
							String parse[] = data.get(i).split("###@");
							if (invalidAttempts.get(index).accountAttempted.equalsIgnoreCase(parse[0]) || invalidAttempts.get(index).passwordAttempted.equals(parse[1])) {
								match = true;
							}
						}
						if (!match) {
							boolean similairPass = false;
							for (int i = 0; i < passAttempted.size(); i++) {
								if (invalidAttempts.get(index).passwordAttempted.contains(passAttempted.get(i))) {
									similairPass = true;
								}
							}
							if (!similairPass) {
								data.add(invalidAttempts.get(index).accountAttempted + "###@" + invalidAttempts.get(index).passwordAttempted);
								passAttempted.add(invalidAttempts.get(index).passwordAttempted);
								flaggedCount++;
							}
						}
					}
					if (flaggedCount == 3) {
						if (!autoBlackListed(ip, uid)) {
							autoBlacklistReason.add(Misc.getDateAndTime() + "[" + ip + "] [" + mac + "] [" + uid + "] auto ip banned for trying to hack 3 accounts [" + data.get(0) + "] & [" + data.get(1) + "] & [" + data.get(2) + "]");
							AutoBlacklisted.autoBlacklistedList.add(new AutoBlacklisted(ip, uid, System.currentTimeMillis()));
							autBlacklistUpdated = true;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String lastBannedAttemptData = "";

	/**
	 * When banned players try to log in, it will save their attempt and where they are banned in a text file.
	 */
	public static void addBannedLogInAttempt(String ip, String mac, String uid, String name, String pass, String reasonBanned) {
		String data = Misc.getDateAndTime() + " [Username: " + name + "][" + ip + "][" + mac + "][" + uid + "][Reason: " + reasonBanned + "]";
		if (lastBannedAttemptData.equals(data)) {
			return;
		}
		lastBannedAttemptData = data;
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.printDontSave(data);
		}
		bannedAttemptsHistory.add(data);
		String originalUid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
		String originalMac = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressMac", 3);
		String originalIp = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
		addToLog(name, ip, mac, uid, pass, originalMac, originalUid, originalIp);

	}
}
