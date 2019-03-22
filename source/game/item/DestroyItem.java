package game.item;

import core.GameType;
import core.Server;
import game.content.degrading.DegradingItem;
import game.content.degrading.DegradingItemJSONLoader;
import game.content.miscellaneous.Blowpipe;
import game.content.music.SoundSystem;
import game.player.Player;

/**
 * Destroy item interface functions.
 *
 * @author MGT Madness, created on 06-07-2015.
 */
public class DestroyItem {

	/**
	 * Destroying item
	 */
	private static final String[][] DESTROY_ITEM =
			{{"@bla@Are you sure you want to destroy this item?", "14174"}, {"Yes.", "14175"},
					{"No.", "14176"}, {" ", "14177"},
					{"@red@Warning! This item will dissapear if you accept.", "14182"}, {" ", "14183"},
					{"itemName", "14184"}};

	/**
	 * Uncharging item
	 */
	private static final String[][] UNCHARGE_ITEM =
			{{"@bla@Are you sure you want to uncharge it?", "14174"}, {"Yes.", "14175"},
					{"No.", "14176"}, {" ", "14177"},
					{"@red@If you uncharge the blowpipe, all scales and darts will fall out.", "14182"},
					{" ", "14183"}, {"itemName", "14184"}};

	/**
	 * Break item
	 */
	private static final String[][] BREAK_ITEM =
			{{"@bla@Are you sure you want to drop this item?", "14174"}, {"Yes.", "14175"},
					{"No.", "14176"}, {" ", "14177"},
					{"@red@If you drop it, you may retain an unusable version.", "14182"},
					{" ", "14183"}, {"itemName", "14184"}};

	/**
	 * True, if the button belongs to the destroy interface.
	 *
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean isDestroyInterfaceButton(Player player, int buttonId) {
		switch (buttonId) {
			case 55096:
				player.getPA().closeInterfaces(true);
				player.itemDestroyedId = -1;
				return true;
			case 55095:
				destroyItemAction(player, player.itemDestroyedId);
				return true;
		}
		return false;
	}

	/**
	 * Displaying destroy item
	 * 
	 * @param player the player
	 * @param itemId the item id
	 * @param slot the slot
	 */
	public static void displayDestroyItemInterface(Player player, int itemId, int slot) {
		/*
		 * The definition
		 */
		DegradingItem item = DegradingItemJSONLoader.getDegradables().get(itemId);
		/*
		 * The item
		 */
		if (item != null) {
			sendDestroyInterface(player, itemId, slot, BREAK_ITEM);
			return;
		}
		sendDestroyInterface(player, itemId, slot, DESTROY_ITEM);
	}

	/**
	 * Displaying uncharge item
	 * 
	 * @param player the player
	 * @param itemId the item id
	 * @param slot the slot
	 */
	public static void displayUnchargeInterface(Player player, int itemId, int slot) {
		if (GameType.isOsrsPvp()) {
			return;
		}
		sendDestroyInterface(player, itemId, slot, UNCHARGE_ITEM);
	}

	/**
	 * Sending destroy interface
	 * 
	 * @param player the player
	 * @param itemId the item id
	 * @param slot the slot
	 * @param type the type
	 */
	private static void sendDestroyInterface(Player player, int itemId, int slot, String[][] type) {
		String itemName = ItemAssistant.getItemName(itemId);
		player.getPA().sendFrame34(14171, itemId, 0,
				ItemAssistant.getItemAmount(player, itemId, slot));
		for (int i = 0; i < type.length; i++) {
			player.getPA().sendFrame126(type[i][0].replaceAll("itemName", itemName),
					Integer.parseInt(type[i][1]));
		}
		player.getPA().sendFrame164(14170);
	}

	/**
	 * Destroying item action
	 * 
	 * @param player the player
	 * @param itemId the item id
	 */
	public static void destroyItemAction(Player player, int itemId) {
		itemId = player.itemDestroyedId;
		if (itemId == -1 || player.itemDestroyedSlot == -1) {
			return;
		}
		String itemName = ItemAssistant.getItemName(itemId);
		int slot = ItemAssistant.getItemSlot(player, itemId);
		if (slot == -1) {
			return;
		}
		/*
		 * The definition
		 */
		DegradingItem item = DegradingItemJSONLoader.getDegradables().get(itemId);
		/*
		 * The item
		 */
		if (item != null) {
			ItemAssistant.deleteItemFromInventory(player, itemId, player.itemDestroyedSlot,
					player.playerItemsN[slot]);
			Server.itemHandler.createGroundItem(player, item.getDropItem(), player.getX(),
					player.getY(), player.getHeight(), player.playerItemsN[slot], true, 50, true,
					player.getPlayerName(), player.getPlayerName(), player.addressIp, player.addressUid,
					"DropItemPacket else");
			String type = ItemDefinition.DEFINITIONS[item.getDropItem()].name.contains("(deg)")
					? "degrades" : "breaks";
			player.playerAssistant
					.sendMessage("Your " + itemName + " " + type + " as you drop it on the ground.");
			player.getPA().closeInterfaces(true);
			player.itemDestroyedId = -1;
			SoundSystem.sendSound(player, 376, 0);
			return;
		}
		if (itemId == 12926 && GameType.isOsrs()) {
			Blowpipe.uncharge(player);
		} else {
			ItemAssistant.deleteItemFromInventory(player, itemId, player.itemDestroyedSlot,
					player.playerItemsN[slot]);
			player.playerAssistant
					.sendMessage("Your " + itemName + " vanishes as you drop it on the ground.");
			player.getPA().closeInterfaces(true);
			player.itemDestroyedId = -1;
			if (ItemAssistant.hasSingularUntradeableItem(player, itemId)) {
				player.singularUntradeableItemsOwned.remove(Integer.toString(itemId));
				if (itemId == 2677) {
					player.clueScrollType = -1;
				}
			}
		}
	}
}
