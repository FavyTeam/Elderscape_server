package game.npc.clicknpc;

import core.GameType;
import core.Plugin;
import game.content.achievement.Achievements;
import game.content.bank.Bank;
import game.content.dialogue.DialogueChain;
import game.content.dialogueold.DialogueHandler.FacialAnimation;
import game.content.interfaces.NpcDoubleItemsInterface;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.minigame.lottery.Lottery;
import game.content.miscellaneous.PvpTask;
import game.content.miscellaneous.Teleport;
import game.content.packet.preeoc.ClickNpcPreEoc;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.skilling.fishing.FishingOld;
import game.content.skilling.thieving.PickPocket;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.item.PotionCombining;
import game.item.UntradeableRepairing;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.npc.pet.Pet;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Second click on NPC interactions.
 *
 * @author MGT Madness, created on 18-01-2013.
 */
public class SecondClickNpc {

	/**
	 * Second click on NPC.
	 *
	 * @param player The associated player.
	 * @param npcType The NPC identity.
	 */
	public static void secondClickNpc(Player player, int npcType) {

		player.resetNpcIdToFollow();
		player.setClickNpcType(0);

		Npc npc = NpcHandler.npcs[player.getNpcClickIndex()];
		NpcHandler.facePlayer(player, npc);
		player.turnPlayerTo(NpcHandler.npcs[player.getNpcClickIndex()].getX(), NpcHandler.npcs[player.getNpcClickIndex()].getY());

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
		if (ClickNpcPreEoc.secondClickNpcPreEocOnly(player, npc)) {
			player.setNpcClickIndex(0);
			return;
		}
		if (secondClickNpcOsrs(player, npc)) {
			player.setNpcClickIndex(0);
			return;
		}
		player.setNpcClickIndex(0);
	}

	private static boolean secondClickNpcOsrs(Player player, Npc npc) {
		if (!GameType.isOsrs()) {
			return false;
		}
		int npcType = npc.npcType;
		final NpcDefinition definition = NpcDefinition.getDefinitions()[npcType];

		// Cats
		if (npcType >= 5590 && npcType <= 5604) {
			player.getDH().sendDialogues(647);
			return true;
		}
		if (Pet.petMetamorphosis(player, npc, 2)) {
			return true;
		}
		if (Pet.pickUpPetClick(player, npc.npcType, "SECOND CLICK")) {
			return true;
		}
		switch (npcType) {
			case 5832:
				NpcDefinition farmer = NpcDefinition.DEFINITIONS[5832];
				player.setDialogueChainAndStart(new DialogueChain().npc(farmer, FacialAnimation.CALM_1, "Use a farming item on me and I'll happily note it."));
				break;

			// Gambler
			case NpcDoubleItemsInterface.NPC_ID :
				NpcDoubleItemsInterface.interactWithNpc(player, definition, "SECOND CLICK");
				break;

			case 1445:
				player.getPA().movePlayer(2801, 2706, 0);
				break;
			//Gnome Glider.
			case 1446:
				player.setActionIdUsed(2650);
				player.getPA().displayInterface(802);
				//player.getPA().sendFrame36(153, 0, false);
				break;
			case 7456:
				UntradeableRepairing.displayRepairableItemInterface(player);
				break;

			// Lottery/Durial
			case 11057:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot participate in the lottery.");
					return true;
				}
				Lottery.buyLotteryTicketsDialogueOption(player);
				break;

			// Nurse Tafani
			case 3343:
				player.getShops().openShop(60);
				break;

			// Bob
			case 505:
				BarrowsRepair.repair(player);
				break;
			// Party pete
			case 5792:
				player.getShops().openShop(83);
				break;
			// Cleaner at wilderness resource area.
			case 2901:
				int free = ItemAssistant.getFreeInventorySlots(player);
				if (free > 10) {
					free = 10;
				}
				ItemAssistant.addItem(player, 453, free);
				player.getPA().sendMessage("The cleaner hands you " + free + " coal.");
				break;
			// Pvp task master.
			case 315:
				if (GameMode.getGameModeContains(player, "IRON MAN")) {
					player.getDH().sendStatement("Ironmen cannot speak to the Pvp task master.");
					return true;
				}
				PvpTask.obtainTask(player);
				break;
			// Horvik, melee.
			case 535:
				player.getShops().openShop(3);
				break;
			// Vannaka, normal-task.
			// Nieve.
			case 6797:
			case 403:
				Slayer.giveTask(player, false);
				break;
			// Shopkeeper at Edgeville, Cape shop.
			case 512:
				player.getShops().openShop(52);
				break;

			case 5449: //decanting
				PotionCombining.decantAllPotions(player);
				player.getDH().sendDialogues(617);
				break;
			// Ajjat.
			case 4288:
				player.getShops().openShop(72);
				break;

			case 3078:
			case 3079:
			case 3080:
			case 3081:
			case 3082:
			case 3083:
			case 3084:
			case 3085:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.HUMAN);
				break;

			case 3086:
			case 3087:
			case 3088:
			case 3089:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.FARMER);
				break;

			case 3103:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.KHARID_WARRIOR);
				break;

			case 3094:
			case 3010:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.GUARD);
				break;

			case 3104:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.PALADIN);
				break;

			case 3106:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.HERO);
				break;

			case 3108:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.KNIGHT_OF_ARDOUGNE);
				break;

			case 3257:
				PickPocket.pickPocket(player, PickPocket.PickPocketData.MASTER_FARMER);
				break;

			// Aubury.
			case 637:
				Teleport.startTeleport(player, 2909 + Misc.random(3), 4830 + Misc.random(4), 0, "MODERN");
				break;

			case 3913:
				FishingOld.startFishing(player, FishingOld.RAW_TROUT);
				break;

			case 3914:
				FishingOld.startFishing(player, FishingOld.RAW_TUNA);
				break;

			// Shop keeper at Entrana, decant.
			case 513:
				PotionCombining.decantAllPotions(player);
				break;

			// Void Knight, Blood money.
			case 1758:
				Achievements.checkCompletionSingle(player, 1017);
				player.getShops().openShop(60);
				break;


			// Sigmund the Merchant.
			case 1282:
				player.getShops().openShop(54);
				break;

			// Mage of Zamorak.
			case 2580:
				player.getDH().sendDialogues(225);
				break;

			// Teleporter
			case 4397:
				if (player.lastTeleport.isEmpty()) {
					player.getPA().sendMessage("You don't have a previous destination to teleport to.");
					return true;
				}
				String[] teleport = player.lastTeleport.split(" ");
				Teleport.spellTeleport(player, Integer.parseInt(teleport[0]), Integer.parseInt(teleport[1]), Integer.parseInt(teleport[2]), true);
				break;

			// Thessalia.
			case 599:
				if (ItemAssistant.hasEquipment(player)) {

					player.playerAssistant.sendMessage("Please remove all your equipment before using this.");
					return true;
				}
				player.getPA().displayInterface(3559);
				player.canChangeAppearance = true;
				break;

			case 494: // Banker.
			case 1600: // Gundai.
			case 2182: // TzHaar-Ket-Zuh
			case 397: // Banker
			case 395: // Banker
			case 396: // Banker
				Skilling.stopAllSkilling(player);
				player.setUsingBankSearch(false);
				Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
				break;

			//TzHaar-Hur-Tel, Tzhaar shop.
			case 2183:
				player.getShops().openShop(66);
				break;
			default:
				if (Plugin.execute("second_click_npc_" + npcType, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
		return false;
	}
}
