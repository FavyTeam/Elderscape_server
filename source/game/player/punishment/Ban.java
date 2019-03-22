package game.player.punishment;

import core.ServerConstants;
import game.content.miscellaneous.YoutubePaid;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.EmailSystem;
import utility.FileUtility;
import utility.Misc;

/**
 * Ban an account.
 *
 * @author MGT Madness, created on 11-04-2017
 */
public class Ban {

	/**
	 * Store names of accounts that are banned.
	 */
	public static ArrayList<String> bannedList = new ArrayList<String>();

	public static void ban(Player player, String command, boolean isCommand) {
		try {
			String name = command.substring(11);
			if (name.isEmpty()) {
				return;
			}
			name = name.toLowerCase();
			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				if (player != null) {
					player.getPA().sendMessage("Account does not exist: " + name);
				}
				return;
			}
			boolean online = false;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name)) {
					online = true;
					loop.getPA().forceToLogInScreen();
					loop.setDisconnected(true, "banned");
					loop.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
					break;
				}
			}

			for (int index = 0; index < bannedList.size(); index++) {
				if (bannedList.get(index).equals(name)) {
					if (player != null) {
						player.getPA().sendMessage(name + " is already banned.");
					}
					return;
				}
			}
			bannedList.add(name);
			if (isCommand) {
				if (player != null) {
					YoutubePaid.accountBanned(player, name);
					player.getPA().sendMessage("Banned " + name + ", online: " + online);
					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has account banned '" + name + "'");
				}
			}
		} catch (Exception e) {
			if (player != null) {
				player.getPA().sendMessage("Wrong usage, ::accountban mgt madness");
			}
		}
	}

	public static void unBan(Player player, String command, boolean unIpBanAswell, boolean alert) {
		try {
			String name = command.substring(6);
			if (name.isEmpty()) {
				return;
			}
			name = name.toLowerCase();
			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				if (player != null) {
					player.getPA().sendMessage("Account does not exist: " + name);
				}
				return;
			}

			boolean deleted = false;
			if (unIpBanAswell) {
				File file = new File("backup/logs/blacklisted/names/" + name + ".txt");
				deleted = file.delete();
				if (deleted) {
					Blacklist.loadBlacklistedData();
					if (player != null) {
						player.getPA().sendMessage("Un-ipbanned " + name);
					}
					if (alert) {
						EmailSystem.addPendingEmail("Player un-ipbanned: " + name, name + " has been un-ipbanned by: " + player.getPlayerName(), "mgtdt@yahoo.com");
					}
				}
			}
			for (int index = 0; index < bannedList.size(); index++) {
				if (bannedList.get(index).equals(name)) {
					bannedList.remove(index);
					if (player != null) {
						player.getPA().sendMessage("Unbanned " + name);
						DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unbanned '" + name + "'");
					}
					return;
				}
			}
			if (deleted) {
				return;
			}
			if (player != null) {
				player.getPA().sendMessage(name + " is not banned to begin with.");
			}
		} catch (Exception e) {
			if (player != null) {
				player.getPA().sendMessage("Wrong usage, ::unban mgt madness");
			}
		}
	}

	public static void readBanLog() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/ban.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					bannedList.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
