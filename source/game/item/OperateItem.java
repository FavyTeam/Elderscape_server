package game.item;

import core.GameType;
import core.Plugin;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.vsnpc.CombatNpc;
import game.content.degrading.DegradingManager;
import game.content.donator.DonatorTokenUse;
import game.content.donator.DonatorTokenUse.DonatorRankSpentData;
import game.content.godbook.BookPreaching;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.CompletionistCape;
import game.content.miscellaneous.MagicCapeSpellbookSwap;
import game.content.miscellaneous.Teleport;
import game.content.skilling.Slayer;
import game.player.Player;
import utility.Misc;

/**
 * Operate an item.
 *
 * @author MGT Madness, created on 22-10-2013.
 */
public class OperateItem {

	public static void applyOperate(Player player, int itemId, int slot) {
		if (!ItemAssistant.hasItemEquippedSlot(player, itemId, slot)) {
			return;
		}
		if (player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE) {
			Misc.printDontSave("[Operate item: " + itemId + "]");
		}
		if (player.doingAnAction()) {
			return;
		}

		if (MagicCapeSpellbookSwap.operatedMagicCape(player, itemId)) {
			return;
		}
		if (itemId == 2550) {
			player.getPA().sendMessage("You have " + player.recoilCharges + " recoil charges left.");
			return;
		}
		// Uncharged ethereum bracelet.
		if (itemId == 21817) {
			player.getPA().sendMessage("This bracelet is uncharged.");
			return;
		}
		// Charged ethereum bracelet.
		if (itemId == 21816) {
			BraceletOfEthereum.check(player);
			return;
		}
		// Dragonfire shield, Dragonfire ward
		if (itemId == 11284 || itemId == 22002) {
			if (player.getPlayerIdAttacking() > 0) {
				Combat.handleDfs(player);
			} else if (player.getNpcIdAttacking() > 0) {
				CombatNpc.handleDfsNPC(player);
			}
			return;
		}

		if (itemId == 12926) {
			Blowpipe.check(player);
			return;
		}
		if (Combat.inPVPAreaOrCombat(player)) {
			return;
		}
		if (DegradingManager.checkCharge(player, itemId)) {
			return;
		}
		if (ItemInteractionManager.handleOperateItem(player, itemId)) {
			return;
		}
		if (player.getWieldedWeapon() == itemId && !player.isExtremeDonator()) {
			player.getPA().sendMessage(DonatorTokenUse.getDonatorRankIcon(DonatorRankSpentData.EXTREME_DONATOR)
					+ "This is for Extreme Donators, help fund the server at ::donate");
			return;
		}
		if (BookPreaching.sendPreachOptions(player, itemId)) {
			return;
		}

		if (GameType.isOsrs() && Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, itemId, 0)) {
			player.startAnimation(Misc.arrayHasNumber(ServerConstants.ARMADYL_GODSWORDS_OSRS, itemId, 0, 1));
			player.gfx0(1211);
			return;
		}
		if (Combat.hasAbyssalTentacle(player, itemId)) {
			player.startAnimation(1658);
			return;
		}
		if (ItemAssistant.getItemName(itemId).equals("Dragon claws")) {
			player.startAnimation(7514);
			player.gfx0(1171);
			return;
		}

		if (ItemAssistant.getItemName(itemId).contains("Granite maul")) {
			player.startAnimation(1667);
			player.gfx100(340);
			return;
		}
		if (GameType.isPreEoc()) {
			switch (itemId) {
				case 20765: // classic cape
					player.gfx0(Misc.random(2) == 1 ? 1471 : 1466);
					return;
			}
		}

