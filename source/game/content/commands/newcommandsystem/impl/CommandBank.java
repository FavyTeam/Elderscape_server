package game.content.commands.newcommandsystem.impl;

import game.content.bank.Bank;
import game.content.commands.newcommandsystem.CommandAbstract;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.skilling.Runecrafting;
import game.player.Area;
import game.player.Player;

public class CommandBank extends CommandAbstract {
	@Override
	public int getRequiredRights() {
		return 0;
	}

	@Override
	public DonatorRankSpentData getDonatorRankRequired() {
		return DonatorRankSpentData.ULTIMATE_DONATOR;
	}

	@Override
	public String[] getName() {
		return new String[]
				       {"bank", "bonk"};
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
		if (!player.isAdministratorRank()) {
			if (Area.inDangerousPvpArea(player)) {
				player.getPA().sendMessage("You cannot bank in the wilderness.");
				return;
			}
			if (!Bank.hasBankingRequirements(player, true)) {
				player.getPA().sendMessage("You cannot do this now.");
				return;
			}
			if (player.getHeight() == 20) {
				player.getPA().sendMessage("You cannot do this now.");
				return;
			}
		}
		if (player.isJailed()) {
			return;
		}
		if (Runecrafting.isRunecraftingDonatorAbuseFlagged(player, true)) {
			return;
		}
		player.getPA().stopAllActions();
		Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
		player.getPA().sendMessage("You have opened the bank.");

	}

}
