package game.player.punishment;

import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.player.Player;
import game.player.PlayerHandler;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Yell mute players with increased lengths.
 *
 * @author MGT Madness, created on 15-07-2017
 */
public class YellMute {
	/**
	 * For each time the player is yell muted, the punishment increases.
	 */
	public final static int[] YELL_MUTE_HOURS =
			{3, 12, 24, 72, 168, 336, 672};

	/**
	 * Apply the yell mute command against the given player.
	 *
	 * @param player
	 * @param command
	 */
	public static void yellMute(Player player, String command) {
		try {
			String name = command.substring(9);
			if (name.isEmpty()) {
				return;
			}
			if (!FileUtility.fileExists(ServerConstants.getCharacterLocation() + name + ".txt") && Misc.getPlayerByName(name) == null) {
				player.getPA().sendMessage("Account does not exist: " + name);
				return;
			}
			boolean online = false;
			long punishmentHours = 0;
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop == null) {
					continue;
				}
				if (loop.getPlayerName().equalsIgnoreCase(name)) {
					online = true;
					loop.yellMutes++;
					if (loop.yellMutes > YELL_MUTE_HOURS.length - 1) {
						loop.yellMutes = YELL_MUTE_HOURS.length - 1;
					}
					name = loop.getPlayerName();
					punishmentHours = YELL_MUTE_HOURS[loop.yellMutes];
					loop.yellMuteExpireTime = System.currentTimeMillis() + ((long) YELL_MUTE_HOURS[loop.yellMutes] * (long) ServerConstants.MILLISECONDS_HOUR);
					break;
				}
			}
			int yellMutes = 0;
			long yellMuteExpireTime = 0;
			if (!online) {
				yellMutes = Integer.parseInt(Blacklist.readOfflinePlayerData(ServerConstants.getCharacterLocation() + name + ".txt", "yellMutes", 3));
				yellMutes++;
				if (yellMutes - 1 > YELL_MUTE_HOURS.length) {
					yellMutes = YELL_MUTE_HOURS.length - 1;
				}
				punishmentHours = YELL_MUTE_HOURS[yellMutes - 1];
				yellMuteExpireTime = System.currentTimeMillis() + (YELL_MUTE_HOURS[yellMutes - 1] * ServerConstants.MILLISECONDS_HOUR);

				name = name.toLowerCase();
				final String name1 = name;
				final int yellMutes1 = yellMutes;
				final long yellMuteExpireTime1 = yellMuteExpireTime;
				new Thread(new Runnable() {
					public void run() {
						try {
							BufferedReader file = new BufferedReader(new FileReader(ServerConstants.getCharacterLocation() + name1 + ".txt"));
							String line;
							String input = "";
							while ((line = file.readLine()) != null) {
								if (line.startsWith("yellMutes =")) {
									line = "yellMutes = " + yellMutes1;
								} else if (line.startsWith("yellMuteExpireTime =")) {
									line = "yellMuteExpireTime = " + yellMuteExpireTime1;
								}
								input += line + '\n';
							}
							FileOutputStream File = new FileOutputStream(ServerConstants.getCharacterLocation() + name1 + ".txt");
							File.write(input.getBytes());
							file.close();
							File.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();

			}
			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has yell muted '" + name + "'");
			Announcement.announce(Misc.capitalize(name) + " has been yell muted for " + punishmentHours + " hours for not following yell rules ::rules", ServerConstants.RED_COL);
		} catch (Exception e) {
			player.getPA().sendMessage("Wrong usage, ::yellmute mgt madness");
		}
	}

	/**
	 * @return True if the player is muted.
	 */
	public static boolean isYellMuted(Player player) {
		if (player.yellMuteExpireTime - System.currentTimeMillis() > 0) {
			Mute.calculateTimeTillUnmute(player, player.yellMuteExpireTime, "You are yell muted for ");
			return true;
		}
		return false;
	}
}
