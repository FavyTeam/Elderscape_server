package game.content.packet;

import core.Server;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.music.SoundSystem;
import game.content.worldevent.BloodKey;
import game.item.GlobalItemSpawn;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;


/**
 * Pickup Item
 **/
public class PickUpItemPacket implements PacketType {

	@Override
	public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer) {
		player.itemPickedUpY = player.getInStream().readSignedWordBigEndian();
		player.itemPickedUpId = player.getInStream().readUnsignedWord();
		player.itemPickedUpX = player.getInStream().readSignedWordBigEndian();
		int amount = player.getInStream().readDWord();
		player.sendDebugMessageF("Pickup item amount: %s", amount);
		pickUpItemPacket(player, trackPlayer, amount);
	}

	public static void pickUpItemPacket(Player player, boolean trackPlayer, int amount) {
		if (player.doingAnAction()) {
			return;
		}
		if (player.getDead()) {
			return;
		}

		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "pItemY: " + player.itemPickedUpY);
			PacketHandler.saveData(player.getPlayerName(), "pItemId: " + player.itemPickedUpId);
			PacketHandler.saveData(player.getPlayerName(), "pItemX: " + player.itemPickedUpX);
		}
		if (ItemAssistant.nulledItem(player.itemPickedUpId)) {
			return;
		}

		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		if (Math.abs(player.getX() - player.itemPickedUpX) > 25 || Math.abs(player.getY() - player.itemPickedUpY) > 25) {
			Movement.resetWalkingQueue(player);
			return;
		}
		Combat.resetPlayerAttack(player);
		if ((player.getX() != player.itemPickedUpX || player.getY() != player.itemPickedUpY) && player.isFrozen()) {
			player.getPA().sendMessage("You cannot reach that!");
			player.walkingToItem = false;
			return;
		}

		// Blood key.
		if (BloodKey.isAnyBloodKey(player.itemPickedUpId)) {
			if (player.getPA().withInDistance(player.itemPickedUpX, player.itemPickedUpY, player.getX(), player.getY(), 1)) {
				BloodKey.pickUpBloodKey(player, player.itemPickedUpId);
				return;
			}
			return;
		}

		if (!EdgeAndWestsRule.canPickUpItemEdgeOrWestsRule(player, player.itemPickedUpId, true)) {
			return;
		}

		if (amount < 1) {
			return;
		}

		if (player.getHeight() == 20 && Combat.inCombat(player)) {
			player.getPA().sendMessage("You cannot pick up items while in combat inside the tournament!");
			return;
		}

		if (Area.inVarrockBasement(player)) {
			player.getPA().sendMessage("So, you thought you were clever? Better luck next time.");
			return;
		}

		if (player.getX() == player.itemPickedUpX && player.getY() == player.itemPickedUpY) {
			player.walkingToItem = false;
			pickUpItem(player, amount);
		} else {
			player.walkingToItem = true;
			if (player.walkingToItemEvent) {
				return;
			}
			player.walkingToItemEvent = true;
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (player.walkingToItem) {
						// Picking up items from a table etc.
						if (player.playerAssistant.withInDistance(player.getX(), player.getY(), player.itemPickedUpX, player.itemPickedUpY, 1)) {
							if (!Region.isStraightPathUnblocked(player.getX(), player.getY(), player.itemPickedUpX, player.itemPickedUpY, player.getHeight(), 1, 1, false)) {
								player.turnPlayerTo(player.itemPickedUpX, player.itemPickedUpY);
								player.walkingToItem = false;
								pickUpItem(player, amount);
								container.stop();
								return;
							}
						}
						if ((player.getX() != player.itemPickedUpX || player.getY() != player.itemPickedUpY) && player.isFrozen()) {
							player.getPA().sendMessage("You cannot reach that!");
							player.walkingToItem = false;
							container.stop();
							return;
						}
						if (player.getX() == player.itemPickedUpX && player.getY() == player.itemPickedUpY) {
							player.walkingToItem = false;
							pickUpItem(player, amount);
							container.stop();
							return;
						}
					} else {
						container.stop();
					}
				}

				@Override
				public void stop() {
					player.walkingToItemEvent = false;
				}
			}, 1);

		}

	}

	public static boolean pickUpItem(Player player, final int amount) {
		if (ItemAssistant.getFreeInventorySlots(player) == 0 && ItemDefinition.getDefinitions()[player.itemPickedUpId].stackable && ItemAssistant.hasItemInInventory(player,
		                                                                                                                                                             player.itemPickedUpId)) {
			if (player.lastItemIdPickedUp == player.itemPickedUpId) {
				if (System.currentTimeMillis() - player.timePickedUpItem < 500) {
					return false;
				}
			}
			Server.itemHandler.removeGroundItem(player, player.itemPickedUpId, player.itemPickedUpX, player.itemPickedUpY, amount, true);
			SoundSystem.sendSound(player, 356, 0);
			player.lastItemIdPickedUp = player.itemPickedUpId;
			GlobalItemSpawn.pickup(player, player.itemPickedUpId, player.itemPickedUpX, player.itemPickedUpY, amount);
			ItemAssistant.resetItems(player,
			                         3214); // Update inventory. Keep here because this method is executed in CycleEvent so if you walk to an item to pick it up, the inventory will update very slow.
		} else if (ItemAssistant.getFreeInventorySlots(player) > 0) {
			if (player.lastItemIdPickedUp == player.itemPickedUpId) {
				if (System.currentTimeMillis() - player.timePickedUpItem < 500) {
					return false;
				}
			}
			Server.itemHandler.removeGroundItem(player, player.itemPickedUpId, player.itemPickedUpX, player.itemPickedUpY, amount, true);
			SoundSystem.sendSound(player, 356, 0);
			player.lastItemIdPickedUp = player.itemPickedUpId;
			GlobalItemSpawn.pickup(player, player.itemPickedUpId, player.itemPickedUpX, player.itemPickedUpY, amount);
			ItemAssistant.resetItems(player,
			                         3214); // Update inventory. Keep here because this method is executed in CycleEvent so if you walk to an item to pick it up, the inventory will update very slow.
			return true;
		} else {
			player.playerAssistant.sendMessage("You do not have enough inventory space.");
			return false;
		}
		return false;
	}

}
