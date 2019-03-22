package game.content.packet;

import core.GameType;
import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.dialogue.DialogueChain;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.item.ItemInteractionManager;
import game.content.minigame.zombie.ZombieGameInstance;
import game.content.miscellaneous.BraceletOfEthereum;
import game.content.miscellaneous.ItemCombining;
import game.content.music.SoundSystem;
import game.content.skilling.Skilling;
import game.content.skilling.summoning.pet.SummoningPetManager;
import game.content.worldevent.BloodKey;
import game.content.worldevent.Tournament;
import game.item.DestroyItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.item.ItemHandler;
import game.npc.pet.DropPet;
import game.player.Area;
import game.player.Player;
import game.player.pet.PlayerPetManager;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.Misc;

/**
 * Drop Item
 **/

public class DropItemPacket implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize, boolean trackPlayer) {
		if (player.doingAnAction() || player.getDoingAgility() || !player.isTutorialComplete() || player.isTeleporting() || player.getDead() || player.isInTrade()
		    || System.currentTimeMillis() - player.alchDelay < 1800) {
			return;
		}

		int itemId = player.getInStream().readUnsignedWordA();
		int value1 = player.getInStream().readUnsignedByte();
		int value2 = player.getInStream().readUnsignedByte();
		int slot = player.getInStream().readUnsignedWordA();

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
			PacketHandler.saveData(player.getPlayerName(), "value1: " + value1);
			PacketHandler.saveData(player.getPlayerName(), "value2: " + value2);
			PacketHandler.saveData(player.getPlayerName(), "slot: " + slot);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			if (player.isAdministratorRank()) {
				ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
			}
			return;
		}

		if (!ItemAssistant.playerHasItem(player, itemId, 1, slot)) {
			return;
		}

		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Drop item: " + itemId + "]");
		}
		if (!EdgeAndWestsRule.canPickUpItemEdgeOrWestsRule(player, itemId, false)) {
			return;
		}


		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		if (player.isJailed()) {
			player.getPA().sendMessage("You cannot do this while jailed.");
			return;
		}
		boolean droppable = true;
		player.playerAssistant.stopAllActions();
		player.itemDestroyedSlot = slot;
		Combat.resetPlayerAttack(player);
		player.itemDestroyedId = itemId;
		Skilling.stopAllSkilling(player);

		if (itemId == 11941 && Combat.inCombat(player)) {
			return;
		}
		if (itemId == 21816) {
			BraceletOfEthereum.uncharge(player);
			return;
		}
		if (itemId == 12926 && GameType.isOsrsEco()) {
			DestroyItem.displayUnchargeInterface(player, itemId, slot);
			return;
		}
		if (ItemCombining.isDismantlable(player, itemId, slot, false)) {
			return;
		}
		if (ItemAssistant.isDestroyItem(itemId)) {
			DestroyItem.displayDestroyItemInterface(player, itemId, slot);
			return;
		}
		// Skill capes and hoods.
		if (itemId >= 9747 && itemId <= 9812) {
			DestroyItem.displayDestroyItemInterface(player, itemId, slot);
			return;
		}

		if (DropPet.dropPetRequirementsOsrs(player, itemId, slot)) {
			return;
		}

		if (SummoningPetManager.spawn(player, itemId)) {
			return;
		}

		if (PlayerPetManager.dropPlayerPet(player, itemId)) {
			return;
		}

		switch (itemId) {
			case 11959:
				if (Area.inWilderness(player.getX(), player.getY(), player.getHeight())) {
					player.getPA().sendMessage("You cannot release chinchompas whilst in the wilderness.");
					return;
				}
				DestroyItem.displayDestroyItemInterface(player, itemId, slot);
				return;

			// Serpentine helms.
			case 12931:
			case 13199:
			case 13197:
				return;
		}

		if (itemId == 20657) {
			player.setDialogueChainAndStart(new DialogueChain().option((p, option) -> {
				if (option == 1) {
					final boolean enabled = p.getAttributes().getOrDefault(Player.RING_OF_SUFFERING_ENABLED);

					player.setDialogueChainAndStart(new DialogueChain().option((p1, option1) -> {
						if (option1 == 1) {
							p.getAttributes().put(Player.RING_OF_SUFFERING_ENABLED, !enabled);
							p.getPA().sendMessageF("You change the state of your ring to %s.", enabled ? "disabled" : "enabled");
						}
						p1.getPA().closeInterfaces(true);
					}, String.format("Do you want to %s the effect?", enabled ? "@red@disable" : "@gre@enable"), "Yes", "No"));
				} else {
					p.getPA().closeInterfaces(true);
				}
			}, "Ring of Suffering settings", "Toggle effect", "Close"));
			return;
		}

		// Explosive potion
		if (itemId == 4045) {
			boolean inCastlewars = false;
			if (!inCastlewars) {
				return;
			}
			int amount = 15;
			if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - amount < 1) {
				amount = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - 1;
			}
			player.startAnimation(827);
			ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
			if (amount > 0) {
				Combat.createHitsplatOnPlayerNormal(player, amount, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
			}
			player.forcedChat("Ow! That really hurt!", false, false);
			player.forcedChatUpdateRequired = true;
			Skilling.updateSkillTabFrontTextMain(player, ServerConstants.HITPOINTS);
			droppable = false;

		}

		// Blood key.
		if (BloodKey.isAnyBloodKey(itemId)) {
			player.getPA().sendMessage("You cannot drop this.");
			return;
		}
		droppable = !ItemInteractionManager.handleDropItem(player, itemId);
		if (droppable) {
			if (ItemDefinition.getDefinitions()[itemId] != null) {
				int itemValue = (ServerConstants.getItemValue(itemId) * player.playerItemsN[slot]);
				if (itemValue > ServerConstants.getDropItemValueInCombatFlag() && (Combat.inCombat(player) && Area.inDangerousPvpArea(player) || System.currentTimeMillis() < player.timeCanTradeAndDrop)) {
					player.getPA().sendMessage(
							"You cannot drop an item more than " + Misc.formatRunescapeStyle(ServerConstants.getDropItemValueInCombatFlag()) + " " + ServerConstants
									                                                                                                                         .getMainCurrencyName()
									                                                                                                                         .toLowerCase()
							+ " in combat.");
					return;
				}
				if (System.currentTimeMillis() - player.timeUsedRiskCommand < 45000 && itemValue > 100) {
					player.getPA().quickChat("I have dropped an item on the floor.");
				}

				if (itemValue > 5000 && player.getPA().isNewPlayerEco()) {
					player.getPA().sendMessage("You cannot drop items as a new player.");
					return;
				}
				if (player.getHeight() == 20) {
					if (Misc.arrayHasNumber(Tournament.ITEMS_CANNOT_DROP, itemId)) {
						boolean skip = false;
						// Incase the player has 2 rune legs and tries to drop 1, it will let him drop.
						if (ItemAssistant.getItemAmount(player, itemId) > 1) {
							skip = true;
						}

						if (!skip) {
							player.getPA().sendMessage("You cannot drop this item to prevent mage-only abuse!");
							return;
						}
					}
				}
				if (ItemAssistant.itemDropsOnFloorAndOnlyAppearsToOwner(itemId)) {
					Server.itemHandler.createGroundItem(player, itemId, player.getX(), player.getY(), player.getHeight(), player.playerItemsN[slot], false,
					                                    ItemHandler.APPEAR_FOR_MYSELF_ONLY_TICKS - 2, true, player.getPlayerName(), player.getPlayerName(), player.addressIp,
							player.addressUid, "DropItemPacket itemDropsOnFloorAndOnlyAppearsToOwner");
				} else {
					Server.itemHandler.createGroundItem(player, itemId, player.getX(), player.getY(), player.getHeight(), player.playerItemsN[slot],
					                                    ZombieGameInstance.getMinigameInstanceIndex(player.getPlayerName()) >= 0 ? true : false, 0, true, player.getPlayerName(),
							player.getPlayerName(), player.addressIp, player.addressUid, "DropItemPacket else");
				}
			}
			ItemAssistant.deleteItemFromInventory(player, itemId, slot, player.playerItemsN[slot]);
			SoundSystem.sendSound(player, 376, 0);
		}
		ItemsKeptOnDeath.updateInterface(player);
	}
}
