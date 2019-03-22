package game.player.punishment;

import core.ServerConstants;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Duel arena ban.
 *
 * @author MGT Madness, created on 01-06-2017
 */
public class DuelArenaBan {
	/**
	 * Store name - ip - uid - expiry time
	 */
	public static ArrayList<String> duelBan = new ArrayList<String>();

	public final static String LOCATION = "backup/logs/duelban.txt";

	public static void saveDuelBans() {
		FileUtility.deleteAllLines(LOCATION);
		FileUtility.saveArrayContentsSilent(LOCATION, duelBan);
	}

	public static void readDuelBans() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(LOCATION));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					duelBan.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
		}

		// Delete expired bans
		for (int index = 0; index < duelBan.size(); index++) {
			String[] parse = duelBan.get(index).split(ServerConstants.TEXT_SEPERATOR);
			long expiryParse = Long.parseLong(parse[3]);

			if (System.currentTimeMillis() > expiryParse) {
				duelBan.remove(index);
				index--;
			}
		}
	}

	public static boolean duelArenaBanApply(String name, String ip, String uid, int days) {
		for (int index = 0; index < duelBan.size(); index++) {
			String[] parse = duelBan.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String nameParse = parse[0];
			long expiryParse = Long.parseLong(parse[3]);

			if (name.equals(nameParse) && (expiryParse - Misc.getHoursToMilliseconds(48)) > System.currentTimeMillis()) {
				return false;
			}
		}
		long expiryTime = System.currentTimeMillis() + (days * Misc.getHoursToMilliseconds(24));
		duelBan.add(name + ServerConstants.TEXT_SEPERATOR + ip + ServerConstants.TEXT_SEPERATOR + uid + ServerConstants.TEXT_SEPERATOR + expiryTime);
		return true;
	}

	public static void duelBan(Player player, String command) {
		String name = command.substring(10);
		name = Misc.capitalize(name);

		if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
			player.getPA().sendMessage("Account does not exist: " + name + ".");
			return;
		}

		boolean applied = false;
		boolean online = false;
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player playerloop = PlayerHandler.players[i];
			if (playerloop == null) {
				continue;
			}
			if (playerloop.getPlayerName().equalsIgnoreCase(name)) {
				applied = true;
				online = true;
				duelArenaBanApply(name, playerloop.addressIp, playerloop.addressUid, 1000);
				break;
			}
		}
		if (!applied) {
			String ip = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "lastSavedIpAddress", 3);
			String uid = Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "addressUid", 3);
			duelArenaBanApply(name, ip, uid, 1000);
		}
		player.playerAssistant.sendMessage("You have banned " + name + " from gambling for 1000 days, online: " + online);
		DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has gambling banned '" + name + "' for 1000 days.");
	}

	public static boolean isDuelBanned(Player player, boolean count7DayBansOnly) {

		// Highest entry saved because the player can ban himself twice, an extra time before his 48 hours is complete, so there will be 2 entries of the same player.
		long expiryTimeHighest = 0;
		String name = "";
		for (int index = 0; index < duelBan.size(); index++) {
			String[] parse = duelBan.get(index).split(ServerConstants.TEXT_SEPERATOR);
			String nameParse = parse[0];
			String ipParse = parse[1];
			String uidParse = parse[2];
			long expiryParse = Long.parseLong(parse[3]);

			if ((player.getPlayerName().equals(nameParse) || player.addressIp.equals(ipParse) && !player.addressIp.isEmpty() || Misc.uidMatches(player.addressIp, uidParse))
			    && expiryParse > System.currentTimeMillis()) {
				if (expiryParse > expiryTimeHighest) {
					expiryTimeHighest = expiryParse;
					name = nameParse;
				}
			}
		}
		if (expiryTimeHighest > 0) {
			long hours = (expiryTimeHighest - System.currentTimeMillis()) / Misc.getHoursToMilliseconds(1);
			if (hours > 168 && count7DayBansOnly) {
				return false;
			}
			player.getPA().sendMessage("You are banned from gambling for another " + hours + " hours.");

			// if more than 2 weeks
			if (hours > 336) {
				player.getPA().sendMessage("You are banned for committing a serious scam on '" + name + "'");
				player.getPA().sendMessage("You have to pay 10m OSRS fine to be unbanned. Buy the gold from ::goldwebsites");
				player.getPA().sendMessage("Then give it to any Moderator on ::staff to be unbanned.");
			}
			return true;
		}
		return false;
	}

}
