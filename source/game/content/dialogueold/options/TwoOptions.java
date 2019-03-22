package game.content.dialogueold.options;

import core.Plugin;
import core.ServerConstants;
import game.content.commands.AdministratorCommand;
import game.content.donator.DonatorContent;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.ItemCombining;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.RandomEvent;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.TradeAndDuel;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.quicksetup.Presets;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.starter.GameMode;
import game.content.starter.NewPlayerContent;
import game.content.tradingpost.TradingPost;
import game.content.worldevent.BloodKey;
import game.item.ItemAssistant;
import game.item.PotionCombining;
import game.item.UntradeableRepairing;
import game.npc.impl.KrakenCombat;
import game.player.Player;
import game.player.punishment.DuelArenaBan;
import java.io.File;
import utility.HackLogHistory;
import utility.Misc;

/**
 * Handle actions on a two option dialogue.
 *
 * @author MGT Madness.
 */
public class TwoOptions {

	public static void firstOption(Player player) {

		switch (player.getDialogueAction()) {
			case 701: // Combine ornament items
				ItemCombining.combine(player);
				break;

			case 700: // Dismantle ornament items
				ItemCombining.dismantle(player);
				break;

			// Untradeable repairing
			case 655:
				UntradeableRepairing.FixItem(player);
				break;
			// Duel arena anti-scam, turn on anti-scam
			case 653:
				if (!player.duelRule[TradeAndDuel.ANTI_SCAM]) {
					player.getTradeAndDuel().toggleAntiScamManually();
				}
				InterfaceAssistant.closeDialogueOnly(player);
				break;

			// Blowpipe dismantle 'yes' option
			case 646:
				Blowpipe.dismantle(player);
				break;
			// Cave kraken, Public room
			case 644:
				player.lastTeleport = 2280 + " " + 10022 + " " + 0;
				Teleport.spellTeleport(player, 2280, 10022, 0, false);
				break;

			// A'abla, ban me from gamblnig.
			case 642:
				if (DuelArenaBan.duelArenaBanApply(player.getPlayerName(), player.addressIp, player.addressUid, 7)) {
					player.getDH().sendDialogues(643);
				} else {
					DuelArenaBan.isDuelBanned(player, false);
					player.getPA().sendMessage("You may ask for another ban 48 hours before it expires.");
					player.getPA().closeInterfaces(true);
				}
				break;
			case 638:
				if (!player.toggleLootKey) {
					ItemAssistant.deleteItemFromInventory(player, 16145, 1);
					player.toggleLootKey = true;
					player.getDH().sendItemChat("", "You will now receive a loot key instead of PvP loot.", 16140, 200, 0, 0);
				} else {
					ItemAssistant.deleteItemFromInventory(player, 16145, 1);
					player.toggleLootKey = false;
					player.getDH().sendItemChat("", "You will now receive regular loot instead of a loot key.", 16140, 200, 0, 0);
				}
				break;
			case 636:
				player.smashVials = true;
				player.getDH().sendItemChat("", "You have toggled the ability to smash vials @blu@ON@bla@.", 229, 200, 15, 0);
				break;

			case 631:
				ItemAssistant.deleteItemFromInventory(player, 21079, 1);
				player.auguryUnlocked = true;
				RegularPrayer.updateRigourAndAugury(player);
				player.getDH().sendItemChat("", "You study the scroll and learn a new prayer: @dre@Augury", 21079, 150, 10, 0);
				break;

			case 634:
				ItemAssistant.deleteItemFromInventory(player, 21034, 1);
				player.rigourUnlocked = true;
				RegularPrayer.updateRigourAndAugury(player);
				player.getDH().sendItemChat("", "You study the scroll and learn a new prayer: @dre@Rigour", 21034, 150, 10, 0);
				break;

			case 616:
				PotionCombining.decantAllPotions(player);
				player.getDH().sendDialogues(617);
				break;
			case 601: //bone un-noting
				player.getDH().sendDialogues(602);
				break;

			case 590: //go down lumbridge stairs
				if (player.getX() == 3205 && player.getY() == 3209) {
					player.getPA().movePlayer(3205, 3209, 2);
					player.getPA().closeInterfaces(true);
				}
				if (player.getX() == 3205 && player.getY() == 3228) {
					player.getPA().movePlayer(3205, 3228, 2);
					player.getPA().closeInterfaces(true);
				}
				//warriors guild
				player.getPA().movePlayer(2840, 3539, 2);
				player.getPA().closeInterfaces(true);
				break;

			case 501:
				if (player.getX() == 3056 && player.getY() == 9562) {
					player.getPA().movePlayer(3056, 9555, 0);
					player.getPA().closeInterfaces(true);
				}
				break;

			// Yes to reset kdr
			case 491:
				int amount = ServerConstants.getResetKdrCost();
				if (!ItemAssistant.hasItemAmountInInventory(player, ServerConstants.getMainCurrencyId(), amount)) {
					player.getDH().sendStatement(
							"You need @blu@" + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + "@bla@ to reset your KDR.");
					return;
				}
				ItemAssistant.deleteItemFromInventory(player, ServerConstants.getMainCurrencyId(), amount);
				PlayerMiscContent.resetKdr(player);
				player.getDH().sendItemChat("Resetting KDR", "You have reset your KDR back to", "0 kills and deaths.", "", 964, 200, 10, 0);
				break;

			// Vannaka, Yes option to upgrade slayer helmet.
			case 21:
				amount = Slayer.getSlayerHelmUpgradeCost();
				if (!ItemAssistant.hasItemAmountInInventory(player, ServerConstants.getMainCurrencyId(), amount)) {

					player.playerAssistant
							.sendMessage("You need " + Misc.formatRunescapeStyle(amount) + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " to buy this.");
					player.getPA().closeInterfaces(true);
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, 11864)) {
					player.playerAssistant.sendMessage("You need the Slayer helmet in your inventory in order to upgrade.");
					player.getPA().closeInterfaces(true);
					return;
				}

				ItemAssistant.deleteItemFromInventory(player, ServerConstants.getMainCurrencyId(), amount);
				ItemAssistant.deleteItemFromInventory(player, 11864, 1);
				ItemAssistant.addItem(player, 11865, 1);
				player.getPA().closeInterfaces(true);
				break;

			// Bob.
			case 2:
				BarrowsRepair.repair(player);
				break;

			// Thessalia, Yes please.
			case 25:
				player.getDH().sendDialogues(290);
				break;

			// Teleporter, Callisto.
			case 101:
				Teleport.spellTeleport(player, 3202, 3865, 0, false);
				break;

			// Teleporter, Previous.
			case 112:
				Teleport.spellTeleport(player, 1900, 5346, player.getPlayerId() * 4 + 2, true);
				break;

			// Delete bank pin.
			case 116:
				player.getPA().closeInterfaces(true);
				if (player.hasEnteredPin) {
					player.setPin = false;
					player.bankPin = "";
					player.fullPin = "";
					player.enteredPin = "";
					player.playerAssistant.sendMessage("Your bank pin has been deleted.");
				} else if (!player.hasEnteredPin) {
					player.getPA().closeInterfaces(true);
					player.playerAssistant.sendMessage("You need to enter your bank pin first.");
				}
				break;

			// Barrows, You have found a hidden tunnel, Yes i'm fearless!
			case 164:
				player.getPA().movePlayer(3551, 9689, 0);
				player.getPA().closeInterfaces(true);
				break;

			// Christmas cracker, That's okay; I might get a party hat!
			case 183:
				player.getPA().closeInterfaces(true);
				if (!ItemAssistant.hasItemInInventory(player, 962)) {
					return;
				}
				player.gfx0(199);
				ItemAssistant.deleteItemFromInventory(player, 962, 1);
				int random = Misc.random(94);
				int itemId = 0;
				if (random < 20) {
					itemId = 1038;
				} else if (random < 40) {
					itemId = 1040;
				} else if (random < 58) {
					itemId = 1048;
				} else if (random < 75) {
					itemId = 1044;
				} else if (random < 85) {
					itemId = 1042;
				} else if (random < 90) {
					itemId = 1046;
				} else if (random < 95) {
					itemId = 11862;
				}
				ItemAssistant.addItem(player, itemId, 1);
				player.playerAssistant.announce(GameMode.getGameModeName(player) + " has received a " + ItemAssistant.getItemName(itemId) + " from a Christmas cracker!");
				player.getPA().sendScreenshot(ItemAssistant.getItemName(itemId), 2);
				break;

			// Vannaka, Yes.
			case 203:
				Slayer.resetTask(player);
				break;

			// Gandai, Yes.
			case 223:
				if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 500000)) {
					player.getDH().sendDialogues(224);
					return;
				}
				player.wildernessAgilityCourseImmunity = System.currentTimeMillis();
				player.getPA().closeInterfaces(true);
				break;
			// Gnome, Let me check your store.
			case 192:
				//player.getShops().openShop(49);
				player.getDH().sendDialogues(1921);
				break;

			// Skill cape master, May i buy a normal Skill cape.
			case 229:
				if (player.baseSkillLevel[player.skillCapeMasterSkill] == 99) {
					player.getDH().sendDialogues(230);
				} else {
					player.getDH().sendDialogues(233);
				}
				break;

			// I'm afraid that's too much money for me.
			case 231:
				player.getPA().closeInterfaces(true);
				break;

			// I'm afraid that's too much money for me.
			case 236:
				player.getPA().closeInterfaces(true);
				break;

			// Yes, i am very nimble and agile!
			case 239:
				player.getPA().movePlayer(player.getX() >= 2878 ? 2876 : 2880, 2952, 0);
				player.getPA().closeInterfaces(true);
				break;

			// Prayer xp lamp.
			case 249:
				ItemAssistant.deleteItemFromInventory(player, 2528, 1);
				Skilling.addSkillExperience(player, 4109, ServerConstants.PRAYER, false);
				player.playerAssistant.sendMessage("You rub the prayer lamp and gain experience.");
				break;

			// Twiggy O'Korn, Achievement shop #1
			case 256:
				player.getShops().openShop(61);
				break;

			// Custom presets yes/no dialogue.
			case 263:
				Presets.update(player, false);
				break;

			case 267:
				BloodKey.confirmPickUpBloodKey(player);
				break;

			case 472:
				BloodKey.confirmPickUpBloodKeyBoss(player);
				break;

			// Achievements shop, Sir pysin.
			case 269:
				player.getShops().openShop(42);
				break;

			// Tutorial, Sure thing!
			case 272:
				player.getDH().sendDialogues(273);
				break;

			// Tutorial, Sure thing! (eco)
			case 562:
				player.getDH().sendDialogues(563);
				break;

			case 285:
				MithrilSeeds.pickUpPlant(player);
				break;

			// Yes delete inventory
			case 286:
				AdministratorCommand.empty(player);
				player.getPA().closeInterfaces(true);
				break;

			// King's Thrones.
			case 448:
				if (!player.isUltimateDonator()) {
					player.getPA().sendMessage("<img=22>This feature is exclusive to Ultimate Donators.");
					player.getPA().closeInterfaces(true);
					return;
				}
				player.getDH().sendDialogues(449);
				break;

			// Yes, teleport me to the wilderness
			case 457:
				if (player.teleportWarningX == 0 || player.teleportWarningY == 0) {
					return;
				}
				Teleport.spellTeleport(player, player.teleportWarningX, player.teleportWarningY, player.teleportWarningHeight, false);
				player.getPA().closeInterfaces(true);
				break;

			// Trading post, cancel button.
			case 475:
				TradingPost.cancelOfferButton(player, "BUYING");
				break;

			// Trading post, cancel button.
			case 476:
				TradingPost.cancelOfferButton(player, "SELLING");
				break;

			// Teleport to: immortal donator
			case 477:
				DonatorContent.teleportToImmortalDonator(player);
				break;

			// Ask about Mystery Boxes
			case 611:
				player.getPA().openWebsite("www.dawntained.com/forum/topic/1010-storepayment-faq/#comment-6874", true);
				player.getDH().sendDialogues(257);
				break;

			// No, do not delete account.
			case 666:
				player.getPA().closeInterfaces(true);
				break;

			// Yes, delete account
			case 667:
				if (!HackLogHistory.isOriginalOwner(player.getPlayerName(), player.addressIp, player.addressUid)) {
					player.getDH().sendStatement("You cannot delete the account unless you are the original owner.");
					return;
				}
				File file = new File(ServerConstants.getCharacterLocation() + player.getPlayerName().toLowerCase() + ".txt");
				if (!file.delete()) {
					player.getDH().sendStatement("Account deletion failed, please try later or after logging out and in.");
					return;
				}
				player.doNotSaveCharacterFile = true;
				player.getPA().forceToLogInScreen();
				player.setDisconnected(true, "account delete");
				player.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
				break;

			// Npc random event talk, yes option
			case 719:
				RandomEvent.randomEventNpcDialogueOptionClicked(player);
				break;

			default:
				Plugin.execute("option_one_" + player.getDialogueAction(), player);
				break;
		}

	}

	public static void secondOption(Player player) {
		switch (player.getDialogueAction()) {
			// Untradeable repairing
			case 655:
				UntradeableRepairing.displayRepairableItemInterface(player);
				break;
			// Duel arena anti-scam, continue with anti-scam turned off
			case 653:
				player.getTradeAndDuel().duelArenaAcceptFirstScreen(true);
				InterfaceAssistant.closeDialogueOnly(player);
				break;
			// Cave kraken, Instanced room
			case 644:
				KrakenCombat.instancedRoom(player);
				break;

			case 636:
				player.smashVials = false;
				player.getDH().sendItemChat("", "You have toggled the ability to smash vials @blu@OFF@bla@.", 229, 200, 15, 0);
				break;
			case 700: // No option for item dismantling
			case 701: // No option for item combining
			case 646: // Blowpipe dismantle 'no' option
			case 616:
			case 631:
			case 634:
			case 638:
			case 642: // A'abla No.
				player.getPA().closeInterfaces(true);
				break;
			case 601: //bone un-noting
				player.getDH().sendDialogues(605);
				break;

			case 590: //go down lumbridge stairs
				if (player.getX() == 3205 && player.getY() == 3209) {
					player.getPA().movePlayer(3205, 3209, 0);
					player.getPA().closeInterfaces(true);
				}
				if (player.getX() == 3205 && player.getY() == 3228) {
					player.getPA().movePlayer(3205, 3228, 0);
					player.getPA().closeInterfaces(true);
				}
				//warriors guild
				player.getPA().movePlayer(2841, 3538, 0);
				player.getPA().closeInterfaces(true);

				break;

			case 501: //wyvern dialogue no
			case 491: //kdr reset no option
				player.getPA().closeInterfaces(true);
				break;

			// Bob, no
			case 2:
				player.getPA().closeInterfaces(true);
				break;

			// Barrow's repair-man, No
			case 10:
				player.getPA().closeInterfaces(true);
				break;

			// Dawntain Guide, No option
			case 20:
				player.getPA().closeInterfaces(true);
				break;

			// Thessalia, no option
			case 25:
				player.getDH().sendDialogues(292);
				break;

			// Wise Old Man, No thank you.
			case 29:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No. To not buy a casket.
			case 35:
				player.getPA().closeInterfaces(true);
				break;
			// Wise Old Man, No
			case 71:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No thank you
			case 72:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No
			case 73:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No
			case 76:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No
			case 78:
				player.getPA().closeInterfaces(true);
				break;

			// Wise Old Man, No
			case 81:
				player.getPA().closeInterfaces(true);
				break;
			// Follow me
			case 88:
				player.forcedChat("Follow me.", false, false);
				player.getPA().closeInterfaces(true);
				break;

			// Teleporter, Next.
			case 101:
				player.getDH().sendDialogues(82);
				break;

			// Teleporter, Previous.
			case 112:
				player.getDH().sendDialogues(161);
				break;

			// Nothing
			case 116:
				player.getPA().closeInterfaces(true);
				break;

			// Christmas cracker, Stop, i want to keep my cracker.
			case 183:
				player.getPA().closeInterfaces(true);
				break;

			case 188:
				player.getPA().closeInterfaces(true);
				break;

			case 189:
				player.getPA().closeInterfaces(true);
				break;



			// Vannaka, No, nevermind.
			case 203:
				player.getPA().closeInterfaces(true);
				break;

			// Gandai, No.
			case 223:
				player.getPA().closeInterfaces(true);
				break;

			// Gnome, Okay i will!
			case 192:
				player.getDH().sendDialogues(1920);
				break;

			// Skill cape master, May i buy a trimmed Skill cape.
			case 229:
				if (player.skillExperience[player.skillCapeMasterSkill] >= 100000000) {
					player.getDH().sendDialogues(235);
				} else {
					player.getDH().sendDialogues(234);
				}
				break;

			// Skill cape master, Fair enough
			case 231:
				if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 99000)) {
					player.getDH().sendDialogues(237);
					return;
				}
				ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId(), 1);
				ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId() + 2, 1);
				player.getDH().sendDialogues(232);
				break;

			// Skill cape master, Fair enough
			case 236:
				if (!ItemAssistant.checkAndDeleteStackableFromInventory(player, 995, 2000000)) {
					player.getDH().sendDialogues(237);
					return;
				}
				ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getUntrimmedSkillCapeId() + 1, 1);
				player.getDH().sendDialogues(232);
				break;

			// No, i am happy where i am thanks!
			case 239:
				player.getPA().closeInterfaces(true);
				break;

			// Donator scroll, keep the scroll.
			case 248:
				player.getPA().closeInterfaces(true);
				break;

			// Prayer xp lamp.
			case 249:
				player.getPA().closeInterfaces(true);
				break;


			// Twiggy O'Korn, Achievement shop #2.
			case 256:
				player.getShops().openShop(62);
				break;

			// Custom presets yes/no dialogue.
			case 263:
				InterfaceAssistant.closeDialogueOnly(player);
				break;

			case 267:
			case 472:
				player.getPA().closeInterfaces(true);
				break;

			// Skill cape shop, Sir pysin.
			case 269:
				player.getShops().openShop(9);
				break;

			// Tutorial,  No thanks, i know my way around Dawntained
			case 272:
				player.getPA().movePlayer(NewPlayerContent.END_TUTORIAL_X_COORDINATE, NewPlayerContent.END_TUTORIAL_Y_COORDINATE, 0);
				//player.getPA().sendMessage(":packet:facecompass");
				NewPlayerContent.endTutorial(player);
				break;

			// Tutorial,  No thanks, i know my way around Dawntained (eco)
			case 562:
				player.getPA().movePlayer(3094, 3496, 0);
				NewPlayerContent.endTutorial(player);
				break;

			case 285:
				player.getPA().closeInterfaces(true);
				MithrilSeeds.resetPlayerPlantData(player);
				break;
			// No, do not delete inventory.
			case 286:
				player.getPA().closeInterfaces(true);
				break;

			// Luxurious thrones
			case 448:
				if (!player.isLegendaryDonator()) {
					player.getPA().sendMessage("<img=7>This feature is exclusive to Legendary Donators.");
					player.getPA().closeInterfaces(true);
					return;
				}
				player.getDH().sendDialogues(259);
				break;

			case 457:
				player.teleportWarningX = 0;
				player.teleportWarningY = 0;
				player.teleportWarningHeight = 0;
				player.getPA().closeInterfaces(true);
				break;

			// Trading post, close button.
			case 475:
				TradingPost.closeButton(player);
				break;

			// Trading post, close button.
			case 476:
				TradingPost.closeButton(player);
				break;

			// Do not teleport to immortal donator
			case 477:
				player.getPA().closeInterfaces(true);
				break;
			// Ask about Pet Mystery Boxes
			case 611:
				player.getPA().openWebsite("www.dawntained.com/forum/topic/1010-storepayment-faq/#comment-5807", true);
				player.getDH().sendDialogues(257);
				break;

			// Yes, delete account
			case 666:
				player.getDH().sendDialogues(667);
				break;

			// No, do not delete account.
			case 667:
				player.getPA().closeInterfaces(true);
				break;

			// Npc random event talk, yes option
			case 719:
				RandomEvent.randomEventNpcDialogueOptionClicked(player);
				break;

			default:
				Plugin.execute("option_two_" + player.getDialogueAction(), player);
				break;
		}
	}

}
