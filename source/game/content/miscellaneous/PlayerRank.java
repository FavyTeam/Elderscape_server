package game.content.miscellaneous;

import core.ServerConfiguration;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.starter.GameMode;
import game.player.Player;

/**
 * Easy way to edit player rank icons.
 *
 * @author MGT Madness, created on 18-01-206.
 */
public class PlayerRank {

	/**
	 * Icons for clan chat, yell and profile.
	 */
	public static String getIconText(int rank, boolean space) {
		if (rank == 0) {
			return "";
		} else {
			return "<img=" + rank + ">" + (space ? " " : "");
		}
	}

	public static boolean isDeveloper(Player player) {
		if (player.getPlayerName().equals("Mgt Madness") || player.getPlayerName().equals("Owain") && ServerConfiguration.DEBUG_MODE
		    || player.getPlayerName().equals("Jason") && ServerConfiguration.DEBUG_MODE) {
			return true;
		}
		return false;
	}

	/**
	 * Rank name for profile.
	 */
	public static String getRankName(String rankIcon, int rank) {
		if (rank == 2) {
			return "Administrator";
		} else if (rank == 1) {
			return "Moderator";
		} else if (rank == 3) {
			return "Donator";
		} else if (rank == 4) {
			return "Super Donator";
		} else if (rank == 5) {
			return "Extreme Donator";
		} else if (rank == 6) {
			return "Legendary Donator";
		} else if (rank == 10) {
			return "Support";
		} else if (rank == 22) {
			return "Ultimate Donator";
		} else if (rank == 7) {
			return "Uber Donator";
		} else if (rank == 23) {
			return "Immortal Donator";
		} else if (rank == 24) {
			return "Supreme Donator";
		} else if (rank == 28) {
			return "Lucifer Donator";
		} else if (rank == 36) {
			return "Omega Donator";
		} else if (rank == 21) {
			return "Youtuber";
		} else if (rank == 9) {
			return "Iron Man";
		} else if (rank == 26) {
			return "Hardcore Iron Man";
		} else if (rank == 25) {
			return "Ultimate Iron Man";
		} else if (rank == 31) {
			return "Head Moderator";
		} else if (rank == 33) {
			return "Developer";
		}
		return rankIcon;
	}

	public static void demoteAndGiveBackDonatorOrIronManRank(Player playerloop, boolean message) {
		playerloop.playerRights = 0;
		int playerRights = -1;
		// Search for the next rank, then see if player has the amount for next rank, then add to claimed.
		for (DonatorRankSpentData data : DonatorRankSpentData.values()) {
			if (playerloop.donatorTokensRankUsed >= data.getTokensRequired()) {
				playerRights = data.getPlayerRights();
			}
		}
		if (GameMode.getGameMode(playerloop, "STANDARD IRON MAN")) {
			playerRights = 9;
		} else if (GameMode.getGameMode(playerloop, "HARDCORE IRON MAN")) {
			playerRights = 26;
		} else if (GameMode.getGameMode(playerloop, "ULTIMATE IRON MAN")) {
			playerRights = 25;
		}
		if (playerRights == -1) {
			return;
		}
		if (playerRights == playerloop.playerRights) {
			return;
		}
		if (message) {
			playerloop.getPA().sendMessage("You have been demoted to a normal player.");
		}
		playerloop.playerRights = playerRights;
		playerloop.setUpdateRequired(true);
		playerloop.setAppearanceUpdateRequired(true);
	}

}
