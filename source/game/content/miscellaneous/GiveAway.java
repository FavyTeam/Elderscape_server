package game.content.miscellaneous;

import core.Server;
import core.ServerConstants;
import game.player.Player;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import network.sql.SQLConstants;
import utility.FileUtility;
import utility.Misc;

/**
 * Giveaway entries.
 * @author MGT Madness, created on 02-09-2018.
 */
public class GiveAway {

	private final static String GIVE_AWAY_PLAYER_ENTRIES_FILE = "backup/logs/giveaway/player_entries.txt";

	private final static String ACTIVE_GIVE_AWAY_FILE = "backup/logs/giveaway/active_giveaways.txt";

	public String name;

	public String uid;

	public String ip;

	public String giveAway;

	public String timeEntered;

	public GiveAway(String name, String uid, String ip, String giveAway, String timeEntered) {
		this.name = name;
		this.uid = uid;
		this.ip = ip;
		this.giveAway = giveAway;
		this.timeEntered = timeEntered;
	}

	private static List<GiveAway> giveAwayList = new ArrayList<GiveAway>();

	private static ArrayList<String> activeGiveAway = new ArrayList<String>();

	private static boolean hasAlreadyJoinedGiveAway(String name, String uid, String ip, String giveAway) {
		for (int index = 0; index < giveAwayList.size(); index++) {
			GiveAway instance = giveAwayList.get(index);
			if (giveAway.equals(instance.giveAway)) {
				if (name.equals(instance.name) || Misc.uidMatches(uid, instance.uid) || ip.equals(instance.ip)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isGiveawayCommand(Player player, String command) {
		if (!command.endsWith("giveaway")) {
			return false;
		}
		String giveAway = command.substring(0, command.indexOf("giveaway"));
		if (!activeGiveAway.contains(giveAway)) {
			player.getPA().sendMessage("The '" + giveAway + "' giveaway does not exist.");
			return true;
		}
		if (hasAlreadyJoinedGiveAway(player.getPlayerName(), player.addressUid, player.addressIp, giveAway)) {
			player.getPA().sendMessage("You have already joined the '" + giveAway + "' giveaway.");
			return true;
		}
		giveAwayList.add(new GiveAway(player.getPlayerName(), player.addressUid, player.addressIp, giveAway, Misc.getDateAndTime()));
		player.getPA().sendMessage("You have successfully entered the '" + giveAway + "' giveaway!");
		player.getPA().sendMessage("If you win, the rewards will be automatically added to your account.");
		return true;
	}

	public static boolean addGiveAway(Player player, String command) {
		if (!command.startsWith("giveawayadd")) {
			return false;
		}
		String giveAway = command.substring(command.indexOf("giveawayadd") + 12);
		if (!activeGiveAway.contains(giveAway)) {
			player.getPA().sendMessage("'" + giveAway + "' has been added to the giveaway list.");
			activeGiveAway.add(giveAway);
			return true;
		}
		else {
			player.getPA().sendMessage("'" + giveAway + "' has already exists as a giveaway.");
		}
		return true;
	}

	public static boolean viewGiveAway(Player player, String command) {
		if (!command.equals("giveawayview")) {
			return false;
		}
		if (activeGiveAway.isEmpty()) {
			player.getPA().sendMessage("There are no active giveaways.");
		} else {
			player.getPA().sendMessage("Active giveaways: ");
			for (int index = 0; index < activeGiveAway.size(); index++) {
				player.getPA().sendMessage("'" + activeGiveAway.get(index) + "'");
			}
		}
		return true;
	}

	public static boolean removeGiveAway(Player player, String command) {
		if (!command.startsWith("giveawayremove")) {
			return false;
		}
		String giveAway = command.substring(command.indexOf("giveawayremove") + 15);
		if (activeGiveAway.contains(giveAway)) {
			player.getPA().sendMessage("'" + giveAway + "' has been removed from the giveaway list.");
			activeGiveAway.remove(giveAway);
			for (int index = 0; index < giveAwayList.size(); index++) {
				GiveAway instance = giveAwayList.get(index);
				if (giveAway.equals(instance.giveAway)) {
					giveAwayList.remove(index);
					index--;
				}
			}
			return true;
		} else {
			player.getPA().sendMessage("'" + giveAway + "' does not exist as a giveaway.");
		}
		return true;
	}

	public static void loadGiveAwayFiles() {
		if (!FileUtility.fileExists(ACTIVE_GIVE_AWAY_FILE)) {
			return;
		}
		activeGiveAway = FileUtility.readFile(ACTIVE_GIVE_AWAY_FILE);
		ArrayList<String> lines = FileUtility.readFile(GIVE_AWAY_PLAYER_ENTRIES_FILE);
		for (int index = 0; index < lines.size(); index++) {
			String line = lines.get(index);
			String[] parse = line.split(ServerConstants.TEXT_SEPERATOR);
			giveAwayList.add(new GiveAway(parse[0], parse[1], parse[2], parse[3], parse[4]));
		}
	}

	public static void saveGiveAwayFiles() {
		FileUtility.deleteAllLines(ACTIVE_GIVE_AWAY_FILE);
		FileUtility.deleteAllLines(GIVE_AWAY_PLAYER_ENTRIES_FILE);
		FileUtility.saveArrayContentsSilent(ACTIVE_GIVE_AWAY_FILE, activeGiveAway);

		 ArrayList<String> lines = new ArrayList<String>();
		for (int index = 0; index < giveAwayList.size(); index++) {
			GiveAway instance = giveAwayList.get(index);
			lines.add(instance.name + ServerConstants.TEXT_SEPERATOR + instance.uid + ServerConstants.TEXT_SEPERATOR + instance.ip + ServerConstants.TEXT_SEPERATOR + instance.giveAway + ServerConstants.TEXT_SEPERATOR + instance.timeEntered);
		}
		FileUtility.saveArrayContentsSilent(GIVE_AWAY_PLAYER_ENTRIES_FILE, lines);
		writeToSql(giveAwayList, SQLConstants.STATS_GIVEAWAY.toTableName());
	}

	private static void writeToSql(List<GiveAway> pushed, String sqlTable) {
		Server.getSqlNetwork().submit(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE " + SQLConstants.getServerSchemaTable(SQLConstants.STATS_GIVEAWAY))) {
				statement.executeUpdate();
			}
		});
		Server.getSqlNetwork().submit(connection -> {
			try (PreparedStatement statement = connection.prepareStatement(String.format("INSERT IGNORE INTO %s (time, giveaway, name, ip, uid) VALUES(?, ?, ?, ?, ?);", sqlTable))) {
				for (GiveAway set : pushed) {
					statement.setString(1, set.timeEntered);
					statement.setString(2, set.giveAway);
					statement.setString(3, set.name);
					statement.setString(4, set.ip);
					statement.setString(5, set.uid);

					statement.addBatch();
				}
				statement.executeBatch();
			}
		});
	}
}
