package game.content.packet;

import core.Server;
import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.skilling.Skilling;
import game.content.worldevent.BloodKey;
import game.item.GroundItem;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;


/**
 * Magic on floor items
 **/
public class MagicOnFloorItemPacket implements PacketType {

	@Override
	public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer) {
		final int itemY = player.getInStream().readSignedWordBigEndian();
		int itemId = player.getInStream().readUnsignedWord();
		final int itemX = player.getInStream().readSignedWordBigEndian();
		if (trackPlayer) {
			PacketHandler.saveData(player.getPlayerName(), "itemY: " + itemY);
			PacketHandler.saveData(player.getPlayerName(), "itemId: " + itemId);
			PacketHandler.saveData(player.getPlayerName(), "itemX: " + itemX);
		}

		if (ItemAssistant.nulledItem(itemId)) {
			return;
		}

		if (player.doingAnAction()) {
			return;
		}
		if (player.getDead()) {
			return;
		}

		if (player.isInTrade() || player.getTradeStatus() == 1 || player.getDuelStatus() >= 1 && player.getDuelStatus() <= 4) {
			return;
		}
		if (!Server.itemHandler.itemExists(itemId, itemX, itemY)) {
			Movement.stopMovement(player);
			return;
		}
		Combat.resetPlayerAttack(player);
		// Blood key.
		if (BloodKey.isAnyBloodKey(itemId)) {
			player.getPA().sendMessage("You cannot use telegrab on the blood key.");
			return;
		}

		if (!Combat.checkMagicRequirementsForNpcCombatAndMagicOnFloorItemPacket(player, 51)) {
			Movement.stopMovement(player);
			return;
		}

		if ((((ItemAssistant.getFreeInventorySlots(player) >= 1) || ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) && ItemDefinition.getDefinitions()[itemId].stackable)
		    || ((ItemAssistant.getFreeInventorySlots(player) > 0) && !ItemDefinition.getDefinitions()[itemId].stackable)) {
			if (player.playerAssistant.withInDistance(player.getX(), player.getY(), itemX, itemY, 12)) {
				if (System.currentTimeMillis() - player.teleGrabDelay <= 2000) {
					player.turnPlayerTo(itemX, itemY);
					Movement.stopMovement(player);
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, 563) || !ItemAssistant.hasItemInInventory(player, 556)) {
					player.turnPlayerTo(itemX, itemY);
					Movement.stopMovement(player);
					player.getPA().sendMessage("You need 1 air rune and 1 law rune.");
					return;
				}
				player.magicOnFloor = true;
				ItemAssistant.deleteItemFromInventory(player, 563, 1);
				ItemAssistant.deleteItemFromInventory(player, 556, 1);
				player.walkingToItem = true;
				int offY = (player.getX() - itemX) * -1;
				int offX = (player.getY() - itemY) * -1;
				player.teleGrabX = itemX;
				player.teleGrabY = itemY;
				player.teleGrabItem = itemId;
				player.setLastCastedMagic(true);
				player.turnPlayerTo(itemX, itemY);
				player.teleGrabDelay = System.currentTimeMillis();
				player.startAnimation(CombatConstants.MAGIC_SPELLS[CombatConstants.TELEGRAB_INDEX][2]);
				player.gfx100(CombatConstants.MAGIC_SPELLS[CombatConstants.TELEGRAB_INDEX][3]);
				player.getPA().createPlayersStillGfx(144, itemX, itemY, 0, 72);
				player.getPA()
				      .createPlayersProjectile(player.getX(), player.getY(), offX, offY, 50, 70, CombatConstants.MAGIC_SPELLS[CombatConstants.TELEGRAB_INDEX][4], 50, 10, 0, 50,
				                               Combat.getProjectileSlope(player));
				Skilling.addSkillExperience(player, CombatConstants.MAGIC_SPELLS[CombatConstants.TELEGRAB_INDEX][7], 6, false);
				Skilling.updateSkillTabFrontTextMain(player, ServerConstants.MAGIC);
				Movement.stopMovement(player);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (!player.walkingToItem) {
							container.stop();
						}
						if (System.currentTimeMillis() - player.teleGrabDelay > 1100 && player.hasLastCastedMagic() && player.walkingToItem) {
							int amount = Server.itemHandler.itemAmount(player, player.teleGrabItem, player.teleGrabX, player.teleGrabY);

							if (amount > 0 && player.playerAssistant.withInDistance(player.getX(), player.getY(), itemX, itemY, 12)) {
								Server.itemHandler.removeGroundItem(player, player.teleGrabItem, player.teleGrabX, player.teleGrabY, amount, true);
								container.stop();
							}
						}
					}

					@Override
					public void stop() {
						player.magicOnFloor = false;
						player.walkingToItem = false;
						player.setLastCastedMagic(false);
					}
				}, 1);
			}
		} else {
			player.playerAssistant.sendMessage("You don't have enough space in your inventory.");
			Movement.stopMovement(player);
		}
	}

}
