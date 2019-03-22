package game.content.donator;

import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.player.Player;


/**
 * Refill Hp, Prayer, Special and cure poison.
 *
 * @author MGT Madness, created on 16-09-2016, in the 12 hour flight from Malaysia to Egypt.
 */
public class DonationsNeeded {

	public static void getDonatorMessage(Player player) {
		player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.DONATOR) + "This is for Donators, help fund the server at ::donate");
	}

	public static void getLegendaryDonatorMessage(Player player) {
		player.getPA().sendMessage("<img=6>This is for Legendary Donators, help fund the server at ::donate");
	}
}
