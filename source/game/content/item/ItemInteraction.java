package game.content.item;

import game.npc.Npc;
import game.player.Player;

/**
 * Handles everything to do with an item
 * 
 * @author 2012
 *
 */
public interface ItemInteraction {

	/**
	 * Whether item can be equipped
	 * 
	 * @param player the player
	 * @param id the id
	 * @param slot the slot
	 * @return equipping item
	 */
	default boolean canEquip(Player player, int id, int slot) {
		return false;
	}

	/**
	 * Operating item
	 * 
	 * @param player the player
	 * @param id the id
	 */
	default void operate(Player player, int id) {

	}

	/**
	 * Clicking item action
	 * 
	 * @param player the player
	 * @param id the id
	 * @param type the click type
	 * @return clicking action
	 */
	default boolean sendItemAction(Player player, int id, int type) {
		return false;
	}

	/**
	 * Using item on item
	 * 
	 * @param player the player
	 * @param id the id
	 * @param useWith the other item
	 * @return item on item
	 */
	default boolean useItem(Player player, int id, int useWith) {
		return false;
	}

	/**
	 * Using item on object
	 * 
	 * @param player the player
	 * @param id the id
	 * @param object the object
	 * @return item on object
	 */
	default boolean useItemOnObject(Player player, int id, int object) {
		return false;
	}

	/**
	 * Using item on npc
	 * 
	 * @param player the player
	 * @param id the id
	 * @param npc the npc
	 * @return item on npc
	 */
	default boolean useItemOnNpc(Player player, int id, Npc npc) {
		return false;
	}

	/**
	 * Dropping the item
	 * 
	 * @param player the player
	 * @param id the id
	 * @return dropping the item
	 */
	default boolean dropItem(Player player, int id) {
		return false;
	}
}
