package game.content.dialogueold.options;

import core.Plugin;
import game.content.achievement.Achievements;
import game.content.donator.Yell;
import game.content.godbook.BookPreaching;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.AutoBuyBack;
import game.content.miscellaneous.PvpTask;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Runecrafting;
import game.content.tradingpost.TradingPost;
import game.player.Player;
import utility.Misc;

/**
 * Handle actions on a four option dialogue.
 *
 * @author MGT Madness.
 */
public class FourOptions {

	public static void firstOption(Player player) {

		switch (player.getDialogueAction()) {

			case 41:
				BookPreaching.handlePreach(player, 3842, 0);
				break;

			case 40:
				BookPreaching.handlePreach(player, 3840, 0);
				break;

			case 42:
				BookPreaching.handlePreach(player, 3844, 0);
				break;

			// Martin the Master Gardener, Money making via skilling
			case 30:
				player.getDH().sendDialogues(33);
				break;

			// Wise Old Man, Can i change my password?
			case 32:
				player.getDH().sendDialogues(28);
				break;

			// Wilderness bosses, Chaos Elemental (50 Wilderness).
			case 82:
				Teleport.spellTeleport(player, 3307, 3916, 0, false);
				break;


			// Air altar
			case 105:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.AIR)) {
					return;
				}
				break;

