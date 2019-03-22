package utility;

import core.ServerConfiguration;
import core.ServerConstants;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LeakedSourceApi {

	//requests used https://api.leakedsource.ru/stats?key=IRQCpph22TQK2tLvdQItRdxXmTXgkcKg

	public final static boolean DISABLE = true;

	public static int playersFoundLeaked;

	public static void main(String args[]) {
		Misc.print(submitRequestAndGetPasswords("big turtle") + "");
	}

	public static List<LeakedSourceApi> leakedSourceLocalDb = new ArrayList<LeakedSourceApi>();

	public String username;

	public ArrayList<String> leakedPasswords = new ArrayList<>();
	
	public boolean alertedOnce;

	public LeakedSourceApi(String username, ArrayList<String> leakedPasswords, boolean alertedOnce) {
		this.username = username;
		this.leakedPasswords = leakedPasswords;
		this.alertedOnce = alertedOnce;
	}

	public static void addNewEntry(String username, ArrayList<String> leakedPasswords) {
		if (isAlreadyInLocalDb(username)) {
			return;
		}
		leakedSourceLocalDb.add(new LeakedSourceApi(username, leakedPasswords, false));
	}

	public static boolean isAlreadyInLocalDb(String username) {
		for (int index = 0; index < leakedSourceLocalDb.size(); index++) {
			LeakedSourceApi instance = leakedSourceLocalDb.get(index);
			if (username.equals(instance.username)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getLeakedPasswordsFromLocalDb(String username) {
		for (int index = 0; index < leakedSourceLocalDb.size(); index++) {
			LeakedSourceApi instance = leakedSourceLocalDb.get(index);
			if (instance.username.equals(username)) {
				return instance.leakedPasswords;
			}
		}
		return null;
	}

	public static void setAlerted(String username) {
		for (int index = 0; index < leakedSourceLocalDb.size(); index++) {
			LeakedSourceApi instance = leakedSourceLocalDb.get(index);
			if (username.equals(instance.username)) {
				if (!instance.alertedOnce) {
					instance.alertedOnce = true;
					playersFoundLeaked++;
				}
				return;
			}
		}
	}

	private final static String FILE_LOCATION = "backup/logs/bruteforce/leaked_source_db.txt";

	private final static String FILE_LOCATION_LEAKED_PLAYERS_AMOUNT = "backup/logs/bruteforce/leaked_source_amount.txt";

	public static void loadFromFile() {
		if (DISABLE) {
			return;
		}
		if (!FileUtility.fileExists(FILE_LOCATION)) {
			return;
		}
		playersFoundLeaked = Integer.parseInt(FileUtility.readFirstLine(FILE_LOCATION_LEAKED_PLAYERS_AMOUNT));
		ArrayList<String> lines = new ArrayList<String>();
		lines = FileUtility.readFile(FILE_LOCATION);
		String username = "";
		boolean alerted = false;
		ArrayList<String> passwords = new ArrayList<>();
		for (int index = 0; index < lines.size(); index++) {
			String line = lines.get(index);
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith("username:")) {
				username = line.substring(10);
				passwords = new ArrayList<>();
				alerted = false;
			}
			else if (line.startsWith("passwords:")) {
				String passwordsSplit = line.substring(11);
				String[] passSplit = passwordsSplit.split("#");
				for (int a = 0; a < passSplit.length; a++) {
					passwords.add(passSplit[a]);
				}
			}
			else if (line.startsWith("alerted:")) {
				leakedSourceLocalDb.add(new LeakedSourceApi(username, passwords, line.contains("true")));
			}
		}
	}

	public static void saveToFile() {
		if (DISABLE) {
			return;
		}
		FileUtility.deleteAllLines(FILE_LOCATION_LEAKED_PLAYERS_AMOUNT);
		FileUtility.addLineOnTxt(FILE_LOCATION_LEAKED_PLAYERS_AMOUNT, playersFoundLeaked + "");
		FileUtility.deleteAllLines(FILE_LOCATION);
		ArrayList<String> lines = new ArrayList<String>();
		for (int index = 0; index < leakedSourceLocalDb.size(); index++) {
			LeakedSourceApi instance = leakedSourceLocalDb.get(index);
			lines.add("username: " + instance.username);
			String pass = "";
			for (int a = 0; a < instance.leakedPasswords.size(); a++) {
				if (a == instance.leakedPasswords.size() - 1) {
					pass = pass + instance.leakedPasswords.get(a);
				} else {
					pass = pass + instance.leakedPasswords.get(a) + "#";
				}
			}
			lines.add("passwords: " + pass);
			lines.add("alerted: " + instance.alertedOnce);
		}
		FileUtility.saveArrayContentsSilent(FILE_LOCATION, lines);
	}

	public static void checkForLeakedSourceEntry(Player player, boolean forceCheck) {
		if (DISABLE) {
			return;
		}
		if (player.isCombatBot()) {
			return;
		}
		if (ServerConfiguration.DEBUG_MODE) {
			return;
		}
		if (Area.inDangerousPvpArea(player)) {
			return;
		}
		if (!player.isTutorialComplete()) {
			return;
		}
		if (player.playerIsLeakedSourceClean && !forceCheck) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				if (isAlreadyInLocalDb(player.getPlayerName())) {
					player.passwordsFromLeakedSource = getLeakedPasswordsFromLocalDb(player.getPlayerName());
					player.leakedSourceRequestComplete = true;
				} else {
					player.passwordsFromLeakedSource = submitRequestAndGetPasswords(player.getPlayerName());
					player.leakedSourceRequestComplete = true;
				}
			}
		}).start();
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent<Object>() {
			int cycleSaved = 0;
			boolean checked = false;
			boolean alertedOnce = false;
			@Override
			public void execute(CycleEventContainer container) {
				if (player.leakedSourceRequestComplete) {
					if (player.passwordsFromLeakedSource == null) {
						container.stop();
						return;
					}
					if (cycleSaved != 0) {
						cycleSaved = container.getExecutions();
					}
					if (!checked) {
						addNewEntry(player.getPlayerName(), player.passwordsFromLeakedSource);
					}
					checked = true;
					if (player.passwordsFromLeakedSource.contains(player.playerPass)) {
						if (!alertedOnce) {
							setAlerted(player.getPlayerName());
						}
						alertedOnce = true;
						player.playerIsLeakedSourceClean = false;
						player.getPA().sendMessage(ServerConstants.RED_COL + "Your password " + player.playerPass + " is leaked on a database not related to Dawntained!");
						player.getPA().sendMessage(ServerConstants.RED_COL + "Type in ::changepass to change your password before you get hacked!");
						if (container.getExecutions() == cycleSaved + 5) {
							container.stop();
						}
					}
					else {
						player.playerIsLeakedSourceClean = true;
						container.stop();
					}
				}
			}

			@Override
			public void stop() {
			}
		}, 1);
	}

	public static ArrayList<String> submitRequestAndGetPasswords(String username) {
		ArrayList<String> websiteLines = new ArrayList<String>();
		try {
			URL url;
			URLConnection uc;
			String urlString = "https://api.leakedsource.ru/search/username?key=IRQCpph22TQK2tLvdQItRdxXmTXgkcKg&query=" + username + "&wildcard=false";
			url = new URL(urlString);
			uc = url.openConnection();
			uc.connect();
			uc = url.openConnection();
			uc.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			uc.setRequestProperty("Cache-Control", "max-age=0");
			uc.setRequestProperty("Connection", "keep-alive");
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.setRequestProperty("Host", "www.google.com");
			uc.setRequestProperty("Origin", "www.google.com");
			uc.setRequestProperty("Referer", "https://www.google.co.uk");
			uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
			uc.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				String[] parsePasswords = inputLine.split("Real_password\":\"");
				for (int index = 0; index < parsePasswords.length; index++) {
					if (index == 0) {
						continue;
					}
					String password = parsePasswords[index];
					password = password.substring(0, password.indexOf("\""));
					websiteLines.add(password);
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return websiteLines;
	}
}
