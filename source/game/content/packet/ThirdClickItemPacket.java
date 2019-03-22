package game.content.packet;

import core.GameType;
import core.Plugin;
import core.ServerConfiguration;
import game.container.impl.MoneyPouch;
import game.content.consumable.Potions;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.ItemCombining;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Wolpertinger;
import game.content.skilling.Slayer;
import game.content.skilling.summoning.Summoning;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 * <p>
 * Proper Streams
 */

public class ThirdClickItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {

		int itemId11 = player.getInStream().readSignedWordBigEndianA();
		int itemSlot = player.getInStream().readUnsignedWordA(); // Slot = 128 +
		// (slotNumber
		// * 256) //
		// Slot
		// starts at
		// 0 to 27.
		int itemId = player.getInStream().readSignedWordA();
		itemSlot = (itemSlot - 128) / 256;

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "itemId11: " + itemId11);
			PacketHandler.saveData(player.getPlayerName(), "itemSlot: " + itemSlot);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}

		if (!ItemAssistant.playerHasItem(player, itemId, 1, itemSlot)) {
			return;
		}

		if (player.isInTrade() || player.getTradeStatus() == 1
		    || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Third click item: " + itemId + "]");
		}
		if (player.doingAnAction()) {
			return;
		}

		if (player.getDead()) {
			return;
		}
		player.itemInventoryOptionId = itemId;
		if (Potions.isPotion(player, itemId)) {
			boolean isGuthixRest = false;
			String name = ItemAssistant.getItemName(itemId);
			if (name.contains("Guthix rest")) {
				isGuthixRest = true;
			}
			player.getPA().sendMessage("You empty the " + ItemAssistant.getItemName(itemId) + ".");
			ItemAssistant.deleteItemFromInventory(player, itemId, itemSlot, 1);
			ItemAssistant.addItemToInventory(player, isGuthixRest ? 1980 : 229, 1, itemSlot, true);
			return;
		}
		// Amulet of glory.
		if (itemId >= 1706 && itemId <= 1712) {
			player.getDH().sendDialogues(456);
			return;
		} else if (itemId == 1704) {
			player.playerAssistant.sendMessage("You have no charges left.");
		} else
		if (ItemCombining.isDismantlable(player, itemId, itemSlot, true)) {
			return;
		} else
		if (Summoning.summonFamiliar(player, itemId)) {
			return;
		}
		if (ItemInteractionManager.handleItemAction(player, itemId, 3)) {
			return;
		}
		switch (itemId) {
			case 13221:
				if (ItemAssistant.hasItemInInventory(player, 13221)) {
					ItemAssistant.deleteItemFromInventory(player, 13221, 1);
					ItemAssistant.addItem(player, 13222, 1);
				}
				break;
			case 13222:
				if (ItemAssistant.hasItemInInventory(player, 13222)) {
					ItemAssistant.deleteItemFromInventory(player, 13222, 1);
					ItemAssistant.addItem(player, 13221, 1);
				}
				break;
			case 995:
				MoneyPouch.addToPouch(player, itemSlot);
				break;

			// Quest cape
			case 9813:
				ItemAssistant.deleteItemFromInventory(player, 9813, 1);
				ItemAssistant.addItem(player, 13068, 1);
				break;

			// Quest cape (t)
			case 13068:
				ItemAssistant.deleteItemFromInventory(player, 13068, 1);
				ItemAssistant.addItem(player, 9813, 1);
				break;

			// Burning amulets
			case 21166:
			case 21169:
			case 21171:
			case 21173:
			case 21175:
				player.getDH().sendDialogues(640);
				break;
			case 21802: // Revs teleport
				Teleport.startTeleportAndDeleteItem(player, 3076 + Misc.random(2), 3650 + Misc.random(2), 0, "ARCEUUS",
				                                    21802);
				break;
			case 21817:
				BraceletOfEthereum.dismantle(player);
				break;

			// Toxic blowpipe (empty)
			case 12924:
				if (GameType.isOsrsEco()) {
					player.getDH().sendDialogues(645);// Blowpipe dismantle option
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;

			// Toxic blowpipe
			case 12926:
				Blowpipe.unload(player);
				break;
			// Ring of recoil break option
			case 2550:
				ItemAssistant.deleteItemFromInventory(player, 2550, itemSlot, 1);
				player.setRecoilCharges(40);
				player.getPA().sendMessage("Your ring of recoil has been destroyed.");
				break;
			// Rune pouch.
			case 12791:
				player.setActionIdUsed(8);
				RunePouch.runePouchItemClick(player, "EMPTY");
				break;

			// Wolpertinger pouch.
			case 12089:
				Wolpertinger.summonWolpertinger(player, true);
				break;
			case 11864:
			case 11865:
				Slayer.dismantleSlayerHelm(player, itemId);
				break;
			default:
				if (Plugin.execute("third_click_item_" + itemId, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}
				break;
		}

	}

}
