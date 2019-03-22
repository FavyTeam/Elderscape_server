package game.content.dialogueold.options;

import core.Plugin;
import game.content.achievement.Achievements;
import game.content.commands.NormalCommand;
import game.content.donator.DonationsNeeded;
import game.content.miscellaneous.MagicCapeSpellbookSwap;
import game.content.miscellaneous.MonkeyGreeGree;
import game.content.miscellaneous.SpellBook;
import game.content.miscellaneous.Teleport;
import game.content.quicksetup.Presets;
import game.player.Player;
import utility.Misc;

/**
 * Handle actions on a three option dialogue.
 *
 * @author MGT Madness.
 */
public class ThreeOptions {

	public static void firstOption(Player player) {

		switch (player.getDialogueAction()) {

			// Glove shop.
			case 13:
				player.getShops().openShop(17);
				break;

			// Supplies Shop.
			case 17:
				player.getShops().openShop(1);
				break;

			// Range Supplies.
			case 19:
				player.getShops().openShop(2);
				break;

			// Altar, Modern spellbook.
			case 22:
				SpellBook.switchToModern(player);
				break;

			// Martin the Master Gardener, yes to open the resources shop
			case 35:
				player.getShops().openShop(16);
				break;

			// Teleporter, Gnome course.
			case 143:
				Teleport.spellTeleport(player, 2469, 3436, 0, true);
				break;

			// Wise Old Man, What is a Train combat account?
			case 147:
				player.getDH().sendDialogues(148);
				break;


			// TzHaar-Mej-Jal, Information about the Fire cape.
			case 166:
				player.getDH().sendDialogues(167);
				break;

			// Item Specialist Merchant, Ask about Nomad.
			case 177:
				player.getDH().sendDialogues(117);
				break;

			// Monkey greegree, Large monkey.
			case 182:
				MonkeyGreeGree.chooseTransformation(player, 1, 1468, 1386, 1382, 1381);
				break;

			// Gnome.
			case 192:
				player.getDH().sendDialogues(193);
				break;

			// Guide, open tier 3 pet shop.
			case 218:
				player.getShops().openShop(30);
				break;

			// Guide, Throne.
			case 259:
				player.throneId = 1097;
				player.getPA().closeInterfaces(true);
				break;

			case 260:
				Achievements.claimReward(player);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(7);
				break;

			// Custom presets dialogue.
			case 262:
				Presets.equipPreset(player);
				break;

			// Party pete, ask about clue casket (1st release) loot
			case 459:
				player.getDH().sendDialogues(454);
				player.getPA().openWebsite("www.dawntained.com/forum/topic/575-clue-scroll-rewards-100-new-items-31-05-2017", true);
				break;

			case 550:
				Teleport.startTeleport(player, 3300, 3302, 0, "ARCEUUS"); //mining, al kharid
				break;
			case 551:
				Teleport.startTeleport(player, 3186, 3425, 0, "ARCEUUS"); //smithing, varrock
				break;
			case 552:
				Teleport.startTeleport(player, 3090, 3225, 0, "ARCEUUS"); //fishing, draynor
				break;
			case 553:
				Teleport.startTeleport(player, 3274, 3181, 0, "ARCEUUS"); //cooking, al kharid
				break;
			case 554:
				Teleport.startTeleport(player, 2722, 3485, 0, "ARCEUUS"); //woodcutting, seers
				break;
			case 555:
				Teleport.startTeleport(player, 2477, 3438, 0, "ARCEUUS"); //agility, gnome
				break;
			case 556:
				Teleport.startTeleport(player, 3096, 3503, 0, "ARCEUUS"); //thieving, stalls
				break;

			// Burning amulet inventory dialogue. bandit camp
			case 640:
				int x = 3041 + Misc.random(1);
				int y = 3649 + Misc.random(1);
				Teleport.startTeleport(player, x, y, 0, "BURNING_AMULET " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Donator assistant, Afk Throne
			case 668:
				if (!player.isLegendaryDonator()) {
					DonationsNeeded.getLegendaryDonatorMessage(player);
					player.getPA().closeInterfaces(true);
					return;
				}
				player.getDH().sendDialogues(448);
				break;

			// Switch to x spellbook, magic cape feature.
			case 720:
				MagicCapeSpellbookSwap.operateMagicCapeOptionUsed(player, 0);
				break;

			default:
				Plugin.execute("option_one_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void secondOption(Player player) {

		switch (player.getDialogueAction()) {

			// Hat shop 2
			case 13:
				player.getShops().openShop(26);
				break;

			// Magician Armour Shop.
			case 17:
				player.getShops().openShop(19);
				break;

			// Range Armour Shop.
			case 19:
				player.getShops().openShop(20);
				break;

			// Altar, Ancient magicks.
			case 22:
				SpellBook.switchToAncients(player);
				break;

			// Martin the Master Gardener, No option to open the resources shop
			case 35:
				player.getPA().closeInterfaces(true);
				break;

			// Boss teleports
			case 101:
				player.getDH().sendDialogues(103);
				break;

			// Teleporter, Barbarian course.
			case 143:
				Teleport.spellTeleport(player, 2552, 3563, 0, true);
				break;

			// Wise Old Man, What is a Set Combat account?
			case 147:
				player.getDH().sendDialogues(150);
				break;

			// Item Specialist Merchant, Vote points shop.
			case 177:
				player.getShops().openShop(33);
				break;

			// Monkey greegree, Karamja monkey.
			case 182:
				MonkeyGreeGree.chooseTransformation(player, 1, 1469, 222, 219, 220);
				break;

			// Gnome, Open agility point shop.
			case 192:
				player.getShops().openShop(35);
				break;

			// Guide, open tier 2 pet shop.
			case 218:
				player.getShops().openShop(27);
				break;

			// Do not change game mode.
			case 252:
				player.getPA().closeInterfaces(true);
				break;

			// Guide, Throne.
			case 259:
				player.throneId = 1098;
				player.getPA().closeInterfaces(true);
				break;

			// Twiggy, Community event shop.
			case 260:
				player.getShops().openShop(73);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(41);
				break;

			// Custom presets dialogue.
			case 262:
				Presets.update(player, true);
				break;
			// Party pete, ask about clue casket (2nd release) loot
			case 459:
				player.getDH().sendDialogues(458);
				player.getPA().openWebsite("www.dawntained.com/forum/topic/679-new-clue-casket-second-release-donator-pking-loot-buff-more-16-06-2017", true);
				break;

			case 550:
				Teleport.startTeleport(player, 3179, 3368, 0, "ARCEUUS"); //mining, varrock west
				break;
			case 551:
				Teleport.startTeleport(player, 2973, 3372, 0, "ARCEUUS"); //smithing, falador
				break;
			case 552:
				Teleport.startTeleport(player, 2599, 3420, 0, "ARCEUUS"); //fishing, fishing guild
				break;
			case 553:
				Teleport.startTeleport(player, 2816, 3441, 0, "ARCEUUS"); //cooking, catherby
				break;
			case 554:
				Teleport.startTeleport(player, 1625, 3504, 0, "ARCEUUS"); //woodcutting, wc guild
				break;
			case 555:
				Teleport.startTeleport(player, 2552, 3562, 0, "ARCEUUS"); //agility, barbarian
				break;
			case 556:
				Teleport.startTeleport(player, 2662, 3306, 0, "ARCEUUS"); //thieving, ardy
				break;

			// Burning amulet inventory dialogue. lava maze
			case 640:
				int x = 3029 + Misc.random(2);
				int y = 3840 + Misc.random(2);
				Teleport.startTeleport(player, x, y, 0, "BURNING_AMULET " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Donator assistant, Custom Donator Pet store
			case 668:
				player.getShops().openShop(16);
				break;

			// Switch to x spellbook, magic cape feature.
			case 720:
				MagicCapeSpellbookSwap.operateMagicCapeOptionUsed(player, 1);
				break;

			default:
				Plugin.execute("option_two_" + player.getDialogueAction(), player);
				break;

		}

	}

	public static void thirdOption(Player player) {

		switch (player.getDialogueAction()) {

			// Boot shop
			case 13:
				player.getShops().openShop(15);
				break;

			case 17:
				player.getDH().sendDialogues(216);
				break;

			case 19:
				player.getDH().sendDialogues(216);
				break;

			// Altar, Lunar spellbook.
			case 22:
				SpellBook.switchToLunar(player);
				break;

			// Martin the Master Gardener, Ask about something else option
			case 35:
				player.getDH().sendDialogues(30);
				break;

			// Bartering
			case 48:
				player.getDH().sendDialogues(51);
				break;

			// How can i donate?
			case 112:
				player.getDH().sendDialogues(113);
				break;

			// Teleporter, Previous.
			case 143:
				player.getDH().sendDialogues(142);
				break;

			// Wise Old Man, Proceed to account selection.
			case 147:
				player.getDH().sendDialogues(151);
				break;

			// Wise Old Man, Go back.
			case 151:
				player.getDH().sendDialogues(147);
				break;

			// TzHaar-Mej-Jal, Nothing.
			case 166:
				player.getPA().closeInterfaces(true);
				break;

			// Item Specialist Merchant, Previous.
			case 177:
				player.getDH().sendDialogues(15);
				break;

			// Monkey greegree, Previous.
			case 182:
				player.getDH().sendDialogues(181);
				break;

			// Gnome, I'll get back to training.
			case 192:
				player.getPA().closeInterfaces(true);
				break;

			// Guide, close.
			case 218:
				player.getPA().closeInterfaces(true);
				break;

			// Previous
			case 242:
				player.getDH().sendDialogues(226);
				break;

			// Guide, Throne.
			case 259:
				player.throneId = 1099;
				player.getPA().closeInterfaces(true);
				break;

			// Twiggy, Open www.dawntained.com/event
			case 260:
				player.getPA().openWebsite("www.dawntained.com/event", true);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(52);
				break;

			// Custom presets dialogue.
			case 262:
				Presets.Rename(player);
				break;

			// Party pete, ask about clue casket (3rd release) loot
			case 459:
				player.getDH().sendDialogues(623);
				player.getPA().openWebsite("www.dawntained.com/forum/topic/1809-clue-casket-3rd-release-ankou-set-3rd-age-cloak-3rd-age-axe-11-10-2017", true);
				break;

			case 550:
				Teleport.startTeleport(player, 3148, 3149, 0, "ARCEUUS"); //mining, west lumbridge
				break;
			case 551:
				Teleport.startTeleport(player, 3107, 3499, 0, "ARCEUUS"); //smithing, edge
				break;
			case 552:
				Teleport.startTeleport(player, 2876, 3336, 0, "ARCEUUS"); //fishing, entrana
				break;
			case 553:
				Teleport.startTeleport(player, 2858, 3336, 0, "ARCEUUS"); //cooking, entrana
				break;
			case 554:
				Teleport.startTeleport(player, 2818, 3084, 0, "ARCEUUS"); //woodcutting, tai bwo
				break;
			case 555:
				NormalCommand.warningTeleport(player, 2998, 3915, 0); // Agility, Wilderness course
				break;
			case 556:
				Teleport.startTeleport(player, 2332, 3171, 0, "ARCEUUS"); //thieving, elves
				break;
			// Burning amulet inventory dialogue. lava maze
			case 640:
				int x = 3233 + Misc.random(2);
				int y = 3635 + Misc.random(2);
				Teleport.startTeleport(player, x, y, 0, "BURNING_AMULET " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Donator assistant, Custom Donator Pet store
			case 668:
				player.getPA().openWebsite("www.dawntained.com/forum/topic/2346-dawntained", true);
				break;

			// Operate magic cape, cancel option.
			case 720:
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_three_" + player.getDialogueAction(), player);
				break;

		}
	}

}
