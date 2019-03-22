package game.content.commands.newcommandsystem;

import core.ServerConstants;
import game.content.commands.newcommandsystem.impl.CommandBank;
import game.content.donator.DonatorContent;
import game.content.miscellaneous.PlayerRank;
import game.player.Player;

import java.util.ArrayList;

public class CommandHandler {

	public final CommandAbstract[] toLoad = {
			new CommandBank()
	};

	/**
	 * All command data are stored in this arraylist
	 */
	public static ArrayList<CommandAbstract> loadedCommands = new ArrayList<CommandAbstract>();

	public void loadCommands() {
		for (CommandAbstract c : toLoad) {
			loadedCommands.add(c);
		}
	}

	public boolean executeCommand(Player player, String playerCommand) {
		boolean available = false;
		CommandAbstract command = null;
		int commandNameIndex = 0;
		for (int index = 0; index < loadedCommands.size(); index++) {
			command = loadedCommands.get(index);
			if (command.matchingType().equals("EQUALS")) {
				for (commandNameIndex = 0; commandNameIndex < command.getName().length; commandNameIndex++) {
					if (playerCommand.equalsIgnoreCase(command.getName()[commandNameIndex])) {
						available = true;
						break;
					}
				}
				if (available) {
					break;
				}
			} else {
				for (int i = 0; i < command.getName().length; i++) {
					if (playerCommand.toLowerCase().startsWith(command.getName()[i])) {
						available = true;
						break;
					}
				}
				if (available) {
					break;
				}
			}
		}
		if (available) {
			// The only time player rights is used for powers, is if its 1 for mod or 2 for admin.
			if (command.getRequiredRights() >= 1 && command.getRequiredRights() <= 2 && (player.playerRights == 0 || player.playerRights > 2)) {
				player.getPA().sendMessage(
						"You need to be a " + PlayerRank.getIconText(command.getRequiredRights(), false) + ServerConstants.RANK_NAMES[command.getRequiredRights()] + "to use the '"
						+ command.getName()[commandNameIndex] + "' command.");
				return true;
			}
			if (command.getDonatorRankRequired() != null) {
				if (player.donatorTokensRankUsed < command.getDonatorRankRequired().getTokensRequired()) {
					DonatorContent.canUseFeature(player, command.getDonatorRankRequired());
					return true;
				}
			}
			command.executeCommand(player, playerCommand);
			return true;
		}

		return false;
	}

}