			// Mage of Zamorak, Nature altar.
			case 106:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.NATURE)) {
					return;
				}
				break;

			// Void knight, Blood money shop.
			case 209:
				Achievements.checkCompletionSingle(player, 1017);
				player.getShops().openShop(60);
				break;

			// Horvik.
			case 216:
				player.getShops().openShop(3);
				break;

			// Sir Pysin, Iron Man, Skilling.
			case 258:
				player.getShops().openShop(67);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(7);
				break;

			// Pvp task.
			case 265:
				PvpTask.killsLeft(player);
				break;

			// Configure messages, Toggle boss kill counts
			case 264:
				player.bossKillCountMessage = !player.bossKillCountMessage;
				player.hasDialogueOptionOpened = false;
				player.getDH().sendDialogues(264);
				break;

			// Amulet of glory inventory dialogue.
			case 456:
				int x = 3085 + Misc.random(3);
				int y = 3491 + Misc.random(5);
				Teleport.startTeleport(player, x, y, 0, "GLORY " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Buying section, edit amount.
			case 473:
				TradingPost.editAmountDialogueOption(player, "BUYING");
				break;

			// Selling section, edit amount.
			case 474:
				TradingPost.editAmountDialogueOption(player, "SELLING");
				break;

			// Lottery npc/Durial
			case 596:
				Lottery.howDoesTheLotteryWorkDialogueOption(player);
				break;

			// Tradesman
			case 620:
				player.getDH().sendDialogues(621);
				break;

			// Adam, Ironman store
			case 656:
				player.getShops().openShop(15);
				break;

			default:
				Plugin.execute("option_one_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void secondOption(Player player) {


		switch (player.getDialogueAction()) {
			case 41:
				BookPreaching.handlePreach(player, 3842, 2);
				break;

			case 40:
				BookPreaching.handlePreach(player, 3840, 2);
				break;

			case 42:
				BookPreaching.handlePreach(player, 3844, 2);
				break;

			//Martin the Master Gardener, Skilling equipment shop 1
			case 30:
				player.getShops().openShop(5);
				break;

			// Wilderness bosses, King Black Dragon (44 Wilderness) & Ice strykwyrm.
			case 82:
				Teleport.spellTeleport(player, 2980 + Misc.random(2), 3866 + Misc.random(2), 0, false);
				break;

			// Fire altar
			case 105:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.FIRE)) {
					return;
				}
				break;

			// Mage of Zamorak, Law altar.
			case 106:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.LAW)) {
					return;
				}
				break;

			// Martin the Master Gardener, Skilling equipment.
			case 141:
				player.getShops().openShop(5);
				break;

			// Teleporter, Agility.
			case 142:
				player.getDH().sendDialogues(143);
				break;

			case 208:
				player.getShops().openShop(39);
				break;

			// Void Knight, Untradeables
			case 209:
				player.getShops().openShop(46);
				break;


			// Horvik.
			case 216:
				player.getShops().openShop(20);
				break;

			// Sir Pysin, Iron Man, Equipment #1.
			case 258:
				player.getShops().openShop(68);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(5);
				break;

			// Pvp task, obtain task.
			case 265:
				PvpTask.obtainTask(player);
				break;

			// Configure messages, Change loot value notification
			case 264:
				InterfaceAssistant.closeDialogueOnly(player);
				InterfaceAssistant.showAmountInterface(player, "LOOT NOTIFICATION", "Enter amount");
				break;

			// Amulet of glory inventory dialogue.
			case 456:
				int x = 2917 + Misc.random(3);
				int y = 3175 + Misc.random(2);
				Teleport.startTeleport(player, x, y, 0, "GLORY " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Buying section, edit price.
			case 473:
				TradingPost.editPriceDialogueOption(player, "BUYING");
				break;

			// Selling section, edit price.
			case 474:
				TradingPost.editPriceDialogueOption(player, "SELLING");
				break;

			// Lottery npc/Durial
			case 596:
				Lottery.buyLotteryTicketsDialogueOption(player);
				break;

			// Tradesman
			case 620:
				player.getDH().sendDialogues(622);
				break;

			// Adam, Expensive shop
			case 656:
				player.getShops().openShop(12);
				break;

			default:
				Plugin.execute("option_two_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void thirdOption(Player player) {

		switch (player.getDialogueAction()) {

			case 41:
				BookPreaching.handlePreach(player, 3842, 1);
				break;

			case 40:
				BookPreaching.handlePreach(player, 3840, 1);
				break;

			case 42:
				BookPreaching.handlePreach(player, 3844, 1);
				break;

			// Martin the Master Gardener, I want to sell resources
			case 30:
				player.getShops().openShop(16);
				break;


			// Teleporter, Next.
			case 82:
				player.getDH().sendDialogues(69);
				break;

			// Cosmic altar
			case 105:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.COSMIC)) {
					return;
				}
				break;

			// Mage of Zamorak, Death altar.
			case 106:
				if (!Runecrafting.canTeleportToAltar(player, Runecrafting.Runes.DEATH)) {
					return;
				}
				break;

			// Teleporter, Mining & Smithing.
			case 142:
				Teleport.spellTeleport(player, 3023, 9739, 0, true);
				break;

			case 208:
				player.getShops().openShop(40);
				break;
			// Void Knight, Buy back untradeables.
			case 209:
				player.getShops().openShop(11);
				break;

			// Horvik.
			case 216:
				player.getShops().openShop(19);
				break;

			// Iron Man shop.
			case 258:
				player.getShops().openShop(41);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(41);
				break;

			// Pvp task, claim reward.
			case 265:
				PvpTask.claimReward(player);
				break;

			// Configure messages, Toggle profile Pvm and rare drop announcement privacy
			case 264:
				player.profilePrivacyOn = !player.profilePrivacyOn;
				player.hasDialogueOptionOpened = false;
				player.getDH().sendDialogues(264);
				break;

			// Amulet of glory inventory dialogue.
			case 456:
				int x = 3104 + Misc.random(4);
				int y = 3252 + Misc.random(1);
				Teleport.startTeleport(player, x, y, 0, "GLORY " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Buying section, cancel offer.
			case 473:
				TradingPost.cancelOfferButton(player, "BUYING");
				break;

			// Selling section, cancel offer.
			case 474:
				TradingPost.cancelOfferButton(player, "SELLING");
				break;

			// Lottery npc/Durial
			case 596:
				Lottery.currentPotAtDialogueOption(player);
				break;

			// Tradesman
			case 620:
				player.getShops().openShop(8);
				break;

			// Adam, Hats & robe sets shop
			case 656:
				player.getShops().openShop(41);
				break;

			default:
				Plugin.execute("option_three_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void fourthOption(Player player) {
		switch (player.getDialogueAction()) {

			case 41:
				BookPreaching.handlePreach(player, 3842, 3);
				break;

			case 40:
				BookPreaching.handlePreach(player, 3840, 3);
				break;

			case 42:
				BookPreaching.handlePreach(player, 3844, 3);
				break;

			// Wise Old Man, Previous.
			case 32:
				player.getDH().sendDialogues(27);
				break;

			// Teleporter, Previous.
			case 82:
				player.getDH().sendDialogues(103);
				break;

			// Next, on the Runecrafting teleport options
			case 105:
				player.getDH().sendDialogues(106);
				break;

			// Mage of Zamorak, Previous.
			case 106:
				player.getDH().sendDialogues(105);
				break;

			// Teleporter, Previous.
			case 142:
				player.getDH().sendDialogues(161);
				break;

			case 202:
				player.getPA().closeInterfaces(true);
				break;

			case 208:
				player.getPA().closeInterfaces(true);
				break;

			// Void Knight, Vote shop.
			case 209:
				AutoBuyBack.toggleOption(player);
				break;

			// Horvik.
			case 216:
				player.getShops().openShop(4);
				break;

			// Iron Man shop.
			case 258:
				player.getShops().openShop(52);
				break;

			// Shopkeeper at Edgeville.
			case 261:
				player.getShops().openShop(52);
				break;
				
// Configure messages,  Yell settings
			case 264:
				Yell.getYellOptionString(player, true);
				break;

			// Pvp task, ask about rewards.
			case 265:
				PvpTask.whatAreRewards(player);
				break;

			// Amulet of glory inventory dialogue.
			case 456:
				int x = 3290 + Misc.random(6);
				int y = 3167 + Misc.random(1);
				Teleport.startTeleport(player, x, y, 0, "GLORY " + player.itemInventoryOptionId + " " + "INVENTORY");
				break;

			// Buying section, close.
			case 473:
				TradingPost.closeButton(player);
				break;

			// Selling section, close.
			case 474:
				TradingPost.closeButton(player);
				break;

			// Lottery npc/Durial
			case 596:
				Lottery.percentageOfWinningDialogueOption(player);
				break;

			// Tradesman
			case 620:
				player.getShops().openShop(12);
				break;

			// Adam, Cape shop
			case 656:
				player.getShops().openShop(52);
				break;

			default:
				Plugin.execute("option_four_" + player.getDialogueAction(), player);
				break;
		}
	}



}
