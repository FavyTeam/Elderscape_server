package game.content.packet;

import core.GameType;
import core.Plugin;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.bank.BankButtons;
import game.content.bank.BankPin;
import game.content.buttons.SpellBookButton;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.vsplayer.LootKey;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.combat.vsplayer.magic.MagicAttack;
import game.content.commands.AdministratorCommand;
import game.content.dialogue.DialogueChain;
import game.content.dialogue.DialogueLink;
import game.content.dialogueold.options.FiveOptions;
import game.content.dialogueold.options.FourOptions;
import game.content.dialogueold.options.ThreeOptions;
import game.content.dialogueold.options.TwoOptions;
import game.content.donator.DonatorContent;
import game.content.donator.DonatorTokenUse;
import game.content.donator.MysteryBox;
import game.content.donator.NameChange;
import game.content.donator.PetMysteryBox;
import game.content.highscores.HighscoresHallOfFame;
import game.content.highscores.HighscoresInterface;
import game.content.interfaces.ChangePasswordInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.interfaces.donator.DonatorMainTab;
import game.content.interfaces.donator.DonatorShop;
import game.content.minigame.AutoDice;
import game.content.miscellaneous.CompletionistCape;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.GnomeGlider;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.NpcDropTableInterface;
import game.content.miscellaneous.PriceChecker;
import game.content.miscellaneous.PvpBlacklist;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.TeleportInterface;
import game.content.miscellaneous.XpLamp;
import game.content.music.MusicTab;
import game.content.packet.preeoc.ClickButtonPreEoc;
import game.content.prayer.book.regular.QuickPrayers;
import game.content.prayer.book.regular.RegularPrayer;
import game.content.profile.Profile;
import game.content.profile.ProfileBiography;
import game.content.quest.QuestHandler;
import game.content.quest.tab.ActivityTab;
import game.content.quest.tab.InformationTab;
import game.content.quest.tab.PanelTab;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Cooking;
import game.content.skilling.Skilling;
import game.content.skilling.Skilling.SkillCapes;
import game.content.skilling.agility.AgilityAssistant;
import game.content.skilling.crafting.EnchantJewelry;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.GlassBlowing;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.crafting.OrbCharging;
import game.content.skilling.crafting.SpinningWheel;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.prayer.BoneOnAltar;
import game.content.skilling.smithing.Smithing;
import game.content.starter.GameMode;
import game.content.starter.NewPlayerContent;
import game.content.tradingpost.TradingPost;
import game.content.worldevent.Tournament;
import game.item.DestroyItem;
import game.item.ItemAssistant;
import game.npc.pet.Pet;
import game.object.custom.ObjectManagerServer;
import game.player.Area;
import game.player.LogOutUpdate;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.FileUtility;
import utility.Misc;

public class ClickingButtonPacket implements PacketType {

