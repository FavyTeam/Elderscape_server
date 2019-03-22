package game.item;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.consumable.Food;
import game.content.minigame.barrows.BarrowsRepair;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.LootNotification;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.content.wildernessbonus.WildernessRisk;
import game.log.GameTickLog;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;
import java.util.List;
import utility.Misc;

/**
 * Handles ground items
 **/

public class ItemHandler {

	public List<GroundItem> items = new ArrayList<GroundItem>();

	public static final int APPEAR_FOR_MYSELF_ONLY_TICKS = 100; // 60 seconds.

	public static final int APPEAR_FOR_EVERYONE_TICKS = 200; // 120 seconds.

	public ItemHandler() {
	}

	/**
	 * Adds item to list
	 **/
	public void addItem(GroundItem item) {
		items.add(item);
	}

	/**
	 * Removes item from list
	 **/
	public void removeItem(GroundItem item) {
		items.remove(item);
	}

	/**
	 * Item amount
	 **/
	public int itemAmount(Player player, int itemId, int itemX, int itemY) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY && i.getOwnerName().equals(player.getPlayerName())) {
				return i.getItemAmount();
			}
		}
		return 0;
	}


	/**
	 * Item exists
	 **/
	public boolean itemExists(int itemId, int itemX, int itemY) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Reloads any items if you enter a new region
	 **/
	public void showGroundItemsToSpecificPlayerOnRegionChange(Player player) {
		if (player != null) {
			player.timeReloadedItems = System.currentTimeMillis();
		}
		// Remove items first, so it doesn't show 2 whips instead of 1 whip.
		// It happens when i walk out of the region, then walk back in.

		player.getPA().sendMessage(":packet:grounditemsremove");

		// So after removing the items, add them.
		for (GroundItem i : items) {
			if (player != null) {
				showGroundItemToPlayerAction(player, i);
			}
		}
	}

	public void itemGameTick() {
		GameTickLog.itemTickDuration = System.currentTimeMillis();
		ArrayList<GroundItem> toRemove = new ArrayList<GroundItem>();
		for (int j = 0; j < items.size(); j++) {
			if (items.get(j) != null) {
				GroundItem i = items.get(j);
				if (i.hideTicks > 0) {
					i.hideTicks--;
				}
				if (i.hideTicks == 1) { // item can now be seen by others
					i.hideTicks = 0;
					showGroundItemToAllPlayers(i);
					if (i.removeTicks == 0) {
						i.removeTicks = APPEAR_FOR_EVERYONE_TICKS;
					}
				}
				if (i.removeTicks > 0) {
					i.removeTicks--;
				}
				if (i.removeTicks == 1) {
					i.removeTicks = 0;
					toRemove.add(i);
				}

			}

		}

		for (int j = 0; j < toRemove.size(); j++) {
			GroundItem i = toRemove.get(j);
			removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount(), i.getItemHeight());
		}
		GameTickLog.itemTickDuration = System.currentTimeMillis() - GameTickLog.itemTickDuration;
	}

	/**
	 * Create a ground item.
	 *
	 * @param appearInstantly True, to make the item appear instantly to everyone.
	 * @param appearForEveryoneCustomTimer Used to make the Firemaking ash appear for only 20 ticks
	 *        instead of 100.
	 * @param killerOwnerName The name of the player that got the loot from pking.
	 * @param originalOwnerIp TODO
	 * @param originalOwnerUid TODO
	 * @param itemFromDescription used to find out how the item was spawned on the ground, so add the
	 *        method it came from and other small details. I initially added it to find out how a player
	 *        picked up 50 million blood money from floor. So i know where it came from.
	 */
	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int itemHeight, int itemAmount, boolean appearInstantly, int appearForEveryoneCustomTimer,
			boolean alertPlayer, String originalOwnerNameSource, String killerOwnerName, String originalOwnerIp, String originalOwnerUid, String itemFromDescription) {
		if (player == null) {
			return;
		}
		if (itemId > 0) {
			if (alertPlayer) {
				LootNotification.loot(player, itemId, itemAmount);
			}

			if (itemId > 4705 && itemId < 4760 && !ItemAssistant.isNoted(itemId)) {
				for (int j = 0; j < BarrowsRepair.brokenBarrows.length; j++) {
					if (BarrowsRepair.brokenBarrows[j][0] == itemId) {
						itemId = BarrowsRepair.brokenBarrows[j][1];
						break;
					}
				}
			}

			if (ItemDefinition.getDefinitions()[itemId] == null) {
				return;
			}
			if (!ItemDefinition.getDefinitions()[itemId].stackable && itemAmount > 0) {

				// I had this bug on server where they can buy 2147m of any unnoted item in their inventory in 1 stack. So if they drop it, it will
				//loop 2147m in the itemAmount for loop, so reduce it to 28.
				if (itemAmount > 200) {
					itemAmount = 200;
				}
				for (int j = 0; j < itemAmount; j++) {
					ItemAssistant.createGroundItem(player, itemId, itemX, itemY, 1);
					if (appearInstantly) {
						GroundItem item = new GroundItem(itemId, itemX, itemY, 1, 2, player.getPlayerName(), player.getGameMode(), itemHeight, appearForEveryoneCustomTimer,
								originalOwnerNameSource, killerOwnerName, originalOwnerIp, originalOwnerUid, itemFromDescription);
						addItem(item);
					} else {
						GroundItem item = new GroundItem(itemId, itemX, itemY, 1,
						                                 player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE ? 2147000000 : APPEAR_FOR_MYSELF_ONLY_TICKS,
						                                 player.getPlayerName(), player.getGameMode(), itemHeight, appearForEveryoneCustomTimer, originalOwnerNameSource,
								killerOwnerName, originalOwnerIp, originalOwnerUid, itemFromDescription);
						addItem(item);
					}
				}
			} else {
				ItemAssistant.createGroundItem(player, itemId, itemX, itemY, itemAmount);
				if (appearInstantly) {
					GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount, 2, player.getPlayerName(), player.getGameMode(), itemHeight, appearForEveryoneCustomTimer,
							originalOwnerNameSource, killerOwnerName, originalOwnerIp, originalOwnerUid, itemFromDescription);
					addItem(item);
				} else {
					GroundItem item = new GroundItem(itemId, itemX, itemY, itemAmount,
					                                 player.isAdministratorRank() && ServerConfiguration.DEBUG_MODE ? 2147000000 : APPEAR_FOR_MYSELF_ONLY_TICKS,
					                                 player.getPlayerName(), player.getGameMode(), itemHeight, appearForEveryoneCustomTimer, originalOwnerNameSource,
							killerOwnerName, originalOwnerIp, originalOwnerUid, itemFromDescription);
					addItem(item);
				}
			}
		}
	}


	/**
	 * Shows items for everyone who is within 60 squares
	 **/
	public void showGroundItemToAllPlayers(GroundItem i) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player player = p;
				if (player != null) {
					if (player.getPlayerName().equals(i.getOwnerName())) {
						continue;
					}
					showGroundItemToPlayerAction(player, i);
				}
			}
		}
	}



	public void showGroundItemToPlayerAction(Player player, GroundItem i) {
		if (player.playerAssistant.distanceToPoint(i.getItemX(), i.getItemY()) <= 60 && player.getHeight() == i.getItemHeight()) {

			int itemWorth = (ServerConstants.getItemValue(i.getItemId()) * i.getItemAmount());
			if (i.hideTicks > 0 && !i.getOwnerName().equalsIgnoreCase(player.getPlayerName())) {
				return;
			}
			// If the stackable item is worth less than 25 blood money, do not show on floor.
			if (itemWorth <= ServerConstants.getStackableItemGroundIgnoreValue() && ItemDefinition.getDefinitions()[i.getItemId()].stackable && !i.getOwnerName().equalsIgnoreCase(
					player.getPlayerName())) {
				return;
			}

			if (i.getGameMode().contains("IRON MAN")) {
				if (!GameMode.getGameModeContains(player, "IRON MAN")) {
					return;
				}
			}
			if (GameMode.getGameModeContains(player, "IRON MAN") && !player.getPlayerName().equals(i.getOwnerName())) {
				return;
			}
			ItemAssistant.createGroundItem(player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
		}

	}

	/**
	 * Removing the ground item
	 **/

	public void removeGroundItem(Player player, int itemId, int itemX, int itemY, int amount, boolean add) {//
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX && i.getItemY() == itemY && player.getHeight() == i.getItemHeight() && i.getItemAmount() == amount) {
				if (i.getGameMode().contains("IRON MAN")) {
					if (!GameMode.getGameModeContains(player, "IRON MAN")) {
						player.getPA().sendMessage("This item belongs to an Ironman.");
						continue;
					}
				}
				if (GameMode.getGameModeContains(player, "IRON MAN") && !player.getPlayerName().equals(i.getOwnerName())) {
					player.getPA().sendMessage("You cannot pick up items belonging to other players.");
					continue;
				}
				if (i.getOwnerName().equalsIgnoreCase("mgt madness") || i.getOwnerName().equalsIgnoreCase("ronald")) {
					if (!player.isAdministratorRank() && !ServerConfiguration.DEBUG_MODE) {
						player.getPA().sendMessage("This item belongs to an Administrator.");
						continue;
					}
				}
				if (!i.getOwnerName().equals(player.getPlayerName())) {
					if (!i.getKillerOwnerName().equals(player.getPlayerName())) {
						if (System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 8000
						    || System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 8000) {
							if (Food.isFood(itemId) ||
							    // Karambwan
							    itemId == 3144 ||
							    ItemDefinition.getDefinitions()[itemId].name.contains("Saradomin brew") && !ItemDefinition.getDefinitions()[itemId].note) {

								WildernessRisk.hasWildernessActivityRisk(player, 0);
								if (player.wildernessRiskAmount >= ServerConstants.getRiskingWealthCannotPickUpFoodRisk()) {
									player.getPA().sendMessage("You cannot pick up food that is not yours while risking " + Misc.formatRunescapeStyle(
											ServerConstants.getRiskingWealthCannotPickUpFoodRisk()) + " " + " or more.");
									break;
								}
							}
						}
					}
				}
				if (!ItemDefinition.getDefinitions()[i.getItemId()].f2p && (System.currentTimeMillis() - player.getTimeUnderAttackByAnotherPlayer() <= 9000
				                                                            || System.currentTimeMillis() - player.getTimeAttackedAnotherPlayer() <= 9000)) {
					if (QuickSetUp.isUsingF2pOnly(player, false, true)) {
						player.getPA().sendMessage("You cannot pick up a p2p item while using full f2p in combat.");
						break;
					}
				}
				if (i.hideTicks > 0 && i.getOwnerName().equalsIgnoreCase(player.getPlayerName())) {
					if (add) {
						long wealthBeforePickUp = ItemAssistant.getAccountBankValueLong(player);
						if (ItemAssistant.addItem(player, i.getItemId(), i.getItemAmount())) {
							ItemTransferLog.pickUpItem(wealthBeforePickUp, player, i.getItemId(), i.getItemAmount(), i.getOriginalOwnerName(), i.getItemX(), i.getItemY(), i.getItemHeight(),
									i.originalOwnerIp, i.originalOwnerUid, i.getItemFromDescription());
							ItemAssistant.pickUpSingularUntradeableItem(player, i.getItemId());
							player.timePickedUpItem = System.currentTimeMillis();
							player.lastItemIdPickedUp = i.getItemId();
							removeControllersItem(i, player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							break;
						}
					} else {
						removeControllersItem(i, player, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						break;
					}
					break;
				} else if (i.hideTicks <= 0) {
					if (add) {
						long wealthBeforePickUp = ItemAssistant.getAccountBankValueLong(player);
						if (ItemAssistant.addItem(player, i.getItemId(), i.getItemAmount())) {
							ItemTransferLog.pickUpItem(wealthBeforePickUp, player, i.getItemId(), i.getItemAmount(), i.getOriginalOwnerName(), i.getItemX(), i.getItemY(), i.getItemHeight(),
									i.originalOwnerIp, i.originalOwnerUid, i.getItemFromDescription());
							ItemAssistant.pickUpSingularUntradeableItem(player, i.getItemId());
							player.timePickedUpItem = System.currentTimeMillis();
							player.lastItemIdPickedUp = i.getItemId();
							removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount(), i.getItemHeight());
							break;
						}
					} else {
						removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount(), i.getItemHeight());
						break;
					}

				}
			}
		}
	}

	/**
	 * Remove item for just the item controller (item not global yet)
	 **/

	public void removeControllersItem(GroundItem i, Player player, int itemId, int itemX, int itemY, int itemAmount) {
		ItemAssistant.removeGroundItem(player, itemId, itemX, itemY, itemAmount);
		removeItem(i);
	}

	/**
	 * Remove item for everyone within 60 squares
	 **/

	public void removeGlobalItem(GroundItem i, int itemId, int itemX, int itemY, int itemAmount, int itemHeight) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player person = p;
				if (person != null) {
					if (p.getHeight() == itemHeight) {
						if (person.playerAssistant.distanceToPoint(itemX, itemY) <= 60) {
							ItemAssistant.removeGroundItem(person, itemId, itemX, itemY, itemAmount);
						}
					}
				}
			}
		}
		removeItem(i);
	}

}
