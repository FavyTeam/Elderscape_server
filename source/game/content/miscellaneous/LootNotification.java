package game.content.miscellaneous;

import core.ServerConstants;
import game.player.Player;

/**
 * Notify the player about the loot.
 *
 * @author MGT Madness, created on 13-02-2017.
 */
public class LootNotification {

	public static void loot(Player player, int itemId, int amount) {
		long worth = (long) ServerConstants.getItemValue(itemId) * (long) amount;
		player.lootThisTick += worth;
	}

	public static void endOfGameTick(Player player) {
		if (player.lootThisTick >= player.valuableLoot) {
			player.getPA().sendMessage(
					ServerConstants.BLUE_COL + "Valuable loot worth x" + ServerConstants.getFormattedNumberString(player.lootThisTick) + " " + ServerConstants.getMainCurrencyName()
					                                                                                                                                          .toLowerCase() + ".");
		}

	}

}
