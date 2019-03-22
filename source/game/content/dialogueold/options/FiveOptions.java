package game.content.dialogueold.options;

import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.content.interfaces.donator.DonatorShop;
import game.content.miscellaneous.MonkeyGreeGree;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Slayer;
import game.content.tradingpost.TradingPost;
import game.item.ItemAssistant;
import game.player.Player;

/**
 * Handle actions on a five option dialogue.
 *
 * @author MGT Madness.
 */
public class FiveOptions {

	public static void firstOption(Player player) {

		switch (player.getDialogueAction()) {
			// Max cape.
			case 199:
				player.getPA().closeInterfaces(true);
				if (!player.announceMaxLevel && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 2080 total level to buy this cape.");
					break;
				}
				if (ItemAssistant.getFreeInventorySlots(player) >= 2) {
					int amount = ServerConstants.getMaxCapeCost();
					if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
						ItemAssistant.addItem(player, 13280, 1);
						ItemAssistant.addItem(player, 13281, 1);
						player.getDH().sendItemChat("", "Congratulations, you've claimed a Max cape & hood!", 13280, 200, 14, 0);
					} else {
						player.playerAssistant.sendMessage("You do not have enough " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
					}
				} else {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				break;
			case 622: //Supplies shop
				player.getShops().openShop(7);
				break;
			case 621: //melee shop
				player.getShops().openShop(3);
				break;

			// Vannaka, I need another assignment.
			case 11:
				Slayer.giveTask(player, false);
				break;

			// Horvik, Cape shop #1.
			case 13:
				player.getShops().openShop(52);
				break;

			// Cosmetic shop 1
			case 14:
				player.getShops().openShop(12);
				break;

			// Item Specialist Merchant, Item points shop.
			case 15:
				player.getShops().openShop(28);
				break;

			// Armour shop 1
			case 16:
				player.getShops().openShop(3);
				break;

			// Completionist cape, top.
			case 133:
				player.partOfCape = "TOP";
				player.getPA().displayInterface(14000);
				break;


			// Pikkupstix, Open pet shop.
			case 135:
				player.getShops().openShop(30);
				break;

			// Monkey greegree, Small ninja.
			case 180:
				MonkeyGreeGree.chooseTransformation(player, 1, 1462, 1386, 1380, 1381);
				break;

			// Monkey greegree, Bearded monkey guard.
			case 181:
				MonkeyGreeGree.chooseTransformation(player, 1, 5276, 1401, 1399, 1400);
				break;

			// Guide.
			case 217:
				player.getDH().sendStatement("Type in ::changepassword YourNewPassHere");
				break;

			// Sir Prysin, Achievement shop #1.
			case 219:
				player.getShops().openShop(42);
				break;
			// Ice strykewyrm.
			case 220:
				Teleport.spellTeleport(player, 2980, 3866, 0, false);
				break;

			// Dark crabs.
			case 221:
				Teleport.spellTeleport(player, 3035, 3683, 0, false);
				break;

			case 226:
				String string = "on";
				if (player.teleportWarning) {
					string = "off";
				}
				player.teleportWarning = !player.teleportWarning;
				player.getDH().sendStatement("Wilderness teleport warning has been toggled to " + string + ".");
				break;

			// Donator assistant, Donator shop.
			case 257:
				DonatorShop.openLastDonatorShopTab(player);
				break;

			// King's throne option.
			case 449:
				player.throneId = 13665;
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_one_" + player.getDialogueAction(), player);
				break;

		}

	}

	public static void secondOption(Player player) {

		switch (player.getDialogueAction()) {
			// Purchase Infernal Cape
			case 199:

				player.getPA().closeInterfaces(true);
				if (player.getWildernessKills(true) < 1000 && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 1,000 Wilderness kills to obtain the powerful Infernal cape.");
					int killsLeft = 1000 - player.getWildernessKills(true);
					player.getPA().sendMessage(killsLeft + " kills left!");
					break;
				}
				if (ItemAssistant.getFreeInventorySlots(player) >= 1) {
					int amount = ServerConstants.getInfernalCapeCost();
					if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
						ItemAssistant.addItem(player, 21295, 1);
						player.getDH().sendItemChat("", "Congratulations, you've claimed an Infernal cape!", 21295, 200, 14, 0);
					} else {
						player.playerAssistant.sendMessage("You do not have enough " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
					}
				} else {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				break;

			case 622: //skilling shop
				player.getShops().openShop(5);
				break;
			case 621: //Ranged shop
				player.getShops().openShop(20);
				break;

			// Vannaka, May you spare me an Enchanted gem?
			case 11:
				ItemAssistant.addItem(player, 4155, 1);
				player.getPA().closeInterfaces(true);
				break;

			// Horvik, Cosmetic shop #2.
			case 13:
				player.getShops().openShop(53);
				break;

			// Cosmetic shop 2
			case 14:
				player.getShops().openShop(13);
				break;

			// Item Specialist Merchant, Coins shop. (items added to bank on death).
			case 15:
				player.getShops().openShop(32);
				break;

			// Armour shop 2
			case 16:
				player.getShops().openShop(21);
				break;

			// Completionist cape, top detail.
			case 133:
				player.partOfCape = "TOP DETAIL";
				player.getPA().displayInterface(14000);
				break;

			// Monkey greegree, Large ninja.
			case 180:
				MonkeyGreeGree.chooseTransformation(player, 1, 1463, 1386, 1380, 1381);
				break;

			// Monkey greegree, Blue face monkey guard.
			case 181:
				MonkeyGreeGree.chooseTransformation(player, 1, 1466, 1401, 1399, 1400);
				break;

			// Guide.
			case 217:
				player.profilePrivacyOn = !player.profilePrivacyOn;
				player.getDH().sendDialogues(242);
				break;

			// Sir Prysin, Achievement shop #2.
			case 219:
				player.getShops().openShop(43);
				break;
			// King black dragon.
			case 220:
				Teleport.spellTeleport(player, 3006, 3850, 0, false);
				break;


			// Revenants.
			case 221:
				Teleport.spellTeleport(player, 2978, 3735, 0, false);
				break;

			case 226:
				String string = "on";
				if (player.killScreenshots) {
					string = "off";
				}
				player.killScreenshots = !player.killScreenshots;
				player.getDH().sendStatement("Automatic kill screenshots has been turned " + string + ".");
				break;

			// Donator assistant, Donator tokens trading post.
			case 257:
				TradingPost.displayTradingPost(player);
				break;

			// King's throne option.
			case 449:
				player.throneId = 13666;
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_two_" + player.getDialogueAction(), player);
				break;
		}
	}

	public static void thirdOption(Player player) {

		switch (player.getDialogueAction()) {
			// Purchase Imbued sara cape
			case 199:

				player.getPA().closeInterfaces(true);
				if (player.getHybridKills() < 250 && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 250 Hybrid kills to obtain the Imbued saradomin cape.");
					break;
				}
				if (ItemAssistant.getFreeInventorySlots(player) >= 1) {
					int amount = ServerConstants.getImbuedCapeCost();
					if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
						ItemAssistant.addItem(player, 21791, 1);
						player.getDH().sendItemChat("", "Congratulations, you've claimed an Imbued saradomin cape!", 21791, 200, 14, 0);
					} else {
						player.playerAssistant.sendMessage("You do not have enough " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
					}
				} else {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				break;
			case 622: //Hats & robes shop
				player.getShops().openShop(41);
				break;
			case 621: //Magic shop
				player.getShops().openShop(19);
				break;

			// Vannaka, Upgrade to Slayer helmet (charged).
			case 11:
				if (!ItemAssistant.hasItemAmountInInventory(player, 11864, 1)) {
					player.getDH().sendDialogues(15);
					return;
				}
				player.getDH().sendDialogues(14);
				break;

			// Horvik, Hat shop.
			case 13:
				player.getShops().openShop(41);
				break;

			// Cape shop
			case 14:
				player.getShops().openShop(11);
				break;

			// Item Specialist Merchant, Coins shop.
			case 15:
				player.getShops().openShop(29);
				break;

			//Armour shop 3
			case 16:
				player.getShops().openShop(4);
				break;

			// Completionist cape, bottom.
			case 133:
				player.partOfCape = "BOTTOM";
				player.getPA().displayInterface(14000);
				break;

			// Monkey greegree, Monkey guard.
			case 180:
				MonkeyGreeGree.chooseTransformation(player, 1, 5275, 1401, 1399, 1400);
				break;

			// Monkey greegree, Small zombie.
			case 181:
				MonkeyGreeGree.chooseTransformation(player, 1, 1467, 1386, 1382, 1381);
				break;
			// Guide, Pet shops.
			case 217:
				player.getDH().sendDialogues(218);
				break;

			// Sir Prysin, God page shop or Iron Man shop when an Iron Man opens the dialogue.
			case 219:
				player.getDH().sendDialogues(258);
				break;
			// Chaos elemental.
			case 220:
				Teleport.spellTeleport(player, 3307, 3916, 0, false);
				break;

			// Venenatis.
			case 221:
				Teleport.spellTeleport(player, 3308, 3737, 0, false);
				break;

			// Guide, Change game mode.
			case 226:
				if (GameType.isOsrsEco()) {
					player.getDH().sendDialogues(664);
				} else {
					player.getPA().closeInterfaces(true);
				}
				break;

			// Twiggy O'Korn, Achievement shop #3.
			case 256:
				player.getShops().openShop(63);
				break;

			// Donator assistant, Pet Mystery Box info
			case 257:
				player.getDH().sendDialogues(611);
				break;

			// King's throne option.
			case 449:
				player.throneId = 13667;
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_three_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void fourthOption(Player player) {

		switch (player.getDialogueAction()) {
			// Purchase Imbued zammy cape
			case 199:

				player.getPA().closeInterfaces(true);
				if (player.getHybridKills() < 250 && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 250 Hybrid kills to obtain the Imbued zamorak cape.");
					break;
				}
				if (ItemAssistant.getFreeInventorySlots(player) >= 1) {
					int amount = ServerConstants.getImbuedCapeCost();
					if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
						ItemAssistant.addItem(player, 21795, 1);
						player.getDH().sendItemChat("", "Congratulations, you've claimed an Imbued zamorak cape!", 21795, 200, 14, 0);
					} else {
						player.playerAssistant.sendMessage("You do not have enough " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
					}
				} else {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				break;
			case 622: //capes shop
				player.getShops().openShop(52);
				break;
			case 621: //Misc shop
				player.getShops().openShop(4);
				break;

			// Vannaka, How many slayer points do i have?
			case 11:
				Slayer.giveBossTask(player);
				break;

			// Horvik, Robe shop.
			case 13:
				player.getShops().openShop(12);
				break;

			// Hat shop 1
			case 14:
				player.getShops().openShop(10);
				break;

			// Item Specialist Merchant, Next.
			case 15:
				player.getDH().sendDialogues(177);
				break;

			// Weapon shop
			case 16:
				player.getShops().openShop(22);
				break;

			// Next.
			case 118:
				player.getDH().sendDialogues(119);
				break;

			// Next.
			case 119:
				player.getDH().sendDialogues(120);
				break;

			// Teleporter, Next.
			case 120:
				player.getDH().sendDialogues(121);
				break;

			// Completionist cape, bottom detail.
			case 133:
				player.partOfCape = "BOTTOM DETAIL";
				player.getPA().displayInterface(14000);
				break;

			// Monkey greegree, Next.
			case 180:
				player.getDH().sendDialogues(181);
				break;

			// Monkey greegree, Next.
			case 181:
				player.getDH().sendDialogues(182);
				break;

			case 217:

				player.getPA().toggleBots(true);
				break;

			// Sir Prysin, Gloves.
			case 219:
				player.getShops().openShop(57);
				break;

			// Mage arena.
			case 220:
				Teleport.spellTeleport(player, 3113, 3959, 0, false);
				break;

			// Callisto.
			case 221:
				Teleport.spellTeleport(player, 3202, 3865, 0, false);
				break;

			// Guide, Previous.
			case 226:
				player.xpLock = !player.xpLock;
				String text = player.xpLock ? "on" : "off";
				player.getDH().sendStatement("Experience lock has been turned " + text + ".");
				break;

			// Twiggy O'Korn, Achievement shop #4.
			case 256:
				player.getShops().openShop(64);
				break;

			// Donator assistant, open donation page.
			case 257:
				player.getPA().openWebsite("www.dawntained.com/donate", true);
				player.getPA().closeInterfaces(true);
				break;

			// King's throne option.
			case 449:
				player.throneId = 13668;
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_four_" + player.getDialogueAction(), player);
				break;

		}

	}

	public static void fifthOption(Player player) {

		switch (player.getDialogueAction()) {
			// Purchase Imbued guthix cape
			case 199:

				player.getPA().closeInterfaces(true);
				if (player.getHybridKills() < 250 && !player.isUberDonator() && !player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You need 250 Hybrid kills to obtain the Imbued guthix cape.");
					break;
				}
				if (ItemAssistant.getFreeInventorySlots(player) >= 1) {
					int amount = ServerConstants.getImbuedCapeCost();
					if (ItemAssistant.checkAndDeleteStackableFromInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
						ItemAssistant.addItem(player, 21793, 1);
						player.getDH().sendItemChat("", "Congratulations, you've claimed an Imbued guthix cape!", 21793, 200, 14, 0);
					} else {
						player.playerAssistant.sendMessage("You do not have enough " + ServerConstants.getMainCurrencyName().toLowerCase() + ".");
					}
				} else {
					player.playerAssistant.sendMessage("Not enough inventory space.");
				}
				break;
			case 621: //back
			case 622: //back
				player.getDH().sendDialogues(620);
				break;

			// Vannaka, Er..nothing...
			case 11:
				player.getShops().openShop(46);
				break;

			// Horvik, Previous.
			case 13:
				player.getDH().sendDialogues(216);
				break;

			// Cosmetic Merchant, Next
			case 14:
				player.getDH().sendDialogues(13);
				break;

			// Item Specialist Merchant, Nevermind.
			case 15:
				player.getPA().closeInterfaces(true);
				break;

			// Previous.
			case 16:
				player.getDH().sendDialogues(216);
				break;

			case 22:
				player.getPA().closeInterfaces(true);
				break;

			// Teleporter, Previous.
			case 69:
				player.getDH().sendDialogues(161);
				break;

			// Teleporter, Previous.
			case 82:
				player.getDH().sendDialogues(69);
				break;

			// Teleporter, Next.
			case 110:
				player.getDH().sendDialogues(176);
				break;

			// Teleporter, Next.
			case 111:
				player.getDH().sendDialogues(112);
				break;

			// Teleporter, Previous.
			case 118:
				player.getDH().sendDialogues(161);
				break;

			// Previous.
			case 119:
				player.getDH().sendDialogues(118);
				break;

			// Previous.
			case 120:
				player.getDH().sendDialogues(119);
				break;

			// Previous.
			case 121:
				player.getDH().sendDialogues(120);
				break;

			// Completionist cape, cancel.
			case 133:
				player.getPA().closeInterfaces(true);
				break;

			// Item Specialist Merchant, Nevermind.
			case 177:
				player.getPA().closeInterfaces(true);
				break;

			// Monkey greegree, Nothing.
			case 180:
				player.getPA().closeInterfaces(true);
				break;

			// Monkey greegree, Previous.
			case 181:
				player.getDH().sendDialogues(180);
				break;

			// Guide, Next.
			case 217:
				player.getDH().sendDialogues(226);
				break;

			// Sir Prysin, Close.
			case 219:
				player.getPA().closeInterfaces(true);
				break;

			// Previous.
			case 220:
				player.getDH().sendDialogues(221);
				break;

			case 221:
				player.getDH().sendDialogues(220);
				break;

			// Guide, Previous.
			case 226:
				player.getDH().sendDialogues(217);
				break;

			// Wilderness activities, Previous.
			case 245:
				player.getDH().sendDialogues(221);
				break;

			// Twiggy O'Korn, Back.
			case 256:
				player.getShops().openShop(65);
				break;

			// Donator assistant, Next
			case 257:
				player.getDH().sendDialogues(668);
				break;

			// King's throne option.
			case 449:
				player.throneId = 13671;
				player.getPA().closeInterfaces(true);
				break;

			default:
				Plugin.execute("option_five_" + player.getDialogueAction(), player);
				break;

		}

	}

}
