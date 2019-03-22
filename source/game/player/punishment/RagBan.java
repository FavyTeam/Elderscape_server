package game.player.punishment;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.miscellaneous.Announcement;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Ban an account.
 *
 * @author MGT Madness, created on 11-04-2017
 */
public class RagBan {
	public String name = "";

	public String ip = "";

	public String mac = "";

	public String uid = "";

	public int hours = 0;

	public long timeBanned = 0;

	public static List<RagBan> ragBanList = new ArrayList<RagBan>();

	/*
	 *
	ragBanList.add(name + "-" + ip + " " + hours + " " + System.currentTimeMillis() + " " + mac + " " + uid);
	 */
	public RagBan(String name, String ip, int hours, long timeBanned, String mac, String uid) {
		this.name = name;
		this.ip = ip;
		this.hours = hours;
		this.timeBanned = timeBanned;
		this.mac = mac;
		this.uid = uid;
	}

	public static void ragBan(Player player, String command) {
		try {
			String parse[] = command.split(" ");
			int hours = Integer.parseInt(parse[1]);
			if (player.isSupportRank()) {
				hours = 1;
			}
			String name = command.replace(parse[0] + " " + parse[1] + " ", "");
			if (name.isEmpty()) {
				return;
			}
			name = name.toLowerCase();
			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				player.getPA().sendMessage("Account does not exist: " + name);
				return;
			}


			for (int index = 0; index < ragBanList.size(); index++) {
				if (ragBanList.get(index).name.equals(Misc.capitalize(name))) {
					player.getPA().sendMessage(name + " is already rag banned.");
					return;
				}
			}
			boolean online = false;
			String ip = "";
			String uid = "";
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name)) {
					if (loop.getMinigame() != null) {
						player.playerAssistant.sendMessage(loop.getPlayerName() + " is in a minigame.");
						return;
					}
					if (loop.getDuelStatus() >= 1) {
						player.getPA().sendMessage(loop.getPlayerName() + " is dueling.");
						return;
					}
					if (loop.getHeight() == 20) {
						player.getPA().sendMessage(loop.getPlayerName() + " is at the tournament.");
						return;
					}
					if (loop.isModeratorRank()) {
						player.getPA().sendMessage("?");
						return;
					}
					online = true;
					ip = loop.addressIp;
					uid = loop.addressUid;
					if (!loop.isJailed()) {
						if (Combat.inCombat(loop)) {
							player.getPA().sendMessage(loop.getPlayerName() + " is in combat and cannot be forced moved, but has been banned.");
						} else if (loop.isJailed()) {
							player.getPA().sendMessage(loop.getPlayerName() + " is jailed and cannot be forced moved, but has been banned.");
						} else {
							loop.getPA().movePlayer(3089, 3502, 0);
						}
					}
					break;
				}
			}
			if (!online) {

				ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
				uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			}
			if (!Blacklist.useAbleData(uid)) {
				uid = "invalid";
			}
			String mac = "invalid";
			ragBanList.add(new RagBan(Misc.capitalize(name), ip, hours, System.currentTimeMillis(), mac, uid));
			player.getPA().sendMessage(name + " has been rag banned, online: " + online + ", for: " + hours + " hours.");
			FileUtility
					.addLineOnTxt("backup/logs/rag_ban_history.txt", Misc.getDateAndTime() + " " + player.getPlayerName() + " banned: " + name + "-" + ip + " " + mac + " " + uid + "for x" + hours + " hours.");
			Announcement.announce(Misc.capitalize(name) + " has been banned from using the wild because of ragging others.");

			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL,
			                           player.getPlayerName() + " has wild banned '" + name + "' for " + hours + " hour" + Misc.getPluralS(hours));
		} catch (Exception e) {
			player.getPA().sendMessage("Wrong usage, ::ragban 5 mgt madness");
		}
	}

	public static void unRagBan(Player player, String command) {
		try {
			String name = command.substring(9);

			if (name.isEmpty()) {
				return;
			}
			name = name.toLowerCase();

			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				player.getPA().sendMessage("Account does not exist: " + name);
				return;
			}

			for (int index = 0; index < ragBanList.size(); index++) {
				if (ragBanList.get(index).name.equals(Misc.capitalize(name))) {
					ragBanList.remove(index);
					player.getPA().sendMessage(name + " has been unbanned.");
					DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has unwild banned '" + name + "'");
					FileUtility.addLineOnTxt("backup/logs/rag_ban_history.txt", Misc.getDateAndTime() + " " + player.getPlayerName() + " unbanned: " + name);
					return;
				}
			}
			player.getPA().sendMessage(name + " is not banned to begin with.");
		} catch (Exception e) {
			player.getPA().sendMessage("Wrong usage, ::unragban mgt madness");
		}
	}

	public static void readBanLog() {
		try {
			BufferedReader file = new BufferedReader(new FileReader("backup/logs/ragban.txt"));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					String parse1[] = line.split(ServerConstants.TEXT_SEPERATOR);
					int hours = Integer.parseInt(parse1[2]);
					long timeBanned = Long.parseLong(parse1[3]);
					ragBanList.add(new RagBan(parse1[0], parse1[1], hours, timeBanned, parse1[4], parse1[5]));
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean multiLogAllowed = false;

	public static void loggedInWithMultiLogging(Player player) {
		if (multiLogAllowed || ServerConfiguration.DEBUG_MODE) {
			return;
		}
		int count = 0;
		for (int index = 0; index < WildernessData.wildernessData.size(); index++) {
			if (player.addressIp.equals(WildernessData.wildernessData.get(index).ip) && Misc.uidMatches(player.addressUid, WildernessData.wildernessData.get(index).uid)) {
				if (count == 1) {
					RagBan.wildDebug
							.add(Misc.getDateAndTime() + " Declined1: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
							     + player.getY() + ", " + player.getHeight());

					player.getPA().sendMessage("You cannot multi-log into the wilderness.");
					player.getPA().sendMessage("Log off and remove your other account so you can start moving again.");
					player.getPA().sendMessage("Type in ::multibug if this is a bug!");
					player.cannotIssueMovement = true;
					player.multiLoggingInWild = true;
					break;
				}
				count++;
			}
		}
	}

	public static boolean isRagBanned(Player player) {
		if (player.bot) {
			return false;
		}

		if (!multiLogAllowed && !ServerConfiguration.DEBUG_MODE) {
			int count = 0;
			for (int index = 0; index < WildernessData.wildernessData.size(); index++) {
				if (player.addressIp.equals(WildernessData.wildernessData.get(index).ip) && Misc.uidMatches(player.addressUid, WildernessData.wildernessData.get(index).uid)) {
					count++;
					if (count == 1) {
						RagBan.wildDebug
								.add(Misc.getDateAndTime() + " Declined2: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX()
								     + ", " + player.getY() + ", " + player.getHeight());
						player.getPA().sendMessage("You cannot multi-log into the wilderness.");
						player.getPA().sendMessage("Type in ::multibug if this is a bug!");
						return true;
					}
				}
			}
		}

		for (int index = 0; index < ragBanList.size(); index++) {
			long hours = ragBanList.get(index).hours;
			long timeBanned = ragBanList.get(index).timeBanned;
			if (ragBanList.get(index).name.equals(player.getPlayerName()) || player.addressIp.equals(ragBanList.get(index).ip) || Misc.uidMatches(player.addressUid,
			                                                                                                                                      ragBanList.get(index).uid)) {
				if (System.currentTimeMillis() > (timeBanned + hours * ServerConstants.MILLISECONDS_HOUR)) {
					ragBanList.remove(index);
					index--;
					continue;
				}
				long left = (timeBanned + (long) hours * (long) ServerConstants.MILLISECONDS_HOUR) - System.currentTimeMillis();
				left /= 60000L;
				player.getPA().sendMessage("You are banned for " + left + " more minutes from the wilderness.");
				return true;
			}
		}
		return false;
	}

	/**
	 * Clear expired Rag banned players.
	 */
	public static void clearOldRagBanList() {

		for (int index = 0; index < ragBanList.size(); index++) {
			int hours = ragBanList.get(index).hours;
			long timeBanned = ragBanList.get(index).timeBanned;
			if (System.currentTimeMillis() > (timeBanned + (hours * ServerConstants.MILLISECONDS_HOUR))) {
				ragBanList.remove(index);
				index--;
			}
		}
	}

	public static void addToWilderness(String addressIp, String uid) {
		if (addressIp.isEmpty()) {
			return;
		}
		if (uid.isEmpty()) {
			uid = addressIp;
		}
		WildernessData.wildernessData.add(new WildernessData(addressIp, uid));
	}

	public static void logOutUpdate(Player player) {
		if (Area.inDangerousPvpArea(player)) {
			RagBan.wildDebug.add(Misc.getDateAndTime() + " Remove2: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
			                     + player.getY() + ", " + player.getHeight());
			removeFromWilderness(player.addressIp, player.addressUid);
		}
	}

	public static void removeFromWilderness(String addressIp, String uid) {
		for (int index = 0; index < WildernessData.wildernessData.size(); index++) {
			if (addressIp.equals(WildernessData.wildernessData.get(index).ip) && uid.equals(WildernessData.wildernessData.get(index).uid)) {
				WildernessData.wildernessData.remove(index);
				break;
			}
		}
	}

	public static void loggedIn(Player player) {
		if (player.bot) {
			return;
		}
		if (Area.inDangerousPvpArea(player)) {
			RagBan.wildDebug.add(Misc.getDateAndTime() + " Add2: " + player.getPlayerName() + ", " + player.addressIp + ", " + player.addressUid + ", " + player.getX() + ", "
			                     + player.getY() + ", " + player.getHeight());
			RagBan.addToWilderness(player.addressIp, player.addressUid);
		}

	}

	/**
	 * There is rare issues where a player cannot enter wild, even though they did not double log.
	 */
	public static ArrayList<String> wildDebug = new ArrayList<String>();

	public static void saveRagBanList() {
		ArrayList<String> stringList = new ArrayList<String>();
		for (int index = 0; index < ragBanList.size(); index++) {
			stringList
					.add(ragBanList.get(index).name + ServerConstants.TEXT_SEPERATOR + ragBanList.get(index).ip + ServerConstants.TEXT_SEPERATOR + ragBanList.get(index).hours + ServerConstants.TEXT_SEPERATOR + ragBanList.get(index).timeBanned + ServerConstants.TEXT_SEPERATOR + ragBanList.get(index).mac
							+ ServerConstants.TEXT_SEPERATOR + ragBanList.get(index).uid);
		}
		FileUtility.saveArrayContents("./backup/logs/ragban.txt", stringList);

	}

}
