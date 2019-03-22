package game.content.packet;

import core.GameType;
import core.Plugin;
import game.content.combat.Combat;
import game.content.interfaces.AreaInterface;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.TradeAndDuel;
import game.content.skilling.Runecrafting;
import game.content.starter.GameMode;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import game.player.movement.Follow;
import network.packet.PacketHandler;
import network.packet.PacketType;


/**
 * Wear Item
 **/

public class WearItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		if (!player.isAnEgg && player.doingAnAction() || player.getDoingAgility()
				|| player.isTeleporting() || player.getDead()) {
			return;
		}
		int wearId = 0;
		int slot = 0;
		int interfaceId = 0;
		wearId = player.getInStream().readUnsignedWord();
		slot = player.getInStream().readUnsignedWordA();
		interfaceId = player.getInStream().readUnsignedWordA();
		player.sendDebugMessageF("wearId=%s, slot=%s, interfaceId=%s", wearId, slot, interfaceId);
		if (ItemAssistant.isNulledSlot(slot)) {
			return;
		}

		if (ItemAssistant.nulledItem(wearId)) {
			return;
		}
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "wearId: " + wearId);
			PacketHandler.saveData(player.getPlayerName(), "wearSlot: " + slot);
			PacketHandler.saveData(player.getPlayerName(), "interfaceId: " + interfaceId);
		}
		if (ItemDefinition.getDefinitions()[wearId].note) {
			return;
		}
		if (player.isInTrade() || player.getTradeStatus() == 1
				|| player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		Follow.resetFollow(player);
		if (!ItemAssistant.playerHasItem(player, wearId, 1, slot)) {
			return;
		}
		if (wearId >= 5509 && wearId <= 5515) {
			int pouch = -1;
			int pouchUsed = wearId;
			switch (pouchUsed) {
				case 5509:
					pouch = 0;
					break;
				case 5510:
					pouch = 1;
					break;
				case 5512:
					pouch = 2;
					break;
				case 5514:
					pouch = 3;
					break;
			}

			Runecrafting.emptyPouch(player, pouch);
			player.playerAssistant.sendMessage("You have emptied your pouch.");
			return;
		}
		switch (wearId) {
			case 22111: // Dragonbone necklace
				player.getAttributes().put(Player.DRAGONBONE_NECKLACE_TIMER, System.currentTimeMillis());
				break;
			case 22557:
				if (!Skull.isSkulled(player)) {
					Plugin.execute("amulet_of_avarice", player);
					return;
				}
				break;

			case 9813:
			case 13068:
				if (!player.getQuestFunction().hasCompletedAll()) {
					player.getPA()
							.sendMessage("You must have completed all quests in order to wear this cape.");
					return;
				}
				break;
			case 1305:
			case 1215:
			case 1231:
			case 5680:
			case 5698:
				if (GameMode.getGameMode(player, "STANDARD IRON MAN")
						|| GameMode.getGameMode(player, "ULTIMATE IRON MAN")
						|| GameMode.getGameMode(player, "HARDCORE IRON MAN")
								&& player.getQuest(3).getStage() != 4) {
					player.getDH().sendItemChat("",
							"You must have completed @blu@Lost City@bla@ to wield this",
							"item as an Ironman.", wearId, 200, 0, 0);
					return;
				}
				break;
			case 4587:
				if (GameMode.getGameMode(player, "STANDARD IRON MAN")
						|| GameMode.getGameMode(player, "ULTIMATE IRON MAN")
						|| GameMode.getGameMode(player, "HARDCORE IRON MAN")
								&& player.getQuest(5).getStage() != 7) {
					player.getDH().sendItemChat("",
							"You must have completed @blu@Monkey Madness@bla@ to wield this",
							"item as an Ironman.", wearId, 200, 0, 0);
					return;
				}
				break;
			default:
				if (Plugin.execute("equip_item_" + wearId, player)) {
				}
				break;
		}


		// Ghrazi rapier
		if (GameType.isOsrs() && player.getDuelStatus() == 5 && ItemAssistant.getItemName(wearId).equals("Ghrazi rapier") && player.duelRule[TradeAndDuel.ANTI_SCAM]) {
			player.getPA().sendMessage("You cannot use this weapon with anti-scam turned on.");
			return;
		}

		if (ItemAssistant.wearItem(player, wearId, slot)) {
			if ((player.getPlayerIdAttacking() > 0 || player.getNpcIdAttacking() > 0)) {
				Combat.resetPlayerAttack(player);
				player.playerAssistant.stopAllActions();
			}
		}
		AreaInterface.updateWalkableInterfaces(player);
	}

}