		switch (itemId) {

			// Completionist cape.
			case 14011:
				CompletionistCape.displayInterface(player);
				break;
			case 11804:// bgs
				player.startAnimation(7642);
				player.gfx0(1212);
				break;

			case 20370: // Bgs or
				player.startAnimation(7643);
				player.gfx0(1212);
				break;

			case 11806:// sgs
				player.startAnimation(7640);
				player.gfx0(1209);
				break;

			case 20372: // Sgs or
				player.startAnimation(7641);
				player.gfx0(1209);
				break;

			// Dragon warhammer
			case 13576:
				player.startAnimation(1378);
				player.gfx0(1292);
				break;

			case 11808:// zgs
				player.startAnimation(7638);
				player.gfx0(1210);
				break;

			case 20374: // Zgs or
				player.startAnimation(7639);
				player.gfx0(1210);
				break;

			case 1305:
				player.gfx100(248);
				player.startAnimation(1058);
				break;

			case 1249:
			case 11889:
			case 11824:
				player.startAnimation(1064);
				player.gfx100(253);
				break;

			case 3204:
				player.gfx100(1172);
				player.startAnimation(1203);
				break;

			case 4587:
			case 20000:
				player.gfx100(347);
				player.startAnimation(1872);
				break;

			case 1434:
				player.startAnimation(1060);
				player.gfx100(251);
				break;

			case 10887:
				player.gfx100(1027);
				player.startAnimation(5870);
				break;

			case 1215:
			case 1231:
			case 5680:
			case 5698:
				player.gfx100(252);
				player.startAnimation(1062);
				break;

			case 13271: // Abyssal dagger.
				player.startAnimation(1062);
				player.gfx0(1283);
				break;

			case 11838:
			case 12808: // Sara's blessed sword.
				player.startAnimation(itemId == 11838 ? 1132 : 1133);
				player.gfx100(1213);
				break;

			case 4151: // Abyssal whip
			case 12773: // Volcanic abyssal whip
			case 12774: // Frozen abyssal whip
			case 15_441:
			case 15_442:
			case 15_443:
			case 15_444:
				player.startAnimation(1658);
				break;

			// Dragon battleaxe
			case 1377:
				player.gfx0(246);
				player.forcedChat("Raarrrrrgggggghhhhhhh!", false, false);
				player.startAnimation(1056);
				break;
			default:
				if (Plugin.execute("operate_item_" + itemId, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}
	}

	public static void operateOption(Player player, int option, int itemId) {
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Operate item: " + itemId + "] [Operate option: " + option + "]");
		}
		if (!ItemAssistant.hasItemEquipped(player, itemId)) {
			return;
		}
		switch (itemId) {

			case 1704:
				player.getPA().sendMessage("You have no charges left.");
				return;
			case 1706:
			case 1708:
			case 1710:
			case 1712:
				int x = 3085 + Misc.random(3);
				int y = 3491 + Misc.random(5);
				int height = 0;
				if (option == 2) {
					x = 2917 + Misc.random(3);
					y = 3175 + Misc.random(2);
				} else if (option == 3) {
					x = 3104 + Misc.random(4);
					y = 3252 + Misc.random(1);
				} else if (option == 4) {
					x = 3290 + Misc.random(6);
					y = 3167 + Misc.random(1);
				}
				if (player.getHeight() == 4) {
					x = 3094;
					y = 3496;
					height = 4;
				}
				Teleport.startTeleport(player, x, y, height, "GLORY " + itemId + " " + "EQUIPMENT");
				break;

			case 21175:
			case 21173:
			case 21171:
			case 21169:
			case 21166:
				int posX = 0;
				int posY = 0;
				int H = 0;
				if (option == 2) {
					posX = 3233 + Misc.random(2);
					posY = 3635 + Misc.random(2);
				} else if (option == 3) {
					posX = 3041 + Misc.random(1);
					posY = 3649 + Misc.random(1);
				} else if (option == 4) {
					posX = 3029 + Misc.random(2);
					posY = 3840 + Misc.random(2);
				}
				Teleport.startTeleport(player, posX, posY, H,
						"BURNING_AMULET " + itemId + " " + "EQUIPMENT");
				break;
		}

		if (Misc.arrayHasNumber(ServerConstants.getSlayerHelms(), itemId) && option == 4) {
			Slayer.checkSlayerHelmOption(player);
			return;
		}
	}

}
