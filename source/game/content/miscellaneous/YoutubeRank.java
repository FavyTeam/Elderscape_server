package game.content.miscellaneous;

import core.GameType;
import core.ServerConstants;
import game.content.staff.StaffManagement;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.log.CoinEconomyTracker;
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
 * Youtube rank, where they get it for only a few days
 *
 * @author MGT Madness, created on 29-05-2017.
 */
public class YoutubeRank {
	/**
	 * Store name#time given.
	 */
	public static ArrayList<String> youtubeRankData = new ArrayList<String>();

	public final static int DAYS = 7;

	public final static String LOCATION = "backup/logs/youtube.txt";

	public static void readYoutuberRanks() {
		try {
			BufferedReader file = new BufferedReader(new FileReader(LOCATION));
			String line;
			while ((line = file.readLine()) != null) {
				if (!line.isEmpty()) {
					youtubeRankData.add(line);
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void giveYoutuber(Player player, String command) {
		String name = command.substring(7);

		if (!FileUtility.accountExists(name)) {
			player.getPA().sendMessage("Account does not exist: " + name);
			return;
		}
		DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has given youtube rank to '" + Misc.capitalize(name) + "'.");
		for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
			Player playerloop = PlayerHandler.players[i];
			if (playerloop == null) {
				continue;
			}
			if (playerloop.getPlayerName().equalsIgnoreCase(name)) {
				giveYoutubeRank(playerloop, player);
				return;
			}
		}
		if (!canGetYoutubeRank(Misc.capitalize(name), null, player)) {
			return;
		}
		if (StaffManagement.pendingRanks.contains(name.toLowerCase() + "-21")) {
			player.getPA().sendMessage(name + " has already been promoted to Youtube rank.");
			return;
		}
		StaffManagement.pendingRanks.add(name.toLowerCase() + "-21");
		player.getPA().sendMessage(name + " is offline but has been promoted to Youtube rank!");
	}

	public static void giveYoutubeRank(Player playerloop, Player moderator) {
		if (!canGetYoutubeRank(playerloop.getPlayerName(), playerloop, moderator)) {
			return;
		}
		if (!playerloop.isModeratorRank()) {
			playerloop.playerRights = 21;
			playerloop.setUpdateRequired(true);
			playerloop.setAppearanceUpdateRequired(true);
		}
		playerloop.getPA().sendMessage("You have been promoted to Youtuber rank for " + DAYS + " days!");
		playerloop.getPA().sendMessage("You can renew it by following the steps on ::yt when you");
		playerloop.getPA().sendMessage("upload a new video.");
		CoinEconomyTracker.addIncomeList(playerloop, "YOUTUBE " + YoutubeRank.getRewardAmount());
		ItemAssistant.addItemReward(null, playerloop.getPlayerName(), ServerConstants.getMainCurrencyId(), getRewardAmount(), false, -1);
		String line = Misc.capitalize(playerloop.getPlayerName()) + "#" + System.currentTimeMillis();

		// Remove their previous entry, then add new entry.
		for (int index = 0; index < youtubeRankData.size(); index++) {
			String parse[] = youtubeRankData.get(index).split("#");
			String userName = parse[0];
			if (playerloop.getPlayerName().equals(userName)) {
				youtubeRankData.remove(index);
				break;
			}
		}
		youtubeRankData.add(line);
		FileUtility.addLineOnTxt(LOCATION, line);
		if (moderator != null) {
			moderator.playerAssistant.sendMessage("You have promoted " + playerloop.getPlayerName() + " to Youtuber for " + DAYS + " days.");
		}
		playerloop.playerAssistant.announce(
				GameMode.getGameModeName(playerloop) + " has been awarded <img=21>Youtuber rank and " + Misc.formatRunescapeStyle(YoutubeRank.getRewardAmount()) + " " + ServerConstants.getMainCurrencyName() + "! Check ::yt");

	}

	public static boolean canGetYoutubeRank(String giveToPlayerName, Player playerloop, Player moderator) {
		for (int index = 0; index < youtubeRankData.size(); index++) {
			String parse[] = youtubeRankData.get(index).split("#");
			String userName = parse[0];
			if (giveToPlayerName.equals(userName)) {
				long originalTime = Long.parseLong(parse[1]);
				if (System.currentTimeMillis() - originalTime <= Misc.getHoursToMilliseconds(120)) {
					if (moderator != null) {
						moderator.getPA().sendMessage("This player was recently promoted to Youtube rank.");
					}
					if (playerloop != null) {
						playerloop.getPA().sendMessage("You were already promoted recently to Youtube rank.");
					}
					return false;
				}
			}
		}
		return true;
	}

	public static int getRewardAmount() {
		return GameType.isOsrsPvp() ? 150_000 : 75_000_000;
	}

	/**
	 * Check the status of the Youtuber rank.
	 */
	public static void logInUpdate(Player player) {
		for (int index = 0; index < youtubeRankData.size(); index++) {
			String parse[] = youtubeRankData.get(index).split("#");
			String userName = parse[0];
			long originalTime = Long.parseLong(parse[1]);

			if (player.getPlayerName().equals(userName)) {
				long oneDay = 86400000;
				long daysLeft = ((oneDay * (long) DAYS) - (System.currentTimeMillis() - originalTime)) / (oneDay);
				if (daysLeft > 0) {
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have " + daysLeft + " days of youtuber rank left.");
					return;
				}
				// all days have passed
				else if ((System.currentTimeMillis() - originalTime) > (oneDay * (long) DAYS)) {
					youtubeRankData.remove(index);
					FileUtility.deleteAllLines(LOCATION);
					FileUtility.saveArrayContentsSilent(LOCATION, youtubeRankData);
					if (!player.isSupportRank() && !player.isModeratorRank()) {
						PlayerRank.demoteAndGiveBackDonatorOrIronManRank(player, true);
					}
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "Your youtuber rank has been removed. You can earn it back by uploading");
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "a " + ServerConstants.getServerName() + " video! Check ::yt");
					return;
				}
				// Less than a day left.
				else {
					long hoursLeft = ((oneDay * (long) DAYS) - (System.currentTimeMillis() - originalTime));
					hoursLeft /= (long) ServerConstants.MILLISECONDS_HOUR;
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have " + hoursLeft + " hours of your youtube rank left! Renew it by uploading");
					player.getPA().sendMessage(ServerConstants.BLUE_COL + "a " + ServerConstants.getServerName() + " video! Check ::yt");
					return;
				}
			}
		}
	}
}