	@Override
	public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer) {
		int buttonId = Misc.hexToInt(player.getInStream().buffer, 0, packetSize);
		clickButton(player, buttonId, trackPlayer);
	}

	public static void clickButton(Player player, int buttonId, boolean trackPlayer) {

		if (ClickButtonPreEoc.handleButtonPreEoc(player, buttonId)) {
			return;
		}
		if (buttonId == 22228 && GameType.isPreEoc()) {
			Misc.print("Teleport to: " + Teleport.debugPreEocTeleports.get(Teleport.index));
			String string = Teleport.debugPreEocTeleports.get(Teleport.index);
			string = string.substring(string.indexOf(";") + 2);
			String[] parse = string.split(" ");
			player.getPA().movePlayer(Integer.parseInt(parse[0]), Integer.parseInt(parse[1]),
			                          Integer.parseInt(parse[2]));
			Teleport.index++;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "buttonId: " + buttonId);
		}


		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Button: " + buttonId + "] [Dialogue: " + player.getDialogueAction() + "]");
		}

		if (EnchantJewelry.isEnchantBoltButton(player, buttonId)) {
			return;
		} else 
		if (CombatInterface.isCombatInterfaceButton(player, buttonId)) {
			int id = CombatInterface.sendClickedCombatStyle(player, buttonId);
			if (id != 0) {
				player.getPA().sendMessage(":packet:combatstyle " + id);
			}
			return;
		}

		if (AutoCast.isOldAutoCastButton(player, buttonId)) {
			return;
		} else if (AutoCast.assignNewAutocast(player, buttonId)) {
			return;
		} else if (CompletionistCape.isCompletionistCapeButton(player, buttonId)) {
			return;
		} else if (XpLamp.xpLampButton(player, buttonId)) {
			return;
		} else if (ClanChatHandler.isClanChatButton(player, buttonId)) {
			return;
		} else if (HighscoresHallOfFame.isButton(player, buttonId)) {
			return;
		} else if (PlayerTitle.playerTitleInterfaceAction(player, buttonId)) {
			return;
		} else if (MysteryBox.isMysteryBoxButton(player, buttonId)) {
			return;
		} else if (GameMode.isGameModeButton(player, buttonId)) {
			return;
		}

		if (player.doingAnAction() || player.getDoingAgility() || player.getDead() || player.isTeleporting()) {
			boolean canUseButtonWhileBusy = false;
			if (buttonId >= 70080 && buttonId <= 70094 || buttonId >= 21233 && buttonId <= 21247
			    || buttonId >= 2171 && buttonId <= 2173 || buttonId == 89176 || buttonId == 89180
			    || buttonId == 89178 || buttonId == 150 || buttonId == 3189 || buttonId == 152 || buttonId == 48176
			    || buttonId == 3147) {
				canUseButtonWhileBusy = true;
			}
			for (int i = 0; i < NewPlayerContent.tutorialButtonExceptionList.length; i++) {
				if (NewPlayerContent.tutorialButtonExceptionList[i] == buttonId) {
					canUseButtonWhileBusy = true;
					break;
				}
			}
			if (!canUseButtonWhileBusy) {
				return;
			}
		}
		if (QuickSetUp.isQuickSetUpButton(player, buttonId)) {
			return;
		} else if (DonatorShop.isDonatorShopButton(player, buttonId)) {
			return;
		} else if (AutoDice.isAutoDiceInterfaceClicked(player, buttonId)) {
			return;
		} else if (SpellBookButton.isSpellBookButton(player, buttonId)) {
			return;
		} else if (BankButtons.isBankButtons(player, buttonId)) {
			return;
		} else if (DestroyItem.isDestroyInterfaceButton(player, buttonId)) {
			return;
		} else if (MusicTab.handleClick(player, buttonId)) {
			return;
		} else if (GnomeGlider.isGnomeGliderButton(player, buttonId)) {
			return;
		} else if (QuickPrayers.clickPray(player, buttonId)) {
			return;
		} else if (Profile.isProfileButton(player, buttonId)) {
			return;
		} else if (ProfileBiography.isBiographyButton(player, buttonId)) {
			return;
		} else if (HighscoresInterface.isHighscoresButton(player, buttonId)) {
			return;
		} else if (LeatherCrafting.isLeatherCraftingButton(player, buttonId)) {
			return;
		}
		if (Achievements.isAchievementButton(player, buttonId)) {
			return;
		}
		if (LeatherCrafting.isTanningButton(player, buttonId)) {
			return;
		}
		if (ChangePasswordInterface.button(player, buttonId)) {
			return;
		}

		if (JewelryCrafting.isJewelryInterfaceButton(player, buttonId)) {
			return;
		}

		if (TeleportInterface.isTeleportInterfaceButton(player, buttonId)) {
			return;
		}

		if (TradingPost.tradingPostButton(player, buttonId)) {
			return;
		}

		if (Smithing.smithingButtons(player, buttonId, "")) {
			return;
		}

		if (NpcDropTableInterface.isNpcDropTableButton(player, buttonId)) {
			return;
		}

		if (GuideBook.isGuideInterfaceButton(player, buttonId)) {
			return;
		}

		if (PvpBlacklist.isPvpBlacklistButton(player, buttonId)) {
			return;
		}

		if (RunePouch.runePouchInterfaceButton(player, buttonId)) {
			return;
		}

		if (GlassBlowing.isGlassBlowingButton(player, buttonId)) {
			return;
		}

		if (QuestHandler.isQuestButton(player, buttonId)) {
			return;
		}

		if (buttonId >= 81118 && buttonId <= 81123) {
			int index = buttonId - 81118;

			// Must check if distance is close to obelisk to prevent packet
			// abuse.
			if (player.getPA().withInDistance(player.getX(), player.getY(), player.getObjectX(), player.getObjectY(),
			                                  2)) {
				Server.objectManager.startObelisk(player.getObjectId(), true,
				                                  ObjectManagerServer.orderedObeliskCoords[index][0],
				                                  ObjectManagerServer.orderedObeliskCoords[index][1]);
			}
			player.getPA().closeInterfaces(true);
			return;
		}

		if (InformationTab.isQuestTabInformationButton(player, buttonId)) {
			return;
		}

		if (PanelTab.isPanelTabButton(player, buttonId)) {
			return;
		}

		if (ActivityTab.isActivityTabButton(player, buttonId)) {
			return;
		}
		if (PriceChecker.isPriceCheckerButton(player, buttonId)) {
			return;
		}
		if (DonatorMainTab.isDonatorInterfaceMainTabButton(player, buttonId)) {
			return;
		}
		if (NpcDoubleItemsInterface.button(player, buttonId)) {
			return;
		}

		switch (buttonId) {
			case 164098:
				if (NameChange.stage == 1) {
					Plugin.execute("name_change_dialogues", player);
				}
				break;
			case 164089:
				player.getPA().setSidebarInterface(9, 42006);
				break;
			case 164093:
				player.playerAssistant.sendMessage(":namechange:");
				player.closePmInterfaceOnWalk = true;
				break;
			// Wilderness rules interface
			case 164077:
				player.getPA().openWebsite("www.dawntained.com/forum/topic/7843-wilderness-rules/", false);
				break;
			case 164081:
				player.getAttributes().put(Player.WILDERNESS_RULES_WARNING_ENABLED, false);
				player.getPA().sendFrame36(1250, 1, false);
				break;
			case 164070:
				if (player.getAttributes().getOrDefault(Player.WILDERNESS_RULES_WARNING_ENABLED, true)) {
					player.getDH().sendStatement("You must click the tick box before doing this.");
				}
				else {
					player.getPA().closeInterfaces(true);
				}
				break;
			case 164073:
				player.getDH().sendStatement("You cannot enter the Wilderness without accepting.");
				break;

			// Quick links buttons in new management tab
			case 164024: // open store
				player.getPA().openWebsite("www.dawntained.com/store", false);
				break;
			case 164036: // open forums
				player.getPA().openWebsite("www.dawntained.com/forum", false);
				break;
			case 164028: // open voting
				player.getPA().openWebsite("www.dawntained.com/vote", false);
				break;
			case 164032: // open wiki
				player.getPA().openWebsite("www.dawntained.wikia.com/wiki/Dawntained_Wiki", false);
				break;

			case 136174:
				boolean alwaysPlaceholder = player.getAttributes().getOrDefault(Bank.ALWAYS_PLACEHOLDER, false);

				alwaysPlaceholder = !alwaysPlaceholder;

				player.getPA().sendFrame36(835, alwaysPlaceholder ? 1 : 0, false);

				player.getAttributes().put(Bank.ALWAYS_PLACEHOLDER, alwaysPlaceholder);
				break;
			case 72038:
				if (GameType.isOsrsEco()) {
					if (player.getQuest(5).getStage() == 7) {
						Teleport.startTeleport(player, 2764, 2785, 0, "ARCEUUS");
					} else {
						player.getDH()
						      .sendStatement("You must have completed @dre@Monkey Madness@bla@ to use this teleport.");
					}
				}
				break;

			// Sound off button.
			case 3173:
				player.soundEnabled = false;
				break;

			// Sound on buttons.
			case 3174:
			case 3175:
			case 3176:
			case 3177:
				player.soundEnabled = true;
				break;

			case 102018:
				player.getPA().setSidebarInterface(2, 19500);
				break;

			case 102096:
				Tournament.talkToCowKiller(player);
				break;
			case 81069:
				if (player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1381
				    && player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1397) {
					player.getDH().sendStatement("You need an air staff to charge air orbs.");
					return;
				}
				OrbCharging.chargeOrb(player, OrbCharging.Orbs.AIR);
				break;
			case 81070:
				if (player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1383
				    && player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1395) {
					player.getDH().sendStatement("You need a water staff to charge water orbs.");
					return;
				}
				OrbCharging.chargeOrb(player, OrbCharging.Orbs.WATER);
				break;
			case 81071:
				if (player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1385
				    && player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1399) {
					player.getDH().sendStatement("You need an earth staff to charge earth orbs.");
					return;
				}
				OrbCharging.chargeOrb(player, OrbCharging.Orbs.EARTH);
				break;
			case 81072:
				if (player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1387
				    && player.playerEquipment[ServerConstants.WEAPON_SLOT] != 1393) {
					player.getDH().sendStatement("You need a fire staff to charge fire orbs.");
					return;
				}
				OrbCharging.chargeOrb(player, OrbCharging.Orbs.FIRE);
				break;

			case 114033:
				PetMysteryBox.openPetMysteryBox(player);
				break;
			// Charge spell.
			case 4169:
				MagicAttack.chargeSpell(player);
				break;
			case 112201:
				player.diceRulesForce = false;
				player.getPA().closeInterfaces(true);
				break;
			case 95084:
				Movement.stopMovement(player);
				player.startAnimation(2763);
				break;

			case 94196:// air guitar
				if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 13221 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 13222) {
					Movement.stopMovement(player);
					player.startAnimation(4751);
					player.gfx0(1239);
				}
				else {
					player.getPA().sendMessage("You need to be wearing a music cape to do this emote.");
				}
				break;

			case 95085:
				Movement.stopMovement(player);
				player.startAnimation(2756);
				break;

			case 95086:
				Movement.stopMovement(player);
				player.startAnimation(2761);
				break;

			case 95087:
				Movement.stopMovement(player);
				player.startAnimation(2764);
				break;

			// skill interfaces
			case 34142: // tab 1
				player.getSkillMenu().menuCompilation(1);
				break;

			case 34119: // tab 2
				player.getSkillMenu().menuCompilation(2);
				break;

			case 34120: // tab 3
				player.getSkillMenu().menuCompilation(3);
				break;

			case 34123: // tab 4
				player.getSkillMenu().menuCompilation(4);
				break;

			case 34133: // tab 5
				player.getSkillMenu().menuCompilation(5);
				break;

			case 34136: // tab 6
				player.getSkillMenu().menuCompilation(6);
				break;

			case 34139: // tab 7
				player.getSkillMenu().menuCompilation(7);
				break;

			case 34155: // tab 8
				player.getSkillMenu().menuCompilation(8);
				break;

			case 34158: // tab 9
				player.getSkillMenu().menuCompilation(9);
				break;

			case 34161: // tab 10
				player.getSkillMenu().menuCompilation(10);
				break;

			case 59199: // tab 11
				player.getSkillMenu().menuCompilation(11);
				break;

			case 59202: // tab 12
				player.getSkillMenu().menuCompilation(12);
				break;
			case 59205: // tab 13
				player.getSkillMenu().menuCompilation(13);
				break;

			case 33224:
				player.getSkillMenu().runecraftingComplex(1);
				player.getSkillMenu().selected = 6;
				break;

			case 103002:
				player.getSkillMenu().hunterComplex(1);
				player.getSkillMenu().selected = 21;
				break;

			case 33210:
				player.getSkillMenu().agilityComplex(1);
				player.getSkillMenu().selected = 8;
				break;

			case 54105:
				player.getSkillMenu().herbloreComplex(1);
				player.getSkillMenu().selected = 9;
				break;

			case 33216:
				player.getSkillMenu().thievingComplex(1);
				player.getSkillMenu().selected = 10;
				break;

			case 33219:
				player.getSkillMenu().craftingComplex(1);
				player.getSkillMenu().selected = 11;
				break;

			case 33222:
				player.getSkillMenu().fletchingComplex(1);
				player.getSkillMenu().selected = 12;
				break;

			case 47130:
				player.getSkillMenu().slayerComplex(1);
				player.getSkillMenu().selected = 13;
				break;

			case 33208:
				player.getSkillMenu().miningComplex(1);
				player.getSkillMenu().selected = 14;
				break;

			case 33211:
				player.getSkillMenu().smithingComplex(1);
				player.getSkillMenu().selected = 15;
				break;

			case 33214:
				player.getSkillMenu().fishingComplex(1);
				player.getSkillMenu().selected = 16;
				break;

			case 33217:
				player.getSkillMenu().cookingComplex(1);
				player.getSkillMenu().selected = 17;
				break;

			case 33220:
				player.getSkillMenu().firemakingComplex(1);
				player.getSkillMenu().selected = 18;
				break;

			case 33223:
				player.getSkillMenu().woodcuttingComplex(1);
				player.getSkillMenu().selected = 19;
				break;

			case 54104:
				player.getSkillMenu().farmingComplex(1);
				player.getSkillMenu().selected = 20;
				break;

			case 109104:
				QuickSetUp.displayInterface(player);
				// QuestHandler.updateAllQuestTab(player);
				break;

			case 109108:
				GuideBook.displayGuideInterface(player);
				break;

			case 86231:
				LootingBag.closeLootingBagInterface(player);
				break;

			case 113180:
				LootKey.bankLoot(player);
				break;

			// Reset session.
			case 19146:
				player.currentSessionExperience = 0;
				break;
			// Show session.
			case 19143:
				player.xpBarShowType = "SESSION";
				Skilling.sendXpToDisplay(player);
				break;

			// Show total.
			case 19140:
				player.xpBarShowType = "TOTAL";
				Skilling.sendXpToDisplay(player);
				break;

			// Show COMBAT.
			case 19147:
				player.xpBarShowType = "COMBAT";
				Skilling.sendXpToDisplay(player);
				break;

			case 15062:
				player.usingShop = false;
				break;

			case 102003:
				if (ItemAssistant.hasItemInInventory(player, 1737)) {
					SpinningWheel.spinningWheel(player, buttonId);
				} else {
					player.getPA().sendMessage("You don't have any wool to spin.");
				}
				break;

			case 102004:
				if (ItemAssistant.hasItemInInventory(player, 1779)) {
					SpinningWheel.spinningWheel(player, buttonId);
				} else {
					player.getPA().sendMessage("You don't have any flax to spin.");
				}
				break;

			case 102005:
				if (ItemAssistant.hasItemInInventory(player, 9436)) {
					SpinningWheel.spinningWheel(player, buttonId);
				} else {
					player.getPA().sendMessage("You don't have any sinew to spin.");
				}
				break;

			case 99228:
				player.lastProfileTabText = "WEAPON PVP";
				break;

			// Decline button on first trade screen.
			case 13094:
				player.getTradeAndDuel().declineTrade1(true);
				break;

			// Decline button on second trade screen.
			case 13220:
				player.getTradeAndDuel().declineTrade1(true);
				break;

			case 99056:
				player.lastDialogueOptionString = "";
				player.getDH().sendDialogues(player.lastDialogueSelected);
				break;

			case 10239:
			case 6211:
			case 6212:
			case 10238:
				int amount = 1;
				if (buttonId == 6211) {
					amount = 2000;
				} else if (buttonId == 6212) {
					amount = 100;
				} else if (buttonId == 10238) {
					amount = 5;
				}
				if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.HERBLORE])) {
					Herblore.herbloreInterfaceAction(player, amount);
				} else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING])) {
					GemCrafting.craftingInterfaceAction(player, amount);
				} else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.PRAYER])) {
					BoneOnAltar.prayerInterfaceAction(player, amount);
				} else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.FLETCHING])) {
					BowStringFletching.fletchingInterfaceAction(player, amount);
				}
				/*
				 * else if (player.skillingInterface.equals("BATTLESTAFF")) {
				 * BattlestaffMaking.BattlestaffInterfaceAction(player, amount); }
				 */
				else if (player.skillingInterface.equals("HARD LEATHER BODY")) {
					LeatherCrafting.hardLeatherBodyInterfaceAction(player, amount);
				} else if (player.skillingInterface.equals("STRINGING AMULET")) {
					JewelryCrafting.stringAmuletAmount(player, amount);
				} else if (player.skillingInterface.equals("COMBINE ARROWS")) {
					Fletching.combineArrowPartsAmount(player, amount);
				} else if (player.skillingInterface.equals("CUT GEM INTO BOLT TIPS")) {
					Fletching.cutGemAmount(player, amount);
				} else if (player.skillingInterface.equals("ATTACH TIPS TO BOLT")) {
					Fletching.attachTipToBoltAmount(player, amount);
				}
				break;

			case 67207:
				player.getPA().requestUpdates();
				break;

			case 96239:
			case 97016:
				player.playerAssistant.setSidebarInterface(6, 24818); // COMBAT
				// FIRST.
				player.ancientsInterfaceType = 2;
				break;

			case 97019:
			case 96254:
				player.playerAssistant.setSidebarInterface(6, 24800); // TELEPORT
				// FIRST.
				player.ancientsInterfaceType = 0;
				break;

			case 97001:
			case 96236:
				player.playerAssistant.setSidebarInterface(6, 24836); // DEFAULT.
				player.ancientsInterfaceType = 1;
				break;

			case 19136:
				if (player.getDead()) {
					return;
				}
				if (player.quickPray) {
					QuickPrayers.turnOffQuicks(player);
					return;
				}
				QuickPrayers.turnOnQuicks(player);
				break;

			case 19137:
				// Select quick prayers
				QuickPrayers.selectQuickInterface(player);
				break;

			case 67079:
				// quick curse confirm
				QuickPrayers.clickConfirm(player);
				break;

			case 5001:
				// select your quick prayers/curses
				QuickPrayers.selectQuickInterface(player);
				player.getPA().sendFrame106(5);
				break;

			// Completionist cape interface, close.
			case 54189:
				player.getPA().closeInterfaces(true);
				break;

			// Close button in Dawntain Guide interface
			case 102189:
				player.getPA().closeInterfaces(true);
				break;

			/* Dialogue options */

			/* Two options */

			// First option on a two option dialogue
			case 9157:
				DialogueChain chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 1));
					}
					return;
				}
				TwoOptions.firstOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Second option on a two option dialogue
			case 9158:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 2));
					}
					return;
				}
				TwoOptions.secondOption(player);
				player.getDH().dialogueActionReset();
				break;

			/* End of Two options */

			/* Three options */

			// First option on a three option dialogue
			case 9167:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 1));
					}
					return;
				}
				ThreeOptions.firstOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Second option on a three option dialogue
			case 9168:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 2));
					}
					return;
				}
				ThreeOptions.secondOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Third option on a three option dialogue
			case 9169:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 3));
					}
					return;
				}
				ThreeOptions.thirdOption(player);
				player.getDH().dialogueActionReset();
				break;

			/* End of Three options */

			/* Four options */

			// First option on a four option dialogue
			case 9178:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 1));
					}
					return;
				}
				FourOptions.firstOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Second option on a four option dialogue
			case 9179:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 2));
					}
					return;
				}
				FourOptions.secondOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Third option on a four option dialogue
			case 9180:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 3));
					}
					return;
				}
				FourOptions.thirdOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Fourth option on a four option dialogue
			case 9181:
				chain = player.getDialogueChain();

				if (chain != null) {
					DialogueLink link = chain.getCurrentLink();

					if (link != null) {
						link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 4));
					}
					return;
				}
				FourOptions.fourthOption(player);
				player.getDH().dialogueActionReset();
				break;

			/* End of Four options */

			/* Five options */

			// First option on a five option dialogue
			case 9190:
                chain = player.getDialogueChain();

                if (chain != null) {
                    DialogueLink link = chain.getCurrentLink();

                    if (link != null) {
                        link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 1));
                    }
                    return;
                }
				FiveOptions.firstOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Second option on a five option dialogue
			case 9191:
                chain = player.getDialogueChain();

                if (chain != null) {
                    DialogueLink link = chain.getCurrentLink();

                    if (link != null) {
                        link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 2));
                    }
                    return;
                }
				FiveOptions.secondOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Third option on a five option dialogue
			case 9192:
                chain = player.getDialogueChain();

                if (chain != null) {
                    DialogueLink link = chain.getCurrentLink();

                    if (link != null) {
                        link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 3));
                    }
                    return;
                }
				FiveOptions.thirdOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Fourth option on a five option dialogue
			case 9193:
                chain = player.getDialogueChain();

                if (chain != null) {
                    DialogueLink link = chain.getCurrentLink();

                    if (link != null) {
                        link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 4));
                    }
                    return;
                }
				FiveOptions.fourthOption(player);
				player.getDH().dialogueActionReset();
				break;

			// Fifth option on a five option dialogue
			case 9194:
                chain = player.getDialogueChain();

                if (chain != null) {
                    DialogueLink link = chain.getCurrentLink();

                    if (link != null) {
                        link.getClickOptionListeners().forEach(listener -> listener.onOption(player, 5));
                    }
                    return;
                }
				FiveOptions.fifthOption(player);
				player.getDH().dialogueActionReset();
				break;

			/* End of Five options */

			/* End of Dialogue options */

			/* Start of Bank pin */

			case 58074:
				BankPin.close(player);
				break;

			// Bank pin interface, I don't know it.
			case 58073:
				player.getPA().closeInterfaces(true);
				break;

			case 58025:
			case 58026:
			case 58027:
			case 58028:
			case 58029:
			case 58030:
			case 58031:
			case 58032:
			case 58033:
			case 58034:
				BankPin.pinEnter(player, buttonId);
				break;

			// Return to bank.
			case 82032:
				if (!player.usingEquipmentBankInterface) {
					return;
				}
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, false);
				player.usingEquipmentBankInterface = false;
				break;

			case 33206:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().attackComplex(1);
					player.getSkillMenu().selected = 0;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "ATTACK");
				break;

			case 33209:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().strengthComplex(1);
					player.getSkillMenu().selected = 1;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "STRENGTH");
				break;

			case 33212:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().defenceComplex(1);
					player.getSkillMenu().selected = 2;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "DEFENCE");
				break;

			case 33215:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().rangedComplex(1);
					player.getSkillMenu().selected = 3;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "RANGED");
				break;

			case 33218:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().prayerComplex(1);
					player.getSkillMenu().selected = 4;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "PRAYER");
				break;

			case 33221:
				if (GameType.isOsrsEco() && !player.getAbleToEditCombat()) {
					player.getSkillMenu().magicComplex(1);
					player.getSkillMenu().selected = 5;
					break;
				}
				EditCombatSkill.editCombatSkillAction(player, "MAGIC");
				break;

			case 33207:
				if (GameType.isOsrsEco()) {
					player.getSkillMenu().hitpointsComplex(1);
					player.getSkillMenu().selected = 7;
					break;
				}
				player.getDH().sendItemChat("", "You can't manually set your Hitpoints level.",
				                            "Instead, it will automatically scale with other combat stats.", 744, 300, 18, 0);
				break;

			// Show equipment stats
			case 112181:

				if (player.clipping) {
					FileUtility.addLineOnTxt(ServerConstants.getOsrsGlobalDataLocation() + "world/remove clipped tiles.txt",
					                         player.getX() + " " + player.getY() + " " + player.getHeight());
				} else if (player.saveNpcText) {
					AdministratorCommand.saveNpcText(player);
				} else {
					ItemAssistant.updateEquipmentBonusInterface(player);
					player.getPA().displayInterface(15106);
				}
				break;

			// Show items kept on death
			case 112178:
				if (player.clipping) {
					FileUtility.addLineOnTxt(ServerConstants.getOsrsGlobalDataLocation() + "world/add clipped tiles.txt",
					                         player.getX() + " " + player.getY() + " " + player.getHeight());
				} else {
					ItemsKeptOnDeath.showDeathInterface(player);
				}
				break;

			case 101244:
			case 149_190:
				PriceChecker.open(player);
				break;

			case 101249:
				if (!player.adminPlayerCollection.isEmpty()) {
					if (player.adminPlayerCollectionIndex > player.adminPlayerCollection.size() - 1) {
						player.getPA().sendMessage("End of player list.");
						return;
					}
					Player suspect = PlayerHandler.players[player.adminPlayerCollection.get(player.adminPlayerCollectionIndex)];
					if (suspect == null) {
						player.getPA().sendMessage("Player is null.");
					} else {
						player.getPA().movePlayer(suspect.getX(), suspect.getY(), suspect.getHeight());
						player.getPA().sendMessage("Teleported to: " + suspect.getPlayerName());
					}

					player.adminPlayerCollectionIndex++;
				} else {
					Pet.callFamiliar(player);
				}
				break;
			case 102090: // tournament info interface
				player.getPA().closeInterfaces(true);
				break;
			case 102143: // tournament info interface
				player.getPA().displayInterface(24950);
				break;
			case 102093: // tournament info interface
			case 102168: // tournament info interface
				player.getPA().displayInterface(24951);
				break;
			case 102146: // tournament info interface
			case 102238: // tournament info interface
				player.getPA().displayInterface(24952);
				break;

			case 102171: // tournament info interface
				player.getPA().displayInterface(24953);
				break;

			case 59004:
				player.getPA().closeInterfaces(true);
				break;
			case 150:
				// Auto retaliate
				player.setAutoRetaliate((player.getAutoRetaliate() == 0) ? 1 : 0);
				player.getPA().sendMessage(":packet:otherbutton 150");
				break;

			// Special attack orb click
			case 154 :
				/*
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				*/
				break;
			/** Specials **/
			case 29188:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 29163:
				if (Combat.staffOfTheDeadSpecial(player)) {
					return;
				}
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 33033:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 29038:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				if (Combat.hasGraniteMaulEquipped(player)) {
					Combat.clickGraniteMaulSpecial(player);
				}
				CombatInterface.updateSpecialBar(player);
				break;
			case 8041:
				player.getPA().closeInterfaces(true);
				break;
			case 29063:
				if (player.getWieldedWeapon() == 1377) {
					if (Combat.checkSpecAmount(player, player.getWieldedWeapon())) {
						player.gfx0(246);
						player.forcedChat("Raarrrrrgggggghhhhhhh!", false, false);
						player.startAnimation(1056);
						player.currentCombatSkillLevel[ServerConstants.STRENGTH] = player.getBaseStrengthLevel() + (player.getBaseStrengthLevel() * 15 / 100);
						Skilling.updateSkillTabFrontTextMain(player, 2);
						CombatInterface.updateSpecialBar(player);
					} else {
						Combat.notEnoughSpecialLeft(player);
					}
				} else {
					player.setUsingSpecialAttack(!player.isUsingSpecial());
					Combat.clickGraniteMaulSpecial(player);
					CombatInterface.updateSpecialBar(player);
				}
				break;

			case 30007:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 48023:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 29138:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;

			// Toxic blowpipe.
			case 29213:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 29113:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 29238:
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			case 30108:
				// Claws
				player.setUsingSpecialAttack(!player.isUsingSpecial());
				Combat.clickGraniteMaulSpecial(player);
				CombatInterface.updateSpecialBar(player);
				break;
			/** Dueling **/
			case 26065:
				// no forfeit
			case 26040:
				player.getTradeAndDuel().toggleRule(0, true);
				break;
			case 26066:
				// no movement
			case 26048:
				player.getTradeAndDuel().toggleRule(1, true);
				break;
			case 26069:
				// no range
			case 26042:
				player.getTradeAndDuel().toggleRule(2, true);
				break;
			case 26070:
				// no melee
			case 26043:
				player.getTradeAndDuel().toggleRule(3, true);
				break;
			case 26071:
				// no mage
			case 26041:
				player.getTradeAndDuel().toggleRule(4, true);
				break;
			case 26072:
				// no drinks
			case 26045:
				player.getTradeAndDuel().toggleRule(5, true);
				break;
			case 26073:
				// no food
			case 26046:
				player.getTradeAndDuel().toggleRule(6, true);
				break;
			case 26074:
				// no prayer
			case 26047:
				player.getTradeAndDuel().toggleRule(7, true);
				break;

			// Obstacles
			case 26076:
			case 26075:
				player.getTradeAndDuel().toggleRule(8, true);
				break;

			// Anti-scam
			case 2158:
				player.getTradeAndDuel().toggleAntiScamManually();
				break;
			case 30136:
				// sp attack
			case 30137:
				player.getTradeAndDuel().toggleRule(10, true);
				break;
			case 53245:
				// no helm
				player.getTradeAndDuel().toggleRule(11, true);
				break;
			case 53246:
				// no cape
				player.getTradeAndDuel().toggleRule(12, true);
				break;
			case 53247:
				// no ammy
				player.getTradeAndDuel().toggleRule(13, true);
				break;
			case 53249:
				// no weapon.
				player.getTradeAndDuel().toggleRule(14, true);
				break;
			case 53250:
				// no body
				player.getTradeAndDuel().toggleRule(15, true);
				break;
			case 53251:
				// no shield
				player.getTradeAndDuel().toggleRule(16, true);
				break;
			case 53252:
				// no legs
				player.getTradeAndDuel().toggleRule(17, true);
				break;
			case 53255:
				// no gloves
				player.getTradeAndDuel().toggleRule(18, true);
				break;
			case 53254:
				// no boots
				player.getTradeAndDuel().toggleRule(19, true);
				break;
			case 53253:
				// no rings
				player.getTradeAndDuel().toggleRule(20, true);
				break;
			case 53248:
				// no arrows
				player.getTradeAndDuel().toggleRule(21, true);
				break;

			// Duel arena, first screen, accept
			case 26018:
				player.getTradeAndDuel().duelArenaAcceptFirstScreen(false);
				break;

			case 25120:
				if (player.getDuelStatus() != 3) {
					return;
				}
				if (Area.inDuelArena(player)) {
					if (player.getDuelStatus() == 5) {
						break;
					}
					Player o2 = player.getTradeAndDuel().getPartnerDuel();
					final Player other = o2;
					if (other == null) {
						player.getTradeAndDuel().declineDuel(false);
						return;
					}
					if (other.getDuelingWith() != player.getPlayerId()) {
						player.getTradeAndDuel().declineTrade1(true);
						return;
					}

					if (!player.getTradeAndDuel().hasRequiredSpaceForDuel()) {
						return;
					}

					player.setDuelStatus(4);
					if (other.getDuelStatus() == 4 && player.getDuelStatus() == 4) {
						player.getTradeAndDuel().startDuel();
						other.getTradeAndDuel().startDuel();
						other.setDuelCount(4);
						player.setDuelCount(4);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								player.duelForceChatCount--;
								player.forcedChat("" + player.duelForceChatCount + "", false, false);
								if (player.duelForceChatCount == 0) {
									container.stop();
								}
							}

							@Override
							public void stop() {
								player.forcedChat("FIGHT!", false, false);
								player.duelForceChatCount = 4;
								player.resetDamageTaken();
								player.setDuelCount(0);
							}
						}, 2);

						CycleEventHandler.getSingleton().addEvent(other, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								other.duelForceChatCount--;
								other.forcedChat("" + other.duelForceChatCount + "", false, false);
								if (other.duelForceChatCount == 0) {
									container.stop();
								}
							}

							@Override
							public void stop() {
								other.forcedChat("FIGHT!", false, false);
								other.duelForceChatCount = 4;
								other.resetDamageTaken();
								other.setDuelCount(0);
							}
						}, 2);

					} else {
						player.getPA().sendFrame126("Waiting for other player...", 6571);
						other.getPA().sendFrame126("Other player has accepted", 6571);
					}
				} else {
					Player o = player.getTradeAndDuel().getPartnerDuel();
					player.getTradeAndDuel().declineDuel(false);
					if (o != null) {
						o.getTradeAndDuel().declineDuel(false);
					}
					player.playerAssistant.sendMessage("You can't stake out of Duel Arena.");
				}
				break;

			// Activate special attack on Summoning orb.
			/*
			 * case 19141: Wolpertinger.specialAttackRequirements(player); break;
			 *
			 * // Dismiss on Summoning orb. case 19142:
			 * //Pet.dismissFamiliar(player); break;
			 *
			 * case 19145: Pet.callFamiliar(player); break;
			 */

			case 152:
				if (player.getDoingAgility()) {
					return;
				}
				if (player.resting) {
					AgilityAssistant.stopResting(player);
					return;
				}
				if (player.runModeOn) {
					player.runModeOn = false;
					player.getPA().sendFrame36(173, 0, false);
				} else {
					player.runModeOn = true;
					player.getPA().sendFrame36(173, 1, false);
				}
				AgilityAssistant.updateRunEnergyInterface(player);
				break;

			// Resting
			case 153:
				if (player.getDoingAgility()) {
					return;
				}
				if (!player.resting) {
					AgilityAssistant.startResting(player);
				} else {
					AgilityAssistant.stopResting(player);
				}
				break;
			case 9154:
				LogOutUpdate.manualLogOut(player);
				break;

			case 89176:
				RegularPrayer.activatePrayer(player, 26);
				break;

			case 89178:
				RegularPrayer.activatePrayer(player, 27);
				break;

			case 89180:
				RegularPrayer.activatePrayer(player, 28);
				break;
			case 21233:
				// thick skin
				RegularPrayer.activatePrayer(player, 0);
				break;
			case 21234:
				// burst of str
				RegularPrayer.activatePrayer(player, 1);
				break;
			case 21235:
				// charity of thought
				RegularPrayer.activatePrayer(player, 2);
				break;
			case 70080:
				// range
				RegularPrayer.activatePrayer(player, 3);
				break;
			case 70082:
				// mage
				RegularPrayer.activatePrayer(player, 4);
				break;
			case 21236:
				// rockskin
				RegularPrayer.activatePrayer(player, 5);
				break;
			case 21237:
				// super human
				RegularPrayer.activatePrayer(player, 6);
				break;
			case 21238:
				// improved reflexes
				RegularPrayer.activatePrayer(player, 7);
				break;
			case 21239:
				// hawk eye
				RegularPrayer.activatePrayer(player, 8);
				break;
			case 21240:
				RegularPrayer.activatePrayer(player, 9);
				break;
			case 21241:
				RegularPrayer.activatePrayer(player, 10);
				break;
			case 70084:
				// 26 range
				RegularPrayer.activatePrayer(player, 11);
				break;
			case 70086:
				// 27 mage
				RegularPrayer.activatePrayer(player, 12);
				break;
			case 21242:
				// steel skin
				RegularPrayer.activatePrayer(player, 13);
				break;
			case 21243:
				// ultimate str
				RegularPrayer.activatePrayer(player, 14);
				break;
			case 21244:
				// incredible reflex
				RegularPrayer.activatePrayer(player, 15);
				break;
			case 21245:
				// protect from magic
				RegularPrayer.activatePrayer(player, 16);
				break;
			case 21246:
				// protect from range
				RegularPrayer.activatePrayer(player, 17);
				break;
			case 21247:
				// protect from melee
				RegularPrayer.activatePrayer(player, 18);
				break;
			case 70088:
				// 44 range
				RegularPrayer.activatePrayer(player, 19);
				break;
			case 70090:
				// 45 mystic
				RegularPrayer.activatePrayer(player, 20);
				break;
			case 2171:
				// retribution.
				RegularPrayer.activatePrayer(player, 21);
				break;
			case 2172:
				// redem
				RegularPrayer.activatePrayer(player, 22);
				break;
			case 2173:
				// smite
				RegularPrayer.activatePrayer(player, 23);
				break;
			case 70092:
				// chiv
				RegularPrayer.activatePrayer(player, 24);
				break;
			case 70094:
				// piety
				RegularPrayer.activatePrayer(player, 25);
				break;

			// Trade accept button first screen.
			case 13092:
				player.getTradeAndDuel().acceptFirstTradeScreen();
				break;
			case 13218:
				player.getTradeAndDuel().acceptSecondTradeScreen();
				break;
			case 74176:
				if (!player.mouseButton) {
					player.mouseButton = true;
					player.getPA().sendFrame36(500, 1, false);
					player.getPA().sendFrame36(170, 1, false);
				} else if (player.mouseButton) {
					player.mouseButton = false;
					player.getPA().sendFrame36(500, 0, false);
					player.getPA().sendFrame36(170, 0, false);
				}
				break;
			case 3189:
				player.splitChat = !player.splitChat;
				InterfaceAssistant.splitPrivateChat(player);
				break;

			// Chat effect
			case 3147:
				if (!player.chatEffects) {
					player.chatEffects = true;
					InterfaceAssistant.chatEffectOn(player);
				} else {
					player.chatEffects = false;
					InterfaceAssistant.chatEffectOff(player);
				}
				break;
			case 48176:
				if (!player.acceptAid) {
					player.acceptAid = true;
					player.getPA().sendFrame36(503, 1, false);
					player.getPA().sendFrame36(427, 1, false);
				} else {
					player.acceptAid = false;
					player.getPA().sendFrame36(503, 0, false);
					player.getPA().sendFrame36(427, 0, false);
				}
				break;
			case 74201:
				// brightness1
				player.getPA().sendFrame36(505, 1, false);
				player.getPA().sendFrame36(506, 0, false);
				player.getPA().sendFrame36(507, 0, false);
				player.getPA().sendFrame36(508, 0, false);
				player.getPA().sendFrame36(166, 1, false);
				break;
			case 74203:
				// brightness2
				player.getPA().sendFrame36(505, 0, false);
				player.getPA().sendFrame36(506, 1, false);
				player.getPA().sendFrame36(507, 0, false);
				player.getPA().sendFrame36(508, 0, false);
				player.getPA().sendFrame36(166, 2, false);
				break;
			case 74204:
				// brightness3
				player.getPA().sendFrame36(505, 0, false);
				player.getPA().sendFrame36(506, 0, false);
				player.getPA().sendFrame36(507, 1, false);
				player.getPA().sendFrame36(508, 0, false);
				player.getPA().sendFrame36(166, 3, false);
				break;
			case 74205:
				// brightness4
				player.getPA().sendFrame36(505, 0, false);
				player.getPA().sendFrame36(506, 0, false);
				player.getPA().sendFrame36(507, 0, false);
				player.getPA().sendFrame36(508, 1, false);
				player.getPA().sendFrame36(166, 4, false);
				break;
			case 74206:
				// area1
				player.getPA().sendFrame36(509, 1, false);
				player.getPA().sendFrame36(510, 0, false);
				player.getPA().sendFrame36(511, 0, false);
				player.getPA().sendFrame36(512, 0, false);
				break;
			case 74207:
				// area2
				player.getPA().sendFrame36(509, 0, false);
				player.getPA().sendFrame36(510, 1, false);
				player.getPA().sendFrame36(511, 0, false);
				player.getPA().sendFrame36(512, 0, false);
				break;
			case 74208:
				// area3
				player.getPA().sendFrame36(509, 0, false);
				player.getPA().sendFrame36(510, 0, false);
				player.getPA().sendFrame36(511, 1, false);
				player.getPA().sendFrame36(512, 0, false);
				break;
			case 74209:
				// area4
				player.getPA().sendFrame36(509, 0, false);
				player.getPA().sendFrame36(510, 0, false);
				player.getPA().sendFrame36(511, 0, false);
				player.getPA().sendFrame36(512, 1, false);
				break;

			/* Emote */

			case 168:
				// Yes
				Movement.stopMovement(player);
				player.startAnimation(855);
				break;
			case 169:
				Movement.stopMovement(player);
				player.startAnimation(856);
				break;
			case 162:
				Movement.stopMovement(player);
				player.startAnimation(857);
				break;
			case 164:
				player.startAnimation(player.playerEquipment[ServerConstants.LEG_SLOT] == 10396 ? 5312 : 858);
				break;
			case 165:
				Movement.stopMovement(player);
				player.startAnimation(player.playerEquipment[ServerConstants.HEAD_SLOT] == 10392 ? 5315 : 859);
				break;
			case 161:
				Movement.stopMovement(player);
				player.startAnimation(860);
				break;
			case 170:
				Movement.stopMovement(player);
				player.startAnimation(861);
				break;
			case 171:
				Movement.stopMovement(player);
				player.startAnimation(862);
				break;
			case 163:
				Movement.stopMovement(player);
				player.startAnimation(863);
				break;
			case 167:
				Movement.stopMovement(player);
				player.startAnimation(864);
				break;
			case 172:
				Movement.stopMovement(player);
				player.startAnimation(865);
				break;
			case 166:
				Movement.stopMovement(player);
				player.startAnimation(
						ItemAssistant.hasItemEquippedSlot(player, 10394, ServerConstants.LEG_SLOT) ? 5316 : 866);
				break;
			case 52050:
				Movement.stopMovement(player);
				player.startAnimation(2105);
				break;
			case 52051:
				Movement.stopMovement(player);
				player.startAnimation(2106);
				break;
			case 52052:
				Movement.stopMovement(player);
				player.startAnimation(2107);
				break;
			case 52053:
				Movement.stopMovement(player);
				player.startAnimation(2108);
				break;
			case 52054:
				Movement.stopMovement(player);
				player.startAnimation(2109);
				break;
			case 52055:
				Movement.stopMovement(player);
				player.startAnimation(2110);
				break;
			case 52056:
				Movement.stopMovement(player);
				if (player.playerEquipment[ServerConstants.HEAD_SLOT] == 10398) {
					player.startAnimation(5313);
					player.gfx0(967);
				} else {
					player.startAnimation(2111);
				}
				break;
			case 52057:
				Movement.stopMovement(player);
				player.startAnimation(2112);
				break;
			case 52058:
				Movement.stopMovement(player);
				player.startAnimation(2113);
				break;
			case 43092:
				player.startAnimation(0x558);
				player.gfx0(574);
				Movement.stopMovement(player);
				break;
			case 2155:
				Movement.stopMovement(player);
				player.startAnimation(0x46B);
				break;
			case 25103:
				Movement.stopMovement(player);
				player.startAnimation(0x46A);
				break;
			case 25106:
				Movement.stopMovement(player);
				player.startAnimation(0x469);
				break;
			case 2154:
				Movement.stopMovement(player);
				player.startAnimation(0x468);
				break;
			case 52071:
				Movement.stopMovement(player);
				player.startAnimation(0x84F);
				break;
			case 52072:
				Movement.stopMovement(player);
				player.startAnimation(0x850);
				break;
			case 59062:
				Movement.stopMovement(player);
				player.startAnimation(2836);
				break;
			case 72032:
				Movement.stopMovement(player);
				player.startAnimation(3544);
				break;
			case 72033:
				Movement.stopMovement(player);
				player.startAnimation(3543);
				break;
			case 72254:
				Movement.stopMovement(player);
				player.startAnimation(6111);
				break;

			case 95080:
				player.getPA().sendMessage("This emote is currently unavailable.");
				break;

			case 95081:
				player.startAnimation(4278);
				break;

			case 95082:

				player.startAnimation(4280);
				break;

			case 95083:
				player.startAnimation(4275);
				break;

			case 81124:
				player.getPA().sendMessage("This emote is currently unavailable.");
				break;

			// Crazy dance
			case 95089:
				if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR)) {
					DonatorContent.crazyDanceEmote(player);
				}
				break;

			// Smooth dance
			case 95088:
				if (DonatorContent.canUseFeature(player, DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR)) {
					DonatorContent.smoothDanceEmote(player);
				}
				break;

			// Vengeance
			case 118098:
				if (!player.spellBook.equals("LUNAR")) {
					PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDateAndTime());
					PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", abusing: Vengeance");
					return;
				}
				Combat.castVengeance(player);
				break;

			case 94197:
				handleSkillCape(player);
				break;

			case 34170:
				player.rawBeefChosen = true;
				Fletching.attemptData(player, 1, false);
				break;
			case 34169:
				player.rawBeefChosen = true;
				Fletching.attemptData(player, 5, false);
				break;
			case 34168:
				player.rawBeefChosen = true;
				Fletching.attemptData(player, 10, false);
				break;
			case 34167:
				player.rawBeefChosen = true;
				Fletching.attemptData(player, 28, false);
				break;
			case 34174:
				Fletching.attemptData(player, 1, true);
				break;
			case 34173:
				Fletching.attemptData(player, 5, true);
				break;
			case 34172:
				Fletching.attemptData(player, 10, true);
				break;
			case 34171:
				Fletching.attemptData(player, 28, true);
				break;
			case 34185:
				Fletching.attemptData(player, 1, 0);
				break;
			case 34184:
				Fletching.attemptData(player, 5, 0);
				break;
			case 34183:
				Fletching.attemptData(player, 10, 0);
				break;
			case 34182:
				Fletching.attemptData(player, 28, 0);
				break;
			case 34189:
				Fletching.attemptData(player, 1, 1);
				break;
			case 34188:
				Fletching.attemptData(player, 5, 1);
				break;
			case 34187:
				Fletching.attemptData(player, 10, 1);
				break;
			case 34186:
				Fletching.attemptData(player, 28, 1);
				break;
			case 34193:
				Fletching.attemptData(player, 1, 2);
				break;
			case 34192:
				Fletching.attemptData(player, 5, 2);
				break;
			case 34191:
				Fletching.attemptData(player, 10, 2);
				break;
			case 34190:
				Fletching.attemptData(player, 28, 2);
				break;

			case 53152:
				Cooking.getAmount(player, 1);
				break;
			case 53151:
				Cooking.getAmount(player, 5);
				break;
			case 53150:
				Cooking.getAmount(player, 28);
				break;
			case 53149:
				Cooking.getAmount(player, 28);
				break;
			default:
				Plugin.execute("click_button_" + buttonId, player);
				break;
		}

	}

	/**
	 * Skill cape emotes.
	 *
	 * @param player The player.
	 */
	public static void handleSkillCape(Player player) {
		if (Combat.inCombatAlert(player)) {
			return;
		}
		boolean canDoEmote = false;
		for (SkillCapes data : SkillCapes.values()) {
			boolean hasCape = player.playerEquipment[ServerConstants.CAPE_SLOT] == data.getUntrimmedId() || player.playerEquipment[ServerConstants.CAPE_SLOT] == data.getTrimmedId();
			if (!hasCape) {
				continue;
			}
			if (hasCape) {
				player.startAnimation(data.getAnimation());
				player.gfx0(data.getGraphic());
				player.doingActionEvent(data.getDuration());
				canDoEmote = true;
				return;
			}
		}
		// Max cape / Completionist cape.
		if (Skilling.hasMaxCapeWorn(player)) {
			player.startAnimation(7121);
			player.gfx0(1286);
			player.doingActionEvent(9);
			canDoEmote = true;
		}
		if (!canDoEmote) {
			player.getPA().sendMessage("You need a skillcape to perform this emote.");
		}
		else {
			Achievements.checkCompletionSingle(player, 1042);
		}

	}

}
