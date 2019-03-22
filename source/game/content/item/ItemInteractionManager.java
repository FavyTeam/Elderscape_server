package game.content.item;

import game.npc.Npc;
import game.player.Player;

/**
 * Handles the item interaction
 * 
 * @author 2012
 *
 */
public class ItemInteractionManager {

	/**
	 * Handles dropping item
	 * 
	 * @param player the player
	 * @param id the id
	 * @return dropping item action
	 */
	public static boolean handleDropItem(Player player, int id) {
		/*
		 * The item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Dropping item
		 */
		return item.dropItem(player, id);
	}
	/**
	 * Handles item on npc action
	 * 
	 * @param player the player
	 * @param id the id
	 * @param npc the npc
	 * @return the item on npc action
	 */
	public static boolean handleItemOnNpc(Player player, int id, Npc npc) {
		/*
		 * The item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Item on npc action
		 */
		return item.useItemOnNpc(player, id, npc);
	}

	/**
	 * Handles item on object action
	 * 
	 * @param player the player
	 * @param id the id
	 * @param object the object
	 * @return the item on object action
	 */
	public static boolean handleItemOnObject(Player player, int id, int object) {
		/*
		 * The item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Item on object action
		 */
		return item.useItemOnObject(player, id, object);
	}

	/**
	 * Handles item on item use
	 * 
	 * @param player the player
	 * @param id the id
	 * @param useWith using with
	 * @param using item
	 * @return the item on item action
	 */
	public static boolean handleItemOnItem(Player player, int id, int useWith) {
		/*
		 * The first item
		 */
		ItemInteraction first = ItemInteractionMap.getSingleton().get(id);

		/*
		 * Valid item
		 */
		if (first != null) {
			return first.useItem(player, id, useWith);
		}
		/*
		 * The second item
		 */
		ItemInteraction second = ItemInteractionMap.getSingleton().get(useWith);

		/*
		 * Valid item
		 */
		if (second != null) {
			return second.useItem(player, useWith, id);
		}
		return false;
	}

	/**
	 * Handles item action
	 * 
	 * @param player the player
	 * @param id the id
	 * @param type the type
	 * @return handles item action
	 */
	public static boolean handleItemAction(Player player, int id, int type) {
		/*
		 * The first item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Click item
		 */
		return item.sendItemAction(player, id, type);
	}

	/**
	 * Handles equipping item
	 * 
	 * @param player the player
	 * @param id the id
	 * @param slot the slot
	 * @return equipping item
	 */
	public static boolean handleEquipItem(Player player, int id, int slot) {
		/*
		 * The first item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return true;
		}
		/*
		 * Click item
		 */
		return item.canEquip(player, id, slot);
	}

	/**
	 * Handles operating item
	 * 
	 * @param player the player
	 * @param id the id
	 * @return operating item
	 */
	public static boolean handleOperateItem(Player player, int id) {
		/*
		 * The first item
		 */
		ItemInteraction item = ItemInteractionMap.getSingleton().get(id);
		/*
		 * Invalid item
		 */
		if (item == null) {
			return false;
		}
		/*
		 * Click item
		 */
		item.operate(player, id);
		return true;
	}
}
