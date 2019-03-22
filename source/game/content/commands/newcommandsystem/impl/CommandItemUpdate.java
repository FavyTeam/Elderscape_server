package game.content.commands.newcommandsystem.impl;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.commands.newcommandsystem.CommandAbstract;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.player.Player;

public class CommandItemUpdate extends CommandAbstract {
	@Override
	public int getRequiredRights() {
		return ServerConstants.ADMINISTRATOR;
	}

	@Override
	public DonatorRankSpentData getDonatorRankRequired() {
		return null;
	}

	@Override
	public String[] getName() {
		return new String[]
				       {"itemupdate"};
	}

	@Override
	public String matchingType() {
		return "EQUALS";
	}

	@Override
	public String getCorrectUsageExample() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void executeCommand(Player player, String command) {
		ServerConfiguration.FORCE_ITEM_UPDATE = !ServerConfiguration.FORCE_ITEM_UPDATE;
		player.getPA().sendMessage("Item update every game tick is: " + ServerConfiguration.FORCE_ITEM_UPDATE);

	}

}
