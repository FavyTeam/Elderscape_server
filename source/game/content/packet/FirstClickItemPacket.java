package game.content.packet;

import core.GameType;
import core.Plugin;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.consumable.Food;
import game.content.consumable.Potions;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.donator.MysteryBox;
import game.content.donator.PetMysteryBox;
import game.content.item.ItemInteractionManager;
import game.content.minigame.barrows.Barrows;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.ClueScroll;
import game.content.miscellaneous.CoinCasket;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.ImbuedHeart;
import game.content.miscellaneous.ItemPacks;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.MithrilSeeds;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.VotingReward;
import game.content.miscellaneous.XpLamp;
import game.content.music.SoundSystem;
import game.content.packet.preeoc.ClickItemPreEoc;
import game.content.skilling.BirdNests;
import game.content.skilling.Runecrafting;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.hunter.HunterStyle;
import game.content.skilling.hunter.HunterTrapCreationMethod;
import game.content.skilling.hunter.ImplingJars;
import game.content.skilling.prayer.BuryBone;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Clicking an item, bury bone, eat food etc
 **/

public class FirstClickItemPacket implements PacketType {
	@Override
	public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer) {
		if (player.doingAnAction() || player.getDoingAgility() || player.isTeleporting() || !player.isTutorialComplete()) {
			return;
		}

		if (player.getDead()) {
			return;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		int unknown1 = player.getInStream().readSignedWordBigEndianA();
		int itemSlot = player.getInStream().readUnsignedWordA();
		int itemId = player.getInStream().readUnsignedWordBigEndian();
		if (ItemAssistant.isNulledSlot(itemSlot)) {
			return;
		}

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "unknown1: " + unknown1);
			PacketHandler.saveData(player.getPlayerName(), "itemSlot: " + itemSlot);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}

		if (itemId != player.playerItems[itemSlot] - 1) {
			return;
		}

		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[First click item: " + itemId + "]");
		}
		player.setFirstItemClicked(itemId);
		if (ItemInteractionManager.handleItemAction(player, itemId, 1)) {
			return;
		}
		if (CoinCasket.isCoinCasketItemId(player, itemId, itemSlot)) {
			return;
		}
		if (Food.isFood(itemId)) {
			Food.eat(player, itemId, itemSlot);
			return;
		}
		if (Potions.isPotion(player, itemId)) {
			Potions.handlePotion(player, itemId, itemSlot);
			return;
		}

		if (BuryBone.isBone(itemId)) {
			BuryBone.buryBone(player, itemId, itemSlot);
			return;
		}

		if (Runecrafting.isPouch(player, itemId)) {
			return;
		}

		if (Herblore.isGrimyHerb(player, itemId, itemSlot)) {
			return;
		}

		if (Artefacts.isArtefact(player, itemId)) {
			return;
		}
		if (PlayerMiscContent.isBauble(player, itemId)) {
			return;
		}
		if (ItemPacks.isPack(player, itemId, itemSlot)) {
			return;
		}
		if (MysteryBox.isMysteryBox(player, itemId)) {
			return;
		}
		if (XpLamp.isExperienceLamp(player, itemId)) {
			return;
		}

		if (ClickItemPreEoc.firstClickItemPreEoc(player, itemId, itemSlot)) {
			return;
		}

		switch (itemId) {

			// Treasure stone
			case 7677 :
				player.getPA().sendMessage("Feels special.");
				break;
			case 20724:
				ImbuedHeart.invigorate(player);
				break;
			case 10006:
				player.getHunterSkill().lay(player, HunterStyle.BIRD_SNARING, HunterTrapCreationMethod.INVENTORY);
				break;

			// Unlock Infernal & Max capes scroll
			case 16266:
				if (player.isInfernalAndMaxCapesUnlockedScrollConsumed()) {
					player.getPA().sendMessage("You have already consumed this scroll before.");
					return;
				}
				if (Combat.inCombatAlert(player)) {
					return;
				}
				player.gfx100(199);
				ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
				player.setInfernalAndMaxCapesUnlockedScrollConsumed(true);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.UBER_DONATOR) + "You consume the scroll to unlock Infernal, Max capes, Imbued capes & 99 capes!");
				break;

			// X1 Custom pet point scroll
			case 16254:
				if (Combat.inCombatAlert(player)) {
					return;
				}
				player.gfx100(199);
				ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
				player.setCustomPetPoints(player.getCustomPetPoints() + 1);
				player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.ULTIMATE_DONATOR) + "You have redeem the scroll for x1 Custom pet point!");
				break;
			case 7509:
				PlayerMiscContent.rockCake(player);
				break;
			case 784:
				VotingReward.HandleReward(player, itemId);
				break;
			// Pet Mystery box
			case 6199:
				PetMysteryBox.displayPetMysteryBoxInterface(player);
				break;

			case 7510: //rock cake
				int amount = 3;
				if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - amount < 1) {
					amount = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - 1;
				}
				player.startAnimation(829);
				if (amount > 0) {
					Combat.createHitsplatOnPlayerNormal(player, amount, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
				}
				player.forcedChat("Ow! I nearly broke a tooth!", false, false);
				player.forcedChatUpdateRequired = true;
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
				break;
			case 299:
				MithrilSeeds.plantSeed(player);
				break;

			case 16366:
				if (Area.inClanWarsDangerousArea(player)) {
					PlayerMiscContent.tournamentAntiDeathDot(player);
				}
				break;
			// Arcane prayer scroll.
			case 21079:
				if (player.auguryUnlocked) {
					player.getPA().sendMessage("You cannot absorb anymore knowledge.");
					return;
				}
				player.getDH().sendDialogues(630);
				break;
			// Dexterous prayer scroll.
			case 21034:
				if (player.rigourUnlocked) {
					player.getPA().sendMessage("You cannot absorb anymore knowledge.");
					return;
				}
				player.getDH().sendDialogues(633);
				break;
			// Loot key scroll
			case 16145:
				player.getDH().sendDialogues(637);
				break;
			case 12791:
				RunePouch.runePouchItemClick(player, "OPEN");
				break;

			// Guide book.
			case 1856:
				GuideBook.displayGuideInterface(player);
				break;

			case 7782: //agility xp
				if (ItemAssistant.hasItemInInventory(player, 7782)) {
					ItemAssistant.deleteItemFromInventory(player, 7782, itemSlot, 1);
					Skilling.addSkillExperience(player, 25000, ServerConstants.AGILITY, true);
					player.getPA().sendMessage("You are rewarded with <col=0008f7>25,000 <col=000000>agility experience.");
					player.gfx100(199);
				}
				break;
			case 7783: //agility xp
				if (ItemAssistant.hasItemInInventory(player, 7783)) {
					ItemAssistant.deleteItemFromInventory(player, 7783, itemSlot, 1);
					Skilling.addSkillExperience(player, 50000, ServerConstants.AGILITY, true);
					player.getPA().sendMessage("You are rewarded with <col=0008f7>50,000 <col=000000>agility experience.");
					player.gfx100(199);
				}
				break;
			case 7784: //agility xp
				if (ItemAssistant.hasItemInInventory(player, 7784)) {
					ItemAssistant.deleteItemFromInventory(player, 7784, itemSlot, 1);
					Skilling.addSkillExperience(player, 100000, ServerConstants.AGILITY, true);
					player.getPA().sendMessage("You are rewarded with <col=0008f7>100,000 <col=000000>agility experience.");
					player.gfx100(199);
				}
				break;

			// coin reward caskets
			case 20546: //small
				if (ItemAssistant.hasItemInInventory(player, 20546)) {
					amount = Misc.random(7500, 12500);
					ItemAssistant.deleteItemFromInventory(player, 20546, itemSlot, 1);
					ItemAssistant.addItemToInventory(player, 995, amount, itemSlot, true);
					player.getDH().sendItemChat("Casket", "You receive " + amount + " coins from the small casket.", 1004, 200, 14, -15);
					player.getPA().sendFilterableMessage("You receive " + amount + " coins from the small casket.");
				}
				break;
			case 20545: //medium
				if (ItemAssistant.hasItemInInventory(player, 20545)) {
					amount = Misc.random(19999, 29999);
					ItemAssistant.deleteItemFromInventory(player, 20545, itemSlot, 1);
					ItemAssistant.addItemToInventory(player, 995, amount, itemSlot, true);
					player.getDH().sendItemChat("Casket", "You receive " + amount + " coins from the medium casket.", 1004, 200, 14, -15);
					player.getPA().sendFilterableMessage("You receive " + amount + " coins from the medium casket.");
				}
				break;
			case 20544: //large
				if (ItemAssistant.hasItemInInventory(player, 20544)) {
					amount = Misc.random(44999, 64999);
					ItemAssistant.deleteItemFromInventory(player, 20544, itemSlot, 1);
					ItemAssistant.addItemToInventory(player, 995, amount, itemSlot, true);
					player.getDH().sendItemChat("Casket", "You receive " + amount + " coins from the large casket.", 1004, 200, 14, -15);
					player.getPA().sendFilterableMessage("You receive " + amount + " coins from the large casket.");
				}
				break;
			case 20543: //huge
				if (ItemAssistant.hasItemInInventory(player, 20543)) {
					amount = Misc.random(74999, 99999);
					ItemAssistant.deleteItemFromInventory(player, 20543, itemSlot, 1);
					ItemAssistant.addItemToInventory(player, 995, amount, itemSlot, true);
					player.getDH().sendItemChat("Casket", "You receive " + amount + " coins from the huge casket.", 1004, 200, 14, -15);
					player.getPA().sendFilterableMessage("You receive " + amount + " coins from the huge casket.");
				}
				break;
			case 21864: //Snow sprite
				if (ItemAssistant.hasItemInInventory(player, 21864)) {
					amount = GameType.isOsrsPvp() ? Misc.random(7500, 12500) : Misc.random(5600000, 9400000);
					ItemAssistant.deleteItemFromInventory(player, 21864, itemSlot, 1);
					ItemAssistant.addItemToInventory(player, ServerConstants.getMainCurrencyId(), amount, itemSlot, true);
					player.getDH()
					      .sendItemChat("Snow sprite", "", "You receive " + amount + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " from the snow sprite.", "", "",
					                    13316, 200, 14, -15);
					player.getPA().sendFilterableMessage("You receive " + amount + " " + ServerConstants.getMainCurrencyName().toLowerCase() + " from the snow sprite.");
				}
				break;
			// Name change scroll.
			case 1505:
				player.getPA().setSidebarInterface(9, 42070);
				player.getPA().changeToSidebar(9);
				player.getPA().sendFrame126("Look up name", 42081);
				player.getPA().sendFrame126("", 42085);
				player.getPA().sendFrame126("@whi@---", 42086);
				break;

			case 10008:
				player.getHunterSkill().lay(player, HunterStyle.BOX_TRAPPING, HunterTrapCreationMethod.INVENTORY);
				//Hunter.layTrap(player);
				break;

			// Christmas cracker.
			case 962:
				player.getDH().sendDialogues(183);
				break;

			case 7478:
				player.getDH().sendDialogues(248);
				break;

			case 11238:
			case 11240:
			case 11242:
			case 11244:
			case 11246:
			case 11248:
			case 11250:
			case 11252:
			case 11254:
			case 11256:
				ImplingJars.openJar(player, itemId, itemSlot);
				break;

			case 5070:
			case 5071:
			case 5072:
			case 5073:
			case 5074:
				BirdNests.LootNest(player, itemId, itemSlot);
				break;

			case 11941:
				LootingBag.displayLootingBagInterface(player);
				break;

			case 3144:
				Potions.eatKarambwan(player, itemId, itemSlot);
				break;

			// Prayer lamp.
			case 2528:
				player.getDH().sendDialogues(249);
				break;


			// Clue casket (1st release)
			case 2740:
				ClueScroll.openCasket(player, itemId, ClueScroll.casketNormal1, ClueScroll.casketRare1);
				break;

			// Clue casket (2nd release)
			case 2742:
				ClueScroll.openCasket(player, itemId, ClueScroll.casketNormal2, ClueScroll.casketRare2);
				break;

			// Clue casket (3rd release)
			case 2744:
				ClueScroll.openCasket(player, itemId, ClueScroll.casketNormal3, ClueScroll.casketRare3);
				break;

			// Monkey greegree.
			case 4024:
				//player.getDH().sendDialogues(180);
				break;

			// Spade.
			case 952:
				player.startAnimation(831);
				SoundSystem.sendSound(player, 380, 500);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						ClueScroll.dig(player);
						Barrows.startDigging(player);
						player.startAnimation(65535);
					}
				}, 2);
				break;

			// Teleport home.
			case 8013:
				int x = 3092 + Misc.random(4);
				int y = 3501 + Misc.random(4);
				int height = 0;
				if (player.getHeight() == 4) {
					x = 3096;
					y = 3497;
					height = 4;
				}
				Teleport.startTeleport(player, x, y, height, "TAB");
				break;

			// Barrows teleport.
			case 19629:
				int a = 3565;
				int b = 3306;
				int c = 0;
				Teleport.startTeleport(player, a, b, c, "BARROWS_TAB");
				break;

			// Ectophial
			case 4251:
				Teleport.startTeleport(player, 3660, 3516, 0, "ECTO");
				break;

			// Royal seed pod
			case 19564:
				boolean inEdgePvp = player.getHeight() == 4;
				if (inEdgePvp) {
					Teleport.startTeleport(player, player.toggleSeedPod ? 3094 + Misc.random(3) : 2464 + Misc.random(3), player.toggleSeedPod ? 3495 + Misc.random(3) : 3474 + Misc.random(3), player.toggleSeedPod ? player.getHeight() : 0, "SEED_POD");
				}
				else {
					Teleport.startTeleport(player, player.toggleSeedPod ? 3086 + Misc.random(3) : 2464 + Misc.random(3), player.toggleSeedPod ? 3489 + Misc.random(3) : 3474 + Misc.random(3), 0, "SEED_POD");
				}
				break;

			// Enchanted gem.
			case 4155:
				if (Combat.inCombat(player)) {
					player.getPA().sendMessage(Slayer.getTaskString(player));
				} else {
					player.getDH().sendDialogues(39);
				}
				break;

			// Clue scroll.
			case 2677:
				ClueScroll.openClueScroll(player);
				break;

			//Dice Bag
			case 16004:
				if (System.currentTimeMillis() - player.diceDelay < 2500) {
					return;
				}
				player.diceDelay = System.currentTimeMillis();
				if (!ClanChatHandler.inDiceCc(player, true, false)) {
					return;
				}
				player.startAnimation(11000);
				player.gfx0(2000);
				player.doingActionEvent(4);
				String message = "I have rolled a " + Misc.random(1, 100) + " on the percentile dice.";
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();

					}

					@Override
					public void stop() {
						ClanChatHandler.sendDiceClanMessage(player.getPlayerName(), player.getClanId(), message);
					}
				}, 2);
				break;
			default:
				if (Plugin.execute("first_click_item_" + itemId, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
		}
	}

}
