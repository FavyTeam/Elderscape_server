package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;
import utility.Misc;

public class VotingReward {

	/**
	 * The chance of the rarest item
	 */
	private static final int RAREST = 0_1;

	/**
	 * The chance of a very rare item
	 */

	private static final int VERY_RARE = 3;

	/**
	 * The chance of a rare item
	 */
	private static final int RARE = 15;

	/**
	 * The chance of an uncommon item
	 */
	private static final int UNCOMMON = 75;

	/**
	 * The chance of a common item
	 */
	private static final int COMMON = 100;

	/**
	 * The vote book items
	 */
	private final static int[][] BOOK_LEWT =
			{
					{1163, COMMON, 1}, //rune full helm
					{1185, COMMON, 1}, //rune sq shield
					{1079, COMMON, 1}, //rune legs
					{1201, COMMON, 1}, //rune kite
					{1213, COMMON, 1}, //rune dagger
					{1275, COMMON, 1}, //rune pick
					{10828, COMMON, 1}, //nezzy helm
					{1319, COMMON, 1}, //rune 2h
					{4131, COMMON, 1}, //rune boots
					{4089, COMMON, 1}, //mystic hat
					{4091, COMMON, 1}, //mystic top
					{4093, COMMON, 1}, //mystic bottoms
					{13147, COMMON, 1}, //small xp lamp
					{1149, UNCOMMON, 1}, //dragon med
					{4153, UNCOMMON, 1}, //granite maul
					{3749, UNCOMMON, 1}, //archer helm
					{3751, UNCOMMON, 1}, //berserker helm
					{3753, UNCOMMON, 1}, //warrior helm
					{3755, UNCOMMON, 1}, //farseer helm
					{12829, UNCOMMON, 1}, //spirit shield
					{4587, UNCOMMON, 1}, //dragon scim
					{2528, UNCOMMON, 1}, //mudium xp lamp

					{11840, RARE, 1}, //dragon boots
					{6585, RARE, 1}, //fury
					{6731, RARE, 1}, //seers ring
					{6733, RARE, 1}, //archers ring
					{6735, RARE, 1}, //warriors ring
					{6737, RARE, 1}, //berserker ring
					{6739, RARE, 1}, //Dragon axe
					{12848, RARE, 1}, //granite maul (or)

					{12831, RARE, 1}, //blessed spirit shield

					{13145, RARE, 1}, //large xp lamp

					{12432, VERY_RARE, 1}, //top hat

					{12393, VERY_RARE, 1}, //Royal top
					{12395, VERY_RARE, 1}, //Royal bottoms
					{12397, VERY_RARE, 1}, //crown

					{11770, VERY_RARE, 1}, //seers ring i
					{11771, VERY_RARE, 1}, //archer ring i
					{11772, VERY_RARE, 1}, //warriors ring i
					{11773, VERY_RARE, 1}, //berserker ring i

					{12538, VERY_RARE, 1}, //drag full helm kit
					{12534, VERY_RARE, 1}, //drag chain kit
					{12536, VERY_RARE, 1}, //drag skirt/legs kit
					{12532, VERY_RARE, 1}, //drag sq kit
					{20002, VERY_RARE, 1}, //drag scim kit
					{20143, VERY_RARE, 1}, //drag defender kit

					{20068, VERY_RARE, 1}, //ags kit
					{20071, VERY_RARE, 1}, //bgs kit
					{20074, VERY_RARE, 1}, //sgs kit
					{20077, VERY_RARE, 1}, //zgs kit

					{12526, VERY_RARE, 1}, //fury kit
					{20062, VERY_RARE, 1}, //torture kit
					{20065, VERY_RARE, 1}, //occult kit

					{12528, VERY_RARE, 1}, //dark inf kit
					{12530, VERY_RARE, 1}, //light inf kit

					{12771, VERY_RARE, 1}, //volcanic whip mix
					{12769, VERY_RARE, 1}, //volcanic whip mix

					{4447, VERY_RARE, 1}, //huge xp lamp

					{20784, RAREST, 1}, //dragon claws
					{11802, RAREST, 1}, //ags
					{12825, RAREST, 1}, //arcane
					{12821, RAREST, 1}, //ely
			};


	public static void ClaimReward(Player player, int[][] book, int votebook) {
		if (ItemAssistant.getFreeInventorySlots(player) < 2) {
			player.getDH().sendStatement("You need at least two available inventory slots to do this.");
			return;
		}
		int random = Misc.random(book.length - 1);
		int amount = book[random][2];
		int coins = Misc.random(250000, 350000);
		int bonus = Misc.random(300000, 500000);
		if (ItemAssistant.hasItemInInventory(player, 784)) {
			ItemAssistant.deleteItemFromInventory(player, 784, 1);
			if (Misc.random(1, 10) < book[random][1]) {
				ItemAssistant.addItem(player, book[random][0], amount);
				ItemAssistant.addItem(player, 995, coins);
				player.getPA().sendMessage("<img=18><col=14499e>You receive 1 " + ItemAssistant.getItemName(book[random][0]) + " and " + Misc.formatRunescapeStyle(coins)
				                           + " Coins from your voting reward!</col>");
			} else {
				ItemAssistant.addItem(player, 995, bonus);
				player.getPA().sendMessage("<img=18><col=14499e> You didn't get an item, instead you receive " + Misc.formatRunescapeStyle(bonus) + " Coins!");
			}
		}
	}


	public static boolean HandleReward(Player player, int book) {

		switch (book) {
			case 784:
				ClaimReward(player, BOOK_LEWT, book);
				return true;
		}
		return false;
	}
}
