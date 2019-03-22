package game.content.packet;

import core.Plugin;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.degrading.DegradingManager;
import game.content.item.ItemInteractionManager;
import game.content.miscellaneous.Blowpipe;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.ItemCombining;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.MagicCapeSpellbookSwap;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.RunecrafterHat;
import game.content.skilling.Slayer;
import game.item.ItemAssistant;
import game.player.Player;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Item Click 2 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 * <p>
 * Proper Streams
 */

public class SecondClickItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		int itemId = player.getInStream().readSignedWordA();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
		}
		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}

		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Second click item: " + itemId + "]");
		}

		if (player.doingAnAction()) {
			return;
		}

		if (player.getDead()) {
			return;
		}

		if (!ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
			return;
		}

		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		if (RunecrafterHat.isRunecrafterHat(player, itemId)) {
			return;
		}

		if (MagicCapeSpellbookSwap.operatedMagicCape(player, itemId)) {
			return;
		}

		if (Misc.arrayHasNumber(ServerConstants.getSlayerHelms(), itemId)) {
			Slayer.checkSlayerHelmOption(player);
			return;
		}
		if (ItemCombining.isDismantlable(player, itemId, ItemAssistant.getItemSlot(player, itemId),
				false)) {
			return;
		}
		if (ItemInteractionManager.handleItemAction(player, itemId, 2)) {
			return;
		}
		if (DegradingManager.checkCharge(player, itemId)) {
			return;
		}
		switch (itemId) {
			case 7509:
				PlayerMiscContent.rockCake(player);
				break;

			// Uncharged ethereum bracelet.
			case 21817:
				player.getPA().sendMessage("This bracelet is uncharged.");
				break;

			// Charged ethereum bracelet.
			case 21816:
				BraceletOfEthereum.check(player);
				break;

			case 2572:
				player.getPA().sendMessage("Improves droprate by 1%.");
				break;
			case 12785:
				player.getPA().sendMessage("Improves droprate by 3%.");
				break;
			case 16004:
				ItemAssistant.addItem(player, 299, Misc.random(20, 60));
				player.getPA().sendMessage("You find some seeds.");
				break;

			// Toxic blowpipe.
			case 12926:
				Blowpipe.check(player);
				break;

			case 11941:
				LootingBag.withdrawLootingBag(player);
				break;
			case 19564:
				boolean hasRequirement = player.getWildernessKills(true) >= 150;
				player.getDH().sendDialogues(hasRequirement ? player.toggleSeedPod ? 717 : 716 : 715);
				break;
			default:
				if (Plugin.execute("second_click_item_" + itemId, player)) {
				} else {
					player.getPA().sendMessage("Nothing interesting happens.");
				}

		}

	}

}
