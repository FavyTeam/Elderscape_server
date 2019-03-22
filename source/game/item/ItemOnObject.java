package game.item;

import core.GameType;
import core.Plugin;
import core.ServerConfiguration;
import game.content.item.ItemInteractionManager;
import game.content.minigame.WarriorsGuild;
import game.content.miscellaneous.CrystalChest;
import game.content.miscellaneous.Ectofuntus;
import game.content.miscellaneous.ItemOnBank;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.WateringCan;
import game.content.miscellaneous.Web;
import game.content.skilling.Bonfire;
import game.content.skilling.Bonfire.Logs;
import game.content.skilling.Cooking;
import game.content.skilling.Farming;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.SpinningWheel;
import game.content.skilling.prayer.BoneOnAltar;
import game.content.skilling.smithing.Smithing;
import game.content.skilling.smithing.SmithingInterface;
import game.content.worldevent.BloodKey;
import game.entity.Entity;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.movement.Follow;
import utility.Misc;

public class ItemOnObject {

	public static void itemOnObject(final Player player, final int objectId, int objectX, int objectY, final int itemId, final int itemSlot) {
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("[Item: " + itemId + "] on [Object: " + objectId + "][Object X: " + objectX + "][Object Y: " + objectY + "]");
		}
		if (player.farmingXCoordinate > 0 && (objectId == Farming.patchCleanObject || objectId == Farming.PATCH_HERBS)) {
			objectX = player.farmingXCoordinate;
			objectY = player.farmingYCoordinate;
			player.setWalkingPacketQueue(objectX, objectY);
		}
		Follow.resetFollow(player);
		player.turnPlayerTo(objectX, objectY);
		player.setObjectX(objectX);
		player.setObjectY(objectY);
		player.setObjectId(objectId);
		player.itemOnObjectEvent = true;
		final int objectX1 = objectX;
		final int objectY1 = objectY;

		player.getEventHandler().addEvent(player, new CycleEvent<Entity>() {
			@Override
			public void execute(CycleEventContainer<Entity> container) {
				if (player.itemOnObjectEvent) {
					int distance = 2;
					// Warrior's guild summoning platform.
					if (objectId == 15621 && objectX1 == 2857) {
						player.setWalkingPacketQueue(2857, 3537);
					}
					if (!player.playerAssistant.withInDistance(objectX1, objectY1, player.getX(), player.getY(), distance)) {
						return;
					} else {
						player.itemOnObjectEvent = false;
						container.stop();
					}

					if (Farming.checkItemOnObject(player, itemId, objectId)) {
						return;
					}

					if (WarriorsGuild.spawnAnimatorAction(player, objectId, itemId)) {
						return;
					}

					if (ItemInteractionManager.handleItemOnObject(player, itemId, objectId)) {
						return;
					}

					if (Smithing.isFurnace(player, objectId)) {

						// Gold bar
						if (itemId != 2357) {
							return;
						}
						player.setActionIdUsed(7);
						JewelryCrafting.jewelryInterface(player);
						return;
					}

					if (objectId == 27277) {
						BloodKey.openChest(player, itemId);
						return;
					}

					if (objectId == 30253 || objectId == 411) {
						BoneOnAltar.useBoneOnAltar(player, itemId);
						return;
					}


					if (ItemOnBank.usingItemOnBank(player, itemId, objectId, itemSlot)) {
						return;
					}
					if (objectId == 2152 && itemId == 569 || itemId == 571 || itemId == 573 || itemId == 575 || itemId == 1391 || itemId == 1393 || itemId == 1395 || itemId == 1397
					    || itemId == 1399 || itemId == 1775) {
						int amountToNote = ItemAssistant.getItemAmount(player, itemId);
						int slot = ItemAssistant.getItemSlot(player, itemId);
						int notedId = ItemAssistant.getNotedItem(itemId);
						if (ItemDefinition.getDefinitions()[notedId].note) {
							ItemAssistant.deleteItemFromInventory(player, itemId, amountToNote);
							ItemAssistant.addItemToInventory(player, notedId, amountToNote, slot, true);
							player.getDH()
							      .sendItemChat("", "You use the power of the obelisk to note " + amountToNote + " " + ItemAssistant.getItemName(itemId) + ".", itemId, 200, 14, 0);
						}
						return;
					}



					switch (objectId) {
						case 29088:
							if (GameType.isOsrsEco()) {
								PlayerMiscContent.EggShrine(player);
							}
							break;
						case 172:
							CrystalChest.lootChest(player, itemId);
							break;

						case 2644:
						case 25824:
						case 14889:
							SpinningWheel.spinningWheel(player, itemId);
							break;

						case 17119:
							if (itemId == 1925) {
								Ectofuntus.FillBucket(player);
							}
							break;

						case 16654:
							Ectofuntus.BoneGrinding(player);
							break;

						// Anvil.
						case 2031:
						case 2097:
							SmithingInterface.showSmithInterface(player, itemId);
							break;

						case 733:
							Web.slash(player, itemId);
							break;
						case 114:
						case 4259:
						case 3038:
						case 2732:
						case 5249: // Fire.
						case 26181:
						case 26185: // Fire
						case 21302: //Oven
						case 12269:
						case 12102:
							for (final Logs g : Logs.values()) {
								if (itemId == g.getLogID()) {
									Bonfire.startBurning(player, itemId);
									return;
								}
							}

							if (itemId == 2132) {
								player.rawBeef = true;
								Cooking.rawBeef(player);
								return;
							}
							player.fireX = objectX1;
							player.fireY = objectY1;
							Cooking.cookThisFood(player, itemId, objectId);
							break;

						// Altar.
						case 2640:
							BoneOnAltar.useBoneOnAltar(player, itemId);
							break;
						case 874:
							WateringCan.refill(player);
							break;
						default:
							if (Plugin.execute("item_" + itemId + "_on_object_" + objectId, player)) {
							} else {
								player.getPA().sendMessage("Nothing interesting happens.");
							}
							break;
					}
				} else {
					container.stop();
				}
			}

			@Override
			public void stop() {
			}

		}, 1);
	}

}
