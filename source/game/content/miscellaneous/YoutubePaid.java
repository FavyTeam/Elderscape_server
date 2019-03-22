package game.content.miscellaneous;

import com.google.common.io.Files;
import core.Server;
import core.ServerConstants;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.punishment.Ban;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import network.sql.SQLConstants;
import tools.EconomyScan;
import utility.AESencrp;
import utility.FileUtility;
import utility.Misc;

/**
 * Kit out paid youtubers and track their activity.
 * @author MGT Madness, created on 02-09-2018.
 *
 */
public class YoutubePaid {

	private final static String PAID_YOUTUBERS_FILE = "backup/logs/giveaway/paid_youtubers.txt";

	private static final String YOUTUBE_ACCOUNT_TEMPLATE_LOCATION = ServerConstants.getDataLocation() + "content/youtube_account.txt";

	private static ArrayList<String> paidYoutubers = new ArrayList<String>();

	public static boolean givePaidYoutubeCommand(Player player, String command) {
		String commandTrigger = "givepaidyt";
		if (!command.startsWith(commandTrigger)) {
			return false;
		}
		command = command.substring(command.indexOf(commandTrigger) + commandTrigger.length() + 1);
		command = command.trim();

		final String finalCommand = command;
		final String realCharacterLocation = ServerConstants.getCharacterLocation() + finalCommand + ".txt";
		player.getPA().sendMessage("Set up started for: " + finalCommand);
		String newPassword = RandomEvent.adj[Misc.random(RandomEvent.adj.length - 1)] + Misc.random(0, 99);
		CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent<Object>() {
			@Override
			public void execute(CycleEventContainer container) {
				switch (container.getExecutions()) {
					case 1 :
						if (Misc.getPlayerByName(finalCommand) != null) {
							Ban.ban(null, "accountban " + finalCommand, false);
						}
						break;

					case 4 :
						if (FileUtility.accountExists(finalCommand)) {
							new File(realCharacterLocation).delete();
						}
						try {
							Files.copy(new File(YOUTUBE_ACCOUNT_TEMPLATE_LOCATION), new File(realCharacterLocation));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;

					case 5 :
						FileUtility.editCharacterFile(finalCommand, "Username", Misc.capitalize(finalCommand));
						break;

					case 6 :
						try {
							FileUtility.editCharacterFile(finalCommand, "Password", AESencrp.encrypt(newPassword));
						} catch (Exception e) {
							e.printStackTrace();
						}
						Ban.unBan(null, "unban " + finalCommand, true, false);
						paidYoutubers.remove(finalCommand);
						paidYoutubers.add(finalCommand);
						container.stop();
						break;
				}
			}

			@Override
			public void stop() {
				player.getPA().sendMessage("Paid youtube account complete: '" + finalCommand + "', with password: " + newPassword);
			}
		}, 1);
		return true;
	}

	public static boolean blackSkullCommand(Player player, String command) {
		if (command.equals("blackskull")) {
			if (paidYoutubers.contains(player.getLowercaseName()) || player.isAdministratorRank()) {
				player.currentKillStreak = 150;
				Skull.whiteSkull(player);
				player.getPA().sendMessage("Black skull has been activated!");
			} else {
				player.getPA().sendMessage("You cannot use this.");
			}
			return true;
		}

		return false;
	}

	public static void savePaidYoutubersFile() {
		FileUtility.deleteAllLines(PAID_YOUTUBERS_FILE);
		FileUtility.saveArrayContentsSilent(PAID_YOUTUBERS_FILE, paidYoutubers);
		writeToSql();

	}

	public static void readPaidYoutubersFile() {
		paidYoutubers = FileUtility.readFile(PAID_YOUTUBERS_FILE);
	}

	public static void accountBanned(Player player, String name) {
		if (paidYoutubers.contains(name)) {
			EconomyScan.doNotPrint = true;
			EconomyScan.scanAccount(new File(ServerConstants.getCharacterLocationWithoutLastSlash() + "/" + name + ".txt"));
			player.getPA().sendMessage(name + " account wealth is: " + Misc.formatRunescapeStyle(EconomyScan.accountWealth));
		}
		paidYoutubers.remove(name);
	}

	private static List<YoutubePaid> youtubePaidList = new ArrayList<YoutubePaid>();

	private static void writeToSql() {
		Server.getSqlNetwork().submit(connection -> {
			try (PreparedStatement statement = connection.prepareStatement(String.format("INSERT IGNORE INTO %s (name, action) VALUES(?, ?);", SQLConstants.STATS_PAID_YOUTUBER.toTableName()))) {
				for (YoutubePaid set : youtubePaidList) {
					statement.setString(1, set.name);
					statement.setString(2, set.action);

					statement.addBatch();
				}
				youtubePaidList.clear();
				statement.executeBatch();
			}
		});
	}

	private String name;

	private String action;

	public YoutubePaid(String name, String action) {
		this.name = name;
		this.action = action;
	}

	public static void addEntry(String playerName, String action) {
		if (paidYoutubers.contains(playerName.toLowerCase())) {
			youtubePaidList.add(new YoutubePaid(playerName, action));
		}
	}

}
