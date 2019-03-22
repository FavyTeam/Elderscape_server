package game.content.donator;

import core.ServerConstants;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

/**
 * Donator scrolls.
 *
 * @author MGT Madness, created on 19-01-2016.
 */
public class DonatorTokenUse {
	public static enum DonatorRankSpentData {
		DONATOR(150, 3),
		SUPER_DONATOR(500, 4),
		EXTREME_DONATOR(1200, 5),
		LEGENDARY_DONATOR(3000, 6),
		ULTIMATE_DONATOR(6000, 22),
		UBER_DONATOR(12000, 7),
		IMMORTAL_DONATOR(25000, 23),
		SUPREME_DONATOR(50000, 24),
		LUCIFER_DONATOR(100000, 28),
		OMEGA_DONATOR(150000, 36);

		private int tokensRequired;

		public int playerRights;


		private DonatorRankSpentData(int spentRequired, int playerRights) {
			this.tokensRequired = spentRequired;
			this.playerRights = playerRights;
		}

		public int getTokensRequired() {
			return tokensRequired;
		}

		public int getPlayerRights() {
			return playerRights;
		}

	}

	public static String getDonatorRankIcon(DonatorRankSpentData rank) {
		return "<img=" + rank.getPlayerRights() + ">";
	}

	public static String getDonatorRankName(DonatorRankSpentData instance) {
		return Misc.capitalize(instance.name().replaceAll("_", " "));
	}

	public static void upgradeToNextRank(Player player, int amountUsed) {
		String rankBefore = "";
		for (DonatorRankSpentData data : DonatorRankSpentData.values()) {
			if (player.donatorTokensRankUsed >= data.getTokensRequired()) {
				rankBefore = data.toString();
			}
		}
		player.donatorTokensRankUsed += amountUsed;
		player.getPA().sendMessage(ServerConstants.BLUE_COL + "You have spent a total of " + player.donatorTokensRankUsed + " donator tokens!");
		int highestRankIndex = -1;
		// Search for the next rank, then see if player has the amount for next rank, then add to claimed.
		for (DonatorRankSpentData data : DonatorRankSpentData.values()) {
			if (player.donatorTokensRankUsed >= data.getTokensRequired()) {
				highestRankIndex = data.ordinal();
			}
		}
		if (highestRankIndex == -1) {
			return;
		}
		if (highestRankIndex + 3 == player.playerRights) {
			return;
		}
		if (highestRankIndex + 1 <= DonatorRankSpentData.values().length - 1) {
			String nextName = DonatorRankSpentData.values()[highestRankIndex + 1].name();
			nextName = Misc.capitalize(nextName).replace("_", " ");
			player.getPA().sendMessage(ServerConstants.BLUE_COL + "You need x" + Misc.formatNumber(
					(DonatorRankSpentData.values()[highestRankIndex + 1].getTokensRequired() - player.donatorTokensRankUsed)) + " more Donator tokens spent to become "
			                           + Misc.getAorAnWithOutKey(nextName) + " " + "<img=" + DonatorRankSpentData.values()[highestRankIndex + 1].playerRights + ">" + nextName
			                           + ".");
		}
		String rankName = DonatorRankSpentData.values()[highestRankIndex].toString();
		boolean rankUpgraded = !rankName.equals(rankBefore);
		rankName = rankName.replace("_", " ");
		rankName = Misc.capitalize(rankName);
		if (!GameMode.getGameModeContains(player, "IRON MAN") && !player.isModeratorRank() && !player.isSupportRank() && !player.isYoutubeRank()) {
			int playerRights = DonatorRankSpentData.values()[highestRankIndex].getPlayerRights();
			if (DonatorRankSpentData.values()[highestRankIndex].toString().equals("IMMORTAL_DONATOR")) {
				player.yellTag = "Immortal";
			} else if (DonatorRankSpentData.values()[highestRankIndex].toString().equals("SUPREME_DONATOR")) {
				player.yellTag = "Supreme";
			} else if (DonatorRankSpentData.values()[highestRankIndex].toString().equals("LUCIFER_DONATOR")) {
				player.yellTag = "Lucifer";
			} else if (DonatorRankSpentData.values()[highestRankIndex].toString().equals("OMEGA_DONATOR")) {
				player.yellTag = "Omega";
			}
			player.playerRights = playerRights;
			player.setUpdateRequired(true);
			player.setAppearanceUpdateRequired(true);
		}
		if (rankUpgraded) {
			player.getPA().announce(GameMode.getGameModeName(player) + " has been promoted to " + DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.values()[highestRankIndex]) + rankName + "!");
			player.getPA().announce("Become a Donator today for a range of benefits ::donate");
			player.getPA().sendScreenshot(rankName, 2);
			if (rankName.contains("Ultimate")) {
				player.throneId = 13665;
				player.setCustomPetPoints(player.getCustomPetPoints() + 1);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.ULTIMATE_DONATOR) + "You have received a custom pet point! Spend it at the Donator Custom Pet shop.");
			}
			else if (rankName.contains("Uber")) {
				player.throneId = 13665;
				player.setCustomPetPoints(player.getCustomPetPoints() + 1);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.UBER_DONATOR) + "You have received a custom pet point! Spend it at the Donator Custom Pet shop.");
			}
			else if (rankName.contains("Supreme")) {
				ItemAssistant.addItemReward(null, player.getPlayerName(), 14011, 1, false, -1);
			}
			else if (rankName.contains("Lucifer")) {
				player.setCustomPetPoints(player.getCustomPetPoints() + 2);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.LUCIFER_DONATOR) + "You have received 2x custom pet pointa! Spend it at the Donator Custom Pet shop.");
			}
			else if (rankName.contains("Omega")) {
				ItemAssistant.addItemReward(null, player.getPlayerName(), 7677, 1, false, -1);
				player.setCustomPetPoints(player.getCustomPetPoints() + 3);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.OMEGA_DONATOR) + "You have received 3x custom pet pointa! Spend it at the Donator Custom Pet shop.");
			}
			player.gfx0(199);
		}
	}

}
