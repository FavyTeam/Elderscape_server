package game.content.staff;

import core.ServerConstants;
import game.content.miscellaneous.PlayerRank;
import game.content.miscellaneous.YoutubeRank;
import game.player.Player;
import java.util.ArrayList;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import utility.FileUtility;
import utility.Misc;

/**
 * Who is support, staff and who handles the Osrs donations.
 *
 * @author MGT Madness, created on 18-10-2017.
 */
public class StaffManagement {

	/**
	 * Store the player's name, followed by the rights he should be changed to.
	 * mgt madness-1 means Mgt madness will get Moderator when he logs on.
	 * If its 0, then it means my rank will be removed.
	 */
	public static ArrayList<String> pendingRanks = new ArrayList<String>();

	/**
	 * Store all the staff team here. Admins, mods and supports
	 * mgt madness-administrator
	 */
	public static ArrayList<String> staffRanks = new ArrayList<String>();

	public static int signUpThreadId = -1;

	public static int hoursPlayTimeRequired = -1;

	public static int hoursActivePlayedTimeInTheLastWeek = -1;

	private final static String SIGN_UP_DATA_LOCATION = "backup/logs/staff/signup.txt";

	public static void loadSignUpData() {
		ArrayList<String> arraylist = FileUtility.readFile(SIGN_UP_DATA_LOCATION);
		try {
			String[] parse = arraylist.get(0).split(" ");
			signUpThreadId = Integer.parseInt(parse[0]);
			hoursPlayTimeRequired = Integer.parseInt(parse[1]);
			hoursActivePlayedTimeInTheLastWeek = Integer.parseInt(parse[2]);
		} catch (Exception e) {

		}
	}

	public static void saveSignUpData() {
		FileUtility.deleteAllLines(SIGN_UP_DATA_LOCATION);
		FileUtility.addLineOnTxt(SIGN_UP_DATA_LOCATION, signUpThreadId + " " + hoursPlayTimeRequired + " " + hoursActivePlayedTimeInTheLastWeek);
	}

	/**
	 * When the player logs in, check if they are to be demoted or promoted.
	 */
	public static void pendingRankLogIn(Player player) {
		for (int index = 0; index < pendingRanks.size(); index++) {
			String[] parse = pendingRanks.get(index).split("-");
			String name = parse[0];
			int rights = Integer.parseInt(parse[1]);

			if (!name.equalsIgnoreCase(player.getLowercaseName())) {
				continue;
			}
			rankOnlinePlayer(player, rights);
			pendingRanks.remove(index);
			index--;
		}
	}

	private static void rankOnlinePlayer(Player player, int rights) {
		if (rights == 0) {
			PlayerRank.demoteAndGiveBackDonatorOrIronManRank(player, true);
		} else {
			if (rights == 21) {
				if (!YoutubeRank.canGetYoutubeRank(player.getPlayerName(), player, null)) {
					return;
				}
			}
			player.playerRights = rights;
			player.setUpdateRequired(true);
			player.setAppearanceUpdateRequired(true);
			player.playerAssistant.sendMessage(ServerConstants.GREEN_COL_PLAIN + "You have been promoted to " + PlayerRank.getRankName("", rights) + " rank.");
			if (rights == 31) {
				player.yellTag = "Head Mod";
			}
			if (rights == 21) {
				YoutubeRank.giveYoutubeRank(player, null);
			}
		}

	}

	public static void changePlayerRank(Player player, String command) {
		if (!player.isHeadModeratorRank()) {
			return;
		}
		try {
			String[] parse = command.split(" ");
			String rank = parse[1].toLowerCase();
			String name = command.replace(parse[0] + " " + parse[1] + " ", "").toLowerCase();
			int rights = 0;
			rights = rank.equals("support") ? 10 : rank.equals("moderator") ? 1 : rank.equals("head") ? 31 : rank.equals("dev") ? 33 : 0;
			if (rights == 0) {
				rank = "player";
			}
			if (name.equalsIgnoreCase("mgt madness")) {
				return;
			}
			DiscordBot.sendMessageDate(DiscordConstants.STAFF_COMMANDS_LOG_CHANNEL, player.getPlayerName() + " has ranked '" + name + "' to " + rank);

			Player loop = Misc.getPlayerByName(name);
			if (loop != null) {
				rankOnlinePlayer(loop, rights);
			} else {
				pendingRanks.add(name + "-" + rights);
			}
			if (rights >= 1) {
				removeFromStaffList(name); // Incase they are support, so remove them from it before adding them to become moderator.
				staffRanks.add(name + "-" + PlayerRank.getRankName("", rights));
			} else {
				removeFromStaffList(name);
			}
			player.getPA().sendMessage(name + " has been ranked to: " + rank + ".");
		} catch (Exception e) {
			player.getPA().sendMessage("Use as ::rank support mgt madness");
		}
	}

	private static void removeFromStaffList(String name) {
		for (int index = 0; index < staffRanks.size(); index++) {
			String[] parse1 = staffRanks.get(index).split("-");
			if (name.equals(parse1[0])) {
				staffRanks.remove(index);
				break;
			}
		}
	}

	public static void readStaffManagementFiles() {
		pendingRanks = FileUtility.readFile("backup/logs/staff/pending_ranks.txt");
		staffRanks = FileUtility.readFile("backup/logs/staff/staff_team.txt");

	}

	public static void saveStaffManagementFiles() {
		FileUtility.deleteAllLines("backup/logs/staff/osrs_moderators.txt");
		FileUtility.deleteAllLines("backup/logs/staff/pending_ranks.txt");
		FileUtility.deleteAllLines("backup/logs/staff/staff_team.txt");
		FileUtility.deleteAllLines("backup/logs/staff/head_moderators.txt");

		FileUtility.saveArrayContentsSilent("backup/logs/staff/pending_ranks.txt", pendingRanks);
		FileUtility.saveArrayContentsSilent("backup/logs/staff/staff_team.txt", staffRanks);
	}
}
