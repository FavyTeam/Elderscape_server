package game.content.commands.newcommandsystem.impl;

import game.content.commands.newcommandsystem.CommandAbstract;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.player.Player;

public class CommandFormat extends CommandAbstract {
	@Override
	public int getRequiredRights() {
		return 0;
	}

	@Override
	public DonatorRankSpentData getDonatorRankRequired() {
		return null;
	}

	@Override
	public String[] getName() {
		return new String[]
				       {"format"};
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
		player.getPA().sendMessage("String");

	}

}
