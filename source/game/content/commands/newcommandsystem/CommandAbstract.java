package game.content.commands.newcommandsystem;

import game.content.donator.DonatorTokenUse;
import game.player.Player;

public abstract class CommandAbstract {

	public abstract int getRequiredRights();

	public abstract DonatorTokenUse.DonatorRankSpentData getDonatorRankRequired();

	public abstract String[] getName();

	public abstract String matchingType();

	public abstract String getCorrectUsageExample();

	public abstract String getDescription();

	public abstract void executeCommand(Player player, String command);
}
