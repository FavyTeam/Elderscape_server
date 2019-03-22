package game.content.miscellaneous;

import core.GameType;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import game.object.clip.ObjectDefinitionServer;
import game.player.Player;
import utility.Misc;

public class ItemOnBank {
	/**
	 * @return True if the player is trying to note/unnote an item on the bank object.
	 */
	public static boolean usingItemOnBank(Player player, int itemId, int objectId, int itemSlot) {
		ObjectDefinitionServer object = ObjectDefinitionServer.getObjectDef(objectId);
		if (object != null) {
			if (object.name == null) {
				return false;
			}
			if (object.name.toLowerCase().contains("bank") || objectId == 3194 || objectId == 76) {

				// Coins.
				if (itemId == 995 && GameType.isOsrsEco()) {
					int amount = ItemAssistant.getItemAmount(player, itemId);
					int token = 13204;
					if (amount > 1000) {
						if (ItemAssistant.hasItemAmountInInventory(player, itemId, amount)) {
							ItemAssistant.deleteItemFromInventory(player, itemId, amount);
							// Give back change.
							if (((amount / 1000) * 1000) != amount) {
								int change = amount - ((amount / 1000) * 1000);
								ItemAssistant.addItemToInventoryOrDrop(player, 995, change);
							}
							ItemAssistant.addItem(player, token, amount / 1000);
							player.getDH().sendItemChat("", "You exchange @blu@" + Misc.formatRunescapeStyle(amount) + "@bla@ Coins for @blu@" + Misc.formatRunescapeStyle(
									amount / 1000) + "@bla@ Platinum tokens.", token, 200, 0, -5);
						}
					} else {
						player.getPA().sendMessage("You need at least 1,000 coins to do this.");
					}
					return true;
				}

				// Platinum tokens
				if (itemId == 13204 && GameType.isOsrsEco()) {
					int amount = ItemAssistant.getItemAmount(player, itemId);
					int gp = 995;
					if (amount < 2147483) {
						if (ItemAssistant.hasItemAmountInInventory(player, itemId, amount)) {
							ItemAssistant.deleteItemFromInventory(player, itemId, amount);
							ItemAssistant.addItem(player, gp, amount * 1000);
							player.getDH().sendItemChat("",
							                            "You exchange @blu@" + Misc.formatRunescapeStyle(amount) + "@bla@ Platinum tokens for @blu@" + Misc.formatRunescapeStyle(
									                            amount * 1000) + "@bla@ Coins.", gp + 9, 200, 35, 0);
						}

					} else {
						player.getDH().sendStatement("You must have less than " + Misc.formatNumber(2147483) + " tokens to do this!");
					}
					return true;
				}

				if (ItemPacks.isPack(player, itemId, itemSlot)) {
					player.getDH().sendItemChat("", "You open the " + ItemAssistant.getItemName(itemId) + ".", itemId, 200, 35, 0);
					return true;
				}
				if (ItemDefinition.getDefinitions()[itemId].note) {
					int amountToUnNote = ItemAssistant.getItemAmount(player, itemId);
					int freeSpace = ItemAssistant.getFreeInventorySlots(player);
					int unNotedId = ItemAssistant.getUnNotedItem(itemId);
					if (!ItemDefinition.getDefinitions()[unNotedId].note) {
						if (ItemAssistant.hasItemAmountInInventory(player, itemId, amountToUnNote)) {
							int amountToAdd = amountToUnNote;
							if (ItemAssistant.getFreeInventorySlots(player) < amountToUnNote) {
								amountToAdd = freeSpace;
							}

							// So if you unnote 28 dark crabs with 27 inventory space, it unnotes all dark crabs.
							if (freeSpace + 1 == amountToUnNote) {
								amountToAdd = freeSpace + 1;
							}
							ItemAssistant.deleteItemFromInventory(player, itemId, amountToAdd);
							ItemAssistant.addItem(player, unNotedId, amountToAdd);
							player.getDH().sendItemChat("", "You un-note " + amountToAdd + " " + ItemAssistant.getItemName(unNotedId) + ".", unNotedId, 200, 10, 0);
						}
					} else {
						player.getDH().sendStatement("This item cannot be unnoted.");
					}
					return true;
				} else {
					int amountToNote = ItemAssistant.getItemAmount(player, itemId);
					int notedId = ItemAssistant.getNotedItem(itemId);
					if (ItemDefinition.getDefinitions()[notedId].note) {
						ItemAssistant.deleteItemFromInventory(player, itemId, amountToNote);
						ItemAssistant.addItem(player, notedId, amountToNote);
						player.getDH().sendItemChat("", "You note " + amountToNote + " " + ItemAssistant.getItemName(itemId) + ".", itemId, 200, 10, 0);
					} else {
						player.getDH().sendStatement("This item is not noteable.");
					}
					return true;
				}
			}
		}
		return false;
	}

}
