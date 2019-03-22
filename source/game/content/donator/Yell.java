package game.content.donator;

import core.ServerConstants;
import game.content.miscellaneous.PlayerRank;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.punishment.Mute;
import game.player.punishment.YellMute;
import utility.Misc;

public class Yell {

	/**
	 * chat command.
	 *
	 * @param player The associated player.
	 * @param playerCommand The command used by the player.
	 */
	public static void yell(Player player, String playerCommand) {
	
		if (Yell.disableYell) {
			player.getPA().sendMessage("Yell is disabled temporariy due to severe toxicity.");
			return;
		}
		int seconds = 30;
		if (player.isImmortalDonator() || player.isModeratorRank() || player.isSupportRank()) {
			seconds = 1;
		} else if (player.isUberDonator()) {
			seconds = 3;
		} else if (player.isUltimateDonator()) {
			seconds = 5;
		} else if (player.isLegendaryDonator()) {
			seconds = 10;
		} else if (player.isExtremeDonator()) {
			seconds = 15;
		} else if (player.isSuperDonator()) {
			seconds = 20;
		}
		if (System.currentTimeMillis() - player.yellTimeUsed < (1000 * seconds)) {
			long left = (System.currentTimeMillis() - player.yellTimeUsed) / 1000;
			left = seconds - left;
			player.getPA().sendMessage("You may yell again in " + left + " second" + Misc.getPluralS((int) left) + ".");
			return;
		}
		if (Mute.isMuted(player)) {
			return;
		}
		if (YellMute.isYellMuted(player)) {
			return;
		}
		try {
			String message = playerCommand.substring(5);
	
			/* Check for empty message. */
			if (message.isEmpty()) {
				return;
			}
	
			if (message.toLowerCase().contains("<col=") || message.toLowerCase().contains("<img=")) {
				return;
			} else if (Misc.containsPassword(player.playerPass, message)) {
				return;
			}
	
			/* Convert message to lowercase and capitalize first letter. */
			String convertMessage = playerCommand.substring(5);
			convertMessage = convertMessage.toLowerCase();
	
			if (Misc.checkForOffensive(message)) {
				player.playerAssistant.sendMessage("Do not use offensive language or you will be muted.");
				return;
			}
			if (player.isJailed()) {
				player.getPA().sendMessage("Cannot yell while jailed.");
				return;
			}
			player.yellTimeUsed = System.currentTimeMillis();
	
			String name = player.getCapitalizedName();
			String tag = "Yell";
			String colour = "255";
			if (player.isAdministratorRank()) {
				tag = "Admin";
				colour = "f4ed2e";
			} else if (player.isHeadModeratorRank()) {
				tag = "Head Mod";
				colour = "0096ff";
			}
			else if (player.isDeveloperRank()) {
				tag = "Developer";
				colour = "002aff";
			} 
			else if (player.isModeratorRank()) {
				tag = "Moderator";
				colour = "ebebeb";
			} else if (player.isSupportRank()) {
				tag = "Support";
				colour = "002aff";
			} else if (player.isYoutubeRank()) {
				tag = "Youtube";
				colour = "db1c1c";
			} else if (player.isOmegaDonator()) {
				tag = "Omega";
				colour = "00adbe";
			} else if (player.isLuciferDonator()) {
				tag = "Lucifer";
				colour = "002233";
			} else if (player.isSupremeDonator()) {
				tag = "Supreme";
				colour = "6b0002";
			} else if (player.isImmortalDonator()) {
				tag = "Immortal";
				colour = "002233";
			} else if (player.isUberDonator()) {
				tag = "Uber";
				colour = "0088fe";
			} else if (player.isUltimateDonator()) {
				tag = "Ultimate";
				colour = "d423e9";
			} else if (player.isLegendaryDonator()) {
				tag = "Legendary";
				colour = "fff600";
			} else if (player.isExtremeDonator()) {
				tag = "Extreme";
				colour = "007911";
			} else if (player.isSuperDonator()) {
				tag = "Super";
				colour = "002aff";
			} else if (player.isDonator()) {
				tag = "Donator";
				colour = "d62222";
			}
			if (player.isUberDonator()) {
				tag = player.yellTag;
			}
	
			for (int i = 0; i < ServerConstants.MAXIMUM_PLAYERS; i++) {
				Player loop = PlayerHandler.players[i];
				if (loop != null) {
					String text = "<col=0>[<col=" + colour + ">" + tag + "<col=0>] " + PlayerRank.getIconText(player.playerRights, false) + name + ":<col=800000> " + Misc.optimize(
							message) + "";
					loop.getPA().sendMessage(":packet:yell#&!" + player.playerRights + "#&!" + player.getPlayerName() + "#&!" + text);
				}
			}
		} catch (Exception e) {
	
		}
	}

	public static boolean disableYell;

	public static void toggleYellSystem(Player player) {
		Yell.disableYell = !Yell.disableYell;
		player.getPA().sendMessage("Is yell disabled? " + Yell.disableYell);
	}



	public static void sendYellModeToClient(Player player) {
		player.getPA().sendMessage(":packet:modeyell " + player.yellMode);
	}

	public static String getYellOptionString(Player player, boolean toggle) {
		String nextOption = "ON";
		switch (player.yellMode) {
			case "ON":
				nextOption = "FRIENDS";
				break;
			case "FRIENDS":
				nextOption = "OFF";
				break;
		}
		if (toggle) {
			player.yellMode = nextOption;
			player.hasDialogueOptionOpened = false;
			player.getDH().sendDialogues(264);
			sendYellModeToClient(player);
			return "";
		}
		return "Yell is currently: " + player.yellMode.toLowerCase() + ". Toggle it to " + nextOption.toLowerCase() + ".";
	}



	public static void yellCommand(Player player, String playerCommand) {
		boolean canYell = player.isModeratorRank() || player.isSupportRank() || player.isYoutubeRank();
		if (!canYell) {
			canYell = DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.DONATOR);
		}
		if (canYell) {
			Yell.yell(player, playerCommand);
		}
	}



	public static void setYellTag(Player player, String playerCommand) {
		if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.UBER_DONATOR)) {
			try {
				String title = playerCommand.substring(11);
				if (title.length() > 12) {
					player.getPA().sendMessage("Stop being Josh! 12 characters maximum.");
					return;
				}
				if (Misc.isFlaggedOffensiveName(title)) {
					player.getPA().sendMessage("You have attempted to use a yelltag that is blacklisted.");
					return;
				}
				if (title.toLowerCase().contains("<col=") || title.toLowerCase().contains("<img=")) {
					return;
				}
				player.yellTag = title;
				player.getPA().sendMessage("Your yell tag has been set to '" + player.yellTag + "'.");
			} catch (Exception e) {
			}
		}
	}

}
