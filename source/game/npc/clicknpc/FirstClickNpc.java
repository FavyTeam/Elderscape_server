package game.npc.clicknpc;


import core.GameType;
import core.Plugin;
import core.ServerConstants;
import game.container.ItemContainer;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.minigame.height_manager.NoAvailableHeightException;
import game.content.minigame.single_minigame.zulrah.ZulrahSinglePlayerMinigame;
import game.content.minigame.single_minigame.zulrah.ZulrahSinglePlayerMinigameFactory;
import game.content.miscellaneous.TeleportInterface;
import game.content.packet.preeoc.ClickNpcPreEoc;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.EntChopping;
import game.content.skilling.Skilling;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fishing.FishingOld;
import game.content.skilling.hunter.HunterStyle;
import game.content.starter.GameMode;
import game.entity.Entity;
import game.entity.MovementState;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.impl.vorkath.Vorkath;
import game.npc.pet.BossPetDrops;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.PlayerAssistant;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * First click on NPC interactions.
 *
 * @author MGT Madness, created on 18-01-2013.
 */
public class FirstClickNpc {
	/**
	 * First click on NPC.
	 *
	 * @param player The associated player.
	 * @param npcId The NPC identity.
	 */
	public static void firstClickNpc(Player player, int npcId) {

		Npc npc = NpcHandler.npcs[player.getNpcClickIndex()];
		NpcHandler.facePlayer(player, npc);
		player.resetNpcIdToFollow();
		player.setClickNpcType(0);
		player.turnPlayerTo(npc.getX(), npc.getY());
		player.sendDebugMessage(String.format("first click npc = %s", npcId));


		// Has to be on a delayed tick or else it will so awkward rotations when talking to an npc when running to it.
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void stop() {
				player.resetFaceUpdate();
			}
		}, 2);


		if (ClickNpcPreEoc.firstClickNpcPreEoc(player, npc)) {
			player.setNpcClickIndex(0);
			return;
		}
		if (firstClickNpcOsrs(player, npc)) {
			player.setNpcClickIndex(0);
			return;
		}
		player.setNpcClickIndex(0);
	}

	private static boolean firstClickNpcOsrs(Player player, Npc npc) {
		if (!GameType.isOsrs()) {
			return false;
		}
		final NpcDefinition definition = NpcDefinition.getDefinitions()[npc.npcType];
		int count = 0;
		for (Skilling.SkillCapeMasterData master : Skilling.SkillCapeMasterData.values()) {
			if (npc.npcType == master.getNpcId() && npc.npcType != 5832) {
				player.skillCapeMasterSkill = count;
				player.getDH().sendDialogues(228);
				if (count == ServerConstants.HERBLORE) {
					player.skillCapeMasterExpression = 9760;
				} else {
					player.skillCapeMasterExpression = 9850;
				}
				return true;
			}
			count++;
		}
		if (EntChopping.isEntStump(player, npc.npcType)) {
			return true;
		}

		if (Pet.pickUpPetClick(player, npc.npcType, "FIRST CLICK")) {
			return true;
		}
		// Guide npc
		if (npc.npcType == 306) {
			Achievements.checkCompletionSingle(player, 1001);
			player.getDH().sendDialogues(217);
			player.setNpcClickIndex(0);
			return true;
		}


		switch (npc.npcType) {
			
			// Gambler
			case NpcDoubleItemsInterface.NPC_ID :
				NpcDoubleItemsInterface.interactWithNpc(player, definition, "FIRST CLICK");
				break;
			case 8193:
				if (!player.isSuperDonator()) {
					player.getPA().sendMessage("You need to be at least a " + DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.SUPER_DONATOR) + "Super Donator to access this!");
				}
				else {
					player.getShops().openShop(21);
				}
				break;
			case 8059:
				if (npc instanceof Vorkath) {
					Vorkath vorkath = (Vorkath) npc;

					vorkath.poke(player);
				}
				break;
			case 8131:
			case 1617:
				ItemContainer items = npc.npcType == 8131 ? player.getVorkathLostItems() : player.getZulrahLostItems();

				if (items.isEmpty()) {
					if (npc.npcType == 1617) {
						player.setDialogueChain(new DialogueChain().npc(definition, DialogueHandler.FacialAnimation.DEFAULT, "Would you like to fight Zulrah?").option((p, option) -> {
							if (option == 1) {
								try {
									player.getPA().closeInterfaces(true);

									final ZulrahSinglePlayerMinigame minigame = ZulrahSinglePlayerMinigameFactory.create(player);

									player.setMovementState(MovementState.DISABLED);
									player.doingActionEvent(10);

									player.setDialogueChain(new DialogueChain().statement("The priestess rows you to Zulrah's shrine,", "then hurriedly paddles away.")).start(player);

									player.getPA().sendFadingScreen("", PlayerAssistant.FadingScreenState.FADE_IN, (byte) 5, 1);

									player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
										@Override
										public void execute(CycleEventContainer<Entity> container) {
											container.stop();

											minigame.start();
											player.setMovementState(MovementState.WALKABLE);
										}

										@Override
										public void stop() {

										}
									}, 10);
								} catch (NoAvailableHeightException nvh) {
									player.getPA().sendMessage("Too many players in this area, please wait until a few leave!");
								}
							}
						}, "Teleport to Zulrah", "Yes", "No")).start(player);
					} else {
						player.setDialogueChain(new DialogueChain().npc(definition, DialogueHandler.FacialAnimation.DEFAULT, "You have no items to claim.")).start(player);
					}
				} else {
					final int cost = ServerConstants.getItemsLostReclaimCost();

					final int currency = ServerConstants.getMainCurrencyId();

					if (cost > 0) {
						player.setDialogueChainAndStart(new DialogueChain().option((first, second) -> {
							if (second == 1) {
								if (!ItemAssistant.hasItemAmountInInventory(first, currency, cost)) {
									first.getPA().closeInterfaces(true);
									first.getPA().sendMessage("You don't have enough " + ServerConstants.getMainCurrencyName() + " to cover the cost.");
									return;
								}
								ItemAssistant.deleteItemFromInventory(first, currency, cost);
								ItemAssistant.addAnywhere(first, items);
								items.clear();
								first.setDialogueChain(new DialogueChain().npc(definition, DialogueHandler.FacialAnimation.DEFAULT,
										"Your lost items have been sent to your inventory, bank, or",
										"they are underneath you on the ground.")).start(first);
							} else {
								first.getPA().closeInterfaces(true);
							}
						}, String.format("Do you want to pay %s to retrieve your items?", Misc.formatRunescapeStyle(cost)),
								String.format("Yes, pay %s to retrieve items.", Misc.formatRunescapeStyle(cost)),
								"No, I don't have that kind of cash."));

					} else {
						ItemAssistant.addAnywhere(player, items);
						items.clear();
						player.setDialogueChain(new DialogueChain().npc(definition, DialogueHandler.FacialAnimation.DEFAULT,
								"Your lost items have been sent to your inventory, bank, or",
								"they are underneath you on the ground.")).start(player);
					}
				}
				break;
			// Pet snakeling
			case 2130:
			case 2131:
			case 2132:
				player.getDH().sendDialogues(702);
				break;

			// Scorpia's offspring
			case 5561:
				player.getDH().sendDialogues(657);
				break;

			// Adam, the Ironman npc
			case 311:
				if (!GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Adam only speaks with the Ironmen bloodline.");
					return true;
				}
				player.getDH().sendDialogues(656);
				break;

			// Kamfreena.
			case 2461:
				if (ItemAssistant.hasItemAmountInInventory(player, 8851, 100)) {
					player.getDH().sendDialogues(251);
				} else {
					player.getDH().sendDialogues(250);
				}
				break;

			// Tzhaar-Mej-Jal, exchange fire cape
			case 2180:
				int chance = ItemAssistant.getItemAmount(player, 6570);
				if (chance == 0) {
					player.getPA().sendMessage("You need at least 1 fire cape to get a chance at the Jad pet.");
					return true;
				}
				// Cannot use if (Misc.random(chance, BossPetDrops.JAD_PET_EXCHANGE_CHANCE) == BossPetDrops.JAD_PET_EXCHANGE_CHANCE)
				// because it will give different results than using a for loop.
				for (int index = 0; index < chance; index++) {
					ItemAssistant.deleteItemFromInventory(player, 6570, 1);
					if (Misc.hasOneOutOf(GameMode.getDropRate(player, BossPetDrops.JAD_PET_EXCHANGE_CHANCE))) {
						BossPetDrops.awardBoss(player, 13225, 13225, 0, "trading Fire capes");
						return true;
					}
				}
				player.getPA().sendMessage("You traded in " + chance + " Fire capes and did not receive the pet.");
				break;

			// Ajjat
			case 2460:
				player.getShops().openShop(14);
				break;

			// A'abla
			case 3341:
				player.getDH().sendDialogues(641);
				break;
			// Halloween dealer
			case 11086:
				player.getShops().openShop(85);
				break;
			case 679: // Tradesman
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendDialogues(656);
					return true;
				}
				player.getDH().sendDialogues(620);
				break;
			case 5449: //bob potion decanting
				player.getDH().sendDialogues(615);
				break;
			case 7456: //Untradeable repairing
				player.getDH().sendDialogues(655);
				break;
			case 2577: //abbot langley
				player.getDH().sendDialogues(600);
				break;
			case 1177: //stalls npc
				player.getDH().sendNpcChat("Psst. Use a stolen item on me and I'll happily", "give you some gold for it.", DialogueHandler.FacialAnimation.CALM_2.getAnimationId());
				break;
			case 517: //general store
				player.getShops().openShop(8);
				break;

			// Cave kraken pet
			case 6640:
				player.getDH().sendDialogues(365);
				break;
			// Vet'ion Jr.
			case 5537:
			case 5536:
				player.getDH().sendDialogues(478);
				break;
			// Jad pet
			case 5892:
				player.getDH().sendDialogues(464);
				break;
			case 964: // Hellpuppy
				player.getDH().sendDialogues(460);
				break;
			// Nurse Tafani
			case 3343:
				player.edgePvpNurseUsedTime = System.currentTimeMillis();
				player.getDH().sendDialogues(455);
				QuickSetUp.heal(player, true, true);
				break;
			// Bloodhound pet
			case 6296:
				player.getDH().sendDialogues(450);
				break;

			// Party pete
			case 5792:
				player.getDH().sendDialogues(459);
				break;
			// Vote manager or skilling tokens (eco)
			case 7283:
				player.getShops().openShop(GameType.isOsrsPvp() ? 33 : 40);
				break;
			// Olmlet
			case 7520:
				player.getDH().sendDialogues(438);
				break;
			// Rocky
			case 7336:
				player.getDH().sendDialogues(435);
				break;
			// Pet zilyana
			case 6633:
				player.getDH().sendDialogues(389);
				break;
			// Pet general graardor.
			case 6632:
				player.getDH().sendDialogues(355);
				break;
			// Prince black dragon
			case 6636:
				player.getDH().sendDialogues(392);
				break;
			// Pet k'ril tsutsaroth
			case 6634:
				player.getDH().sendDialogues(358);
				break;
			// Chaos elemental
			case 2055:
				player.getDH().sendDialogues(329);
				break;
			// Kree arra.
			case 6631:
				player.getDH().sendDialogues(370);
				break;
			// Dagannoth Rex.
			case 6630:
				player.getDH().sendDialogues(340);
				break;
			// Dagannoth Prime.
			case 6629:
				player.getDH().sendDialogues(333);
				break;
			// Dagannoth Supreme.
			case 6628:
				player.getDH().sendDialogues(348);
				break;
			// Venenatis spiderling.
			case 5557:
				player.getDH().sendDialogues(407);
				break;
			// Beaver
			case 6717:
				player.getDH().sendDialogues(418);
				break;
			// Heron
			case 6722:
				player.getDH().sendDialogues(420);
				break;
			// Rock golem
			case 7439:
			case 7440:
			case 7441:
			case 7442:
			case 7443:
			case 7444:
			case 7445:
			case 7446:
			case 7447:
			case 7448:
			case 7449:
			case 7450:
				player.getDH().sendDialogues(422);
				break;

			// Tangleroot
			case 7335:
				player.getDH().sendDialogues(431);
				break;

			// Giant squirrel
			case 7334:
				player.getDH().sendDialogues(427);
				break;

			//  Rift guardian
			case 7337:
			case 7338:
			case 7339:
			case 7340:
			case 7341:
			case 7342:
			case 7343:
			case 7344:
			case 7345:
			case 7346:
			case 7347:
			case 7348:
			case 7349:
			case 7350:
				if (!GameType.isPreEoc()) {
					player.getDH().sendDialogues(433);
				}
				break;
			// Callisto cub
			case 5558:
				player.getDH().sendDialogues(294);
				break;
			// Fairy.
			case 520:
				PlayerTitle.displayInterface(player);
				break;
			// Cleaner at wilderness resource area.
			case 2901:
				player.getDH().sendDialogues(443);
				break;

			// Lottery npc/Durial321
			case 11057:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot participate in the lottery.");
					return true;
				}
				player.getDH().sendDialogues(593);
				break;

			// Mandrith
			case 6599:
				player.getPA().sendMessage("Mandrith does not feel like talking.");
				break;

			// Cow31337Killer.
			case 4420:
				player.getPA().displayInterface(24950);
				break;
			// Pvp task master.
			case 315:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot speak to the Pvp task master.");
					return true;
				}
				player.getDH().sendDialogues(265);
				break;

			// Adam.
			case 11257:
				player.getDH().sendDialogues(258);
				break;

			case 512:
				player.getDH().sendDialogues(261);
				break;
			case 7285:
				player.getShops().openShop(49);
				break;

			// Hatius Cosaintus.
			case 5523:
				player.getDH().sendDialogues(257);
				break;

			case 2800:
				if (System.currentTimeMillis() - player.flaxDelay <= 4000) {
					return true;
				}
				player.flaxDelay = System.currentTimeMillis();
				final Player finalPlayer = player;
				if (!ItemAssistant.hasItemInInventory(finalPlayer, 1735)) {
					finalPlayer.getPA().sendMessage("You need a pair of shears to shave the sheep.");
					return true;
				}
				player.startAnimation(893);
				CycleEventHandler.getSingleton().addEvent(finalPlayer, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@SuppressWarnings("unused")
					@Override
					public void stop() {
						if (npc == null) {
							return;
						}
						int oldX = npc.getX();
						int oldY = npc.getY();
						Pet.deletePet(npc);
						ItemAssistant.addItem(finalPlayer, 1737, 1);
						finalPlayer.getPA().sendFilterableMessage("You shear the sheep.");
						NpcHandler.spawnDefaultNpcSheep(2790, "Sheep", oldX, oldY, 0, "ROAM", 30, 2800);
					}
				}, 3);
				break;
			// Twiggy O'Korn.
			case 4042:
				player.getDH().sendDialogues(260);
				break;
			// Ellis.
			case 3231:
				LeatherCrafting.displayTanningInterface(player);
				break;

			// Abbot Langley.
			case 801:
				player.getDH().sendDialogues(253);
				break;

			// Mage of Zamorak.
			case 2580:
				player.getShops().openShop(47);
				break;

			// Jatix.
			case 1174:
				player.getShops().openShop(50);
				break;

			// Shop assistant.
			case 513:
				player.getShops().openShop(5);
				break;

			// Drogo dwarf.
			case 1048:
				player.getShops().openShop(59);
				break;

			// Kamfreena.
			case 4289:
				if (ItemAssistant.hasItemAmountInInventory(player, 8851, 100)) {
					player.getDH().sendDialogues(251);
				} else {
					player.getDH().sendDialogues(250);
				}
				break;

			// Hans.
			/*case 3077:
				Achievements.checkCompletionSingle(player, 1079);
				if (GameMode.getGameMode(player, "IRON MAN"))
				{
					Achievements.checkCompletionSingle(player, 1014);
				}
				break;*/

			// Vannaka.
			case 6797:
			case 403:
				player.getDH().sendDialogues(10);
				break;

			case 1645:
			case 1646:
			case 1647:
			case 1648:
			case 1649:
			case 1650:
			case 1651:
			case 1652:
			case 1653:
			case 1654:
				player.getHunterSkill().net(player, npc, HunterStyle.BUTTERFLY_NETTING);
				break;

			// Gundai.
			case 1600:
				Achievements.checkCompletionSingle(player, 1010);
				player.getDH().sendDialogues(222);
				break;

			// Martin the Master Gardener.
			case 5832:
				player.getShops().openShop(6);
				break;

			// Gnome
			case 6083:
				player.getDH().sendDialogues(191);
				break;

			//Sharks
			case 1506:
				FishingOld.startFishing(player, FishingOld.RAW_SHARK);
				break;

			// Shrimp.
			case 3913:
				FishingOld.startFishing(player, FishingOld.RAW_SHRIMP);
				break;

			// Lobster.
			case 3914:
				FishingOld.startFishing(player, FishingOld.RAW_LOBSTER);
				break;

			// Monk fish.
			case 635:
				FishingOld.startFishing(player, FishingOld.RAW_MONKFISH);
				break;

			// Karambwan.
			case 4712:
				FishingOld.startFishing(player, FishingOld.RAW_KARAMBWAN);
				break;


			// Dark crab.
			case 3915:
				FishingOld.startFishing(player, FishingOld.RAW_DARK_CRAB);
				break;

			// Anglerfish
			case 4082:
				FishingOld.startFishing(player, FishingOld.RAW_ANGLERFISH);
				break;

			// Horvik.
			case 535:
				player.getDH().sendDialogues(216);
				break;

			// Void Knight.
			case 1758:
				player.getDH().sendDialogues(209);
				break;

			// Bob.
			case 505:
				player.getDH().sendDialogues(1);
				break;

			// Teleporter.
			case 4397:
				player.setActionIdUsed(4397);
				player.canUseTeleportInterface = true;
				TeleportInterface.displayInterface(player);
				break;

			// Make-over mage
			case 1306:
				player.getDH().sendDialogues(24);
				break;

			// Member Merchant.
			case 2328:
				player.getDH().sendDialogues(112);
				break;

			// Item Specialist Merchant.
			case 521:
				player.getDH().sendDialogues(114);
				break;

			// Melee Merchant Shop.
			case 705:
				player.getDH().sendDialogues(16);
				break;

			case 946:
				// Magician Merchant.
				player.getDH().sendDialogues(17);
				break;

			// Range Merchant.
			case 1861:
				player.getDH().sendDialogues(19);
				break;

			//Cosmetic Merchant.
			case 659:
				player.getDH().sendDialogues(14);
				break;

			// Bankers.
			case 494:
			case 6538:
			case 2182: // TzHaar-Ket-Zuh
				player.setUsingBankSearch(false);
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
				break;

			// Merchant.
			case 596:
				player.getShops().openShop(7);
				break;

			// Sir Prysin.
			case 5083:
				player.getShops().openShop(GameType.isOsrsPvp() ? 42 : 13);
				break;

			case 2108: //wise old man
				player.getShops().openShop(9);
				break;

			//TzHaar-Hur-Tel, Tzhaar shop.
			case 2183:
				player.getShops().openShop(66);
				break;

			default:
				if (Plugin.execute("first_click_npc_" + npc.npcType, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
		return false;
	}


}
