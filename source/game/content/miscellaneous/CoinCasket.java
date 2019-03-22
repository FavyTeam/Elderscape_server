package game.content.miscellaneous;


import core.Server;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcDrops;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Coin casket item.
 *
 * @author MGT Madness, created on 29-11-2015.
 */
public class CoinCasket {

	/**
	 * Coin casket item feature.
	 *
	 * @param player The associated player.
	 * @param itemId The item identity.
	 * @param slot The slot of the item in the inventory.
	 * @return True if the itemId given matches the Casket id.
	 */
	public static boolean isCoinCasketItemId(Player player, int itemId, int slot) {
		int amount = 0;
		switch (itemId) {
			case 2714:
				amount = Misc.random(NpcDrops.LARGE_CASKET_LOOT - 50_000, NpcDrops.LARGE_CASKET_LOOT + 50_000);
				break;

			case 2717:
				amount = Misc.random(NpcDrops.MEDIUM_CASKET_LOOT - 30_000, NpcDrops.MEDIUM_CASKET_LOOT + 30_000);
				break;

			case 2720:
				amount = Misc.random(NpcDrops.SMALL_CASKET_LOOT - 20_000, NpcDrops.SMALL_CASKET_LOOT + 20_000);
				break;

			case 2724:
				amount = Misc.random(NpcDrops.TINY_CASKET_LOOT - 10_000, NpcDrops.TINY_CASKET_LOOT + 10_000);
				break;
		}

		if (amount > 0) {
			int coinAmount = amount;
			ItemAssistant.addItemToInventory(player, 995, coinAmount, slot, true);
			ItemAssistant.deleteItemFromInventory(player, itemId, 1);
			player.playerAssistant.sendMessage("You receive " + Misc.formatNumber(coinAmount) + " coins from the casket.");
			return true;
		}
		return false;
	}

	/**
	 * Create the coin casket item on the ground depending on npc hp.
	 *
	 * @param player The associated player.
	 * @param npcHp The NPC killed hp.
	 * @param x The NPC's x coordinate.
	 * @param y The NPC's y coordinate.
	 */
	public static void dropCoinCasket(Player player, Npc npc, int npcId, int npcHp, int x, int y) {

		if (!Misc.hasOneOutOf(35)) {
			return;
		}

		int casketId = 0;
		if (npcHp > 140) {
			casketId = 2714;
		} else if (npcHp > 80) {
			casketId = 2717;
		} else {
			casketId = 2720;
		}
		int amount = Area.inDangerousPvpArea(player) ? 2 : 1;
		Server.itemHandler.createGroundItem(player, casketId, x, y, npc.getHeight(), amount, false, 0, true, "", "", "", "", "dropCoinCasket " + npc.name + " " + casketId);
	}

}
