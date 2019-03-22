package game.player.punishment;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;
import utility.FileUtility;
import utility.Misc;

/**
 * Ip mute system, with unipmute by entering name.
 *
 * @author MGT Madness, created on 06-04-2017.
 */
public class IpMute {

	/**
	 * Store name-ip-uid
	 */
	public static ArrayList<String> ipMutedData = new ArrayList<String>();

	public static void unIpMute(Player player, String command) {
		try {
			String name = command.substring(9);
			if (name.isEmpty()) {
				return;
			}
			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				player.getPA().sendMessage("Account does not exist: " + name);
				return;
			}
			String ipTarget = "";
			String uidTarget = "";
			boolean online = false;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name)) {
					online = true;
					ipTarget = loop.addressIp;
					uidTarget = loop.addressUid;
					loop.ipMuted = false;
					name = loop.getPlayerName();
					break;
				}
			}

			if (!online) {
				ipTarget = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
				uidTarget = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			}
			boolean complete = false;
			int hours = 0;
			long time = 0;
			for (int index = 0; index < ipMutedData.size(); index++) {
				String[] parse = ipMutedData.get(index).split(ServerConstants.TEXT_SEPERATOR);
				String storedName = parse[0];
				String ip = parse[1];
				String uid = parse[2];
				if (storedName.equals(name) || ip.equals(ipTarget) || Misc.uidMatches(uid, uidTarget)) {
					//  length 3 = old mute system, length 5 is new one where it is timed and stores hours length and time muted.
					if (parse.length == 5) {
						hours = Integer.parseInt(parse[3]);
						time = Long.parseLong(parse[4]);
					}
					ipMutedData.remove(index);
					index--;
					complete = true;
					// Do not add break, because there can be more than 1 occurence of an ip muted account if they switched ips.
				}
			}
			if (!complete) {
				player.getPA().sendMessage(name + " is not ipmuted.");
				return;
			} else {
				for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
					Player loop = PlayerHandler.players[i];
					if (loop == null) {
						continue;
					}
					if (loop.addressIp.equals(ipTarget) || Misc.uidMatches(loop.addressUid, uidTarget)) {
						loop.ipMuted = false;
					}
				}
			}
			long hoursToServeLeft = (time + Misc.getHoursToMilliseconds(hours)) - System.currentTimeMillis();
			hoursToServeLeft /= Misc.getHoursToMilliseconds(1);
			String serveLeft = ", who still had " + hoursToServeLeft + " hours left of mute to serve.";
			if (hours == 0) {
				serveLeft = ", who was permanently muted.";
			}
			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unip muted '" + name + "'" + serveLeft);
			player.getPA().sendMessage("Un-ip muted " + name + ": online: " + online + serveLeft);
		} catch (Exception e) {
			player.getPA().sendMessage("Wrong usage, ::unipmute mgt madness");
		}
	}

	public static void ipMute(Player player, String command) {
		try {
			String[] parse = command.split(" ");
			int hours = Integer.parseInt(parse[1]);
			if (player != null) {
				if (player.isSupportRank()) {
					hours = 1;
				}
			}
			String name = command.replace("ipmute " + hours + " ", "");
			if (name.isEmpty()) {
				return;
			}

			boolean online = false;
			String ip = "";
			String uid = "";
			Player mutedPlayer = null;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name)) {
					online = true;
					ip = loop.addressIp;
					uid = loop.addressUid;
					loop.ipMuted = true;
					mutedPlayer = loop;
					name = loop.getPlayerName();
					break;
				}
			}

			if (!online) {
				if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt")) {
					if (player != null) {
						player.getPA().sendMessage("Account does not exist: " + name);
					}
					return;
				}
				ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
				uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			}
			uid = Blacklist.formatUid(uid);
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.addressIp.equals(ip) || Misc.uidMatches(loop.addressUid, uid)) {
					loop.ipMuted = true;
				}
			}
			ipMutedData.add(name.toLowerCase() + ServerConstants.TEXT_SEPERATOR + ip + ServerConstants.TEXT_SEPERATOR + uid + ServerConstants.TEXT_SEPERATOR + hours
			                + ServerConstants.TEXT_SEPERATOR + System.currentTimeMillis());

			if (player != null) {
				player.getPA().sendMessage(name + " has been ip muted for " + hours + " hours, online: " + online);
				DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has ip muted '" + name + "' for " + hours + " hours.");
			} else {
				DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, "[Server] has ip muted '" + name + "' for " + hours + " hours for possible advertising.");
				if (mutedPlayer != null) {
					for (int index = 0; index < mutedPlayer.newPlayerChat.size(); index++) {
						DiscordCommands.addOutputText(mutedPlayer.getPlayerName() + ": " + mutedPlayer.newPlayerChat.get(index));
					}
					DiscordBot.sendMessage(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, DiscordCommands.queuedBotString, true);
				}
			}
		} catch (Exception e) {
			if (player != null) {
				player.getPA().sendMessage("Wrong usage, ::ipmute 6 mgt madness");
			}
		}
	}

	public static void readIpMuteLog() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/ipmute.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					ipMutedData.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ipMuteLogInUpdate(Player player) {
		String lastName = "";
		for (int index = 0; index < ipMutedData.size(); index++) {
			String[] parse = ipMutedData.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String storedName = parse[0];
			String ip = parse[1];
			String uid = parse[2];
			int hours = 0;
			long time = 0;
			//  length 3 = old mute system, length 5 is new one where it is timed and stores hours length and time muted.
			if (parse.length == 5) {
				hours = Integer.parseInt(parse[3]);
				time = Long.parseLong(parse[4]);
			}
			lastName = storedName;
			if (ip.equals(player.addressIp) || player.getPlayerName().toLowerCase().equalsIgnoreCase(storedName) || Misc.uidMatches(player.addressUid, uid)) {
				if (hours == 0 || System.currentTimeMillis() < (time + Misc.getHoursToMilliseconds(hours))) {
					player.ipMuted = true;
				} else {
					ipMutedData.remove(index);
				}
				break;
			}
		}
		if (player.ipMuted) {
			player.ipMutedOriginalName = lastName;
		}
	}
}
